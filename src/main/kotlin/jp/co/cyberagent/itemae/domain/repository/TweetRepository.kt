package jp.co.cyberagent.itemae.domain.repository

import jp.co.cyberagent.itemae.domain.model.entity.Tweet
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface TweetRepository : JpaRepository<Tweet, Long> {
    fun findByUserId(userId: Long): List<Tweet>

    @Query(value="SELECT u.id as uid, u.screen_name, u.name, u.description, u.location, u.url, u.born_on, u.created_at as user_created_at, u.updated_at, tw.id as tid, tw.text, tw.created_at as tweet_created_at " +
            "FROM (SELECT * FROM tweets t WHERE (t.user_id in ?1) OR (t.user_id = ?2) AND TIMESTAMPDIFF(HOUR,t.created_at,CURRENT_TIMESTAMP) < 72 ORDER BY t.created_at DESC LIMIT 50) as tw JOIN users u ON tw.user_id = u.id",
            nativeQuery = true)
    fun getFeed(follower: List<Long>, id: Long): List<Array<Any>>

    @Query(value="SELECT tw_u.id AS tw_u_user_id, tw_u.screen_name AS tw_u_screen_name, tw_u.name AS tw_u_name, tw_u.description AS tw_u_description, tw_u.location AS tw_u_location, tw_u.url AS tw_u_url, tw_u.born_on AS tw_u_born_on, tw_u.created_at AS tw_u_created_at, tw_u.updated_at AS tw_u_updated_at, tw_and_rw_and_u.tw_id, tw_and_rw_and_u.tw_text, tw_and_rw_and_u.tw_created_at, tw_and_rw_and_u.rw_user_id, tw_and_rw_and_u.rw_u_screen_name, tw_and_rw_and_u.rw_u_name, tw_and_rw_and_u.rw_u_description, tw_and_rw_and_u.rw_u_location, tw_and_rw_and_u.rw_u_url, tw_and_rw_and_u.rw_u_born_on, tw_and_rw_and_u.rw_u_created_at, tw_and_rw_and_u.rw_u_updated_at, tw_and_rw_and_u.rw_created_at" +
            " FROM ( SELECT tw.id AS tw_id, tw.user_id AS tw_user_id, tw.text AS tw_text, tw.created_at AS tw_created_at, rw_and_u.rw_user_id, rw_and_u.rw_tweet_id, rw_and_u.rw_created_at, rw_and_u.rw_u_id, rw_and_u.rw_u_screen_name, rw_and_u.rw_u_name, rw_and_u.rw_u_description, rw_and_u.rw_u_location, rw_and_u.rw_u_url, rw_and_u.rw_u_born_on, rw_and_u.rw_u_created_at, rw_and_u.rw_u_updated_at FROM tweets AS tw INNER JOIN( SELECT rw.user_id AS rw_user_id, rw.tweet_id AS rw_tweet_id, rw.created_at AS rw_created_at, rw_u.id AS rw_u_id, rw_u.screen_name AS rw_u_screen_name, rw_u.name AS rw_u_name, rw_u.description AS rw_u_description, rw_u.location AS rw_u_location, rw_u.url AS rw_u_url, rw_u.born_on AS rw_u_born_on, rw_u.created_at AS rw_u_created_at, rw_u.updated_at AS rw_u_updated_at FROM retweets AS rw INNER JOIN users AS rw_u ON rw.user_id = rw_u.id WHERE (rw.user_id in ?1) OR (rw.user_id = ?2) ORDER BY rw.created_at DESC LIMIT 50 ) AS rw_and_u ON tw.id = rw_and_u.rw_tweet_id ) AS tw_and_rw_and_u INNER JOIN users AS tw_u ON tw_and_rw_and_u.tw_user_id = tw_u.id",
            nativeQuery = true)
    fun getReTweetFeed(follower: List<Long>, id: Long): List<Array<Any>>

    @Query(value="SELECT u.id as uid, u.screen_name, u.name, u.description, u.location, u.url, u.born_on, u.created_at as user_created_at, u.updated_at, tw.id as tid, tw.text, tw.created_at as tweet_created_at " +
            "FROM (SELECT * FROM tweets t WHERE t.user_id = ?1 AND TIMESTAMPDIFF(HOUR,t.created_at,CURRENT_TIMESTAMP) < 72 ORDER BY t.created_at DESC LIMIT 50) as tw JOIN users u ON tw.user_id = u.id",
            nativeQuery = true)
    fun getMyFeed(id: Long): List<Array<Any>>

    @Query(value="SELECT tw_u.id AS tw_u_user_id, tw_u.screen_name AS tw_u_screen_name, tw_u.name AS tw_u_name, tw_u.description AS tw_u_description, tw_u.location AS tw_u_location, tw_u.url AS tw_u_url, tw_u.born_on AS tw_u_born_on, tw_u.created_at AS tw_u_created_at, tw_u.updated_at AS tw_u_updated_at, tw_and_rw_and_u.tw_id, tw_and_rw_and_u.tw_text, tw_and_rw_and_u.tw_created_at, tw_and_rw_and_u.rw_user_id, tw_and_rw_and_u.rw_u_screen_name, tw_and_rw_and_u.rw_u_name, tw_and_rw_and_u.rw_u_description, tw_and_rw_and_u.rw_u_location, tw_and_rw_and_u.rw_u_url, tw_and_rw_and_u.rw_u_born_on, tw_and_rw_and_u.rw_u_created_at, tw_and_rw_and_u.rw_u_updated_at, tw_and_rw_and_u.rw_created_at" +
            " FROM ( SELECT tw.id AS tw_id, tw.user_id AS tw_user_id, tw.text AS tw_text, tw.created_at AS tw_created_at, rw_and_u.rw_user_id, rw_and_u.rw_tweet_id, rw_and_u.rw_created_at, rw_and_u.rw_u_id, rw_and_u.rw_u_screen_name, rw_and_u.rw_u_name, rw_and_u.rw_u_description, rw_and_u.rw_u_location, rw_and_u.rw_u_url, rw_and_u.rw_u_born_on, rw_and_u.rw_u_created_at, rw_and_u.rw_u_updated_at FROM tweets AS tw INNER JOIN( SELECT rw.user_id AS rw_user_id, rw.tweet_id AS rw_tweet_id, rw.created_at AS rw_created_at, rw_u.id AS rw_u_id, rw_u.screen_name AS rw_u_screen_name, rw_u.name AS rw_u_name, rw_u.description AS rw_u_description, rw_u.location AS rw_u_location, rw_u.url AS rw_u_url, rw_u.born_on AS rw_u_born_on, rw_u.created_at AS rw_u_created_at, rw_u.updated_at AS rw_u_updated_at FROM retweets AS rw INNER JOIN users AS rw_u ON rw.user_id = rw_u.id WHERE (rw.user_id = ?1) ORDER BY rw.created_at DESC LIMIT 50 ) AS rw_and_u ON tw.id = rw_and_u.rw_tweet_id ) AS tw_and_rw_and_u INNER JOIN users AS tw_u ON tw_and_rw_and_u.tw_user_id = tw_u.id",
            nativeQuery = true)
    fun getMyReTweetFeed(id: Long): List<Array<Any>>

    @Query(value = "SELECT u.id as uid, u.screen_name, u.name, u.description, u.location, u.url, u.born_on, u.created_at as user_created_at, u.updated_at, t4.id as tid, t4.text, t4.created_at as tweet_created_at FROM (SELECT * FROM (SELECT * FROM (SELECT * FROM tweets t WHERE TIMESTAMPDIFF(HOUR,t.created_at,CURRENT_TIMESTAMP) < 72) as t2 ) as t3 WHERE UPPER(t3.text) LIKE UPPER(CONCAT('%',?1,'%')) ORDER BY t3.created_at DESC LIMIT 50) as t4 JOIN users u ON t4.user_id = u.id",
            nativeQuery = true)
    fun searchByKeyword(keyword: String): List<Array<Any>>

    @Query(value="SELECT u.id as uid, u.screen_name, u.name, u.description, u.location, u.url, u.born_on, u.created_at as user_created_at, u.updated_at, tw.id as tid, tw.text, tw.created_at as tweet_created_at " +
            "FROM (SELECT * FROM tweets t WHERE TIMESTAMPDIFF(HOUR,t.created_at,CURRENT_TIMESTAMP) < 72 AND (t.id in ?1) ORDER BY t.created_at DESC LIMIT 50) as tw JOIN users u ON tw.user_id = u.id",
            nativeQuery = true)
    fun findByTweetIds(tweetIds: List<Long>): List<Array<Any>>
}