package praktikum;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import praktikum.data.UserData;
import praktikum.model.UserLogin;
import praktikum.model.UserModel;
import praktikum.steps.UserSteps;

import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;


public class LoginUserTest extends BaseApiTest {

    private UserModel user;

    @Before
    public void prepareData() {
        user = UserData.getRandomUser();
        Response response = UserSteps.createUser(user);
        accessToken = UserSteps.extractAccessToken(response);
    }
    @After
    public void tearDown() {
        if (accessToken != null) {
            UserSteps.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Успешный логин существующего пользователя")
    public void userLoginSuccess() {
        UserLogin credentials = new UserLogin(user.getEmail(), user.getPassword());
        UserSteps.loginUser(credentials)
                .then()
                .log().all()
                .statusCode(HTTP_OK)
                .body("success", equalTo(true))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue())
                .body("user.email", equalTo(user.getEmail()))
                .body("user.name", equalTo(user.getName()));
    }
    @Test
    @DisplayName("Получение ошибки при входе пользователя в систему с неправильным паролем")
    public void testUserLoginWithWrongPasswordError() {
        UserLogin credentials = new UserLogin(user.getEmail(), user.getPassword() + 23);
        UserSteps.loginUser(credentials)
                .then()
                .log().all()
                .statusCode(HTTP_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }
    @Test
    @DisplayName("Получение ошибки при входе пользователя в систему с неправильным email")
    public void testUserLoginWithWrongEmailError() {
        UserLogin credentials = new UserLogin(user.getEmail() + "ru", user.getPassword());
        UserSteps.loginUser(credentials)
                .then()
                .log().all()
                .statusCode(HTTP_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }
}
