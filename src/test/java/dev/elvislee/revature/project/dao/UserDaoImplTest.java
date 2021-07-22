package dev.elvislee.revature.project.dao;

import dev.elvislee.revature.project.model.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class UserDaoImplTest {
    private final UserDaoImpl userDao = UserDaoImpl.getUserDaoInstance();

    @Test
    public void testAddExistedUser() {
        User user = new User("harry", "Harry", "Porter", "11111111");
        assertFalse(userDao.addUser(user));
    }

    @Test
    public void testAddNullUser() {
        User user = null;
        assertFalse(userDao.addUser(user));
    }

    @Test
    public void testAddShortUserIdUser() {
        User user = new User("harr", "Harry", "Porter", "11111111");
        assertFalse(userDao.addUser(user));
    }

    @Test
    public void testAddNewUser() {
        User user = new User("newUser2", "New", "User2", "11111111");
        assertTrue(userDao.addUser(user));
    }

    @Test
    public void testGetExistedUser() {
        assertEquals("harry", userDao.getUser("harry").getUserId());
    }

    @Test
    public void testGetNonUser() {
        assertEquals(User.NULL_USER, userDao.getUser("nonUser"));
    }

    @Test
    public void testGetNullUser() {
        assertEquals(User.NULL_USER, userDao.getUser(null));
    }

    @Test
    public void testUpdateUserLoginDateTime() {
        User user = new User("harry", "Harry", "Porter", "11111111");
        assertEquals(1, userDao.updateUserLoginDateTime(user, LocalDateTime.now()));
    }

    @Test
    public void testUpdateUserLoginDateTimeWithNullValue() {
        assertEquals(0, userDao.updateUserLoginDateTime(null, null));
    }

}
