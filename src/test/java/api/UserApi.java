package api;
import static org.apache.http.HttpStatus.*;

import api.dto.ChangingUserData;
import api.dto.CreatingUser;
import api.dto.UserLogin;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class UserApi {
    @Step("Регистрация пользователя")
    public Response registerUser(CreatingUser creatingUser) {
        return given().header("Content-Type", "application/json").and()
                .body(creatingUser).when().post("/api/auth/register");
    }
    @Step("Авторизация пользователя")
    public Response userLogin(UserLogin userLogin) {
        return given().log().all().header("Content-Type", "application/json")
                .body(userLogin).post("/api/auth/login");
    }
    @Step("Изменение данных пользователя")
    public Response changingUserData(ChangingUserData changingUserData) {
        return given().header("Content-Type", "application/json")
                .and().body(changingUserData).when().patch("/api/auth/user");
    }
    @Step("Удаление пользователя")
    public void deleteUser(UserLogin userLogin) {
        Response loginResponse = userLogin(userLogin);

        if (loginResponse.statusCode() != SC_OK) {
            throw new RuntimeException("Авторизация не удалась. Статус: " + loginResponse.statusCode());
        }

        String fullToken = loginResponse.jsonPath().getString("accessToken");
        String accessToken = fullToken.replace("Bearer ", "");

        given().auth().oauth2(accessToken).header("Content-Type", "application/json")
                .body(userLogin).delete("/api/auth/user").then().statusCode(SC_ACCEPTED);
    }


}
