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


    @GetMapping("/auth")
    public String auth(@CookieValue("auth") String auth) {
        boolean isVerified = verifyUser(auth);
        if (isVerified)
            return "hello";
        else
            return "you are not logged in"; // возможно стоит сделать return auth()
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
            //newUser.setName(regInfo[0]);
            //newUser.setPasswordHash(passHash);
            long id;
            var exists = userService.existsByLogPass(login, passHash);
            if (exists) {
                var user = userService.getByLogPass(login, passHash);
                id = user.getId();
            }
            else
            {
                var saved = userService.save(newUser);
                id = saved.getId();
            }

            Algorithm algorithm = Algorithm.HMAC256(passHash); // возвращаем токен для последующей аутентификации
            String token = JWT.create()
                    .withIssuer("Issuer")
                    .withClaim("user", regInfo[0])
                    .withClaim("id", id)
                    .sign(algorithm);


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
        var user = userService.getUser(id);
        return userService.getUser(id);

    }


    public boolean verifyUser(String token) {
        // здесь надо выпарсить имя юзера из токена и найти его пароль в базе данных (или можно хранить в базе токен)

        long id = getIdFromJWT(token);
        if (id == -1 || !userService.existsById(id))
            return false;
        String passHash;
        String login;
        long idDB = 0;
        try {
            // var idList = new ArrayList<Long>();
            //idList.add(id);
            //var users = userRepository.findAllById(idList);
            var user = userService.getUser(id);
            passHash = user.getPasswordHash();
            login = user.getLogin();
            idDB = user.getId();
            //passHash = users.get(0).getPasswordHash();
            //login = users.get(0).getLogin();
            //idDB = users.get(0).getId();
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
        //System.out.println("token from getIdFromJWT "+token);
        String[] secondPart = token.split("\\.");
        if (secondPart.length < 3)
            return -1;
        byte[] decoded = Base64.getUrlDecoder().decode(secondPart[1]);
        String token_parsed = new String(decoded);
        Pattern pattern = Pattern.compile("\"id\":(.+),");
        Matcher matcher = pattern.matcher(token_parsed);
        var aaa = matcher.find();
        String strId = "";
        try {
            strId = matcher.group(1);
        } catch (Exception e) {
            return -1;
        }

        long id;
        try {
            id = Long.parseLong(strId);
            return id;
        } catch (Exception e) {
            return -1;
        }
    }


    @ExceptionHandler({ MethodArgumentTypeMismatchException.class})
    public void handleException(Exception ex) {
        System.out.println(ex.getStackTrace().toString());
        //
    }


}
