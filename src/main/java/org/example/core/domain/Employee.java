package org.example.core.domain;

public class Employee {
    long id;
    String name;
    //String shift;
    boolean active;

    Employee(long id, String name, boolean active) {
        this.id = id;
        this.name = name;
        this.active = active;
    }

    // {id: 12345, name : Sinem, active : true}

    void employee_status_activate(){
        this.active = true;
    }

    void employee_status_deactivate(){
        this.active = false;
    }
}

