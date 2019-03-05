package mindbadger.football.maintenance.model.mapping;

import mindbadger.football.maintenance.model.base.JsonApiResponseBase;

public class SingleMapping extends JsonApiResponseBase {
    Mapping data;

    public Mapping getData() {
        return data;
    }

    public void setData(Mapping data) {
        this.data = data;
    }
}
