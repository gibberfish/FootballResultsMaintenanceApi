package mindbadger.football.maintenance.model.trackeddivision;

import mindbadger.football.maintenance.model.base.JsonApiBase;

public class TrackedDivision extends JsonApiBase {
    private Attributes attributes;

    public Attributes getAttributes() {
        return attributes;
    }

    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
    }

    public class Attributes {
        private int sourceId;
        private String dialect;

        public int getSourceId() {
            return sourceId;
        }

        public void setSourceId(int sourceId) {
            this.sourceId = sourceId;
        }

        public String getDialect() {
            return dialect;
        }

        public void setDialect(String dialect) {
            this.dialect = dialect;
        }
    }
}
