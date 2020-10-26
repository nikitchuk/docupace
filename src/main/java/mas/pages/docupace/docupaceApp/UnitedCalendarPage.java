package mas.pages.docupace.docupaceApp;

import mas.controls.Button;
import mas.pages.AbstractPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

public class UnitedCalendarPage extends AbstractPage {
    @FindBy(xpath = "//*[contains(text(), 'Saved Filters')]")
    private Button btSavedFilters;

    public UnitedCalendarPage(WebDriver driver) {
        super(driver);
    }
    @Override
    public boolean isPageContentValid() {
        wait.waitForAjax();
        return isElementPresentNow(By.cssSelector("[tooltip-text='Month']")) && isElementPresentNow(By.xpath("//*[contains(text(), 'Saved Filters')]"));
    }
}
