package model;

public class BonusEpisode extends Episode {
    private static final long serialVersionUID = 1L;

    public BonusEpisode(int id, String title, int durationMinutes) {
        super(id, title, durationMinutes);
    }

    @Override
    public String getTypeLabel() {
        return "Bonus";
    }
}