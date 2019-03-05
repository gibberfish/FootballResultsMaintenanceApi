package mindbadger.football.maintenance.model.Fixture;

import mindbadger.football.maintenance.model.Fixture.Fixture;
import mindbadger.football.maintenance.model.base.JsonApiResponseBase;

public class SingleFixture extends JsonApiResponseBase {
    Fixture data;

    public Fixture getData() {
        return data;
    }

    public void setData(Fixture data) {
        this.data = data;
    }
}
