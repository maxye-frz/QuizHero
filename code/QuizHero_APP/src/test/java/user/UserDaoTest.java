package user;

import util.DaoFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;

public class UserDaoTest {
    private UserDao userDao;

    @Before
    public void setup() throws URISyntaxException {
        DaoFactory.connectDatabase();
        userDao = DaoFactory.getUserDao();
    }

    @After
    public void clearTable() {
        DaoFactory.clearDatabase();
    }

    @Test
    public void checkUserExist() {
        User newUser = new User("new user", "new@user.com", "hashedCode", "newSalt");
        userDao.checkUserExist("new@user.com");
    }

    @Test (expected = RegisterException.class)
    public void checkUserExistException() {
        User newUser = new User("new user", "new@user.com", "hashedCode", "newSalt");
        userDao.registerUser(newUser);
        userDao.checkUserExist("new@user.com");
    }

    @Test
    public void registerUserUpdateUserId() {
        User newUser = new User("new user", "new@user.com", "hashedCode", "newSalt");
        User anotherUser = new User("another user", "another@user.com", "hashedCode", "salt");
        assertEquals(null, newUser.getUserId());
        assertEquals(null, anotherUser.getUserId());
        userDao.registerUser(newUser);
        userDao.registerUser(anotherUser);
        Integer userIdOne = 1;
        Integer userIdTwo = 2;
        assertEquals(userIdOne, newUser.getUserId());
        assertEquals(userIdTwo, anotherUser.getUserId());
    }
}
