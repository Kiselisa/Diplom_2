package praktikum.steps;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import praktikum.model.UserLogin;
import praktikum.model.UserModel;

import static io.restassured.RestAssured.given;
import static praktikum.data.UserData.*;

public class UserSteps {

    @Step("Создание нового пользователя: email={user.email}, password={user.password}, name={user.name}")
    public static Response createUser(UserModel user) {
        return given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post(CREATE_USER_PATH)
                .then()
                .extract().response();
    }
    @Step("Логин пользователя в системе: email={credentials.email}, password={credentials.password}")
    public static Response loginUser(UserLogin credentials) {
        return given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(credentials)
                .when()
                .post(LOGIN_USER_PATH)
                .then()
                .extract().response();
    }

    @Step("Получение токена пользователя")
    public static String extractAccessToken(Response response) {
        return response
                .then()
                .extract()
                .path("accessToken");
    }

    @Step("Удаление пользователя")
    public static void deleteUser(String accessToken) {
        given()
                .log().all()
                .baseUri(praktikum.data.UserData.BASE_URI)
                .header("Authorization", accessToken)
                .when()
                .delete(DELETE_USER_PATH)
                .then()
                .extract().response();
    }
}
