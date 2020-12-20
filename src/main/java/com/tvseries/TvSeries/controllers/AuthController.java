package com.tvseries.TvSeries.controllers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.tvseries.TvSeries.db.UserRepository;
import com.tvseries.TvSeries.db.UserService;
import com.tvseries.TvSeries.model.User;
import com.tvseries.TvSeries.common.RSA;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.xml.bind.DatatypeConverter;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@RestController
public class AuthController {

    private RSA rsa;
    private final UserService userService;

    public AuthController(UserService userService, RSA rsa) {
        this.rsa = rsa;
        this.userService = userService;
    }


    @GetMapping("/register")
        // здесь мы даем юзеру наш публичный RSA ключ чтобы он зашифровал свои логин - пароль и направляем в register
    String register() {
        return rsa.getPublicKey().toString();
    }

    @PostMapping("/register/insecure")
    public String insecureRegister(@CookieValue("register") String RSAlogpass) {
        System.out.println("insecure-register: " + RSAlogpass);
        var regInfo = RSAlogpass.split(":");
        var login = regInfo[0];
        var password = regInfo[1];

        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
        md.update(password.getBytes());
        byte[] digest = md.digest();
        String passHash = DatatypeConverter.printHexBinary(digest).toUpperCase();
        try {
            var newUser = new User(login, login, passHash);
            long id;
            if (userService.existsByLogin(login)) {
                if (userService.existsByLoginPassHash(login, passHash))
                {
                    var user = userService.getByLoginPasshash(login, passHash);
                    id = user.getId();  // пользователь существует и пароль верный
                }
                else
                {
                    System.out.println("wrong password???"); // пользователь существует но пароль неверный
                    return "";  // вернули пустую куку, надо на фронте бы интеллектуально обработать это
                }
            }
            else
            {
                var saved = userService.save(newUser);  // пользователя не существовало, создаем
                id = saved.getId();
            }

            Algorithm algorithm = Algorithm.HMAC256(passHash); // возвращаем токен для последующей аутентификации
            String token = JWT.create()
                    .withIssuer("Issuer")
                    .withClaim("user", login)
                    .withClaim("id", id)
                    .sign(algorithm);

            System.out.println(getIdFromJWT(token));
            return token;
        } catch (JWTCreationException exception) {
            return "";
        }
    }


    @PostMapping("/register")
    public String register(@CookieValue("register") String RSAlogpass) {
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
        var login = regInfo[0];
        var password = regInfo[1];

        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
        md.update(password.getBytes());
        byte[] digest = md.digest();
        String passHash = DatatypeConverter.printHexBinary(digest).toUpperCase();
        try {
            var newUser = new User(regInfo[0], regInfo[0], passHash);
            //newUser.setName(regInfo[0]);
            //newUser.setPasswordHash(passHash);
            var saved = userService.save(newUser);
            long id = saved.getId();

            Algorithm algorithm = Algorithm.HMAC256(passHash); // возвращаем токен для последующей аутентификации
            String token = JWT.create()
                    .withIssuer("Issuer")
                    .withClaim("user", regInfo[0])
                    .withClaim("id", id)
                    .sign(algorithm);

            System.out.println(getIdFromJWT(token));
            return token;
        } catch (JWTCreationException exception) {
            return "";
        }
    }


    @GetMapping("/profile")
    User getProfile(@CookieValue("auth") String token) {
        System.out.println(token);
        long id = getIdFromJWT(token);
        System.out.println(id);
        if (id == -1 || !verifyUser(token))
            return new User("", "anonymous", "anonymous");
        return userService.getUser(id);

    }


    public boolean verifyUser(String token) {
        long id = getIdFromJWT(token);
        if (id == -1 || !userService.existsById(id))
            return false;
        String passHash;
        String login;
        long idDB = 0;
        try {
            var user = userService.getUser(id);
            passHash = user.getPasswordHash();
            login = user.getLogin();
            idDB = user.getId();
        } catch (Exception e) {
            return false;
        }
        try {
            Algorithm algorithm = Algorithm.HMAC256(passHash);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("Issuer")
                    .withClaim("user", login)
                    .withClaim("id", idDB)
                    .build();
            DecodedJWT jwt = verifier.verify(token);
        } catch (JWTVerificationException exception) {
            return false;
        }
        return true;
    }

    public static long getIdFromJWT(String token) {
        String[] secondPart = token.split("\\.");
        if (secondPart.length < 3)
            return -1;
        byte[] decoded = Base64.getUrlDecoder().decode(secondPart[1]);
        String token_parsed = new String(decoded);
        token_parsed = token_parsed.replaceAll("\\\\", "");// убрали слэши, с ними не парсится в JSONObject
        JSONObject obj = new JSONObject(token_parsed);
        try {
            Long id = obj.getLong("id");
            return id;
        }
        catch (JSONException e)
        {
            return -1;
        }
    }


    @ExceptionHandler({ MethodArgumentTypeMismatchException.class})
    public void handleException(Exception ex) {
        System.out.println(ex.getStackTrace().toString());
        //
    }


}
