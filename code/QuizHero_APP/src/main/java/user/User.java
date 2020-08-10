package user;

import java.util.Objects;
import java.util.UUID;


/**
 * Instructor class is the data model of an Instructor. Each object of Instructor class
 * stores relevant data of a single instructor such as instructor id
 * instructor's name, instructor's email and password
 *
 * @author Ziming Chen, Nanxi Ye, Chenghao Sun
 * @version 1.0
 */
public class User {
    private Integer userId; // unique id of user
    private String name; // name of user
    private String email; // email of user
    private String pswd; // password of user
    private String githubId; // githubId of user
    private String salt; // salt of user

    /**
     * This method is the constructor of the class when a user login without github
     * It is equivalent to using a pre-set github account to create a private repo under organization
     *
     * @param name name of the instructor
     * @param email email of the instructor
     * @param pswd password of the instructor
     */
    public User(String name, String email, String pswd, String salt) {
        this.name = name;
        this.email = email;
        this.pswd = pswd;
        this.salt = salt;
    }

    /**
     * This method is the constructor of the class when a user login with github
     *
     * @param name name of the instructor
     * @param githubId githubId of the instructor
     */
    public User(String name, String email, String githubId) {
        this.name = name;
        this.email = email;
        this.githubId = githubId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPswd() {
        return pswd;
    }

    public void setPswd(String pswd) {
        this.pswd = pswd;
    }

    public String getGithubId() {
        return githubId;
    }

    public void setGithubId(String githubId) {
        this.githubId = githubId;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userId, user.userId) &&
                Objects.equals(name, user.name) &&
                Objects.equals(email, user.email) &&
                Objects.equals(pswd, user.pswd) &&
                Objects.equals(githubId, user.githubId) &&
                Objects.equals(salt, user.salt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, name, email, pswd, githubId, salt);
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", pswd='" + pswd + '\'' +
                ", githubId='" + githubId + '\'' +
                ", salt='" + salt + '\'' +
                '}';
    }
}
