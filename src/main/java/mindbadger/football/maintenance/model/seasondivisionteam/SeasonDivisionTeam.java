package mindbadger.football.maintenance.model.seasondivisionteam;

import mindbadger.football.maintenance.model.base.JsonApiBase;
import mindbadger.football.maintenance.model.base.Links;
import mindbadger.football.maintenance.model.base.Relationship;

public class SeasonDivisionTeam extends JsonApiBase {
    private Attributes attributes;
    private Relationships relationships;

    public SeasonDivisionTeam() {
        this.attributes = new SeasonDivisionTeam.Attributes();
        this.relationships = new SeasonDivisionTeam.Relationships();
        this.links = new Links();
        this.type = "seasonDivisionTeams";
    }

    public Attributes getAttributes() {
        return attributes;
    }

    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
    }

    public Relationships getRelationships() {
        return relationships;
    }

    public void setRelationships(Relationships relationships) {
        this.relationships = relationships;
    }

    public String getUniqueKey () {
        return attributes.getSeasonNumber() + "_" + attributes.getDivisionId() + "_" + attributes.getTeamId();
    }

    public class Attributes {
        private int seasonNumber;
        private String divisionId;
        private String teamId;

        public int getSeasonNumber() {
            return seasonNumber;
        }

        public void setSeasonNumber(int seasonNumber) {
            this.seasonNumber = seasonNumber;
        }

        public String getDivisionId() {
            return divisionId;
        }

        public void setDivisionId(String divisionId) {
            this.divisionId = divisionId;
        }

        public String getTeamId() {
            return teamId;
        }

        public void setTeamId(String teamId) {
            this.teamId = teamId;
        }
    }

    public class Relationships {
        private Relationship seasonDivision;
        private Relationship teams;

        public Relationship getSeasonDivision() {
            return seasonDivision;
        }

        public void setSeasonDivision(Relationship seasonDivision) {
            this.seasonDivision = seasonDivision;
        }

        public Relationship getTeams() {
            return teams;
        }

        public void setTeams(Relationship teams) {
            this.teams = teams;
        }
    }
}
