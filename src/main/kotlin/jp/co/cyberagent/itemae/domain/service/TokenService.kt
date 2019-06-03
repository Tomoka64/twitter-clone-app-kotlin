package jp.co.cyberagent.itemae.domain.service

import jp.co.cyberagent.itemae.domain.model.entity.Token
import jp.co.cyberagent.itemae.domain.repository.TokenReponsitory
import jp.co.cyberagent.itemae.domain.util.getNow
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service


@Service
class TokenService (
        private val tokenReponsitory: TokenReponsitory
) {
    fun saveToken(userId: Long, token: String) {
        try {
            tokenReponsitory.save(Token(
                    userId = userId,
                    token = token,
                    createdAt = getNow()
            ))
        } catch (e: DataIntegrityViolationException) {
            throw e
        } catch (e: Exception) {
            //TODO : log
            throw e
        }
    }
    fun findToken(userId: Long): Token {
        try {
            return tokenReponsitory.findById(userId).get()
        } catch (e: DataIntegrityViolationException) {
            throw e
        } catch (e: Exception) {
            //TODO : log
            throw e
        }
    }

    fun deleteToken(userId: Long) {
        try {
            tokenReponsitory.deleteById(userId)
        } catch (e: DataIntegrityViolationException) {
            throw e
        } catch (e: Exception) {
            //TODO : log
            throw e
        }
    }
}