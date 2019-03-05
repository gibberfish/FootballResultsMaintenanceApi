package mindbadger.football.maintenance.model.team;

import mindbadger.football.maintenance.model.base.JsonApiResponseBase;

public class SingleTeam extends JsonApiResponseBase {
    Team data;

    public Team getData() {
        return data;
    }

    public void setData(Team data) {
        this.data = data;
    }
}
