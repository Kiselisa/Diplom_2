package praktikum;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import praktikum.data.UserData;
import praktikum.model.OrderModel;
import praktikum.model.UserModel;
import praktikum.steps.OrderSteps;
import praktikum.steps.UserSteps;

import java.util.ArrayList;
import java.util.List;

import static java.net.HttpURLConnection.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class CreateOrderTest extends BaseApiTest {

    private List<String> validIngredients;

    @Before
    public void prepareData() {
        Response ingredientsResponse = OrderSteps.getIngredients();
        validIngredients = OrderSteps.extractIngredientHashes(ingredientsResponse);
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            UserSteps.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Создание заказа с авторизацией")
    public void testCreateOrderWithAuthSuccess() {
        UserModel user = UserData.getRandomUser();
        Response userResponse = UserSteps.createUser(user);
        accessToken = UserSteps.extractAccessToken(userResponse);

        List<String> orderIngredients = List.of(
                validIngredients.get(0),
                validIngredients.get(1)
        );
        OrderModel order = new OrderModel(orderIngredients);

        OrderSteps.createOrder(order, accessToken)
                .then()
                .log().all()
                .statusCode(HTTP_OK)
                .body("success", equalTo(true))
                .body("name", notNullValue())
                .body("order.number", notNullValue())
                .body("order.status", equalTo("done"))
                .body("order.owner.name", equalTo(user.getName()))
                .body("order.owner.email", equalTo(user.getEmail()))
                .body("order.ingredients", notNullValue());
    }


    @Test
    @DisplayName("Создание заказа без авторизации")
    public void testCreateOrderWithoutAuthSuccess() {
        List<String> orderIngredients = List.of(
                validIngredients.get(0),
                validIngredients.get(1)
        );
        OrderModel order = new OrderModel(orderIngredients);

        OrderSteps.createOrder(order, null)
                .then()
                .log().all()
                .statusCode(HTTP_OK)
                .body("success", equalTo(true))
                .body("name", notNullValue())
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа с ингредиентами")
    public void testCreateOrderWithIngredients() {
        List<String> orderIngredients = List.of(
                validIngredients.get(0),
                validIngredients.get(0),
                validIngredients.get(2)
        );
        OrderModel order = new OrderModel(orderIngredients);

        OrderSteps.createOrder(order, null)
                .then()
                .log().all()
                .statusCode(HTTP_OK)
                .body("success", equalTo(true))
                .body("name", notNullValue())
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Получение ошибки при создании заказа без ингредиентов (пустой список)")
    public void testCreateOrderWithoutIngredientsError() {
        OrderModel order = new OrderModel(new ArrayList<>());

        OrderSteps.createOrder(order, null)
                .then()
                .log().all()
                .statusCode(HTTP_BAD_REQUEST)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Получение ошибки при создании заказа с неверным хешем ингредиента")
    public void testCreateOrderWithInvalidIngredientHashError() {
        List<String> invalidIngredients = List.of(
                validIngredients.get(0),
                validIngredients.get(2) + 23,
                validIngredients.get(5) + 98
        );
        OrderModel order = new OrderModel(invalidIngredients);

        OrderSteps.createOrder(order, null)
                .then()
                .log().all()
                .statusCode(HTTP_INTERNAL_ERROR);
    }
}
