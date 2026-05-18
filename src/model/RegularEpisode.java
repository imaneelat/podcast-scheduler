package model;

public class RegularEpisode extends Episode {
    private static final long serialVersionUID = 1L;

    public RegularEpisode(int id, String title, int durationMinutes) {
        super(id, title, durationMinutes);
    }

    @Override
    public String getTypeLabel() {
        return "Regular";
    }
}