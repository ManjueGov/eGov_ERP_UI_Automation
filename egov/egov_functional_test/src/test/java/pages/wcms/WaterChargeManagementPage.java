package pages.wcms;

import com.gargoylesoftware.htmlunit.javascript.host.canvas.ext.WEBGL_compressed_texture_s3tc;
import entities.ptis.*;
import entities.wcms.EnclosedDocument;
import entities.wcms.FieldInspectionDetails;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;
import pages.BasePage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.jayway.awaitility.Awaitility.await;
import static java.util.concurrent.TimeUnit.SECONDS;

public class WaterChargeManagementPage extends BasePage {

    private WebDriver webDriver;

    @FindBy(id = "propertyIdentifier")
    private WebElement waterConnectionAssesmentNumberTextBox;

    @FindBy(id = "approvalDepartment")
    private WebElement approvalWaterDepartment;

    @FindBy(id = "approvalDesignation")
    private WebElement approvalWaterDesignation;

    @FindBy(id = "approvalPosition")
    private WebElement approvalWaterPosition;

    @FindBy(id = "approvalComent")
    private WebElement approvalWaterComment;

    @FindBy(id = "Forward")
    private WebElement additionalForwardButton;

    @FindBy(id = "Generate Estimation Notice")
    private WebElement generateEstimationNoticeButton;

    @FindBy(id = "moduleName")
    private WebElement searchApplicationService;

    @FindBy(id = "applicationType")
    private WebElement searchApplicationType;

    @FindBy(id = "searchapplication")
    private WebElement searchApplicationButton;

    @FindBy(id = "payBtn")
    private WebElement collectFeesButton;

    @FindBy(id = "totalamounttobepaid")
    private WebElement totalAmount;

    @FindBy(id = "instrHeaderCash.instrumentAmount")
    private WebElement amountToBePaidTextBox;

    @FindBy(css = "input[type='submit'][id='button2']")
    private WebElement button2;

    @FindBy(css = "input[type='button'][id='button2']")
    private WebElement editDCBCloseButton;

    @FindBy(id = "buttonClose")
    private WebElement closeReceiptButton;

    @FindBy(id = "Forward")
    private WebElement forwardButton;

    @FindBy(linkText = "Close")
    private WebElement closeSearchApplication;

    @FindBy(id = "approvalNumber")
    private WebElement sanctionNumber;

    @FindBy(id = "Approve")
    private WebElement commissionerApprove;

    @FindBy(id = "Sign")
    private WebElement digitalSignature;

    @FindBy(id = "Generate WorkOrder")
    private WebElement generateWorkOrder;

    @FindBy(id = "Execute Tap")
    private WebElement executeTap;

    @FindBy(id = "Generate Acknowledgement")
    private WebElement generateAcknowledgement;

    @FindBy(id = "Generate Reconnection Ack")
    private WebElement generateReConnectionAcknowledgement;

    @FindBy(id = "monthlyFee")
    private WebElement monthlyFees;

    @FindBy(id = "existingConnection.donationCharges")
    private WebElement donationCharges;

    @FindBy(id = "Create")
    private WebElement createDataEntryScreen;

    @FindBy(id = "consumerCodeData")
    private WebElement hscNumber;

    @FindBy(id = "executionDate")
    private WebElement dataEntryExecutionDate;

    @FindBy(name = "fromDate")
    private WebElement searchApplicationDate;

    @FindBy(id = "applicationNumber")
    private WebElement additionalApplicationNumber;

    @FindBy(name = "applicationNumber")
    private WebElement applicationSearchBox;

    @FindBy(linkText = "Close")
    private WebElement additionalCloseButton;

    @FindBy(id = "app-appcodo")
    private WebElement consumerNumberTextBox;

    @FindBy(id = "submitButtonId")
    private WebElement consumerSearchButton;

    @FindBy(id = "reConnectionReason")
    private WebElement reConnectionReason;

    @FindBy(id = "editDCB")
    private WebElement addEditDCB;

    @FindBy(id = "actualAmount")
    private WebElement dcbActualAmount;

    @FindBy(id = "actualCollection")
    private WebElement dcbActualCollection;

    @FindBy(id = "submitButtonId")
    private WebElement dcbSubmit;

    @FindBy(id = "chequeradiobutton")
    private WebElement chequeModeOfPaymentRadio;

    @FindBy(id = "instrumentChequeNumber")
    private WebElement chequeNumberTextBox;

    @FindBy(id = "instrumentDate")
    private WebElement chequeDateTextBox;

    @FindBy(id = "bankName")
    private WebElement bankNameInput;

    @FindBy(css = "input[id='instrumentChequeAmount'][type='text']")
    private WebElement payAmountBoxForCheque;

    @FindBy(id = "ddradiobutton")
    private WebElement ddModeofPaymentRadio;

    @FindBy(name = "consumerCode")
    private WebElement onlineConsumerCode;

    @FindBy(id = "cashradiobutton")
    private WebElement cashRadio;

    @FindBy(id = "searchapprvedapplication")
    private WebElement onlineSearchApplication;

    @FindBy(css = ".btn.btn-xs.btn-secondary.collect-hoardingWiseFee")
    private WebElement onlinePayButton;

    @FindBy(className = "justbold")
    private List<WebElement> totalOnlineAmount;

    @FindBy(id = "paymentAmount")
    private WebElement totalOnlineAmountToBePaid;

    @FindBy(name = "radioButton1")
    private WebElement axisBankRadio;

    @FindBy(id = "checkbox")
    private WebElement termsAndConditionsCheckBox;

    @FindBy(xpath = "html/body/center/table[6]/tbody/tr[3]/td/table/tbody/tr/td[3]/a/img")
    private WebElement masterCardImage;

    @FindBy(id = "CardNumber")
    private WebElement cardNumber;

    @FindBy(id = "CardMonth")
    private WebElement cardMonth;

    @FindBy(id = "CardYear")
    private WebElement cardYear;

    @FindBy(id = "Securecode")
    private WebElement cvvNumber;

    @FindBy(id = "Paybutton")
    private WebElement onlineCardPaymentButton;

    @FindBy(id = "paymentInfo")
    private WebElement onlinePaymentSuccessMessage;

    @FindBy(id = "btnGenerateReceipt")
    private WebElement onlineGenerateReceipt;

    @FindBy(id = "consumerCode")
    private WebElement reConnectionConsumerCode;

    @FindBy(id = "aplicationSearchResults")
    private WebElement applicationSearchTable;

    @FindBy(css = ".panel-title.text-center")
    private WebElement forwardMessage;

    private WebElement appRow1;

    private String message = null;

    SimpleDateFormat currentDate = new SimpleDateFormat("dd/MM/yyyy");

    public WaterChargeManagementPage(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    public void enterWaterConnectionAssessmentNumber(String number){
        waitForElementToBeClickable(waterConnectionAssesmentNumberTextBox, webDriver);
        enterText(waterConnectionAssesmentNumberTextBox, number);
    }

    public void enterWaterApprovalDetails(ApprovalDetails approvalDetails){

        waitForElementToBeClickable(approvalWaterDepartment, webDriver);
        new Select(approvalWaterDepartment).selectByVisibleText(approvalDetails.getApproverDepartment());

        waitForElementToBeClickable(approvalWaterDesignation, webDriver);
        new Select(approvalWaterDesignation).selectByVisibleText(approvalDetails.getApproverDesignation());

        waitForElementToBeClickable(approvalWaterPosition, webDriver);
        new Select(approvalWaterPosition).selectByVisibleText(approvalDetails.getApprover());

        waitForElementToBeClickable(additionalForwardButton, webDriver);
        additionalForwardButton.click();
        switchToNewlyOpenedWindow(webDriver);
    }

    public void clickOnGenerateNotice(){
        waitForElementToBeVisible(generateEstimationNoticeButton , webDriver);
        waitForElementToBeClickable(generateEstimationNoticeButton , webDriver);
        jsClick(generateEstimationNoticeButton , webDriver);
        switchToNewlyOpenedWindow(webDriver);
        webDriver.close();
        switchToPreviouslyOpenedWindow(webDriver);
    }

    public void searchWaterConnectionApplications(String connectionType , String applicationNumber){

        waitForElementToBeClickable(searchApplicationService , webDriver);
        new Select(searchApplicationService).selectByVisibleText("Water Tax");
        waitForElementToBeClickable(searchApplicationType , webDriver);
        new Select(searchApplicationType).selectByVisibleText(connectionType.replaceAll("_"," "));

        waitForElementToBeClickable(applicationSearchBox , webDriver);
        applicationSearchBox.sendKeys(applicationNumber);

        waitForElementToBeClickable(searchApplicationDate , webDriver);
        searchApplicationDate.sendKeys(currentDate.format(new Date()) , Keys.TAB);

        waitForElementToBeClickable(searchApplicationButton , webDriver);
        searchApplicationButton.click();
    }

    public void clickOnCollectCharges(){
        waitForElementToBeClickable(collectFeesButton, webDriver);
        jsClick(collectFeesButton,webDriver);
        switchToNewlyOpenedWindow(webDriver);
    }

    public void toReceiveAmount(){
        waitForElementToBeClickable(cashRadio , webDriver);
        jsClick(cashRadio ,webDriver);
        String amount = totalAmount.getAttribute("value");

        amountToBePaidTextBox.sendKeys(amount.split("\\.")[0]);

        waitForElementToBeClickable(button2 , webDriver);
        jsClick(button2, webDriver);
        switchToNewlyOpenedWindow(webDriver);
    }

    public void closeSuccessfulPaymentReceiptPage(){
        waitForElementToBeClickable(closeReceiptButton , webDriver);
        closeReceiptButton.click();
    }
    public void closeSearchApplicationPage(){

        for (String winHandle : webDriver.getWindowHandles()) {
            String title = webDriver.switchTo().window(winHandle).getCurrentUrl();
            if(title.equals("http://kurnool-qa.egovernments.org/wtms/elastic/appSearch/")){
                break;
            }
        }
        waitForElementToBeClickable(closeSearchApplication , webDriver);
        closeSearchApplication.click();

        switchToPreviouslyOpenedWindow(webDriver);
    }

    public void forward() {
        waitForElementToBeClickable(forwardButton , webDriver);
        forwardButton.click();
        switchToNewlyOpenedWindow(webDriver);
    }

    public String getAcknowledgementMessage(){
        waitForElementToBeVisible(forwardMessage , webDriver);
        message = forwardMessage.getText();
        return message;
    }

    public void closeAcknowledgementPage(){
        jsClick(closeSearchApplication , webDriver);
        switchToPreviouslyOpenedWindow(webDriver);
    }

    public void commissionerApprove(){

        waitForElementToBeClickable(sanctionNumber , webDriver);
        sanctionNumber.sendKeys("12345");
        waitForElementToBeClickable(commissionerApprove , webDriver);
        jsClick(commissionerApprove ,webDriver);

        switchToNewlyOpenedWindow(webDriver);
        closeAcknowledgementPage();
    }

    public void commissionerDigitalSignature(){
        waitForElementToBeClickable(digitalSignature , webDriver);
        digitalSignature.click();

        switchToNewlyOpenedWindow(webDriver);
        closeAcknowledgementPage();
    }

    public void generateWorkOrder(){

        waitForElementToBeClickable(generateWorkOrder , webDriver);
        jsClick(generateWorkOrder , webDriver);

        switchToNewlyOpenedWindow(webDriver);
        webDriver.close();
        switchToPreviouslyOpenedWindow(webDriver);
    }

    public String executeTap(){

        WebElement applicationNumber = webDriver.findElement(By.xpath(".//*[@id='page-container']/div[1]/div[2]/div[2]/div[4]"));
        waitForElementToBeVisible(applicationNumber , webDriver);
        String number = applicationNumber.getText();

        waitForElementToBeClickable(executeTap , webDriver);
        executeTap.click();

        switchToNewlyOpenedWindow(webDriver);
        closeAcknowledgementPage();
        return number;
    }

    public void commissionerClosureApprove(){

        waitForElementToBeClickable(commissionerApprove , webDriver);
        commissionerApprove.click();

        switchToNewlyOpenedWindow(webDriver);
        closeAcknowledgementPage();
    }

    public void toGenerateAcknowledgement(){

        waitForElementToBeClickable(generateAcknowledgement , webDriver);
        generateAcknowledgement.click();

        switchToNewlyOpenedWindow(webDriver);
        webDriver.close();

        switchToPreviouslyOpenedWindow(webDriver);
    }

    public void enterWaterDataEntryDetails(ApplicantInfo applicantInfo , String assessmentNumber){
        waitForElementToBeClickable(waterConnectionAssesmentNumberTextBox, webDriver);
        enterText(waterConnectionAssesmentNumberTextBox, assessmentNumber);
        enterText(hscNumber , applicantInfo.getHscNumber());
        enterText(dataEntryExecutionDate , applicantInfo.getConnectionDate());
    }

    public void estimationFeeDetails(){

        enterText(monthlyFees ,"1000");
        enterText(donationCharges , "100");

        createDataEntryScreen.click();
        switchToNewlyOpenedWindow(webDriver);
    }

    public String closesTheDataEntryPage(){

        WebElement successMessage = webDriver.findElement(By.cssSelector(".main-content>table>tbody>tr>td>strong"));
        String message = successMessage.getText();

        webDriver.close();
        switchToPreviouslyOpenedWindow(webDriver);

        return message;
    }

    public String findAdditionalApplicationNumber(){
        String number = webDriver.getCurrentUrl().split("\\=")[1];

        webDriver.close();

        switchToPreviouslyOpenedWindow(webDriver);

        return number;
    }


    public void enterConsumerNumber(String consumerNumber){

        waitForElementToBeClickable(consumerNumberTextBox, webDriver);
        enterText(consumerNumberTextBox, consumerNumber);

        waitForElementToBeClickable(consumerSearchButton, webDriver);
        consumerSearchButton.click();
        switchToNewlyOpenedWindow(webDriver);
    }

    public String enterReConnectionDetails(){
        WebElement acknowledgementNumber = webDriver.findElement(By.id("applicationNumber"));
        String number = acknowledgementNumber.getText();
        waitForElementToBeClickable(reConnectionReason , webDriver);
        reConnectionReason.sendKeys("Required Again");
        return number;
    }

    public void toGenerateReConnectionAcknowledgement(){

        waitForElementToBeClickable(generateReConnectionAcknowledgement , webDriver);
        generateReConnectionAcknowledgement.click();

        switchToNewlyOpenedWindow(webDriver);
        webDriver.close();

        switchToPreviouslyOpenedWindow(webDriver);
    }

    public void clickOnAddEditDCB(){
        waitForElementToBeClickable(addEditDCB , webDriver);
        addEditDCB.click();
        switchToNewlyOpenedWindow(webDriver);
    }

    public void enterDetailsOfDCB(){

        waitForElementToBeClickable(dcbActualAmount , webDriver);
        enterText(dcbActualAmount , "100");

        waitForElementToBeClickable(dcbActualCollection , webDriver);
        enterText(dcbActualCollection , "100");

        waitForElementToBeClickable(dcbSubmit , webDriver);
        dcbSubmit.click();
        switchToNewlyOpenedWindow(webDriver);
    }

    public String closesDCBPage(){
        WebElement element = webDriver.findElement(By.xpath("html/body/div[1]/div/table/tbody/tr[1]/td/strong"));
        waitForElementToBeVisible(element , webDriver);
        message = element.getText();

        waitForElementToBeClickable(editDCBCloseButton, webDriver);
        editDCBCloseButton.click();
        switchToPreviouslyOpenedWindow(webDriver);
        return message;
    }

    public void collectWaterCharges(){

        waitForElementToBeClickable(collectFeesButton , webDriver);
        collectFeesButton.click();

        switchToNewlyOpenedWindow(webDriver);
    }

    public void paymentWithMode(String mode){

        waitForElementToBeVisible(totalAmount, webDriver);
        String amount = totalAmount.getAttribute("value");
        String actualAmount = amount.split("\\.")[0];

        switch (mode){

            case "cash" :

                waitForElementToBeClickable(cashRadio , webDriver);
                jsClick(cashRadio ,webDriver);

                waitForElementToBeClickable(amountToBePaidTextBox,webDriver);
                amountToBePaidTextBox.sendKeys(actualAmount);

                break;

            case "cheque" :

                waitForElementToBeClickable(chequeModeOfPaymentRadio,webDriver);
                jsClick(chequeModeOfPaymentRadio,webDriver);

                waitForElementToBeVisible(chequeNumberTextBox,webDriver);
                chequeNumberTextBox.sendKeys("123456");
                waitForElementToBeClickable(chequeDateTextBox,webDriver);
                chequeDateTextBox.sendKeys("02/01/2017");
                waitForElementToBeClickable(bankNameInput,webDriver);
                bankNameInput.sendKeys("102");

                webDriver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
                await().atMost(10, SECONDS).until(() -> webDriver.findElement(By.id("bankcodescontainer"))
                        .findElements(By.cssSelector("ul li"))
                        .get(0).click());

                bankNameInput.sendKeys(Keys.TAB);

                waitForElementToBeClickable(payAmountBoxForCheque,webDriver);
                payAmountBoxForCheque.sendKeys(actualAmount);

                break;

            case "dd" :

                waitForElementToBeClickable(ddModeofPaymentRadio,webDriver);
                jsClick(ddModeofPaymentRadio,webDriver);

                waitForElementToBeVisible(chequeNumberTextBox,webDriver);
                chequeNumberTextBox.sendKeys("123456");
                waitForElementToBeClickable(chequeDateTextBox,webDriver);
                chequeDateTextBox.sendKeys("02/01/2017");
                waitForElementToBeClickable(bankNameInput,webDriver);
                bankNameInput.sendKeys("102");

                webDriver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
                await().atMost(10, SECONDS).until(() -> webDriver.findElement(By.id("bankcodescontainer"))
                        .findElements(By.cssSelector("ul li"))
                        .get(0).click());

                waitForElementToBeClickable(payAmountBoxForCheque,webDriver);
                payAmountBoxForCheque.sendKeys(actualAmount);

                break;

        }
        jsClick(button2, webDriver);
        switchToNewlyOpenedWindow(webDriver);
    }

    public void onlinePaymentLink(){
        webDriver.navigate().to("http://kurnool-uat.egovernments.org/wtms/search/waterSearch/");
    }

    public void enterOnlineConsumerNumber(String consumerNumber){
        waitForElementToBeClickable(onlineConsumerCode , webDriver);
        onlineConsumerCode.sendKeys(consumerNumber);

        waitForElementToBeClickable(onlineSearchApplication , webDriver);
        onlineSearchApplication.click();
    }

    public void clickOnOnlinePayButton(){

        waitForElementToBeClickable(onlinePayButton , webDriver);
        onlinePayButton.click();
    }

    public void selectBankDetails(){

        waitForElementToBeVisible(totalOnlineAmount.get(1) , webDriver);
        String amount = totalOnlineAmount.get(1).getText();

        waitForElementToBeClickable(totalOnlineAmountToBePaid , webDriver);
        totalOnlineAmountToBePaid.sendKeys(amount.split("\\.")[0]);

        waitForElementToBeClickable(axisBankRadio , webDriver);
        jsClick(axisBankRadio ,webDriver);

        waitForElementToBeClickable(termsAndConditionsCheckBox , webDriver);
        termsAndConditionsCheckBox.click();

        waitForElementToBeClickable(button2, webDriver);
        button2.click();

        waitForElementToBeClickable(masterCardImage , webDriver);
        masterCardImage.click();
    }

    public void enterCardDetails(){

        waitForElementToBeClickable(cardNumber , webDriver);
        cardNumber.sendKeys("5123456789012346");

        waitForElementToBeClickable(cardMonth , webDriver);
        cardMonth.sendKeys("05");

        waitForElementToBeClickable(cardYear ,webDriver);
        cardYear.sendKeys("17");

        waitForElementToBeClickable(cvvNumber , webDriver);
        cvvNumber.sendKeys("123");

        waitForElementToBeClickable(onlineCardPaymentButton , webDriver);
        onlineCardPaymentButton.click();
    }

    public String onlinePaymentSuccess(){

        waitForElementToBeVisible(onlinePaymentSuccessMessage , webDriver);
        message = onlinePaymentSuccessMessage.getText();
        return message;
    }

    public void onlineGenerateReceipt(){
        waitForElementToBeClickable(onlineGenerateReceipt , webDriver);
        onlineGenerateReceipt.click();

        waitForElementToBeClickable(closeReceiptButton , webDriver);
        if(closeReceiptButton.isDisplayed()){
            webDriver.close();
        }
    }

    public void closeCollectChargesReceipt(){
        waitForElementToBeClickable(closeReceiptButton , webDriver);
        closeReceiptButton.click();

        switchToPreviouslyOpenedWindow(webDriver);
    }

    public void openSearchApplication(String applicationNumber) {
        appRow1 = getSearchApplicationRowFor(applicationNumber);
        waitForElementToBeClickable(appRow1 , webDriver);
        jsClick(appRow1 , webDriver);
        switchToNewlyOpenedWindow(webDriver);
    }

    private WebElement getSearchApplicationRowFor(String applicationNumber) {

        List<WebElement> applicationRows = applicationSearchTable.findElement(By.tagName("tbody")).findElements(By.tagName("tr"));

        for (WebElement applicationRow1 : applicationRows) {
            if (applicationRow1.findElements(By.tagName("td")).get(1).getText().contains(applicationNumber))
                return applicationRow1;
        }

        throw new RuntimeException("No application row found for -- " + applicationNumber);
    }
}
