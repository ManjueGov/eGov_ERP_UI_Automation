package tests.propertyTax.masters;

import builders.propertyTax.RequestInfoBuilder;
import builders.propertyTax.masters.occupancy.OccupancyMasterRequestBuilder;
import builders.propertyTax.masters.occupancy.OccupancyMastersBuilder;
import com.jayway.restassured.response.Response;
import entities.requests.propertyTax.RequestInfo;
import entities.requests.propertyTax.masters.occupancy.OccuapancyMasters;
import entities.requests.propertyTax.masters.occupancy.OccupancyMasterRequest;
import entities.responses.propertyTax.masters.occupancy.create.OccupancyMasterResponse;
import org.testng.Assert;
import org.testng.annotations.Test;
import resources.propertyTax.masters.OccupancyMasterResource;
import tests.BaseAPITest;
import utils.*;

import java.io.IOException;

import static data.UserData.NARASAPPA;

public class OccupancyMasterVerificationTest extends BaseAPITest {

    OccuapancyMasters[] occuapancyMasters;
    RequestInfo requestInfo;
    PTISMasterSearchHelper helper;

    public OccupancyMasterVerificationTest() {
        occuapancyMasters = new OccuapancyMasters[1];
    }

    @Test(groups = {Categories.PTIS, Categories.SANITY})
    public void OccupancyMasterTest() throws IOException {
        LoginAndLogoutHelper.login(NARASAPPA);   //Login
        helper = new PTISMasterSearchHelper();
        requestInfo = new RequestInfoBuilder().withAuthToken(scenarioContext.getAuthToken()).build();
        OccupancyMasterResponse create = createOccupancyMaster();   //Create
        helper.searchOccupancyMaster(create);     //Search

        OccupancyMasterResponse update = updateOccupancyMaster(create.getOccuapancyMasters()[0].getId());   //Update
        helper.searchOccupancyMaster(update);     //Search
        LoginAndLogoutHelper.logout(); //Logout
    }

    private OccupancyMasterResponse createOccupancyMaster() throws IOException {
        new APILogger().log("Create Occupancy Master is Started");
        occuapancyMasters[0] = new OccupancyMastersBuilder().withName("Test_" + get5DigitRandomInt())
                .withCode(get5DigitRandomInt()).withNameLocal("Test_" + get5DigitRandomInt()).build();
        OccupancyMasterRequest request = new OccupancyMasterRequestBuilder().withRequestInfo(requestInfo)
                .withOccupancyMaster(occuapancyMasters).build();

        Response response = new OccupancyMasterResource().create(RequestHelper.getJsonString(request));
        OccupancyMasterResponse responseObject = checkAsserts(request, response);
        new APILogger().log("Create Occupancy Master is Completed");

        return responseObject;
    }

    private OccupancyMasterResponse updateOccupancyMaster(int id) throws IOException {
        new APILogger().log("Update Occupancy Master is Started");
        occuapancyMasters[0] = new OccupancyMastersBuilder().withId(id).withName("Test_" + get5DigitRandomInt())
                .withCode(get5DigitRandomInt()).withNameLocal("Test_" + get5DigitRandomInt()).build();
        OccupancyMasterRequest request = new OccupancyMasterRequestBuilder().withRequestInfo(requestInfo)
                .withOccupancyMaster(occuapancyMasters).build();

        Response response = new OccupancyMasterResource().update(RequestHelper.getJsonString(request));
        OccupancyMasterResponse responseObject = checkAsserts(request, response);
        Assert.assertEquals(responseObject.getOccuapancyMasters()[0].getId(), id);
        new APILogger().log("Update Occupancy Master is Completed");

        return responseObject;
    }

    private OccupancyMasterResponse checkAsserts(OccupancyMasterRequest request, Response response) throws IOException {
        OccupancyMasterResponse responseObject = (OccupancyMasterResponse)
                ResponseHelper.getResponseAsObject(response.asString(), OccupancyMasterResponse.class);

        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertEquals(responseObject.getOccuapancyMasters()[0].getName(), request.getOccuapancyMasters()[0].getName());
        Assert.assertEquals(responseObject.getOccuapancyMasters()[0].getNameLocal(), request.getOccuapancyMasters()[0].getNameLocal());
        Assert.assertEquals(responseObject.getOccuapancyMasters()[0].getCode(), request.getOccuapancyMasters()[0].getCode());
        Assert.assertEquals(responseObject.getResponseInfo().getStatus(), "SUCCESSFUL");

        return responseObject;
    }
}