package jp.co.cyberagent.itemae.domain.service

import jp.co.cyberagent.itemae.domain.model.entity.Favorite
import jp.co.cyberagent.itemae.domain.model.entity.FavoritePrimaryKey
import jp.co.cyberagent.itemae.domain.repository.FavoriteRepository
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.stereotype.Service
import java.lang.Exception
import java.sql.Timestamp

@Service
class FavoriteService (
        private val favoriteRepository: FavoriteRepository
) {
    fun createFavorite(userId: Long, tweetId: Long) {
        val favoritePrimaryKey = FavoritePrimaryKey(userId, tweetId)
        val favorite = Favorite(favoritePrimaryKey, Timestamp(System.currentTimeMillis()))
        try {
            favoriteRepository.save(favorite)
        } catch (e: DataIntegrityViolationException) {
            throw e
        } catch (e: Exception) {
            //TODO : log
            throw e
        }
    }

    fun deleteFavorite(userId: Long, tweetId: Long) {
        val favorite = favoriteRepository.findByFavoritePrimaryKey(FavoritePrimaryKey(userId,tweetId))
        try {
            favoriteRepository.delete(favorite)
        } catch (e: DataIntegrityViolationException) {
            throw e
        } catch (e: EmptyResultDataAccessException) {
            throw e
        } catch (e: Exception) {
            //TODO : log
            throw e
        }
    }

    fun countFavorite(tweetId: Long): Long {
        return favoriteRepository.count(tweetId)
    }

    fun findFavoriteIds(userId: Long):List<Long> {
        return favoriteRepository.findByUserId(userId)
    }

    fun findTrendTweetIds():List<Long> {
        return favoriteRepository.findFavoriteTweetId()
    }
}