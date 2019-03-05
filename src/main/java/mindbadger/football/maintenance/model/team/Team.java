package mindbadger.football.maintenance.model.team;

import mindbadger.football.maintenance.model.base.JsonApiBase;
import mindbadger.football.maintenance.model.base.Links;

public class Team extends JsonApiBase {
    private Team.Attributes attributes;

    public Team() {
        this.attributes = new Team.Attributes();
        this.links = new Links();
        this.type = "teams";
    }

    public Attributes getAttributes() {
        return attributes;
    }

    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
    }

    public class Attributes {
        private String teamName;

        public String getTeamName() {
            return teamName;
        }

        public void setTeamName(String teamName) {
            this.teamName = teamName;
        }
    }
}
