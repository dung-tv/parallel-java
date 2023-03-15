import com.github.underscore.U;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamForeachTest {
    @AllArgsConstructor
    @ToString
    @EqualsAndHashCode
    static class ResourceLocation {
        String id;
        String lat;
        String lng;
    }

    @AllArgsConstructor
    @ToString
    static class ResourceTmp {
        String ls;
        List<ResourceLocation> l;
    }

    @ToString
    static
    class Distance {
        /* Miles, Kilometers, Nautical Miles */
        enum Unit {K, N, M};
        static String distanceStr = "";
        public static double distance(double lat1, double lon1, double lat2, double lon2, Unit unit) {
            double theta = lon1 - lon2;
            double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
            dist = Math.acos(dist);
            dist = rad2deg(dist);
            dist = dist * 60 * 1.1515;
            if (unit == Unit.K) {
                dist = dist * 1.609344;
            } else if (unit == Unit.N) {
                dist = dist * 0.8684;
            }
            return (dist);
        }

        /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
        /*::  This function converts decimal degrees to radians             :*/
        /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
        private static double deg2rad(double deg) {
            return (deg * Math.PI / 180.0);
        }

        /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
        /*::  This function converts radians to decimal degrees             :*/
        /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
        private static double rad2deg(double rad) {
            return (rad * 180.0 / Math.PI);
        }

        public static void setDistanceStr(double dS) {
            distanceStr = distanceStr + dS + "\n";
        }
    }

    public static void main(String[] args) throws SQLException, IOException {
        Connection conn = DatabaseConnection.getConnection();
        Gson gson = new Gson();

        String query = "select json_merge(\n" +
                "    json_object(\"id\", r.id),\n" +
                "    json_object(\"name\", r.name),\n" +
                "    json_object(\"type\", r.type),\n" +
                "    json_object(\"desc\", r.desc),\n" +
                "    json_object(\"created_at\", r.created_at),\n" +
                "    json_object(\"updated_at\", r.updated_at),\n" +
                "    json_object(\"attributes\", (select JSON_ARRAYAGG(\n" +
                "                                            json_merge(\n" +
                "                                                    json_object(\"id\", a.id),\n" +
                "                                                    json_object(\"name\", a.name),\n" +
                "                                                    json_object(\"desc\", a.desc),\n" +
                "                                                    json_object(\"alias\", a.alias),\n" +
                "                                                    json_object(\"value\", a.value),\n" +
                "                                                    json_object(\"created_at\", a.created_at),\n" +
                "                                                    json_object(\"updated_at\", a.updated_at)\n" +
                "                                            )\n" +
                "                                    )\n" +
                "                            from attributes a\n" +
                "                                    left join resource_attribute ra on ra.attribute_id = a.id\n" +
                "                            where ra.resource_id = r.id)\n" +
                "        )\n" +
                ") as resource\n" +
                "from resources r join (select * from (\n" +
                "                    (\n" +
                "                        select IF(a.alias = 'lat', a.value, null) as lat, ra.resource_id as lat_id from attributes a left join resource_attribute ra on a.id = ra.attribute_id\n" +
                "                        where a.alias = 'lat'\n" +
                "                    ) as lat,\n" +
                "                    (\n" +
                "                        select IF(a.alias = 'lng', a.value, null) as lng, ra.resource_id as lng_id from attributes a left join resource_attribute ra on a.id = ra.attribute_id\n" +
                "                        where a.alias = 'lng'\n" +
                "                    ) as lng)\n" +
                ") as tmp on r.id = tmp.lat_id and  r.id = tmp.lng_id;";

        // create the java statement
        Statement st = conn.createStatement();
        System.out.println("START " + Instant.now());

        // execute the query, and get a java resultset
        ResultSet rs = st.executeQuery(query);

        List<ResourceLocation> resourceLocations = new ArrayList<>();
        List<String> resources = new ArrayList<>();
        // iterate through the java resultset
        while (rs.next())
        {
            String resource = rs.getString("resource");
            resources.add(resource);
        }
        st.close();
//        resources.add("{\"id\": 1, \"desc\": null, \"name\": \"Tower\", \"type\": \"TOWER\", \"attributes\": [{\"id\": 1, \"desc\": null, \"name\": \"Tower Type\", \"alias\": \"tower_type\", \"value\": \"2\", \"created_at\": \"2022-12-29 09:47:30.990000\", \"updated_at\": \"2022-12-29 09:47:30.990000\"}, {\"id\": 2, \"desc\": null, \"name\": \"Tower Level\", \"alias\": \"tower_level\", \"value\": \"10\", \"created_at\": \"2022-12-29 09:47:31.039000\", \"updated_at\": \"2022-12-29 09:47:31.039000\"}, {\"id\": 3, \"desc\": null, \"name\": \"Tower Reward 1\", \"alias\": \"tower_reward1\", \"value\": \"500000\", \"created_at\": \"2022-12-29 09:47:31.087000\", \"updated_at\": \"2022-12-29 09:47:31.087000\"}, {\"id\": 4, \"desc\": null, \"name\": \"Tower Reward Type 1\", \"alias\": \"tower_reward_type1\", \"value\": \"2\", \"created_at\": \"2022-12-29 09:47:31.134000\", \"updated_at\": \"2022-12-29 09:47:31.134000\"}, {\"id\": 5, \"desc\": null, \"name\": \"Tower Reward 2\", \"alias\": \"tower_reward2\", \"value\": \"20000\", \"created_at\": \"2022-12-29 09:47:31.184000\", \"updated_at\": \"2022-12-29 09:47:31.184000\"}, {\"id\": 6, \"desc\": null, \"name\": \"Tower Reward Type 2\", \"alias\": \"tower_reward_type2\", \"value\": \"3\", \"created_at\": \"2022-12-29 09:47:31.232000\", \"updated_at\": \"2022-12-29 09:47:31.232000\"}, {\"id\": 7, \"desc\": null, \"name\": \"Latitude\", \"alias\": \"lat\", \"value\": \"10.78660600\", \"created_at\": \"2022-12-29 09:47:31.280000\", \"updated_at\": \"2022-12-29 09:47:31.280000\"}, {\"id\": 8, \"desc\": null, \"name\": \"Longitude\", \"alias\": \"lng\", \"value\": \"106.62610100\", \"created_at\": \"2022-12-29 09:47:31.328000\", \"updated_at\": \"2022-12-29 09:47:31.328000\"}], \"created_at\": \"2022-12-29 09:47:30.942000\", \"updated_at\": \"2022-12-29 09:47:30.942000\"}");
//        resources.add("{\"id\": 1, \"desc\": null, \"name\": \"Tower\", \"type\": \"TOWER\", \"attributes\": [{\"id\": 1, \"desc\": null, \"name\": \"Tower Type\", \"alias\": \"tower_type\", \"value\": \"2\", \"created_at\": \"2022-12-29 09:47:30.990000\", \"updated_at\": \"2022-12-29 09:47:30.990000\"}, {\"id\": 2, \"desc\": null, \"name\": \"Tower Level\", \"alias\": \"tower_level\", \"value\": \"10\", \"created_at\": \"2022-12-29 09:47:31.039000\", \"updated_at\": \"2022-12-29 09:47:31.039000\"}, {\"id\": 3, \"desc\": null, \"name\": \"Tower Reward 1\", \"alias\": \"tower_reward1\", \"value\": \"500000\", \"created_at\": \"2022-12-29 09:47:31.087000\", \"updated_at\": \"2022-12-29 09:47:31.087000\"}, {\"id\": 4, \"desc\": null, \"name\": \"Tower Reward Type 1\", \"alias\": \"tower_reward_type1\", \"value\": \"2\", \"created_at\": \"2022-12-29 09:47:31.134000\", \"updated_at\": \"2022-12-29 09:47:31.134000\"}, {\"id\": 5, \"desc\": null, \"name\": \"Tower Reward 2\", \"alias\": \"tower_reward2\", \"value\": \"20000\", \"created_at\": \"2022-12-29 09:47:31.184000\", \"updated_at\": \"2022-12-29 09:47:31.184000\"}, {\"id\": 6, \"desc\": null, \"name\": \"Tower Reward Type 2\", \"alias\": \"tower_reward_type2\", \"value\": \"3\", \"created_at\": \"2022-12-29 09:47:31.232000\", \"updated_at\": \"2022-12-29 09:47:31.232000\"}, {\"id\": 7, \"desc\": null, \"name\": \"Latitude\", \"alias\": \"lat\", \"value\": \"10.78660600\", \"created_at\": \"2022-12-29 09:47:31.280000\", \"updated_at\": \"2022-12-29 09:47:31.280000\"}, {\"id\": 8, \"desc\": null, \"name\": \"Longitude\", \"alias\": \"lng\", \"value\": \"106.62610100\", \"created_at\": \"2022-12-29 09:47:31.328000\", \"updated_at\": \"2022-12-29 09:47:31.328000\"}], \"created_at\": \"2022-12-29 09:47:30.942000\", \"updated_at\": \"2022-12-29 09:47:30.942000\"}");
//        resources.add("{\"id\": 2, \"desc\": null, \"name\": \"Tower\", \"type\": \"TOWER\", \"attributes\": [{\"id\": 26, \"desc\": null, \"name\": \"Tower Type\", \"alias\": \"tower_type\", \"value\": \"3\", \"created_at\": \"2022-12-29 09:47:33.543000\", \"updated_at\": \"2022-12-29 09:47:33.543000\"}, {\"id\": 27, \"desc\": null, \"name\": \"Tower Level\", \"alias\": \"tower_level\", \"value\": \"13\", \"created_at\": \"2022-12-29 09:47:33.595000\", \"updated_at\": \"2022-12-29 09:47:33.595000\"}, {\"id\": 28, \"desc\": null, \"name\": \"Tower Reward 1\", \"alias\": \"tower_reward1\", \"value\": \"500000\", \"created_at\": \"2022-12-29 09:47:33.640000\", \"updated_at\": \"2022-12-29 09:47:33.640000\"}, {\"id\": 29, \"desc\": null, \"name\": \"Tower Reward Type 1\", \"alias\": \"tower_reward_type1\", \"value\": \"2\", \"created_at\": \"2022-12-29 09:47:33.691000\", \"updated_at\": \"2022-12-29 09:47:33.691000\"}, {\"id\": 30, \"desc\": null, \"name\": \"Tower Reward 2\", \"alias\": \"tower_reward2\", \"value\": \"0\", \"created_at\": \"2022-12-29 09:47:33.737000\", \"updated_at\": \"2022-12-29 09:47:33.737000\"}, {\"id\": 31, \"desc\": null, \"name\": \"Tower Reward Type 2\", \"alias\": \"tower_reward_type2\", \"value\": \"3\", \"created_at\": \"2022-12-29 09:47:33.784000\", \"updated_at\": \"2022-12-29 09:47:33.784000\"}, {\"id\": 32, \"desc\": null, \"name\": \"Latitude\", \"alias\": \"lat\", \"value\": \"10.78671300\", \"created_at\": \"2022-12-29 09:47:33.830000\", \"updated_at\": \"2022-12-29 09:47:33.830000\"}, {\"id\": 33, \"desc\": null, \"name\": \"Longitude\", \"alias\": \"lng\", \"value\": \"106.65605800\", \"created_at\": \"2022-12-29 09:47:33.876000\", \"updated_at\": \"2022-12-29 09:47:33.876000\"}], \"created_at\": \"2022-12-29 09:47:33.494000\", \"updated_at\": \"2022-12-29 09:47:33.494000\"}");
//        resources.add("{\"id\": 3, \"desc\": null, \"name\": \"Tower\", \"type\": \"TOWER\", \"attributes\": [{\"id\": 102, \"desc\": null, \"name\": \"Tower Type\", \"alias\": \"tower_type\", \"value\": \"2\", \"created_at\": \"2022-12-29 09:47:41.233000\", \"updated_at\": \"2022-12-29 09:47:41.233000\"}, {\"id\": 103, \"desc\": null, \"name\": \"Tower Level\", \"alias\": \"tower_level\", \"value\": \"7\", \"created_at\": \"2022-12-29 09:47:41.279000\", \"updated_at\": \"2022-12-29 09:47:41.279000\"}, {\"id\": 104, \"desc\": null, \"name\": \"Tower Reward 1\", \"alias\": \"tower_reward1\", \"value\": \"20000\", \"created_at\": \"2022-12-29 09:47:41.326000\", \"updated_at\": \"2022-12-29 09:47:41.326000\"}, {\"id\": 105, \"desc\": null, \"name\": \"Tower Reward Type 1\", \"alias\": \"tower_reward_type1\", \"value\": \"3\", \"created_at\": \"2022-12-29 09:47:41.371000\", \"updated_at\": \"2022-12-29 09:47:41.371000\"}, {\"id\": 106, \"desc\": null, \"name\": \"Tower Reward 2\", \"alias\": \"tower_reward2\", \"value\": \"10000\", \"created_at\": \"2022-12-29 09:47:41.419000\", \"updated_at\": \"2022-12-29 09:47:41.419000\"}, {\"id\": 107, \"desc\": null, \"name\": \"Tower Reward Type 2\", \"alias\": \"tower_reward_type2\", \"value\": \"2\", \"created_at\": \"2022-12-29 09:47:41.468000\", \"updated_at\": \"2022-12-29 09:47:41.468000\"}, {\"id\": 108, \"desc\": null, \"name\": \"Latitude\", \"alias\": \"lat\", \"value\": \"10.79246800\", \"created_at\": \"2022-12-29 09:47:41.514000\", \"updated_at\": \"2022-12-29 09:47:41.514000\"}, {\"id\": 109, \"desc\": null, \"name\": \"Longitude\", \"alias\": \"lng\", \"value\": \"106.69384200\", \"created_at\": \"2022-12-29 09:47:41.560000\", \"updated_at\": \"2022-12-29 09:47:41.560000\"}], \"created_at\": \"2022-12-29 09:47:41.183000\", \"updated_at\": \"2022-12-29 09:47:41.183000\"}");
        System.out.println(resources.size());
        for (String s:
                resources) {
            JsonObject json = gson.fromJson(s, JsonObject.class);
            JsonArray jsonArray = json.getAsJsonArray("attributes");
            String lat = "";
            String lng = "";
            for (JsonElement jE:
                    jsonArray) {
                JsonObject j = jE.getAsJsonObject();
                if (j.get("alias").getAsString().equals("lat")) {
                    lat = j.get("value").getAsString();
                }
                if (j.get("alias").getAsString().equals("lng")) {
                    lng = j.get("value").getAsString();
                }
            }
            resourceLocations.add(new ResourceLocation(json.get("id").getAsString(), lat, lng));
        }
        List<ResourceTmp> arrTmp = new ArrayList<>();
        List<Integer> arrTmpNonValid = new ArrayList<>();
        String distanceStr = "";

        double RESOURCE_LIMIT = 0.08;
        System.out.println("START CALC " + Instant.now());

        List<ResourceLocation> tmpArrPassing = new ArrayList<>();
        List<List<ResourceLocation>> resourceLocationPairs = resourceLocations.stream()
                .flatMap(i -> resourceLocations.stream().parallel()
                        .filter(j -> {
                            tmpArrPassing.add(i);
                            return !i.lat.equals(j.lat) && !i.lng.equals(j.lng) && !tmpArrPassing.contains(j);
                        })
                        .map(j -> {
                            List<ResourceLocation> pairLocations = new ArrayList<>();
                            pairLocations.add(i);
                            pairLocations.add(j);
                            return pairLocations;
                        })
                )
                .collect(Collectors.toList());
        resourceLocationPairs.stream().parallel().forEach(r -> {
            String latI = r.get(0).lat;
            String lngI = r.get(0).lng;
            String latJ = r.get(1).lat;
            String lngJ = r.get(1).lng;

            Double dS = Distance.distance(Double.parseDouble(latI), Double.parseDouble(lngI), Double.parseDouble(latJ), Double.parseDouble(lngJ), Distance.Unit.K);
            Distance.setDistanceStr(dS);

            if (dS < RESOURCE_LIMIT) {
                arrTmpNonValid.add(Integer.parseInt(r.get(0).id));
            }
        });

//        for (ResourceLocation i : resourceLocations) {
//            String latI = i.lat;
//            String lngI = i.lng;
//
//            for (ResourceLocation j : resourceLocations) {
//                String latJ = j.lat;
//                String lngJ = j.lng;
//                String latIJString = Stream.of(latI, lngI, latJ, lngJ).sorted((a, b) -> {
//                    Double aD = Double.parseDouble(String.valueOf(a));
//                    Double bD = Double.parseDouble(String.valueOf(b));
//                    return aD.compareTo(bD);
//                }).collect(Collectors.joining(","));
//                List<ResourceTmp> check = U.filter(arrTmp, e -> e.ls.equals(latIJString));
//                if (latI.equals(latJ) && lngI.equals(lngJ)) {
//                    continue;
//                }
//                if (check.size() > 0) {
//                    continue;
//                }
//
//                List<ResourceLocation> l = new ArrayList<>();
//                l.add(new ResourceLocation(i.id, latI, lngI));
//                l.add(new ResourceLocation(i.id, latJ, lngJ));
//                arrTmp.add(new ResourceTmp(latIJString, l));
//
//                Double dS = Distance.distance(Double.parseDouble(latI), Double.parseDouble(lngI), Double.parseDouble(latJ), Double.parseDouble(lngJ), Distance.Unit.K);
//                distanceStr = distanceStr + dS + "\n";
//
//                if (dS < RESOURCE_LIMIT) {
//                    arrTmpNonValid.add(Integer.parseInt(i.id));
//                }
//            }
//        }
        System.out.println("START WRITE " + Instant.now());

        BufferedWriter writer = new BufferedWriter(new FileWriter("log-distance-stream.txt"));
        writer.write(Distance.distanceStr);

        writer.close();
        System.out.println(arrTmpNonValid.size());
        for (int e : arrTmpNonValid) {
            String rrUpdate = "UPDATE resources r set r.status='UNACTIVATED' where r.id = " + e;
            System.out.println(rrUpdate);
        }
        System.out.println("DONE " + Instant.now());
    }
}
