package jp.co.cyberagent.itemae.domain.util

import com.google.protobuf.Timestamp as protoTimeStamp
import java.time.LocalDateTime
import java.time.ZoneId
import java.sql.Timestamp as sqlTimeStamp

class TimeConverter() {
    fun splTimeStampToProtoTimestamp(timeIn: sqlTimeStamp):protoTimeStamp {
       return getProtoTimeStamp(timeIn.toLocalDateTime())
    }
    fun protoTimeStampToSqlTimeStamp(timeIn: protoTimeStamp): sqlTimeStamp {
        return sqlTimeStamp(timeIn.seconds)
    }
    private fun getProtoTimeStamp(date: LocalDateTime): protoTimeStamp {
        return protoTimeStamp.newBuilder().setSeconds(date.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()).build()
    }
}