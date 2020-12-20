package com.tvseries.TvSeries.db;

import com.tvseries.TvSeries.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository  extends JpaRepository<User, Long> {


    @Query("select case when count(c)> 0 then true else false end from User c where c.login like :login")
    boolean existsByLogin(@Param("login") String login);


    @Query("select case when count(c)> 0 then true else false end from User c where lower(c.login) like lower(:login) and lower(c.passwordHash) like lower(:passwordHash)")
    boolean existsByLoginAndPasswHash(@Param("login") String login, @Param("passwordHash") String passwordHash);

}

