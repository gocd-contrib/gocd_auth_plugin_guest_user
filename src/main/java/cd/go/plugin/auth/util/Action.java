package cd.go.plugin.auth.util;

import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

public interface Action<InputType, OutputType> {
    OutputType call(InputType request);
}
