package tests;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Test;
import api.UserApi;
import data.User;
import utils.RandomUtils;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class CreateOrderTests {
    private final UserApi userApi = new UserApi();
    private String accessToken;

    @After
    public void tearDown() {
        if (accessToken != null) {
            userApi.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Успешное создание заказа с авторизацией и ингредиентами")
    public void createOrderWithAuthAndIngredients() {
        // Создаем пользователя
        User user = new User(
                RandomUtils.generateRandomEmail(),
                "password",
                RandomUtils.generateRandomName());
        Response createResponse = userApi.createUser(user);
        accessToken = createResponse.path("accessToken");

        // Получаем список доступных ингредиентов
        Response ingredientsResponse = given()
                .get("https://stellarburgers.nomoreparties.site/api/ingredients");
        String firstIngredient = ingredientsResponse.jsonPath().getString("data[0]._id");
        String secondIngredient = ingredientsResponse.jsonPath().getString("data[1]._id");

        // Создаем заказ
        Response orderResponse = given()
                .header("Authorization", accessToken)
                .header("Content-type", "application/json")
                .body(String.format("{\"ingredients\": [\"%s\", \"%s\"]}", firstIngredient, secondIngredient))
                .when()
                .post("https://stellarburgers.nomoreparties.site/api/orders");

        orderResponse.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("order.number", equalTo(orderResponse.path("order.number")));
    }

    @Test
    @DisplayName("Успешное создание заказа без авторизации")
    public void createOrderWithoutAuth() {
        // Получаем список доступных ингредиентов
        Response ingredientsResponse = given()
                .get("https://stellarburgers.nomoreparties.site/api/ingredients");
        String firstIngredient = ingredientsResponse.jsonPath().getString("data[0]._id");

        Response response = given()
                .header("Content-type", "application/json")
                .body(String.format("{\"ingredients\": [\"%s\"]}", firstIngredient))
                .when()
                .post("https://stellarburgers.nomoreparties.site/api/orders");

        response.then()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Ошибка создания заказа без ингредиентов")
    public void createOrderWithoutIngredients() {
        Response response = given()
                .header("Content-type", "application/json")
                .body("{}")
                .when()
                .post("https://stellarburgers.nomoreparties.site/api/orders");

        response.then()
                .statusCode(400)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Ошибка создания заказа с неверным хешем ингредиентов")
    public void createOrderWithInvalidIngredientHash() {
        Response response = given()
                .header("Content-type", "application/json")
                .body("{\"ingredients\": [\"invalid_hash\"]}")
                .when()
                .post("https://stellarburgers.nomoreparties.site/api/orders");

        response.then()
                .statusCode(500);
    }
}