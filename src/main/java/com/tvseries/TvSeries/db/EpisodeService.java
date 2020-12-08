package com.tvseries.TvSeries.db;

import com.tvseries.TvSeries.model.Episode;
import com.tvseries.TvSeries.model.TvShow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;


@Repository
public class EpisodeService {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private EpisodeRepository epRepository;

    public Episode create(Episode episode) {
        return epRepository.save(episode);
    }

    public Episode read(Long id) {
        return epRepository.findById(id).orElseThrow(() -> new RuntimeException(""+id));
    }


    public void delete(Long id) {
        epRepository.deleteById(id);
    }

    public List<Episode> findAll(){
        return epRepository.findAll();
    }

    public Episode getOne(Long id){return epRepository.getOne(id);}




}
