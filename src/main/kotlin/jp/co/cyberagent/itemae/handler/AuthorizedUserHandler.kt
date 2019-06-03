package jp.co.cyberagent.itemae.handler

import io.grpc.Status
import io.grpc.stub.StreamObserver
import jp.co.cyberagent.itemae.domain.model.entity.TweetProto
import jp.co.cyberagent.itemae.domain.service.*
import jp.co.cyberagent.itemae.domain.model.entity.Tweet as entityTweet
import jp.co.cyberagent.itemae.proto.*
import jp.co.cyberagent.itemae.domain.model.entity.User as entityUser
import jp.co.cyberagent.itemae.domain.util.*
import jp.co.cyberagent.itemae.handler.Interceptor.AuthorizationInterceptor
import org.lognet.springboot.grpc.GRpcService
import jp.co.cyberagent.itemae.handler.context.ContextHandler
import kotlinx.coroutines.*
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.dao.EmptyResultDataAccessException

@GRpcService(interceptors = [AuthorizationInterceptor::class] )
class AuthorizedUserHandler(
        private val userService: UserService,
        private val tweetService: TweetService,
        private val friendService: FriendService,
        private val convertService: ConvertService,
        private val reTweetService: ReTweetService,
        private val favoriteService: FavoriteService,
        private val userAuthorityService: UserAuthorityService,
        private val firebaseNotificationService: FirebaseNotificationService,
        private val notificationService: NotificationService,
        private val notificationTweetService: NotificationTweetService,
        private val tokenService: TokenService
) : AuthorizedItemaeServiceGrpc.AuthorizedItemaeServiceImplBase() {

    override fun getTimeline(request: GetAuthorizedUserRequest?, responseObserver: StreamObserver<TweetList>?) {
        val myId = ContextHandler.getUserId()
        try {
            val follower = friendService.findFollowerIds(myId)

            val tweetList: MutableList<TweetProto> = mutableListOf()
            if (follower.isEmpty()) {
                val resultTweetSQL = tweetService.findMyTweetList(myId)
                val resultReTweetSQL = tweetService.findMyReTweetList(myId)
                if (resultReTweetSQL.isEmpty() && resultTweetSQL.isEmpty()) {
                    responseObserver?.onNext(TweetList.getDefaultInstance())
                    return
                }
                    for (i in resultTweetSQL) {
                        tweetList.add(convertService.toTweetEntity(i, false))
                    }
                    for (i in resultReTweetSQL) {
                        tweetList.add(convertService.toTweetEntity(i, true))
                    }
            } else {
                    runBlocking {
                        val asyncTweetSQL = async { tweetService.findTweetList(follower, myId) }
                        val asyncReTweetSQL = async { tweetService.findReTweetList(follower, myId) }
                        val resultTweetSQL = asyncTweetSQL.await()
                        val resultReTweetSQL = asyncReTweetSQL.await()
                        for (i in resultTweetSQL) {
                            tweetList.add(convertService.toTweetEntity(i, false))
                        }
                        for (i in resultReTweetSQL) {
                            tweetList.add(convertService.toTweetEntity(i, true))
                        }
                    }
                }

            if (tweetList.isEmpty()) {
                responseObserver?.onNext(TweetList.getDefaultInstance())
                return
            }
            sortTweetProtoList(tweetList)
            val result = createTweetList(myId,tweetList,50)

            responseObserver?.onNext(result)
        } catch (e: DataIntegrityViolationException) {
            responseObserver?.onError(Status.INVALID_ARGUMENT.asRuntimeException())
        } catch (e: Exception) {
            responseObserver?.onError(Status.INTERNAL.asRuntimeException())
        } finally {
            responseObserver?.onCompleted()
        }
    }

    override fun createTweet(request: CreateTweetRequest?, responseObserver: StreamObserver<TweetId>?) {
        val userId = ContextHandler.getUserId()
        val tweet = entityTweet(userId = userId, text = request!!.text, createdAt = getNow())
        try {
            val newTweet = tweetService.createTweet(tweet)
            val returnId = TweetId.newBuilder().setTweetId(newTweet.id).build()
            responseObserver?.onNext(returnId)
        } catch (e: DataIntegrityViolationException) {
            responseObserver?.onError(Status.INVALID_ARGUMENT.asRuntimeException())
        } catch (e: Exception) {
            responseObserver?.onError(Status.INTERNAL.asRuntimeException())
        } finally {
            responseObserver?.onCompleted()
        }
    }

    override fun deleteTweet(request: ReflectTweetRequest?, responseObserver: StreamObserver<EmptyResponse>?) {
        val userId = ContextHandler.getUserId()
        try {
            val tweet = tweetService.findById(request!!.tweetId)
            if (tweet.userId != userId) {
                responseObserver?.onError(Status.INVALID_ARGUMENT.asRuntimeException())
            }
            tweetService.deleteTweet(tweet)
            responseObserver?.onNext(EmptyResponse.getDefaultInstance())
        } catch (e: EmptyResultDataAccessException) {
            responseObserver?.onError(Status.INVALID_ARGUMENT.asRuntimeException())
        } catch (e: DataIntegrityViolationException) {
            responseObserver?.onError(Status.INVALID_ARGUMENT.asRuntimeException())
        } catch (e: Exception) {
            responseObserver?.onError(Status.INTERNAL.asRuntimeException())
        } finally {
            responseObserver?.onCompleted()
        }
    }

    override fun favorite(request: ReflectTweetRequest?, responseObserver: StreamObserver<EmptyResponse>?) {
        val userId = ContextHandler.getUserId()
        try {
            favoriteService.createFavorite(userId, request!!.tweetId)
            var user = userService.findUserById(userId)
            try {
                firebaseNotificationService.send("favorite", user, request.tweetId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            responseObserver?.onNext(EmptyResponse.getDefaultInstance())
        } catch (e: DataIntegrityViolationException) {
            responseObserver?.onError(Status.INVALID_ARGUMENT.asRuntimeException())
        } catch (e: Exception) {
            responseObserver?.onError(Status.INTERNAL.asRuntimeException())
        } finally {
            responseObserver?.onCompleted()
        }
    }

    override fun reTweet(request: ReflectTweetRequest?, responseObserver: StreamObserver<EmptyResponse>?) {
        val userId = ContextHandler.getUserId()
        try {
            reTweetService.createReTweet(userId, request!!.tweetId)
            var user = userService.findUserById(userId)
            try {
                firebaseNotificationService.send("retweet", user, request.tweetId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            responseObserver?.onNext(EmptyResponse.getDefaultInstance())
        } catch (e: DataIntegrityViolationException) {
            responseObserver?.onError(Status.INVALID_ARGUMENT.asRuntimeException())
        } catch (e: Exception) {
            responseObserver?.onError(Status.INTERNAL.asRuntimeException())
        } finally {
            responseObserver?.onCompleted()
        }
    }

    override fun dismissFavorite(request: ReflectTweetRequest?, responseObserver: StreamObserver<EmptyResponse>?) {
        val userId = ContextHandler.getUserId()
        try {
            favoriteService.deleteFavorite(userId, request!!.tweetId)
            responseObserver?.onNext(EmptyResponse.getDefaultInstance())
        } catch (e: DataIntegrityViolationException) {
            responseObserver?.onError(Status.INVALID_ARGUMENT.asRuntimeException())
        } catch (e: Exception) {
            responseObserver?.onError(Status.INTERNAL.asRuntimeException())
        } finally {
            responseObserver?.onCompleted()
        }
    }

    override fun dismissReTweet(request: ReflectTweetRequest?, responseObserver: StreamObserver<EmptyResponse>?) {
        val userId = ContextHandler.getUserId()
        try {
            reTweetService.deleteReTweet(userId, request!!.tweetId)
            responseObserver?.onNext(EmptyResponse.getDefaultInstance())
        } catch (e: DataIntegrityViolationException) {
            responseObserver?.onError(Status.INVALID_ARGUMENT.asRuntimeException())
        } catch (e: Exception) {
            responseObserver?.onError(Status.INTERNAL.asRuntimeException())
        } finally {
            responseObserver?.onCompleted()
        }
    }

    override fun follow(request: FollowUnFollowRequest?, responseObserver: StreamObserver<EmptyResponse>?) {
        val userId = ContextHandler.getUserId()
        try {
            val targetUser = userAuthorityService.findByUserId(request!!.targetUserId)
            friendService.follow(userId, targetUser.userId)
            var user = userService.findUserById(userId)
            try {
                firebaseNotificationService.send("follow", user, targetUser.userId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            responseObserver?.onNext(EmptyResponse.getDefaultInstance())
        } catch (e: DataIntegrityViolationException) {
            responseObserver?.onError(Status.INVALID_ARGUMENT.asRuntimeException())
        } catch (e: Exception) {
            responseObserver?.onError(Status.INTERNAL.asRuntimeException())
        } finally {
            responseObserver?.onCompleted()
        }
    }

    override fun unFollow(request: FollowUnFollowRequest?, responseObserver: StreamObserver<EmptyResponse>?) {
        val userId = ContextHandler.getUserId()
        try {
            friendService.unfollow(userId, request!!.targetUserId)
            responseObserver?.onNext(EmptyResponse.getDefaultInstance())
        } catch (e: DataIntegrityViolationException) {
            responseObserver?.onError(Status.INVALID_ARGUMENT.asRuntimeException())
        } catch (e: Exception) {
            responseObserver?.onError(Status.INTERNAL.asRuntimeException())
        } finally {
            responseObserver?.onCompleted()
        }
    }

    override fun getSelfUser(request: GetAuthorizedUserRequest?, responseObserver: StreamObserver<User>?) {
        val userId = ContextHandler.getUserId()
        try {
            val user = userService.findUserById(userId)
            val protoUser = convertService.entityUserToProtoUser(user)
            responseObserver?.onNext(protoUser.build())
        } catch (e: EmptyResultDataAccessException) {
            responseObserver?.onError(Status.INVALID_ARGUMENT.asRuntimeException())
        } catch (e: DataIntegrityViolationException) {
            responseObserver?.onError(Status.INVALID_ARGUMENT.asRuntimeException())
        } catch (e: Exception) {
            responseObserver?.onError(Status.INTERNAL.asRuntimeException())
        } finally {
            responseObserver?.onCompleted()
        }
    }

    override fun getFollowingUserList(request: GetUserInfoRequest?, responseObserver: StreamObserver<UserList>?) {
        val userId = ContextHandler.getUserId()
        try {
            val users = friendService.findFollowingOrFollowedUserList(request!!.userId, true)
            val userListBuilder = UserList.newBuilder()

            val followerIds = friendService.findFollowerIds(userId)
            val followedIds = friendService.findFollowedIds(userId)

            for (u in users) {
                val userEntity = convertService.entityUserToProtoUser(u)
                if (followerIds.contains(u.id)) {
                    userEntity.setIsFollowing(true)
                }
                if (followedIds.contains(u.id)) {
                    userEntity.setIsFollowed(true)
                }
                userListBuilder.addUsers(userEntity.build())
            }
            val result = userListBuilder.build()

            responseObserver?.onNext(result)
        } catch (e: EmptyResultDataAccessException) {
            responseObserver?.onError(Status.INVALID_ARGUMENT.asRuntimeException())
        } catch (e: DataIntegrityViolationException) {
            responseObserver?.onError(Status.INVALID_ARGUMENT.asRuntimeException())
        } catch (e: Exception) {
            responseObserver?.onError(Status.INTERNAL.asRuntimeException())
        } finally {
            responseObserver?.onCompleted()
        }
    }

    override fun getFollowerUserList(request: GetUserInfoRequest?, responseObserver: StreamObserver<UserList>?) {
        val userId = ContextHandler.getUserId()
        try {
            val users = friendService.findFollowingOrFollowedUserList(request!!.userId, false)
            val userListBuilder = UserList.newBuilder()

            val followerIds = friendService.findFollowerIds(userId)
            val followedIds = friendService.findFollowedIds(userId)

            for (u in users) {
                val userEntity = convertService.entityUserToProtoUser(u)
                if (followerIds.contains(u.id)) {
                    userEntity.setIsFollowing(true)
                }
                if (followedIds.contains(u.id)) {
                    userEntity.setIsFollowed(true)
                }
                userListBuilder.addUsers(userEntity.build())
            }
            val result = userListBuilder.build()

            responseObserver?.onNext(result)
        } catch (e: EmptyResultDataAccessException) {
            responseObserver?.onError(Status.INVALID_ARGUMENT.asRuntimeException())
        } catch (e: DataIntegrityViolationException) {
            responseObserver?.onError(Status.INVALID_ARGUMENT.asRuntimeException())
        } catch (e: Exception) {
            responseObserver?.onError(Status.INTERNAL.asRuntimeException())
        } finally {
            responseObserver?.onCompleted()
        }
    }

    override fun getAnotherUser(request: GetUserInfoRequest?, responseObserver: StreamObserver<User>?) {
        val userId = ContextHandler.getUserId()
        try {
            val user = userService.findUserById(request!!.userId)
            val result = convertService.entityUserToProtoUser(user)

            if (!friendService.containFollowerId(userId, user.id).isEmpty()) {
                result.setIsFollowing(true)
            }
            if (!friendService.containFollowedId(userId, user.id).isEmpty()) {
                result.setIsFollowed(true)
            }
            responseObserver?.onNext(result.build())
        } catch (e: EmptyResultDataAccessException) {
            responseObserver?.onError(Status.INVALID_ARGUMENT.asRuntimeException())
        } catch (e: DataIntegrityViolationException) {
            responseObserver?.onError(Status.INVALID_ARGUMENT.asRuntimeException())
        } catch (e: Exception) {
            responseObserver?.onError(Status.INTERNAL.asRuntimeException())
        } finally {
            responseObserver?.onCompleted()
        }
    }

    override fun searchUsers(request: SearchRequest?, responseObserver: StreamObserver<UserList>?) {
        val userId = ContextHandler.getUserId()
        try {
            val users = userService.searchUser(request!!.keyword)
            val userListBuilder = UserList.newBuilder()

            val followerIds = friendService.findFollowerIds(userId)
            val followedIds = friendService.findFollowedIds(userId)

            for (u in users) {
                val userEntity = convertService.entityUserToProtoUser(u)
                if (followerIds.contains(u.id)) {
                    userEntity.setIsFollowing(true)
                }
                if (followedIds.contains(u.id)) {
                    userEntity.setIsFollowed(true)
                }
                userListBuilder.addUsers(userEntity.build())
            }
            val result = userListBuilder.build()
            responseObserver?.onNext(result)
        } catch (e: EmptyResultDataAccessException) {
            responseObserver?.onError(Status.INVALID_ARGUMENT.asRuntimeException())
        } catch (e: DataIntegrityViolationException) {
            responseObserver?.onError(Status.INVALID_ARGUMENT.asRuntimeException())
        } catch (e: Exception) {
            responseObserver?.onError(Status.INTERNAL.asRuntimeException())
        } finally {
            responseObserver?.onCompleted()
        }
    }

    override fun searchTweets(request: SearchRequest?, responseObserver: StreamObserver<TweetList>?) {
        val myId = ContextHandler.getUserId()
        try {
            val tweetList: MutableList<TweetProto> = mutableListOf()
            val searchResult = tweetService.searchTweets(request!!.keyword)
            for (i in searchResult) {
                tweetList.add(convertService.toTweetEntity(i, false))
            }

            sortTweetProtoList(tweetList)
            val result = createTweetList(myId,tweetList,50)

            responseObserver?.onNext(result)
        } catch (e: DataIntegrityViolationException) {
            responseObserver?.onError(Status.INVALID_ARGUMENT.asRuntimeException())
        } catch (e: Exception) {
            responseObserver?.onError(Status.INTERNAL.asRuntimeException())
        } finally {
            responseObserver?.onCompleted()
        }
    }

    override fun upsertFCMToken(request: UpsertFCMTokenRequest?, responseObserver: StreamObserver<EmptyResponse>?) {
        val userId = ContextHandler.getUserId()
        try {
            tokenService.saveToken(userId, request!!.token)
            responseObserver?.onNext(EmptyResponse.getDefaultInstance())
        } catch (e: DataIntegrityViolationException) {
            responseObserver?.onError(Status.INVALID_ARGUMENT.asRuntimeException())
        } catch (e: Exception) {
            responseObserver?.onError(Status.INTERNAL.asRuntimeException())
        } finally {
            responseObserver?.onCompleted()
        }
    }

    override fun getNotifications(request: GetAuthorizedUserRequest?, responseObserver: StreamObserver<NotificationList>?) {
        val userId = ContextHandler.getUserId()
        try {
            val notifications = notificationService.findByReceiverId(userId)
            val builder = NotificationList.newBuilder()
            for (notification in notifications) {
                var tweet = Notification.Tweet.newBuilder()
                if (notification.notificationTypeName == "retweet" || notification.notificationTypeName == "favorite") {
                    val notificationTweet = notificationTweetService.findById(notification.id)
                    tweet = convertService.toNotificationTweet(tweetService.findById(notificationTweet.tweetId))
                }
                val user = userService.findUserById(notification.senderUserId)
                builder.addNotifications(convertService.toProtoNotification(notification.notificationTypeName, user, tweet.build(), notification.createdAt))
            }
            val result = builder.build()
            responseObserver?.onNext(result)
        } catch (e: DataIntegrityViolationException) {
            responseObserver?.onError(Status.INVALID_ARGUMENT.asRuntimeException())
        } catch (e: Exception) {
            responseObserver?.onError(Status.INTERNAL.asRuntimeException())
        } finally {
            responseObserver?.onCompleted()
        }
    }

    override fun getTweetList(request: GetUserInfoRequest?, responseObserver: StreamObserver<TweetList>?) {
        val myId = ContextHandler.getUserId()
        try {
            val userId = request!!.userId

            val tweetList: MutableList<TweetProto> = mutableListOf()
            runBlocking {
                val asyncTweetSQL = async { tweetService.findMyTweetList(userId) }
                val asyncReTweetSQL = async { tweetService.findMyReTweetList(userId) }
                val resultTweetSQL = asyncTweetSQL.await()
                val resultReTweetSQL = asyncReTweetSQL.await()

                for (i in resultTweetSQL) {
                    tweetList.add(convertService.toTweetEntity(i, false))
                }
                for (i in resultReTweetSQL) {
                    tweetList.add(convertService.toTweetEntity(i, true))
                }
            }
            if (tweetList.isEmpty()) {
                responseObserver?.onNext(TweetList.getDefaultInstance())
                return
            }
            sortTweetProtoList(tweetList)
            val result = createTweetList(myId,tweetList,50)

            responseObserver?.onNext(result)
        } catch (e: DataIntegrityViolationException) {
            responseObserver?.onError(Status.INVALID_ARGUMENT.asRuntimeException())
        } catch (e: Exception) {
            responseObserver?.onError(Status.INTERNAL.asRuntimeException())
        } finally {
            responseObserver?.onCompleted()
        }
    }

    override fun getReTweetList(request: GetUserInfoRequest?, responseObserver: StreamObserver<TweetList>?) {
        val myId = ContextHandler.getUserId()
        try {
            val userId = request!!.userId
            val tweetIds = reTweetService.findReTweetIds(userId)
            if (tweetIds.isEmpty()) {
                responseObserver?.onNext(TweetList.getDefaultInstance())
                return
            }
            val resultSQL = tweetService.findTweetsByTweetIds(tweetIds)
            val tweetList: MutableList<TweetProto> = mutableListOf()
            for (i in resultSQL) {
                tweetList.add(convertService.toTweetEntity(i, false))
            }

            sortTweetProtoList(tweetList)
            val result = createTweetList(myId,tweetList,50)

            responseObserver?.onNext(result)
        } catch (e: DataIntegrityViolationException) {
            responseObserver?.onError(Status.INVALID_ARGUMENT.asRuntimeException())
        } catch (e: Exception) {
            responseObserver?.onError(Status.INTERNAL.asRuntimeException())
        } finally {
            responseObserver?.onCompleted()
        }
    }

    override fun getFavoriteList(request: GetUserInfoRequest?, responseObserver: StreamObserver<TweetList>?) {
        val myId = ContextHandler.getUserId()
        try {
            val userId = request!!.userId
            val tweetIds = favoriteService.findFavoriteIds(userId)
            if (tweetIds.isEmpty()) {
                responseObserver?.onNext(TweetList.getDefaultInstance())
                return
            }
            val resultSQL = tweetService.findTweetsByTweetIds(tweetIds)
            val tweetList: MutableList<TweetProto> = mutableListOf()
            for (i in resultSQL) {
                tweetList.add(convertService.toTweetEntity(i, false))
            }

            sortTweetProtoList(tweetList)
            val result = createTweetList(myId,tweetList,50)

            responseObserver?.onNext(result)
        } catch (e: DataIntegrityViolationException) {
            responseObserver?.onError(Status.INVALID_ARGUMENT.asRuntimeException())
        } catch (e: Exception) {
            responseObserver?.onError(Status.INTERNAL.asRuntimeException())
        } finally {
            responseObserver?.onCompleted()
        }
    }

    override fun getTrendTweetList(request: GetAuthorizedUserRequest?, responseObserver: StreamObserver<TweetList>?) {
        val myId = ContextHandler.getUserId()
        try {
            val tweetIds = favoriteService.findTrendTweetIds()
            if (tweetIds.isEmpty()) {
                responseObserver?.onNext(TweetList.getDefaultInstance())
                return
            }
            val resultSQL = tweetService.findTweetsByTweetIds(tweetIds)
            val tweetList: MutableList<TweetProto> = mutableListOf()
            for (i in resultSQL) {
                tweetList.add(convertService.toTweetEntity(i, false))
            }

            val timeLineBuilder = TweetList.newBuilder()

            val retweetIds = reTweetService.findReTweetIds(myId)
            val favoriteIds = favoriteService.findFavoriteIds(myId)

            val resultSize = tweetList.size

            for (i in 0..(resultSize-1)) {
                for (j in 0..(resultSize-1)) {
                    if (tweetList[j].tweetId != tweetIds[i]) {
                        continue
                    }

                    val tweetId = tweetList[j].tweetId
                    val reTweetCount = reTweetService.countReTweet(tweetId)
                    val favoriteCount = favoriteService.countFavorite(tweetId)
                    tweetList[j].retweetCount = reTweetCount
                    tweetList[j].favoriteCount = favoriteCount

                    val protoTweet = convertService.entityTweetToProtoTweet(tweetList[j])

                    if (retweetIds.contains(tweetId)) {
                        protoTweet.setIsRetweet(true)
                    }
                    if (favoriteIds.contains(tweetId)) {
                        protoTweet.setIsFavorite(true)
                    }

                    timeLineBuilder.addTweets(protoTweet.build())
                }
            }

            val result = timeLineBuilder.build()

            responseObserver?.onNext(result)
        } catch (e: DataIntegrityViolationException) {
            responseObserver?.onError(Status.INVALID_ARGUMENT.asRuntimeException())
        } catch (e: Exception) {
            responseObserver?.onError(Status.INTERNAL.asRuntimeException())
        } finally {
            responseObserver?.onCompleted()
        }
    }

    override fun editUser(request: EditUserRequest?, responseObserver: StreamObserver<EmptyResponse>?) {
        val myId = ContextHandler.getUserId()
        try {
            val protoUser = request!!.user
            val entityUser = convertService.protoUserToEntityUser(protoUser)
            userService.editUser(entityUser.screenName,entityUser.name,entityUser.description,entityUser.location,entityUser.url,entityUser.bornOn,getNow(),myId)
            responseObserver?.onNext(EmptyResponse.getDefaultInstance())
        } catch (e: DataIntegrityViolationException) {
            responseObserver?.onError(Status.INVALID_ARGUMENT.asRuntimeException())
        } catch (e: Exception) {
            responseObserver?.onError(Status.INTERNAL.asRuntimeException())
        } finally {
            responseObserver?.onCompleted()
        }
    }

    fun sortTweetProtoList(tweetList: MutableList<TweetProto>): MutableList<TweetProto> {
        tweetList.sortWith(object: Comparator<TweetProto>{
            override fun compare(p1: TweetProto, p2: TweetProto): Int = when {
                p1.createdAtForSort < p2.createdAtForSort -> 1
                p1.createdAtForSort == p2.createdAtForSort -> 0
                else -> -1
            }
        })
        return tweetList
    }

    fun createTweetList(myId: Long, tweetList: MutableList<TweetProto>, limit: Int): TweetList {
        val timeLineBuilder = TweetList.newBuilder()
        val retweetIds = reTweetService.findReTweetIds(myId)
        val favoriteIds = favoriteService.findFavoriteIds(myId)

        val size = if (tweetList.size < limit) tweetList.size else limit

        for (i in 0 until (size-1)) {
            val tweetId = tweetList[i].tweetId
            val reTweetCount = reTweetService.countReTweet(tweetId)
            val favoriteCount = favoriteService.countFavorite(tweetId)
            tweetList[i].retweetCount = reTweetCount
            tweetList[i].favoriteCount = favoriteCount

            val protoTweet = convertService.entityTweetToProtoTweet(tweetList[i])

            if (retweetIds.contains(tweetId)) {
                protoTweet.setIsRetweet(true)
            }
            if (favoriteIds.contains(tweetId)) {
                protoTweet.setIsFavorite(true)
            }

            timeLineBuilder.addTweets(protoTweet.build())
        }
        return timeLineBuilder.build()
    }

}
