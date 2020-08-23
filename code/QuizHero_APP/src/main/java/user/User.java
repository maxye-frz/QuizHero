package user;

import java.util.Objects;
import java.util.UUID;


/**
 * User class is the data model of a user.
 *
 * @author QuizHero team @JHU OOSE spring20
 * @version 1.3
 */
public class User {
    private Integer userId; // serial id of registered user
    private String name; // user name
    private String email; // user email
    private String pswd; // user password hashed by frontend
    private String salt; // salt of user for password hashing
    private String repoId; // UUID of the private repo created under organization on github; for non-github login user only
    private String githubId; // GitHub ID for github login user only

    /**
     * Constructor of user without github login
     *
     * @param name name of the user
     * @param email email of the user
     * @param pswd password of the user
     * @param salt salt of the user
     */
    public User(String name, String email, String pswd, String salt) {
        this.name = name;
        this.email = email;
        this.pswd = pswd;
        this.salt = salt;
        this.repoId = UUID.randomUUID().toString();
    }

    /**
     * Constructor of user with github login
     *
     * @param name name of the user from its github profile
     * @param githubId GitHub ID of the user from its github profile
     */
    public User(String name, String githubId) {
        this.name = name;
        this.githubId = githubId;
        this.repoId = UUID.randomUUID().toString();
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPswd() {
        return pswd;
    }

    public void setPswd(String pswd) {
        this.pswd = pswd;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getRepoId() {
        return repoId;
    }

    public void setRepoId(String repoId) {
        this.repoId = repoId;
    }

    public String getGithubId() {
        return githubId;
    }

    public void setGithubId(String githubId) {
        this.githubId = githubId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userId, user.userId) &&
                Objects.equals(name, user.name) &&
                Objects.equals(email, user.email) &&
                Objects.equals(pswd, user.pswd) &&
                Objects.equals(salt, user.salt) &&
                Objects.equals(repoId, user.repoId) &&
                Objects.equals(githubId, user.githubId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, name, email, pswd, salt, repoId, githubId);
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", pswd='" + pswd + '\'' +
                ", repoId='" + repoId + '\'' +
                ", githubId='" + githubId + '\'' +
                ", salt='" + salt + '\'' +
                '}';
    }

//    public void createRepo() throws IOException {
//        String accessToken = GithubUtil.getPersonalAccessToken();
//        String org = GithubUtil.getOrganizationName();
//        String repoName = this.getRepoId();
//        CloseableHttpClient httpclient = HttpClients.createDefault();
//        try{
//            URI postUri = new URIBuilder()
//                    .setScheme("https")
//                    .setHost("api.github.com")
//                    .setPath("/orgs/" + org + "/repos")
//                    .build();
//            HttpPost httppost = new HttpPost(postUri);
//            String inputJson = "{\n" +
//                    "\"name\": \"" + repoName + "\",\n" +
//                    "\"private\": \"" + true + "\"\n" +
//                    "}";
//            System.out.println(inputJson);
//            StringEntity stringEntity = new StringEntity(inputJson);
//            httppost.setEntity(stringEntity);
//            httppost.setHeader("AUTHORIZATION", "token " + accessToken);
//            httppost.setHeader("Accept", "application/vnd.github.v3+json");
//            HttpResponse response = httpclient.execute(httppost);
//            HttpEntity responseEntity = response.getEntity();
//            String responseString = EntityUtils.toString(responseEntity);
//            System.out.println(responseString);
//        } catch (URISyntaxException | UnsupportedEncodingException e) {
//            e.printStackTrace();
//        } catch (ClientProtocolException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            httpclient.close();
//        }
//    }
}
