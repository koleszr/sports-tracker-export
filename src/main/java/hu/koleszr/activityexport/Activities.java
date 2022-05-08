package hu.koleszr.activityexport;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public record Activities(Error error, List<Activity> payload) {
    public Stream<Activity> stream() {
        if (Objects.nonNull(error)) {
            System.err.printf("Failed to get activities: %s!%n", error);
            return Stream.empty();
        }

        return payload.stream();
    }
}
