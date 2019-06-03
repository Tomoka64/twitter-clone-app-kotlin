package jp.co.cyberagent.itemae.domain.model.entity

import java.io.Serializable
import java.sql.Timestamp
import javax.persistence.Embeddable
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.Table

@Embeddable
data class FavoritePrimaryKey(
        val userId: Long = 0,
        val tweetId: Long = 0
): Serializable

@Entity
@Table(name = "favorites")
data class Favorite(
        @EmbeddedId
        val favoritePrimaryKey: FavoritePrimaryKey = FavoritePrimaryKey(),

        val createdAt: Timestamp = Timestamp(0)
)
