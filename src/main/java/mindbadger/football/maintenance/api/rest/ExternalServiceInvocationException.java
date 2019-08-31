package mindbadger.football.maintenance.api.rest;

public class ExternalServiceInvocationException extends Exception {
    private Exception embeddedException;

    public ExternalServiceInvocationException (Exception embeddedException) {
        super(embeddedException.getMessage());
        this.embeddedException = embeddedException;
    }

    @Override
    public String getMessage() {
        return embeddedException.getMessage();
    }

    public Exception getEmbeddedException() {
        return embeddedException;
    }
}
