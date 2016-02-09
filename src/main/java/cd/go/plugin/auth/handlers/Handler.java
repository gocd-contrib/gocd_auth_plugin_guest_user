package cd.go.plugin.auth.handlers;

import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

public interface Handler {
    GoPluginApiResponse call(GoPluginApiRequest request);
}
