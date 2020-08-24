package file;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import exception.DaoException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * FileDao interface defines methods related to the file table
 * @author QuizHero team @JHU OOSE spring20
 * @version 1.3
 */
public class FileDao {
    private Sql2o sql2o;

    public FileDao(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    public static String push(File file, String accessToken,
                            String fileContent, String message) throws IOException {
        String owner = file.getOwner();
        String repo = file.getRepo();
        String path = file.getPath();
        String sha = file.getSha();
        String content = Base64.getMimeEncoder().encodeToString(fileContent.getBytes()).replaceAll("\\r\\n|\\r|\\n|", "");
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            URI putUri = new URIBuilder()
                    .setScheme("https")
                    .setHost("api.github.com")
                    .setPath("/repos/" + owner + "/" + repo + "/contents/" + path)
                    .build();
            HttpPut httpput = new HttpPut(putUri);
            String inputJson = "{\n" +
                    "\"message\": \"" + message + "\",\n" +
                    "\"content\": \"" + content + "\",\n" +
                    "\"sha\": \"" + sha + "\"\n" +
                    "}";
            StringEntity stringEntity = new StringEntity(inputJson);
            httpput.setEntity(stringEntity);
            httpput.setHeader("Accept", "application/vnd.github.v3+json");
            httpput.setHeader("AUTHORIZATION", "token " + accessToken);
            HttpResponse response = httpclient.execute(httpput);
            HttpEntity responseEntity = response.getEntity();
            String responseString = EntityUtils.toString(responseEntity);
            System.out.println(responseString);
            JsonObject JsonObject = new Gson().fromJson(responseString, JsonObject.class);
            sha = JsonObject.get("content").getAsJsonObject().get("sha").toString().replaceAll("\"", "");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } finally {
            httpclient.close();
        }
        return sha;
    }

    public static String pull(File file, String accessToken) throws IOException {
        String owner = file.getOwner();
        String repo = file.getRepo();
        String path = file.getPath();
        String content = ""; //why need this?
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            URI getUri = new URIBuilder()
                    .setScheme("https")
                    .setHost("api.github.com")
                    .setPath("/repos/" + owner + "/" + repo + "/contents/" + path)
                    .build();
            HttpGet httpget = new HttpGet(getUri);
            httpget.setHeader("AUTHORIZATION", "token " + accessToken);
            httpget.setHeader("Accept", "application/vnd.github.v3+json");
            HttpResponse response = httpclient.execute(httpget);
            HttpEntity responseEntity = response.getEntity();
            String responseString = EntityUtils.toString(responseEntity);
            System.out.println(responseString);
            JsonObject JsonObject = new Gson().fromJson(responseString, JsonObject.class);
            String encodedContent = JsonObject.get("content")
                    .toString()
                    .replace("\"", "")
                    .replace("\\n", "");
            byte[] decodedContent = Base64.getMimeDecoder().decode(encodedContent);
            content = new String(decodedContent, StandardCharsets.UTF_8);
            System.out.println(content);
            String sha = JsonObject.get("sha").toString().replaceAll("\"", "");
            System.out.println(sha);
            file.setSha(sha);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpclient.close();
        }
        return content;
    }

//    @NotThreadSafe
//    static
//    class HttpDeleteWithBody extends HttpEntityEnclosingRequestBase {
//        public static final String METHOD_NAME = "DELETE";
//        public String getMethod() { return METHOD_NAME; }
//
//        public HttpDeleteWithBody(final String uri) {
//            super();
//            setURI(URI.create(uri));
//        }
//        public HttpDeleteWithBody(final URI uri) {
//            super();
//            setURI(uri);
//        }
//        public HttpDeleteWithBody() { super(); }
//    }

//    public static void delete(File file, String accessToken) throws IOException {
//        String owner = file.getOwner();
//        String repo = file.getRepo();
//        String path = file.getPath();
//        String sha = file.getSha();
//        String message = "delete from database by QuizHero";
//        CloseableHttpClient httpclient = HttpClients.createDefault();
//        try {
//            URI deleteUri = new URIBuilder()
//                    .setScheme("https")
//                    .setHost("api.github.com")
//                    .setPath("/repos/" + owner + "/" + repo + "/contents/" + path)
//                    .build();
//            HttpDeleteWithBody httpdelete = new HttpDeleteWithBody(deleteUri);
//            String inputJson = "{\n" +
//                    "\"message\": \"" + message + "\",\n" +
//                    "\"sha\": \"" + sha + "\"\n" +
//                    "}";
//            StringEntity stringEntity = new StringEntity(inputJson);
//            httpdelete.setEntity(stringEntity);
//            httpdelete.setHeader("Accept", "application/vnd.github.v3+json");
//            httpdelete.setHeader("AUTHORIZATION", "token " + accessToken);
//            HttpResponse response = httpclient.execute(httpdelete);
//            HttpEntity responseEntity = response.getEntity();
//            String responseString = EntityUtils.toString(responseEntity);
//            System.out.println(responseString);
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        } catch (ClientProtocolException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            httpclient.close();
//        }
//    }

    /**
     * This method is used to store the file stream in the database
     * @param file an instance of File class
     */
    public void storeFile(File file) {
        try (Connection conn = sql2o.open()) {
            String sql = "insert into file values (:fileId, :fileName, :filePermission, :quizPermission, " +
                    ":owner, :repo, :path, :sha, :fileCss)";
            conn.createQuery(sql)
                    .addParameter("fileId", file.getFileId())
                    .addParameter("fileName", file.getFileName())
                    .addParameter("filePermission", file.getFilePermission())
                    .addParameter("quizPermission", file.getQuizPermission())
                    .addParameter("owner", file.getOwner())
                    .addParameter("repo", file.getRepo())
                    .addParameter("path", file.getPath())
                    .addParameter("sha", file.getSha())
                    .addParameter("fileCss", file.getFileCss())
                    .executeUpdate();
        } catch (Sql2oException ex) {
            throw new DaoException("Unable to store file content", ex);
        }
        storeInsFile(file);
    }

    /**
     * This method is used to store the relationship between file and instructor in the database
     * @param file an instance of File class
     */
    public void storeInsFile(File file) {
        try (Connection conn = sql2o.open()) {
            String sql = "INSERT INTO account_file(userId, fileId) VALUES (:userId, :fileId);";
            conn.createQuery(sql, true)
                    .addParameter("userId", file.getUserId())
                    .addParameter("fileId", file.getFileId())
                    .executeUpdate();
            System.out.println("user-file information stored.");
        } catch (Sql2oException ex1) {
            throw new DaoException("Unable to store user-file information.", ex1);
        }
    }


//    /**
//     * This method is used to get the file stream from the database
//     * @param fileId unique id of a file
//     * @return InputStream of the file content
//     */
//    public InputStream getFileContent(String fileId) {
//        checkFileExist(fileId);
//        ByteArrayInputStream byteStream;
//        try (Connection conn = sql2o.open()) {
//            String sql = "SELECT fileContent FROM file WHERE fileId = :fileId";
//            byteStream = conn.createQuery(sql)
//                    .addParameter("fileId", fileId)
//                    .executeAndFetchFirst(ByteArrayInputStream.class);
//
//            return byteStream;
//        } catch (Sql2oException ex) {
//            throw new DaoException("Unable to fetch file.", ex);
//        }
//    }

    public File getFile(String fileId) {
        checkFileExist(fileId);
        File file;
        try (Connection conn = sql2o.open()) {
            String sql = "SELECT * FROM file Where fileId = :fileId;";
            file = conn.createQuery(sql)
                    .addParameter("fileId", fileId)
                    .executeAndFetchFirst(File.class);
        } catch (Sql2oException ex) {
            throw new DaoException("database error" + ex.getMessage(), ex);
        }
        return file;
    }

    public String getFileContent(String accessToken, String fileId) {
        checkFileExist(fileId);
        File file;
        String content;
        try (Connection conn = sql2o.open()) {
            String sql = "SELECT * FROM file Where fileId = :fileId;";
            file = conn.createQuery(sql)
                    .addParameter("fileId", fileId)
                    .executeAndFetchFirst(File.class);
            content = pull(file, accessToken);
        }
        catch (Sql2oException | IOException ex) {
            throw new DaoException("database error" + ex.getMessage(), ex);
        }
        return content;
    }

//    /**
//     * This method is used to update the file
//     * @param fileId a string of fileId
//     * @param fileName a string of fileName
//     * @param fileContent an inputStream of fileContent
//     */
//    public void updateFile(String fileId, String fileName, InputStream fileContent) {
//        checkFileExist(fileId);
//        try (Connection conn = sql2o.open()) {
//            String sql = "UPDATE file SET fileName = :valFileName," +
//                    " fileContent = :valFileContent" +
//                    " WHERE fileId = :valFileId";
//
//            System.out.println(sql); //console msg
//            conn.createQuery(sql)
//                    .addParameter("valFileId", fileId)
//                    .addParameter("valFileName", fileName)
//                    .addParameter("valFileContent", fileContent)
//                    .executeUpdate();
//        } catch (Sql2oException ex) {
//            throw new DaoException("Unable to update file", ex);
//        }
//    }
    public Integer getFileUserId(String fileId) {
        checkFileExist(fileId);
        Integer userId;
        try (Connection conn = sql2o.open()) {
            String sql = "SELECT userId from account_file WHERE fileId = :fileId";
            System.out.println(sql);
            userId = conn.createQuery(sql).addParameter("fileId", fileId)
                    .executeAndFetchFirst(Integer.class);
        } catch (Sql2oException ex) {
            throw new DaoException("database error" + ex.getMessage(), ex);
        }
        return userId;
    }

    public File updateFile(String accessToken, String fileId, String fileName, String fileContent, String message) {
        checkFileExist(fileId);
        File file = getFile(fileId);
        try {
            file.setUserId(getFileUserId(fileId));
            String sha = push(file, accessToken, fileContent, message);
            file.setSha(sha);
            if (!file.getFileName().equals(fileName)) {
                changeFileName(fileId, fileName);
            }
            file.setFileName(fileName);
            changeFileSha(fileId, sha);
        } catch (IOException ex) {
            throw new DaoException("database error" + ex.getMessage(), ex);
        }
        return file;
    }

    public void changeFileName(String fileId, String fileName) {
        checkFileExist(fileId);
        try (Connection conn = sql2o.open()) {
            String sql = "UPDATE file SET fileName = '" + fileName + "'" +
                    " WHERE fileId = :fileId";
            System.out.println(sql);
            conn.createQuery(sql).addParameter("fileId", fileId)
                    .executeUpdate();
        } catch (Sql2oException ex) {
            throw new DaoException("Unable to change file name", ex);
        }
    }

    public void changeFileSha(String fileId, String sha) {
        checkFileExist(fileId);
        try (Connection conn = sql2o.open()) {
            String sql = "UPDATE file SET sha = '" + sha + "'" +
                    " WHERE fileId = :fileId";
            System.out.println(sql);
            conn.createQuery(sql).addParameter("fileId", fileId)
                    .executeUpdate();
        } catch (Sql2oException ex) {
            throw new DaoException("Unable to change sha", ex);
        }
    }


    /**
     * This method is used to modify the file permission status in the database
     * @param fileId unique id of a file
     * @param filePermission permission status of a file
     */
    public void changeFilePermission(String fileId, boolean filePermission) {
        checkFileExist(fileId);
        try (Connection conn = sql2o.open()) {
            String sql = "UPDATE file SET filePermission = " + filePermission +
                    " WHERE fileId = :fileId";
            System.out.println(sql);
            conn.createQuery(sql).addParameter("fileId", fileId)
                    .executeUpdate();
        } catch (Sql2oException ex) {
            throw new DaoException("Unable to change file permission status", ex);
        }
    }

    /**
     * This method is used to get the file permission status in the database
     * @param fileId unique id of a file
     * @return true or false of the file permission status
     */
    public Boolean checkFilePermission(String fileId) {
        try (Connection conn = sql2o.open()) {
            String sql = "SELECT filePermission from file WHERE fileId = :fileId";
            System.out.println(sql);
            Boolean permission = conn.createQuery(sql).addParameter("fileId", fileId)
                    .executeAndFetchFirst(Boolean.class);
            return permission;
        } catch (Sql2oException ex) {
            throw new DaoException("Unable to get file permission status", ex);
        }
    }

    /**
     * This method is used to modify the quiz permission status of a file in the database
     * @param fileId unique id of a file
     * @param quizPermission permission status of the quiz content in a file
     */
    public void changeQuizPermission(String fileId, boolean quizPermission) {
        checkFileExist(fileId);
        try (Connection conn = sql2o.open()) {
            String sql = "Update file set quizPermission = " + quizPermission +
                    " WHERE fileId = :fileId";
            System.out.println(sql);
            conn.createQuery(sql).addParameter("fileId", fileId)
                    .executeUpdate();
        } catch (Sql2oException ex) {
            throw new DaoException("Unable to change quiz permission status", ex);
        }
    }

    /**
     * This method is used to get the quiz permission status of a file in the database
     * @param fileId unique id of a file
     * @return true or false of the quiz permission status
     */
    public Boolean checkQuizPermission(String fileId) {
        try (Connection conn = sql2o.open()) {
            String sql = "SELECT quizPermission from file WHERE fileId = :fileId";
            System.out.println(sql);
            Boolean quizPermission = conn.createQuery(sql).addParameter("fileId", fileId)
                    .executeAndFetchFirst(Boolean.class);
            return quizPermission;
        } catch (Sql2oException ex) {
            throw new DaoException("Unable to get quiz permission status", ex);
        }
    }

    /**
     * This method is used to modify all the information of a file in the database
     * @param fileId unique id of a file
     */
    public void deleteFile(String fileId) {
        checkFileExist(fileId);
        try (Connection conn = sql2o.open()) {
            // Delete row from ins_file table
            String sql = "DELETE FROM account_file WHERE fileId = :fileId";
            System.out.println(sql);
            conn.createQuery(sql).addParameter("fileId", fileId)
                    .executeUpdate();

            // Delete row from quiz table
            sql = "DELETE FROM quiz WHERE fileId = :fileId";
            System.out.println(sql);
            conn.createQuery(sql).addParameter("fileId", fileId)
                    .executeUpdate();

            // Delete row from file table
            sql = "DELETE FROM file WHERE fileId = :fileId";
            System.out.println(sql);
            conn.createQuery(sql).addParameter("fileId", fileId)
                    .executeUpdate();

        } catch (Sql2oException ex) {
            throw new DaoException("Unable to delete the file", ex);
        }
    }

    /**
     * This method is used to check whether a file exists in the database
     * @param fileId unique id of a file
     */
    public void checkFileExist(String fileId) {
        try (Connection conn = sql2o.open()) {
            String sql = "SELECT quizPermission from file WHERE fileId = :fileId";
            Boolean quizPermission = conn.createQuery(sql).addParameter("fileId", fileId)
                    .executeAndFetchFirst(Boolean.class);
           if (quizPermission == null){
               throw new DaoException("File not exist");
           }
        } catch (Sql2oException ex) {
            throw new DaoException("database connection error", ex);
        }
    }

    public String getCSS(String fileId) {
        checkFileExist(fileId);
        String css;
        try (Connection conn = sql2o.open()) {
            String sql = "SELECT fileCss FROM file WHERE fileId = :fileId";
            css = conn.createQuery(sql)
                    .addParameter("fileId", fileId)
                    .executeAndFetchFirst(String.class);

            return css;
        } catch (Sql2oException ex) {
            throw new DaoException("Unable to fetch file.", ex);
        }
    }

    public void updateCSS(String fileId, String css) {
        checkFileExist(fileId);
        try (Connection conn = sql2o.open()) {
            String sql = "UPDATE file SET fileCss = :css" +
                    " WHERE fileId = :fileId";

            System.out.println(sql); //console msg
            conn.createQuery(sql)
                    .addParameter("css", css)
                    .addParameter("fileId", fileId)
                    .executeUpdate();
        } catch (Sql2oException ex) {
            throw new DaoException("Unable to update css", ex);
        }
    }
}
