package hu.koleszr.activityexport;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public record Activity(String workoutKey, long startTime) {
    public LocalDateTime localStartTime() {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(startTime), ZoneId.systemDefault());
    }
}
