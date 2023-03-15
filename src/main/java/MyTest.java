import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

interface MyTestLD {
    public String changeStr(
            int numberQ,
            String queryInsertR,
            String queryInsertA
    );
}

public class MyTest {

    public static void main(String[] args) throws SQLException {
//        float random = U.random(75, 120) / 100f;
//        System.out.println(random);


//        Connection conn = DatabaseConnection.getConnection();
//        String query = "select * from locations order by rand();";
//
//        // create the java statement
//        Statement st = conn.createStatement();
//
//        // execute the query, and get a java resultset
//        ResultSet rs = st.executeQuery(query);
//
//        List<MyApp.Location> locations = new ArrayList<>();
//
//        // iterate through the java resultset
//        while (rs.next())
//        {
//            String lat = rs.getString("lat");
//            String lng = rs.getString("lng");
//
//            // print the results
//            locations.add(new MyApp.Location(lat, lng));
//        }
//        st.close();

//        List<List<MyApp.Location>> listLocations = Lists.partition(locations, 5);

//        for (MyApp.Location l:
//             locations) {
//            System.out.println(l);
//        }

//        List<Integer> a = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13);
//        List<Integer> b = Arrays.asList(5, 6, 7, 8);

//        List<List<Integer>> listLocations = Lists.partition(a, 5);
//
//        System.out.println(Math.ceil(1/2f));

//        int i = 0;
//        for (Integer l:
//             a) {
//            System.out.println(i);
//            i++;
//        }
//        for (Integer l:
//                b) {
//            System.out.println(i);
//            i++;
//        }
//        Instant instant = Instant.now();
//        LocalDateTime ldt1 = LocalDateTime.ofInstant(instant.plus(0, ChronoUnit.DAYS), ZoneId.of("+0"));
//
//        System.out.println(ldt1);
//        ldt1 = ldt1
//                .withHour(23)
//                .withMinute(59)
//                .withSecond(59);
//
//        Instant result = ldt1.atZone(ZoneId.systemDefault()).toInstant();
//        System.out.println(result);
//        System.out.println(ldt1);
//        System.out.println("".toString());
//
//        System.out.println(String.valueOf(U.sample(Arrays.asList(MyApp.TowerType.NORMAL, MyApp.TowerType.ADVANCE, MyApp.TowerType.SPECIAL)).type));

        final int i = 0;
        final MyTestLD str = new MyTestLD() {
            @Override
            public String changeStr(int numberQ, String queryInsertR, String queryInsertA) {
                return queryInsertA;
            }
        };
        String s = "Haa" + str.changeStr(1, "", "avv");
        String ll = Stream.of("5", "1", "2", "3", "4").sorted((a, b) -> {
                    Double aD = Double.parseDouble(a);
                    Double bD = Double.parseDouble(b);
                    return aD.compareTo(bD);
                }).collect(Collectors.joining(","));
//        0.0512122039173519
//        0.051209740053713676 Km
        System.out.println(MyJSON.Distance.distance(21.0157985687256, 105.82088470459, 21.01612984, 105.82122747, MyJSON.Distance.Unit.K) + " Km\n");
        System.out.println(ll);
        System.out.println(s);
        System.out.println(i);
        List<Integer> check = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).stream().filter(e -> e % 2 == 0).collect(Collectors.toList());
        System.out.println(check);
    }
}
