package jp.co.cyberagent.itemae.domain.model.entity

import java.sql.Date
import java.sql.Timestamp
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class TweetProto(
        @Id
        var tweetId: Long = 0,
        var userId: Long = 0,
        var userScreenName: String = "",
        var userName: String = "",
        var userDescription: String = "",
        var userLocation: String = "",
        var userUrl: String = "",
        var userBornOn: Date = Date(0),
        var userCreatedAt: Timestamp = Timestamp(0),
        var userUpdatedAt: Timestamp = Timestamp(0),
        var text: String = "",
        var retweetCount: Long = 0,
        var favoriteCount: Long = 0,
        var createdAt: Timestamp = Timestamp(0),
        var retweetedUserId: Long = 0,
        var retweetedUserScreenName: String = "",
        var retweetedUserName: String = "",
        var retweetedUserDescription: String = "",
        var retweetedUserLocation: String = "",
        var retweetedUserUrl: String = "",
        var retweetedUserBornOn: Date = Date(0),
        var retweetedUserCreatedAt: Timestamp = Timestamp(0),
        var retweetedUserUpdatedAt: Timestamp = Timestamp(0),
        var createdAtForSort: Long = 0
)