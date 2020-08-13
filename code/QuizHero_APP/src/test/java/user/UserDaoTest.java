package user;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import util.DaoFactory;

import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;

public class UserDaoTest {

    private UserDao userDao;
    private User JohnSmith;
    private User octoCat;

    @Before
    public void setup() throws URISyntaxException {
        DaoFactory.connectDatabase();
        userDao = DaoFactory.getUserDao();
        //add instructor to empty instructor table; this instructor has instructorId of 1
        JohnSmith = new User("John Smith",
                "jsmith@quizhero.com",
                "exampleHashedPassword",
                "example salt");
        octoCat = new User("octo cat", "exampleGithubId");
        userDao.registerUser(JohnSmith);
        userDao.registerUser(octoCat);
    }

    @After
    public void clearTable() {
        DaoFactory.clearDatabase();
    }


    //userDao.checkUserExist()
    @Test
    public void checkUserExistNoException() {
        userDao.checkUserExist("nonRegisteredEmail@quizhero.com");
    }

    @Test (expected = RegisterException.class)
    public void checkUserExistReturnRegisterException() {
        userDao.checkUserExist("jsmith@quizhero.com");
    }

    //userDao.registerUser()
    @Test
    public void registerUser() {
        User newUser = new User("new user",
                "newUser@quizhero.com",
                "hashed password",
                "salt");
        User githubUser = new User("newGit", "123456");
        userDao.registerUser(newUser);
        userDao.registerUser(githubUser);
        Integer newUserId = 3; //newUser is the third user registered
        Integer newGitId = 4; //githubUser is the fourth registered
        assertEquals(newUserId, newUser.getUserId());
        assertEquals(newGitId, githubUser.getUserId());
    }

    @Test (expected = RegisterException.class)
    public void registerUserReturnRegisterException() {
        User newUser = new User("new user",
                "jsmith@quizhero.com",
                "hashed password",
                "salt");
        userDao.registerUser(newUser);
    }

    //userDao.findPassword()
    @Test
    public void findPassword() {
        User resultUser = userDao.findPassword(JohnSmith.getEmail());
        assertEquals(JohnSmith.getPswd(), resultUser.getPswd());
    }

    @Test (expected = LoginException.class)
    public void findPasswordReturnLoginException() {
        userDao.findPassword("nonexistemail@quizhero.com");
    }

    //userDao.userLogin()
    @Test
    public void userLogin() {
        User resultUser = userDao.userLogin(JohnSmith.getEmail());
        assertEquals(JohnSmith.getName(), resultUser.getName());
        assertEquals(JohnSmith.getEmail(), resultUser.getEmail());
    }

    @Test (expected = LoginException.class)
    public void userLoginReturnLoginException() {
        userDao.userLogin("nonexistemail@quizhero.com");
    }

    //userDao.githubLogin()

}
