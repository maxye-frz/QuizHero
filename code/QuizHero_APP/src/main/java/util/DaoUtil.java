package util;
import user.User;
import user.UserDao;

/**
 * DaoUtil class contains methods that add some initial sample data to the database
 * @author Ziming Chen, Nanxi Ye, Chenghao Sun
 * @version 1.0
 */
public final class DaoUtil {

    /* This class is not mean to be instantiated! */
    private DaoUtil() {}


    /**
     * Add some sample instructors to the instructor table
     * @param userDao dao for instructor table
     */
    public static void addSampleUsers(UserDao userDao) {
        userDao.registerUser(new User("Allen", "zchen85@jhu.edu", "9999", "salt"));
        userDao.registerUser(new User("Bob Wang", "bob@jhu.edu", "8888", "salt"));
        userDao.registerUser(new User("Richard", "richard@jhu.edu", "7777", "salt"));
    }

}
