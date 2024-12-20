package com.example.timetablemanager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import static com.example.timetablemanager.Database.*;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class swapCourse {
    @FXML
    private Label SwapClasroomLabel, EnrollmendLabel, ClasssroomLabel, EnrollmendLabel2, ClasssroomLabel2, CapacityLabel2, CapacityLabel;

    @FXML
    private Button btnSave,btnBack;

    @FXML
    private ListView EnrolledListView, ClassroomListView, EnrolledListView2, ClassroomListView2, CapacityListView, CapacityListView2;

    @FXML
    private ComboBox SelectCourseCombo, SelectCourse2;

    private String courseCapacity;

    private String classroomCapacity;

    private String classroomCapacity2;

    private String selectedClass;

    private String selectedClass2;

    private String selectedCourse;

    private String selectedCourse2;

    private int numberOfStudents;

    private int numberOfStudents2;

    private List<Course> allCourses;

    private ttManagerController mainController;  // Declare a reference to the main controller

    public void setMainController(ttManagerController mainController) {
        this.mainController = mainController;
    }

    @FXML
    public void initialize() {

        allCourses = Database.getAllCourses();

        SelectCourseCombo.setItems(FXCollections.observableArrayList(Database.getAllCourseNames()));
        SelectCourseCombo.setOnAction(event -> {
             selectedCourse = SelectCourseCombo.getValue().toString();

             //Adding enrolled studend number
            List<String> enrolledStudents = Database.getStudentsEnrolledInCourse(selectedCourse);
             numberOfStudents = enrolledStudents.size();
            EnrolledListView.setItems(FXCollections.observableArrayList(numberOfStudents));

            //Adding Classroom
            Course selectedCourseObject = allCourses.stream()
                    .filter(course -> course.getCourseID().equals(selectedCourse))
                    .findFirst().orElse(null);

            if (selectedCourseObject == null) {
                showAlert("Error", "Invalid selection. Please try again.");
                return;
            }
            String classroom1 = selectedCourseObject.getClassroom();
            System.out.println(classroom1);
            ClassroomListView.setItems(FXCollections.observableArrayList(classroom1));

            //Adding classroom capacity
            selectedClass=classroom1;
            List<Integer> capacities = getAllClassroomCapacities(selectedClass);

            if (capacities.isEmpty()) {
                classroomCapacity = "";
            } else {
                classroomCapacity = capacities.stream().map(String::valueOf).collect(Collectors.joining(", "));
            }
            CapacityListView.setItems(FXCollections.observableArrayList(classroomCapacity));

        });

        SelectCourse2.setItems(FXCollections.observableArrayList(Database.getAllCourseNames()));
        SelectCourse2.setOnAction(event -> {
            selectedCourse2 = SelectCourse2.getValue().toString(); // Seçilen değeri alır

            //Adding enrolled studend number
            List<String> enrolledStudents = Database.getStudentsEnrolledInCourse(selectedCourse2);
            numberOfStudents2 = enrolledStudents.size();
            EnrolledListView2.setItems(FXCollections.observableArrayList(numberOfStudents2));

            //Adding Classroom
            Course selectedCourseObject = allCourses.stream()
                    .filter(course -> course.getCourseID().equals(selectedCourse2))
                    .findFirst().orElse(null);

            if (selectedCourseObject == null) {
                showAlert("Error", "Invalid selection. Please try again.");
                return;
            }
            String classroom1 = selectedCourseObject.getClassroom();
            ClassroomListView2.setItems(FXCollections.observableArrayList(classroom1));

            //Adding classroom capacity
            selectedClass2=classroom1;
            List<Integer> capacities = getAllClassroomCapacities(selectedClass2);

            if (capacities.isEmpty()) {
                classroomCapacity2 = "";
            } else {
                classroomCapacity2 = capacities.stream().map(String::valueOf).collect(Collectors.joining(", "));
            }
            CapacityListView2.setItems(FXCollections.observableArrayList(classroomCapacity2));
        });





        btnSave.setOnAction(event -> {
            if(classroomCapacity==null||classroomCapacity2==null) {
                showAlert("Error", "Invalid selection. Please try again.");
            } else if (classroomCapacity.equals("")||classroomCapacity2.equals("")) {
                showAlert("Error", "Invalid selection. Please try again.");
            }
            else if(Integer.parseInt(classroomCapacity)<numberOfStudents2 || Integer.parseInt(classroomCapacity2)<numberOfStudents) {
                showAlert("Error", "The selected classroom does not meet the student capacity for the course.");
            } else {
                Course selectedCourseObject = allCourses.stream()
                        .filter(course -> course.getCourseID().equals(selectedCourse))
                        .findFirst().orElse(null);

                if (selectedCourseObject == null) {
                    showAlert("Error", "Invalid selection. Please try again.");
                    return;
                }
                String classroom1 =selectedCourseObject.getClassroom();

                Course selectedCourseObject2 = allCourses.stream()
                        .filter(course -> course.getCourseID().equals(selectedCourse2))
                        .findFirst().orElse(null);

                if (selectedCourseObject2 == null) {
                    showAlert("Error", "Invalid selection. Please try again.");
                    return;
                }
                String classroom2 =selectedCourseObject2.getClassroom();

                selectedCourseObject.setClassroom(classroom2);
                selectedCourseObject2.setClassroom(classroom1);

                Database.changeClassroom(selectedCourse, classroom2);
                Database.changeClassroom(selectedCourse2, classroom1);

                showAlert("Success", "Courses' classrooms have been swapped successfully!");

                ClassroomListView.setItems(FXCollections.observableArrayList(selectedClass2));
                ClassroomListView2.setItems(FXCollections.observableArrayList(selectedClass));

            }

        });

        refreshTimetableView();
        btnBack.setOnAction(event -> switchScene("mainLayout.fxml"));



    }
    private void refreshTimetableView() {
        if (mainController != null) {
            mainController.refreshTable(); // Call the refreshTable method in the main controller
        }
    }



    private void switchScene(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/timetablemanager/" + fxmlFile));
            Object newRoot = loader.load();

            if (!(newRoot instanceof javafx.scene.Parent)) {
                throw new IllegalArgumentException("Loaded root is not a valid JavaFX parent node.");
            }

            Stage stage = (Stage) btnBack.getScene().getWindow();
            Scene scene = stage.getScene();
            scene.setRoot((javafx.scene.Parent) newRoot);

            boolean wasFullScreen = stage.isFullScreen();
            stage.setFullScreen(wasFullScreen);

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load the scene: " + fxmlFile);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            showAlert("Error", "Invalid root type in FXML: " + fxmlFile);
        }
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
