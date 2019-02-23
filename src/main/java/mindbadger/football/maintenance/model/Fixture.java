package mindbadger.football.maintenance.model;

public class Fixture extends JsonApiBase {
    private Fixture.Attributes attributes;
    private Fixture.Relationships relationships;

    private class Attributes {
        int seasonNumber;
        String fixtureDate;
        String divisionId;
        String homeTeamId;
        String awayTeamId;
        Integer homeGoals;
        Integer awayGoals;

        public int getSeasonNumber() {
            return seasonNumber;
        }

        public void setSeasonNumber(int seasonNumber) {
            this.seasonNumber = seasonNumber;
        }

        public String getFixtureDate() {
            return fixtureDate;
        }

        public void setFixtureDate(String fixtureDate) {
            this.fixtureDate = fixtureDate;
        }

        public String getDivisionId() {
            return divisionId;
        }

        public void setDivisionId(String divisionId) {
            this.divisionId = divisionId;
        }

        public String getHomeTeamId() {
            return homeTeamId;
        }

        public void setHomeTeamId(String homeTeamId) {
            this.homeTeamId = homeTeamId;
        }

        public String getAwayTeamId() {
            return awayTeamId;
        }

        public void setAwayTeamId(String awayTeamId) {
            this.awayTeamId = awayTeamId;
        }

        public Integer getHomeGoals() {
            return homeGoals;
        }

        public void setHomeGoals(Integer homeGoals) {
            this.homeGoals = homeGoals;
        }

        public Integer getAwayGoals() {
            return awayGoals;
        }

        public void setAwayGoals(Integer awayGoals) {
            this.awayGoals = awayGoals;
        }
    }

    private class Relationships {
        private Relationship seasonDivision;
        private Relationship homeTeam;
        private Relationship awayTeam;

        public Relationship getSeasonDivision() {
            return seasonDivision;
        }

        public void setSeasonDivision(Relationship seasonDivision) {
            this.seasonDivision = seasonDivision;
        }

        public Relationship getHomeTeam() {
            return homeTeam;
        }

        public void setHomeTeam(Relationship homeTeam) {
            this.homeTeam = homeTeam;
        }

        public Relationship getAwayTeam() {
            return awayTeam;
        }

        public void setAwayTeam(Relationship awayTeam) {
            this.awayTeam = awayTeam;
        }
    }
}
