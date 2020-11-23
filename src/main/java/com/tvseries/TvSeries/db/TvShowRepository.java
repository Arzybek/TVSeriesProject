package com.tvseries.TvSeries.db;

import com.tvseries.TvSeries.model.TvShow;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TvShowRepository extends JpaRepository<TvShow, Long> {

}
