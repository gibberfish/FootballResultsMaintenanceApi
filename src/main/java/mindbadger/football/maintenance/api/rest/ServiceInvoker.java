package mindbadger.football.maintenance.api.rest;

import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@Component("serviceInvoker")
public class ServiceInvoker {
    private Logger logger = Logger.getLogger(ServiceInvoker.class);


    public static final String APPLICATION_VND_API_JSON = "application/vnd.api+json";

//    //TODO Remove me!!!!!!
//    public static void main(String[] args) {
//        ServiceInvoker si = new ServiceInvoker();
//        MultiValuedMap<String, String> params = new HashSetValuedHashMap<>();
//        params.put("filter[homeGoals]", "1");
//
//        try {
//            String response = si.get("http://localhost:1972/dataapi/seasonDivisions/2017_2/fixtures", ServiceInvoker.APPLICATION_VND_API_JSON, params);
//            logger.debug("GET: " + response);
//
//            final String payload = "{\n" +
//                    "    \"data\": {\n" +
//                    "\t    \"id\": \"409548\",\n" +
//                    "\t    \"type\": \"fixtures\",\n" +
//                    "\t    \"attributes\": {\n" +
//                    "\t        \"seasonNumber\": 2017,\n" +
//                    "\t        \"fixtureDate\": \"2017-09-19\",\n" +
//                    "\t        \"divisionId\": \"2\",\n" +
//                    "\t        \"homeTeamId\": \"65\",\n" +
//                    "\t        \"awayTeamId\": \"42\",\n" +
//                    "\t        \"homeGoals\": 1,\n" +
//                    "\t        \"awayGoals\": 0\n" +
//                    "\t    }\n" +
//                    "\t}\n" +
//                    "}";
//
//            response = si.patch("http://localhost:1972/dataapi/fixtures/409548", ServiceInvoker.APPLICATION_VND_API_JSON, payload);
//            logger.debug("PATCH: " + response);
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public String get (String uri, String mediaType) throws IOException, URISyntaxException {
        MultiValuedMap<String, String> params = new HashSetValuedHashMap<>();
        return get(uri, mediaType, params);
    }

    public String get (String uri, String mediaType, MultiValuedMap<String, String> parameters)
            throws URISyntaxException, IOException {

        logger.debug("Execute get on " + uri + ", " + mediaType + ", with");

        int timeout = 60;
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(timeout * 1000)
                .setConnectionRequestTimeout(timeout * 1000)
                .setSocketTimeout(timeout * 1000).build();
        CloseableHttpClient httpclient =
                HttpClientBuilder.create().setDefaultRequestConfig(config).build();

        try {
            URIBuilder builder = new URIBuilder(uri);
            MapIterator<String,String> iterator = parameters.mapIterator();
            List<NameValuePair> nvps = new ArrayList<>();
            while ((iterator.hasNext())) {
                String key = iterator.next();
                String value = iterator.getValue();
                nvps.add(new BasicNameValuePair(key, value));
            }
            builder.setParameters(nvps);

            HttpGet httpget = new HttpGet(builder.build());
            httpget.setHeader("Content-type", mediaType);

            logger.debug("... ABOUT TO EXECUTE : " + httpget);

            return httpclient.execute(httpget, new GetResponseHandler());
        } finally {
            httpclient.close();
        }
    }

    public String patch (String url, String mediaType, String payload) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpPatch httpPatch = new HttpPatch(url);
            httpPatch.setHeader("Content-type", mediaType);

            final StringEntity stringData = new StringEntity(payload);

            httpPatch.setEntity(stringData);

            return httpclient.execute(httpPatch, new GetResponseHandler());
        } finally {
            httpclient.close();
        }
    }

    public String post (String url, String mediaType, String payload) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Content-type", mediaType);

            final StringEntity stringData = new StringEntity(payload);

            httpPost.setEntity(stringData);

            return httpclient.execute(httpPost, new GetResponseHandler());
        } finally {
            httpclient.close();
        }
    }


//    public Response callApiGet (String apiTarget, String targetType, String mediaType) {
//        MultiValuedMap<String, String> parameters = new HashSetValuedHashMap<>();
//        return callApiGet(apiTarget, targetType, mediaType, parameters);
//    }
//
//    public Response callApiGet (String apiTarget, String targetType, String mediaType, MultiValuedMap<String, String> parameters) {
//        ClientConfig configuration = new ClientConfig();
//        configuration = configuration.property(ClientProperties.CONNECT_TIMEOUT, 10000);
//        configuration = configuration.property(ClientProperties.READ_TIMEOUT, 60000);
//        Client client = ClientBuilder.newClient(configuration);
//
//        logger.debug("apiTarget = " + apiTarget);
//        logger.debug("mediaType = " + mediaType);
//
//        WebTarget webTarget
//                = client.target(apiTarget);
//
//        WebTarget getDivisionsTarget
//                = webTarget.path(targetType);
//
//        MapIterator<String,String> iterator = parameters.mapIterator();
//        while ((iterator.hasNext())) {
//            String key = iterator.next();
//            String value = iterator.getValue();
//            getDivisionsTarget = getDivisionsTarget.queryParam(key, value);
//        }
//
//        logger.debug(getDivisionsTarget.toString());
//
//        return getDivisionsTarget.
//                request(mediaType).get();
//    }
//
//    public Response callApiPost (String apiTarget, String targetType, String mediaType, Object payload) {
//        ClientConfig configuration = new ClientConfig();
//        configuration = configuration.property(ClientProperties.CONNECT_TIMEOUT, 10000);
//        configuration = configuration.property(ClientProperties.READ_TIMEOUT, 60000);
//        Client client = ClientBuilder.newClient(configuration);
//
//        WebTarget webTarget
//                = client.target(apiTarget);
//
//        WebTarget getDivisionsTarget
//                = webTarget.path(targetType);
//
//        Response response = getDivisionsTarget.request(mediaType).post (Entity.entity(payload, mediaType));
//
//        return response;
//    }
//
//    public Response callApiPatch (String apiTarget, String targetType, String mediaType, Object payload, String id) {
//
//
//        HttpClient httpclient = HttpClientBuilder.create().build();
//
//
//        String url = apiTarget + "/" + targetType + "/" + id;
//        logger.debug("URL = " + url);
//        HttpUriRequest req = new HttpPatch(url);
//        req.setHeader("Content-type", ServiceInvoker.APPLICATION_VND_API_JSON);
//
//        final StringEntity stringData;
//        try {
//            stringData = new StringEntity(payload.toString());
//            ((HttpPatch)req).setEntity(stringData);
//
//            HttpResponse httpResponse = httpclient.execute(req);
//
//            logger.debug("Status code : " + httpResponse.getStatusLine().getStatusCode());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return null;
//
////        ClientConfig configuration = new ClientConfig();
////        configuration = configuration.property(ClientProperties.CONNECT_TIMEOUT, 10000);
////        configuration = configuration.property(ClientProperties.READ_TIMEOUT, 60000);
////        configuration = configuration.property(HttpUrlConnectorProvider.SET_METHOD_WORKAROUND, true);
////        Client client = ClientBuilder.newClient(configuration);
////
////        WebTarget webTarget
////                = client.target(apiTarget);
////
////        WebTarget getDivisionsTarget
////                = webTarget.path(targetType);
////
////        Response response = getDivisionsTarget.request(mediaType).method("PATCH", Entity.entity(payload, mediaType));
////
////        return response;
//    }

}
