package com.tvseries.TvSeries.controllers;

import java.io.IOException;
import java.util.*;

import com.tvseries.TvSeries.common.ListUtils;
import com.tvseries.TvSeries.db.TvShowRepository;
import com.tvseries.TvSeries.db.TvShowService;
import com.tvseries.TvSeries.model.TvShow;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.util.StreamUtils;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletResponse;

@RestController
@ComponentScan("com.tvseries.TvSeries.common")
class TvShowController {

    private final TvShowService tvShowService;

    TvShowController(TvShowService tvShowService) {
        this.tvShowService = tvShowService;
    }

    // Aggregate root

    @GetMapping("/tvshows")
    List<List<TvShow>> all(@RequestParam(value = "q", required = false) Integer perPage,
                     @RequestParam(value = "page", required = false) Integer page) {
        List<TvShow> allShows = tvShowService.findAllExceptCustom();
        ArrayList<List<TvShow>> res = new ArrayList<List<TvShow>>();
        if(perPage!=null){
            if(page==null) {
                ArrayList<List<TvShow>> pages = (ArrayList<List<TvShow>>)ListUtils.partition(allShows, perPage);
                return pages;
            }
            ArrayList<TvShow> pages = (ArrayList<TvShow>) tvShowService.findAllExceptCustom(page-1, perPage);
            res.add(pages);
            return res;
        }
        res.add(allShows);
        return res;
    }

    @GetMapping("/tvshows/search")
    List<List<TvShow>> search (@RequestParam(value = "q", required = false) String query) {
        List<TvShow> allShows = tvShowService.searchByName(query);
        ArrayList<List<TvShow>> res = new ArrayList<List<TvShow>>();
        res.add(allShows);
        return res;
    }

    @PostMapping("/tvshows")
    TvShow newTvShow(@RequestBody TvShow newEmployee) {
        return tvShowService.create(newEmployee);
    }

    // Single item

    @GetMapping("/tvshows/{id}")
    TvShow one(@PathVariable Long id) {
        return tvShowService.read(id);
    }

    @GetMapping(value = "/tvshows/image/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    void getImage(@PathVariable Long id, HttpServletResponse response) throws IOException {
        var imgFile = new ClassPathResource("image/"+id.toString()+".jpg");
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        StreamUtils.copy(imgFile.getInputStream(), response.getOutputStream());
    }

    @PutMapping("/tvshows/{id}")
    TvShow replaceTvShow(@RequestBody TvShow tvShow) {
        return tvShowService.update(tvShow);
    }

    @DeleteMapping("/tvshows/{id}")
    void deleteTvShow(@PathVariable Long id) {
        tvShowService.delete(id);
    }

    @GetMapping("/tvshows/rating")
    Float getRating(@RequestParam(required = true) long showID)
    {
        var show = tvShowService.read(showID);
        return show.getRating();
    }


    @GetMapping("/tvshows/randomReviews")
    ArrayList<String> getRandomReviews(@RequestParam(required = true) int amount, @RequestParam(required = true) long showID)
    {
        var show = tvShowService.read(showID);
        return show.getNRandomReviews(amount);
    }


    @ExceptionHandler({ MethodArgumentTypeMismatchException.class})
    public void handleException(Exception ex) {
        System.out.println(ex.getStackTrace().toString());
        //
    }

}
