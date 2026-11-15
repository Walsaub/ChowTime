package com.chowtime.models;

public class Employee {
    private int id;
    private String firstName;
    private String lastName;
    private String username;
    private String roleName;

    public Employee(int id, String firstName, String lastName, String username, String roleName){
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.roleName = roleName;
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUsername() {
        return username;
    }

    public String getRoleName() {
        return roleName;
    }

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }
}
