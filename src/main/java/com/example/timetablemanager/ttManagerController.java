package com.example.timetablemanager;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

public class ttManagerController {

    @FXML
    private Button btnAddCourse, btnEnrollStudent, btnAssignClassroom, btnSwapClassroom, btnSearch;

    @FXML
    private MenuItem menuImportCSV, menuLoadTimetable, menuSaveTimetable, menuExportTimetable, menuExit;
    @FXML
    private MenuItem menuUserManual, menuAbout;

    @FXML
    private TableView<Course> timetableTable;

    @FXML
    private TableColumn<Course, String> courseIDColumn,lecturerColumn,dayColumn,timeColumn,enrolledStudentsColumn,classroomColumn,capacityColumn;


    @FXML
    public void initialize() {
        // Course ID
        courseIDColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getCourseID()));

        // Lecturer
        lecturerColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getLecturer()));

        // Days
        dayColumn.setCellValueFactory(data -> {
            // Extract the part before the space as the day
            List<String> days = data.getValue().getDays();
            String extractedDays = days.stream()
                    .map(day -> day.split(" ")[0]) // Take the part before the first space
                    .distinct()
                    .collect(Collectors.joining(", "));
            return new javafx.beans.property.SimpleStringProperty(extractedDays);
        });

        // Times
        timeColumn.setCellValueFactory(data -> {
            // Extract the part after the space as the time
            List<String> times = data.getValue().getTimes();
            String extractedTimes = times.stream()
                    .map(time -> time.contains(" ") ? time.substring(time.indexOf(" ") + 1) : time) // Take the part after the first space
                    .distinct()
                    .collect(Collectors.joining(", "));
            return new javafx.beans.property.SimpleStringProperty(extractedTimes);
        });

        // Classroom
        classroomColumn.setCellValueFactory(data -> {
            String classroom = data.getValue().getClassroom();
            if (classroom == null || classroom.trim().isEmpty()) {
                return new javafx.beans.property.SimpleStringProperty("N/A");
            } else {
                return new javafx.beans.property.SimpleStringProperty(classroom);
            }
        });

        // Capacity as "Enrolled/Capacity"
        capacityColumn.setCellValueFactory(data -> {
            int enrolled = data.getValue().getStudents().size();
            int capacity = data.getValue().getCapacity();
            String display = enrolled + " / " + capacity;
            if (capacity == 0) {
                return new javafx.beans.property.SimpleStringProperty("N/A");
            } else {
                return new javafx.beans.property.SimpleStringProperty(display);
            }
        });

        // Enrolled Students (show count instead of names)
        enrolledStudentsColumn.setCellValueFactory(data -> {
            int count = data.getValue().getStudents().size();
            return new javafx.beans.property.SimpleStringProperty(String.valueOf(count));
        });


        // Enrolled Students (show count instead of names)
        enrolledStudentsColumn.setCellValueFactory(data -> {
            int count = data.getValue().getStudents().size();
            return new javafx.beans.property.SimpleStringProperty(String.valueOf(count));
        });

        // TableView'e çift tıklama dinleyicisi ekleme
        timetableTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Course selectedCourse = timetableTable.getSelectionModel().getSelectedItem();
                if (selectedCourse != null) {
                    openCourseSchedulerController(selectedCourse);
                }
            }
        });



        // Populate table with current timetable courses
        timetableTable.setItems(FXCollections.observableArrayList(TimetableManager.getTimetable()));

        // Buttons and menu actions
        btnSearch.setOnAction(event -> showAlert("Search", "Search logic not attached yet."));
        btnAddCourse.setOnAction(event -> switchScene("addCourseLayout.fxml"));
        btnEnrollStudent.setOnAction(event -> showAlert("Enroll Student", "Enroll Student logic not attached yet."));
        btnAssignClassroom.setOnAction(event -> switchScene("assignClassroomLayout.fxml"));
        btnSwapClassroom.setOnAction(event -> switchScene("swapCourse.fxml"));

        menuImportCSV.setOnAction(event -> showAlert("Import CSV", "Import CSV not attached yet."));
        menuLoadTimetable.setOnAction(event -> showAlert("Load Timetable", "Load Timetable not attached yet."));
        menuSaveTimetable.setOnAction(event -> showAlert("Save Timetable", "Save Timetable not attached yet."));
        menuExportTimetable.setOnAction(event -> showAlert("Export Timetable", "Export Timetable not attached yet."));
        menuExit.setOnAction(event -> System.exit(0));
        menuUserManual.setOnAction(event -> showAlert("User Manual", "User Manual not attached yet."));
        menuAbout.setOnAction(event -> showAlert("About", "About not attached yet."));
        timetableTable.setItems(FXCollections.observableArrayList(TimetableManager.getTimetable()));

    }


    //TODO: Unique courseID aand StudentName
    public void refreshTable() {
        // Reload courses from the database to get updated assignments
        Database.reloadCourses();

        // Removing duplicate courses by courseID
        List<Course> original = Database.getAllCourses();
        LinkedHashMap<String, Course> uniqueCourses = new LinkedHashMap<>();
        for (Course c : original) {
            uniqueCourses.put(c.getCourseID(), c);
        }

        // For each unique course, remove duplicate students
        for (Course c : uniqueCourses.values()) {
            LinkedHashMap<String, Student> uniqueStudents = new LinkedHashMap<>();
            for (Student s : c.getStudents()) {
                uniqueStudents.put(s.getFullName(), s);
            }
            // Replace the course's student list with a new list of unique students
            c.setStudents(new ArrayList<>(uniqueStudents.values()));
        }

        // Update the table with the filtered, unique courses
        timetableTable.setItems(FXCollections.observableArrayList(uniqueCourses.values()));
    }





    public void loadTimetableFromCSV(File file) {
        if (file != null) {
            System.out.println("Loading timetable from: " + file.getAbsolutePath());
            // TODO: Add CSV parsing and data population logic
        } else {
            System.err.println("File is null, cannot load timetable.");
        }
    }


    public void openCourseSchedulerController(Course course) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/timetablemanager/courseSchedulerLayout.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));

            CourseSchedulerController controller = loader.getController();
            controller.setCourseData(course);
            stage.setTitle("Course Scheduler");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load Course Scheduler.");
        }
    }





    private void switchScene(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/timetablemanager/" + fxmlFile));
            Object newRoot = loader.load();

            if (!(newRoot instanceof javafx.scene.Parent)) {
                throw new IllegalArgumentException("Loaded root is not a valid JavaFX parent node");
            }

            Stage stage = (Stage) btnAddCourse.getScene().getWindow();
            Scene scene = stage.getScene();
            scene.setRoot((javafx.scene.Parent) newRoot);

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load: " + fxmlFile);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            showAlert("Error", "Invalid root type in FXML: " + fxmlFile);
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        try {
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/com/example/timetablemanager/icons/alert.png")));
        } catch (RuntimeException e) {
            System.err.println("Couldn't load icon");
            e.printStackTrace();
        }

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
