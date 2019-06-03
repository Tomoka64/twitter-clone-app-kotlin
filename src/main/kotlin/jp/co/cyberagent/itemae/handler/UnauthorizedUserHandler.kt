package jp.co.cyberagent.itemae.handler

import com.google.firebase.auth.FirebaseAuth
import io.grpc.Status
import io.grpc.stub.StreamObserver
import jp.co.cyberagent.itemae.domain.model.entity.TweetProto
import jp.co.cyberagent.itemae.domain.service.*
import jp.co.cyberagent.itemae.proto.*
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.lognet.springboot.grpc.GRpcService
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.dao.EmptyResultDataAccessException

@GRpcService(interceptors = [] )
class UnauthorizedUserHandler(
        private val userService: UserService,
        private val tweetService: TweetService,
        private val friendService: FriendService,
        private val reTweetService: ReTweetService,
        private val favoriteService: FavoriteService,
        private val userAuthorityService: UserAuthorityService,
        private val convertService: ConvertService
):UnauthorizedItemaeServiceGrpc.UnauthorizedItemaeServiceImplBase() {

    override fun createUser(request: CreateUserRequest?, responseObserver: StreamObserver<EmptyResponse>?) {
        try {
            val user = convertService.protoUserToEntityUser(request!!.user)
            val savedUser = userService.createUser(user)
            val decodedToken = FirebaseAuth.getInstance().verifyIdTokenAsync(request.token)
            userAuthorityService.saveAuthorityUser(savedUser.id, decodedToken.get().uid)
            responseObserver?.onNext(EmptyResponse.getDefaultInstance())
        } catch (e: DataIntegrityViolationException) {
            responseObserver?.onError(Status.INVALID_ARGUMENT.asRuntimeException())
        } catch (e: Exception) {
            responseObserver?.onError(Status.INTERNAL.asRuntimeException())
        } finally {
            responseObserver?.onCompleted()
        }
    }

    override fun getUser(request: GetUserInfoRequest?, responseObserver: StreamObserver<User>?) {
        try {
            val user = userService.findUserById(request!!.userId)
            val result = convertService.entityUserToProtoUser(user)
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

    override fun getTweetList(request: GetUserInfoRequest?, responseObserver: StreamObserver<TweetList>?) {
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
            tweetList.sortWith(object: Comparator<TweetProto>{
                override fun compare(p1: TweetProto, p2: TweetProto): Int = when {
                    p1.createdAtForSort < p2.createdAtForSort -> 1
                    p1.createdAtForSort == p2.createdAtForSort -> 0
                    else -> -1
                }
            })

            val timeLineBuilder = TweetList.newBuilder()

            val listSize = tweetList.size
            val resultSize = if (listSize < 50) listSize else 50
            for (i in 0..(resultSize-1)) {
                val tweetId = tweetList[i].tweetId
                val reTweetCount = reTweetService.countReTweet(tweetId)
                val favoriteCount = favoriteService.countFavorite(tweetId)
                tweetList[i].retweetCount = reTweetCount
                tweetList[i].favoriteCount = favoriteCount

                timeLineBuilder.addTweets(convertService.entityTweetToProtoTweet(tweetList[i]))
            }

            val result = timeLineBuilder.build()

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

    override fun getFollowingUserList(request: GetUserInfoRequest?, responseObserver: StreamObserver<UserList>?) {
        try {
            val users = friendService.findFollowingOrFollowedUserList(request!!.userId, false)
            val userListBuilder = UserList.newBuilder()
            for (i in users) {
                userListBuilder.addUsers(convertService.entityUserToProtoUser(i))
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
        try {
            val users = friendService.findFollowingOrFollowedUserList(request!!.userId, false)
            val userListBuilder = UserList.newBuilder()
            for (i in users) {
                userListBuilder.addUsers(convertService.entityUserToProtoUser(i))
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
}
