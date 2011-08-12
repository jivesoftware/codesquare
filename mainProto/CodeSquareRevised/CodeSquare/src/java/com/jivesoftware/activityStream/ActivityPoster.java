package com.jivesoftware.activityStream;

import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthException;
import net.oauth.OAuthMessage;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;

/***
 * Code taken from this
 * https://developers.jivesoftware.com/community/docs/DOC-1119
 * @author jive tutorial
 */
public class ActivityPoster {
    private static final String DEFAULT_GATEWAY_URL = "http://gateway.jivesoftware.com";
    private static final Pattern MATCH_URL = Pattern
            .compile("^https?://\\w+([_.-]\\w+)(:[1-9][0-9]{0,4})?");
    private static final Pattern MATCH_USER_ID = Pattern
            .compile("^-?[1-9][0-9]{0,17}$");

    private String jive_id, app_id, user_id, key, secret, activity;
    private String gateway_url, deliver_to_user_id;

    public ActivityPoster(final String jive_id, final String app_id,
            final String user_id, final String key, final String secret,
            final String activity) {
        this(DEFAULT_GATEWAY_URL, jive_id, app_id, user_id, null, key, secret,
                activity);
    }

    private ActivityPoster(final String gateway_url, final String jive_id,
            final String app_id, final String user_id,
            final String deliver_to_user_id, final String key,
            final String secret, final String activity) {
        this.gateway_url = gateway_url.replaceAll("/+$", "");
        this.jive_id = jive_id;
        this.app_id = app_id;
        this.user_id = user_id;
        this.deliver_to_user_id = deliver_to_user_id;
        this.key = key;
        this.secret = secret;
        this.activity = activity;
    }

    public String post() {
        String requestURI = gateway_url + "/gateway/api/activity/v1/update/"
                + jive_id + '/' + app_id + '/' + user_id;
        if (deliver_to_user_id != null) {
            requestURI += "?deliverTo=" + deliver_to_user_id;
        }
        String body = activity;
        OAuthMessage message = sign(requestURI, body);
        String result = "";
        try {
            System.out.println("Posting " + message.URL);
            System.out.println("Authorization: "
                    + message.getAuthorizationHeader(null));

            StatusLine statusLine = fetch(message);
            result = statusLine.getStatusCode() + " "
                    + statusLine.getReasonPhrase();
        } catch (IOException e) {
            e.printStackTrace();
            result = e.getMessage();
        }

        return result;
    }

    protected OAuthMessage sign(String requestURI, String body) {
        Collection<? extends Map.Entry> parameters = null;

        OAuthConsumer oauthConsumer = new OAuthConsumer(null, key, secret, null);
        OAuthAccessor accessor = new OAuthAccessor(oauthConsumer);
        OAuthMessage oauthMessage = null;
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(
                    body.getBytes());
            oauthMessage = accessor.newRequestMessage("POST", requestURI,
                    parameters, bais);
        } catch (OAuthException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return oauthMessage;
    }

    protected StatusLine fetch(OAuthMessage message) throws IOException {
        HttpRequestBase httpMethod;
        String methodType = message.method;
        if ("POST".equals(methodType) || "PUT".equals(methodType)) {
            HttpEntityEnclosingRequestBase enclosingMethod = ("POST"
                    .equals(methodType)) ? new HttpPost(message.URL)
                    : new HttpPut(message.URL);

            StringEntity bodyStreamEntity = new StringEntity(
                    message.readBodyAsString(), "application/json", "utf-8");
            enclosingMethod.setEntity(bodyStreamEntity);
            httpMethod = enclosingMethod;
        } else if ("DELETE".equals(methodType)) {
            httpMethod = new HttpDelete(message.URL);
        } else if ("HEAD".equals(methodType)) {
            httpMethod = new HttpHead(message.URL);
        } else {
            httpMethod = new HttpGet(message.URL);
        }

        httpMethod.setHeader("Authorization",
                message.getAuthorizationHeader(null));
        httpMethod.setHeader("X-Jive-App-Id", app_id);
        HttpClient client = null;
        try {
            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, (int) 1500);
            HttpConnectionParams.setSoTimeout(httpParams, (int) 5000);
            httpParams
                    .setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, false);
            httpParams.setBooleanParameter(
                    CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
            httpMethod.setParams(httpParams);
            client = new DefaultHttpClient();
            HttpResponse httpResponse = client.execute(httpMethod);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            System.out.println("Activity post response: " + statusCode);
            printResponse(httpResponse);
            return httpResponse.getStatusLine();
        } catch (IOException e) {
            System.err.println("Exception when trying to fetch " + message.URL);
            e.printStackTrace();
            throw e;
        } finally {
            if (client != null)
                client.getConnectionManager().shutdown();
        }
    }

    private void printResponse(HttpResponse httpResponse) throws IOException {
        System.out.println(httpResponse.getStatusLine().getStatusCode() + " "
                + httpResponse.getStatusLine().getReasonPhrase());

        for (Header header : httpResponse.getAllHeaders()) {
            System.out.println(String.format("%s: %s", header.getName(),
                    header.getValue()));
        }

        HttpEntity entity = httpResponse.getEntity();
        if (entity.getContentLength() > 0)
            entity.writeTo(System.out);
    }
    /***
     * Only method that was changed, hardcoded jiveid, appid, key, secret
     * @param user_id
     * @param deliver_to_user_id
     * @param jsonActivity 
     */
    public static void postToActivity(String user_id,
            String deliver_to_user_id, String jsonActivity) {
        try {
            int idx = 0;
            String gateway_url = DEFAULT_GATEWAY_URL;
            String jive_id = "99b3427b-202e-4891-a50e-1fb5e7b173be";
            String app_id = "2862464a-fa1c-439c-a8ab-2686a8b6315c";
            String key = "2862464afa1c439ca8ab2686a8b6315c";
            String secret = "g9GMyn0m+mMKhxaaN0EfzELQtlw=";

            ActivityPoster poster = new ActivityPoster(gateway_url, jive_id,
                    app_id, user_id, deliver_to_user_id, key, secret,
                    jsonActivity);
            String result = poster.post();
            System.out.println("\nPost response " + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
