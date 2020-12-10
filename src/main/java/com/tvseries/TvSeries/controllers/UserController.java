package com.tvseries.TvSeries.controllers;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.tvseries.TvSeries.common.ListUtils;
import com.tvseries.TvSeries.db.*;
import com.tvseries.TvSeries.model.Episode;
import com.tvseries.TvSeries.model.TvShow;
import com.tvseries.TvSeries.model.User;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.*;

import javax.sound.midi.Track;
import java.util.*;

@RestController
@ComponentScan("com.tvseries.TvSeries.common")
@RequestMapping("/user")
public class UserController {

    private final TvShowService tvShowService;

    private final UserService userService;

    private final EpisodeService episodeService;

    public UserController(UserService userService, TvShowService tvShowService, EpisodeService episodeService) {

        this.tvShowService = tvShowService;
        this.userService = userService;
        this.episodeService = episodeService;
    }

    // Aggregate root

    @GetMapping("/watching")
    public List<TvShow> watching(@RequestParam(value = "q", required = false) Integer perPage,
                     @RequestParam(value = "page", required = false) Integer page, @CookieValue("auth") String token) {


        if (!verifyUser(token))
            return null;
        long userID = AuthController.getIdFromJWT(token);
        ArrayList<TvShow> allShows = new ArrayList<>();
        Set<Long> allShowsID = userService.getUser(userID).getWatchingShowsIDs();
        for (Long showID:allShowsID) {
            allShows.add(tvShowService.read(showID));
        }
        if(perPage!=null){
            if(page==null)
                page = 1;
            ArrayList<List<TvShow>> pages = (ArrayList<List<TvShow>>) ListUtils.partition(allShows, perPage);
            if(page>pages.size() || page<=0)
                return null;
            return pages.get(page-1);
        }
        else return allShows;
    }

    @PostMapping("/deleteWatching")
    public Boolean deleteWatching(@RequestParam(required = true) long showID, @CookieValue("auth") String token)
    {
        if (!verifyUser(token))
            return false;
        long userID = AuthController.getIdFromJWT(token);
        User user = userService.getUser(userID);
        user.deleteWatchingShow(showID);
        userService.save(user);
        return true;
    }

    @PostMapping("/addWatching")
    public Boolean addWatching(@RequestParam(required = true) long showID, @CookieValue("auth") String token)
    {
        System.out.println("watching show request "+showID);
        if (!verifyUser(token)) {
            System.out.println("watching show request: not verified user");
            return false;
        }
        long userID = AuthController.getIdFromJWT(token);
        User user = userService.getUser(userID);
        TvShow show = tvShowService.read(showID);
        user.addWatchingShow(show);
        userService.update(user);
        System.out.println("added");
        for (Long id:userService.getUser(userID).getWatchingShowsIDs()) {
            System.out.println(id);
        }
        return true;
    }


    @PostMapping("/addUserWatchingShow")
    public Boolean addUserWatchingShow(@CookieValue("auth") String token, @RequestParam String info)
    {
        System.out.println("addUserWatchingShow request ");
        //System.out.println(data);
        if (!verifyUser(token)) {
            System.out.println("addUserWatchingShow request: not verified user");
            return false;
        }
        long userID = AuthController.getIdFromJWT(token);
        User user = userService.getUser(userID);
        TvShow show = new TvShow();
        info = info.substring(1);
        info = info.substring(0, info.length()-1);
        info = info.replace("\"", "");
        var splitted  = info.split(", ");//  очень говнокод, нужно поискать норм либу
        for (String prop:splitted) {
            if(prop.startsWith("showName:"))
            {
                show.setName(prop.split(": ")[1]);
            }
            if (prop.startsWith("episodesCount:"))
            {
                int counter = Integer.parseInt(prop.split(": ")[1]);
                for (int i=0;i<counter;i++)
                {
                    Episode ep = new Episode();
                    ep.setIndex(i);
                    show.addEpisode(ep);
                    episodeService.create(ep);//возможно не нужно
                }
            }
        }
        show.setImgLink("100500");
        show.setCategory("Added by user");
        //show.setName(data.get("showName"));
        //int counter = Integer.parseInt(data.get("episodesCount"));
        tvShowService.create(show); // нужно сделать отдельную таблицу
        user.addWatchingShow(show);
        userService.update(user);
        System.out.println("added");
        for (Long id:userService.getUser(userID).getWatchingShowsIDs()) {
            System.out.println(id);
        }
        return true;
    }




    @PostMapping("/watchEpisode")
    public Boolean watchEpisode(@RequestParam(required = true) long showID, @RequestParam(required = true) long epID,  @CookieValue("auth") String token)
    {
        if (!verifyUser(token))
            return false;
        System.out.println("watch episode request"+showID+epID);
        long userID = AuthController.getIdFromJWT(token);
        User user = userService.getUser(userID);
        TvShow show = tvShowService.read(showID);
        Episode ep = episodeService.getOne(epID);
        user.watchEpisode(showID, epID);
        userService.save(user);
        return true;
    }


    @PostMapping("/unwatchEpisode")
    public Boolean unwatchEpisode(@RequestParam(required = true) long showID, @RequestParam(required = true) long epID,  @CookieValue("auth") String token)
    {
        if (!verifyUser(token))
            return false;
        long userID = AuthController.getIdFromJWT(token);
        User user = userService.getUser(userID);
        user.unwatchEpisode(showID, epID);
        userService.save(user);
        return true;
    }


    @GetMapping("/isWatching")
    public Boolean isWatchingShow(@RequestParam(required = true) long showID, @CookieValue("auth") String token)
    {
        System.out.println("isWatchingShow request: "+showID);
        if (!verifyUser(token)) {
            System.out.println("isWatchingShow request: not verified user");
            return false;
        }
        long userID = AuthController.getIdFromJWT(token);
        User user = userService.getUser(userID);
        System.out.println(user.getWatchingShowsIDs().contains(showID));
        return user.getWatchingShowsIDs().contains(showID);
    }


    @GetMapping("/isWatchedEpisode")
    public Boolean isWatchedEpisode(@RequestParam(required = true) long showID,@RequestParam(required = true) long episodeID, @CookieValue("auth") String token)
    {
        if (!verifyUser(token))
            return false;
        long userID = AuthController.getIdFromJWT(token);
        User user = userService.getUser(userID);
        return user.isWatchedEpisode(showID, episodeID);
    }

    @GetMapping("/watchedEpisodes")
    public Boolean[] getWatchedEpisodes(@RequestParam(required = true) long showID, @CookieValue("auth") String token)
    {
        if (!verifyUser(token))
            return null;
        long userID = AuthController.getIdFromJWT(token);
        User user = userService.getUser(userID);
        return user.getWatchedEpisodes(showID);
    }




    public boolean verifyUser(String token) {
        // здесь надо выпарсить имя юзера из токена и найти его пароль в базе данных (или можно хранить в базе токен)

        long id = AuthController.getIdFromJWT(token);
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


}
