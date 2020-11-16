package com.tvseries.TvSeries;

import com.tvseries.TvSeries.dto.TvShow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class LoadDatabase {

    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    CommandLineRunner initDatabase(TvShowRepository repository) {

        return args -> {
            log.info("Preloading " + repository.save(new TvShow("Everybody hates Chris", "comedy", 2005)));
            log.info("Preloading " + repository.save(new TvShow("Friends", "comedy", 1994)));
            log.info("Preloading " + repository.save(new TvShow("The Middle", "sitcom,", 2009)));
            log.info("Preloading " + repository.save(new TvShow("New Girl", "sitcom", 2011)));
        };
    }
}
