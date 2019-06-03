package jp.co.cyberagent.itemae.domain.service

import jp.co.cyberagent.itemae.domain.model.entity.UserAuthority
import jp.co.cyberagent.itemae.domain.repository.UserAuthorityRepository
import jp.co.cyberagent.itemae.domain.util.getNow
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import java.lang.Exception
import javax.transaction.Transactional

@Service
class UserAuthorityService(
        private val userAuthorityRepository: UserAuthorityRepository
){
    @Transactional
    fun saveAuthorityUser(userId: Long, firebaseUid: String) {
        val userAuthority = UserAuthority(
                userId = userId,
                firebaseUid = firebaseUid,
                createdAt = getNow()
        )
        try {
            userAuthorityRepository.save(userAuthority)
        } catch (e: DataIntegrityViolationException) {
            //TODO : log
            throw e
        } catch (e: Exception) {
            throw e
        }
    }
    fun findByUserId(userId: Long): UserAuthority {
        try {
            return userAuthorityRepository.findById(userId).get()
        } catch (e: DataIntegrityViolationException) {
            //TODO : log
            throw e
        } catch (e: Exception) {
            throw e
        }
    }
    fun findByFirebaseUid(firebaseUid: String): UserAuthority {
        try {
            return userAuthorityRepository.findByFirebaseUid(firebaseUid)
        } catch (e: DataIntegrityViolationException) {
            //TODO : log
            throw e
        } catch (e: Exception) {
            throw e
        }

    }
}
