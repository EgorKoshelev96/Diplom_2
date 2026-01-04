import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class ChangingUserDataTest {

    private String email;
    private String password;

    private String accessToken;


    private String getAccessToken(String email, String password) {
        UserLogin userLogin = new UserLogin(email, password);
        Response loginResponse = given().header("Content-Type", "application/json")
                .and().body(userLogin).when().post("/api/auth/login");
        String fullToken = loginResponse.jsonPath().getString("accessToken");
        return accessToken = fullToken.replace("Bearer ", "");
    }

    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = "https://stellarburgers.education-services.ru";

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

    @ParameterizedTest
    @CsvSource({
            "Koshe@mail.ru,12345m,Egor",
            "Koshele@mail.ru,12345m58,Egor",
            "Koshele@mail.ru,12345m58,Egor_Koshelev",
    })
    public void changingUserDataWithoutWuthorization(String email, String password, String name) {
        ChangingUserData changingUserData = new ChangingUserData(email, password, name);
        changingUserData.setEmail(email);
        changingUserData.setPassword(password);
        changingUserData.setName(name);

        Response response =
                given().header("Content-Type", "application/json")
                        .and().body(changingUserData).when().patch("/api/auth/user");
        response.then().assertThat().body("success", equalTo(false)).and()
                .statusCode(401);

    }

    @ParameterizedTest
    @CsvSource({
            "QWEQRE@EQ.RU,123456b,Koshele@mail.ru,12345m58,Egor",
            "Koshele@mail.ru,12345m58,Koshelev@mail.ru,12345m,Egor",
            "Koshelev@mail.ru,12345m,Koshele@mail.ru,12345m58,Egor_Koshelev",
            "Koshele@mail.ru,12345m58,QWEQRE@EQ.RU,123456b,Egor"

    })
    public void сhangingUserDataWithAuthorization(String email, String password, String email1, String password1, String name1) {
        accessToken = getAccessToken(email, password);
        ChangingUserData changingUserData = new ChangingUserData(email1, password1, name1);
        changingUserData.setEmail(email1);
        changingUserData.setPassword(password1);
        changingUserData.setName(name1);

        Response response =
                given().auth().oauth2(accessToken).header("Content-Type", "application/json")
                        .and().body(changingUserData).when().patch("/api/auth/user");
        System.out.println("Статус обновления: " + response.statusCode());
        System.out.println("Тело ответа обновления: " + response.asString());
        response.then().assertThat().body("success", equalTo(true)).and()
                .statusCode(200);

    }


}
