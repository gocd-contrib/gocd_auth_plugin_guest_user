package cd.go.plugin.auth.handlers;

import cd.go.plugin.auth.util.Json;
import cd.go.plugin.auth.util.ValidationRequestMother;
import com.google.gson.Gson;
import com.thoughtworks.go.plugin.api.request.DefaultGoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static cd.go.plugin.auth.util.Constants.*;
import static cd.go.plugin.auth.util.LogHelper.turnOffLogging;
import static cd.go.plugin.auth.util.ValidationRequestMother.validationRequestBodyFor;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.fail;

public class PluginSettingsHandlerTest {
    @Before
    public void setUp() throws Exception {
        turnOffLogging();
    }

    @Test
    public void shouldHaveNamesAndEmailAddressAsPartOfSettings() throws Exception {
        ensureSettingsResponseHas(SETTINGS_USERNAME_KEY);
        ensureSettingsResponseHas(SETTINGS_USER_DISPLAY_NAME_KEY);
        ensureSettingsResponseHas(SETTINGS_USER_EMAIL_KEY);
    }

    @Test
    public void viewTemplateShouldHaveAllTheKeysFromSettings() throws Exception {
        Handler viewHandler = PluginSettingsHandler.getView();

        GoPluginApiResponse response = viewHandler.call(someRequest());
        String view = response.responseBody();

        assertThat(view, containsString(SETTINGS_USERNAME_KEY));
        assertThat(view, containsString(SETTINGS_USER_DISPLAY_NAME_KEY));
        assertThat(view, containsString(SETTINGS_USER_EMAIL_KEY));
    }

    @Test
    public void shouldValidateEmptyUsername() throws Exception {
        DefaultGoPluginApiRequest request = setupRequestForValidationWith("", "valid-display-name", "valid@email.com");

        GoPluginApiResponse response = PluginSettingsHandler.validateConfiguration().call(request);

        ensureResponseHasNumberOfErrors(response, 1);
        ensureResponseHasValidationError(response, SETTINGS_USERNAME_KEY);
    }

    @Test
    public void shouldValidateEmptyDisplayName() throws Exception {
        DefaultGoPluginApiRequest request = setupRequestForValidationWith("valid-username", "", "valid@email.com");

        GoPluginApiResponse response = PluginSettingsHandler.validateConfiguration().call(request);

        ensureResponseHasNumberOfErrors(response, 1);
        ensureResponseHasValidationError(response, SETTINGS_USER_DISPLAY_NAME_KEY);
    }

    @Test
    public void shouldValidateEmptyEmailAddress() throws Exception {
        DefaultGoPluginApiRequest request = setupRequestForValidationWith("valid-username", "valid-display-name", "");

        GoPluginApiResponse response = PluginSettingsHandler.validateConfiguration().call(request);

        ensureResponseHasNumberOfErrors(response, 1);
        ensureResponseHasValidationError(response, SETTINGS_USER_EMAIL_KEY);
    }

    @Test
    public void shouldValidateMultipleMissingFieldsToo() throws Exception {
        DefaultGoPluginApiRequest request = setupRequestForValidationWith("", "valid-display-name", "");

        GoPluginApiResponse response = PluginSettingsHandler.validateConfiguration().call(request);

        ensureResponseHasNumberOfErrors(response, 2);
        ensureResponseHasValidationError(response, SETTINGS_USERNAME_KEY);
        ensureResponseHasValidationError(response, SETTINGS_USER_EMAIL_KEY);
    }

    private void ensureResponseHasNumberOfErrors(GoPluginApiResponse response, int numberOfErrorsExpected) {
        String responseBody = response.responseBody();
        List validationErrors = new Gson().fromJson(responseBody, List.class);
        assertThat("Response was: " + responseBody, validationErrors.size(), is(numberOfErrorsExpected));
    }

    private void ensureResponseHasValidationError(GoPluginApiResponse response, String keyOfExpectedError) {
        String responseBody = response.responseBody();
        List validationErrors = new Gson().fromJson(responseBody, List.class);

        for (Object validationError : validationErrors) {
            if (((Map) validationError).get("key").equals(keyOfExpectedError)) {
                return;
            }
        }
        fail(String.format("Did not find error with key: %s in %s.", keyOfExpectedError, responseBody));
    }

    private void ensureSettingsResponseHas(String key) {
        Handler settingsHandler = PluginSettingsHandler.getConfiguration();

        GoPluginApiResponse response = settingsHandler.call(someRequest());
        Map responseAsMap = Json.toMap(response.responseBody());

        assertThat(response.responseCode(), is(200));
        assertThat(responseAsMap.containsKey(key), is(true));
    }

    private DefaultGoPluginApiRequest setupRequestForValidationWith(String username, String displayName, String emailAddress) {
        DefaultGoPluginApiRequest request = someRequest();
        request.setRequestBody(validationRequestBodyFor(username, displayName, emailAddress));
        return request;
    }

    private DefaultGoPluginApiRequest someRequest() {
        return new DefaultGoPluginApiRequest("SHOULDNT_MATTER", "SHOULDNT_MATTER", "SHOULDNT_MATTER");
    }
}