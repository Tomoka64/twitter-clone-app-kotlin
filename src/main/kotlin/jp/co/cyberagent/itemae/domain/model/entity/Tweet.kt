package jp.co.cyberagent.itemae.domain.model.entity

import java.sql.Timestamp
import javax.persistence.*

@Entity
@Table(name = "tweets")
data class Tweet(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = 0,
        val userId: Long = 0,
        val text: String = "",
        val createdAt: Timestamp = Timestamp(0)
)

