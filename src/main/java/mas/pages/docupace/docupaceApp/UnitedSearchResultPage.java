package mas.pages.docupace.docupaceApp;

import mas.controls.Button;
import mas.controls.Label;
import mas.dto.FlightData;
import mas.pages.AbstractPage;
import mas.utils.runTime.Step;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.Duration.ofSeconds;

public class UnitedSearchResultPage extends AbstractPage {

    private static final String CSS_FLIGHT_TIME_FLIGHT_TIME_DEPART = ".flight-time.flight-time-depart";
    private static final String CSS_FLIGHT_TIME_FLIGHT_TIME_ARRIVE = ".flight-time.flight-time-arrive";
    private static final String CSS_STOPS = ".flight-connection-container";
    private static final String CSS_DURATION = ".flight-duration.otp-tooltip-trigger";

    @FindBy(id = "column-ECO-BASIC")
    private Button btEconomyBasic;
    @FindBy(id = "a-results-show-all")
    private Button btShowAllFlight;
    @FindBy(css = CSS_FLIGHT_TIME_FLIGHT_TIME_DEPART)
    private List<Label> listLbFlightDeparture;
    @FindBy(css = "li.flight-block")
    private List<WebElement> listElFlightBlock;
    @FindBy(css = CSS_FLIGHT_TIME_FLIGHT_TIME_ARRIVE)
    private List<Label> listLbFlightArrival;
    @FindBy(css = CSS_STOPS)
    private List<Label> listLbFlightStops;
    @FindBy(css = CSS_DURATION)
    private List<Label> listLbFlightDuration;
    @FindBy(css = "button[class*='BookFlightForm-bookFlightForm__findFlightBtn']")
    private Button btFindFlight;


    public UnitedSearchResultPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public boolean isPageContentValid() {

        wait.waitForAjax(90);
        wait.waitUntilElementIsInvisible(By.cssSelector(".spinner-container"), 20);
        return verifyIfTextPresent("Departure:", By.cssSelector(".lof-origin2"));
    }

    public List<String> geListLbFlightDeparture() {
        return listLbFlightDeparture.stream().map(Label::getText).map(String::trim).collect(Collectors.toList());
    }


    public List<FlightData> getAvailableFlightDataWithEcoBasicPrice() {
        List<FlightData> listFlightData = new ArrayList<>();
        for (WebElement elFlightBlock : listElFlightBlock) {
            wait.turnOffImplicitTimeout();
            if (elFlightBlock.findElements(By.cssSelector("[id*='product_ECO-BASIC']")).size() > 0) {
                FlightData flightData = new FlightData();
                flightData.setEconomyPrice(getStringFromBlock(elFlightBlock, "[id*='product_ECO-BASIC']"));
                flightData.setDepart(getStringFromBlock(elFlightBlock, CSS_FLIGHT_TIME_FLIGHT_TIME_DEPART));
                flightData.setArrive(getStringFromBlock(elFlightBlock, CSS_FLIGHT_TIME_FLIGHT_TIME_ARRIVE));
                flightData.setStops(getStringFromBlock(elFlightBlock, CSS_STOPS));
                flightData.setDuration((getStringFromBlock(elFlightBlock, CSS_DURATION)));
                listFlightData.add(flightData);
            }
            wait.turnOnImplicitTimeout();
        }
        return listFlightData;
    }

    @NotNull
    private String getStringFromBlock(WebElement elFlightBlock, String cssFlightTimeFlightTimeDepart) {
        return elFlightBlock.findElement(By.cssSelector(cssFlightTimeFlightTimeDepart)).getText().replaceAll("\n", " ");
    }

    @Step("ShowAllFlight")
    public UnitedSearchResultPage clickBtShowAllFlight() {
        btShowAllFlight.click();
        wait.waitForAjax(60);
        javaScript.call("arguments[0].scrollIntoView(true);", isElementPresent(By.cssSelector(".lof-origin2")));
        return addPageToFactory(UnitedSearchResultPage.class);
    }

    @Step("Click  sort by Economy")
    public UnitedSearchResultPage clickBtEconomyBasic() {
        btEconomyBasic.click();
        wait.waitForAjax(60);
        javaScript.call("arguments[0].scrollIntoView(true);", isElementPresent(By.cssSelector(".lof-origin2")));
        return addPageToFactory(UnitedSearchResultPage.class);
    }

    @Step("is sorted by economy ")
    public boolean isSortedByEconomyASC() {
        return hasClass(btEconomyBasic, "ascending")
                && hasClass(btEconomyBasic, "bg-economy")
                && hasClass(btEconomyBasic, "active") ;
    }

    public boolean hasClass(Button element, String sortedClass) {
        String classes = element.getAttribute("class");
        for (String c : classes.split(" ")) {
            if (c.equals(sortedClass)) {
                return true;
            }
        }

        return false;
    }


}