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

public class GetOrderTests {
    private final UserApi userApi = new UserApi();
    private String accessToken;

    @After
    public void tearDown() {
        if (accessToken != null) {
            userApi.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Успешное получение заказов авторизованного юзера")
    public void getOrdersWithAuthSuccess() {
        // Создаем пользователя
        User user = new User(
                RandomUtils.generateRandomEmail(),
                "password",
                RandomUtils.generateRandomName());
        Response createResponse = userApi.createUser(user);
        accessToken = createResponse.path("accessToken");

        // Создаем заказ, чтобы у пользователя была история
        Response ingredientsResponse = given()
                .get("https://stellarburgers.nomoreparties.site/api/ingredients");
        String firstIngredient = ingredientsResponse.jsonPath().getString("data[0]._id");

        given()
                .header("Authorization", accessToken)
                .header("Content-type", "application/json")
                .body(String.format("{\"ingredients\": [\"%s\"]}", firstIngredient))
                .when()
                .post("https://stellarburgers.nomoreparties.site/api/orders");

        // Получаем заказы пользователя
        Response response = given()
                .header("Authorization", accessToken)
                .when()
                .get("https://stellarburgers.nomoreparties.site/api/orders");

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("orders[0].ingredients.size()", equalTo(1));
    }

    @Test
    @DisplayName("Ошибка получения заказов неавторизованного юзера")
    public void getOrdersWithoutAuth() {
        Response response = given()
                .when()
                .get("https://stellarburgers.nomoreparties.site/api/orders");

        response.then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }
}
