package co.vargoi.clan.database.mysql;

import co.vargoi.clan.VargoiClan;
import me.aglerr.lazylibs.libs.Common;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class SQLDatabaseInitializer {

    private static final SQLDatabaseInitializer instance = new SQLDatabaseInitializer();

    public static SQLDatabaseInitializer getInstance(){
        return instance;
    }

    private VargoiClan plugin;

    public void init(VargoiClan plugin){
        this.plugin = plugin;

        if(!SQLHelper.createConnection(plugin)){
            Common.log(ChatColor.RED, "Couldn't connect to the database", "Make sure all information is correct.");
            Bukkit.getPluginManager().disablePlugin(plugin);
            return;
        }

        createClansTable();
        createStatsTable();
        createBedwarsTable();
        createPlayersTable();
    }

    private void createClansTable(){
        SQLHelper.executeUpdate("CREATE TABLE IF NOT EXISTS " + SQLHelper.CLAN_TABLE + " (" +
                "uuid VARCHAR(36) PRIMARY KEY, " +
                "name TEXT, " +
                "description TEXT, " +
                "tag TEXT, " +
                "colorTag TEXT, " +
                "owner TEXT, " +
                "maxMembers INTEGER, " +
                "createdAt TEXT" +
                ");"
        );
    }

    private void createStatsTable(){
        SQLHelper.executeUpdate("CREATE TABLE IF NOT EXISTS " + SQLHelper.STATS_TABLE + " (" +
                "uuid VARCHAR(36) PRIMARY KEY, " +
                "balance INTEGER, " +
                "points INTEGER" +
                ");"
        );
    }

    private void createBedwarsTable(){
        SQLHelper.executeUpdate("CREATE TABLE IF NOT EXISTS " + SQLHelper.BEDWARS_TABLE + " (" +
                "uuid VARCHAR(36) PRIMARY KEY, " +
                "wins INTEGER, " +
                "losses INTEGER, " +
                "kills INTEGER, " +
                "deaths INTEGER, " +
                "bed INTEGER, " +
                "finalKills INTEGER" +
                ");"
        );
    }

    private void createPlayersTable(){
        SQLHelper.executeUpdate("CREATE TABLE IF NOT EXISTS " + SQLHelper.PLAYER_TABLE + " (" +
                "uuid VARCHAR(36) PRIMARY KEY, " +
                "name TEXT, " +
                "rank TEXT" +
                ");"
        );
    }

}
