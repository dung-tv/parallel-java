import com.github.underscore.U;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.ToString;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class MyApp {

    enum MonsterType {
        DEMON(1),
        ANGEL(2),
        BEAST(3),
        GOD(4),
        DEMIGOD(5),
        DRAGON(6);

        int type;

        MonsterType(int type) {
            this.type = type;
        }
    }

    enum MonsterElementalType {
        FIRE(1),
        WATER(2),
        EARTH(3),
        WIND(4),
        LIGHTNING(5),
        VOID(6);

        int type;

        MonsterElementalType(int type) {
            this.type = type;
        }
    }

    enum TowerType {
        NORMAL(1),
        ADVANCE(2),
        SPECIAL(3);

        int type;

        TowerType(int type) {
            this.type = type;
        }
    }

    enum RewardType {
        GOLD(1),
        EXP(2),
        GEM1(3);

        int type;

        RewardType(int type) {
            this.type = type;
        }
    }

    enum MineType {
        SOLO(1),
        FACTION(2),
        GUILD(3);

        int type;

        MineType(int type) {
            this.type = type;
        }
    }

    enum ResourceType {
        TOWER(1),
        MINE(2),
        CHECKPOINT(3),
        MONSTER(4);

        int type;

        ResourceType(int type) {
            this.type = type;
        }
    }

    enum ResourceStatus {
        ACTIVATED(1),
        UNACTIVATED(2);

        int status;

        ResourceStatus(int status) {
            this.status = status;
        }
    }

    @AllArgsConstructor
    @ToString
    static
    class Attribute {
        String hp;
        String min_atk;
        String max_atk;
        String min_matk;
        String max_matk;
        String elemental_dmg;
        String critical_chance;
        String accuracy;
        String dodge;
        String defense;
        String magic_resist;
        String fire_resist;
        String water_resist;
        String earth_resist;
        String wind_resist;
        String lightning_resist;
        String speed;
    }

    @AllArgsConstructor
    @ToString
    static
    class Monster {
        String monster_alias;
        String name;
        String exp;
        String level;
        String type;
        String rarity;
        String rate;
        String elemental_type;
    }

    static class MyQuery {
        static String queryInsertR = "\ninsert into resources (name, type, i_tmp) values";
        static String queryInsertA = "\ninsert into attributes (name, alias, value, i_tmp, j_tmp) values";
        static String queryInsertRA = "\ninsert into resource_attribute (resource_id, attribute_id)"
                + "\nSELECT r.id as resource_id, a.id as attribute_id from resources r left join attributes a on r.i_tmp = a.i_tmp and isnull(a.j_tmp);";
        static String queryInsertM = "\ninsert into monsters (monster_alias, type, rarity, level, exp, rate, elemental_type, name, i_tmp, j_tmp) values";
        static String queryInsertMA = "\ninsert into monster_attribute (monster_id, attribute_id)"
                + "\nSELECT m.id as monster_id, a.id as attribute_id from monsters m left join attributes a on m.j_tmp = a.j_tmp and a.i_tmp = m.i_tmp;";
        static String queryInsertRM = "\ninsert into resource_monster (resource_id, monster_id)"
                + "\nSELECT r.id as resource_id, m.id as monster_id from resources r left join monsters m on r.i_tmp = m.i_tmp where m.j_tmp is not null;";
        static void addR(String s) {
            queryInsertR += s;
        }

        static void addA(String s) {
            queryInsertA += s;
        }

        static void addM(String s) {
            queryInsertM += s;
        }

        static String getQuery() {
            queryInsertR = queryInsertR.replaceAll(".$", ";");
            queryInsertA = queryInsertA.replaceAll(".$", ";");
            queryInsertM = queryInsertM.replaceAll(".$", ";");
            return queryInsertR + queryInsertA + queryInsertM + queryInsertRA + queryInsertMA + queryInsertRM;
        }
    }


    public static void createResourceTower(
            String towerType,
            String towerLevel,
            String towerReward1,
            String towerRewardType1,
            String towerReward2,
            String towerRewardType2,
            String lat,
            String lng,
            int numberQ
    ) {

        String queryInsertR = ""
                + "\n('Tower', 'TOWER', '" + numberQ + "'),";
        String queryInsertA = ""
                + "\n('Tower Type', 'tower_type', '" + towerType + "', '" + numberQ + "', null),"
                + "\n('Tower Level', 'tower_level', '" + towerLevel + "', '" + numberQ + "', null),"
                + "\n('Tower Reward 1', 'tower_reward1', '" + towerReward1 + "', '" + numberQ + "', null),"
                + "\n('Tower Reward Type 1', 'tower_reward_type1', '" + towerRewardType1 + "', '" + numberQ + "', null),"
                + "\n('Tower Reward 2', 'tower_reward2', '" + towerReward2 + "', '" + numberQ + "', null),"
                + "\n('Tower Reward Type 2', 'tower_reward_type2', '" + towerRewardType2 + "', '" + numberQ + "', null),"
                + "\n('Latitude', 'lat', '" + lat + "', '" + numberQ + "', null),"
                + "\n('Longitude', 'lng', '" + lng + "', '" + numberQ + "', null),";

        String queryInsertM = "";

        for (int j = 0; j < U.random(1, 5); j++) {
            float rateMonster = U.random(75, 120) / 100f;
            String monsterAlias = U.sample(U.range(1, 6)).toString();
            Map<String, Monster> monsters = new HashMap<>();
            monsters.put("1", new Monster(
                    monsterAlias,
                    "Doggo",
                    "0",
                    U.sample(U.range(1, 10)).toString(),
                    MonsterType.BEAST.toString(),
                    "3",
                    String.valueOf(rateMonster),
                    MonsterElementalType.FIRE.toString()
            ));
            monsters.put("2", new Monster(
                    monsterAlias,
                    "Transformed Molly",
                    "0",
                    U.sample(U.range(1, 10)).toString(),
                    MonsterType.BEAST.toString(),
                    "3",
                    String.valueOf(rateMonster),
                    MonsterElementalType.EARTH.toString()
            ));
            monsters.put("3", new Monster(
                    monsterAlias,
                    "Metalic Faceless",
                    "0",
                    U.sample(U.range(1, 10)).toString(),
                    MonsterType.BEAST.toString(),
                    "3",
                    String.valueOf(rateMonster),
                    MonsterElementalType.EARTH.toString()
            ));
            monsters.put("4", new Monster(
                    monsterAlias,
                    "Winged Poisonous",
                    "0",
                    U.sample(U.range(1, 10)).toString(),
                    MonsterType.BEAST.toString(),
                    "4",
                    String.valueOf(rateMonster),
                    MonsterElementalType.WIND.toString()
            ));
            monsters.put("5", new Monster(
                    monsterAlias,
                    "Faceless",
                    "0",
                    U.sample(U.range(1, 10)).toString(),
                    MonsterType.BEAST.toString(),
                    "3",
                    String.valueOf(rateMonster),
                    MonsterElementalType.LIGHTNING.toString()
            ));
            monsters.put("6", new Monster(
                    monsterAlias,
                    "Winged Cretaceous",
                    "0",
                    U.sample(U.range(1, 10)).toString(),
                    MonsterType.BEAST.toString(),
                    "3",
                    String.valueOf(rateMonster),
                    MonsterElementalType.EARTH.toString()
            ));

            Map<String, Attribute> attributes = new HashMap<>();
            attributes.put("1", new Attribute(
                    String.valueOf(Math.ceil(1038 * rateMonster)),
                    String.valueOf(Math.ceil(107 * rateMonster)),
                    String.valueOf(Math.ceil(127 * rateMonster)),
                    String.valueOf(Math.ceil(107 * rateMonster)),
                    String.valueOf(Math.ceil(127 * rateMonster)),
                    String.valueOf(Math.ceil(10 * rateMonster)),
                    String.valueOf(Math.ceil(5 * rateMonster)),
                    String.valueOf(Math.ceil(0 * rateMonster)),
                    String.valueOf(Math.ceil(20 * rateMonster)),
                    String.valueOf(Math.ceil(40 * rateMonster)),
                    String.valueOf(Math.ceil(10 * rateMonster)),
                    String.valueOf(Math.ceil(0 * rateMonster)),
                    String.valueOf(Math.ceil(0 * rateMonster)),
                    String.valueOf(Math.ceil(0 * rateMonster)),
                    String.valueOf(Math.ceil(0 * rateMonster)),
                    String.valueOf(Math.ceil(0 * rateMonster)),
                    String.valueOf(Math.ceil(10 * rateMonster))
            ));
            attributes.put("2", new Attribute(
                    String.valueOf(Math.ceil(1328 * rateMonster)),
                    String.valueOf(Math.ceil(139 * rateMonster)),
                    String.valueOf(Math.ceil(164 * rateMonster)),
                    String.valueOf(Math.ceil(139 * rateMonster)),
                    String.valueOf(Math.ceil(164 * rateMonster)),
                    String.valueOf(Math.ceil(10 * rateMonster)),
                    String.valueOf(Math.ceil(5 * rateMonster)),
                    String.valueOf(Math.ceil(0 * rateMonster)),
                    String.valueOf(Math.ceil(20 * rateMonster)),
                    String.valueOf(Math.ceil(35 * rateMonster)),
                    String.valueOf(Math.ceil(12 * rateMonster)),
                    String.valueOf(Math.ceil(0 * rateMonster)),
                    String.valueOf(Math.ceil(0 * rateMonster)),
                    String.valueOf(Math.ceil(0 * rateMonster)),
                    String.valueOf(Math.ceil(0 * rateMonster)),
                    String.valueOf(Math.ceil(0 * rateMonster)),
                    String.valueOf(Math.ceil(11 * rateMonster))
            ));
            attributes.put("3", new Attribute(
                    String.valueOf(Math.ceil(1742 * rateMonster)),
                    String.valueOf(Math.ceil(184 * rateMonster)),
                    String.valueOf(Math.ceil(217 * rateMonster)),
                    String.valueOf(Math.ceil(184 * rateMonster)),
                    String.valueOf(Math.ceil(217 * rateMonster)),
                    String.valueOf(Math.ceil(10 * rateMonster)),
                    String.valueOf(Math.ceil(5 * rateMonster)),
                    String.valueOf(Math.ceil(0 * rateMonster)),
                    String.valueOf(Math.ceil(20 * rateMonster)),
                    String.valueOf(Math.ceil(10 * rateMonster)),
                    String.valueOf(Math.ceil(40 * rateMonster)),
                    String.valueOf(Math.ceil(0 * rateMonster)),
                    String.valueOf(Math.ceil(0 * rateMonster)),
                    String.valueOf(Math.ceil(0 * rateMonster)),
                    String.valueOf(Math.ceil(0 * rateMonster)),
                    String.valueOf(Math.ceil(0 * rateMonster)),
                    String.valueOf(Math.ceil(12 * rateMonster))
            ));
            attributes.put("4", new Attribute(
                    String.valueOf(Math.ceil(1020 * rateMonster)),
                    String.valueOf(Math.ceil(90 * rateMonster)),
                    String.valueOf(Math.ceil(106 * rateMonster)),
                    String.valueOf(Math.ceil(90 * rateMonster)),
                    String.valueOf(Math.ceil(106 * rateMonster)),
                    String.valueOf(Math.ceil(10 * rateMonster)),
                    String.valueOf(Math.ceil(5 * rateMonster)),
                    String.valueOf(Math.ceil(0 * rateMonster)),
                    String.valueOf(Math.ceil(20 * rateMonster)),
                    String.valueOf(Math.ceil(15 * rateMonster)),
                    String.valueOf(Math.ceil(20 * rateMonster)),
                    String.valueOf(Math.ceil(0 * rateMonster)),
                    String.valueOf(Math.ceil(0 * rateMonster)),
                    String.valueOf(Math.ceil(0 * rateMonster)),
                    String.valueOf(Math.ceil(0 * rateMonster)),
                    String.valueOf(Math.ceil(0 * rateMonster)),
                    String.valueOf(Math.ceil(13 * rateMonster))
            ));
            attributes.put("5", new Attribute(
                    String.valueOf(Math.ceil(1390 * rateMonster)),
                    String.valueOf(Math.ceil(122 * rateMonster)),
                    String.valueOf(Math.ceil(144 * rateMonster)),
                    String.valueOf(Math.ceil(122 * rateMonster)),
                    String.valueOf(Math.ceil(144 * rateMonster)),
                    String.valueOf(Math.ceil(10 * rateMonster)),
                    String.valueOf(Math.ceil(5 * rateMonster)),
                    String.valueOf(Math.ceil(0 * rateMonster)),
                    String.valueOf(Math.ceil(20 * rateMonster)),
                    String.valueOf(Math.ceil(0 * rateMonster)),
                    String.valueOf(Math.ceil(50 * rateMonster)),
                    String.valueOf(Math.ceil(0 * rateMonster)),
                    String.valueOf(Math.ceil(0 * rateMonster)),
                    String.valueOf(Math.ceil(0 * rateMonster)),
                    String.valueOf(Math.ceil(0 * rateMonster)),
                    String.valueOf(Math.ceil(0 * rateMonster)),
                    String.valueOf(Math.ceil(15 * rateMonster))
            ));
            attributes.put("6", new Attribute(
                    String.valueOf(Math.ceil(1962 * rateMonster)),
                    String.valueOf(Math.ceil(168 * rateMonster)),
                    String.valueOf(Math.ceil(198 * rateMonster)),
                    String.valueOf(Math.ceil(168 * rateMonster)),
                    String.valueOf(Math.ceil(198 * rateMonster)),
                    String.valueOf(Math.ceil(10 * rateMonster)),
                    String.valueOf(Math.ceil(5 * rateMonster)),
                    String.valueOf(Math.ceil(0 * rateMonster)),
                    String.valueOf(Math.ceil(25 * rateMonster)),
                    String.valueOf(Math.ceil(55 * rateMonster)),
                    String.valueOf(Math.ceil(20 * rateMonster)),
                    String.valueOf(Math.ceil(0 * rateMonster)),
                    String.valueOf(Math.ceil(0 * rateMonster)),
                    String.valueOf(Math.ceil(0 * rateMonster)),
                    String.valueOf(Math.ceil(0 * rateMonster)),
                    String.valueOf(Math.ceil(0 * rateMonster)),
                    String.valueOf(Math.ceil(18 * rateMonster))
            ));
            Monster monster = monsters.get(monsterAlias);
            Attribute attribute = attributes.get(monsterAlias);
            queryInsertM += ""
                    + "\n('" + monster.monster_alias + "', '" + monster.type + "', '" + monster.rarity + "', '"
                    + monster.level + "', '" + monster.exp + "', '" + monster.rate + "', '" + monster.elemental_type + "', '" + monster.name + "', '"
                    + numberQ + "', '" + j + "'),";
            queryInsertA += ""
                    + "\n('HP', 'hp', '" + (attribute.hp) + "', '" + numberQ + "', '" + j + "'),"
                    + "\n('Min ATK', 'min_atk', '" + (attribute.min_atk) + "', '" + numberQ + "', '" + j + "'),"
                    + "\n('Max ATK', 'max_atk', '" + (attribute.max_atk) + "', '" + numberQ + "', '" + j + "'),"
                    + "\n('Min MATK', 'min_matk', '" + (attribute.min_matk) + "', '" + numberQ + "', '" + j + "'),"
                    + "\n('Max MATK', 'max_matk', '" + (attribute.max_matk) + "', '" + numberQ + "', '" + j + "'),"
                    + "\n('Elemental DMG', 'elemental_dmg', '" + (attribute.elemental_dmg) + "', '" + numberQ + "', '" + j + "'),"
                    + "\n('Critical Chance', 'critical_chance', '" + (attribute.critical_chance) + "', '" + numberQ + "', '" + j + "'),"
                    + "\n('Accuracy', 'accuracy', '" + (attribute.accuracy) + "', '" + numberQ + "', '" + j + "'),"
                    + "\n('Dodge', 'dodge', '" + (attribute.dodge) + "', '" + numberQ + "', '" + j + "'),"
                    + "\n('Defense', 'defense', '" + (attribute.defense) + "', '" + numberQ + "', '" + j + "'),"
                    + "\n('Magic Resist', 'magic_resist', '" + (attribute.magic_resist) + "', '" + numberQ + "', '" + j + "'),"
                    + "\n('Fire Resist', 'fire_resist', '" + (attribute.fire_resist) + "', '" + numberQ + "', '" + j + "'),"
                    + "\n('Water Resist', 'water_resist', '" + (attribute.water_resist) + "', '" + numberQ + "', '" + j + "'),"
                    + "\n('Earth Resist', 'earth_resist', '" + (attribute.earth_resist) + "', '" + numberQ + "', '" + j + "'),"
                    + "\n('Wind Resist', 'wind_resist', '" + (attribute.wind_resist) + "', '" + numberQ + "', '" + j + "'),"
                    + "\n('Lightning Resist', 'lightning_resist', '" + (attribute.lightning_resist) + "', '" + numberQ + "', '" + j + "'),"
                    + "\n('Speed', 'speed', '" + (attribute.speed) + "', '" + numberQ + "', '" + j + "'),";
        }
        MyQuery.addR(queryInsertR);
        MyQuery.addA(queryInsertA);
        MyQuery.addM(queryInsertM);
    }


    public static void createResourceMine(
            String mineID,
            String mineType,
            String mineLevel,
            String claimTime,
            String protectEndTime,
            String rewardType,
            String rewardAmount,
            String maximumReward,
            String lat,
            String lng,
            int numberQ
    ) {

        String queryInsertR = ""
                + "\n('Mine', 'MINE', '" + numberQ + "'),";
        String queryInsertA = ""
                + "\n('MineID', 'mine_id', '" + mineID + "', '" + numberQ + "', null),"
                + "\n('Mine Type', 'mine_type', '" + mineType + "', '" + numberQ + "', null),"
                + "\n('Mine Level', 'mine_level', '" + mineLevel + "', '" + numberQ + "', null),"
                + "\n('Mine Claim Time', 'mine_claim_time', '" + claimTime + "', '" + numberQ + "', null),"
                + "\n('Mine Protect End Time', 'mine_protect_end_time', '" + protectEndTime + "', '" + numberQ + "', null),"
                + "\n('Mine Reward Type', 'mine_reward_type', '" + rewardType + "', '" + numberQ + "', null),"
                + "\n('Mine Reward Amount', 'mine_reward_amount', '" + rewardAmount + "', '" + numberQ + "', null),"
                + "\n('Mine Maximum Reward', 'mine_maximum_reward', '" + maximumReward + "', '" + numberQ + "', null),"
                + "\n('Latitude', 'lat', '" + lat + "', '" + numberQ + "', null),"
                + "\n('Longitude', 'lng', '" + lng + "', '" + numberQ + "', null),";
        MyQuery.addR(queryInsertR);
        MyQuery.addA(queryInsertA);
    }


    public static void createResourceCheckPoint(
            String name,
            String rewardType1,
            String reward1,
            String rewardType2,
            String reward2,
            String rewardType3,
            String reward3,
            String lat,
            String lng,
            int numberQ
    ) {

        String queryInsertR = ""
                + "\n('Check Point', 'CHECKPOINT', '" + numberQ + "'),";
        String queryInsertA = ""
                + "\n('Name', 'name', '" + name + "', '" + numberQ + "', null),"
                + "\n('Check Point Reward Type 1', 'check_point_reward_type1', '" + rewardType1 + "', '" + numberQ + "', null),"
                + "\n('Check Point Reward 1', 'reward1', '" + reward1 + "', '" + numberQ + "', null),"
                + "\n('Check Point Reward Type 2', 'check_point_reward_type2', '" + rewardType2 + "', '" + numberQ + "', null),"
                + "\n('Check Point Reward 2', 'check_point_reward2', '" + reward2 + "', '" + numberQ + "', null),"
                + "\n('Check Point Reward Type 3', 'check_point_reward_type3', '" + rewardType3 + "', '" + numberQ + "', null),"
                + "\n('Check Point Reward 3', 'check_point_reward3', '" + reward3 + "', '" + numberQ + "', null),"
                + "\n('Latitude', 'lat', '" + lat + "', '" + numberQ + "', null),"
                + "\n('Longitude', 'lng', '" + lng + "', '" + numberQ + "', null),";
        MyQuery.addR(queryInsertR);
        MyQuery.addA(queryInsertA);
    }

    @AllArgsConstructor
    @ToString
    static
    class Location {
        String lat;
        String lng;
    }

    public static void main(String[] args) throws SQLException, IOException {
        System.out.println("START " + Instant.now());

        Connection conn = DatabaseConnection.getConnection();
        List<Location> locations = new ArrayList<>();

        String query = "select * from locations order by rand();";

        // create the java statement
        Statement st = conn.createStatement();

        // execute the query, and get a java resultset
        ResultSet rs = st.executeQuery(query);

        // iterate through the java resultset
        while (rs.next())
        {
            String lat = rs.getString("lat");
            String lng = rs.getString("lng");

            // print the results
            locations.add(new MyApp.Location(lat, lng));
        }
        st.close();

        int partitionSize = (int)Math.ceil(locations.size() / 5f);

        System.out.println("QUERY DONE " + partitionSize + " " + Instant.now());

        List<List<Location>> listLocations = Lists.partition(locations, partitionSize);

        List<Location> oneArray = listLocations.get(0);

        int i = 0;
        for (Location l : oneArray) {
            createResourceTower(
                    String.valueOf(U.sample(Arrays.asList(TowerType.NORMAL, TowerType.ADVANCE, TowerType.SPECIAL)).type),
                    U.sample(U.range(1,15)).toString(),
                    U.sample(Arrays.asList(20000, 10000, 100000, 200000, 500000, 0)).toString(),
                    String.valueOf(U.sample(Arrays.asList(RewardType.GOLD, RewardType.EXP, RewardType.GEM1)).type),
                    U.sample(Arrays.asList(20000, 10000, 100000, 200000, 500000, 0)).toString(),
                    String.valueOf(U.sample(Arrays.asList(RewardType.GOLD, RewardType.EXP, RewardType.GEM1)).type),
                    l.lat,
                    l.lng,
                    i
            );
            i++;
        }

        List<Location> twoArray = listLocations.get(1);
        Instant instant = Instant.now();
        LocalDateTime ldt1 = LocalDateTime.ofInstant(instant.plus(0, ChronoUnit.DAYS), ZoneId.of("+0"));

        ldt1 = ldt1
                .withHour(23)
                .withMinute(59)
                .withSecond(59);

        Instant result = ldt1.atZone(ZoneId.systemDefault()).toInstant();

        for (Location l : twoArray) {
            int maxReward = U.sample(Arrays.asList(100000, 400, 100000, 1000, 1000000));
            createResourceMine(
                    U.sample(Arrays.asList(1, 2, 3, 20, 21)).toString(),
                    String.valueOf(U.sample(Arrays.asList(MineType.FACTION, MineType.GUILD, MineType.SOLO)).type),
                    U.sample(Arrays.asList(10, 20, 15, 40, 90)).toString(),
                    U.sample(Arrays.asList(ldt1, "")).toString(),
                    String.valueOf(U.sample(Arrays.asList(RewardType.GOLD, RewardType.GEM1)).type),
                    U.sample(U.range(10, maxReward)).toString(),
                    String.valueOf(maxReward),
                    U.sample(Arrays.asList(0, 60000)).toString(),
                    l.lat,
                    l.lng,
                    i
            );
            i++;
        }

        List<Location> checkPoints = new ArrayList<>();
        checkPoints.addAll(listLocations.get(2));
        checkPoints.addAll(listLocations.get(3));
        checkPoints.addAll(listLocations.get(4));

        for (Location l : checkPoints) {
            createResourceCheckPoint(
                    "Check point " + i,
                    U.sample(Arrays.asList(20000, 10000, 100000, 200000, 500000, 0)).toString(),
                    String.valueOf(U.sample(Arrays.asList(RewardType.GOLD, RewardType.EXP, RewardType.GEM1)).type),
                    U.sample(Arrays.asList(20000, 10000, 100000, 200000, 500000, 0)).toString(),
                    String.valueOf(U.sample(Arrays.asList(RewardType.GOLD, RewardType.EXP, RewardType.GEM1)).type),
                    U.sample(Arrays.asList(20000, 10000, 100000, 200000, 500000, 0)).toString(),
                    String.valueOf(U.sample(Arrays.asList(RewardType.GOLD, RewardType.EXP, RewardType.GEM1)).type),
                    l.lat,
                    l.lng,
                    i
            );
            i++;
        }
        System.out.println("START WRITE " + Instant.now());
        String queryInsert = MyQuery.getQuery();

        BufferedWriter writer = new BufferedWriter(new FileWriter("seeder.sql"));
        writer.write(queryInsert);

        writer.close();
        System.out.println("STOP " + Instant.now());
    }
}
