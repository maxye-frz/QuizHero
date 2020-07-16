package user;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import exception.ApiError;
import exception.DaoException;
import javalinjwt.JWTProvider;
import file.File;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.javalin.JavalinWebContext;
import util.OAuthUtil;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static util.JavalinUtil.app;
//import static util.Pac4jUtil.githubSecurityHandler;

public class UserApi {

    private static JWTProvider provider = userJWTProvider.createHMAC512(); //initialize provider

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


    /**
     * This method is used to open the route for instructor to login
     * call instructorDao to check user identity
     * if login successful, send status code 201
     * if wrong user information, send status code 403, request forbidden
     * @param userDao dao for instructor table
     */
    public static void login(UserDao userDao) {
        // instructor login action, return user including his/her id
        app.post("/login", ctx -> {
            String email = ctx.formParam("email");
//            String pswd = ctx.formParam("pswd");
            try {
                User user = userDao.userLogin(email);
                String token = provider.generateToken(user);
                ctx.json(new JWTResponse(token));
                ctx.cookie("token", token);
                ctx.json(user); //comment this line after cookie is done
                ctx.contentType("application/json");
                ctx.status(201); // created successfully
            } catch (DaoException ex) {
                throw new ApiError(ex.getMessage(), 500); // server internal error
            } catch (LoginException ex) {
                throw new ApiError(ex.getMessage(), 403); // request forbidden, user not found
            }
        });
    }


//    public static void githubLogin(UserDao userDao) {
//        app.before("/github", githubSecurityHandler);
//        app.get("/github", ctx -> {
//            //get profile id and name
//            List<CommonProfile> profile = new ProfileManager<CommonProfile>(new JavalinWebContext(ctx)).getAll(true);
//            CommonProfile userProfile = profile.get(0);
//            String name = userProfile.getUsername();
//            String githubId = userProfile.getId();
//            System.out.println(name);
//            System.out.println(githubId);
//            try {
//                User user = userDao.githubLogin(name, githubId);
////                ctx.json(user); //json text of user model is printed on web page
////                System.out.println(user);
////                ctx.contentType("application/json");
////                ctx.status(200);
//                String token = provider.generateToken(user);
//                ctx.json(new JWTResponse(token));
//                ctx.cookie("token", token);
//                ctx.json(user); //comment this line after cookie is done
//                ctx.contentType("application/json");
//                ctx.status(200); // created successfully
//                System.out.println(ctx.queryParam("login"));
//                if (Objects.isNull(ctx.queryParam("login"))) {
//                    System.out.println("Redirect");
//                    ctx.redirect("http://localhost:3000/login");
//                }
//            } catch (DaoException ex) {
//                throw new ApiError(ex.getMessage(), 500); // server internal error
//            } catch (LoginException ex) {
//                throw new ApiError(ex.getMessage(), 403); // request forbidden, user not found
//            }
//        });
//    }

    public static void githubLogin() {
        app.get("/github", ctx-> {
            String githubOAuth = "https://github.com/login/oauth/authorize?client_id="
                    + OAuthUtil.getClientId() + "&scope=" + OAuthUtil.getScope();
            ctx.redirect(githubOAuth);
        });
    }

    public static void githubCallback() {
        app.get("/callback", ctx-> {
            String code = Objects.requireNonNull(ctx.queryParam("code"));
            System.out.println(code);
            HttpClient httpclient = HttpClients.createDefault();
            URI uri = new URIBuilder()
                    .setScheme("https")
                    .setHost("github.com")
                    .setPath("/login/oauth/access_token")
                    .setParameter("client_id", OAuthUtil.getClientId())
                    .setParameter("client_secret", OAuthUtil.getClientSecret())
                    .setParameter("code", code)
                    .build();
            HttpPost httppost = new HttpPost(uri);
            httppost.setHeader("Accept", "application/json");
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity);
            JsonObject jsonObject = new Gson().fromJson(responseString, JsonObject.class);
            String accessToken = jsonObject.get("access_token").toString();
            if (entity != null) {
                System.out.println(entity.getContentType());
                System.out.println(accessToken);
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
