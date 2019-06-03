package jp.co.cyberagent.itemae.domain.service

import jp.co.cyberagent.itemae.domain.repository.UserRepository
import jp.co.cyberagent.itemae.domain.model.entity.User
import jp.co.cyberagent.itemae.domain.util.getNow
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.stereotype.Service
import java.lang.Exception
import java.sql.Date
import java.sql.Timestamp

@Service
class UserService(
        private val userRepository: UserRepository
) {
    fun createUser(user: User) :User{
        try {
            user.createdAt = getNow()
            return userRepository.save(user)
        } catch (e: DataIntegrityViolationException) {
            throw e
        } catch (e: Exception) {
            //TODO : log
            throw e
        }
    }

    fun findUserById(id: Long): User {
        try {
            return userRepository.findById(id).get()
        } catch (e: DataIntegrityViolationException) {
            throw e
        } catch (e: EmptyResultDataAccessException) {
            throw e
        } catch (e: Exception) {
            //TODO : log
            throw e
        }
    }

    fun findUserByScreenName(screenName: String): User {
        try {
            return userRepository.findByScreenName(screenName)
        } catch (e: DataIntegrityViolationException) {
            throw e
        } catch (e: EmptyResultDataAccessException) {
            throw e
        } catch (e: Exception) {
            //TODO : log
            throw e
        }
    }

    fun searchUser(keyword: String): List<User> {
        try {
            return userRepository.findByKeyword(keyword)
        } catch (e: DataIntegrityViolationException) {
            throw e
        } catch (e: EmptyResultDataAccessException) {
            throw e
        } catch (e: Exception) {
            //TODO : log
            throw e
        }
    }

    fun editUser(screenName: String, name: String, description: String, location: String, url: String, bornOn: Date, updatedAt: Timestamp, id: Long) {
        userRepository.editById(screenName,name,description,location,url,bornOn,updatedAt,id)
    }
}
