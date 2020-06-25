package user;

import exception.ApiError;
import exception.DaoException;
import model.File;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.javalin.JavalinWebContext;

import java.util.List;
import java.util.Objects;

import static util.JavalinUtil.app;
import static util.Pac4jUtil.githubSecurityHandler;

public class UserApi {

    /**
     * This method is used to open the route for front-end to register a new instructor
     * pass data to the Instructor class
     * if register successful, send status code 201
     * if user already exists, send status code 403, request forbidden
     * @param userDao call instructorDao to update instructor table
     */
    public static void register(UserDao userDao) {
        // instructor login action, return user including his/her id
        app.post("/register", ctx -> {
            User user = ctx.bodyAsClass(User.class);
            try {
                userDao.registerUser(user);
                ctx.json(user);
                ctx.contentType("application/json");
                ctx.status(201); // created successfully
            } catch (DaoException ex) {
                throw new ApiError(ex.getMessage(), 500); // server internal error
            } catch (RegisterException ex) {
                throw new ApiError(ex.getMessage(), 403); // request forbidden, user already exists
            }
        });
    }

    public static void emailForPassword(UserDao userDao) {
        app.post("/emailForPassword", ctx -> {
           String email = ctx.formParam("email");
           try {
                User user = userDao.findPassword(email);
                ctx.json(user);
                ctx.contentType("application/json");
                ctx.status(201); // created successfully
           } catch (DaoException ex) {
                throw new ApiError(ex.getMessage(), 500); // server internal error
            } catch (LoginException ex) {
                throw new ApiError(ex.getMessage(), 403); // request forbidden, user not found
            }
        });
    }


//    /**
//     * This method is used to open the route for instructor to login
//     * call instructorDao to check user identity
//     * if login successful, send status code 201
//     * if wrong user information, send status code 403, request forbidden
//     * @param userDao dao for instructor table
//     */
//    public static void login(UserDao userDao) {
//        // instructor login action, return user including his/her id
//        app.post("/login", ctx -> {
//            String email = ctx.formParam("email");
//            String pswd = ctx.formParam("pswd");
//            try {
//                User user = userDao.userLogin(email, pswd);
//                ctx.json(user);
//                ctx.contentType("application/json");
//                ctx.status(201); // created successfully
//            } catch (DaoException ex) {
//                throw new ApiError(ex.getMessage(), 500); // server internal error
//            } catch (LoginException ex) {
//                throw new ApiError(ex.getMessage(), 403); // request forbidden, user not found
//            }
//        });
//    }

    public static void githubLogin(UserDao userDao) {
        app.before("/github", githubSecurityHandler);
        app.get("/github", ctx -> {
            //get profile id and name
            List<CommonProfile> profile = new ProfileManager<CommonProfile>(new JavalinWebContext(ctx)).getAll(true);
            CommonProfile userProfile = profile.get(0);
            String name = userProfile.getUsername();
            String githubId = userProfile.getId();
            System.out.println(name);
            System.out.println(githubId);
            try {
                User user = userDao.githubLogin(name, githubId);
                ctx.json(user); //json text of user model is printed on web page
                System.out.println(user);
                ctx.contentType("application/json");
                ctx.status(200);
                ctx.redirect("http://localhost:3000/register");
            } catch (DaoException ex) {
                throw new ApiError(ex.getMessage(), 500); // server internal error
            } catch (LoginException ex) {
                throw new ApiError(ex.getMessage(), 403); // request forbidden, user not found
            }
        });
    }

    /**
     * This method is used to open the route for front-end to get the file history
     * get the list of files of uploaded by the instructor, and send to the front-end
     * @param userDao call instructorDao to fetch data
     */
    public static void getFileListFromInstructor(UserDao userDao) {
        app.get("/history", ctx -> {
            try {
                int userId = Integer.parseInt(Objects.requireNonNull(ctx.queryParam("userId")));
                List<File> fileHistory = userDao.getUserFileList(userId);
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
}
