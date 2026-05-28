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

    private Response response;

    @After
    public void tearDown() {
        if (response != null) {
            try {
                String token = UserSteps.extractAccessToken(response);
                if (token != null) {
                    UserSteps.deleteUser(token);
                }
            } catch (Exception ignored) {
            }
        }
    }

    @Test
    @DisplayName("Успешное создание нового пользователя")
    @Description("Проверка создания уникального пользователя с валидными данными. Ожидается статус 200 OK и токен в ответе.")
    public void testUserCreateSuccess() {
        UserModel user = UserData.getRandomUser();
        response = createUser(user);

        response.then()
                .log().all()
                .statusCode(HTTP_OK)
                .body("success", equalTo(true))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue())
                .body("user.email", equalTo(user.getEmail()))
                .body("user.name", equalTo(user.getName()));

    }

    @Test
    @DisplayName("Получение ошибки при создании существующего пользователя")
    @Description("Проверка запрета повторной регистрации. Создаем пользователя, затем пытаемся создать дубликат с тем же email")
    public void testCreateSameUserError() {
        UserModel user = UserData.getRandomUser();

        response = createUser(user);
        response.then()
                .log().all()
                .statusCode(HTTP_OK)
                .body("success", equalTo(true));

        Response errorResponse = createUser(user);
        errorResponse.then()
                .log().all()
                .statusCode(HTTP_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }
    @Test
    @DisplayName("Получение ошибки при создании пользователя без email(null)")
    @Description("Проверка валидации обязательного поля email при передаче значения null")
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
    @Description("Проверка валидации обязательного поля email при передаче пустой строки")
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
    @Description("Проверка валидации обязательного поля password при передаче значения null")
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
    @Description("Проверка валидации обязательного поля password при передаче пустой строки")
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
    @Description("Проверка валидации обязательного поля name при передаче значения null")
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
    @Description("Проверка валидации обязательного поля name при передаче пустой строки")
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
