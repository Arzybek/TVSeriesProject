package com.tvseries.TvSeries;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.tvseries.TvSeries.dto.User;
import common.RSA;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.repository.cdi.Eager;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.xml.bind.DatatypeConverter;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@RestController
@ComponentScan("common")
public class AuthController {

    private RSA rsa;
    private final UserRepository userRepository;

    AuthController(UserRepository userRepository, RSA rsa) {

        this.rsa = rsa;
        this.userRepository = userRepository;
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
            var newUser = new User();
            newUser.setName(regInfo[0]);
            newUser.setPasswordHash(passHash);
            var saved = userRepository.save(newUser);
            long id = saved.getId();

            Algorithm algorithm = Algorithm.HMAC256(passHash); // возвращаем токен для последующей аутентификации
            String token = JWT.create()
                    .withIssuer("Issuer")
                    .withClaim("user", regInfo[0])
                    .withClaim("id", id)
                    .sign(algorithm);


            return token;
        } catch (JWTCreationException exception){
            return "";
        }
    }


    public boolean verifyUser(String token)
    {
        // здесь надо выпарсить имя юзера из токена и найти его пароль в базе данных (или можно хранить в базе токен)

        long id = getIdFromJWT(token);
        if (id==-1 || !userRepository.existsById(id))
            return false;
        String passHash;
        String login;
        long idDB = 0;
        try
        {
            passHash = userRepository.getOne(id).getPasswordHash();
            login = userRepository.getOne(id).getLogin();
            idDB = userRepository.getOne(id).getId();
        }
        catch (Exception e)
        {
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
        } catch (JWTVerificationException exception){
            return false;
        }
        return true;
    }

    public long getIdFromJWT(String token)
    {
        System.out.println(token);
        String[] secondPart = token.split("\\.");
        if (secondPart.length<3)
            return -1;
        byte[] decoded = Base64.getUrlDecoder().decode(secondPart[1]);
        String token_parsed = new String(decoded);
        Pattern pattern = Pattern.compile("\"id\":(.+),");
        Matcher matcher = pattern.matcher(token_parsed);
        var aaa = matcher.find();
        String strId = "";
        try{
            strId = matcher.group(1);
        }
        catch (Exception e)
        {
            return -1;
        }

        long id;
        try {
            id = Long.parseLong(strId);
            return id;
        }
        catch (Exception e)
        {
            return -1;
        }
    }


}
