package jp.co.cyberagent.itemae.domain.service

import jp.co.cyberagent.itemae.domain.model.entity.ReTweet
import jp.co.cyberagent.itemae.domain.model.entity.ReTweetPrimaryKey
import jp.co.cyberagent.itemae.domain.repository.ReTweetRepository
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import java.lang.Exception
import java.sql.Timestamp

@Service
class ReTweetService (
        private val reTweetRepository: ReTweetRepository
) {
    fun createReTweet(userId: Long, tweetId: Long) {
        val reTweetKey = ReTweetPrimaryKey(userId,tweetId)
        val reTweet = ReTweet(reTweetKey, Timestamp(System.currentTimeMillis()))
        try {
            reTweetRepository.save(reTweet)
        } catch (e: DataIntegrityViolationException) {
            throw e
        } catch (e: Exception) {
            //TODO : log
            throw e
        }
    }
    fun deleteReTweet(userId: Long, tweetId: Long) {
        val favorite = reTweetRepository.findByReTweetPrimaryKey(ReTweetPrimaryKey(userId,tweetId))
        try {
            reTweetRepository.delete(favorite)
        } catch (e: DataIntegrityViolationException) {
            throw e
        } catch (e: Exception) {
            //TODO : log
            throw e
        }

    }

    fun countReTweet(tweetId: Long):Long {
        return reTweetRepository.count(tweetId)
    }

    fun findReTweetIds(userId: Long):List<Long> {
        return reTweetRepository.findByUserId(userId)
    }
}