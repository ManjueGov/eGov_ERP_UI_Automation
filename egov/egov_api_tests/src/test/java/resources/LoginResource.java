package resources;

import com.jayway.restassured.response.Response;
import utils.APILogger;
import utils.Properties;

import java.util.Map;

import static com.jayway.restassured.RestAssured.given;

public class LoginResource {

    public Response login(Map json , String path) {

        new APILogger().log("login request started -- " + json);

        Response response = given().request().with()
                .urlEncodingEnabled(false)
                .header("Content-type", "application/x-www-form-urlencoded")
                .header("Authorization", "Basic ZWdvdi11c2VyLWNsaWVudDplZ292LXVzZXItc2VjcmV0")
                .params(json)
                .when()
                .post(path + Properties.loginUrl);

        return response;
    }

    public Response logout(String accessToken) {

        new APILogger().log("logout request started-- " + accessToken);

        Response response = given().request().with()
                .urlEncodingEnabled(false)
                .header("Content-type", "application/x-www-form-urlencoded")
                .header("Authorization", "Basic ZWdvdi11c2VyLWNsaWVudDplZ292LXVzZXItc2VjcmV0")
                .when()
                .post(Properties.serverUrl + Properties.logoutUrl + accessToken);

        return response;
    }
}