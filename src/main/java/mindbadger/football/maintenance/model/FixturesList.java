package mindbadger.football.maintenance.model;

import java.util.List;

public class FixturesList extends JsonApiResponseBase {
    List<Fixture> data;

    public List<Fixture> getData() {
        return data;
    }

    public void setData(List<Fixture> data) {
        this.data = data;
    }
}
