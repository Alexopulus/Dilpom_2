package tests;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Test;
import api.UserApi;
import data.User;
import utils.RandomUtils;

import static org.hamcrest.Matchers.equalTo;

public class CreateUserTests {
    private final UserApi userApi = new UserApi();
    private String accessToken;

    @After
    public void tearDown() {
        if (accessToken != null) {
            userApi.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Валидное создание уникального юзера")
    public void createUserSuccessfully() {
        User user = new User(
                RandomUtils.generateRandomEmail(),
                "password",
                RandomUtils.generateRandomName());

        Response response = userApi.createUser(user);
        accessToken = response.path("accessToken");

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(user.getEmail().toLowerCase()))
                .body("user.name", equalTo(user.getName()));
    }

    @Test
    @DisplayName("Ошибка при создании уже зарегистрированного юзера")
    public void createExistingUser() {
        User user = new User(
                RandomUtils.generateRandomEmail(),
                "password",
                RandomUtils.generateRandomName());

        userApi.createUser(user);
        Response response = userApi.createUser(user);

        response.then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Ошибка при создании юзера без почты")
    public void createUserWithoutEmail() {
        User user = new User();
        user.setPassword("password");
        user.setName("Name");

        Response response = userApi.createUser(user);

        response.then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Ошибка при создании пользователя без пароля")
    public void createUserWithoutPassword() {
        User user = new User();
        user.setEmail(RandomUtils.generateRandomEmail());
        user.setName("Name");

        Response response = userApi.createUser(user);

        response.then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Ошибка при создании пользователя без имени")
    public void createUserWithoutName() {
        User user = new User();
        user.setEmail(RandomUtils.generateRandomEmail());
        user.setPassword("password");

        Response response = userApi.createUser(user);

        response.then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
}
