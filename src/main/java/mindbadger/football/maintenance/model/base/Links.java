package mindbadger.football.maintenance.model.base;

public class Links {
    private String self;
    private String related;
    private String first;
    private String last;

    public Links () {}
    public Links (String self, String related, String first, String last) {
        this.self = self;
        this.related = related;
        this.first = first;
        this.last = last;
    }

    public String getSelf() {
        return self;
    }

    public void setSelf(String self) {
        this.self = self;
    }

    public String getRelated() {
        return related;
    }

    public void setRelated(String related) {
        this.related = related;
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }
}
