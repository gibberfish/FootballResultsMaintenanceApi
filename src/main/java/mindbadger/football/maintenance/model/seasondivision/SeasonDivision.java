package mindbadger.football.maintenance.model.seasondivision;

import mindbadger.football.maintenance.model.base.JsonApiBase;
import mindbadger.football.maintenance.model.base.Links;
import mindbadger.football.maintenance.model.base.Relationship;
import mindbadger.football.maintenance.model.season.Season;

public class SeasonDivision extends JsonApiBase {
    private Attributes attributes;
    private Relationships relationships;


    public SeasonDivision () {
        this.attributes = new SeasonDivision.Attributes();
        this.relationships = new SeasonDivision.Relationships();
        this.links = new Links();
        this.type = "seasonDivisions";
    }

    public String getUniqueKey () {
        return attributes.getSeasonNumber() + "_" + attributes.getDivisionId();
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

    public class Attributes {
        private int position;
        private int seasonNumber;
        private String divisionId;

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

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
    }

    public class Relationships {
        private Relationship division;
        private Relationship teams;
        private Relationship fixtureDates;
        private Relationship season;
        private Relationship fixtures;

        public Relationship getDivision() {
            return division;
        }

        public void setDivision(Relationship division) {
            this.division = division;
        }

        public Relationship getTeams() {
            return teams;
        }

        public void setTeams(Relationship teams) {
            this.teams = teams;
        }

        public Relationship getFixtureDates() {
            return fixtureDates;
        }

        public void setFixtureDates(Relationship fixtureDates) {
            this.fixtureDates = fixtureDates;
        }

        public Relationship getSeason() {
            return season;
        }

        public void setSeason(Relationship season) {
            this.season = season;
        }

        public Relationship getFixtures() {
            return fixtures;
        }

        public void setFixtures(Relationship fixtures) {
            this.fixtures = fixtures;
        }
    }
}
