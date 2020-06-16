import dao.DaoFactory;
import util.JavalinUtil;

import java.net.URISyntaxException;
import java.nio.file.Paths;

/**
 * Main class is used to start the application
 * modify DaoFactory.DROP_TABLES_IF_EXIST or ApiServer.INITIALIZE_WITH_SAMPLE_DATA globally before starting;
 * @author Ziming Chen, Nanxi Ye, Chenghao Sun
 * @version 1.1
 */
public class Main {
    public static void main(String[] args) throws URISyntaxException {
        DaoFactory.PATH_TO_DATABASE_FILE = Paths.get("src", "main", "resources").toFile().
                getAbsolutePath()
                + "/db/Store.db";
        DaoFactory.DROP_TABLES_IF_EXIST = false;
        JavalinUtil.INITIALIZE_WITH_SAMPLE_DATA = false;
        JavalinUtil.start();
    }
}
