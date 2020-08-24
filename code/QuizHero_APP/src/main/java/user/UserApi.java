package user;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import exception.ApiError;
import exception.DaoException;
import file.File;
import javalinjwt.JWTProvider;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import util.GithubUtil;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static util.JavalinUtil.app;

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


    public static void githubLogin() {
        app.get("/github", ctx-> {
            String githubOAuth = "https://github.com/login/oauth/authorize?client_id="
                    + GithubUtil.getClientId() + "&scope=" + GithubUtil.getScope();
            ctx.redirect(githubOAuth);
            ctx.status(302);
        });
    }

    private static Map<String, String> getToken(String code) throws IOException {
        Map<String, String> tokenMap = new HashMap<>();
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            URI postUri = new URIBuilder()
                    .setScheme("https")
                    .setHost("github.com")
                    .setPath("/login/oauth/access_token")
                    .setParameter("client_id", GithubUtil.getClientId())
                    .setParameter("client_secret", GithubUtil.getClientSecret())
                    .setParameter("code", code)
                    .build();
            HttpPost httppost = new HttpPost(postUri);
            httppost.setHeader("Accept", "application/json");
            HttpResponse postResponse = httpclient.execute(httppost);
            HttpEntity postEntity = postResponse.getEntity();
            String postResponseString = EntityUtils.toString(postEntity);
            JsonObject jsonObject = new Gson().fromJson(postResponseString, JsonObject.class);
            System.out.println(jsonObject);
            String accessToken = jsonObject.get("access_token").toString().replaceAll("\"", "");
            String tokenType = jsonObject.get("token_type").toString().replaceAll("\"", "");
            tokenMap.put("accessToken", accessToken);
            tokenMap.put("tokenType", tokenType);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } finally {
            httpclient.close();
        }
        return tokenMap;
    }

    private static Map<String, String> getUserInfo(String accessToken) throws IOException {
        Map<String, String> userInfoMap = new HashMap<>();
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            URI getUri = new URIBuilder()
                    .setScheme("https")
                    .setHost("api.github.com")
                    .setPath("/user")
                    .build();
            HttpGet httpget = new HttpGet(getUri);
            httpget.setHeader("AUTHORIZATION", "token " + accessToken);
            HttpResponse getResponse = httpclient.execute(httpget);
            HttpEntity getResponseEntity = getResponse.getEntity();
            String getResponseString = EntityUtils.toString(getResponseEntity);
            JsonObject newJsonObject = new Gson().fromJson(getResponseString, JsonObject.class);
            System.out.print(newJsonObject);
            String githubUserName = newJsonObject.get("login").toString().replace("\"", "");
            String githubEmail = newJsonObject.get("email").toString().replace("\"", "");
            String githubId = newJsonObject.get("id").toString().replace("\"", "");
            userInfoMap.put("githubUserName", githubUserName);
            userInfoMap.put("githubEmail", githubEmail);
            userInfoMap.put("githubId", githubId);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } finally {
            httpclient.close();
        }
        return userInfoMap;
    }

    public static void githubCallback(UserDao userDao) {
        app.get("/callback", ctx-> {
            // receive the code from github api after user login
            String code = Objects.requireNonNull(ctx.queryParam("code"));
            // start http request to receive access token from github api
            Map<String, String> tokenMap = getToken(code);

            //write access token to cookie; require no encryption?
            ctx.cookie("access_token", tokenMap.get("accessToken"));
            ctx.cookie("token_type", tokenMap.get("tokenType"));

            //get user info
            Map<String, String> userInfoMap = getUserInfo(tokenMap.get("accessToken"));
            String githubUserName = userInfoMap.get("githubUserName");
            String githubEmail = userInfoMap.get("githubEmail");
            String githubId = userInfoMap.get("githubId");

            //create/get user model
            try {
                User user = userDao.githubLogin(githubUserName, githubId);
                String token = provider.generateToken(user);
                ctx.json(new JWTResponse(token));
                ctx.cookie("token", token);
                ctx.json(user); //comment this line after cookie is done
                System.out.println(user);
                ctx.contentType("application/json");
                ctx.status(200); // created successfully
                System.out.println(ctx.queryParam("login"));
                if (Objects.isNull(ctx.queryParam("login"))) {
                    System.out.println("Redirect");
                    ctx.redirect("https://quiz-hero.herokuapp.com/login");
                    ctx.status(302);
                }
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
