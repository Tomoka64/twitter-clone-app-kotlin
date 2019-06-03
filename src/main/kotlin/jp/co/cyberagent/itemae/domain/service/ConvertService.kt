package jp.co.cyberagent.itemae.domain.service

import jp.co.cyberagent.itemae.domain.model.entity.TweetProto
import jp.co.cyberagent.itemae.domain.util.TimeConverter
import jp.co.cyberagent.itemae.proto.EmptyResponse
import jp.co.cyberagent.itemae.proto.Notification
import org.springframework.stereotype.Service
import java.math.BigInteger
import java.sql.Timestamp
import jp.co.cyberagent.itemae.domain.model.entity.User as entityUser
import jp.co.cyberagent.itemae.proto.User as protoUser
import jp.co.cyberagent.itemae.domain.model.entity.Tweet as entityTweet
import jp.co.cyberagent.itemae.proto.Tweet as protoTweet
import com.google.protobuf.Timestamp as protoTimeStamp
import java.sql.Date as sqlDate

@Service
class ConvertService() {
    val timeConverter = TimeConverter()
    fun entityUserToProtoUser(user: entityUser):protoUser.Builder {
        val pUser = protoUser.newBuilder()
                .setUserId(user.id)
                .setScreenName(user.screenName)
                .setDisplayName(user.name)
                .setDescription(user.description)
                .setLocation(user.location)
                .setUrl(user.url)
                .setCreatedAt(timeConverter.splTimeStampToProtoTimestamp(user.createdAt))
                .setUpdatedAt(timeConverter.splTimeStampToProtoTimestamp(user.updatedAt))
                .setBornOn(timeConverter.splTimeStampToProtoTimestamp(Timestamp(user.bornOn.time)))
        return pUser
    }

    fun protoUserToEntityUser(user: protoUser): entityUser {
        return entityUser(
                id = user.userId,
                screenName = user.screenName,
                name = user.displayName,
                description = user.description,
                location = user.location,
                url = user.url,
                createdAt = timeConverter.protoTimeStampToSqlTimeStamp(user.createdAt),
                updatedAt = timeConverter.protoTimeStampToSqlTimeStamp(user.updatedAt),
                bornOn = java.sql.Date(timeConverter.protoTimeStampToSqlTimeStamp(user.bornOn).time)
        )
    }

    fun entityTweetToProtoTweet(tweet: TweetProto):protoTweet.Builder{
        val protoTweet = protoTweet.newBuilder()
                .setTweetId(tweet.tweetId)
                .setUser(entityUserToProtoUser(entityUser(
                        id = tweet.userId,
                        screenName = tweet.userScreenName,
                        name = tweet.userName,
                        location = tweet.userLocation,
                        url = tweet.userUrl,
                        createdAt = tweet.createdAt,
                        updatedAt = tweet.userUpdatedAt,
                        bornOn = tweet.userBornOn
                )))
                .setText(tweet.text)
                .setRetweetCount(tweet.retweetCount)
                .setFavoriteCount(tweet.favoriteCount)
                .setCreatedAt(timeConverter.splTimeStampToProtoTimestamp(tweet.createdAt))
                .setRetweetedUser(entityUserToProtoUser(entityUser(
                        id = tweet.retweetedUserId,
                        screenName = tweet.retweetedUserScreenName,
                        name = tweet.retweetedUserName,
                        location = tweet.retweetedUserLocation,
                        url = tweet.retweetedUserUrl,
                        createdAt = tweet.retweetedUserCreatedAt,
                        updatedAt = tweet.retweetedUserUpdatedAt,
                        bornOn = tweet.retweetedUserBornOn
                )))
        return protoTweet
    }

    fun toNotificationTweet(tweet: entityTweet): Notification.Tweet.Builder {
        return Notification.Tweet.newBuilder()
                .setTweetId(tweet.id)
                .setUserId(tweet.userId)
                .setText(tweet.text)
                .setCreatedAt(timeConverter.splTimeStampToProtoTimestamp((tweet.createdAt)))
    }

    fun toTweetEntity(array: Array<Any>,isRetweet: Boolean) : TweetProto {
        val userId = array[0] as BigInteger
        val userLong = userId.toLong()
        val twitterId = array[9] as BigInteger
        val twitterLong = twitterId.toLong()

        val userScreenName = array[1] as String
        val name = array[2] as String
        val description = array[3] as String
        val location = array[4] as String
        val url = array[5] as String
        val userBorn = array[6] as java.sql.Date
        val userCreatedAt= array[7] as Timestamp
        val userUpdatedAt= array[8] as Timestamp
        val text = array[10] as String
        val createdAt = array[11] as Timestamp

        val proto = TweetProto()
        proto.tweetId = twitterLong
        proto.userId= userLong
        proto.userScreenName = userScreenName
        proto.userName = name
        proto.userDescription = description
        proto.userLocation = location
        proto.userUrl = url
        proto.userBornOn = userBorn
        proto.userCreatedAt = userCreatedAt
        proto.userUpdatedAt = userUpdatedAt
        proto.text = text
        proto.createdAt = createdAt

        if (isRetweet) {
            val rUserId = array[12] as BigInteger
            val rUserLong = rUserId.toLong()
            val createdAtForSort = array[21] as Timestamp
           val sortLong = createdAtForSort.time

            proto.retweetedUserId = rUserLong
            proto.retweetedUserScreenName = array[13] as String
            proto.retweetedUserName = array[14] as String
            proto.retweetedUserDescription = array[15] as String
            proto.retweetedUserLocation = array[16] as String
            proto.retweetedUserUrl = array[17] as String
            proto.retweetedUserBornOn = array[18] as java.sql.Date
            proto.retweetedUserCreatedAt = array[19] as Timestamp
            proto.retweetedUserUpdatedAt = array[20] as Timestamp
            proto.createdAtForSort = sortLong
        } else {
            val sortLong = createdAt.time
            proto.createdAtForSort = sortLong
        }

        return proto
    }

    fun toProtoNotification(type: String, user: entityUser, tweet: Notification.Tweet?, createdAt: Timestamp): Notification {
        val notification = Notification.newBuilder()
        notification
                .setNotificationType(type)
                .setUser(entityUserToProtoUser(user))
                .setCreatedAt(timeConverter.splTimeStampToProtoTimestamp(createdAt))
        if (tweet != null) {
            notification.setTweet(tweet)
        } else {
            notification.setEmpty(EmptyResponse.newBuilder())
        }
        return notification.build()
    }
}
