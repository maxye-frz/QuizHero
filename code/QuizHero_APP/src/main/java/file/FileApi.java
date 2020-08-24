package file;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import exception.ApiError;
import exception.DaoException;
import io.javalin.http.UploadedFile;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import util.GithubUtil;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

import static util.JavalinUtil.app;

public class FileApi {

    /**
     * This method is used to open the route for instructor to upload a file
     * receive file stream and corresponding user id from front-end
     * pass data to the File class
     * @param fileDao call fileDao to update file table
     */
//    public static void uploadFile(FileDao fileDao) {
//        app.post("/upload", context -> {
//            UploadedFile uploadedFile = context.uploadedFile("file"); // get file part
//            try (InputStream inputStream = Objects.requireNonNull(uploadedFile).getContent()) {
//                // fetch user id from form-data, require argument not null
//                int userId = Integer.parseInt(Objects.requireNonNull(context.formParam("userId")));
//                System.out.println("user id: " + userId);
//                String fileName = uploadedFile.getFilename();
//                System.out.println("file content received. File name: " + fileName);
//
//                File file = new File (userId, fileName); // generate File object
//                fileDao.storeFile(file); // store file and update user-file info in database
//
//                Map<String, Object> fileMap = new HashMap<>(); // return fileId and fileName to front-end
//                fileMap.put("fileId", file.getFileId());
//                fileMap.put("fileName", file.getFileName());
//                context.json(fileMap);
//                context.contentType("application/json");
//                context.status(201);
//            } catch (DaoException ex) {
//                throw new ApiError("server error when uploading file: " + ex.getMessage(), 500);
//            } catch (NullPointerException ex) {
//                throw new ApiError("bad request with missing argument: " + ex.getMessage(), 400); // client bad request
//            }
//        });
//    }

//    public static void uploadFile(FileDao fileDao) {
//        app.post("/upload", ctx -> {
//            try {
//                UploadedFile uploadedFile = ctx.uploadedFile("file");
//                String fileName = uploadedFile.getFilename();
//                String fileContent = Objects.requireNonNull(uploadedFile).getContent().toString();
//                int userId = Integer.parseInt(Objects.requireNonNull(ctx.formParam("userId")));
//                String repoId = ctx.formParam("repoId");
//                String owner = GithubUtil.getGitName();
//                String repo = repoId;
//                String path = fileName;
//                String accessToken = GithubUtil.getPersonalAccessToken();
//                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
//                String message = "upload new md by QuizHero at " + timeStamp;
//                File file = new File(userId, fileName, owner, repo, path);
//                String sha = fileDao.push(file, accessToken, fileContent, message);
//                System.out.println("sha is : " + sha);
//                file.setSha(sha);
//                fileDao.storeFile(file);
//                Map<String, Object> fileMap = new HashMap<>(); // return fileId and fileName to front-end
//                fileMap.put("userId", userId);
//                fileMap.put("repoId", repoId);
//                fileMap.put("fileId", file.getFileId());
//                ctx.json(fileMap);
//                ctx.contentType("application/json");
//                ctx.status(201);
//            } catch (DaoException ex) {
//                throw new ApiError("sever error upload file: " + ex.getMessage(), 500);
//            } catch (NullPointerException ex) {
//                throw new ApiError("bad request with missing argument: " + ex.getMessage(), 400); // client bad request
//            }
//        });
//    }

    /**
     * This method is used to open the route for front-end to fetch a file
     * get file stream from database, and send the stream to front-end
     * @param fileDao call fileDao to get data from file table
     */
    public static void fetchFile(FileDao fileDao) {
        app.get("/fetch", ctx -> {
            try {
                String fileId = Objects.requireNonNull(ctx.queryParam("fileId")); // get file id from form-data
                System.out.println("file id: " + fileId);
                String accessToken = GithubUtil.getPersonalAccessToken();
                String content = fileDao.getFileContent(accessToken, fileId);
                ctx.result(content);
                System.out.println("fetch file successfully.");
                ctx.status(200);
            } catch (DaoException ex) {
                throw new ApiError("server error when fetching file: " + ex.getMessage(), 500);
            } catch (NullPointerException ex) {
                throw new ApiError("bad request with missing argument: " + ex.getMessage(), 400);
            }
        });
    }

    public static void studentFetchFile(FileDao fileDao) {
        app.get("/studentfetch", ctx -> {
            try {
                String fileId = Objects.requireNonNull(ctx.queryParam("fileId")); // get file id from form-data
                System.out.println("file id: " + fileId);
                Boolean filePermission = fileDao.checkFilePermission(fileId);
                if (filePermission) {
                    String accessToken = GithubUtil.getPersonalAccessToken();
                    String content = fileDao.getFileContent(accessToken, fileId);
                    ctx.result(content);

                    System.out.println("Send file successfully.");
                    ctx.status(200);
//                    context.redirect("http://localhost:3000/student");
                } else {
                    System.out.println("Don't have permission");
                    ctx.status(200);
                    ctx.redirect("http://localhost:3000/nopermission");
                }
            } catch (DaoException ex) {
                throw new ApiError("server error when fetching file: " + ex.getMessage(), 500);
            } catch (NullPointerException ex) {
                throw new ApiError("bad request with missing argument: " + ex.getMessage(), 400);
            }
        });
    }

    /**
     *  Save (new) file api
     */
    public static void saveFile(FileDao fileDao) {
        app.post("/save", ctx -> {
            try {
                File file;
                String accessToken = GithubUtil.getPersonalAccessToken();
                String fileId = ctx.formParam("fileId"); //get fileId
                String fileName = ctx.formParam("fileName"); //get file name
                String fileContent = ctx.formParam("rawString"); //get file content
                int userId = Integer.parseInt(Objects.requireNonNull(ctx.formParam("userId")));
                String repoId = ctx.formParam("repoId");
                String owner = GithubUtil.getOrganizationName();
                String repo = repoId;
                String path = fileId;
                if (fileId.equals("")) {
                    file = new File(userId, fileName, owner, repo, path);
                    file.setPath(file.getFileId());
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());String message = "initial save file by QuizHero at " + timeStamp;
                    System.out.println(file);
                    System.out.println(accessToken);
                    System.out.println(message);
                    System.out.println(fileContent);
                    String sha = fileDao.push(file, accessToken, fileContent, message);
                    file.setSha(sha);
                    fileDao.storeFile(file);
                } else {
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
                    String message = "save file by QuizHero at " + timeStamp;
                    file = fileDao.updateFile(accessToken, fileId, fileName, fileContent, message);
                }
                ctx.json(file);
                ctx.contentType("application/json");
                ctx.status(201);
            } catch (DaoException ex) {
                throw new ApiError("server error when save file: " + ex.getMessage(), 500);
            } catch (NullPointerException ex) {
                throw new ApiError("bad request with missing argument: " + ex.getMessage(), 400); // client bad request
            }
        });
    }

//    public static void testDelete(FileDao fileDao) {
//        app.post("/test", ctx-> {
//            String fileId = ctx.formParam("fileId");
//            String accessToken = ctx.formParam("accessToken");
//            File file = fileDao.getFile(fileId);
//            fileDao.delete(file, accessToken);
//        });
//    }

    public static void push(FileDao fileDao) {
//        saveFile(fileDao);
        app.post("/push", context -> {
            //read owner, repo and path as query parameter
            String owner = "quizherotest";
            String repo = context.formParam("repoId");
            String path = context.formParam("fileName");
            //read file content string as form parameter
            String fileContent = context.formParam("rawString");
            String message = "init new file";
            String accessToken = context.cookie("access_token");
            String tokenType = context.cookie("token_type");
            String sha = context.cookie("sha");
            String content = Base64.getMimeEncoder().encodeToString(fileContent.getBytes());

            HttpClient httpclient = HttpClients.createDefault();
            URI putUri = new URIBuilder()
                    .setScheme("https")
                    .setHost("api.github.com")
                    .setPath("/orgs/" + owner + "/" + repo + "/contents/" + path)
                    .build();
            HttpPut httpput = new HttpPut(putUri);
            String inputJson = "{\n" +
                    "\"message\": \"" + message + "\",\n" +
                    "\"content\": \"" + content + "\",\n" +
                    "\"sha\": \"" + sha + "\"\n" +
                    "}";
            System.out.println(inputJson);
            StringEntity stringEntity = new StringEntity(inputJson);
            httpput.setEntity(stringEntity);

            httpput.setHeader("Accept", "application/vnd.github.v3+json");
            httpput.setHeader("AUTHORIZATION", "token " + accessToken);
            HttpResponse response = httpclient.execute(httpput);
            HttpEntity responseEntity = response.getEntity();
            String responseString = EntityUtils.toString(responseEntity);
            System.out.println(responseString);
            Map<String, Object> pushMap = new HashMap<>(); // return  to front-end
            pushMap.put("sha", sha);
            pushMap.put("message", message);
            context.json(pushMap);
            context.status(201);
        });
    }

    /**
     * This method is used to open the route for front-end to change
     * the file permission of a single file
     * @param fileDao call fileDao to update file table
     */
    public static void changeFilePermission(FileDao fileDao) {
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
    public static void checkFilePermission(FileDao fileDao) {
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
     * This method is used to open the route for front-end to
     * change the quiz permission of a single file
     * @param fileDao call fileDao to update file table
     */
    public static void changeQuizPermission(FileDao fileDao) {
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
    public static void checkQuizPermission(FileDao fileDao) {
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
    public static void deleteFile(FileDao fileDao) {
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

//    public static void uploadCSS(FileDao fileDao) {
//        app.post("/uploadCSS", context -> {
//            UploadedFile uploadedCss = context.uploadedFile("fileCSS"); // get file part
//            try (InputStream inputStream = Objects.requireNonNull(uploadedCss).getContent()) {
//                // fetch file id from form-data, require argument not null
//                String fileId = Objects.requireNonNull(context.formParam("fileId"));
//                System.out.println("file id: " + fileId);
//                fileDao.updateCSS(fileId, inputStream);
//                Map<String, Object> fileMap = new HashMap<>(); // return fileId and fileName to front-end
//                fileMap.put("fileId", fileId);
//                fileMap.put("fileCSS", inputStream);
//                context.json(fileMap);
//                context.contentType("application/json");
//                context.status(201);
//            } catch (DaoException ex) {
//                throw new ApiError("server error when uploading file: " + ex.getMessage(), 500);
//            } catch (NullPointerException ex) {
//                throw new ApiError("bad request with missing argument: " + ex.getMessage(), 400); // client bad request
//            }
//        });
//    }

    public static void saveCSS(FileDao fileDao) {
        app.post("/saveCSS", context -> {
            try {
                String fileId = context.formParam("fileId");
                String cssContent = context.formParam("fileCSS");
                assert fileId != null;
                fileDao.updateCSS(fileId, cssContent);
                Map<String, Object> fileMap = new HashMap<>(); // return fileId and fileName to front-end
                fileMap.put("fileId", fileId);
                fileMap.put("fileCSS", cssContent);
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

    public static void readCSS(FileDao fileDao) {
        app.get("/readCSS", context -> {
            try {
                String fileId = Objects.requireNonNull(context.queryParam("fileId")); // get file id from form-data
                System.out.println("file id: " + fileId);
                String css = fileDao.getCSS(fileId);
                context.result(css);
                System.out.println("Send file successfully.");
                context.status(200);
            } catch (DaoException ex) {
                throw new ApiError("server error when fetching file: " + ex.getMessage(), 500);
            } catch (NullPointerException ex) {
                throw new ApiError("bad request with missing argument: " + ex.getMessage(), 400);
            }
        });
    }

    public static void clone(FileDao fileDao) {
        app.post("/clone", ctx -> {
            try{
                int userId = Integer.parseInt(Objects.requireNonNull(ctx.formParam("userId")));
                String githubOwner = Objects.requireNonNull(ctx.queryParam("owner"));
                String githubRepo = Objects.requireNonNull(ctx.queryParam("repo"));
                String githubPath = Objects.requireNonNull(ctx.queryParam("path"));
                String fileName = githubPath; // temp fileName
                String accessToken = ctx.cookie("access_token");
                File githubFile = new File(userId, fileName, githubOwner, githubRepo, githubPath);
                String content = fileDao.pull(githubFile, accessToken);
                String repoId = ctx.formParam("repoId");
                String owner = GithubUtil.getOrganizationName();
                String repo = repoId;
                String path = githubFile.getFileId();
                String quizheroAccessToken = GithubUtil.getPersonalAccessToken();
                File clonedFile = new File(userId, fileName, owner, repo, path);
                clonedFile.setPath(githubFile.getFileId());
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
                String message = "cloned from github by QuizHero at " + timeStamp;
                System.out.println(clonedFile);
                String sha = fileDao.push(clonedFile, quizheroAccessToken, content, message);
                clonedFile.setSha(sha);
                fileDao.storeFile(clonedFile);
                ctx.json(clonedFile);
                ctx.contentType("application/json");
                ctx.status(201);
//                System.out.println(quizheroFile);
//                String quizheorSha = fileDao.push(quizheroFile, quizheroAccessToken, content, message);
//                quizheroFile.setSha(quizheorSha);
//                fileDao.storeFile(quizheroFile);
//                Map<String, Object> cloneMap = new HashMap<>();
//                cloneMap.put("userId", userId);
//                cloneMap.put("repoId", repoId);
//                cloneMap.put("fileId", quizheroFile.getFileId());
//                ctx.json(cloneMap);
//                ctx.contentType("application/json");
//                ctx.status(201);
            }catch (DaoException ex) {
                throw new ApiError("server error when uploading file: " + ex.getMessage(), 500);
            } catch (NullPointerException ex) {
                throw new ApiError("bad request with missing argument: " + ex.getMessage(), 400); // client bad request
            }
        });
    }

    //github pull file
//    public static void pull(FileDao fileDao) {
//        app.post("/pull", context -> {
//            //get the desired file from github
//            //read owner, repo and path as query parameter
//            String owner = Objects.requireNonNull(context.queryParam("owner"));
//            String repo = Objects.requireNonNull(context.queryParam("repo"));
//            String path = Objects.requireNonNull(context.queryParam("path"));
//            String accessToken = context.cookie("access_token");
//            System.out.println(accessToken);
//            String tokenType = context.cookie("token_type");
//            HttpClient httpclient = HttpClients.createDefault();
//            URI getUri = new URIBuilder()
//                    .setScheme("https")
//                    .setHost("api.github.com")
//                    .setPath("/repos/" + owner + "/" + repo + "/contents/" + path)
//                    .build();
//            HttpGet httpget = new HttpGet(getUri);
//            httpget.setHeader("AUTHORIZATION", "token " + accessToken);
//            httpget.setHeader("Accept", "application/vnd.github.v3+json");
//            HttpResponse response = httpclient.execute(httpget);
//            HttpEntity responseEntity = response.getEntity();
//            String responseString = EntityUtils.toString(responseEntity);
//            System.out.println(responseString);
//            JsonObject JsonObject = new Gson().fromJson(responseString, JsonObject.class);
////            System.out.println(JsonObject);
//            String content = JsonObject.get("content")
//                    .toString()
//                    .replace("\"", "")
//                    .replace("\\n", "");
//            System.out.println(content);
//            String sha = JsonObject.get("sha").toString().replaceAll("\"", "");
//            System.out.println(sha);
//            context.cookie("sha", sha);
//            Map<String, Object> pullMap = new HashMap<>();
//            pullMap.put("sha", sha);
//
//            byte[] decodedContent = Base64.getMimeDecoder().decode(content);
//
////            byte[] decodedContent =  Base64.getMimeDecoder().decode(content.getBytes(StandardCharsets.US_ASCII));
//            InputStream inputStream = new ByteArrayInputStream(decodedContent);
//
////            InputStream inputStream = new ByteArrayInputStream(responseString.getBytes()); //this is for reponse from .raw
//
//            try  {
//                // fetch user id from form-data, require argument not null
//                int userId = Integer.parseInt(Objects.requireNonNull(context.formParam("userId")));
//                System.out.println("user id: " + userId);
//                String fileName = path;
//                System.out.println("file content received. File name: " + fileName);
////                File localFile = new File("upload/" + uploadedFile.getFilename());
////                FileUtils.copyInputStreamToFile(inputStream, localFile);
////                String url = localFile.getAbsolutePath();
//
//                File file = new File (userId, fileName, inputStream); // generate File object
//                fileDao.storeFile(file); // store file and update user-file info in database
//
//                pullMap.put("fileId", file.getFileId());
//                pullMap.put("fileName", file.getFileName());
//                context.json(pullMap);
//                context.contentType("application/json");
//                context.status(201);
//            } catch (DaoException ex) {
//                throw new ApiError("server error when uploading file: " + ex.getMessage(), 500);
//            } catch (NullPointerException ex) {
//                throw new ApiError("bad request with missing argument: " + ex.getMessage(), 400); // client bad request
//            }
//        });
//    }

//    public static void push(FileDao fileDao) {
////        saveFile(fileDao);
//        app.post("/push", context -> {
//            //read owner, repo and path as query parameter
//            String owner = Objects.requireNonNull(context.queryParam("owner"));
//            String repo = Objects.requireNonNull(context.queryParam("repo"));
//            String path = Objects.requireNonNull(context.queryParam("path"));
//            //read file content string as form parameter
//            String fileContent = context.formParam("rawString");
//            String message = context.formParam("commit");
//            String accessToken = context.cookie("access_token");
//            String tokenType = context.cookie("token_type");
//            String sha = context.cookie("sha");
//            String content = Base64.getMimeEncoder().encodeToString(fileContent.getBytes());
//
//            HttpClient httpclient = HttpClients.createDefault();
//            URI putUri = new URIBuilder()
//                    .setScheme("https")
//                    .setHost("api.github.com")
//                    .setPath("/repos/" + owner + "/" + repo + "/contents/" + path)
//                    .build();
//            HttpPut httpput = new HttpPut(putUri);
//            String inputJson = "{\n" +
//                    "\"message\": \"" + message + "\",\n" +
//                    "\"content\": \"" + content + "\",\n" +
//                    "\"sha\": \"" + sha + "\"\n" +
//                    "}";
//            StringEntity stringEntity = new StringEntity(inputJson);
//            httpput.setEntity(stringEntity);
//
//            httpput.setHeader("Accept", "application/vnd.github.v3+json");
//            httpput.setHeader("AUTHORIZATION", "token " + accessToken);
//            HttpResponse response = httpclient.execute(httpput);
//            HttpEntity responseEntity = response.getEntity();
//            String responseString = EntityUtils.toString(responseEntity);
//            System.out.println(responseString);
//            Map<String, Object> pushMap = new HashMap<>(); // return  to front-end
//            pushMap.put("sha", sha);
//            pushMap.put("message", message);
//            context.json(pushMap);
//            context.status(201);
//        });
//    }

    public static void listRepo() {
        app.get("/listRepo", ctx -> {
            HttpClient httpclient = HttpClients.createDefault();
            URI getUri = new URIBuilder()
                    .setScheme("https")
                    .setHost("api.github.com")
                    .setPath("/user/repos")
                    .build();
            HttpGet httpget = new HttpGet(getUri);
            String accessToken = ctx.cookie("access_token");
            httpget.setHeader("AUTHORIZATION", "token " + accessToken);
            HttpResponse response = httpclient.execute(httpget);
            HttpEntity responseEntity = response.getEntity();
            String responseString = EntityUtils.toString(responseEntity);
            JsonArray JsonArray = new Gson().fromJson(responseString, JsonArray.class);
            System.out.println(JsonArray);
            List<String> repo = new ArrayList<>();
            for (int i = 0; i < JsonArray.size(); i++) {
                JsonObject repoObject = new Gson().fromJson(JsonArray.get(i), JsonObject.class);
                String repoName = repoObject.get("full_name").toString().replaceAll("\"", "");
                repo.add(repoName);
            }
            System.out.println(repo);
            ctx.json(repo);
            ctx.status(200);
        });
    }

    public static void listContent() {
        app.get("listContent", ctx -> {
            String owner = Objects.requireNonNull(ctx.queryParam("owner"));
            String repo = Objects.requireNonNull(ctx.queryParam("repo"));
            String path = Objects.requireNonNull(ctx.queryParam("path"));
            String accessToken = ctx.cookie("access_token");
            String tokenType = ctx.cookie("token_type");
            HttpClient httpclient = HttpClients.createDefault();
            URI getUri = new URIBuilder()
                    .setScheme("https")
                    .setHost("api.github.com")
                    .setPath("/repos/" + owner + "/" + repo + "/contents/" + path)
                    .build();
            HttpGet httpget = new HttpGet(getUri);
            httpget.setHeader("AUTHORIZATION", tokenType + " " + accessToken);
            HttpResponse response = httpclient.execute(httpget);
            HttpEntity responseEntity = response.getEntity();
            String responseString = EntityUtils.toString(responseEntity);
            JsonArray JsonArray = new Gson().fromJson(responseString, JsonArray.class);
            System.out.println(JsonArray);
            List<String> pathArray = new ArrayList<>();
            for (int i = 0; i < JsonArray.size(); i++) {
                JsonObject repoObject = new Gson().fromJson(JsonArray.get(i), JsonObject.class);
                String pathName = repoObject.get("path").toString().replaceAll("\"", "");
                pathArray.add(pathName);
            }
            ctx.json(pathArray);
            ctx.status(200);
        });
    }
}
