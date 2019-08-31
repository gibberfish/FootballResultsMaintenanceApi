package mindbadger.football.maintenance.api.rest;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.IOException;

public class GetResponseHandler implements ResponseHandler<String> {
    private Logger logger = Logger.getLogger(GetResponseHandler.class);

    @Override
    public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
        int status = response.getStatusLine().getStatusCode();
        String reason = response.getStatusLine().getReasonPhrase();

        logger.debug("getStatusLine() = " + response.getStatusLine());
        logger.debug("getReasonPhrase() = " + response.getStatusLine().getReasonPhrase());
        logger.debug("getStatusCode() = " + response.getStatusLine().getStatusCode());
        logger.debug("getProtocalVersion() = " + response.getStatusLine().getProtocolVersion());
        logger.debug("getEntity() = " + response.getEntity().getContent().toString());

        if (status >= 200 && status < 300) {
            HttpEntity entity = response.getEntity();
            return entity != null ? EntityUtils.toString(entity) : null;
        } else {
            throw new ClientProtocolException("Unexpected response status: " + status + ", " + reason);
        }
    }
}
