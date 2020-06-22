package api;

import util.JavalinUtil;
import util.Pac4jUtil;

import static io.javalin.plugin.rendering.template.TemplateUtil.model;
import static util.Pac4jUtil.callback;
import static util.JavalinUtil.app;
import static util.Pac4jUtil.githubSecurityHandler;
import static util.Pac4jUtil.pac4jConfig;
import static util.Pac4jUtil.localLogoutHandler;


public class Pac4jApi {
    //callback api
    public static void getCallBack() { app.get("/callback", callback); }
    public static void postCallBack() { app.post("/callback", callback); }

    public static void getLocalLogout() {
        app.get("/logout", localLogoutHandler(pac4jConfig));
    }
}
