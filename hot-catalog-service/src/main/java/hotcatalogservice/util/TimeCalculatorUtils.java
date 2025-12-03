package hotcatalogservice.util;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;

public class TimeCalculatorUtils {
    // 또는 이번 주 일요일 23:59:59까지
    public static Duration calculateDurationToEndOfWeek() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endOfWeek = now
                .with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
                .with(LocalTime.MAX);
        return Duration.between(now, endOfWeek);
    }

}
