package com.tvseries.TvSeries.db;

import com.tvseries.TvSeries.model.TvShow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class TvShowService {
    @PersistenceContext
    private EntityManager em;

    @Autowired
    private TvShowRepository repository;

    @Autowired
    private EpisodeRepository epRepository;

    public TvShow create(TvShow tvShow) {
        return repository.save(tvShow);
    }

    public TvShow read(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException(""+id));
    }

    public List<TvShow> searchByName (String name) {
        return repository.findTvShowByName(name);
    }

    public TvShow update(TvShow tvShow) {
        return repository.findById(tvShow.getId())
                .map(show -> {
                    show.setName(tvShow.getName());
                    show.setCategory(tvShow.getCategory());
                    show.setYear(tvShow.getYear());
                    return repository.save(show);
                })
                .orElseGet(() -> {
                    return repository.save(tvShow);
                });
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public List<TvShow> findAll(){

        TypedQuery query = em.createQuery("select c from TvShow c", TvShow.class);

        return query.getResultList();

        //return repository.findAll();
    }

    public List<TvShow> findAllExceptCustom(int page, int pageSize) {

        TypedQuery query = em.createQuery("select c from TvShow c WHERE c.isUserShow = FALSE", TvShow.class);

        query.setFirstResult(page * pageSize);
        query.setMaxResults(pageSize);

        return query.getResultList();
    }

    public List<TvShow> findAll(int page, int pageSize) {

        TypedQuery query = em.createQuery("select c from TvShow c", TvShow.class);

        query.setFirstResult(page * pageSize);
        query.setMaxResults(pageSize);

        return query.getResultList();
    }
}
