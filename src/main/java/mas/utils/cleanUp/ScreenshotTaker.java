package mas.utils.cleanUp;

import com.googlecode.pngtastic.core.PngImage;
import com.googlecode.pngtastic.core.PngOptimizer;
import mas.utils.runTime.EndToEndProperties;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.Deflater;

public final class ScreenshotTaker extends AbstractSaver {

    private static Map<String, Integer> captured = new HashMap<>();

    private static String SCREENSHOT_FILE_NAME_FORMAT = "screenshot-%s-err%02d.png";

    /**
     * Constructor
     *
     * @param webDriver Default WebDriver, which will be used for saving page
     */
    public ScreenshotTaker(WebDriver webDriver) {
        super(webDriver);
    }

    /**
     * Take a screenshot from the currently used webdriver.
     *
     * @param testName Simple name of output file, without any path or extension.
     * @return saved screenshot in target directory
     */
    public String takeScreenshot(String testName) {
        return takeScreenshot(testName, driver);
    }

    /**
     * Take sreenshot and save it to target directory
     *
     * @param testName  Simple name of output file, without any path or extension.
     * @param webDriver Default WebDriver, which will be used for saving page
     * @return saved screenshot in target directory
     */
    public static String takeScreenshot(String testName, WebDriver webDriver) {
        if (webDriver == null) {
            return null;
        }

        File srcFile;
        try {
            srcFile = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);
        } catch (WebDriverException e) {
            logger.error("Could not take screenshot.", e);
            return null;
        }
        String fileName = getFileName(testName, captured, SCREENSHOT_FILE_NAME_FORMAT);

        if (EndToEndProperties.getInstance().COMPRESS_SCREENSHOTS) {
            reduceColors(srcFile);
            srcFile = compress(srcFile);
        }

        try {
            FileUtils.copyFile(srcFile, new File(SAVE_DIRECTORY + fileName));
        } catch (Exception e) {
            logger.error("Could not save screenshot.", e);
        }
        return SAVE_DIRECTORY_URL + fileName;
    }

    private static void reduceColors(File srcFile) {
        try {
            BufferedImage src = ImageIO.read(srcFile);
            BufferedImage compressed = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_BYTE_INDEXED);

            ColorConvertOp cco = new ColorConvertOp(src.getColorModel().getColorSpace(), src.getColorModel().getColorSpace(), null);
            cco.filter(src, compressed);
            compressed.getGraphics().drawImage(src, 0, 0, null);

            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("png");
            ImageWriter writer = writers.next();

            OutputStream os = new FileOutputStream(srcFile);
            ImageOutputStream ios = ImageIO.createImageOutputStream(os);

            writer.setOutput(ios);
            ImageWriteParam param = writer.getDefaultWriteParam();
            if (param.canWriteCompressed()) {
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(0);
            }
            writer.write(null, new IIOImage(compressed, null, null), param);
        } catch (IOException e) {
            logger.warn("Cannot decrease color depth");
        }
    }

    private static File compress(File srcFile) {
        try {
            InputStream is = new FileInputStream(srcFile);
            PngImage pngImage = new PngImage(is);

            PngOptimizer optimizer = new PngOptimizer();
            PngImage optimized = optimizer.optimize(pngImage, true, Deflater.BEST_COMPRESSION);

            final ByteArrayOutputStream optimizedBytes = new ByteArrayOutputStream();
            optimized.writeDataOutputStream(optimizedBytes);
            return optimized.export(srcFile.getAbsolutePath(), optimizedBytes.toByteArray());
        } catch (IOException e) {
            logger.warn("Cannot compress screenshot");
            return srcFile;
        }
    }

}
