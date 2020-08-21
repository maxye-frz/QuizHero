package user;

import file.File;
import file.FileDao;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import util.DaoFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class UserDaoTest {

    private UserDao userDao;
    private FileDao fileDao;
    private User JohnSmith;
    private User octoCat;

    @Before
    public void setup() throws URISyntaxException {
        DaoFactory.connectDatabase();
        userDao = DaoFactory.getUserDao();
        fileDao = DaoFactory.getFileDao();
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

    //userDao.getUserFileList()
    @Test
    public void emptyFileList() {
        List<File> emptyFileList = new ArrayList<>();
        List<File> resultList = userDao.getUserFileList(JohnSmith.getUserId());
        assertEquals(emptyFileList, resultList);
    }

    @Test
    public void nonEmptyFileList() {
        File file1 = new File(1, "testFileName", "owner", "repo", "path");
        File file2 = new File(1, "anotherTestFileName", "owner", "repo", "path");
        fileDao.storeFile(file1);
        fileDao.storeFile(file2);
        List<File> fileList = new ArrayList<>();
        fileList.add(file1);
        fileList.add(file2);
        List<File> resultList = userDao.getUserFileList(JohnSmith.getUserId());
        // the returned fileList does not contain all file information, can only be compared as the following fields:
        assertEquals(fileList.get(0).getFileId(), resultList.get(0).getFileId());
        assertEquals(fileList.get(1).getFileId(), resultList.get(1).getFileId());
        assertEquals(fileList.get(0).getFileName(), resultList.get(0).getFileName());
        assertEquals(fileList.get(1).getFileName(), resultList.get(1).getFileName());
    }
}
