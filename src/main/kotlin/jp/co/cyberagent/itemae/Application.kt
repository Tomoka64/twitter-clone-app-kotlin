package jp.co.cyberagent.itemae

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import java.util.Base64
import java.nio.charset.StandardCharsets


@SpringBootApplication
class Application {
	init {
		val credential = System.getenv("FIREBASE_CREDENTIAL") ?: ""

		val firebaseCredential = credential.byteInputStream()

		val options = FirebaseOptions.Builder()
				.setCredentials(GoogleCredentials.fromStream(firebaseCredential))
				.build()

		FirebaseApp.initializeApp(options)
	}
}

fun main(args: Array<String>) {
	SpringApplicationBuilder()
			.sources(Application::class.java)
			.web(WebApplicationType.REACTIVE)
			.run(*args)
}
