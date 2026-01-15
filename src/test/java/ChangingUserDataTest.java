import api.BaseTest.BaseTest;
import api.UserApi;
import api.dto.ChangingUserData;
import api.dto.UserLogin;
import io.restassured.response.Response;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.apache.http.HttpStatus.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class ChangingUserDataTest extends BaseTest {
    UserApi userApi = new UserApi();
    private String accessToken;


    private String getAccessToken(String email, String password) {
        UserLogin userLogin = new UserLogin(email, password);
        Response loginResponse = userApi.userLogin(userLogin);
        String fullToken = loginResponse.jsonPath().getString("accessToken");
        return accessToken = fullToken.replace("Bearer ", "");
    }


    @ParameterizedTest
    @CsvSource({
            "Koshe@mail.ru,12345m,Egor",
            "Koshele@mail.ru,12345m58,Egor",
            "Koshele@mail.ru,12345m58,Egor_Koshelev",
    })
    public void changingUserDataWithoutWuthorization(String email, String password, String name) {
        ChangingUserData changingUserData = new ChangingUserData(email, password, name);

        Response response =
                userApi.changingUserData(changingUserData);
        response.then().assertThat().body("success", equalTo(false)).and()
                .statusCode(SC_UNAUTHORIZED);

    }

    @ParameterizedTest
    @CsvSource({
            "QWEQRE@EQ.RU,123456b,Koshele@mail.ru,12345m58,Egor",
            "Koshele@mail.ru,12345m58,Koshelev@mail.ru,12345m,Egor",
            "Koshelev@mail.ru,12345m,Koshele@mail.ru,12345m58,Egor_Koshelev",
            "Koshele@mail.ru,12345m58,QWEQRE@EQ.RU,123456b,Egor"

    })
    public void changingUserDataWithAuthorization(String email, String password, String email1, String password1, String name1) {
        accessToken = getAccessToken(email, password);
        ChangingUserData changingUserData = new ChangingUserData(email1, password1, name1);

        Response response =
                given().auth().oauth2(accessToken).header("Content-Type", "application/json")
                        .and().body(changingUserData).when().patch("/api/auth/user");
        System.out.println("Статус обновления: " + response.statusCode());
        System.out.println("Тело ответа обновления: " + response.asString());
        response.then().assertThat().body("success", equalTo(true)).and()
                .statusCode(SC_OK);

    }


}
