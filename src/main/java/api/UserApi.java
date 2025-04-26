package api;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import data.User;

import static io.restassured.RestAssured.given;

public class UserApi {
    private static final String BASE_URL = "https://stellarburgers.nomoreparties.site/api";

    @Step("Создание пользователя")
    public Response createUser(User user) {
        return given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post(BASE_URL + "/auth/register");
    }

    @Step("Авторизация пользователя")
    public Response loginUser(User user) {
        return given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post(BASE_URL + "/auth/login");
    }

    @Step("Удаление пользователя")
    public Response deleteUser(String accessToken) {
        return given()
                .header("Authorization", accessToken)
                .when()
                .delete(BASE_URL + "/auth/user");
    }

    @Step("Обновление данных пользователя")
    public Response updateUser(String accessToken, User updatedUser) {
        return given()
                .header("Authorization", accessToken)
                .header("Content-type", "application/json")
                .body(updatedUser)
                .when()
                .patch(BASE_URL + "/auth/user");
    }

    @Step("Получение данных пользователя")
    public Response getUserData(String accessToken) {
        return given()
                .header("Authorization", accessToken)
                .when()
                .get(BASE_URL + "/auth/user");
    }
}
