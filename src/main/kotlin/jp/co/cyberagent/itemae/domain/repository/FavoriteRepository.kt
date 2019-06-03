package jp.co.cyberagent.itemae.domain.repository

import jp.co.cyberagent.itemae.domain.model.entity.Favorite
import jp.co.cyberagent.itemae.domain.model.entity.FavoritePrimaryKey
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface FavoriteRepository : JpaRepository<Favorite, Long> {
    fun findByFavoritePrimaryKey(favoritePrimaryKey: FavoritePrimaryKey): Favorite

    @Query("SELECT COUNT(*) FROM Favorite f WHERE f.favoritePrimaryKey.tweetId = ?1")
    fun count(tweetId: Long): Long

    @Query("SELECT f.favoritePrimaryKey.tweetId FROM Favorite f WHERE f.favoritePrimaryKey.userId = ?1")
    fun findByUserId(userId: Long): List<Long>

    @Query(value = "SELECT f.tweet_id FROM favorites f WHERE DATEDIFF(CURRENT_TIMESTAMP,f.created_at) = 0 GROUP BY f.tweet_id ORDER BY COUNT(*) DESC LIMIT 20",
            nativeQuery = true)
    fun findFavoriteTweetId(): List<Long>
}