package cd.go.plugin.auth.handlers;

import cd.go.plugin.auth.util.Wrapper;
import com.thoughtworks.go.plugin.api.GoApplicationAccessor;
import com.thoughtworks.go.plugin.api.GoPluginIdentifier;
import com.thoughtworks.go.plugin.api.request.DefaultGoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import org.junit.Before;
import org.junit.Test;

import static cd.go.plugin.auth.util.AccessorStub.*;
import static cd.go.plugin.auth.util.LogHelper.turnOffLogging;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class PluginIndexRequestHandlerTest {
    private GoApplicationAccessor accessor;
    private PluginIndexRequestHandler handler;
    private GoPluginIdentifier pluginIdentifier;

    @Before
    public void setUp() throws Exception {
        turnOffLogging();
        accessor = mock(GoApplicationAccessor.class);
        pluginIdentifier = mock(GoPluginIdentifier.class);

        Wrapper<GoApplicationAccessor> accessorWrapper = new Wrapper<GoApplicationAccessor>();
        accessorWrapper.holdOnTo(accessor);

        handler = new PluginIndexRequestHandler(accessorWrapper, pluginIdentifier);
    }

    @Test
    public void shouldRespondWithAPageDirectingToSetupIfUsernameIsNotPresent() throws Exception {
        expectCallToFetchSettingsFromServer(accessor, "", "valid-display-name", "valid@email.com");

        GoPluginApiResponse response = handler.call(someRequest());

        assertThat(response.responseBody(), containsString("It looks like the plugin has not been configured"));
    }

    @Test
    public void shouldRespondWithAPageDirectingToSetupIfDisplayNameIsNotPresent() throws Exception {
        expectCallToFetchSettingsFromServer(accessor, "valid-username", "", "valid@email.com");

        GoPluginApiResponse response = handler.call(someRequest());

        assertThat(response.responseBody(), containsString("It looks like the plugin has not been configured"));
    }

    @Test
    public void shouldAuthenticateTheConfiguredGuestUserWhenSettingsAreValid() throws Exception {
        expectCallToFetchSettingsFromServer(accessor, "valid-username", "valid-display-name", "valid@email.com");
        expectCallToAuthenticateUser(accessor, "valid-username", "valid-display-name", "valid@email.com");

        GoPluginApiResponse response = handler.call(someRequest());

        assertThat(response.responseHeaders().get("Location"), is("/"));
    }

    private DefaultGoPluginApiRequest someRequest() {
        return new DefaultGoPluginApiRequest("SHOULDNT_MATTER", "SHOULDNT_MATTER", "SHOULDNT_MATTER");
    }
}