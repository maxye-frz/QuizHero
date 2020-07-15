//package util;
//
//import org.pac4j.core.config.Config;
//import org.pac4j.javalin.CallbackHandler;
//import org.pac4j.javalin.LogoutHandler;
//import org.pac4j.javalin.SecurityHandler;
//import pac4j.SignInConfigFactory;
//
//public class Pac4jUtil {
////    private static final Logger logger = LoggerFactory.getLogger(JavalinUtil.class);  // initialize logger
//    public static Config pac4jConfig = new SignInConfigFactory().build(); //pc4j sign in config, used to initialize handlers and clients
//    public static CallbackHandler callback = new CallbackHandler(pac4jConfig, null, false);
//    public static SecurityHandler githubSecurityHandler = new SecurityHandler(pac4jConfig, "GitHubClient");
//    public static LogoutHandler localLogoutHandler(Config config) {
//        LogoutHandler localLogout = new LogoutHandler(config, "/?defaulturlafterlogout");
//        localLogout.destroySession = true;
//        return localLogout;
//    }
//}
