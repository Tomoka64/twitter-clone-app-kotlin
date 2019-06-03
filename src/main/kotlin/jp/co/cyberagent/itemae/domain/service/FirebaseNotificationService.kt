package jp.co.cyberagent.itemae.domain.service

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import jp.co.cyberagent.itemae.domain.model.entity.User
import org.springframework.stereotype.Service

@Service
class FirebaseNotificationService(
        private val tokenService: TokenService,
        private val tweetService: TweetService,
        private val notificationTweetService: NotificationTweetService,
        private val notificationService: NotificationService
) {
    fun send(type: String, user: User, id: Long) {
        try {
            val author = tweetService.findById(id)
            try {
                val builder = Message.builder()
                val userToken = tokenService.findToken(user.id).token
                val token = FirebaseAuth.getInstance().createCustomTokenAsync(userToken).get()

                var message = ""
                when (type) {
                    "favorite" -> {
                        message = "%sさんがあなたのツイートをいいねしました".format(user.screenName)
                    }
                    "retweet" -> {
                        message = "%sさんがあなたのツイートをリツイートしました".format(user.screenName)
                    }
                    "follow" -> {
                        message = "%sさんがあなたをフォローしました".format(user.screenName)
                    }
                }


                val notification = Notification("itemae", message)
                val messenger = builder
                        .setNotification(notification)
                        .setToken(token)
                        .build()
                FirebaseMessaging.getInstance().sendAsync(messenger)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val not = notificationService.create(user.id, author.userId, type)

            if (type == "favorite" || type == "retweet") {
                notificationTweetService.create(not.id, id)
            }

        } catch (e: Exception) {
            throw e
        }
    }
}