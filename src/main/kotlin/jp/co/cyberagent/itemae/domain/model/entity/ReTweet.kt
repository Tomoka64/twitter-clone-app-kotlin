package jp.co.cyberagent.itemae.domain.model.entity

import java.io.Serializable
import java.sql.Timestamp
import javax.persistence.*

@Embeddable
data class ReTweetPrimaryKey(
        val userId: Long = 0,
        val tweetId: Long = 0
): Serializable

@Entity
@Table(name = "retweets")
data class ReTweet(
        @EmbeddedId
        val reTweetPrimaryKey: ReTweetPrimaryKey = ReTweetPrimaryKey(),

        val createdAt: Timestamp = Timestamp(0)
)
