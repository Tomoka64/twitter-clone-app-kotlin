package jp.co.cyberagent.itemae.domain.model.entity

import java.sql.Timestamp
import java.sql.Date
import javax.persistence.*


@Entity
@Table(name = "users")
data class User(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = 0,

        val screenName: String = "",
        val name: String = "",
        val description: String = "",
        val location: String = "",
        val url: String = "",
        val bornOn: Date = Date(0),
        var createdAt: Timestamp = Timestamp(0),
        var updatedAt: Timestamp = Timestamp(0)
)
