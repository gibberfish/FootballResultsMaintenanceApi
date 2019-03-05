package mindbadger.football.maintenance.model.season;

import mindbadger.football.maintenance.model.base.JsonApiResponseBase;

public class SingleSeason extends JsonApiResponseBase {
    Season data;

    public Season getData() {
        return data;
    }

    public void setData(Season data) {
        this.data = data;
    }
}
