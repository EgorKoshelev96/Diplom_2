import com.github.javafaker.Faker;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class ReceivingOrdersSpecificUserTest {
    private static Faker faker = new Faker();
    private static String email;
    private static String password;
    private static String name;

    String accessToken;

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.education-services.ru";
        email = faker.internet().emailAddress();
        password = faker.internet().password(6, 10, true, true, true);
        name = faker.name().firstName();

    }

    @Test
    public void receivingOrdersSpecificUserUnauthorizedUser() {
        creatingUser();
        viewingOrdersUnauthorizedUser();
    }


    @Step("Создание пользователя")
    public Response creatingUser() {
        СreatingUser creatingUser = new СreatingUser(email, password, name);
        Response response =
                given().header("Content-Type", "application/json").and()
                        .body(creatingUser).when().post("/api/auth/register");
        System.out.println(response.body().asString());
        response.then().assertThat().body("success", equalTo(true)).and()
                .statusCode(200);
        return response;
        }
    @Step("Просмотр заказов конкретного пользователя")
    public Response viewingOrdersUnauthorizedUser() {
        Response response =
                given().header("Content-Type", "application/json").and()
                        .when().get("/api/orders");
        System.out.println(response.body().asString());
        response.then().assertThat().body("success", equalTo(false)).and()
                .statusCode(401);
        return response;
    }

    @Test
    public void receivingOrdersSpecificUserAuthorizedUser() {
        creatingUserValid();
        loginUser();
        createdOrder();
        getOrder();
    }
    @Step("Создание пользователя")
    public Response creatingUserValid() {
        СreatingUser creatingUser = new СreatingUser(email, password, name);
        Response response =
                given().header("Content-Type", "application/json").and()
                        .body(creatingUser).when().post("/api/auth/register");
        System.out.println(response.body().asString());
        response.then().assertThat().body("success", equalTo(true)).and()
                .statusCode(200);
        return response;
    }
    @Step
    public Response loginUser(){
        UserLogin userLogin = new UserLogin(email, password);
        Response loginResponse = given().header("Content-Type", "application/json")
                .and().body(userLogin).when().post("/api/auth/login");
        String fullToken = loginResponse.jsonPath().getString("accessToken");
        accessToken = fullToken.replace("Bearer ", "");
        return loginResponse;

    }
    @Step("Создание заказов")
    public Response createdOrder() {
        List<String> ingredients = Arrays.asList("61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa6f","61c0c5a71d1f82001bdaaa70");
        CreatingOrder сreatingOrder = new CreatingOrder(ingredients);
        Response ingredientsResponse = given().auth().oauth2(accessToken).header("Content-Type", "application/json")
                .and().body(сreatingOrder).when().post("/api/orders");
        return ingredientsResponse;
    }
    @Step("Просмотр заказов конкретного пользователя")
    public Response getOrder() {
        Response getIngredientsResponse = given().auth().oauth2(accessToken).header("Content-Type", "application/json")
                .and().when().get("/api/orders");
        System.out.println("Тело ответа (с авторизацией): " + getIngredientsResponse.asString());
        getIngredientsResponse.then().assertThat().body("success", equalTo(true)).and()
                .statusCode(200);
        return getIngredientsResponse;
    }



}




