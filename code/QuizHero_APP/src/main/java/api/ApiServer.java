package api;
import com.google.gson.Gson;
import dao.*;
import exception.*;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.*;
import io.javalin.Javalin;
import io.javalin.plugin.json.JavalinJson;
import io.javalin.http.UploadedFile;
import model.File;
import org.pac4j.core.client.Client;
import org.pac4j.core.config.Config;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.http.client.indirect.FormClient;
import org.pac4j.javalin.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pac4j.SignInConfigFactory;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.plugin.rendering.template.TemplateUtil.model;


//NOTE: ApiServer is currently not used. it is refactored to Main, util and api

/**
 * ApiServer class is used for implementing the RESTful API
 * This class opens various routes, handles requests from front-end, and passes data to the Model
 * Calls DAOs to interact with PostgreSQL database
 * Sends data back to the client (front-end)
 * Controller Layer in MVC pattern
 * @author Ziming Chen, Nanxi Ye, Chenghao Sun
 * @version 1.0
 */
public final class ApiServer {
    public static boolean INITIALIZE_WITH_SAMPLE_DATA = true;
    public static int PORT = getHerokuAssignedPort();
    private static Javalin app;
    private static final Logger logger = LoggerFactory.getLogger(ApiServer.class);  // initialize logger
    private static Config pac4jConfig = new SignInConfigFactory().build(); //pc4j sign in config, used to initialize handlers and clients
    private static CallbackHandler callback = new CallbackHandler(pac4jConfig, null, false);
    private static SecurityHandler githubSecurityHandler = new SecurityHandler(pac4jConfig, "GitHubClient");

    /* This class is not meant to be instantiated! */
    private ApiServer() {}

    /**
     * This method is used to return the appropriate port number for the application
     * if application is deployed on Heroku, return the port assigned by Heroku
     * otherwise, return 7000 as default
     * @return port number for the application
     */
    private static int getHerokuAssignedPort() {
        String herokuPort = System.getenv("PORT");
        if (herokuPort != null) {
            return Integer.parseInt(herokuPort);
        }
        return 7000;
    }

    //log out on github
    private static LogoutHandler centralLogoutHandler(Config config) {
        LogoutHandler centralLogout = new LogoutHandler(config);
        centralLogout.defaultUrl = "http://localhost:7000/?defaulturlafterlogoutafteridp";
        centralLogout.logoutUrlPattern = "http://localhost:7000/.*";
        centralLogout.localLogout = false;
        centralLogout.centralLogout = true;
        centralLogout.destroySession = true;
        return centralLogout;
    }

    //log out local cache
    private static LogoutHandler localLogoutHandler(Config config) {
        LogoutHandler localLogout = new LogoutHandler(config, "/?defaulturlafterlogout");
        localLogout.destroySession = true;
        return localLogout;
    }

    //get profile method
    private static List<CommonProfile> getProfiles(Context ctx) {
        return new ProfileManager<CommonProfile>(new JavalinWebContext(ctx)).getAll(true);
    }


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
        InstructorDao instructorDao = DaoFactory.getInstructorDao();
        QuizDao quizDao = DaoFactory.getQuizDao();

        // add some sample data
        if (INITIALIZE_WITH_SAMPLE_DATA) {
            DaoUtil.addSampleUsers(instructorDao);
        }

        // Routing
        getHomepage();
        routing(fileDao, instructorDao, quizDao);

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
     * This method is used to open various routes
     * @param fileDao       DAO for file table
     * @param instructorDao DAO for instructor table
     * @param quizDao       DAO for quiz table
     */
    private static void routing(FileDao fileDao, InstructorDao instructorDao, QuizDao quizDao) {
        //sign in service
        getCallBack();
        postCallBack();
        getGithub();
        getLoginPage();
        getLocalLogout();
        getCentralLogout();
        postProfile();

        // fetch quiz statistics
        getQuizStatByFileId(quizDao);

        // update quiz statistics
        postQuiz(quizDao);
        postRecords(quizDao);

        // login and register
//        login(instructorDao);
        register(instructorDao);

        // upload, fetch file content and modify file status
        uploadFile(fileDao);
        saveFile(fileDao);
        fetchFile(fileDao);
        changeFilePermission(fileDao);
        checkFilePermission(fileDao);
        changeQuizPermission(fileDao);
        checkQuizPermission(fileDao);
        getFileListFromInstructor(instructorDao);
        deleteFile(fileDao);
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

    //new index page, see ui design
    private static void getIndexPage() {
        app = Javalin.create(config -> {
            config.addStaticFiles("/public");
            config.enableCorsForAllOrigins();
            config.addSinglePageRoot("/", "/public/index.html");
        });
    }

    //sign in api
    private static void getLoginPage() {
        app.get("/signin", ctx -> ctx.render("/templates/index.vm", model("profiles", getProfiles(ctx))));
    }

    //callback api
    private static void getCallBack() {
        app.get("/callback", callback);
    }
    private static void postCallBack() {
        app.post("/callback", callback);
    }

    //github api
    private static void getGithub() {
        app.before("/github", githubSecurityHandler);
        app.get("/github", ctx -> ctx.render("/templates/protectedPage.vm", model("profiles", getProfiles(ctx))));
    }

    private static void getLocalLogout() {
        app.get("/logout", localLogoutHandler(pac4jConfig));
    }

    private static void getCentralLogout() {
        app.get("/central-logout", centralLogoutHandler(pac4jConfig));
    }

    private static void postProfile() {
        app.before("/body", new SecurityHandler(pac4jConfig, "HeaderClient"));
        app.post("/body", ctx -> {
            logger.debug("Body: " + ctx.body());
            ctx.result("done: " + getProfiles(ctx));
        });
    }


    /**
     * This method is used to open the route for front-end to get the quiz statistics of
     * all the quizzes in a single file and send to the front-end
     * @param quizDao call quizDao to get data from quiz table
     */
    private static void getQuizStatByFileId(QuizDao quizDao) {
        // handle HTTP Get request to retrieve Quiz statistics
        app.get("/quizstat", ctx -> {
            try {
                String fileId = Objects.requireNonNull(ctx.queryParam("fileId"));
                System.out.println("File id: " + fileId);
                List<Quiz> quizzesByFileId = quizDao.getQuizStatByFileId(fileId);
                if (quizzesByFileId.isEmpty()) {
                    throw new ApiError("Unable to find quizzes", 500);
                }
                ctx.json(quizzesByFileId);
                ctx.status(200); // everything ok!
            } catch (NullPointerException ex) {
                throw new ApiError("bad request with missing argument: " + ex.getMessage(), 400);
            } catch (DaoException ex) {
                throw new ApiError(ex.getMessage(), 500);
            }
        });
    }

    /**
     * This method is used to open the route for front-end to post a new quiz question
     * pass data to the Quiz class
     * call quizDao to add a quiz question to the database
     * @param quizDao call quizDao to update quiz table
     */
    private static void postQuiz(QuizDao quizDao) {
        // quizzes are initialized once a markdown in quiz format is uploaded
        app.post("/quiz", ctx -> {
            Quiz quiz = ctx.bodyAsClass(Quiz.class);
            try {
                quizDao.add(quiz);
                ctx.json(quiz);
                ctx.contentType("application/json");
                ctx.status(201); // created successfully
            } catch (DaoException ex) {
                throw new ApiError("database error: " + ex.getMessage(), 500);
            } // quiz already exists, request forbidden
        });
    }

    /**
     * This method is used to open the route for front-end to post a record of a quiz question
     * pass data to the Record class
     * call quizDao to update the quiz table using the incoming record
     * @param quizDao call quizDao to update quiz table
     */
    private static void postRecords(QuizDao quizDao) {
        // student adds a record of a Quiz question through HTTP POST request
        app.post("/record", ctx -> {
            Record record = ctx.bodyAsClass(Record.class);
            try {
                quizDao.updateQuizStat(record);
                ctx.json(record);
                ctx.contentType("application/json");
                ctx.status(201); // created successfully
            } catch (DaoException ex) {
                throw new ApiError(ex.getMessage(), 500);
            } // quiz not found
        });
    }

//    /**
//     * This method is used to open the route for instructor to login
//     * call instructorDao to check user identity
//     * if login successful, send status code 201
//     * if wrong user information, send status code 403, request forbidden
//     * @param instructorDao dao for instructor table
//     */
//    private static void login(InstructorDao instructorDao) {
//        // instructor login action, return user including his/her id
//        app.post("/login", ctx -> {
//            String email = ctx.formParam("email");
//            String pswd = ctx.formParam("pswd");
//            try {
//                Instructor instructor = instructorDao.userLogin(email, pswd);
//                ctx.json(instructor);
//                ctx.contentType("application/json");
//                ctx.status(201); // created successfully
//            } catch (DaoException ex) {
//                throw new ApiError(ex.getMessage(), 500); // server internal error
//            } catch (LoginException ex) {
//                throw new ApiError(ex.getMessage(), 403); // request forbidden, user not found
//            }
//
//        });
//    }

    /**
     * This method is used to open the route for front-end to register a new instructor
     * pass data to the Instructor class
     * if register successful, send status code 201
     * if user already exists, send status code 403, request forbidden
     * @param instructorDao call instructorDao to update instructor table
     */
    private static void register(InstructorDao instructorDao) {
        // instructor login action, return user including his/her id
        app.post("/register", ctx -> {
            Instructor instructor = ctx.bodyAsClass(Instructor.class);
            try {
                instructorDao.registerUser(instructor);
                ctx.json(instructor);
                ctx.contentType("application/json");
                ctx.status(201); // created successfully
            } catch (DaoException ex) {
                throw new ApiError(ex.getMessage(), 500); // server internal error
            } catch (RegisterException ex) {
                throw new ApiError(ex.getMessage(), 403); // request forbidden, user already exists
            }
        });
    }

    /**
     * This method is used to open the route for instructor to upload a file
     * receive file stream and corresponding user id from front-end
     * pass data to the File class
     * @param fileDao call fileDao to update file table
     */
    private static void uploadFile(FileDao fileDao) {
        app.post("/upload", context -> {
            UploadedFile uploadedFile = context.uploadedFile("file"); // get file part
            try (InputStream inputStream = Objects.requireNonNull(uploadedFile).getContent()) {
                // fetch user id from form-data, require argument not null
                int userId = Integer.parseInt(Objects.requireNonNull(context.formParam("userId")));
                System.out.println("user id: " + userId);
                String fileName = uploadedFile.getFilename();
                System.out.println("file content received. File name: " + fileName);
//                File localFile = new File("upload/" + uploadedFile.getFilename());
//                FileUtils.copyInputStreamToFile(inputStream, localFile);
//                String url = localFile.getAbsolutePath();

                File file = new File (userId, fileName, inputStream); // generate File object
                fileDao.storeFile(file); // store file and update user-file info in database

                Map<String, Object> fileMap = new HashMap<>(); // return fileId and fileName to front-end
                fileMap.put("fileId", file.getFileId());
                fileMap.put("fileName", file.getFileName());
                context.json(fileMap);
                context.contentType("application/json");
                context.status(201);
            } catch (DaoException ex) {
                throw new ApiError("server error when uploading file: " + ex.getMessage(), 500);
            } catch (NullPointerException ex) {
                throw new ApiError("bad request with missing argument: " + ex.getMessage(), 400); // client bad request
            }
        });
    }

    /**
     * This method is used to open the route for front-end to fetch a file
     * get file stream from database, and send the stream to front-end
     * @param fileDao call fileDao to get data from file table
     */
    private static void fetchFile(FileDao fileDao) {
        app.get("/fetch", context -> {
            try {
                String fileId = Objects.requireNonNull(context.queryParam("fileId")); // get file id from form-data
                System.out.println("file id: " + fileId);
                InputStream in = fileDao.getFileContent(fileId);
                InputStream inputStream = new BufferedInputStream(in); /* BufferedInputStream is used to improve the performance of the inside InputStream */
                context.result(inputStream);
                System.out.println("Send file successfully.");
                context.status(200);
            } catch (DaoException ex) {
                throw new ApiError("server error when fetching file: " + ex.getMessage(), 500);
            } catch (NullPointerException ex) {
                throw new ApiError("bad request with missing argument: " + ex.getMessage(), 400);
            }
        });
    }

//    /**
//     *  Save (new) file api
//     */
//    private static void saveFile(FileDao fileDao) {
//        app.post("/save", context -> {
//            try {
//                File newFile;
//                int userId = Integer.parseInt(Objects.requireNonNull(context.formParam("userId"))); //get userId
//                String fileId = context.formParam("fileId"); //get fileId
//                String fileName = context.formParam("fileName"); //get file name
//                String fileContent = context.formParam("rawString"); //get file content as string
//                InputStream fileStream = new ByteArrayInputStream(fileContent.getBytes(StandardCharsets.UTF_8)); //convert string inputsStream
//                if (fileId != null) {
//                    fileDao.deleteFile(fileId);
//                    newFile = new File(userId, fileId, fileName, fileStream);
//                } else {
//                    newFile = new File(userId, fileName, fileStream);
//                }
//                fileDao.storeFile(newFile);
//                Map<String, Object> fileMap = new HashMap<>(); // return fileId and fileName to front-end
//                fileMap.put("fileId", newFile.getFileId());
//                fileMap.put("fileName", newFile.getFileName());
//                context.json(fileMap);
//                context.contentType("application/json");
//                context.status(201);
//            } catch (DaoException ex) {
//                throw new ApiError("server error when save file: " + ex.getMessage(), 500);
//            } catch (NullPointerException ex) {
//                throw new ApiError("bad request with missing argument: " + ex.getMessage(), 400); // client bad request
//            }
//        });
//    }

    /**
     *  Save (new) file api
     */
    private static void saveFile(FileDao fileDao) {
        app.post("/save", context -> {
            try {
                File newFile;
                int userId = Integer.parseInt(Objects.requireNonNull(context.formParam("userId"))); //get userId
                String fileId = context.formParam("fileId"); //get fileId
                String fileName = context.formParam("fileName"); //get file name
                String fileContent = context.formParam("rawString"); //get file content as string
                InputStream fileStream = new ByteArrayInputStream(fileContent.getBytes(StandardCharsets.UTF_8)); //convert string inputsStream
                assert fileId != null;
                if (!fileId.equals("null") && !fileId.equals("")) {
                    fileDao.updateFile(fileId, fileName, fileStream);
                } else {
                    newFile = new File(userId, fileName, fileStream);
                    fileDao.storeFile(newFile);
                    fileId = newFile.getFileId();
                    fileName = newFile.getFileName();
                }

                Map<String, Object> fileMap = new HashMap<>(); // return fileId and fileName to front-end
                fileMap.put("fileId", fileId);
                fileMap.put("fileName", fileName);
                context.json(fileMap);
                context.contentType("application/json");
                context.status(201);
            } catch (DaoException ex) {
                throw new ApiError("server error when save file: " + ex.getMessage(), 500);
            } catch (NullPointerException ex) {
                throw new ApiError("bad request with missing argument: " + ex.getMessage(), 400); // client bad request
            }
        });
    }

    /**
     * This method is used to open the route for front-end to change
     * the file permission of a single file
     * @param fileDao call fileDao to update file table
     */
    private static void changeFilePermission(FileDao fileDao) {
        app.post("/filepermission", ctx -> {
            try {
                String fileId = Objects.requireNonNull(ctx.formParam("fileId"));
                boolean permission = Boolean.parseBoolean(Objects.requireNonNull(ctx.formParam("permission")));
                System.out.println("fileId: " + fileId + " file permission: " + permission);
                fileDao.changeFilePermission(fileId, permission);
                ctx.status(201); // created successfully
            } catch (DaoException ex) {
                throw new ApiError(ex.getMessage(), 500); // server internal error
            } catch (NullPointerException ex) {
                throw new ApiError("bad request with missing argument: " + ex.getMessage(), 400);
            }
        });
    }

    /**
     * This method is used to open the route for front-end to check
     * the file permission of a single file
     * get file permission status from database and send to the front-end
     * @param fileDao call fileDao to get file permission status from file table
     */
    private static void checkFilePermission(FileDao fileDao) {
        app.get("/filepermission", ctx -> {
            try {
                String fileId = Objects.requireNonNull(ctx.queryParam("fileId"));
                System.out.println("fileId: " + fileId);
                Boolean filePermission = fileDao.checkFilePermission(fileId);
                ctx.result(String.valueOf(filePermission));
                ctx.status(200);
            } catch (DaoException ex) {
                throw new ApiError(ex.getMessage(), 500); // server internal error
            } catch (NullPointerException ex) {
                throw new ApiError("bad request with missing argument: " + ex.getMessage(), 400);
            }
        });
    }

    /**
     * This method is used to open the route for front-end to get the file history
     * get the list of files of uploaded by the instructor, and send to the front-end
     * @param instructorDao call instructorDao to fetch data
     */
    private static void getFileListFromInstructor(InstructorDao instructorDao) {
        app.get("/history", ctx -> {
            try {
                int userId = Integer.parseInt(Objects.requireNonNull(ctx.queryParam("instructorId")));
                List<File> fileHistory = instructorDao.getUserFileList(userId);
                System.out.println(fileHistory.size());
                ctx.json(fileHistory);
                ctx.status(200);
            } catch (DaoException ex) {
                throw new ApiError(ex.getMessage(), 500); // server internal error
            } catch (NullPointerException ex) {
                throw new ApiError("bad request with missing argument: " + ex.getMessage(), 400);
            }
        });
    }

    /**
     * This method is used to open the route for front-end to
     * change the quiz permission of a single file
     * @param fileDao call fileDao to update file table
     */
    private static void changeQuizPermission(FileDao fileDao) {
        app.post("/quizpermission", ctx -> {
            try {
                String fileId = Objects.requireNonNull(ctx.formParam("fileId"));
                boolean permission = Boolean.parseBoolean(Objects.requireNonNull(ctx.formParam("permission")));
                System.out.println("fileId: " + fileId + " quiz permission: " + permission);
                fileDao.changeQuizPermission(fileId, permission);
                ctx.status(201); // created successfully
            } catch (DaoException ex) {
                throw new ApiError(ex.getMessage(), 500); // server internal error
            } catch (NullPointerException ex) {
                throw new ApiError("bad request with missing argument: " + ex.getMessage(), 400);
            }
        });
    }

    /**
     * This method is used to open the route for front-end to check
     * the quiz permission of a single file
     * get quiz permission status from database and send to the front-end
     * @param fileDao call fileDao to get quiz permission status from file table
     */
    private static void checkQuizPermission(FileDao fileDao) {
        app.get("/quizpermission", ctx -> {
            try {
                String fileId = Objects.requireNonNull(ctx.queryParam("fileId"));
                System.out.println("fileId: " + fileId);
                Boolean quizPermission = fileDao.checkQuizPermission(fileId);
                ctx.result(String.valueOf(quizPermission));
                ctx.status(200);
            } catch (DaoException ex) {
                throw new ApiError(ex.getMessage(), 500); // server internal error
            } catch (NullPointerException ex) {
                throw new ApiError("bad request with missing argument: " + ex.getMessage(), 400);
            }
        });
    }

    /**
     * This method is used to open the route for front-end to delete a certain file
     * @param fileDao call fileDao to delete all the file data
     */
    private static void deleteFile(FileDao fileDao) {
        app.post("/deletefile", ctx -> {
            try {
                String fileId = Objects.requireNonNull(ctx.formParam("fileId"));
                System.out.println("fileId: " + fileId);
                fileDao.deleteFile(fileId);
                ctx.result("File deleted successfully");
                ctx.status(201);
            } catch (DaoException ex) {
                throw new ApiError(ex.getMessage(), 500); // server internal error
            } catch (NullPointerException ex) {
                throw new ApiError("bad request with missing argument: " + ex.getMessage(), 400);
            }
        });
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
