package com.biocoda.miov10.model;

public class User {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String uiPreference;

    // full constructor
    public User(int id, String firstName, String lastName, String email, String password, String uiPreference) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.uiPreference = uiPreference;
    }

    // empty constructor
    public User() {
    }

    // getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUiPreference() {
        return uiPreference;
    }

    public void setUiPreference(String uiPreference) {
        this.uiPreference = uiPreference;
    }
}
