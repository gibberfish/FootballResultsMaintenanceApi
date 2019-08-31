package mindbadger.football.maintenance.api.rest;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class GetResponseHandler implements ResponseHandler<SimpleResponse> {

    @Override
    public SimpleResponse handleResponse(HttpResponse response) throws ServiceInvokerException, IOException {
        SimpleResponse simpleResponse = new SimpleResponse ();

        int status = response.getStatusLine().getStatusCode();
        simpleResponse.setStatusCode(status);

        HttpEntity entity = response.getEntity();
        simpleResponse.setBody(entity != null ? EntityUtils.toString(entity) : null);

        if (status >= 200 && status < 300) {
            return simpleResponse;
        } else {
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(simpleResponse.getBody());
            if(element.isJsonObject()) {
                JsonObject jsonObject = element.getAsJsonObject();

                JsonElement errors = jsonObject.get("errors");

                if (errors != null && errors.isJsonArray()) {
                    JsonArray arrayOfError = errors.getAsJsonArray();
                    JsonElement firstError = arrayOfError.get(0);

                    if (firstError != null && firstError.isJsonObject()) {
                        JsonObject firstErrorObject = firstError.getAsJsonObject();
                        JsonElement detailElement = jsonObject.get("detail");

                        if (detailElement != null && detailElement.isJsonPrimitive()) {
                            String detail = detailElement.getAsString();
                            simpleResponse.setErrorDetail(detail);
                        }
                    }
                }
            }

            throw new ServiceInvokerException(simpleResponse);
        }
    }
}
