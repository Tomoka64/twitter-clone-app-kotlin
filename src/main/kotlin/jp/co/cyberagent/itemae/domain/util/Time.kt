package jp.co.cyberagent.itemae.domain.util

import java.sql.Timestamp
import java.time.Instant

fun getNow():Timestamp {
    return Timestamp.from(Instant.now())
}