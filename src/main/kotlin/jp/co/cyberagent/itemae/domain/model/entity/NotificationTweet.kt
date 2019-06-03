package jp.co.cyberagent.itemae.domain.model.entity

import java.sql.Timestamp
import javax.persistence.*

@Entity
@Table(name = "notification_tweets")
data class NotificationTweet (
        @Id
        val notificationId: Long = 0,
        val tweetId: Long = 0,
        val createdAt: Timestamp = Timestamp(0)
)