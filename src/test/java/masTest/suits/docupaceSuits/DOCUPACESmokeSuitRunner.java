package masTest.suits.docupaceSuits;


import com.googlecode.junittoolbox.SuiteClasses;
import com.googlecode.junittoolbox.WildcardPatternSuite;
import org.junit.experimental.categories.Categories;
import org.junit.runner.RunWith;


@RunWith(WildcardPatternSuite.class)
@SuiteClasses({"../../ui/docupaceApp/*Test.class"})
@Categories.IncludeCategory(DOCUPACESmokeSuit.class)

public class DOCUPACESmokeSuitRunner {

}