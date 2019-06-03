package jp.co.cyberagent.itemae.handler.context

import io.grpc.Context

object ContextHandler {

    private val userId :Context.Key<Long> = Context.key("user_id")

    fun setUserId(ctx: Context, value: Long) = ctx.withValue(userId, value)

    fun getUserId():Long{
        return userId.get()
    }
}
