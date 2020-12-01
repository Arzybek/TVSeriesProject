package com.tvseries.TvSeries;


import com.tvseries.TvSeries.controllers.AuthController;
import com.tvseries.TvSeries.db.UserRepository;
import com.tvseries.TvSeries.common.RSA;
import com.tvseries.TvSeries.model.TvShow;
import com.tvseries.TvSeries.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserShowsInteractionTests {

    @Autowired
    private UserRepository userRepository;


    @Test
    void contextLoads() {
    }

    @Test
    public void TestGetWatchingShowsFromUser() {
        var user = new User();
        var show = new TvShow("aaa", "aaaa", 1965);
        user.addWatchingShow(show);

        var user2 = userRepository.save(user);

        assert userRepository.findById(user2.getId()).get().getWatchingShows().get(0).equals(show);
    }

    @Test
    public void TestAddWatchingShowsFromUser() {
        var user = new User();
        var show = new TvShow("aaa", "aaaa", 1965);
        user.addWatchingShow(show);

        var user2 = userRepository.save(user);

        assert userRepository.findById(user2.getId()).get().getWatchingShows().get(0).equals(show);

        user = userRepository.findById(user2.getId()).get();

        show = new TvShow("bbb", "bbbb", 1922);
        user.addWatchingShow(show);

        user2 = userRepository.save(user);

        assert userRepository.findById(user2.getId()).get().getWatchingShows().size()==2;

        var showToCompare = userRepository.findById(user2.getId()).get().getWatchingShows().get(1);

        assert showToCompare.equals(show);

    }

    @Test
    public void TestDeleteWatchingShowsFromUser() {
        var user = new User();
        var show = new TvShow("aaa", "aaaa", 1965);
        var show2 = new TvShow("bbb", "bbbb", 1922);
        user.addWatchingShow(show);
        user.addWatchingShow(show2);

        var user2 = userRepository.save(user);

        var userFromDB = userRepository.findById(user2.getId()).get();

        userFromDB.deleteWatchingShow(show2);

        userRepository.save(userFromDB);

        userFromDB = userRepository.findById(userFromDB.getId()).get();

        assert userFromDB.getWatchingShows().size()==1;

        assert userFromDB.getWatchingShows().get(0).equals(show);


    }


}
