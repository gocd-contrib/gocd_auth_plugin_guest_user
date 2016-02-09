package cd.go.plugin.auth;

import cd.go.plugin.auth.handlers.Handler;
import cd.go.plugin.auth.handlers.PluginConfigurationHandler;
import cd.go.plugin.auth.handlers.PluginIndexRequestHandler;
import cd.go.plugin.auth.handlers.PluginSettingsHandler;
import cd.go.plugin.auth.util.Action;
import cd.go.plugin.auth.util.Wrapper;
import com.thoughtworks.go.plugin.api.GoApplicationAccessor;
import com.thoughtworks.go.plugin.api.GoPlugin;
import com.thoughtworks.go.plugin.api.GoPluginIdentifier;
import com.thoughtworks.go.plugin.api.annotation.Extension;
import com.thoughtworks.go.plugin.api.exceptions.UnhandledRequestTypeException;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static cd.go.plugin.auth.util.Logging.withLogging;
import static cd.go.plugin.auth.util.Constants.*;

@Extension
public class GuestUserAuthPlugin implements GoPlugin {
    private final Map<String, Handler> handlers;
    private final GoPluginIdentifier goPluginIdentifier;
    private Wrapper<GoApplicationAccessor> accessorWrapper = new Wrapper<GoApplicationAccessor>();

    public GuestUserAuthPlugin() {
        goPluginIdentifier = new GoPluginIdentifier("authentication", Arrays.asList("1.0"));

        handlers = new HashMap<String, Handler>();

        handlers.put(CALL_FROM_SERVER_GET_CONFIGURATION,      PluginSettingsHandler.getConfiguration());
        handlers.put(CALL_FROM_SERVER_GET_VIEW,               PluginSettingsHandler.getView());
        handlers.put(CALL_FROM_SERVER_VALIDATE_CONFIGURATION, PluginSettingsHandler.validateConfiguration());

        handlers.put(CALL_FROM_SERVER_PLUGIN_CONFIGURATION,   new PluginConfigurationHandler());
        handlers.put(CALL_FROM_SERVER_INDEX,                  new PluginIndexRequestHandler(accessorWrapper, goPluginIdentifier));
    }

    @Override
    public void initializeGoApplicationAccessor(GoApplicationAccessor goApplicationAccessor) {
        /* This call happens too late for the handlers to be given the accessor. So, give them a
         * wrapper instead (earlier, in constructor) and fill up the wrapper with the accessor now.
         */
        accessorWrapper.holdOnTo(goApplicationAccessor);
    }

    @Override
    public GoPluginIdentifier pluginIdentifier() {
        return goPluginIdentifier;
    }

    /* This is where all calls from the server come to, and have to be handled. */
    @Override
    public GoPluginApiResponse handle(GoPluginApiRequest request) throws UnhandledRequestTypeException {
        return withLogging("From server to plugin", request, new Action<GoPluginApiRequest, GoPluginApiResponse>() {
            public GoPluginApiResponse call(GoPluginApiRequest request) {
                if (handlers.containsKey(request.requestName())) {
                    return handlers.get(request.requestName()).call(request);
                }
                throw new RuntimeException("Handler for request of type: " + request.requestName() + " is not implemented");
            }
        });
    }

}
