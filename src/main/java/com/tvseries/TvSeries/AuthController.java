package com.tvseries.TvSeries;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import common.RSA;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;


@RestController
@ComponentScan("common")
public class AuthController {

    private RSA rsa;
    private final TvShowRepository repository;

    AuthController(TvShowRepository repository, RSA rsa) {

        this.rsa = rsa;
        this.repository = repository;
    }


    @GetMapping("/auth")
    String auth(@CookieValue("auth") String auth)
    {
        boolean isVerified = verifyUser(auth);
        if (isVerified)
            return "hello";
        else
            return "you are not logged in"; // возможно стоит сделать return auth()
    }


    @GetMapping("/register") // здесь мы даем юзеру наш публичный RSA ключ чтобы он зашифровал свои логин - пароль и направляем в register
    String register()
    {
        return rsa.getPublicKey().toString();
    }


    @PostMapping("/register")
    String register(@CookieValue("register") String RSAlogpass)
    {
        String decrypted;
        try {
            //System.out.println(RSAlogpass);
            decrypted = rsa.decrypt(RSAlogpass, rsa.getPrivateKey());
            System.out.println(decrypted);
        } catch (IllegalBlockSizeException | InvalidKeyException | BadPaddingException | NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        var regInfo = decrypted.split(":");
        // здесь нужно добавлять пользователя в базу юзеров
        try {
            Algorithm algorithm = Algorithm.HMAC256(regInfo[1]); // возвращаем токен для последующей аутентификации
            String token = JWT.create()
                    .withIssuer("Issuer")
                    .withClaim("user", regInfo[0])
                    .sign(algorithm);
            return token;
        } catch (JWTCreationException exception){
            return "";
        }
    }


    private boolean verifyUser(String token)
    {
        // здесь надо выпарсить имя юзера из токена и найти его пароль в базе данных (или можно хранить в базе токен)
        String pass = "TABURETH"; // и вставить пароль сюда
        try {
            Algorithm algorithm = Algorithm.HMAC256(pass);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("Issuer")
                    .build();
            DecodedJWT jwt = verifier.verify(token);
        } catch (JWTVerificationException exception){
            return false;
        }
        return true;
    }


}
