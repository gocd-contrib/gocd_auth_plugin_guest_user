package cd.go.plugin.auth.handlers;

import cd.go.plugin.auth.util.Json;
import com.thoughtworks.go.plugin.api.request.DefaultGoPluginApiRequest;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static cd.go.plugin.auth.util.Constants.*;
import static cd.go.plugin.auth.util.LogHelper.turnOffLogging;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertThat;

public class PluginConfigurationHandlerTest {
    private PluginConfigurationHandler handler;
    private GoPluginApiRequest someRequest;

    @Before
    public void setUp() throws Exception {
        turnOffLogging();
        handler = new PluginConfigurationHandler();
        someRequest = new DefaultGoPluginApiRequest("SHOULDNT_MATTER", "SHOULDNT_MATTER", "SHOULDNT_MATTER");
    }

    @Test
    public void shouldClaimToBeAWebAuthenticationPlugin() throws Exception {
        GoPluginApiResponse response = handler.call(someRequest);
        Map responseAsMap = Json.toMap(response.responseBody());

        assertThat(response.responseCode(), is(200));
        assertThat(responseAsMap.get(CONFIG_WEB_AUTH), Is.<Object>is(true));
        assertThat(responseAsMap.get(CONFIG_PASSWORD_AUTH), Is.<Object>is(false));
    }

    @Test
    public void shouldHaveAnEmbeddedDisplayImage() throws Exception {
        GoPluginApiResponse response = handler.call(someRequest);
        Map responseAsMap = Json.toMap(response.responseBody());

        assertThat((String) responseAsMap.get(CONFIG_DISPLAY_IMAGE_URL), startsWith("data:image/png;base64,"));
    }
}