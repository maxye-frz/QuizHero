package pac4j;

import org.pac4j.core.authorization.authorizer.RequireAnyRoleAuthorizer;
import org.pac4j.core.client.Clients;
import org.pac4j.core.client.direct.AnonymousClient;
import org.pac4j.core.config.Config;
import org.pac4j.core.config.ConfigFactory;
import org.pac4j.core.matching.matcher.PathMatcher;
import org.pac4j.http.client.direct.ParameterClient;
import org.pac4j.http.client.indirect.FormClient;
import org.pac4j.jwt.config.signature.SecretSignatureConfiguration;
import org.pac4j.jwt.credentials.authenticator.JwtAuthenticator;
import org.pac4j.oauth.client.GitHubClient;
import org.pac4j.oauth.profile.github.GitHubProfile;

public class SignInConfigFactory implements ConfigFactory {
    @Override
    public Config build(Object... parameters) {
//        // handle login form
//        FormClient formClient = new FormClient("http://localhost:8080/login-form", trivialUserPassAuthenticator);
//
//        // REST authent with JWT for a token passed in the url as the token parameter
//        ParameterClient parameterClient = new ParameterClient("token", new JwtAuthenticator(new SecretSignatureConfiguration("salt")));
//        parameterClient.setSupportGetRequest(true);
//        parameterClient.setSupportPostRequest(false);

        //github sign in:
        GitHubClient gitHubClient = new GitHubClient("2ee2dcaf4624183da206",
                "4591ff823f08bad9c51e8a44ef9bf8621aa74a35");

        Clients clients = new Clients("http://localhost:7000/callback", gitHubClient);

        Config config = new Config(clients);
        config.setHttpActionAdapter(new HttpActionAdapter());
        return config;
    }
}
