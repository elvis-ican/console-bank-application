package dev.elvislee.revature.project.model;

import java.time.LocalDateTime;

/**
 * The User class helps to create a POJO user object for storing
 * information of the user and pass between methods. All of the fields
 * of the user class are same as the corresponding user table in the
 * database.
 */
public class User {
    private String userId;
    private String firstName;
    private String lastName;
    private String password;
    private String status;
    private LocalDateTime lastLoginDateTime;
    public static final User NULL_USER = new User();

    public User() {
        super();
    }

    public User(String userId, String firstName, String lastName, String password) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.status = "active";
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getLastLoginDateTime() {
        return lastLoginDateTime;
    }

    public void setLastLoginDateTime(LocalDateTime lastLoginDateTime) {
        this.lastLoginDateTime = lastLoginDateTime;
    }

}
