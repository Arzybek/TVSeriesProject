package com.tvseries.TvSeries.db;

import com.tvseries.TvSeries.model.TvShow;
import com.tvseries.TvSeries.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.NoSuchElementException;

@Repository
public class UserService {


    @PersistenceContext
    private EntityManager em;

    @Autowired
    private UserRepository repository;

    public User save(User user) {
        return repository.save(user);
    }

    public User getUser(Long id) {
        try{
            return repository.findById(id).get();
        }
        catch (NoSuchElementException e)
        {
            return null;
        }
    }


    public User update(User user) {
        return repository.save(user); // возможно стоит именно  апдейтить
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Boolean existsById(Long id){
        return repository.existsById(id);
    }

    public void deleteAll(){
        repository.deleteAll();
    }
}
