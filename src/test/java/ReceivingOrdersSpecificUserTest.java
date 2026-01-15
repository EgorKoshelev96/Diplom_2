import api.BaseTest.BaseTest;
import api.BaseTest.utils.RequiresRegistration;
import api.OrderApi;
import api.UserApi;
import api.dto.CreatingOrder;
import api.dto.UserLogin;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.apache.http.HttpStatus.*;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;

public class ReceivingOrdersSpecificUserTest extends BaseTest {
    UserApi userApi = new UserApi();
    OrderApi orderApi = new OrderApi();

    @AfterEach
    public void deleteUser() {
        UserLogin userLogin = new UserLogin(email,password);
        userApi.deleteUser(userLogin);
    }

    @Test
    @RequiresRegistration
    public void receivingOrdersSpecificUserUnauthorizedUser() {
        Response response = orderApi.viewUserOrder();
        System.out.println(response.body().asString());
        response.then().assertThat().body("success", equalTo(false)).and()
                .statusCode(SC_UNAUTHORIZED);
    }



    @Test
    @RequiresRegistration
    public void receivingOrdersSpecificUserAuthorizedUser() {
        orderApi.setAuthData(email, password);
        createdOrder();
        getOrder();
    }

    @Step("Создание заказов")
    public Response createdOrder() {
        List<String> ingredients = Arrays.asList("61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa6f","61c0c5a71d1f82001bdaaa70");
        CreatingOrder creatingOrder = new CreatingOrder(ingredients);
        Response ingredientsResponse = orderApi.changingUserDataToken(creatingOrder);
        return ingredientsResponse;
    }
    @Step("Просмотр заказов конкретного пользователя")
    public Response getOrder() {
        Response getIngredientsResponse = orderApi.viewUserOrderToken();
        System.out.println("Тело ответа (с авторизацией): " + getIngredientsResponse.asString());
        getIngredientsResponse.then().assertThat().body("success", equalTo(true)).and()
                .statusCode(SC_OK);
        return getIngredientsResponse;
    }



}




