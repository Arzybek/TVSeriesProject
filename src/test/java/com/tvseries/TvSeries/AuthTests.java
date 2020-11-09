package com.tvseries.TvSeries;


import com.tvseries.TvSeries.dto.User;
import common.RSA;
import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

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
    public void TestVerifyUserNoUser()
    {
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
        System.out.println(token);
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
        assert id==3;
    }

    @Test
    public void TestGetIdFromJWTFake()
    {
        var controller = new AuthController(userRepository, rsa);
        var text = "{\"id\":1,aaaa}";
        var baseEncoded = Base64.encodeBase64(text.getBytes());
        var fakeStr = new String(baseEncoded);
        var id = controller.getIdFromJWT(fakeStr);
        System.out.println(id);
        assert id==1;
    }

}
