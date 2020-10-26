package masTest.ui.docupaceApp;

import mas.pages.docupace.docupaceApp.UnitedDashboardPage;
import masTest.ui.BaseTest;


public abstract class DocupaceBaseTest extends BaseTest {

    protected UnitedDashboardPage initUserPortal() {
        init();
        String url = "https://www.united.com/en/us/";
        System.out.println("Open url " + url);
        getWebDriver().get(url);
        return addPageToFactory(UnitedDashboardPage.class);
    }

}