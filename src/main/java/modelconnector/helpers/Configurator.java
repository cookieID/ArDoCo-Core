package modelconnector.helpers;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * This class is able to load the config file.
 *
 * @author Sophie
 *
 */
public final class Configurator {
    private static final Logger logger = Logger.getLogger(Configurator.class);

    private static final String propFile = "/config.properties";

    private static final Properties config = loadConfiguration();

    private Configurator() {
        throw new IllegalAccessError();
    }

    private static Properties loadConfiguration() {

        Properties prop = new Properties();

        try (InputStream inputStream = Configurator.class.getResourceAsStream(propFile)) {

            prop.load(inputStream);

        } catch (IOException e) {
            logger.debug(e.getMessage(), e.getCause());
        }
        return prop;
    }

    /**
     * Returns the specified property of the config file as a string.
     *
     * @param key
     *            name of the specified property
     * @return value of the property as a string
     */
    public static String getProperty(String key) {
        return config.getProperty(key);
    }

    /**
     * Returns the specified property of the config file as a double.
     *
     * @param key
     *            name of the specified property
     * @return value of the property as a double
     */
    public static double getPropertyAsDouble(String key) {
        try {
            return Double.parseDouble(config.getProperty(key));
        } catch (NumberFormatException n) {
            logger.debug(n.getMessage(), n.getCause());
            return -1;
        }
    }

    /**
     * Returns the specified property of the config file as an int.
     *
     * @param key
     *            name of the specified property
     * @return value of the property as an int
     */
    public static int getPropertyAsInt(String key) {
        try {
            return Integer.parseInt(config.getProperty(key));
        } catch (NumberFormatException n) {
            logger.debug(n.getMessage(), n.getCause());
            return -1;
        }
    }

    /**
     * Returns the specified property of the config file as a list of strings.
     *
     * @param key
     *            name of the specified property
     * @return value of the property as a list of strings
     * @throws Exception
     *             if the key is not found in the configuration file.
     */
    public static List<String> getPropertyAsList(String key) throws IllegalArgumentException {
        List<String> values = new ArrayList<>();
        String value = config.getProperty(key);
        if (value == null) {
            throw new IllegalArgumentException("Key: " + key + " not found in config");
        }

        if (value.strip()
                 .length() == 0) {
            return values;
        }

        values.addAll(List.of(value.split(" ")));
        return values;
    }

    /**
     * Identifies the meant specific enum of the class clazz by the data string and returns it.
     *
     * @param <T>
     *            the enum identified by the string
     * @param data
     *            the string that identifies the enum, the names have to be equal
     * @param clazz
     *            the class that holds the enum
     * @return the enum identified by the given string
     */
    public static <T extends Enum<T>> List<T> getPropertyAsListOfEnumTypes(String data, Class<T> clazz) {
        List<T> selectedValues = new ArrayList<>();
        T[] values = clazz.getEnumConstants();
        List<String> valueList = getPropertyAsList(data);

        for (T val : values) {
            if (valueList.contains(val.name())) {
                selectedValues.add(val);
            }
        }

        return selectedValues;

    }
}
