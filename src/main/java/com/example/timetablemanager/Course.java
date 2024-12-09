package com.example.timetablemanager;

import java.util.ArrayList;
import java.util.List;

public class Course {
    // Attributes
    private String courseId;
    private String courseName;
    private String description;
    private int capacity;
    private String classroom;
    private List<Student> students;

    // Constructor
    public Course(String courseId, String courseName, String description, int capacity, String classroom) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.description = description;
        this.capacity = capacity;
        this.classroom = classroom;
        this.students = new ArrayList<>();
    }

    // Methods
    public boolean addStudent(Student student) {
        if (students.size() < capacity && !students.contains(student)) {
            students.add(student);
            return true;
        }
        return false;
    }

    public void assignClassroom(String classroom) {
        this.classroom = classroom;
    }

    public String getSchedule() {
        return "Course: " + courseName + ", Classroom: " + classroom;
    }

    // Getters and Setters
    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    public List<Student> getStudents() {
        return students;
    }
}