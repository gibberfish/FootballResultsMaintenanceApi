package mindbadger.football.maintenance.api.rest;

import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component("serviceInvoker")
public class ServiceInvoker {
    private Logger logger = Logger.getLogger(ServiceInvoker.class);

    @Value("${service.timeout}")
    private int timeout;

    public static final String APPLICATION_VND_API_JSON = "application/vnd.api+json";

    public String get(String uri, String mediaType) throws IOException, URISyntaxException {
        MultiValuedMap<String, String> params = new HashSetValuedHashMap<>();
        return get(uri, mediaType, params);
    }

    public String get(String uri, String mediaType, MultiValuedMap<String, String> parameters)
            throws URISyntaxException, IOException {

        logger.debug("Execute get on " + uri + ", " + mediaType + ", with");

        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(timeout * 1000)
                .setConnectionRequestTimeout(timeout * 1000)
                .setSocketTimeout(timeout * 1000)
                .setSocketTimeout(timeout * 1000).build();
        CloseableHttpClient httpclient =
                HttpClientBuilder.create().setDefaultRequestConfig(config).build();

        try {
            URIBuilder builder = new URIBuilder(uri);
            MapIterator<String, String> iterator = parameters.mapIterator();
            List<NameValuePair> nvps = new ArrayList<>();
            while ((iterator.hasNext())) {
                String key = iterator.next();
                String value = iterator.getValue();
                nvps.add(new BasicNameValuePair(key, value));
            }
            builder.setParameters(nvps);

            HttpGet httpget = new HttpGet(builder.build());
            httpget.setHeader("Content-type", mediaType);

            logger.debug("... ABOUT TO EXECUTE : " + httpget + " (start " + Instant.now() + ")");

            final String result = httpclient.execute(httpget, new GetResponseHandler());
            return result;
        } finally {
            httpclient.close();
            logger.debug("... done at " + Instant.now());

        }
    }

    public String patch(String url, String mediaType, String payload) throws IOException {
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

    public String post(String url, String mediaType, String payload) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Content-type", mediaType);

            final StringEntity stringData = new StringEntity(payload);

            httpPost.setEntity(stringData);

            HttpResponse response = httpclient.execute(httpPost);
            ResponseHandler<String> handler = new GetResponseHandler();
            String body = handler.handleResponse(response);

            InputStream stream = response.getEntity().getContent();
            String result = new BufferedReader(new InputStreamReader(stream))
                    .lines().collect(Collectors.joining("\n"));
            logger.debug("POST response body = " + result);

            return body;
        } finally {
            httpclient.close();
        }
    }

    public String put(String url, String mediaType, String payload) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpPut httpPut = new HttpPut(url);
            httpPut.setHeader("Content-type", mediaType);

            final StringEntity stringData = new StringEntity(payload);

            httpPut.setEntity(stringData);

            return httpclient.execute(httpPut, new GetResponseHandler());
        } finally {
            httpclient.close();
        }
    }
}