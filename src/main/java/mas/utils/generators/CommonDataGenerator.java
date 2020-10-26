package mas.utils.generators;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.Random;

public final class CommonDataGenerator {


    private static Random random = new Random();

    private CommonDataGenerator() {
    }

    public static String createAlphaString(String prefix, int length) {
        if (prefix == null) {
            throw new IllegalArgumentException("prefix cannot be null");
        }
        if (prefix.length() > length) {
            throw new IllegalArgumentException("prefix cannot be greater than length of generated string");
        }
        return prefix + createAlphaString(length - prefix.length());
    }

    /**
     * Create pure alphabetic string
     *
     * @param length number of characters
     * @return random string
     */
    public static String createAlphaString(int length) {
        return RandomStringUtils.randomAlphabetic(length);
    }

    public static String createAlphaString(String prefix, int length, String suffix) {
        if (prefix == null) {
            throw new IllegalArgumentException("prefix cannot be null");
        }
        if ((prefix.length() + suffix.length()) > length) {
            throw new IllegalArgumentException("prefix + suffix cannot be greater than length of generated string");
        }
        return prefix + createAlphaString(length - (prefix.length() + suffix.length())) + suffix;
    }

    /**
     * Create pure numeric string
     *
     * @param length number of digits
     * @return random number as string
     */
    public static String createNumericString(int length) {
        return RandomStringUtils.randomNumeric(length);
    }

    /**
     * Create random numeric string with total length and specified prefix
     *
     * @param prefix - prefix e.g. phone number prefix
     * @param length - total length of generated string
     * @return numeric string
     */
    public static String createNumericString(String prefix, int length) {
        if (prefix == null) {
            throw new IllegalArgumentException("prefix cannot be null");
        }
        if (prefix.length() > length) {
            throw new IllegalArgumentException("prefix cannot be greater than length of generated string");
        }
        return prefix + RandomStringUtils.randomNumeric(length - prefix.length());
    }

    /**
     * Create numeric string in bounds from to (for example 1-12,3-10 etc.)
     *
     * @param lowerValue - lower bound
     * @param upperValue - upper bound
     * @return random number within bounds as string
     */
    public static String createNumericStringFromTo(int lowerValue, int upperValue) {
        int result = lowerValue + new Random().nextInt(upperValue - lowerValue);
        return Integer.toString(result);
    }

    public static String createAlphaNumericString(String prefix, int length) {
        if (prefix == null) {
            throw new IllegalArgumentException("prefix cannot be null");
        }
        if (prefix.length() > length) {
            throw new IllegalArgumentException("prefix cannot be greater than length of generated string");
        }
        return prefix + createAlphaNumericString(length - prefix.length());
    }

    /**
     * Create alphanumeric string
     *
     * @param length number of characters
     * @return random string
     */
    public static String createAlphaNumericString(int length) {
        return RandomStringUtils.randomAlphanumeric(length);
    }

    public static String createAlphaNumericString(String prefix, int length, String suffix) {
        if (prefix == null) {
            throw new IllegalArgumentException("prefix cannot be null");
        }
        if ((prefix.length() + suffix.length()) > length) {
            throw new IllegalArgumentException("prefix + suffix cannot be greater than length of generated string");
        }
        return prefix + createAlphaNumericString(length - (prefix.length() + suffix.length())) + suffix;
    }

    /**
     * Create random String from provided signs
     *
     * @param length   - required legth
     * @param alphabet - signs
     * @return custom String
     */
    public static String createCustomString(int length, String alphabet) {
        return createCustomString(length, alphabet.toCharArray());
    }

    /**
     * Create random String from provided signs
     *
     * @param length - required legth
     * @param signs  - signs
     * @return custom String
     */
    public static String createCustomString(int length, char[] signs) {
        return RandomStringUtils.random(length, signs);
    }

    /**
     * Generate integer number
     * if 0 zero is passed its also returned
     * if negative number is passed exception is thrown
     *
     * @param size - maxium number that can be generated
     * @return random integer in 0 - size
     */
    public static Integer generateInt(int size) {
        if (size == 0) {
            return 0;
        }

        return random.nextInt(size);
    }
}
