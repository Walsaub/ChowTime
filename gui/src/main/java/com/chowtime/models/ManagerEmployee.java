package com.chowtime.models;

public class ManagerEmployee {
    private final int id;
    private final String firstName;
    private final String lastName;
    private final String username;
    private final String roleName;
    private final int roleID;

    public ManagerEmployee(int id, String firstName, String lastName, String username, String roleName, int roleID) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.roleName = roleName;
        this.roleID = roleID;
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

    public int getRoleID() {
        return roleID;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
