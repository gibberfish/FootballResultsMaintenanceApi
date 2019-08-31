package mindbadger.football.maintenance.api.rest;

import org.apache.http.client.ClientProtocolException;

public class ServiceInvokerException extends RuntimeException {
    private SimpleResponse simpleResponse;

    public ServiceInvokerException (SimpleResponse simpleResponse) {
        this.simpleResponse = simpleResponse;
    }

    @Override
    public String getMessage() {
        return simpleResponse.getStatusCode() + ": " + simpleResponse.getErrorDetail();
    }
}
