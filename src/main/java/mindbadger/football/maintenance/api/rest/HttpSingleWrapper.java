package mindbadger.football.maintenance.api.rest;

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

    public C getSingle (String url, String mediaType, Class<S> type) throws IOException {
        MultiValuedMap<String, String> params = new HashSetValuedHashMap<>();
        return getSingle(url, mediaType, params, type);
    }

    public C getSingle (String url, String mediaType, MultiValuedMap<String, String> params, Class<S> type) throws IOException {
        try {
            SimpleResponse response = serviceInvoker.get(url, mediaType, params);
            Gson gson = new Gson();
            S fixturesList = gson.fromJson(response.getBody(), type);
            return fixturesList.getData();
        } catch (URISyntaxException e) {
            logger.error("URISyntaxException executing getSingle: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public S createOrUpdate (String url, S objectToSave, String mediaType, Class<S> type) throws IOException {
        Gson gson = new Gson();
        String payload = gson.toJson(objectToSave);

        SimpleResponse response = null;
        if (objectToSave.getData().getId() == null) {
            logger.debug("POST: " + payload);
            response = serviceInvoker.post(url, ServiceInvoker.APPLICATION_VND_API_JSON, payload);
        } else {
            url = url + "/" + objectToSave.getData().getId();
            logger.debug("PATCH: " + payload);
            response = serviceInvoker.patch(url, ServiceInvoker.APPLICATION_VND_API_JSON, payload);
        }

        return gson.fromJson(response.getBody(), type);
    }

    public void delete (String url, String mediaType) throws IOException {
        serviceInvoker.delete(url, ServiceInvoker.APPLICATION_VND_API_JSON);
    }
}
