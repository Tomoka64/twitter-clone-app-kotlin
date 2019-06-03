package jp.co.cyberagent.itemae.handler.Interceptor

import autovalue.shaded.com.`google$`.common.base.`$Strings`.nullToEmpty
import io.grpc.*
import jp.co.cyberagent.itemae.domain.service.UserAuthorityService
import jp.co.cyberagent.itemae.handler.context.ContextHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import com.google.firebase.auth.FirebaseAuth

@Component
class AuthorizationInterceptor(
        @Autowired private val userAuthorityService: UserAuthorityService
): ServerInterceptor {

    override fun <ReqT, RespT> interceptCall(
            call: ServerCall<ReqT, RespT>?,
            headers: Metadata?,
            next: ServerCallHandler<ReqT, RespT>?): ServerCall.Listener<ReqT> {
        val token = nullToEmpty(headers!!.get(Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER)))

        try {
            val userRecord = FirebaseAuth.getInstance().verifyIdTokenAsync(token)
            val userAuthority = userAuthorityService.findByFirebaseUid(userRecord.get().uid)
            val userId = userAuthority.userId
            val serverCall = object : ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(call) {}

            val ctx = ContextHandler.setUserId(Context.current(), userId)
            return Contexts.interceptCall(ctx, serverCall, headers, next)
        } catch (e: Exception) {
            call?.close(Status.UNAUTHENTICATED, Metadata())
            throw e
        }
    }
}
