package com.tvseries.TvSeries;


import com.tvseries.TvSeries.controllers.AuthController;
import com.tvseries.TvSeries.db.UserRepository;
import com.tvseries.TvSeries.common.RSA;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AuthTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RSA rsa;


    @Test
    void contextLoads() {
    }

    @Test
    public void TestVerifyUserNoUser() {
        var controller = new AuthController(userRepository, rsa);
        assert !controller.verifyUser("aaa");
        assert !controller.verifyUser("");
    }


    @Test
    public void TestVerifyUserExistingUser() throws Exception {
        var controller = new AuthController(userRepository, rsa);
        String regInfo = "test:1234";
        var rsaLogPass = RSA.encrypt(regInfo, rsa.getPublicKey());
        var token = controller.register(rsaLogPass);
        //System.out.println(token);
        long id = controller.getIdFromJWT(token);
        System.out.println(id);
        assert controller.verifyUser(token);
    }

    @Test
    public void TestGetIdFromJWT() throws Exception {
        var controller = new AuthController(userRepository, rsa);
        String regInfo = "test:1234";
        var rsaLogPass = RSA.encrypt(regInfo, rsa.getPublicKey());
        var token = controller.register(rsaLogPass);
        //token = token.replace("\n", "");
        long id = controller.getIdFromJWT(token);
        assert id == 3;
    }


    @Test
    public void TestLoginUser() throws Exception {
        var controller = new AuthController(userRepository, rsa);
        String regInfo = "test:1234";
        var rsaLogPass = RSA.encrypt(regInfo, rsa.getPublicKey());
        var token = controller.register(rsaLogPass);
        var answer = controller.auth(token);
        assert answer == "hello";
    }


    @Test
    public void TestLoginInvalidUser() throws Exception {
        var controller = new AuthController(userRepository, rsa);
        var token = "TABURETH";
        var answer = controller.auth(token);
        assert answer == "you are not logged in";
    }
}
