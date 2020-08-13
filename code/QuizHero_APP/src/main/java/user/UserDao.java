package user;

import exception.DaoException;
import file.File;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import java.util.*;

/**
 * InstructorDao interface defines methods related to the instructor table
 * e.g. user login, user register, get files of a user
 * @author QuizHero team @JHU spring20
 * @version 1.3
 */
public class UserDao {
    private Sql2o sql2o;
    public UserDao(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    /**
     * This method is used to check if the input email exists as a stored user information in database table
     * @param email String of the email address to be checked
     */
    public void checkUserExist(String email) {
        // email must be unique
        Integer id;
        try (Connection conn = sql2o.open()) {
            String sql = "SELECT userId FROM account Where email = :email;";
            id =  conn.createQuery(sql)
                    .addParameter("email", email)
                    .executeScalar(Integer.class);
            if (id != null) {
                throw new RegisterException("User already exists with the same email.");
            }
        } catch (Sql2oException ex) {
            throw new DaoException("Database error", ex);
        }
    }

    /**
     * This method is used to register a new user information and store user info in database table
     * @param user Instructor class containing the registered user information
     */
    public void registerUser(User user) {
        checkUserExist(user.getEmail());
        try (Connection conn = sql2o.open()) {
            String sql = "INSERT INTO account(name, email, pswd, repoId, githubId, salt) " +
                    "VALUES (:name, :email, :pswd, :repoId, :githubId, :salt);";
            int id = (int) conn.createQuery(sql, true)
                    .addParameter("name", user.getName())
                    .addParameter("email", user.getEmail())
                    .addParameter("pswd", user.getPswd())
                    .addParameter("repoId", user.getRepoId())
                    .addParameter("githubId", user.getGithubId())
                    .addParameter("salt", user.getSalt())
                    .executeUpdate()
                    .getKey(); // Returns the key this connection is associated with.

            user.setUserId(id);
            System.out.println("Register user successfully.");
        } catch (Sql2oException ex) {
            throw new DaoException("Unable to register the user.", ex);
        }
    }

    public User findPassword(String email) {
        User user;
        try (Connection conn = sql2o.open()) {
            String sql = "SELECT pswd FROM account Where email = :email;";
            user =  conn.createQuery(sql)
                    .addParameter("email", email)
                    .executeAndFetchFirst(User.class);
        } catch (Sql2oException ex) {
            throw new DaoException("Database error", ex);
        }
        if (user == null) {
            throw new LoginException("User authentication failure. Please input again.");
        }
        return user;
    }

    /**
     * This method is used to verify registered user information for given input
     * @param email String of user email address
     * @return an instance of user class with matching name and email fields and no other user fields
     */
    public User userLogin(String email) {
        User user;
        try (Connection conn = sql2o.open()) {
            String sql = "SELECT userId, name, email FROM account Where email = :email;";
            user =  conn.createQuery(sql)
                    .addParameter("email", email)
                    .executeAndFetchFirst(User.class);
        } catch (Sql2oException ex) {
            throw new DaoException("Database error", ex);
        }

        if (user == null) {
            throw new LoginException("User authentication failure. Please input again.");
        }

        return user; // return if find the user
    }

    /**
     * This method is used to verify user login from github
     * @param name String of user GitHub name
     * @param githubId String of user GitHub ID
     * @return an instance of Instructor class with matching email and password fields
     */
    public User githubLogin(String name, String githubId) {
        User user;
        try (Connection conn = sql2o.open()) {
            String sql = "SELECT userId, name, email, githubId FROM account Where githubId = :githubId;";
            user =  conn.createQuery(sql)
                    .addParameter("githubId", githubId)
                    .executeAndFetchFirst(User.class);
        } catch (Sql2oException ex) {
            throw new DaoException("Database error", ex);
        }

        if (user == null) {
            //github login not exists, register new user
            user = new User(name, githubId);
            try (Connection conn = sql2o.open()) {
                String sql = "INSERT INTO account(name, email, githubId) VALUES (:name, :email, :githubId);";
                int id = (int) conn.createQuery(sql, true)
                        .addParameter("name", user.getName())
                        .addParameter("email", user.getEmail())
                        .addParameter("githubId", user.getGithubId())
                        .executeUpdate()
                        .getKey(); // Returns the key this connection is associated with.

                user.setUserId(id);
                System.out.println("Register user successfully.");
            } catch (Sql2oException ex) {
                throw new DaoException("Unable to register the user.", ex);
            }
        }

        return user; // return if find the instructor
    }



    /**
     * This method is used to get the uploaded file of given user
     * @param userId unique id of user
     * @return List of File classes that were uploaded by the user of input userId
     */
    public List<File> getUserFileList(int userId) {
        String sql = "SELECT file.fileId, fileName, userId FROM file " +
                "JOIN account_file ON file.fileId = account_file.fileId " +
                "WHERE userId = :userId";
        try (Connection conn = sql2o.open()) {
            return conn.createQuery(sql)
                            .addParameter("userId", userId)
                            .executeAndFetch(File.class);
        } catch (Sql2oException ex) {
            throw new DaoException("Unable to find file history", ex);
        }
    }
}
