package user;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import javalinjwt.JWTGenerator;
import javalinjwt.JWTProvider;

public class userJWTProvider {
    static JWTProvider createHMAC512() {
        JWTGenerator<User> generator = (user, alg) -> {
            JWTCreator.Builder token = JWT.create()
                    .withClaim("userId", user.getUserId())
                    .withClaim("name", user.getName())
                    .withClaim("email", user.getEmail())
                    .withClaim("repoId", user.getRepoId())
                    .withClaim("githubId", user.getGithubId())
                    .withClaim("salt", user.getSalt());
            return token.sign(alg);
        };

        Algorithm algorithm = Algorithm.HMAC256("salt");
        JWTVerifier verifier = JWT.require(algorithm).build();

        return new JWTProvider(algorithm, generator, verifier);
    }
}
