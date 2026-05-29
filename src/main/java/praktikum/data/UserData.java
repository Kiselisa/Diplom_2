package praktikum.data;
import com.github.javafaker.Faker;
import praktikum.model.UserModel;

public class UserData {
    public static final String BASE_URI = "https://stellarburgers.education-services.ru/";
    public static final String CREATE_USER_PATH = "/api/auth/register";
    public static final String LOGIN_USER_PATH = "/api/auth/login";
    public static final String DELETE_USER_PATH = "/api/auth/user";
    public static final String ORDERS_PATH = "/api/orders";
    public static final String INGREDIENTS_PATH = "/api/ingredients";


    public static UserModel getRandomUser() {
        Faker faker = new Faker();
        return new UserModel(
                faker.internet().emailAddress(),
                faker.internet().password(6,8),
                faker.name().firstName()
        );
    }
}