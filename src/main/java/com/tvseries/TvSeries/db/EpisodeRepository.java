package com.tvseries.TvSeries.db;

import com.tvseries.TvSeries.model.Episode;
import com.tvseries.TvSeries.model.TvShow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EpisodeRepository extends JpaRepository<Episode, Long> {

}
