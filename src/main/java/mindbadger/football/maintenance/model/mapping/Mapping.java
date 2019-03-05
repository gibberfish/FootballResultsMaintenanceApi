package mindbadger.football.maintenance.model.mapping;

import mindbadger.football.maintenance.model.base.JsonApiBase;

public class Mapping extends JsonApiBase {
    private Attributes attributes;

    public Attributes getAttributes() {
        return attributes;
    }

    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
    }

    public class Attributes {
        private int sourceId;
        private String fraId;

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
    }
}
