package tests;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Test;
import api.UserApi;
import data.User;
import utils.RandomUtils;

import static org.hamcrest.Matchers.equalTo;

public class ChangeUserTests {
    private final UserApi userApi = new UserApi();
    private String accessToken;

    @After
    public void tearDown() {
        if (accessToken != null) {
            userApi.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Успешное изменение почты с авторизацией")
    public void successUpdateUserEmailWithAuth() {
        User user = new User(
                RandomUtils.generateRandomEmail(),
                "password",
                RandomUtils.generateRandomName());

        Response createResponse = userApi.createUser(user);
        accessToken = createResponse.path("accessToken");

        User updatedUser = new User();
        updatedUser.setEmail(RandomUtils.generateRandomEmail());
        updatedUser.setPassword(user.getPassword());

        Response updateResponse = userApi.updateUser(accessToken, updatedUser);

        updateResponse.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(updatedUser.getEmail().toLowerCase()));
    }

    @Test
    @DisplayName("Успешное изменение имени с авторизацией")
    public void successUpdateUserNameWithAuth() {
        User user = new User(
                RandomUtils.generateRandomEmail(),
                "password",
                RandomUtils.generateRandomName());

        Response createResponse = userApi.createUser(user);
        accessToken = createResponse.path("accessToken");

        User updatedUser = new User();
        updatedUser.setName("New_" + RandomUtils.generateRandomName());

        Response updateResponse = userApi.updateUser(accessToken, updatedUser);

        updateResponse.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.name", equalTo(updatedUser.getName()));
    }

    @Test
    @DisplayName("Ошибка изменения данных без авторизации")
    public void updateUserWithoutAuth() {
        User updatedUser = new User();
        updatedUser.setEmail(RandomUtils.generateRandomEmail());

        Response response = userApi.updateUser("", updatedUser);

        response.then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

    @Test
    @DisplayName("Ошибка изменения на уже существующую почту")
    public void updateUserToExistingEmail() {
        // Создаем первого пользователя
        User firstUser = new User(
                RandomUtils.generateRandomEmail(),
                "password",
                RandomUtils.generateRandomName());
        userApi.createUser(firstUser);

        // Создаем второго пользователя
        User secondUser = new User(
                RandomUtils.generateRandomEmail(),
                "password",
                RandomUtils.generateRandomName());
        Response createResponse = userApi.createUser(secondUser);
        accessToken = createResponse.path("accessToken");

        // Пытаемся изменить email второго пользователя на email первого
        User updatedUser = new User();
        updatedUser.setEmail(firstUser.getEmail());

        Response updateResponse = userApi.updateUser(accessToken, updatedUser);

        updateResponse.then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("User with such email already exists"));
    }
}
