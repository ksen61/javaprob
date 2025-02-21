package com.example.treker;

public class Habit {
    private String id; // Firestore ID
    private String name;
    private String iconResId;
    private boolean isCompleted;

    public Habit() {
        // Пустой конструктор для Firestore
    }

    public Habit(String id, String name, String iconResId, boolean isCompleted) {
        this.id = id;
        this.name = name;
        this.iconResId = iconResId;
        this.isCompleted = isCompleted;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getIconResId() {
        return iconResId;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}
