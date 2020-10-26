# UI test

## Quick start
1. Install java 8 and check that java works correctly  by java -version 
1. Install maven and check that maven works correctly that mvn -v
1. Chrome  should be installed 

Its should be enough for run 


## Running 
- for local launch  from  IDE set  VM options -ea -Dbrowser=CHROME -Dmachine=LOCAL -DtestRail=false to VM Option. Run test by jUnit.
- for local launch from console use ```mvn test -Dtest=$testPath -Dbrowser=CHROME -Dmachine=LOCAL -DtestRail=false```
- Also  u can run separate  test or suits suits located on test.java.masTest.suits


## TestConfig 
 1. Dbrowser - Browser for run test. All possible browsers in mas.models.common.enums.Browser 
 1. Dmachine - running env(now using only local)
 1. DtestRail - add result of test to testRail or not. Please do not use -DtestRail=true for separate launch test. Should  be used only for suits which should report to TestRail.  
 1. -Denv - env for test  by default should be DEV;  
 
 
##  Suits 
Now we have 2 type suits for each  application 
For  running each  from  console without add test result  to testRail


1.  Docupace:
    -  Regression: ```mvn test -Dtest=masTest.suits.docupaceSuits.DOCUPACERegressionSuitRunner -Dbrowser=CHROME -Dmachine=LOCAL -DtestRail=false```
    -  Smoke: ```mvn test -Dtest=masTest.suits.docupaceSuits.DOCUPACESmokeSuitRunner -Dbrowser=CHROME -Dmachine=LOCAL -DtestRail=false```

## How suits works 
We using WildcardPatternSuite. 
@SuiteClasses - folders  and classes in which will search  Category.
@Category -  marked by interface which category included or excluded  in suit. 