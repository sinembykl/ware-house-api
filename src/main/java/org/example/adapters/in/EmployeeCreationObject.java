package org.example.adapters.in;

public class EmployeeCreationObject {
    public String name;
    public boolean active;
    public String shift;

    // Default constructor required for JSON deserialization
    public EmployeeCreationObject() {
    }

    public EmployeeCreationObject(String name, boolean active, String shift) {
        this.name = name;
        this.active = active;
        this.shift = shift;
    }

    // Getters and setters (required for JSON mapping)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }
}