package dao;

import exception.DaoException;
import exception.LoginException;
import exception.RegisterException;
import model.File;
import model.User;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import java.util.*;

public class Sql2oUserDao implements UserDao {
    private Sql2o sql2o;

    public Sql2oUserDao(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public User userLogin(String email, String pswd) {
        User user;
        try (Connection conn = sql2o.open()) {
            String sql = "SELECT userId, name, email, githubId FROM account Where email = :email AND pswd = :pswd;";
            user =  conn.createQuery(sql)
                    .addParameter("email", email)
                    .addParameter("pswd", pswd)
                    .executeAndFetchFirst(User.class);
        } catch (Sql2oException ex) {
            throw new DaoException("Database error", ex);
        }

        if (user == null) {
            throw new LoginException("User authentication failure. Please input again.");
        }

        return user; // return if find the instructor
    }

    @Override
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
                String sql = "INSERT INTO account(name, githubId) VALUES (:name, :githubId);";
                int id = (int) conn.createQuery(sql, true)
                        .addParameter("name", user.getName())
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

    @Override
    public void registerUser(User user) {
        checkUserExist(user.getEmail());
        System.out.println("user not exists, register permit.");
        try (Connection conn = sql2o.open()) {
            String sql = "INSERT INTO account(name, email, pswd) VALUES (:name, :email, :pswd);";
            int id = (int) conn.createQuery(sql, true)
                    .addParameter("name", user.getName())
                    .addParameter("email", user.getEmail())
                    .addParameter("pswd", user.getPswd())
                    .executeUpdate()
                    .getKey(); // Returns the key this connection is associated with.

            user.setUserId(id);
            System.out.println("Register user successfully.");
        } catch (Sql2oException ex) {
            throw new DaoException("Unable to register the user.", ex);
        }
    }

    @Override
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

    @Override
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
