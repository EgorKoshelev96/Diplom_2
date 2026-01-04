import com.github.javafaker.Faker;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class СreatingUserTest {
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
    public void deleteUser(){
        UserLogin userLogin = new UserLogin(email,password);
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
    public void uniqueUser() {
        СreatingUser creatingUser = new СreatingUser(email,password,name);
        Response response =
                given().header("Content-Type", "application/json").and()
                        .body(creatingUser).when().post("/api/auth/register");
        System.out.println(response.body().asString());
        response.then().assertThat().body("success", equalTo(true)).and()
                .statusCode(200);
    }
    @Test
    public void createUserWhoIsAlreadyRegistered() {
        СreatingUser createUserWhoIsAlreadyRegistered = new СreatingUser(email, password, name);
        Response response =
                given().header("Content-Type", "application/json").and()
                        .body(createUserWhoIsAlreadyRegistered).when().post("/api/auth/register");
        System.out.println(response.body().asString());
        response.then().assertThat().body("success", equalTo(true)).and()
                .statusCode(200);
        Response responseSameLogin=
                given().header("Content-Type", "application/json")
                        .and().body(createUserWhoIsAlreadyRegistered).when().post("/api/auth/register");
        responseSameLogin.then().statusCode(403).body("message", equalTo("User already exists"));
        System.out.println(responseSameLogin.body().asString());
    }

    @Test
    public void oneOfRequiredFieldsNotFilled(){
        emailIsNotFilled();
        passwordIsNotFilled();
        nameIsNotFilled();

    }
    @Step("Поле email не заполнено")
    public Response emailIsNotFilled() {
        СreatingUser emailIsNotFilled = new СreatingUser(null, password, name);
        Response email =
                given().header("Content-Type", "application/json")
                        .and().body(emailIsNotFilled).when().post("/api/auth/register");
        email.then().statusCode(403).body("message", equalTo("Email, password and name are required fields"));
        System.out.println(email.body().asString());
        return email;

    }
    @Step("Поле password не заполнено")
    public Response passwordIsNotFilled() {
        СreatingUser passwordIsNotFilled = new СreatingUser(email, null, name);
        Response password =
                given().header("Content-Type", "application/json")
                        .and().body(passwordIsNotFilled).when().post("/api/auth/register");
        password.then().statusCode(403).body("message", equalTo("Email, password and name are required fields"));
        System.out.println(password.body().asString());
        return password;
    }
    @Step("Поле name не заполнено")
    public Response nameIsNotFilled() {
        СreatingUser nameIsNotFilled = new СreatingUser(email, password, null);
        Response name =
                given().header("Content-Type", "application/json")
                        .and().body(nameIsNotFilled).when().post("/api/auth/register");
        name.then().statusCode(403).body("message", equalTo("Email, password and name are required fields"));
        System.out.println(name.body().asString());
        return name;
    }

}
