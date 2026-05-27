package praktikum;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Test;
import praktikum.data.UserData;
import praktikum.model.UserModel;
import praktikum.steps.UserSteps;

import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static praktikum.steps.UserSteps.createUser;

public class CreateUserTest extends BaseApiTest {
    @After
    public void tearDown() {
        if (accessToken != null) {
            UserSteps.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Успешное создание нового пользователя")
    @Description("Код успешного создания: 200 OK, а не 201 Created")
    public void testUserCreateSuccess() {
        UserModel user = UserData.getRandomUser();
        Response response = createUser(user);

        response.then()
                .log().all()
                .statusCode(HTTP_OK)
                .body("success", equalTo(true))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue())
                .body("user.email", equalTo(user.getEmail()))
                .body("user.name", equalTo(user.getName()));

        accessToken = UserSteps.extractAccessToken(response);
    }

    @Test
    @DisplayName("Получение ошибки при создании существующего пользователя")
    public void testCreateSameUserError() {
        UserModel user = UserData.getRandomUser();

        Response response = createUser(user);
        response.then()
                .log().all()
                .statusCode(HTTP_OK)
                .body("success", equalTo(true));
        accessToken = UserSteps.extractAccessToken(response);

        Response errorResponse = createUser(user);
        errorResponse.then()
                .log().all()
                .statusCode(HTTP_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }
    @Test
    @DisplayName("Получение ошибки при создании пользователя без email(null)")
    public void testCreateUserWithNullEmailError() {
        UserModel user = UserData.getRandomUser();
        user.setEmail(null);
        createUser(user)
                .then()
                .log().all()
                .statusCode(HTTP_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
    @Test
    @DisplayName("Получение ошибки при создании пользователя без email(незаполненное поле))")
    public void testCreateUserWithNoEmailError() {
        UserModel user = UserData.getRandomUser();
        user.setEmail("");
        createUser(user)
                .then()
                .log().all()
                .statusCode(HTTP_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
    @Test
    @DisplayName("Получение ошибки при создании пользователя без пароля(null)")
    public void testCreateUserWithNullPasswordError() {
        UserModel user = UserData.getRandomUser();
        user.setPassword(null);
        createUser(user)
                .then()
                .log().all()
                .statusCode(HTTP_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
    @Test
    @DisplayName("Получение ошибки при создании пользователя без пароля(незаполненное поле)")
    public void testCreateUserWithNoPasswordError() {
        UserModel user = UserData.getRandomUser();
        user.setPassword("");
        createUser(user)
                .then()
                .log().all()
                .statusCode(HTTP_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
    @Test
    @DisplayName("Получение ошибки при создании пользователя без имени(null)")
    public void testCreateUserWithNullNameError() {
        UserModel user = UserData.getRandomUser();
        user.setName(null);
        createUser(user)
                .then()
                .log().all()
                .statusCode(HTTP_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
    @Test
    @DisplayName("Получение ошибки при создании пользователя без имени(незаполненное поле)")
    public void testCreateUserWithNoNameError() {
        UserModel user = UserData.getRandomUser();
        user.setName("");
        createUser(user)
                .then()
                .log().all()
                .statusCode(HTTP_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
}
