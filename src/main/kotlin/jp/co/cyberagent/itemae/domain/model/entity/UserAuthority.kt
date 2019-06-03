package jp.co.cyberagent.itemae.domain.model.entity

import java.sql.Timestamp
import javax.persistence.*

@Entity
@Table(name = "user_authorities")
data class UserAuthority (
        @Id
        val userId: Long = 0,
        val firebaseUid: String = "",
        val createdAt: Timestamp = Timestamp(0)
)