package dev.elvislee.revature.project.dao;

import dev.elvislee.revature.project.model.User;
import dev.elvislee.revature.project.util.ConnectionUtil;
import dev.elvislee.revature.project.util.Log4j;
import org.apache.log4j.Logger;

import java.sql.*;
import java.time.LocalDateTime;

/**
 * The UserDaoImpl class provides various methods
 * for saving, retrieving and updating user related data
 * into or from the database.
 */
public class UserDaoImpl implements UserDao{
    private static UserDaoImpl userDao;
    private static Logger logger = Log4j.getLogger();

    private UserDaoImpl() {
    }

    /**
     * The getUserDaoInstance method ensure a singleton
     * userDao is returned to the methode invoked it.
     */
    public static UserDaoImpl getUserDaoInstance() {
        if (userDao == null) {
            userDao = new UserDaoImpl();
        }
        return userDao;
    }

    /**
     * The addUser method takes a user object,
     * the data of the user object will be extracted and add into
     * the user table in the database.
     *
     * @param   user
     * @return  1 for success execution, 0 for unsuccessful deposit
     */
    public boolean addUser(User user) {
        if (user != null && user.getUserId().length() > 4) {
            if (getUser(user.getUserId()) != User.NULL_USER) {
                return false;
            } else {
                int count = 0;
                String sql = "insert into bank_user values(?,?,?,?,?,?)";
                try (Connection conn = ConnectionUtil.getConnection();
                    PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, user.getUserId());
                    ps.setString(2, user.getFirstName());
                    ps.setString(3, user.getLastName());
                    ps.setString(4, user.getPassword());
                    ps.setString(5, user.getStatus());
                    ps.setObject(6, user.getLastLoginDateTime());
                    count = ps.executeUpdate();
                } catch (SQLException e) {
                    logger.error(e);
                    e.printStackTrace();
                }
                if (count > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * The getUser method takes a userId, retrieving the information
     * of the user, create and return the user object.
     *
     * @param   userId
     * @return
     */
    public User getUser(String userId) {
        User user = new User();;
        if (userId != null) {
            String sql = "select * from bank_user where userid=?";
            try (Connection conn = ConnectionUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, userId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    user.setUserId(userId);
                    user.setFirstName(rs.getString("first_name"));
                    user.setLastName(rs.getString("last_name"));
                    user.setPassword(rs.getString("pin"));
                    user.setStatus(rs.getString("status"));
                    user.setLastLoginDateTime(rs.getObject("last_login", LocalDateTime.class));
                } else {
                    return User.NULL_USER;
                }
            } catch (SQLException e) {
                logger.error(e);
                e.printStackTrace();
            }
            return user;
        } else {
            return User.NULL_USER;
        }
    }

    /**
     * The updateUserLoginDateTime method takes a user object, a LocalDataTime value
     * of the current login, the value will be updated to the record of the user in
     * the band_user table of the database.
     *
     * @param   user
     * @param   dateTime
     * @return
     */
    public int updateUserLoginDateTime(User user, LocalDateTime dateTime) {
        int count = 0;
        if (user != null || dateTime != null) {
            String sql = "update bank_user set last_login=? where userid=?";
            try (Connection conn = ConnectionUtil.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setObject(1, dateTime);
                ps.setString(2, user.getUserId());
                count = ps.executeUpdate();
            } catch (SQLException e) {
                logger.error(e);
                e.printStackTrace();
            }
        }
        return count;
    }

}
