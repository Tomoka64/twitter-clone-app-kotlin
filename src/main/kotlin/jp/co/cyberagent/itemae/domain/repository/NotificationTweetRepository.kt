package jp.co.cyberagent.itemae.domain.repository

import jp.co.cyberagent.itemae.domain.model.entity.NotificationTweet
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface NotificationTweetRepository : JpaRepository<NotificationTweet, Long> {
    @Query("SELECT t.tweetId FROM NotificationTweet t WHERE t.notificationId in ?1")
    fun findByNotificationId(notificationId: List<Long>): List<Long>
}