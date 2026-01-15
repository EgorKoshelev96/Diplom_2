import api.BaseTest.BaseTest;
import api.BaseTest.utils.RequiresRegistration;
import api.OrderApi;
import api.UserApi;
import static org.apache.http.HttpStatus.*;
import api.dto.CreatingOrder;
import api.dto.UserLogin;
import api.dto.CreatingUser;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.equalTo;

public class CreatingOrderTest extends BaseTest {
    UserApi userApi = new UserApi();
    OrderApi orderApi = new OrderApi();


    @AfterEach
    public void deleteUser() {
        UserLogin userLogin = new UserLogin(email,password);
        userApi.deleteUser(userLogin);
        }

    @Test
    @RequiresRegistration
    public void creatingOrderwWithoutAuthorization() {
        List<String> ingredients = Arrays.asList("61c0c5a71d1f82001bdaaa73", "61c0c5a71d1f82001bdaaa74");
        CreatingOrder creatingOrder = new CreatingOrder(ingredients);
        Response response =
                orderApi.changingUserData(creatingOrder);
        response.then().assertThat().body("success", equalTo(true)).and()
                .statusCode(SC_OK);
    }

    @ParameterizedTest
    @MethodSource("creatingOrderIngredients")
    void creatingOrderTest(List<String> ingredients) {
        CreatingUser creatingUser = new CreatingUser(email, password, name);
        UserLogin userLogin = new UserLogin(email, password);
        CreatingOrder creatingOrder = new CreatingOrder(ingredients);
        userApi.registerUser(creatingUser);
        userApi.userLogin(userLogin);

        creatingOrder.setIngredients(ingredients);

        Response response =
                orderApi.changingUserData(creatingOrder);

        int actualStatusCode = response.statusCode();

        if (actualStatusCode == SC_OK) {
            boolean success = response.jsonPath().getBoolean("success");
            System.out.println("ok: " + success);
        } else if (actualStatusCode == SC_BAD_REQUEST) {
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
