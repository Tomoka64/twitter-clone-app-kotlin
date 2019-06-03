package jp.co.cyberagent.itemae.domain.repository

import jp.co.cyberagent.itemae.domain.model.entity.UserAuthority
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserAuthorityRepository : JpaRepository<UserAuthority, Long> {
    fun findByFirebaseUid(firebaseUid: String): UserAuthority
}