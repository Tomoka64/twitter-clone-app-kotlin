package jp.co.cyberagent.itemae.domain.service

import jp.co.cyberagent.itemae.domain.model.entity.Tweet
import jp.co.cyberagent.itemae.domain.repository.TweetRepository
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import java.lang.Exception

@Service
class TweetService (
        private val tweetRepository: TweetRepository
) {
    fun createTweet(tweet: Tweet):Tweet {
        try {
            return tweetRepository.save(tweet)
        } catch (e: DataIntegrityViolationException) {
            //TODO : log
            throw e
        } catch (e: Exception) {
            throw e
        }
    }

    fun findTweetById(id: Long): Tweet {
        try {
            return tweetRepository.findById(id).get()
        } catch (e: DataIntegrityViolationException) {
            //TODO : log
            throw e
        } catch (e: Exception) {
            throw e
        }
    }

    fun deleteTweet(tweet: Tweet) {
        try {
            tweetRepository.delete(tweet)
        } catch (e: DataIntegrityViolationException) {
            //TODO : log
            throw e
        } catch (e: Exception) {
            throw e
        }
    }

    fun findTweetListByUserID(userId: Long): List<Tweet> {
        try {
            return tweetRepository.findByUserId(userId)
        } catch (e: DataIntegrityViolationException) {
            //TODO : log
            throw e
        } catch (e: Exception) {
            throw e
        }
    }

    fun findById(tweetId: Long): Tweet {
        return tweetRepository.findById(tweetId).get()
    }

    fun findTweetList(follower: List<Long>, id: Long): List<Array<Any>>{
        return tweetRepository.getFeed(follower,id)
    }

    fun findReTweetList(follower: List<Long>, id: Long): List<Array<Any>>{
        return tweetRepository.getReTweetFeed(follower, id)
    }

    fun findMyTweetList(userId: Long): List<Array<Any>>{
        return tweetRepository.getMyFeed(userId)
    }

    fun findMyReTweetList(userId: Long): List<Array<Any>>{
        return tweetRepository.getMyReTweetFeed(userId)
    }

    fun searchTweets(keyword: String): List<Array<Any>>{
        return tweetRepository.searchByKeyword(keyword)
    }

    fun findTweetsByTweetIds(tweetIds: List<Long>): List<Array<Any>>{
        return tweetRepository.findByTweetIds(tweetIds)
    }
}
