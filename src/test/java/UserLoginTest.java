import api.BaseTest.BaseTest;
import api.BaseTest.utils.RequiresRegistration;
import api.UserApi;
import api.dto.UserLogin;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;

public class UserLoginTest extends BaseTest {
    UserApi userApi = new UserApi();


    @AfterEach
    public void deleteUser(TestInfo testInfo) {
        String testMethodName = testInfo.getTestMethod().orElseThrow().getName();
        if ("loginWithIncorrectLoginAndPassword".equals(testMethodName)) {
            System.out.println("пропускаем удаление в тесте loginWithIncorrectLoginAndPassword" + testMethodName);
            return;
        }
        UserLogin userLogin = new UserLogin(email,password);
        userApi.deleteUser(userLogin);
        }

    @Test
    @RequiresRegistration
    public void loginUnderExistingUser() {
       UserLogin userLogin = new UserLogin(email, password);
        Response responseLogin =
                userApi.userLogin(userLogin);
        responseLogin.then().assertThat().body("success", equalTo(true)).and()
                    .statusCode(SC_OK);
        }
    @Test
    public void loginWithIncorrectLoginAndPassword() {
        UserLogin userLogin = new UserLogin(email, password);
        Response response =
                userApi.userLogin(userLogin);
        response.then().statusCode(SC_UNAUTHORIZED).body("message", equalTo("email or password are incorrect"));
        System.out.println(response.body().asString());
        }

    }

