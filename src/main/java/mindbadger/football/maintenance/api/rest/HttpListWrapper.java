package mindbadger.football.maintenance.api.rest;

import com.google.gson.Gson;
import mindbadger.football.maintenance.model.base.JsonApiBase;
import mindbadger.football.maintenance.model.base.JsonApiList;
import mindbadger.football.maintenance.util.ApplicationContextProvider;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;
import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class HttpListWrapper<L extends JsonApiList, C extends JsonApiBase> {
    Logger logger = Logger.getLogger(HttpListWrapper.class);

    private ServiceInvoker serviceInvoker;

    public HttpListWrapper() {
        serviceInvoker = (ServiceInvoker) ApplicationContextProvider.getApplicationContext().getBean("serviceInvoker");
    }

    public List<C> getList (String url, String mediaType, Class<L> type) throws IOException {
        MultiValuedMap<String, String> params = new HashSetValuedHashMap<>();
        return getList(url, mediaType, params, type);
    }

    public List<C> getList (String url, String mediaType, MultiValuedMap<String, String> params, Class<L> type) throws IOException {
        try {
            SimpleResponse response = serviceInvoker.get(url, mediaType, params);
            Gson gson = new Gson();
            L mappingsList = gson.fromJson(response.getBody(), type);
            return mappingsList.getData();
        } catch (URISyntaxException e) {
            logger.error("URISyntaxException executing getList: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void saveList (String url, L listOfObjectsToSave, String mediaType, Class<L> type) throws IOException {
        Gson gson = new Gson();
        String payload = gson.toJson(listOfObjectsToSave);

        SimpleResponse response = null;
        response = serviceInvoker.put(url, ServiceInvoker.APPLICATION_VND_API_JSON, payload);
    }
}
