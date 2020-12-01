package com.tvseries.TvSeries.controllers;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.tvseries.TvSeries.common.ListUtils;
import com.tvseries.TvSeries.db.TvShowRepository;
import com.tvseries.TvSeries.db.UserRepository;
import com.tvseries.TvSeries.model.TvShow;
import com.tvseries.TvSeries.model.User;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.*;

import javax.sound.midi.Track;
import java.util.ArrayList;
import java.util.List;

@RestController
@ComponentScan("com.tvseries.TvSeries.common")
@RequestMapping("/user")
public class UserController {

    private final TvShowRepository tvShowrepository;

    private final UserRepository userRepository;

    public UserController(TvShowRepository TvShowrepository, UserRepository userRepository) {

        this.tvShowrepository = TvShowrepository;
        this.userRepository = userRepository;
    }

    // Aggregate root

    @GetMapping("/watching")
    public List<TvShow> watching(@RequestParam(value = "q", required = false) Integer perPage,
                     @RequestParam(value = "page", required = false) Integer page, @CookieValue("auth") String token) {


        if (!verifyUser(token))
            return null;
        long userID = AuthController.getIdFromJWT(token);
        List<TvShow> allShows = userRepository.findById(userID).get().getWatchingShows();
        if(perPage!=null){
            if(page==null)
                page = 1;
            ArrayList<List<TvShow>> pages = (ArrayList<List<TvShow>>) ListUtils.partition(allShows, perPage);
            if(page>pages.size() || page<=0)
                return null;
            return pages.get(page-1);
        }
        else return userRepository.findById(userID).get().getWatchingShows();
    }

    @PostMapping("/deleteWatching")
    public Boolean deleteWatching(@RequestParam(required = true) long showID, @CookieValue("auth") String token)
    {
        if (!verifyUser(token))
            return false;
        long userID = AuthController.getIdFromJWT(token);
        User user = userRepository.findById(userID).get();
        TvShow show = tvShowrepository.findById(showID).get();
        user.deleteWatchingShow(show);
        userRepository.save(user);
        return true;
    }

    @PostMapping("/addWatching")
    public Boolean addWatching(@RequestParam(required = true) long showID, @CookieValue("auth") String token)
    {
        if (!verifyUser(token))
            return false;
        long userID = AuthController.getIdFromJWT(token);
        User user = userRepository.findById(userID).get();
        TvShow show = tvShowrepository.findById(showID).get();
        user.addWatchingShow(show);
        userRepository.save(user);
        return true;
    }


    public boolean verifyUser(String token) {
        // здесь надо выпарсить имя юзера из токена и найти его пароль в базе данных (или можно хранить в базе токен)

        long id = AuthController.getIdFromJWT(token);
        if (id == -1 || !userRepository.existsById(id))
            return false;
        String passHash;
        String login;
        long idDB = 0;
        try {
            var idList = new ArrayList<Long>();
            idList.add(id);
            var users = userRepository.findAllById(idList);
            passHash = users.get(0).getPasswordHash();
            login = users.get(0).getLogin();
            idDB = users.get(0).getId();
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


}
