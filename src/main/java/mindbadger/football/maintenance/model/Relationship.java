package mindbadger.football.maintenance.model;

public class Relationship {
    Data data;
    Links links;

    public Relationship () {}

    public Relationship (Data data, Links links) {
        this.data = data;
        this.links = links;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }
}
