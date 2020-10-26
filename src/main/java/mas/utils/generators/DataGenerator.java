package mas.utils.generators;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Component
public class DataGenerator {

    public int generateIntDataInRange(int startOfRange, int endOfRange) {
        return startOfRange + (int) (Math.random() * endOfRange);
    }

    public static String makeRandomEmail() {
        return "AT" + CommonDataGenerator.createAlphaNumericString(10) + "@sharklasers.com";
    }

    public static String makeRandomCompanyName() {
        return "ATCompany" + CommonDataGenerator.createAlphaNumericString(10);
    }

    public static String makeRandomName() {
        return "AT" + CommonDataGenerator.createAlphaString(9);
    }

    public static String makeRandomPhone() {
        return   CommonDataGenerator.createNumericString(12);
    }

    public static String makeRandomInn() {
        return CommonDataGenerator.createNumericString(12);
    }

    public static String makeRandomPass() {
        return CommonDataGenerator.createAlphaNumericString(8);
    }

    protected void setAllToTrue(Object schema) {
        Method[] methods = schema.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (method.getParameterCount() == 1) {
                Class<?> param = method.getParameterTypes()[0];
                if (param == Boolean.TYPE || param == Boolean.class) {
                    try {
                        method.invoke(schema, true);
                    } catch (IllegalAccessException | InvocationTargetException ignored) {
                    }
                }
            }
        }
    }




    private String resolve(String value, String ifEmpty) {
        return StringUtils.isBlank(value) ? ifEmpty : value;
    }
}
