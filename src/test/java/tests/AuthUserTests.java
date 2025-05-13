package tests;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Test;
import api.UserApi;
import data.User;
import utils.RandomUtils;

import static org.hamcrest.Matchers.equalTo;

public class AuthUserTests {
    private final UserApi userApi = new UserApi();
    private String accessToken;

    @After
    public void tearDown() {
        if (accessToken != null) {
            userApi.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Валидная авторизация существующего юзера")
    public void successLoginExistingUser() {
        User user = new User(
                RandomUtils.generateRandomEmail(),
                "password",
                RandomUtils.generateRandomName());

        userApi.createUser(user);
        Response response = userApi.loginUser(user);
        accessToken = response.path("accessToken");

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(user.getEmail().toLowerCase()))
                .body("user.name", equalTo(user.getName()));
    }

    @Test
    @DisplayName("Ошибка авторизации с неверной почтой")
    public void loginWithBadEmail() {
        User user = new User(
                RandomUtils.generateRandomEmail(),
                "password",
                RandomUtils.generateRandomName());

        userApi.createUser(user);

        User wrongUser = new User(
                "wrong_" + user.getEmail(),
                user.getPassword(),
                user.getName());

        Response response = userApi.loginUser(wrongUser);

        response.then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Ошибка авторизации с неверным паролем")
    public void loginWithBadPassword() {
        User user = new User(
                RandomUtils.generateRandomEmail(),
                "password",
                RandomUtils.generateRandomName());

        userApi.createUser(user);

        User wrongUser = new User(
                user.getEmail(),
                "wrong_password",
                user.getName());

        Response response = userApi.loginUser(wrongUser);

        response.then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }
}