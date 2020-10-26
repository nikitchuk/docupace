package mas.prophet;

import org.apache.commons.lang3.StringUtils;

import java.io.FileOutputStream;
import java.io.IOException;


final class ProphetLogger {
    private static FileOutputStream file;

    private ProphetLogger() {
    }

    private static void openLogFile() {
        try {
            file = new FileOutputStream("target/prediction.txt", true);
        } catch (IOException e) {
            System.err.println("Cannot open prediction log.");
        }
    }

    private static void closeLogFile() {
        try {
            file.flush();
            file.close();
        } catch (IOException e) {
            System.err.println("Prediction log is already closed.");
        }
    }

    static void append(String text) {
        openLogFile();
        try {
            file.write(text.getBytes());
            file.write('\n');
            file.flush();
        } catch (IOException ignored) {
        } finally {
            closeLogFile();
        }
    }

    static void debug(CallNode callTree, CallNode stackTree, String message) {
        append("---------------------------------------- " + message);
        printTree(callTree, 0);
        append("\n");
        printTree(stackTree, 0);
        append("\n");
    }

    private static void printTree(CallNode node, int depth) {
        append(StringUtils.repeat("| ", depth) + node);
        for (CallNode callNode : node.getChildren())
            printTree(callNode, depth + 1);
    }
}
