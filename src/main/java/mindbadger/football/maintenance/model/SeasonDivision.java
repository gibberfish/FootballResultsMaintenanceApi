package mindbadger.football.maintenance.model;

public class SeasonDivision extends JsonApiBase{
    private Attributes attributes;
    private Relationships relationships;


    public SeasonDivision () {}
    public SeasonDivision (String id, String type, Links links, Attributes attributes, Relationships relationships) {
        this.id = id;
        this.type = type;
        this.links = links;
        this.attributes = attributes;
        this.relationships = relationships;
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

    private class Attributes {
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

    private class Relationships {
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
