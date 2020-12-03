package com.tvseries.TvSeries.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.tvseries.TvSeries.common.ListUtils;
import com.tvseries.TvSeries.db.TvShowRepository;
import com.tvseries.TvSeries.db.TvShowService;
import com.tvseries.TvSeries.model.TvShow;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.util.StreamUtils;
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
    List<TvShow> all(@RequestParam(value = "q", required = false) Integer perPage,
                     @RequestParam(value = "page", required = false) Integer page) {
        List<TvShow> allShows = tvShowService.findAll();
        if(perPage!=null){
            if(page==null)
                page = 1;
            ArrayList<TvShow> pages = (ArrayList<TvShow>) tvShowService.findAll(page-1, perPage);
            return pages;
        }
        else return tvShowService.findAll();
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

}
