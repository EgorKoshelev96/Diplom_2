import api.BaseTest.BaseTest;
import api.BaseTest.utils.RequiresRegistration;
import api.UserApi;
import api.dto.CreatingUser;

import api.dto.UserLogin;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;

public class СreatingUserTest extends BaseTest {
    UserApi userApi = new UserApi();

    @AfterEach
    public void deleteUser(TestInfo testInfo) {
        String testMethodName = testInfo.getTestMethod().orElseThrow().getName();
        if ("oneOfRequiredFieldsNotFilled".equals(testMethodName)) {
            System.out.println("пропускаем удаление в тесте oneOfRequiredFieldsNotFilled" + testMethodName);
            return;
        }
        UserLogin userLogin = new UserLogin(email,password);
        userApi.deleteUser(userLogin);
    }

    @Test
    public void uniqueUser() {
        CreatingUser creatingUser = new CreatingUser(email,password,name);
        Response response = userApi.registerUser(creatingUser);
        System.out.println(response.body().asString());
        response.then().assertThat().body("success", equalTo(true)).and()
                .statusCode(SC_OK);
    }
    @Test
    @RequiresRegistration
    public void createUserWhoIsAlreadyRegistered() {
        CreatingUser creatingUser = new CreatingUser(email, password, name);
        Response responseSameLogin =
                userApi.registerUser(creatingUser);
        responseSameLogin.then().statusCode(SC_FORBIDDEN).body("message", equalTo("User already exists"));
        System.out.println(responseSameLogin.body().asString());
    }

    @Test
    public void oneOfRequiredFieldsNotFilled() {
        emailIsNotFilled();
        passwordIsNotFilled();
        nameIsNotFilled();

    }
    @Step("Поле email не заполнено")
    public Response emailIsNotFilled() {
        CreatingUser emailIsNotFilled = new CreatingUser(null, password, name);
        Response email =
                userApi.registerUser(emailIsNotFilled);
        email.then().statusCode(SC_FORBIDDEN).body("message", equalTo("Email, password and name are required fields"));
        System.out.println(email.body().asString());
        return email;

    }
    @Step("Поле password не заполнено")
    public Response passwordIsNotFilled() {
        CreatingUser passwordIsNotFilled = new CreatingUser(email, null, name);
        Response password =
                userApi.registerUser(passwordIsNotFilled);
        password.then().statusCode(SC_FORBIDDEN).body("message", equalTo("Email, password and name are required fields"));
        System.out.println(password.body().asString());
        return password;
    }
    @Step("Поле name не заполнено")
    public Response nameIsNotFilled() {
        CreatingUser nameIsNotFilled = new CreatingUser(email, password, null);
        Response name =
                userApi.registerUser(nameIsNotFilled);
        name.then().statusCode(SC_FORBIDDEN).body("message", equalTo("Email, password and name are required fields"));
        System.out.println(name.body().asString());
        return name;
    }

}
