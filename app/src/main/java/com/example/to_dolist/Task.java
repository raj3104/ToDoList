package com.example.to_dolist;

import java.util.Date;

public class Task {
    private String title;
    private Date dueDate;

    public Task(String title, Date dueDate) {
    }

    public Task(String title, String dueDate) {
    }

    public String getTitle() {
        return title;
    }

    public Date getDueDate() {
        return dueDate;
    }

    // Constructors, getters, setters
}
