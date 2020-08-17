package file;

import java.io.InputStream;
import java.util.Objects;
import java.util.UUID;


/**
 * File class is the data model of a File.
 * @author QuizHero team @JHU OOSE spring20
 * @version 1.3
 */
public class File {
    private int userId; // id of the instructor who uploads the file
    private String fileId; // unique file id of the file
    private String fileName; // name of the file
    private Boolean filePermission; // permission control of access to viewing the file
    private Boolean quizPermission; // permission control of access to all quizzes in the file
    private InputStream css; // css string of the file
    private String owner; // owner of the file on github
    private String repo; // repo of the file on github
    private String path; // path of the file on github
    private String sha; // sha (commit id) of the file on github
//    private String date; // date of the file created
//    private Integer size; // size of the file

    /**
     * This method is the constructor of the class
     *
     * @param instructorId id for instructor
     * @param fileName name of the file
     */
    public File(int instructorId, String fileName) {
        this.userId = instructorId;
        this.fileId = generateUniqueFileId();
        this.fileName = fileName;
        this.filePermission = false; // default false;
        this.quizPermission = false; // default false;
    }

    /**
     * This method is the constructor of the class
     *
     * @param instructorId id for instructor
     * @param fileId string for fileId
     * @param fileName name of the file
     */
    public File(int instructorId, String fileId, String fileName) {
        this.userId = instructorId;
        this.fileId = fileId;
        this.fileName = fileName;
        this.filePermission = false; // default false;
        this.quizPermission = false; // default false;
    }

    /**
     * This method is the constructor of the class
     *
     * @param instructorId id for instructor
     * @param fileId id of the file
     * @param fileName name of the file
     * @param fileAccess access permission of the file
     * @param quizAccess access permission of the quiz
     */
    public File(int instructorId, String fileId, String fileName, Boolean fileAccess, Boolean quizAccess, InputStream css) {
        this.userId = instructorId;
        this.fileId = fileId;
        this.fileName = fileName;
        this.filePermission = fileAccess;
        this.quizPermission = quizAccess;
        this.css = css;
    }

    /**
     * This method is used to generated the UUID of the file
     * @return unique file id
     */
    private String generateUniqueFileId() {
        return UUID.randomUUID().toString();
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Boolean getFilePermission() {
        return filePermission;
    }

    public void setFilePermission(Boolean filePermission) {
        this.filePermission = filePermission;
    }

    public Boolean getQuizPermission() {
        return quizPermission;
    }

    public void setQuizPermission(Boolean quizPermission) {
        this.quizPermission = quizPermission;
    }

    public InputStream getCss() {
        return css;
    }

    public void setCss(InputStream css) {
        this.css = css;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getRepo() {
        return repo;
    }

    public void setRepo(String repo) {
        this.repo = repo;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSha() {
        return sha;
    }

    public void setSha(String sha) {
        this.sha = sha;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        File file = (File) o;
        return userId == file.userId &&
                Objects.equals(fileId, file.fileId) &&
                Objects.equals(fileName, file.fileName) &&
                Objects.equals(filePermission, file.filePermission) &&
                Objects.equals(quizPermission, file.quizPermission) &&
                Objects.equals(css, file.css) &&
                Objects.equals(owner, file.owner) &&
                Objects.equals(repo, file.repo) &&
                Objects.equals(path, file.path) &&
                Objects.equals(sha, file.sha);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, fileId, fileName, filePermission, quizPermission, css, owner, repo, path, sha);
    }

    @Override
    public String toString() {
        return "File{" +
                "userId=" + userId +
                ", fileId='" + fileId + '\'' +
                ", fileName='" + fileName + '\'' +
                ", filePermission=" + filePermission +
                ", quizPermission=" + quizPermission +
                ", css=" + css +
                ", owner='" + owner + '\'' +
                ", repo='" + repo + '\'' +
                ", path='" + path + '\'' +
                ", sha='" + sha + '\'' +
                '}';
    }

    //
//    /**
//     * This method is used to get the private variable value
//     * @return named instructorId
//     */
//    public Integer getUserId() {
//        return userId;
//    }
//
//    /**
//     * This method is used to get the private variable value
//     * @return named fileId
//     */
//    public String getFileId() {
//        return fileId;
//    }
//
//    /**
//     * This method is used to get the private variable value
//     * @return named fileName
//     */
//    public String getFileName() {
//        return fileName;
//    }
//
//    /**
//     * This method is used to get the private variable value
//     * @return named filePermission
//     */
//    public Boolean getFilePermission() {
//        return filePermission;
//    }
//
//    /**
//     * This method is used to get the private variable value
//     * @return named quizPermission
//     */
//    public Boolean getQuizPermission() {
//        return quizPermission;
//    }
//
//    /**
//     * This method is used to get the private variable value
//     * @return named fileContent
//     */
//    public InputStream getFileContent() {
//        return fileContent;
//    }
//
//    /**
//     * This method is used to get the css content of the file in inputStream
//     * @return inputStream css
//     */
//    public InputStream getCss() {return css;}
//
//
//    /**
//     * This method overrides the toString method of the class
//     * to display specific content of the class information
//     */
//    @Override
//    public String toString() {
//        return "File{" +
//                "fileId='" + fileId + '\'' +
//                ", fileName='" + fileName + '\'' +
//                ", filePermission=" + filePermission +
//                ", quizPermission=" + quizPermission +
//                ", fileContent=" + fileContent +
//                ", css=" + css +
//                '}';
//    }
//
//    /**
//     * This method overrides the equals method of the class
//     * to implement specific functionality of the equals function
//     * to the class
//     */
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        File file = (File) o;
//        return Objects.equals(getFileId(), file.getFileId()) &&
//                Objects.equals(getFileName(), file.getFileName()) &&
//                Objects.equals(getFilePermission(), file.getFilePermission()) &&
//                Objects.equals(getQuizPermission(), file.getQuizPermission()) &&
//                Objects.equals(getFileContent(), file.getFileContent()) &&
//                Objects.equals(getCss(), file.getCss());
//    }
//
//    /**
//     * This method overrides the hashCode method of the class
//     * to implement specific functionality of the hashCode function
//     * to the class
//     */
//    @Override
//    public int hashCode() {
//        return Objects.hash(getFileId(), getFileName(), getFilePermission(), getQuizPermission(), getFileContent(), getCss());
//    }
}
