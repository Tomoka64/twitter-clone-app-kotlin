package jp.co.cyberagent.itemae.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import jp.co.cyberagent.itemae.domain.model.entity.User
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional
import java.sql.Date
import java.sql.Timestamp

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findByScreenName(screenName: String): User

    @Query("SELECT u FROM User u WHERE UPPER(u.screenName) LIKE UPPER(CONCAT('%',?1,'%')) OR UPPER(u.name) LIKE UPPER(CONCAT('%',?1,'%')) OR UPPER(u.description) LIKE UPPER(CONCAT('%',?1,'%'))")
    fun findByKeyword(keyword: String): List<User>

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.screenName = ?1, u.name = ?2, u.description = ?3, u.location = ?4, u.url = ?5, u.bornOn = ?6, u.updatedAt = ?7 WHERE u.id = ?8")
    fun editById(screenName: String, name: String, description: String, location: String, url: String, bornOn: Date, updatedAt: Timestamp, id: Long)
}