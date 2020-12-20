package com.tvseries.TvSeries;

import com.tvseries.TvSeries.common.RSA;
import com.tvseries.TvSeries.controllers.AuthController;
import com.tvseries.TvSeries.controllers.UserController;
import com.tvseries.TvSeries.db.*;
import com.tvseries.TvSeries.model.TvShow;
import com.tvseries.TvSeries.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
public class UserControllerTests {


    @Autowired
    private UserService userService;

    @Autowired
    private TvShowService tvShowService;

    @Autowired
    private EpisodeService episodeService;

    @Autowired
    private RSA rsa;


    @Test
    void contextLoads() {
    }

    @Test
    public void TestRegister() {
        userService.deleteAll();

        var logpass = "aaa:111";
        var controller = new AuthController(userService, rsa);
        var token = controller.insecureRegister(logpass);
        assert controller.verifyUser(token);
    }


    @Test
    public void TestAddWatching() {
        userService.deleteAll();

        var logpass = "aaa:111";
        var authController = new AuthController(userService, rsa);
        var token = authController.insecureRegister(logpass);
        var userController = new UserController(userService, tvShowService, episodeService);
        var randomShow = tvShowService.findAll().get(0);
        userController.addWatching(randomShow.getId(), token);
        assert userController.watching(10, 1, token).get(0).equals(randomShow);
    }

    @Test
    public void TestDeleteWatching() {
        userService.deleteAll();

        var logpass = "aaa:111";
        var authController = new AuthController(userService, rsa);
        var token = authController.insecureRegister(logpass);
        var userController = new UserController(userService, tvShowService, episodeService);
        var randomShow1 = tvShowService.findAll().get(0);
        var randomShow2 = tvShowService.findAll().get(1);
        userController.addWatching(randomShow1.getId(), token);
        userController.addWatching(randomShow2.getId(), token);
        assert userController.watching(10, 1, token).get(0).equals(randomShow1);
        assert userController.watching(10, 1, token).get(1).equals(randomShow2);
        userController.deleteWatching(randomShow1.getId(), token);
        assert userController.watching(10, 1, token).size()==1;
        assert userController.watching(10, 1, token).get(0).equals(randomShow2);
    }


    @Test
    public void TestGetWatchingWithWrongToken() {
        userService.deleteAll();
        var logpass = "aaa:111";
        var authController = new AuthController(userService, rsa);
        var token = authController.insecureRegister(logpass);
        var userController = new UserController(userService, tvShowService, episodeService);
        var randomShow = tvShowService.findAll().get(0);
        userController.addWatching(randomShow.getId(), token);
        assert userController.watching(10, 1, token).get(0).equals(randomShow);
        assert userController.watching(10, 1, "12345")==null;
    }

    @Test
    public void TestAddWatchingWithWrongToken() {
        userService.deleteAll();
        var logpass = "aaa:111";
        var authController = new AuthController(userService, rsa);
        var token = authController.insecureRegister(logpass);
        var userController = new UserController(userService, tvShowService, episodeService);
        var randomShow = tvShowService.findAll().get(0);
        userController.addWatching(randomShow.getId(), token);
        assert userController.watching(10, 1, token).get(0).equals(randomShow);
        var randomShow2 = tvShowService.findAll().get(0);
        assert !userController.addWatching(randomShow2.getId(), "12345");
    }

    @Test
    public void TestUserExists(){
        userService.deleteAll();
        var user1 = new User("aaa", "bbb");
        var user1saved = userService.save(user1);
        var user2 = new User("ccc", "bbb");
        assert userService.existsById(user1saved.getId());
        assert userService.existsByLogin(user1saved.getLogin());
        assert userService.existsByLogin(user1.getLogin());
        assert !userService.existsByLogin(user2.getLogin());
    }

}
