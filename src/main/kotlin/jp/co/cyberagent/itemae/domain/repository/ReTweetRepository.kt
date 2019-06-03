package jp.co.cyberagent.itemae.domain.repository

import jp.co.cyberagent.itemae.domain.model.entity.ReTweet
import jp.co.cyberagent.itemae.domain.model.entity.ReTweetPrimaryKey
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
    interface ReTweetRepository : JpaRepository<ReTweet, Long> {
    fun findByReTweetPrimaryKey(reTweetPrimaryKey: ReTweetPrimaryKey): ReTweet

    @Query("SELECT COUNT(*) FROM ReTweet r WHERE r.reTweetPrimaryKey.tweetId = ?1")
    fun count(tweetId: Long): Long

    @Query("SELECT r.reTweetPrimaryKey.tweetId FROM ReTweet r WHERE r.reTweetPrimaryKey.userId = ?1")
    fun findByUserId(userId: Long): List<Long>
}