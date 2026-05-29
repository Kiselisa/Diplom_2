package praktikum;
import io.restassured.RestAssured;
import org.junit.BeforeClass;
import static praktikum.data.UserData.BASE_URI;
public class BaseApiTest {

    protected String accessToken;

    @BeforeClass
    public static void setUp() {
        RestAssured.baseURI = BASE_URI;
    }
}
