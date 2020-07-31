package file;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import sun.nio.cs.US_ASCII;
import util.OAuthUtil;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static util.JavalinUtil.app;

public class FileApi {
    /**
     * This method is used to open the route for instructor to upload a file
     * receive file stream and corresponding user id from front-end
     * pass data to the File class
     * @param fileDao call fileDao to update file table
     */
    public static void uploadFile(FileDao fileDao) {
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
    public static void fetchFile(FileDao fileDao) {
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

    public static void studentFetchFile(FileDao fileDao) {
        app.get("/studentfetch", context -> {
            try {
                String fileId = Objects.requireNonNull(context.queryParam("fileId")); // get file id from form-data
                System.out.println("file id: " + fileId);
                Boolean filePermission = fileDao.checkFilePermission(fileId);
                if (filePermission) {
                    InputStream in = fileDao.getFileContent(fileId);
                    InputStream inputStream = new BufferedInputStream(in); /* BufferedInputStream is used to improve the performance of the inside InputStream */
                    context.result(inputStream);
                    System.out.println("Send file successfully.");
                    context.status(200);
//                    context.redirect("http://localhost:3000/student");
                } else {
                    System.out.println("Don't have permission");
                    context.status(200);
                    context.redirect("http://localhost:3000/nopermission");
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


    //github pull file
    public static void pull(FileDao fileDao) {
        app.post("/pull", context -> {
            //get the desired file from github
            //read owner, repo and path as query parameter
            String owner = Objects.requireNonNull(context.queryParam("owner"));
            String repo = Objects.requireNonNull(context.queryParam("repo"));
            String path = Objects.requireNonNull(context.queryParam("path"));
            String accessToken = context.cookie("access_token");
            String tokenType = context.cookie("token_type");
            HttpClient httpclient = HttpClients.createDefault();
            URI getUri = new URIBuilder()
                    .setScheme("https")
                    .setHost("api.github.com")
                    .setPath("/repos/" + owner + "/" + repo + "/contents/" + path)
                    .build();
            HttpGet httpget = new HttpGet(getUri);
            httpget.setHeader("AUTHORIZATION", tokenType + " " + accessToken);
//            httpget.setHeader("Accept", "application/vnd.github.VERSION.raw");
            HttpResponse response = httpclient.execute(httpget);
            HttpEntity responseEntity = response.getEntity();
            String responseString = EntityUtils.toString(responseEntity);
            System.out.println(responseString);
            JsonObject JsonObject = new Gson().fromJson(responseString, JsonObject.class);
//            System.out.println(JsonObject);
            String content = JsonObject.get("content")
                    .toString()
                    .replace("\"", "")
                    .replace("\\n", "");
            System.out.println(content);
            String sha = JsonObject.get("sha").toString().replaceAll("\"", "");
            System.out.println(sha);
            context.cookie("sha", sha);

            byte[] decodedContent = Base64.getMimeDecoder().decode(content);

//            byte[] decodedContent =  Base64.getMimeDecoder().decode(content.getBytes(StandardCharsets.US_ASCII));
            InputStream inputStream = new ByteArrayInputStream(decodedContent);

//            InputStream inputStream = new ByteArrayInputStream(responseString.getBytes()); //this is for reponse from .raw

            try  {
                // fetch user id from form-data, require argument not null
                int userId = Integer.parseInt(Objects.requireNonNull(context.formParam("userId")));
                System.out.println("user id: " + userId);
                String fileName = path;
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

    public static void push(FileDao fileDao) {
//        saveFile(fileDao);
        app.post("/push", context -> {
            //read owner, repo and path as query parameter
            String owner = Objects.requireNonNull(context.queryParam("owner"));
            String repo = Objects.requireNonNull(context.queryParam("repo"));
            String path = Objects.requireNonNull(context.queryParam("path"));
            //read file content string as form parameter
            String fileContent = context.formParam("rawString");
            String message = context.formParam("commit");
            String accessToken = context.cookie("access_token");
            String tokenType = context.cookie("token_type");
            String sha = context.cookie("sha");
            String content = Base64.getMimeEncoder().encodeToString(fileContent.getBytes());

            HttpClient httpclient = HttpClients.createDefault();
            URI putUri = new URIBuilder()
                    .setScheme("https")
                    .setHost("api.github.com")
                    .setPath("/repos/" + owner + "/" + repo + "/contents/" + path)
                    .setParameter("message", message)
                    .setParameter("content", content)
                    .setParameter("sha", sha)
                    .build();
            HttpPut httpput = new HttpPut(putUri);
            httpput.setHeader("AUTHORIZATION", tokenType + " " + accessToken);
            HttpResponse response = httpclient.execute(httpput);
            HttpEntity responseEntity = response.getEntity();
            String responseString = EntityUtils.toString(responseEntity);
            System.out.println(responseString);
            //ctx.json?
            context.status(201);
        });
    }

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
//            httpget.setHeader("Accept", "application/vnd.github.VERSION.raw");
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
