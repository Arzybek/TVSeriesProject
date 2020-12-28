package com.tvseries.TvSeries;


import com.tvseries.TvSeries.controllers.AuthController;
import com.tvseries.TvSeries.db.UserRepository;
import com.tvseries.TvSeries.common.RSA;
import com.tvseries.TvSeries.db.UserService;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootTest
public class AuthTests {

    @Autowired
    private UserService userService;

    @Autowired
    private RSA rsa;


    @Test
    void contextLoads() {
    }

    @Test
    public void TestVerifyUserNoUser() {
        var controller = new AuthController(userService, rsa);
        assert !controller.verifyUser("aaa");
        assert !controller.verifyUser("");
    }


    @Test
    public void TestVerifyUserExistingUser() throws Exception {
        var controller = new AuthController(userService, rsa);
        String regInfo = "test:1234";
        var rsaLogPass = RSA.encrypt(regInfo, rsa.getPublicKey());
        var token = controller.register(rsaLogPass);
        //System.out.println(token);
        long id = AuthController.getIdFromJWT(token);
        System.out.println(id);
        assert controller.verifyUser(token);
    }

    @Test
    public void TestGetIdFromJWT() throws Exception {
        var controller = new AuthController(userService, rsa);
        String regInfo = "test:1234";
        var rsaLogPass = RSA.encrypt(regInfo, rsa.getPublicKey());
        var token = controller.register(rsaLogPass);
        //token = token.replace("\n", "");
        long id = controller.getIdFromJWT(token);
        assert id == 255;
    }


    @Test
    public void JsonParserTest() throws JSONException {
        var tokenPayload = "{\"iss\":\"Issuer\",\"id\":33,\"user\":\"123\"}";
        tokenPayload = tokenPayload.replaceAll("\\\\", "");// убрали слэши, с ними не парсится в JSONObject
        JSONObject obj = new JSONObject(tokenPayload);
        Long id = obj.getLong("id");
        assert id == 33;
    }


}
