package com.tvseries.TvSeries;

import java.util.List;

import com.tvseries.TvSeries.dto.TvShow;
import common.RSA;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.*;

@RestController
@ComponentScan("common")
class TvShowController {

    private final TvShowRepository tvShowrepository;

    private final UserRepository userRepository;

    TvShowController(TvShowRepository TvShowrepository, UserRepository userRepository) {

        this.tvShowrepository = TvShowrepository;
        this.userRepository = userRepository;
    }

    // Aggregate root

    //@GetMapping("/tvshows")
    //List<TvShow> all() {
    //    return tvShowrepository.findAll();
    //}

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

    @GetMapping("/tvshows")
    List<TvShow> getRange(@RequestParam int range, @RequestParam int page)
    {
        return tvShowrepository.findAll().subList(range*page, range*(page+1));
    }

}
