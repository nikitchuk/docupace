package masTest.ui.docupaceApp;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import mas.dto.FlightData;
import mas.models.common.enums.Airports;
import mas.models.common.enums.FlightClass;
import mas.models.common.enums.Month;
import mas.pages.docupace.docupaceApp.UnitedDashboardPage;
import mas.pages.docupace.docupaceApp.UnitedSearchResultPage;
import mas.utils.runTime.Scenario;
import masTest.suits.docupaceSuits.DOCUPACERegressionSuit;
import masTest.suits.docupaceSuits.DOCUPACESmokeSuit;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

public class DocupaceE2ETest extends DocupaceBaseTest {


    @Category({DOCUPACERegressionSuit.class, DOCUPACESmokeSuit.class})
    @Test
    @Scenario("Test united airlines take data from flight search")
    public void testUnitedAirlinesTakeDataFromFlightSearch() throws JsonProcessingException {
        UnitedDashboardPage unitedDashboardPage = initUserPortal();
        unitedDashboardPage.fillFlightFrom(Airports.NEW_YORK_JFK);
        unitedDashboardPage.chooseOneWay();
        unitedDashboardPage.selectClassFlight(FlightClass.ECONOMY);
        unitedDashboardPage.selectDepartDate(20, Month.AUGUST,2021);
        unitedDashboardPage.fillFlightTo(Airports.MIAMI_ALL);
        collector.checkThat("button flight is not available", unitedDashboardPage.isBtFindFlight(), is(true));

        UnitedSearchResultPage unitedSearchResultPage = unitedDashboardPage.submitBtFindFlight();
        unitedSearchResultPage.clickBtEconomyBasic();
        collector.checkThat("Zero flight with selected data available in basic economy price", unitedSearchResultPage.isSortedByEconomyASC(), is(true));


        unitedSearchResultPage.clickBtShowAllFlight();
        List<FlightData> listFlightData = unitedSearchResultPage.getAvailableFlightDataWithEcoBasicPrice();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        List<String> jsonResult = new ArrayList<>();
        collector.checkThat("Zero flight with selected data available in basic economy price", listFlightData.size(), greaterThan(0));
        for (FlightData flightData : listFlightData) {
            jsonResult.add(ow.writeValueAsString(flightData));

        }
        logger.info("Print json result  flight data " + jsonResult);
    }


}
