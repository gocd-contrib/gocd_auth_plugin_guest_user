package cd.go.plugin.auth.util;

import cd.go.plugin.auth.handlers.PluginSettingsHandlerTest;

import static cd.go.plugin.auth.util.Constants.SETTINGS_USERNAME_KEY;
import static cd.go.plugin.auth.util.Constants.SETTINGS_USER_DISPLAY_NAME_KEY;
import static cd.go.plugin.auth.util.Constants.SETTINGS_USER_EMAIL_KEY;

public class ValidationRequestMother {
    public static String validationRequestBodyFor(String username, String displayName, String emailAddress) {
        /* Not using Json utility class here. Just in case it has a problem, this will pass for the wrong reasons. */
        return String.format("{\"plugin-settings\": {%s, %s, %s}}",
                jsonValue(SETTINGS_USERNAME_KEY, username),
                jsonValue(SETTINGS_USER_DISPLAY_NAME_KEY, displayName),
                jsonValue(SETTINGS_USER_EMAIL_KEY, emailAddress));
    }

    private static String jsonValue(String key, String value) {
        return String.format("\"%s\": {\"value\": \"%s\"}", key, value);
    }
}
