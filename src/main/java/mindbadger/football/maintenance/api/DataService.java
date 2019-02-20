package mindbadger.football.maintenance.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class DataService {
    @Value("${data.api.target}")
    private String dataApiTarget;

    private JsonObject callApiGet (String targetType) {
        Map<String,String> parameters = new HashMap<>();
        return callApiGet(targetType, parameters);
    }

    private JsonObject callApiGet (String targetType, Map<String,String> parameters) {
        Client client = ClientBuilder.newClient();

        System.out.println("dataApiTarget = " + dataApiTarget);

        WebTarget webTarget
                = client.target(dataApiTarget);

        WebTarget getDivisionsTarget
                = webTarget.path(targetType);


        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            getDivisionsTarget = getDivisionsTarget.queryParam(entry.getKey(), entry.getValue());
        }

        System.out.println(getDivisionsTarget.toString());

        JsonObject rootJsonObject = getDivisionsTarget.
                request("application/vnd.api+json").get(JsonObject.class);

        return rootJsonObject;
    }

    public List<String> getSeasonDivisions (int seasonNumber) {
        String url = "seasons/" + seasonNumber + "/seasonDivisions";
        System.out.println("url = " + url);

        JsonObject root = callApiGet(url);

        JsonArray data = root.getJsonArray("data");

        List<String> divisions = new ArrayList<>();

        for (JsonObject jsonObject : data.getValuesAs(JsonObject.class)) {
            JsonObject attributes = jsonObject.getJsonObject("attributes");

            divisions.add(attributes.getString("divisionId"));
//            System.out.println(Arrays.asList(
//                    jsonObject.getString("id"),
//                    jsonObject.getString("type"),
//                    attributes.get("divisionName")));
        }
        return divisions;
    }

    public void getUnplayedFixturesForDivisionBeforeToday (int seasonNumber, String divisionId) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.now();
        String dateString = date.format(formatter);

        String seasonDivision = seasonNumber + "_" + divisionId;

        String url = "seasonDivisions/" + seasonDivision + "/fixtures";

        Map<String, String> params = new HashMap<>();
        params.put("page[limit]", "10000");
        params.put("filter[fixtures][homeGoals][EQ]","null");
        params.put("filter[fixtureDate][LE]", dateString);

        JsonObject root = callApiGet(url, params);

        JsonArray data = root.getJsonArray("data");

        List<String> divisions = new ArrayList<>();

        for (JsonObject jsonObject : data.getValuesAs(JsonObject.class)) {
            String id = jsonObject.getString("id");
            System.out.println("id = " + id);
        }
        /*
        dataapi/seasonDivisions/2018_1/fixtures?

        &
        &

         */

//        Client client = ClientBuilder.newClient();
//
//        WebTarget webTarget
//                = client.target(dataApiTarget);
//
//        WebTarget getDivisionsTarget
//                = webTarget.path("divisions");
//
//        JsonObject rootJsonObject = getDivisionsTarget.
//                request("application/vnd.api+json").get(JsonObject.class);
//
//        //String list = serviceResponse.readEntity(new GenericType<String>() {});
////         System.out.println(list);
//
//        JsonArray data = rootJsonObject.getJsonArray("data");
//
//        for (JsonObject jsonObject : data.getValuesAs(JsonObject.class)) {
//            JsonObject attributes = jsonObject.getJsonObject("attributes");
//            System.out.println(Arrays.asList(
//                    jsonObject.getString("id"),
//                    jsonObject.getString("type"),
//                    attributes.get("divisionName")));
//        }
    }
}
