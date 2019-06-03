package jp.co.cyberagent.itemae.domain.repository

import jp.co.cyberagent.itemae.domain.model.entity.Friend
import jp.co.cyberagent.itemae.domain.model.entity.FriendPrimaryKey
import jp.co.cyberagent.itemae.domain.model.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface FriendRepository : JpaRepository<Friend,Long> {
    fun findByFriendPrimaryKey(friendKey: FriendPrimaryKey): List<Friend>

    @Query("SELECT u " +
            "FROM User u " +
            "JOIN Friend f ON u.id = f.friendPrimaryKey.followedUserId " +
            "WHERE f.friendPrimaryKey.followingUserId = ?1")
    fun findFollowingUserList(id: Long): List<User>

    @Query("SELECT u " +
            "FROM User u " +
            "JOIN Friend f ON u.id = f.friendPrimaryKey.followingUserId " +
            "WHERE f.friendPrimaryKey.followedUserId = ?1")
    fun findFollowedUserList(id: Long): List<User>


    @Query("SELECT f.friendPrimaryKey.followedUserId " +
            "FROM Friend f " +
            "WHERE f.friendPrimaryKey.followingUserId = ?1")
    fun findFollowerIds(id: Long): List<Long>

    @Query("SELECT f.friendPrimaryKey.followingUserId " +
            "FROM Friend f " +
            "WHERE f.friendPrimaryKey.followedUserId = ?1")
    fun findFollowedIds(id: Long): List<Long>

    @Query("SELECT f.friendPrimaryKey.followedUserId " +
            "FROM Friend f " +
            "WHERE f.friendPrimaryKey.followingUserId = ?1 AND f.friendPrimaryKey.followedUserId = ?2")
    fun containFollowerId(myId: Long, targetId: Long): List<Long>

    @Query("SELECT f.friendPrimaryKey.followingUserId " +
            "FROM Friend f " +
            "WHERE f.friendPrimaryKey.followedUserId = ?1 AND f.friendPrimaryKey.followingUserId = ?2")
    fun containFollowedId(myId: Long, targetId: Long): List<Long>

}
