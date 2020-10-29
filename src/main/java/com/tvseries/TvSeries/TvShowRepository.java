package com.tvseries.TvSeries;

import com.tvseries.TvSeries.dto.TvShow;
import org.springframework.data.jpa.repository.JpaRepository;


interface TvShowRepository extends JpaRepository<TvShow, Long> {

}
