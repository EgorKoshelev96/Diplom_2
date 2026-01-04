import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class UserLoginTest {
    private Faker faker = new Faker();
    private String email;
    private String password;
    private String name;
    String accessToken;

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.education-services.ru";
        email = faker.internet().emailAddress();
        password = faker.internet().password(6, 10, true, true, true);
        name = faker.name().firstName();
    }

    @AfterEach
    public void deleteUser() {
        UserLogin userLogin = new UserLogin(email, password);
        Response loginResponse =
                given().header("Content-Type", "application/json")
                        .and().body(userLogin).when().post("/api/auth/login");

        accessToken = loginResponse.jsonPath().getString("accessToken");
        if (loginResponse.statusCode() == 200) {
            given().auth().oauth2(accessToken).header("Content-Type", "application/json")
                    .and().body(userLogin).when().delete("api/auth/user");
        }
    }
    @Test
    public void loginUnderExistingUser () {
        СreatingUser creatingUser = new СreatingUser(email, password, name);
        UserLogin userLogin = new UserLogin(email, password);
        given().header("Content-Type", "application/json").and()
                    .body(creatingUser).when().post("/api/auth/register");

        Response response =
                given().header("Content-Type", "application/json")
                        .and().body(userLogin).when().post("/api/auth/login");
        response.then().assertThat().body("success", equalTo(true)).and()
                    .statusCode(200);
        }
    @Test
    public void loginWithIncorrectLoginAndPassword() {
        UserLogin userLogin = new UserLogin(email, password);
        Response response =
                given().header("Content-Type", "application/json")
                        .and().body(userLogin).when().post("/api/auth/login");
        response.then().statusCode(401).body("message", equalTo("email or password are incorrect"));
        System.out.println(response.body().asString());
        }


    }

