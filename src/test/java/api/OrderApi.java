package api;
import static org.apache.http.HttpStatus.*;

import api.dto.CreatingOrder;
import api.dto.UserLogin;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class OrderApi {
    UserApi userApi = new UserApi();
    private String email;
    private String password;

    public void setAuthData(String email, String password) {
        this.email = email;
        this.password = password;
    }
    @Step("Создание заказа")
    public Response changingUserData(CreatingOrder creatingOrder) {
        return given().header("Content-Type", "application/json")
                .and().body(creatingOrder).when().post("/api/orders");
    }
    @Step("Просмотр заказов конкретного пользователя")
    public Response viewUserOrder() {
        return given().header("Content-Type", "application/json")
                .and().when().get("/api/orders");
    }
    @Step("Создание заказа с токеном")
    public Response changingUserDataToken(CreatingOrder creatingOrder) {
        UserLogin userLogin = new UserLogin(email, password);
        System.out.println("Email: " + email + ", Password: " + password);
        Response loginResponse = userApi.userLogin(userLogin);

        if (loginResponse.statusCode() != SC_OK) {
            throw new RuntimeException("Авторизация не удалась. Статус: " + loginResponse.statusCode());
        }

        String fullToken = loginResponse.jsonPath().getString("accessToken");
        String accessToken = fullToken.replace("Bearer ", "");
        return given().auth().oauth2(accessToken).header("Content-Type", "application/json")
                .and().body(creatingOrder).when().post("/api/orders");
    }

    @Step("Просмотр заказов конкретного пользователя с токеном")
    public Response viewUserOrderToken() {
        UserLogin userLogin = new UserLogin(email, password);
        Response loginResponse = userApi.userLogin(userLogin);

        if (loginResponse.statusCode() != SC_OK) {
            throw new RuntimeException("Авторизация не удалась. Статус: " + loginResponse.statusCode());
        }

        String fullToken = loginResponse.jsonPath().getString("accessToken");
        String accessToken = fullToken.replace("Bearer ", "");
        return given().auth().oauth2(accessToken).header("Content-Type", "application/json")
                .and().when().get("/api/orders");
    }



}
