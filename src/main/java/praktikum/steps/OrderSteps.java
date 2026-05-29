package praktikum.steps;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import praktikum.model.OrderModel;
import java.util.List;
import static io.restassured.RestAssured.given;
import static praktikum.data.UserData.INGREDIENTS_PATH;
import static praktikum.data.UserData.ORDERS_PATH;

public class OrderSteps {

    @Step("Получение списка всех ингредиентов")
    public static Response getIngredients() {
        return given()
                .log().all()
                .when()
                .get(INGREDIENTS_PATH)
                .then()
                .log().all()
                .extract().response();
    }

    @Step("Извлечение списка хешей доступных ингредиентов")
    public static List<String> extractIngredientHashes(Response response) {
        return response
                .then()
                .extract()
                .path("data._id");
    }

    @Step("Создание заказа (с токеном или без)")
    public static Response createOrder(OrderModel order, String accessToken) {
        var requestSpec = given()
                .log().all()
                .contentType(ContentType.JSON);

        if (accessToken != null && !accessToken.isEmpty()) {
            requestSpec.header("Authorization", accessToken);
        }

        return requestSpec
                .body(order)
                .when()
                .post(ORDERS_PATH)
                .then()
                .log().all()
                .extract().response();
    }
}
