package mindbadger.football.maintenance.api;

import com.google.gson.Gson;
import mindbadger.football.maintenance.model.base.JsonApiBase;
import mindbadger.football.maintenance.model.base.JsonApiSingle;
import mindbadger.football.maintenance.util.ApplicationContextProvider;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;
import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;

public class HttpSingleWrapper<S extends JsonApiSingle<C>, C extends JsonApiBase> {
    Logger logger = Logger.getLogger(HttpSingleWrapper.class);

    private ServiceInvoker serviceInvoker;

    public HttpSingleWrapper() {
        serviceInvoker = (ServiceInvoker) ApplicationContextProvider.getApplicationContext().getBean("serviceInvoker");
    }

    public C getSingle (String url, String mediaType, Class<S> type) throws ClientProtocolException {
        MultiValuedMap<String, String> params = new HashSetValuedHashMap<>();
        return getSingle(url, mediaType, params, type);
    }

    public C getSingle (String url, String mediaType, MultiValuedMap<String, String> params, Class<S> type) throws ClientProtocolException {
        try {
            String response = serviceInvoker.get(url, mediaType, params);
            Gson gson = new Gson();
            S fixturesList = gson.fromJson(response, type);
            return fixturesList.getData();
        } catch (ClientProtocolException e) {
            throw e;
        } catch (IOException | URISyntaxException e) {
            logger.error("URISyntaxException executing getSingle: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public S createOrUpdate (String url, S objectToSave, String mediaType, Class<S> type) throws ClientProtocolException {
        Gson gson = new Gson();
        String payload = gson.toJson(objectToSave);

        String response = null;
        try {
            if (objectToSave.getData().getId() == null) {
                response = serviceInvoker.post(url, ServiceInvoker.APPLICATION_VND_API_JSON, payload);
            } else {
                url = url + "/" + objectToSave.getData().getId();
                response = serviceInvoker.patch(url, ServiceInvoker.APPLICATION_VND_API_JSON, payload);
            }

            return gson.fromJson(response, type);
        } catch (ClientProtocolException e) {
            throw e;
        } catch (IOException e) {
            logger.error("URISyntaxException executing createOrUpdate: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
