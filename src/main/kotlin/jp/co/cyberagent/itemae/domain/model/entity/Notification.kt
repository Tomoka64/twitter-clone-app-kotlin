package jp.co.cyberagent.itemae.domain.model.entity

import java.sql.Timestamp
import javax.persistence.*

@Entity
@Table(name = "notifications")
data class Notification (
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = 0,
        val senderUserId: Long = 0,
        val receiverUserId: Long = 0,
        val notificationTypeName: String = "",
        val createdAt: Timestamp = Timestamp(0)
)