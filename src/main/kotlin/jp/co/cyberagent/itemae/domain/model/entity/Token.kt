package jp.co.cyberagent.itemae.domain.model.entity

import java.sql.Timestamp
import javax.persistence.*

@Entity
@Table(name = "user_notification_tokens")
data class Token (
    @Id
    val userId: Long = 0,
    val token: String = "",
    val createdAt: Timestamp = Timestamp(0),
    val updatedAt: Timestamp = Timestamp(0)
)