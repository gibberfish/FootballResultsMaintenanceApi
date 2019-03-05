package mindbadger.football.maintenance.model.seasondivisionteam;

import mindbadger.football.maintenance.model.base.JsonApiResponseBase;

public class SingleSeasonDivisionTeam extends JsonApiResponseBase {
    SeasonDivisionTeam data;

    public SeasonDivisionTeam getData() {
        return data;
    }

    public void setData(SeasonDivisionTeam data) {
        this.data = data;
    }
}
