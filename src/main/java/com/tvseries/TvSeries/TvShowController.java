package com.tvseries.TvSeries;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.tvseries.TvSeries.dto.TvShow;
import common.RSA;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

@RestController
@ComponentScan("common")
class TvShowController {

    private final TvShowRepository repository;

    private RSA rsa;

    TvShowController(TvShowRepository repository, RSA rsa) {

        this.repository = repository;
        this.rsa = rsa;
    }

    // Aggregate root

    @GetMapping("/tvshows")
    List<TvShow> all() {
        return repository.findAll();
    }

    @PostMapping("/tvshows")
    TvShow newTvShow(@RequestBody TvShow newEmployee) {
        return repository.save(newEmployee);
    }

    // Single item

    @GetMapping("/tvshows/{id}")
    TvShow one(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException(""+id));
    }

    @PutMapping("/tvshows/{id}")
    TvShow replaceTvShow(@RequestBody TvShow tvShow, @PathVariable Long id) {

        return repository.findById(id)
                .map(show -> {
                    show.setName(tvShow.getName());
                    show.setCategory(tvShow.getCategory());
                    show.setYear(tvShow.getYear());
                    return repository.save(show);
                })
                .orElseGet(() -> {
                    tvShow.setId(id);
                    return repository.save(tvShow);
                });
    }

    @DeleteMapping("/tvshows/{id}")
    void deleteTvShow(@PathVariable Long id) {
        repository.deleteById(id);
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
