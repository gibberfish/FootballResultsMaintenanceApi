package mindbadger.football.maintenance.model.mapping;

import mindbadger.football.maintenance.model.base.JsonApiBase;

public class Mapping extends JsonApiBase {
    private Attributes attributes;

    public Mapping (String type) {
        this.attributes = new Attributes();
        this.type = type;
    }

    public Attributes getAttributes() {
        return attributes;
    }

    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
    }

    public class Attributes {
        private int sourceId;
        private String fraId;
        private String dialect;

        public int getSourceId() {
            return sourceId;
        }

        public void setSourceId(int sourceId) {
            this.sourceId = sourceId;
        }

        public String getFraId() {
            return fraId;
        }

        public void setFraId(String fraId) {
            this.fraId = fraId;
        }

        public String getDialect() { return dialect; }

        public void setDialect(String dialect) { this.dialect = dialect; }
    }
}
