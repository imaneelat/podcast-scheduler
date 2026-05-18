package model;

public enum EpisodeStatus {
    DRAFT("Draft"),
    SCHEDULED("Scheduled"),
    PUBLISHED("Published");

    private final String label;

    EpisodeStatus(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}