package cd.go.plugin.auth;

import cd.go.plugin.auth.util.Json;
import cd.go.plugin.auth.util.LogHelper;
import com.google.gson.Gson;
import com.thoughtworks.go.plugin.api.GoApplicationAccessor;
import com.thoughtworks.go.plugin.api.request.DefaultGoPluginApiRequest;
import com.thoughtworks.go.plugin.api.request.GoApiRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoApiResponse;
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
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GuestUserAuthPluginTest {
    private GuestUserAuthPlugin plugin;
    private GoApplicationAccessor accessor;

    @Before
    public void setUp() throws Exception {
        turnOffLogging();
        plugin = new GuestUserAuthPlugin();
        accessor = mock(GoApplicationAccessor.class);
        plugin.initializeGoApplicationAccessor(accessor);
    }

    @Test
    public void shouldFailWhenAnUnexpectedRequestTypeIsSent() throws Exception {
        String unknownType = "SOME_UNKNOWN_TYPE";

        try {
            plugin.handle(requestOfType(unknownType));
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), containsString(unknownType + " is not implemented"));
        }
    }

    @Test
    public void shouldHandleCallToFetchConfigurationKeys() throws Exception {
        GoPluginApiResponse response = plugin.handle(requestOfType(CALL_FROM_SERVER_GET_CONFIGURATION));

        assertThat(response.responseBody(), containsString(SETTINGS_USERNAME_KEY));
        assertThat(response.responseBody(), containsString(SETTINGS_USER_DISPLAY_NAME_KEY));
        assertThat(response.responseBody(), containsString(SETTINGS_USER_EMAIL_KEY));
    }

    @Test
    public void shouldHandleCallToFetchConfigurationView() throws Exception {
        GoPluginApiResponse response = plugin.handle(requestOfType(CALL_FROM_SERVER_GET_VIEW));

        assertThat(response.responseBody(), containsString("ng-model"));
        assertThat(response.responseBody(), containsString(SETTINGS_USERNAME_KEY));
    }

    @Test
    public void shouldHandleCallToValidateConfiguration() throws Exception {
        DefaultGoPluginApiRequest request = requestOfType(CALL_FROM_SERVER_VALIDATE_CONFIGURATION);
        request.setRequestBody(validationRequestBodyFor("", "", ""));

        GoPluginApiResponse response = plugin.handle(request);
        List validationErrors = new Gson().fromJson(response.responseBody(), List.class);

        assertThat(validationErrors.size(), is(3));
    }

    @Test
    public void shouldHandleCallToGetPluginConfiguration() throws Exception {
        DefaultGoPluginApiRequest request = requestOfType(CALL_FROM_SERVER_PLUGIN_CONFIGURATION);

        Map<String, String> response = Json.toMapOfStrings(plugin.handle(request).responseBody());

        assertThat(response.get(CONFIG_DISPLAY_NAME), is("Guest login"));
    }

    @Test
    public void shouldHandleCallToIndexWebRequest() throws Exception {
        when(accessor.submit(any(GoApiRequest.class))).thenReturn(new DefaultGoApiResponse(200));
        DefaultGoPluginApiRequest request = requestOfType(CALL_FROM_SERVER_INDEX);

        GoPluginApiResponse response = plugin.handle(request);

        assertThat(response.responseBody(), containsString("plugin has not been configured"));
    }

    private DefaultGoPluginApiRequest requestOfType(String type) {
        return new DefaultGoPluginApiRequest("authentication", "1.0", type);
    }
}