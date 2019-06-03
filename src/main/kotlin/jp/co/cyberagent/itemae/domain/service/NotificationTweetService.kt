package jp.co.cyberagent.itemae.domain.service

import jp.co.cyberagent.itemae.domain.model.entity.NotificationTweet
import jp.co.cyberagent.itemae.domain.repository.NotificationTweetRepository
import jp.co.cyberagent.itemae.domain.util.getNow
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service

@Service
class NotificationTweetService (
        private val notificationTweetRepository: NotificationTweetRepository
) {
    fun create(notificationId: Long, tweetId: Long) {
        try {
            notificationTweetRepository.save(NotificationTweet(
                    notificationId = notificationId,
                    tweetId = tweetId,
                    createdAt = getNow()
            ))
        } catch (e: DataIntegrityViolationException) {
            throw e
        } catch (e: Exception) {
            //TODO : log
            throw e
        }
    }
    fun findByUserId(notificationId: List<Long>) : List<Long> {
        try {
            return notificationTweetRepository.findByNotificationId(notificationId)
        } catch (e: DataIntegrityViolationException) {
            throw e
        } catch (e: Exception) {
            //TODO : log
            throw e
        }
    }

    fun findById(notificationId: Long): NotificationTweet {
        return notificationTweetRepository.findById(notificationId).get()
    }
}