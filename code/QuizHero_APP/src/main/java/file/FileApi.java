package file;

import com.sun.tools.javac.util.DefinedBy;
import exception.ApiError;
import exception.DaoException;
import exception.UserAuthenticationException;
import io.javalin.http.UploadedFile;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
                String token = context.cookie("token");
                if (token == null) {
                    System.out.println("no token");
                    throw new UserAuthenticationException("need user authentication");
                }
                String fileId = Objects.requireNonNull(context.queryParam("fileId")); // get file id from form-data
                System.out.println("file id: " + fileId);
                InputStream in = fileDao.getFileContent(fileId);
                InputStream inputStream = new BufferedInputStream(in); /* BufferedInputStream is used to improve the performance of the inside InputStream */
                context.result(inputStream);
                System.out.println("Send file successfully.");
                context.status(200);
            } catch (UserAuthenticationException ex) {
                    throw new ApiError("403 Forbidden: " + ex.getMessage(), 403);
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

    public static void uploadCSS(FileDao fileDao) {
        app.post("/uploadCSS", context -> {
            UploadedFile uploadedCss = context.uploadedFile("fileCSS"); // get file part
            try (InputStream inputStream = Objects.requireNonNull(uploadedCss).getContent()) {
                // fetch file id from form-data, require argument not null
                String fileId = Objects.requireNonNull(context.formParam("fileId"));
                System.out.println("file id: " + fileId);
                fileDao.updateCSS(fileId, inputStream);
                Map<String, Object> fileMap = new HashMap<>(); // return fileId and fileName to front-end
                fileMap.put("fileId", fileId);
                fileMap.put("fileCSS", inputStream);
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

    public static void saveCSS(FileDao fileDao) {
        app.post("/saveCSS", context -> {
            try {
                String fileId = context.formParam("fileId");
                String cssContent = context.formParam("fileCSS");
                InputStream cssStream = new ByteArrayInputStream(cssContent.getBytes(StandardCharsets.UTF_8));
                assert fileId != null;
                fileDao.updateCSS(fileId, cssStream);
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
                InputStream in = fileDao.getCSS(fileId);
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
}
