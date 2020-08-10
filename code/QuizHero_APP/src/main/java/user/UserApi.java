package user;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import exception.ApiError;
import exception.DaoException;
import javalinjwt.JWTProvider;
import file.File;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import util.GithubUtil;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

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
            String uniqueId = UUID.randomUUID().toString();
            user.setGithubId(uniqueId);
            try {
                userDao.registerUser(user);

                String accessToken = GithubUtil.getPersonalAccessToken();
                String org = GithubUtil.getOrganizationName();
                String repoName = user.getGithubId();
                System.out.println(repoName);
                HttpClient httpclient = HttpClients.createDefault();
                URI postUri = new URIBuilder()
                        .setScheme("https")
                        .setHost("api.github.com")
                        .setPath("/orgs/" + org + "/repos")
                        .build();
                HttpPost httppost = new HttpPost(postUri);
                String inputJson = "{\n" +
                        "\"name\": \"" + repoName + "\",\n" +
                        "\"private\": \"" + true + "\"\n" +
                        "}";
                System.out.println(inputJson);
                StringEntity stringEntity = new StringEntity(inputJson);
                httppost.setEntity(stringEntity);
                httppost.setHeader("AUTHORIZATION", "token " + accessToken);
                httppost.setHeader("Accept", "application/vnd.github.v3+json");
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity responseEntity = response.getEntity();
                String responseString = EntityUtils.toString(responseEntity);
                System.out.println(responseString);

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

// old github login api using pac4j and github client
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
                    + GithubUtil.getClientId() + "&scope=" + GithubUtil.getScope();
            ctx.redirect(githubOAuth);
            ctx.status(302);
        });
    }

    public static void githubCallback(UserDao userDao) {
        app.get("/callback", ctx-> {
            // receive the code from github api after user login
            String code = Objects.requireNonNull(ctx.queryParam("code"));
            // start http request to receive access token from github api
            HttpClient httpclientPost = HttpClients.createDefault();
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
            HttpResponse postResponse = httpclientPost.execute(httppost);
            HttpEntity postEntity = postResponse.getEntity();
            String postResponseString = EntityUtils.toString(postEntity);
            JsonObject jsonObject = new Gson().fromJson(postResponseString, JsonObject.class);
            System.out.println(jsonObject);
            String accessToken = jsonObject.get("access_token").toString().replaceAll("\"", "");
            String tokenType = jsonObject.get("token_type").toString().replaceAll("\"", "");

            //write access token to cookie; require no encryption?
            ctx.cookie("access_token", accessToken);
            ctx.cookie("token_type", tokenType);

            //get user info
            HttpClient httpclientGet = HttpClients.createDefault();
            URI getUri = new URIBuilder()
                    .setScheme("https")
                    .setHost("api.github.com")
                    .setPath("/user")
                    .build();
            HttpGet httpget = new HttpGet(getUri);
            httpget.setHeader("AUTHORIZATION", "token " + accessToken);
            HttpResponse getResponse = httpclientGet.execute(httpget);
            HttpEntity getResponseEntity = getResponse.getEntity();
            String getResponseString = EntityUtils.toString(getResponseEntity);
            JsonObject newJsonObject = new Gson().fromJson(getResponseString, JsonObject.class);
            System.out.print(newJsonObject);
            String githubUserName = newJsonObject.get("login").toString().replace("\"", "");
            String githubEmail = newJsonObject.get("email").toString().replace("\"", "");
            String githubId = newJsonObject.get("id").toString().replace("\"", "");

            //create/get user model
            try {
                User user = userDao.githubLogin(githubUserName, githubEmail, githubId);
                ctx.json(user); //json text of user model is printed on web page
                System.out.println(user);
                ctx.contentType("application/json");
                ctx.status(200);
                String token = provider.generateToken(user);
                ctx.json(new JWTResponse(token));
                ctx.cookie("token", token);
                ctx.json(user); //comment this line after cookie is done
                ctx.contentType("application/json");
                ctx.status(200); // created successfully
                System.out.println(ctx.queryParam("login"));
                if (Objects.isNull(ctx.queryParam("login"))) {
                    System.out.println("Redirect");
                    ctx.redirect("http://localhost:3000/login");
                }
            } catch (DaoException ex) {
                throw new ApiError(ex.getMessage(), 500); // server internal error
            } catch (LoginException ex) {
                throw new ApiError(ex.getMessage(), 403); // request forbidden, user not found
            }
            ctx.redirect("/login");
            ctx.status(302);
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
