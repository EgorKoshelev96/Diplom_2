import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;



public class CreatingOrderTest {
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
        СreatingUser creatingUser = new СreatingUser(email, password, name);
        given().header("Content-Type", "application/json").and()
                .body(creatingUser).when().post("/api/auth/register");
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
    public void creatingOrderwWithoutAuthorization() {
        List<String> ingredients = Arrays.asList("61c0c5a71d1f82001bdaaa73", "61c0c5a71d1f82001bdaaa74");
        CreatingOrder сreatingOrder = new CreatingOrder(ingredients);
        Response response =
                given().header("Content-Type", "application/json")
                        .and().body(сreatingOrder).when().post("/api/orders");
        response.then().assertThat().body("success", equalTo(true)).and()
                .statusCode(200);
    }

    @ParameterizedTest
    @MethodSource("creatingOrderIngredients")
    void creatingOrderTest(List<String> ingredients) {

        UserLogin userLogin = new UserLogin(email, password);
        CreatingOrder сreatingOrder = new CreatingOrder(ingredients);

        given().header("Content-Type", "application/json")
                .and().body(userLogin).when().post("/api/auth/login");

        сreatingOrder.setIngredients(ingredients);

        Response response =
                given().header("Content-Type", "application/json")
                        .and().body(сreatingOrder).when().post("/api/orders");

        int actualStatusCode = response.statusCode();

        if (actualStatusCode == 200) {
            boolean success = response.jsonPath().getBoolean("success");
            System.out.println("ok: " + success);
        } else if (actualStatusCode == 400) {
            System.out.println("Ingredient ids must be provided StatusCode: " + actualStatusCode);
        } else {
            System.out.println("With an invalid ingredient hash StatusCode: " + actualStatusCode);

        }


    }

    private static Stream<Arguments> creatingOrderIngredients() {
        return Stream.of(
                Arguments.of(Arrays.asList("61c0c5a71d1f82001bdaaa74", "61c0c5a71d1f82001bdaaa71", "61c0c5a71d1f82001bdaaa70")),
                Arguments.of(Collections.emptyList()),
                Arguments.of(Arrays.asList("61c0c5a71d1f82001bdaaa234234", "61c0c5a71d1f82001bdaaa"))
        );
    }


}
