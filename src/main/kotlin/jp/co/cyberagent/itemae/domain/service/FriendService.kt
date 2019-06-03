package jp.co.cyberagent.itemae.domain.service

import jp.co.cyberagent.itemae.domain.model.entity.Friend
import jp.co.cyberagent.itemae.domain.model.entity.FriendPrimaryKey
import jp.co.cyberagent.itemae.domain.model.entity.User
import jp.co.cyberagent.itemae.domain.repository.FriendRepository
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.stereotype.Service
import java.lang.Exception
import java.sql.Timestamp

@Service
class FriendService (
        private val friendRepository: FriendRepository
) {
    fun follow(userId: Long, targetUserId: Long) {
        val friendKey = FriendPrimaryKey(userId,targetUserId)
        val friend = Friend(friendKey, Timestamp(System.currentTimeMillis()))
        try {
            friendRepository.save(friend)
        } catch (e: DataIntegrityViolationException) {
            throw e
        } catch (e: Exception) {
            //TODO : log
            throw e
        }
    }

    fun unfollow(userId: Long, targetUserId: Long) {
        val friend = friendRepository.findByFriendPrimaryKey(FriendPrimaryKey(userId,targetUserId)).get(0)
        try {
            friendRepository.delete(friend)
        } catch (e: DataIntegrityViolationException) {
            throw e
        } catch (e: Exception) {
            //TODO : log
            throw e
        }
    }

    fun findFollowingOrFollowedUserList(id: Long, isFollowing: Boolean): List<User> {
        when (isFollowing) {
            true -> try {
                return friendRepository.findFollowingUserList(id)
            } catch (e: DataIntegrityViolationException) {
                throw e
            } catch (e: EmptyResultDataAccessException) {
                throw e
            } catch (e: Exception) {
                //TODO : log
                throw e
            }
            false -> try {
                return friendRepository.findFollowedUserList(id)
            } catch (e: DataIntegrityViolationException) {
                throw e
            } catch (e: EmptyResultDataAccessException) {
                throw e
            } catch (e: Exception) {
                //TODO : log
                throw e
            }
        }
    }

    fun findFollowerIds(id: Long): List<Long> {
        try {
            return friendRepository.findFollowerIds(id)
        } catch (e: DataIntegrityViolationException) {
            throw e
        } catch (e: Exception) {
            //TODO : log
            throw e
        }
    }

    fun findFollowedIds(id: Long): List<Long> {
        try {
            return friendRepository.findFollowedIds(id)
        } catch (e: DataIntegrityViolationException) {
            throw e
        } catch (e: Exception) {
            //TODO : log
            throw e
        }
    }

    fun containFollowerId(myId: Long, targetId: Long): List<Long> {
        try {
            return friendRepository.containFollowerId(myId,targetId)
        } catch (e: DataIntegrityViolationException) {
            throw e
        } catch (e: Exception) {
            //TODO : log
            throw e
        }
    }

    fun containFollowedId(myId: Long, targetId: Long): List<Long> {
        try {
            return friendRepository.containFollowedId(myId,targetId)
        } catch (e: DataIntegrityViolationException) {
            throw e
        } catch (e: Exception) {
            //TODO : log
            throw e
        }
    }
}