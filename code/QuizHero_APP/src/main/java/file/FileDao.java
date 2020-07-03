package file;

import exception.DaoException;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * FileDao interface defines methods related to the file table
 * e.g. store file, get file, delete file, change quiz and file permission
 * @author Ziming Chen, Nanxi Ye, Chenghao Sun
 * @version 1.0
 */
public class FileDao {
    private Sql2o sql2o;

    public FileDao(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    /**
     * This method is used to store the file stream in the database
     * @param file an instance of File class
     */
    public void storeFile(File file) {
        try (Connection conn = sql2o.open()) {
            String sql = "insert into file values (:fileId, :fileName, :filePermission, :quizPermission, :fileContent)";
            conn.createQuery(sql)
                    .addParameter("fileId", file.getFileId())
                    .addParameter("fileName", file.getFileName())
                    .addParameter("filePermission", file.getFilePermission())
                    .addParameter("quizPermission", file.getQuizPermission())
                    .addParameter("fileContent", file.getFileContent())
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

    /**
     * This method is used to get the file stream from the database
     * @param fileId unique id of a file
     * @return InputStream of the file content
     */
    public InputStream getFileContent(String fileId) {
        checkFileExist(fileId);
        ByteArrayInputStream byteStream;
        try (Connection conn = sql2o.open()) {
            String sql = "SELECT fileContent FROM file WHERE fileId = :fileId";
            byteStream = conn.createQuery(sql)
                    .addParameter("fileId", fileId)
                    .executeAndFetchFirst(ByteArrayInputStream.class);

            return byteStream;
        } catch (Sql2oException ex) {
            throw new DaoException("Unable to fetch file.", ex);
        }
    }

    /**
     * This method is used to update the file
     * @param fileId a string of fileId
     * @param fileName a string of fileName
     * @param fileContent an inputStream of fileContent
     */
    public void updateFile(String fileId, String fileName, InputStream fileContent) {
        checkFileExist(fileId);
        try (Connection conn = sql2o.open()) {
            String sql = "UPDATE file SET fileName = :valFileName," +
                    " fileContent = :valFileContent" +
                    " WHERE fileId = :valFileId";

            System.out.println(sql); //console msg
            conn.createQuery(sql)
                    .addParameter("valFileId", fileId)
                    .addParameter("valFileName", fileName)
                    .addParameter("valFileContent", fileContent)
                    .executeUpdate();
        } catch (Sql2oException ex) {
            throw new DaoException("Unable to update file", ex);
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
}
