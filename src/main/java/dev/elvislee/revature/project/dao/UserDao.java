package dev.elvislee.revature.project.dao;

import dev.elvislee.revature.project.model.User;

/**
 * The UserDao interface declares some basic methods for manipulating
 * User data in the database.
 */
public interface UserDao {
    public boolean addUser(User user);
    public User getUser(String userId);
}
