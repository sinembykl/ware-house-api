package org.example.core.domain;

public class Employee {
    private Long id;
    private String name;
    private boolean active;
    private String shift;


    public Employee( String name, boolean active, String shift) {
        this.name = name;
        this.active = active;
        this.shift = shift;
    }
    public Employee(){

    }

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}
    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
    public boolean isActive() {return active;}
    public void activate() {this.active = true;}
    public void deactivate() {this.active = false;}
    public String getShift() {return shift;}
    public void setShift(String shift) {this.shift = shift;}

}

