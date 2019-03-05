package mindbadger.football.maintenance.model.seasondivision;

import mindbadger.football.maintenance.model.base.JsonApiResponseBase;

public class SingleSeasonDivision extends JsonApiResponseBase {
    SeasonDivision data;

    public SeasonDivision getData() {
        return data;
    }

    public void setData(SeasonDivision data) {
        this.data = data;
    }
}
