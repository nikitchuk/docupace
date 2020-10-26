package masTest.suits;


import com.googlecode.junittoolbox.SuiteClasses;
import com.googlecode.junittoolbox.WildcardPatternSuite;
import org.junit.BeforeClass;
import org.junit.experimental.categories.Categories;
import org.junit.runner.RunWith;


@RunWith(WildcardPatternSuite.class)
@SuiteClasses({"../api/rest/*Test.class"})
@Categories.IncludeCategory(ApiSuit.class)

public class ApiSuitRunner {
    @BeforeClass
    public  static void setTestRailRun(){
    }
}