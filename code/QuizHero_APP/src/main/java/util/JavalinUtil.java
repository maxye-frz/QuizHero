package util;

import com.google.gson.Gson;
import exception.ApiError;
import file.FileApi;
import file.FileDao;
import io.javalin.Javalin;
import io.javalin.plugin.json.JavalinJson;
import quiz.QuizApi;
import quiz.QuizDao;
import user.UserApi;
import user.UserDao;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class JavalinUtil {
    public static boolean INITIALIZE_WITH_SAMPLE_DATA = true;
    public static int PORT = HerokuUtil.getHerokuAssignedPort();
    public static Javalin app;
    private JavalinUtil() {}

    /**
     * This method is used to start application server
     * obtain various DAOs from DaoFactory including fileDao, instructorDao, quizDao
     * finally handle exceptions
     * @exception URISyntaxException exception occurs if a string could not be parsed as a URI reference
     */
    public static void start() throws URISyntaxException {
        // instantiate Sql2o and get DAOs
        DaoFactory.connectDatabase();
        FileDao fileDao = DaoFactory.getFileDao();
        UserDao userDao = DaoFactory.getUserDao();
        QuizDao quizDao = DaoFactory.getQuizDao();

//        // add sample users
//        if (INITIALIZE_WITH_SAMPLE_DATA) {
//            DaoUtil.addSampleUsers(userDao);
//        }

        // Routing
        getHomepage();
        routing(fileDao, userDao, quizDao);

        // start application server
        startJavalin();

        // Handle exceptions
        app.exception(ApiError.class, (exception, ctx) -> {
            // ApiError err = (ApiError) exception;
            Map<String, Object> jsonMap = new HashMap<>();
            jsonMap.put("status", exception.getStatus());
            jsonMap.put("errorMessage", exception.getMessage());
            ctx.status(exception.getStatus());
            ctx.json(jsonMap);
        });
    }

    public static void stop() {
        app.stop();
    }

    /**
     * This method is used to get the homepage from resources/public
     * Catch-all route for the single-page application; The ReactJS application
     */
    private static void getHomepage() {
        app = Javalin.create(config -> {
            config.addStaticFiles("/public");
            config.enableCorsForAllOrigins();
            config.addSinglePageRoot("/", "/public/index.html");
        });
    }

    /**
     * This method is used to open various routes
     * @param fileDao       DAO for file table
     * @param userDao       DAO for account (user) table
     * @param quizDao       DAO for quiz table
     */
    private static void routing(FileDao fileDao, UserDao userDao, QuizDao quizDao) {

        // login and register
        UserApi.register(userDao);
        UserApi.login(userDao);
        UserApi.emailForPassword(userDao);
        // login from github
        UserApi.githubLogin();
        UserApi.githubCallback(userDao);
        // get file list from user
        UserApi.getFileListFromInstructor(userDao);

        // upload, fetch file content and modify file status
        FileApi.uploadFile(fileDao);
        FileApi.saveFile(fileDao);
//        FileApi.testDelete(fileDao);
        FileApi.push(fileDao);
        FileApi.fetchFile(fileDao);
        FileApi.studentFetchFile(fileDao);
        FileApi.changeFilePermission(fileDao);
        FileApi.checkFilePermission(fileDao);
        FileApi.changeQuizPermission(fileDao);
        FileApi.checkQuizPermission(fileDao);
        FileApi.deleteFile(fileDao);
        FileApi.clone(fileDao);
        FileApi.listRepo();
        FileApi.listContent();
        FileApi.readCSS(fileDao);
        FileApi.saveCSS(fileDao);
        FileApi.uploadCSS(fileDao);

        // fetch quiz statistics
        QuizApi.getQuizStatByFileId(quizDao);
        // update quiz statistics
        QuizApi.postQuiz(quizDao);
        QuizApi.postRecords(quizDao);
    }

    /**
     * This method is used to create gson mapping and start Javalin
     */
    private static void startJavalin() {
        Gson gson = new Gson();
        JavalinJson.setFromJsonMapper(gson::fromJson);
        JavalinJson.setToJsonMapper(gson::toJson);
        app.start(PORT);
    }
}
