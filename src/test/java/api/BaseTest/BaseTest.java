package api.BaseTest;
import api.BaseTest.utils.RequiresRegistration;
import api.UserApi;
import api.dto.CreatingUser;
import io.restassured.RestAssured;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;

import java.lang.reflect.Method;


public class BaseTest {
    UserApi userApi = new UserApi();
    protected Faker faker = new Faker();
    protected String email;
    protected String password;
    protected String name;


    @BeforeEach
    public void setUp(TestInfo testInfo) {
        email = faker.internet().emailAddress();
        password = faker.internet().password(6, 10, true, true, true);
        name = faker.name().firstName();
        RestAssured.baseURI = "https://stellarburgers.education-services.ru";
        Method testMethod = testInfo.getTestMethod().orElse(null);
        if (testMethod != null && testMethod.isAnnotationPresent(RequiresRegistration.class)) {
            CreatingUser creatingUser = new CreatingUser(email, password, name);
            userApi.registerUser(creatingUser);

        }

    }
}