package jp.co.cyberagent.itemae.domain.repository

import jp.co.cyberagent.itemae.domain.model.entity.Notification
import jp.co.cyberagent.itemae.domain.model.entity.Tweet
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface NotificationRepository : JpaRepository<Notification, Long> {
    @Query(value="SELECT * FROM notifications t WHERE t.receiver_user_id = ?1 AND TIMESTAMPDIFF(HOUR,t.created_at,CURRENT_TIMESTAMP) < 72 ORDER BY t.created_at DESC LIMIT 50", nativeQuery = true)
    fun findByReceiverUserId(receiverUserId: Long): List<Notification>
    fun findBySenderUserId(senderUserId: Long): List<Notification>
}