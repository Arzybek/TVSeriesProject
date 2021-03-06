package com.tvseries.TvSeries.db;

import com.tvseries.TvSeries.model.TvShow;
import com.tvseries.TvSeries.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.NoSuchElementException;

import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.caseSensitive;
import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.ignoreCase;

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
        return repository.save(user);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Boolean existsById(Long id){
        return repository.existsById(id);
    }

    public Boolean existsByLogin(String login){

        return repository.existsByLogin(login);

    }



    public Boolean existsByLoginPassHash(String login, String passHash){

        return repository.existsByLoginAndPasswHash(login, passHash);
    }



    public User getByLoginPasshash(String login, String passHash){

        if (!existsByLoginPassHash(login, passHash))
            return null;

        ExampleMatcher matcher = ExampleMatcher.matchingAny()
                .withIgnorePaths("id")
                .withMatcher("login", ignoreCase())
                .withMatcher("passwordHash", ignoreCase());


        var test = new User(login, login, passHash);
        Example<User> example = Example.of(test, matcher);
        return repository.findOne(example).get();
    }

    public void deleteAll(){
        repository.deleteAll();
    }
}
