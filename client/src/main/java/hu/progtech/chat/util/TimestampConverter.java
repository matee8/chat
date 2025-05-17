package hu.progtech.chat.util;

import com.google.protobuf.Timestamp;
import com.google.protobuf.util.Timestamps;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public final class TimestampConverter {
    private TimestampConverter() {}

    public static LocalDateTime toLocalDateTime(final Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }

        return LocalDateTime.ofInstant(
                Instant.ofEpochMilli(Timestamps.toMillis(timestamp)), ZoneId.systemDefault());
    }

    public static Timestamp fromLocalDateTime(final LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }

        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());

        long millis = zonedDateTime.toInstant().toEpochMilli();

        return Timestamps.fromMillis(millis);
    }
}
