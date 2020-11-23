package com.tvseries.TvSeries.controllers;

import java.util.ArrayList;
import java.util.List;

import com.tvseries.TvSeries.common.ListUtils;
import com.tvseries.TvSeries.db.TvShowRepository;
import com.tvseries.TvSeries.db.UserRepository;
import com.tvseries.TvSeries.model.TvShow;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.*;

@RestController
@ComponentScan("com.tvseries.TvSeries.common")
class TvShowController {

    private final TvShowRepository tvShowrepository;

    private final UserRepository userRepository;

    TvShowController(TvShowRepository TvShowrepository, UserRepository userRepository) {

        this.tvShowrepository = TvShowrepository;
        this.userRepository = userRepository;
    }

    // Aggregate root

    @GetMapping("/tvshows")
    List<TvShow> all(@RequestParam(value = "q", required = false) Integer perPage,
                     @RequestParam(value = "page", required = false) Integer page) {
        List<TvShow> allShows = tvShowrepository.findAll();
        if(perPage!=null){
            if(page==null)
                page = 1;
            ArrayList<List<TvShow>> pages = (ArrayList<List<TvShow>>) ListUtils.partition(allShows, perPage);
            if(page>pages.size() || page<=0)
                return null;
            return pages.get(page-1);
        }
        else return tvShowrepository.findAll();
    }

    @PostMapping("/tvshows")
    TvShow newTvShow(@RequestBody TvShow newEmployee) {
        return tvShowrepository.save(newEmployee);
    }

    // Single item

    @GetMapping("/tvshows/{id}")
    TvShow one(@PathVariable Long id) {
        return tvShowrepository.findById(id)
                .orElseThrow(() -> new RuntimeException(""+id));
    }

    @PutMapping("/tvshows/{id}")
    TvShow replaceTvShow(@RequestBody TvShow tvShow, @PathVariable Long id) {

        return tvShowrepository.findById(id)
                .map(show -> {
                    show.setName(tvShow.getName());
                    show.setCategory(tvShow.getCategory());
                    show.setYear(tvShow.getYear());
                    return tvShowrepository.save(show);
                })
                .orElseGet(() -> {
                    tvShow.setId(id);
                    return tvShowrepository.save(tvShow);
                });
    }

    @DeleteMapping("/tvshows/{id}")
    void deleteTvShow(@PathVariable Long id) {
        tvShowrepository.deleteById(id);
    }

}
