package model;

import java.io.Serializable;
import java.time.LocalDateTime;

    public abstract class Episode implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String title;
    private int durationMinutes;
    private EpisodeStatus status;
    private LocalDateTime scheduledTime;

    public Episode(int id, String title, int durationMinutes) {
        this.id = id;
        this.title = title;
        this.durationMinutes = durationMinutes;
        this.status = EpisodeStatus.DRAFT;
        this.scheduledTime = null;
    }

    public abstract String getTypeLabel();

    public void schedule(LocalDateTime time) throws ScheduleConflictException {
        if (this.status == EpisodeStatus.PUBLISHED) {
            throw new ScheduleConflictException("Cannot schedule an already published episode.");
        }
        if (time.isBefore(LocalDateTime.now())) {
            throw new ScheduleConflictException("Cannot schedule an episode in the past.");
        }
        this.scheduledTime = time;
        this.status = EpisodeStatus.SCHEDULED;
    }

    public void publish(LocalDateTime publishTime) {
        if (this.status == EpisodeStatus.SCHEDULED) {
            if (scheduledTime != null && publishTime.isAfter(scheduledTime)) {
                this.status = EpisodeStatus.PUBLISHED;
            }
        }
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public int getDurationMinutes() { return durationMinutes; }
    public EpisodeStatus getStatus() { return status; }
    public LocalDateTime getScheduledTime() { return scheduledTime; }

    public String getStatusDetail() {
        if (status == EpisodeStatus.SCHEDULED && scheduledTime != null) {
            return status.toString() + " (" + scheduledTime.toString().replace("T", " ") + ")";
        }
        return status.toString();
    }

    @Override
    public String toString() {
        return String.format("[%s] %s - %d min - Status: %s",
                getTypeLabel(), title, durationMinutes, getStatusDetail());
    }
}
