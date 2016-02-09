package cd.go.plugin.auth.util;

import cd.go.plugin.auth.util.Constants;
import cd.go.plugin.auth.util.Json;
import com.thoughtworks.go.plugin.api.GoApplicationAccessor;
import com.thoughtworks.go.plugin.api.request.GoApiRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoApiResponse;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import java.util.Map;

import static cd.go.plugin.auth.util.Constants.*;
import static cd.go.plugin.auth.util.Constants.API_CALL_PLUGIN_SETTINGS_GET;
import static cd.go.plugin.auth.util.Constants.SETTINGS_USER_EMAIL_KEY;
import static cd.go.plugin.auth.util.MapBuilder.create;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.when;

public class AccessorStub {
    public static void expectCallToAuthenticateUser(GoApplicationAccessor accessor, String username, String displayName, String emailAddress) {
        Map detailsMap = create().add(AUTH_API_USERNAME_KEY, username).add(AUTH_API_DISPLAYNAME_KEY, displayName)
                .add(AUTH_API_EMAIL_KEY, emailAddress).build();

        String bodyOfRequest = Json.toJson(create().add(AUTH_API_USER_KEY, detailsMap).build());
        when(accessor.submit(requestMatcherFor(API_CALL_AUTHENTICATE_USER, bodyOfRequest))).thenReturn(new DefaultGoApiResponse(200));
    }

    public static void expectCallToFetchSettingsFromServer(GoApplicationAccessor accessor, String configuredUsername, String configuredDisplayName, String configuredEmailAddress) {
        DefaultGoApiResponse response = new DefaultGoApiResponse(200);
        response.setResponseBody(Json.toJson(create()
                .add(SETTINGS_USERNAME_KEY, configuredUsername)
                .add(SETTINGS_USER_DISPLAY_NAME_KEY, configuredDisplayName)
                .add(SETTINGS_USER_EMAIL_KEY, configuredEmailAddress)
                .build()));

        String bodyOfRequest = Json.toJson(create().add("plugin-id", Constants.PLUGIN_ID).build());
        when(accessor.submit(requestMatcherFor(API_CALL_PLUGIN_SETTINGS_GET, bodyOfRequest))).thenReturn(response);
    }

    private static GoApiRequest requestMatcherFor(final String apiCallType, final String bodyOfRequest) {
        return argThat(new BaseMatcher<GoApiRequest>() {
            @Override
            public boolean matches(Object o) {
                if (!(o instanceof GoApiRequest)) {
                    return false;
                }

                GoApiRequest actualRequest = (GoApiRequest) o;
                boolean requestBodiesMatch = Json.toMap(actualRequest.requestBody()).equals(Json.toMap(bodyOfRequest));
                boolean requestTypesMatch = actualRequest.api().equals(apiCallType);

                return requestTypesMatch && requestBodiesMatch;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Type: " + apiCallType + " and body: " + bodyOfRequest);
            }
        });
    }

}
