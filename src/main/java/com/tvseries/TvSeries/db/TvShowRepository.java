package com.tvseries.TvSeries.db;

import com.tvseries.TvSeries.model.TvShow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;


public interface TvShowRepository extends JpaRepository<TvShow, Long> {
    List<TvShow> findTvShowByName(String name);
}
