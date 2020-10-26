package mas.pages.docupace.docupaceApp;

import mas.controls.*;
import mas.models.common.enums.Airports;
import mas.models.common.enums.FlightClass;
import mas.models.common.enums.Month;
import mas.pages.AbstractPage;
import mas.utils.runTime.Step;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static java.time.Duration.ofSeconds;

public class UnitedDashboardPage extends AbstractPage {

    @FindBy(id = "bookFlightOriginInput")
    private TextBox tbBookFlightOriginInput;
    @FindBy(id = "bookFlightDestinationInput")
    private TextBox tbBookFlightDestinationInput;
    @FindBy(id = "oneway")
    private Element radioOneWay;
    @FindBy(id = "DepartDate")
    private Element elDepartDate;
    @FindBy(css = "button[class*='BookFlightForm-bookFlightForm__findFlightBtn']")
    private Button btFindFlight;
    @FindBy(css = "bookFlightModel.passengers")
    private Element elCountPassengers;
    @FindBy(css = "[aria-labelledby='cabinClassLabel']")
    private Element elCabinClass;
    @FindBy(css = "[role='tooltip']")
    private Element elTooltipData;
    @FindBy(css = "[role='listbox']")
    private Element elListBox;
    @FindBy(css = "[role='application']")
    private Element elCalendar;


    //CalendarElements
    @FindBy(css = "button.DayPickerNavigation_rightButton__horizontal")
    private Button nextMonth;
    @FindBy(css = "button.DayPickerNavigation_leftButton__horizontal")
    private Button previousMonth;
    @FindBy(xpath = "//div[contains(@class, 'CalendarMonthGrid_month__horizonta') and not(contains(@class, 'hidden'))]")
    private Element elMonth;


    public UnitedDashboardPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public boolean isPageContentValid() {
        wait.waitForAjax();
        return verifyIfTextPresent("Book", By.cssSelector("li#travelTab"));
    }


    @Step("fill flight From")
    public void fillFlightFrom(Airports airports) {
        selectAirport(airports, tbBookFlightOriginInput);
    }

    @Step("fill flight From")
    public void fillFlightTo(Airports airports) {
        selectAirport(airports, tbBookFlightDestinationInput);
    }

    private void selectAirport(Airports airports, TextBox tbBookFlightDestinationInput) {
        tbBookFlightDestinationInput.click();
        tbBookFlightDestinationInput.clearByDeletion();

        tbBookFlightDestinationInput.sendKeys(airports.getAirPortSearch());
        Wait<Boolean> fluentWait = new FluentWait<>(true).withTimeout(ofSeconds(5)).pollingEvery(ofSeconds(1)).ignoring(NoSuchElementException.class);
        try {
            fluentWait.until(x -> ExpectedConditions.visibilityOf(elTooltipData));
        } catch (TimeoutException ex) {
            Assert.fail("List not displayed");
        }
        List<Element> dropDownElement = elTooltipData.findAllElements(By.cssSelector("button")).stream().filter(el -> el.getText().contains(airports.getAirPortNameInList())).collect(Collectors.toList());
        dropDownElement.get(0).click();
    }

    @Step("Select flight type")
    public void selectClassFlight(FlightClass flightClass) {
        elCabinClass.click();
        Wait<Boolean> fluentWait = new FluentWait<>(true).withTimeout(ofSeconds(5)).pollingEvery(ofSeconds(1)).ignoring(NoSuchElementException.class);
        try {
            fluentWait.until(x -> ExpectedConditions.visibilityOf(elListBox));
        } catch (TimeoutException ex) {
            Assert.fail("List not displayed");
        }
        elListBox.findElement(By.xpath(String.format("//li[@aria-label='%s'][@role='option']", flightClass.getClassFlight()))).click();
    }

    @Step("Select flight type")
    public void selectDepartDate(int day, Month month, int year) {
        if (day < 1 || day > 31 || year < 2020) {
            Assert.fail("not allow value in date");
        }
        elDepartDate.click();
        Wait<Boolean> fluentWait = new FluentWait<>(true).withTimeout(ofSeconds(5)).pollingEvery(ofSeconds(1)).ignoring(NoSuchElementException.class);
        try {
            fluentWait.until(x -> ExpectedConditions.visibilityOf(elCalendar));
        } catch (TimeoutException ex) {
            Assert.fail("Calendar not displayed");
        }
        int i = 0; // for finish loop always
        while (!(elMonth.findElement(By.cssSelector("div.CalendarMonth_caption")).getText().equals(month.getMonthName() + " " + year)) && i < 100) {
            nextMonth.click();
            i++;
        }
        elMonth.findElement(By.xpath(String.format("//td[contains(@class, 'CalendarDay CalendarDay') and contains(@aria-label, '%s')][text()='%s']", month.getMonthName(), day))).click();
    }


    @Step("submit Find Flight")
    public UnitedSearchResultPage submitBtFindFlight() {
        btFindFlight.click();
        return addPageToFactory(UnitedSearchResultPage.class);
    }

    @Step("Select oneWay")
    public void chooseOneWay() {
        radioOneWay.click();
    }


    @Step("Check that FindFlight button displayed")
    public boolean isBtFindFlight() {
        return btFindFlight.isDisplayed();
    }

}