package com.tvseries.TvSeries.db;

import com.tvseries.TvSeries.model.Episode;
import com.tvseries.TvSeries.model.TvShow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class LoadDatabase {

    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    CommandLineRunner initDatabase(TvShowRepository repository, EpisodeRepository epRepository) {

        return args -> {
            var tvShow1 = repository.save(new TvShow("Everybody hates Chris", "comedy", 2005, "1"));
            tvShow1.setDescription("Крис - самый старший ребенок, и воспитание сестры с братом частично ложится на него, " +
                    "к тому же, в отличие от них, ему приходится ходить не в обычную, а привилегированную школу. Несмотря на " +
                    "то, что он сразу становится мишенью своих одноклассников, его доброта и ум помогут найти новых друзей.");
            for(int i=1;i<=10;i++)
            {
                var ep = new Episode(tvShow1.getName(),tvShow1.getId(), i);
                ep.setEpisodeName("a"+i);
                tvShow1.addEpisode(epRepository.save(ep));
            }
            for (int i=0;i<6;i++)
            {
                tvShow1.addReview((long) i,  (i)+" test review");
            }

            var tvShow2 = repository.save(new TvShow("Friends", "comedy", 1994, "2"));
            for(int i=1;i<=18;i++)
            {
                var ep = new Episode(tvShow2.getName(),tvShow2.getId(), i);
                ep.setEpisodeName("b"+i);
                tvShow2.addEpisode(epRepository.save(ep));
            }

            log.info("Preloading " + repository.save(tvShow1));
            log.info("EpisodesCount "+repository.findById(tvShow1.getId()).get().getEpisodes().size());
            log.info("Preloading " + repository.save(tvShow2));
            log.info("Preloading " + repository.save(new TvShow("The Middle", "sitcom,", 2009, "3")));
            log.info("Preloading " + repository.save(new TvShow("New Girl", "sitcom", 2011, "4")));
            log.info("Preloading " + repository.save(new TvShow("Mandolorian", "Sci-Fi", 2019, "6")));

            var naruto = repository.save(new TvShow("Naruto", "anime", 2002, "5"));
                    naruto.setDescription("Легендарное аниме");

            for(int i=1;i<=220;i++)
            {
                var ep = new Episode(naruto.getName(), naruto.getId(), i);
                ep.setEpisodeName("n"+i);
                naruto.addEpisode(epRepository.save(ep));
            }

            log.info("EpisodesCount "+repository.findById(naruto.getId()).get().getEpisodes().size());
            log.info("Preloading " + repository.save(naruto));
        };
    }
}
