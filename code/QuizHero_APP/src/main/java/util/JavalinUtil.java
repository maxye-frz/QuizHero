package util;

import com.google.gson.Gson;
import dao.*;
import exception.ApiError;
import io.javalin.Javalin;
import io.javalin.plugin.json.JavalinJson;

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

        // add some sample data
        if (INITIALIZE_WITH_SAMPLE_DATA) {
            DaoUtil.addSampleUsers(userDao);
        }

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
     * @param userDao       DAO for user table
     * @param quizDao       DAO for quiz table
     */
    private static void routing(FileDao fileDao, UserDao userDao, QuizDao quizDao) {
        //sign in service
        api.Pac4jApi.getCallBack();
        api.Pac4jApi.postCallBack();
        api.Pac4jApi.getLocalLogout();

        // login and register
        api.UserApi.register(userDao);
        api.UserApi.login(userDao);
        // login from github
        api.UserApi.githubLogin(userDao);
        // get file list from user
        api.UserApi.getFileListFromInstructor(userDao);
        // fetch quiz statistics
        api.QuizApi.getQuizStatByFileId(quizDao);
        // update quiz statistics
        api.QuizApi.postQuiz(quizDao);
        api.QuizApi.postRecords(quizDao);

        // upload, fetch file content and modify file status
        api.FileApi.uploadFile(fileDao);
        api.FileApi.saveFile(fileDao);
        api.FileApi.fetchFile(fileDao);
        api.FileApi.changeFilePermission(fileDao);
        api.FileApi.checkFilePermission(fileDao);
        api.FileApi.changeQuizPermission(fileDao);
        api.FileApi.checkQuizPermission(fileDao);
        api.FileApi.deleteFile(fileDao);
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
