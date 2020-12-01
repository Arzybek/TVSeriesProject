package com.tvseries.TvSeries;

import com.tvseries.TvSeries.common.RSA;
import com.tvseries.TvSeries.controllers.AuthController;
import com.tvseries.TvSeries.controllers.UserController;
import com.tvseries.TvSeries.db.TvShowRepository;
import com.tvseries.TvSeries.db.UserRepository;
import com.tvseries.TvSeries.model.TvShow;
import com.tvseries.TvSeries.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
public class UserControllerTests {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TvShowRepository tvShowRepository;

    @Autowired
    private RSA rsa;


    @Test
    void contextLoads() {
    }

    @Test
    public void TestRegister() {
        userRepository.deleteAll();

        var logpass = "aaa:111";
        var controller = new AuthController(userRepository, rsa);
        var token = controller.insecureRegister(logpass);
        assert controller.verifyUser(token);
    }


    @Test
    public void TestAddWatching() {
        userRepository.deleteAll();

        var logpass = "aaa:111";
        var authController = new AuthController(userRepository, rsa);
        var token = authController.insecureRegister(logpass);
        var userController = new UserController(tvShowRepository, userRepository);
        var randomShow = tvShowRepository.findAll().get(0);
        userController.addWatching(randomShow.getId(), token);
        assert userController.watching(10, 1, token).get(0).equals(randomShow);
    }

    @Test
    public void TestDeleteWatching() {
        userRepository.deleteAll();

        var logpass = "aaa:111";
        var authController = new AuthController(userRepository, rsa);
        var token = authController.insecureRegister(logpass);
        var userController = new UserController(tvShowRepository, userRepository);
        var randomShow1 = tvShowRepository.findAll().get(0);
        var randomShow2 = tvShowRepository.findAll().get(1);
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
        userRepository.deleteAll();
        var logpass = "aaa:111";
        var authController = new AuthController(userRepository, rsa);
        var token = authController.insecureRegister(logpass);
        var userController = new UserController(tvShowRepository, userRepository);
        var randomShow = tvShowRepository.findAll().get(0);
        userController.addWatching(randomShow.getId(), token);
        assert userController.watching(10, 1, token).get(0).equals(randomShow);
        assert userController.watching(10, 1, "12345")==null;
    }

    @Test
    public void TestAddWatchingWithWrongToken() {
        userRepository.deleteAll();
        var logpass = "aaa:111";
        var authController = new AuthController(userRepository, rsa);
        var token = authController.insecureRegister(logpass);
        var userController = new UserController(tvShowRepository, userRepository);
        var randomShow = tvShowRepository.findAll().get(0);
        userController.addWatching(randomShow.getId(), token);
        assert userController.watching(10, 1, token).get(0).equals(randomShow);
        var randomShow2 = tvShowRepository.findAll().get(1);
        assert !userController.addWatching(randomShow2.getId(), "12345");
    }

}
