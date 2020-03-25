package com.testswitch_api.testswitchapi.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class LoginService {

    private long hour = 1000 * 60 * 60;
    private String signer = "9I5vTy13F93oPdDUTUCo";

    public String generateToken() {
        Algorithm algorithm = Algorithm.HMAC256(signer);
        Date date = new Date(hour + System.currentTimeMillis());
        String token = JWT.create()
                .withIssuer("techswitch-ispy")
                .withExpiresAt(date)
                .sign(algorithm);
        return token;
    }

    public boolean validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(signer);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("techswitch-ispy")
                    .build(); //Reusable verifier instance
            DecodedJWT jwt = verifier.verify(token);
            System.out.println(jwt.getPayload());
            return true;
        } catch (Exception exception) {
            //Invalid signature/claims
            return false;
        }
    }
}
