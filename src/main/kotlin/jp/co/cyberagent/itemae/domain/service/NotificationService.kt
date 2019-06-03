package jp.co.cyberagent.itemae.domain.service

import jp.co.cyberagent.itemae.domain.model.entity.Notification
import jp.co.cyberagent.itemae.domain.repository.NotificationRepository
import jp.co.cyberagent.itemae.domain.util.getNow
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.stereotype.Service

@Service
class NotificationService (
        private val notificationRepository: NotificationRepository
) {
    fun create(sender: Long, receiver: Long, type: String):Notification {
        try {
            return notificationRepository.save(Notification(
                    senderUserId = sender,
                    receiverUserId = receiver,
                    notificationTypeName = type,
                    createdAt = getNow()
            ))
        } catch (e: DataIntegrityViolationException) {
            throw e
        } catch (e: Exception) {
            //TODO : log
            throw e
        }
    }

    fun findBySenderId(sender: Long):List<Notification> {
        try {
            return notificationRepository.findBySenderUserId(sender)
        } catch (e: EmptyResultDataAccessException) {
            throw e
        } catch (e: DataIntegrityViolationException) {
            throw e
        } catch (e: Exception) {
            //TODO : log
            throw e
        }
    }

    fun findByReceiverId(receiver: Long):List<Notification> {
        try {
            return notificationRepository.findByReceiverUserId(receiver)
        } catch (e: EmptyResultDataAccessException) {
            throw e
        } catch (e: DataIntegrityViolationException) {
            throw e
        } catch (e: Exception) {
            //TODO : log
            throw e
        }
    }
}