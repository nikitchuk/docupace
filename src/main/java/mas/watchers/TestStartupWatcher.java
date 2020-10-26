package mas.watchers;

import mas.utils.Config;
import mas.utils.runTime.*;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;

import static mas.utils.runTime.Country.ALL;
import static mas.utils.runTime.Country.NOT_SPECIFIED;
import static org.apache.commons.lang3.StringUtils.*;

public class TestStartupWatcher extends TestWatcher {

    private static boolean isScenarioActive(Scenario scenario) {
        Country selected = EndToEndProperties.getInstance().COUNTRY;
        if (selected == ALL || selected == NOT_SPECIFIED)
            return true;

        List<Country> declared = Arrays.asList(scenario.countries());
        return declared.size() == 0
                || declared.contains(ALL)
                || declared.contains(NOT_SPECIFIED)
                || Arrays.asList(scenario.countries()).contains(selected);
    }

    public static List<String> getNames(Collection<Annotation> annotations) {
        List<Scenario> scenarios = filterScenarios(annotations);
        return scenarios.stream().map(Scenario::value).collect(Collectors.toList());
    }

    public static List<Scenario> filterScenarios(Collection<Annotation> annotations) {
        List<Scenario> scenarios = new ArrayList<>();
        for (Annotation annotation : annotations) {
            if (annotation instanceof Scenario)
                scenarios.add((Scenario) annotation);
            else if (annotation instanceof Scenarios)
                Collections.addAll(scenarios, ((Scenarios) annotation).value());
        }
        return scenarios.stream().filter(TestStartupWatcher::isScenarioActive).collect(Collectors.toList());
    }


    @Override
    protected void starting(Description description) {
        List<Scenario> scenarios = filterScenarios(description.getAnnotations());
        List<String> names = getNames(scenarios);
        EventAppender.startTest(description.getMethodName(), names);

        // PRINT NAME OF STARTED TEST
        if (EndToEndProperties.getInstance().PRINT_TEST_RESULTS) {
            System.out.println("________________________________________________________________________________\n" +
                    "STARTING TEST : " + description.getMethodName() + "\n");
        }

        // PRINT RELATED SCENARIOS
        if (EndToEndProperties.getInstance().PRINT_SCENARIOS) {
            List<String> links = getLinks(scenarios);

            int maxWidthScenario = names.stream().map(String::length).reduce(0, Integer::max);
            int maxWidthLink = links.stream().map(String::length).reduce(0, Integer::max);
            int maxWidth = maxWidthLink + maxWidthScenario;

            String dottedLine = "  " + repeat("-", maxWidth + 5) + "\n";
            if (maxWidth > 0) {
                System.out.print(dottedLine);
                List<String> lines = new ArrayList<>();
                for (int i = 0; i < names.size(); i++) {
                    lines.add("  | "
                            + names.get(i)
                            + repeat(" ", maxWidthScenario - names.get(i).length())
                            + " "
                            + links.get(i)
                            + repeat(" ", maxWidthLink + 1 - links.get(i).length())
                            + " |");
                }

                lines.stream().sorted().forEach(System.out::println);
                System.out.println(dottedLine);
            }
        }
    }

    public static List<String> getNames(List<Scenario> scenarios) {
        return scenarios.stream().map(Scenario::value).collect(Collectors.toList());
    }

    private List<String> getLinks(List<Scenario> scenarios) {
        return scenarios.stream().map(this::getLink).collect(Collectors.toList());
    }

    private String getLink(Scenario s) {
        if (isNoneBlank((CharSequence[]) s.url()) && !(s.url().length == 0)) {
            return Arrays.toString(s.url()).replaceAll("\\[|,|\\]", "");
        } else {
            String firstWord = s.value().split(" ")[0];
            return firstWord.contains("-") ?
                    "jiraLink" + firstWord : "";
        }
    }

}
