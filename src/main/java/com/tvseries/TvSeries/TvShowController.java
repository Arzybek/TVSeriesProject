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

    TvShowController(TvShowRepository repository, RSA rsa) {

        this.repository = repository;
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

}
