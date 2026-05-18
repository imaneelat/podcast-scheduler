package app;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

public class PodcastSchedulerApp extends Application {

    private EpisodeRepository repo = new EpisodeRepository();
    private ListView<Episode> episodeListView = new ListView<>();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Podcast Episode Scheduler");

        TextField titleField = new TextField();
        titleField.setPromptText("Episode Title");

        TextField durationField = new TextField();
        durationField.setPromptText("Minutes");
        durationField.setPrefWidth(70);

        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("Regular", "Bonus");
        typeBox.setValue("Regular");

        Button createBtn = new Button("Create Episode");

        HBox createBox = new HBox(10, new Label("Title:"), titleField, new Label("Dur:"), durationField, typeBox, createBtn);
        createBox.setPadding(new Insets(10));
        createBox.setAlignment(Pos.CENTER_LEFT);

        DatePicker datePicker = new DatePicker();
        TextField timeField = new TextField("12:00");
        timeField.setPrefWidth(60);
        Button scheduleBtn = new Button("Schedule Selected");

        HBox scheduleBox = new HBox(10, new Label("Date:"), datePicker, new Label("Time:"), timeField, scheduleBtn);
        scheduleBox.setPadding(new Insets(10));
        scheduleBox.setAlignment(Pos.CENTER_LEFT);

        Button publishBtn = new Button("Publish Selected");
        Button saveBtn = new Button("Save Data");

        HBox actionBox = new HBox(10, publishBtn, saveBtn);
        actionBox.setPadding(new Insets(10));
        actionBox.setAlignment(Pos.CENTER);

        VBox root = new VBox(10, createBox, scheduleBox, new Label("  Episodes List:"), episodeListView, actionBox);
        root.setPadding(new Insets(10));


        createBtn.setOnAction(e -> {
            try {
                String title = titleField.getText();
                int dur = Integer.parseInt(durationField.getText());
                repo.createEpisode(typeBox.getValue(), title, dur);
                refreshList();
                titleField.clear();
                durationField.clear();
            } catch (NumberFormatException ex) {
                showAlert("Input Error", "Duration must be a number.");
            }
        });

        scheduleBtn.setOnAction(e -> {
            Episode selected = episodeListView.getSelectionModel().getSelectedItem();
            if (selected == null || datePicker.getValue() == null) {
                showAlert("Warning", "Select an episode and a date.");
                return;
            }
            try {
                LocalTime time = LocalTime.parse(timeField.getText());
                LocalDateTime dt = LocalDateTime.of(datePicker.getValue(), time);
                repo.scheduleEpisode(selected, dt);
                refreshList();
                showAlert("Success", "Episode scheduled!");
            } catch (ScheduleConflictException | DateTimeParseException ex) {
                showAlert("Error", ex.getMessage());
            }
        });

        publishBtn.setOnAction(e -> {
            Episode selected = episodeListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                selected.publish(LocalDateTime.now());
                refreshList();
            }
        });

        saveBtn.setOnAction(e -> {
            try {
                repo.saveToFile();
                showAlert("Success", "Data saved successfully.");
            } catch (EpisodePersistenceException ex) {
                showAlert("Error", "Could not save file.");
            }
        });

        refreshList();

        Scene scene = new Scene(root, 650, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void refreshList() {
        episodeListView.getItems().clear();
        episodeListView.getItems().addAll(repo.getEpisodes());
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}