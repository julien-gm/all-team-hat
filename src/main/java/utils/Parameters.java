package utils;

import java.io.FileInputStream;
import java.util.Properties;

public enum Parameters {
    column_birthdate, column_club, column_excludeDay, column_email, column_firstName, column_firstSkill, column_gender, column_handler, column_height, column_lastName, column_lastSkill, column_pseudo, column_teammate, filterDay, inputSheetId, inputSheetRange, outputSheetId, runs, sheet, teams;

    // TODO prioritize an external .properties file if there is one
    private static final String PROPERTIES_FILE = "src/main/resources/hatligot.properties";
    private static Properties properties;
    private String value;

    private void init() {
        if (properties == null) {
            properties = new Properties();
            try {
                properties.load(new FileInputStream(PROPERTIES_FILE));
            } catch (Exception e) {
                System.err.println("Unable to load " + PROPERTIES_FILE + " file from classpath : " + e);
                System.exit(1);
            }
        }
        value = (String) properties.get(this.toString());
    }

    public String getStringValue() {
        if (value == null) {
            init();
        }
        return value;
    }

    public int getIntValue() {
        return Integer.valueOf(getStringValue());
    }
}
