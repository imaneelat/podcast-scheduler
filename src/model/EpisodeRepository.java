package model;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EpisodeRepository {

    private List<Episode> episodes = new ArrayList<>();
    private int nextId = 1;
    private static final String FILE_NAME = "episodes.txt";

    public EpisodeRepository() {
        loadFromFile();
    }

    public Episode createEpisode(String type, String title, int duration) {
        Episode newEpisode;
        if ("Bonus".equalsIgnoreCase(type)) {
            newEpisode = new BonusEpisode(nextId++, title, duration);
        } else {
            newEpisode = new RegularEpisode(nextId++, title, duration);
        }
        episodes.add(newEpisode);
        return newEpisode;
    }

    public void scheduleEpisode(Episode episode, LocalDateTime scheduleTime) throws ScheduleConflictException {
        Optional<Episode> conflict = episodes.stream()
                .filter(e -> e.getStatus() == EpisodeStatus.SCHEDULED && e.getScheduledTime().equals(scheduleTime))
                .findFirst();

        if (conflict.isPresent() && !conflict.get().equals(episode)) {
            throw new ScheduleConflictException("Another episode is already scheduled for " + scheduleTime);
        }

        episode.schedule(scheduleTime);
    }

    public List<Episode> getEpisodes() {
        return episodes;
    }

    public void saveToFile() throws EpisodePersistenceException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(episodes);
            oos.writeInt(nextId);
        } catch (IOException e) {
            throw new EpisodePersistenceException("Failed to save data to file.", e);
        }
    }


    private void loadFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            episodes = (List<Episode>) ois.readObject();
            nextId = ois.readInt();
        } catch (Exception e) {

            episodes = new ArrayList<>();
            nextId = 1;
        }
    }
}