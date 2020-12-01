package com.tvseries.TvSeries.controllers;


import com.tvseries.TvSeries.common.ListUtils;
import com.tvseries.TvSeries.db.TvShowRepository;
import com.tvseries.TvSeries.db.UserRepository;
import com.tvseries.TvSeries.model.TvShow;
import com.tvseries.TvSeries.model.User;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@ComponentScan("com.tvseries.TvSeries.common")
@RequestMapping("/user")
public class UserController {

    private final TvShowRepository tvShowrepository;

    private final UserRepository userRepository;

    UserController(TvShowRepository TvShowrepository, UserRepository userRepository) {

        this.tvShowrepository = TvShowrepository;
        this.userRepository = userRepository;
    }

    // Aggregate root

    @GetMapping("/watching")
    List<TvShow> watching(@RequestParam(value = "q", required = false) Integer perPage,
                     @RequestParam(value = "page", required = false) Integer page, @CookieValue("auth") String token) {


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
    void deleteWatching(@RequestParam(required = true) long showID, @CookieValue("auth") String token)
    {
        long userID = AuthController.getIdFromJWT(token);
        User user = userRepository.findById(userID).get();
        TvShow show = tvShowrepository.findById(showID).get();
        user.deleteWatchingShow(show);
        userRepository.save(user);
    }

    @PostMapping("/addWatching")
    void addWatching(@RequestParam(required = true) long showID, @CookieValue("auth") String token)
    {
        long userID = AuthController.getIdFromJWT(token);
        User user = userRepository.findById(userID).get();
        TvShow show = tvShowrepository.findById(showID).get();
        user.addWatchingShow(show);
        userRepository.save(user);
    }


}
