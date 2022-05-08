package hu.koleszr.activityexport;

public record Error(String description) {
    @Override
    public String toString() {
        return description;
    }
}
