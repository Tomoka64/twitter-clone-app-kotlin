package jp.co.cyberagent.itemae.domain.model.entity

import java.sql.Timestamp
import javax.persistence.*
import java.io.Serializable


@Embeddable
data class FriendPrimaryKey(
        val followingUserId: Long = 0,
        val followedUserId: Long = 0
): Serializable

@Entity
@Table(name = "friends")
data class Friend(
        @EmbeddedId
        val friendPrimaryKey: FriendPrimaryKey = FriendPrimaryKey(),

        val createdAt: Timestamp = Timestamp(0)
)
