package mindbadger.football.maintenance.model.base;

import java.lang.reflect.Type;
import java.util.List;

public class JsonApiList<T> extends JsonApiResponseBase {
    List<T> data;

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
