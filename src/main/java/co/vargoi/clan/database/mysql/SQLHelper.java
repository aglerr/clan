package co.vargoi.clan.database.mysql;

import co.vargoi.clan.VargoiClan;
import co.vargoi.clan.clan.objects.Clan;
import co.vargoi.clan.clan.objects.ClanBedwars;
import co.vargoi.clan.clan.objects.ClanPlayer;
import co.vargoi.clan.clan.objects.ClanStats;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class SQLHelper {

    public static String CLAN_TABLE = "vargoiclan_clan";
    public static String STATS_TABLE = "vargoiclan_stats";
    public static String BEDWARS_TABLE = "vargoiclan_bedwars";
    public static String PLAYER_TABLE = "vargoiclan_player";

    private static SQLSession globalSession;

    public static boolean createConnection(VargoiClan plugin){
        globalSession = new SQLSession(plugin);
        return globalSession.createConnection();
    }

    public static void executeUpdate(String statement){
        globalSession.executeUpdate(statement);
    }

    public static void executeUpdate(String statement, Consumer<SQLException> onFailure){
        globalSession.executeUpdate(statement, onFailure);
    }

    public static void executeQuery(String statement, QueryConsumer<ResultSet> callback){
        globalSession.executeQuery(statement, callback::accept);
    }

    public static void executeQuery(String statement, QueryConsumer<ResultSet> callback, Consumer<SQLException> onFailure){
        globalSession.executeQuery(statement, callback::accept, onFailure);
    }

    public static void buildStatement(String query, QueryConsumer<PreparedStatement> consumer, Consumer<SQLException> onFailure){
        globalSession.buildStatement(query, consumer::accept, onFailure);
    }

    public static boolean doesConditionExist(String statement){
        AtomicBoolean result = new AtomicBoolean(false);
        executeQuery(statement, resultSet -> result.set(resultSet.next()));
        return result.get();
    }

    public static void save(ClanStats clanStats){
        String condition = "SELECT uuid FROM `" + STATS_TABLE + "` WHERE " +
                "uuid=\"" + clanStats.getClanUUID() + "\";";

        if(doesConditionExist(condition)){
            executeUpdate(
                    "UPDATE `{stats_table}` SET " +
                            "balance=" + clanStats.getBalance() + ", " +
                            "points=" + clanStats.getPoints() + " " +
                            "WHERE uuid=\"" + clanStats.getClanUUID() + "\";"
            );
        } else {
            executeUpdate("INSERT INTO `{stats_table}` " +
                    "VALUES (" +
                    "\"" + clanStats.getClanUUID() + "\", " +
                    clanStats.getBalance() + ", " +
                    clanStats.getPoints() + ");"
            );
        }
    }

    public static void save(ClanPlayer clanPlayer){
        String condition = "SELECT name FROM `" + PLAYER_TABLE + "` WHERE " +
                "name=\"" + clanPlayer.getName() + "\";";

        String rank = clanPlayer.getRank() == null ? null : clanPlayer.getRank().name();
        if(doesConditionExist(condition)){
            executeUpdate("UPDATE `{player_table}` SET " +
                    "uuid=\"" + clanPlayer.getClanUUID() + "\", " +
                    "rank=\"" + rank + "\" " +
                    "WHERE name=\"" + clanPlayer.getName() + "\";"
            );
        } else {
            executeUpdate("INSERT INTO `{player_table}` VALUES (" +
                    "\"" + clanPlayer.getClanUUID() + "\", " +
                    "\"" + clanPlayer.getName() + "\", " +
                    "\"" + rank + "\");"
            );
        }
    }

    public static void save(ClanBedwars clanBedwars){
        String condition = "SELECT uuid FROM `" + BEDWARS_TABLE + "` WHERE " +
                "uuid=\"" + clanBedwars.getClanUUID() + "\";";

        if(doesConditionExist(condition)){
            executeUpdate("UPDATE `{bedwars_table}` SET " +
                    "wins=" + clanBedwars.getWins() + ", " +
                    "losses=" + clanBedwars.getLosses() + ", " +
                    "kills=" + clanBedwars.getKills() + ", " +
                    "deaths=" + clanBedwars.getDeaths() + ", " +
                    "bed=" + clanBedwars.getBedsDestroyed() + ", " +
                    "finalKills=" + clanBedwars.getFinalKills() + " " +
                    "WHERE uuid=\"" + clanBedwars.getClanUUID() + "\";"
            );
        } else {
            executeUpdate("INSERT INTO `{bedwars_table}` VALUES (" +
                    "\"" + clanBedwars.getClanUUID() + "\", " +
                    clanBedwars.getWins() + ", " +
                    clanBedwars.getLosses() + ", " +
                    clanBedwars.getKills() + ", " +
                    clanBedwars.getDeaths() + ", " +
                    clanBedwars.getBedsDestroyed() + ", " +
                    clanBedwars.getFinalKills() + ");"
            );
        }
    }

    public static void save(Clan clan){
        String condition = "SELECT uuid FROM `" + CLAN_TABLE + "` WHERE " +
                "uuid=\"" + clan.getClanUUID() + "\";";

        if(doesConditionExist(condition)){
            executeUpdate("UPDATE `{clan_table}` SET " +
                    "name=\"" + clan.getName() + "\", " +
                    "description=\"" + clan.getDescription() + "\", " +
                    "tag=\"" + clan.getTag() + "\", " +
                    "colorTag=\"" + clan.getColorTag() + "\", " +
                    "owner=\"" + clan.getOwner() + "\", " +
                    "maxMembers=" + clan.getMaxMembers() + ", " +
                    "createdAt=\"" + clan.getCreatedAt().format(VargoiClan.TIME_FORMAT) + "\" " +
                    "WHERE uuid=\"" + clan.getClanUUID() + "\";"
            );
        } else {
            executeUpdate("INSERT INTO `{clan_table}` VALUES (" +
                    "\"" + clan.getClanUUID() + "\", " +
                    "\"" + clan.getName() + "\", " +
                    "\"" + clan.getDescription() + "\", " +
                    "\"" + clan.getTag() + "\", " +
                    "\"" + clan.getColorTag() + "\", " +
                    "\"" + clan.getOwner() + "\", " +
                    clan.getMaxMembers() + ", " +
                    "\"" + clan.getCreatedAt().format(VargoiClan.TIME_FORMAT) + "\");"
            );
        }
    }

    public static void close(){
        globalSession.close();
    }

    public interface QueryConsumer<T>{

        void accept(T value) throws SQLException;

    }

}
