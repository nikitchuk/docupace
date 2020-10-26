package mas.utils.runTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import mas.prophet.CallNode;
import mas.prophet.Prophet;
import mas.utils.cleanUp.Evidence;
import mas.watchers.TestStartupWatcher;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class EventAppender {

    private static ObjectMapper mapper = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_ABSENT);
    private static FileOutputStream file;
    private static String separator = "";
    private static int blockDepth = 0;
    private static Prophet prophet = new Prophet();
    private static Logger logger = LoggerFactory.getLogger(EventAppender.class);

    static {
        openLogFile();
    }

    public static void addMixInClassToMapper(Class<?> target, Class<?> mixinSource) {
        mapper.addMixIn(target, mixinSource);
    }

    public static void logEvent(Event event) {
        try {
            append(separator + mapper.writeValueAsString(event));
            separator = ",";
        } catch (JsonMappingException e) {
            Exception errorHolder = new Exception(event.getThrowable().getMessage());
            errorHolder.setStackTrace(event.getThrowable().getStackTrace());
            event.setThrowable(errorHolder);
            logEvent(event);
        } catch (JsonProcessingException e) {
            logger.error("Failed to log an event.", e);
        }
    }

    public static void endBlock() {
        separator = ",";
        append("]}");
        blockDepth--;
    }

    public static void startBlock(String text, List<String> scenarios) {
        append(separator +
                "{\"value\":\"" + StringEscapeUtils.escapeJson(text) +
                "\",\"time\":" + System.currentTimeMillis() +
                ",\"scenarios\":" + toJSONList(scenarios) +
                ",\"children\":[");
        separator = "";
        blockDepth++;
    }

    private static void append(String text) {
        try {
            file.write(text.getBytes());
            file.flush();
        } catch (IOException ignored) {
        }
    }

    private static String toJSONList(List<String> list) {
        return list.stream().map(x -> "\"" + StringEscapeUtils.escapeJson(x) + "\"").collect(Collectors.toList()).toString();
    }

    private static void startMock(CallNode node) {
        List<String> scenarios = getScenarios(node);
        String step = getStepName(node);
        append(separator +
                "{\"value\":\"" + StringEscapeUtils.escapeJson(step) +
                "\",\"scenarios\":" + toJSONList(scenarios) +
                ",\"type\":\"" + Event.Type.MOCK +
                "\",\"children\":[");
        separator = "";
        blockDepth++;
    }

    public static void startTest(String text, List<String> scenarios) {
        append(separator +
                "{\"value\":\"" + StringEscapeUtils.escapeJson(text) +
                "\",\"time\":" + System.currentTimeMillis() +
                ",\"scenarios\":" + toJSONList(scenarios) +
                ",\"type\":\"" + Event.Type.TEST +
                "\",\"children\":[");
        separator = "";
        blockDepth++;
    }

    public static void openLogFile() {
        try {
            File logFile = new File(EndToEndProperties.getInstance().EVENT_LOG);
            logFile.getParentFile().mkdirs();
            file = new FileOutputStream(logFile);
            append("[");
            separator = "";
        } catch (IOException e) {
            logger.error("Failed to create the event log file.", e);
        }
    }

    public static void closeLogFile() {
        try {
            append("]");
            file.close();
        } catch (IOException e) {
            logger.error("Event log is already closed.", e);
        }
    }

    public static void logError(Throwable exception, Evidence evidence, boolean predictSteps) {
        Event event = new Event(Event.Type.ERROR)
                .setThrowable(exception)
                .setValue(exception.getClass().getName());

        if (evidence != null)
            event.setEvidence(evidence.toList());

        logEvent(event);

        if (predictSteps) {
            CallNode predicted = prophet.predictSteps(exception.getStackTrace());
            if (predicted != null)
                logFutureSteps(predicted, 1);
            while (blockDepth > 1)
                endBlock();
        }
    }

    public static void logSkip(Throwable exception) {
        Event event = new Event(Event.Type.SKIP)
                .setValue(exception.getMessage());

        logEvent(event);
        CallNode predicted = prophet.predictSteps(exception.getStackTrace());
        if (predicted != null)
            logFutureSteps(predicted, 1);
    }

    private static void logFutureSteps(CallNode node, int depth) {
        if (depth > blockDepth)
            startMock(node);

        if (node.getChildren().size() > 0) {
            logFutureSteps(node.getChildren().get(0), depth + 1);

            for (int i = 1; i < node.getChildren().size(); i++)
                logMockEvents(node.getChildren().get(i));
        }

        if (depth >= 2)
            endBlock();
    }

    private static void logMockEvents(CallNode node) {
        startMock(node);

        for (int i = 0; i < node.getChildren().size(); i++)
            logMockEvents(node.getChildren().get(i));

        endBlock();
    }

    private static List<String> getScenarios(CallNode node) {
        Object[] ann = node.getMethod().getAvailableAnnotations();
        Annotation[] annotations = Arrays.copyOf(ann, ann.length, Annotation[].class);
        return TestStartupWatcher.getNames(Arrays.asList(annotations));
    }

    private static String getStepName(CallNode node) {
        try {
            Object annotation = node.getMethod().getAnnotation(Step.class);
            return annotation != null ? ((Step) annotation).value() : node.getMethod().getName();
        } catch (ClassNotFoundException ignored) {
            return "Anonymous step";
        }
    }
}
