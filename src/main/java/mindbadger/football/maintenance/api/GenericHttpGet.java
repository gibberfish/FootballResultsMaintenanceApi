package mindbadger.football.maintenance.api;

import com.google.gson.Gson;
import mindbadger.football.maintenance.model.base.JsonApiBase;
import mindbadger.football.maintenance.model.base.JsonApiList;
import mindbadger.football.maintenance.util.ApplicationContextProvider;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class GenericHttpGet<L extends JsonApiList, C extends JsonApiBase> {
    Logger logger = Logger.getLogger(GenericHttpGet.class);

    private ServiceInvoker serviceInvoker;

    public GenericHttpGet() {
        serviceInvoker = (ServiceInvoker) ApplicationContextProvider.getApplicationContext().getBean("serviceInvoker");
    }

    public List<C> getList (String url, String mediaType, MultiValuedMap<String, String> params, Class<L> type) {
        try {
            String response = serviceInvoker.get(url, mediaType, params);
            Gson gson = new Gson();
            L mappingsList = gson.fromJson(response, type);
            return mappingsList.getData();
        } catch (IOException e) {
            logger.error("IOException executing getList: " + e.getMessage());
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            logger.error("URISyntaxException executing getList: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
