package co.vargoi.clan.database.mysql;

import co.vargoi.clan.VargoiClan;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.aglerr.lazylibs.libs.Common;
import org.bukkit.ChatColor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Consumer;

public class SQLSession {

    private final VargoiClan plugin;

    private HikariDataSource dataSource;

    public SQLSession(VargoiClan plugin){
        this.plugin = plugin;
    }

    public boolean createConnection(){
        try{
            Common.log(ChatColor.RESET, "Trying to connect to MySQL database...");

            HikariConfig config = new HikariConfig();
            config.setConnectionTestQuery("SELECT 1");
            config.setPoolName("VargoiClan Pool");

            config.setDriverClassName("com.mysql.jdbc.Driver");

            String address = "localhost";
            String dbName = "vargoi";
            String user = "root";
            String password = "";
            int port = 3306;
            int waitTimeout = 600000;
            int maxLifetime = 1800000;

            boolean useSSL = true;
            boolean publicKeyRetrieval = true;

            config.setJdbcUrl("jdbc:mysql://" + address + ":" + port + "/" + dbName + "?useSSL=" + useSSL);
            config.setJdbcUrl(String.format("jdbc:mysql://%s:%d/%s?useSSL=%b&allowPublicKeyRetrieval=%b",
                    address, port, dbName, useSSL, publicKeyRetrieval));
            config.setUsername(user);
            config.setPassword(password);
            config.setMinimumIdle(5);
            config.setMaximumPoolSize(50);
            config.setConnectionTimeout(10000);
            config.setIdleTimeout(waitTimeout);
            config.setMaxLifetime(maxLifetime);
            config.addDataSourceProperty("characterEncoding", "utf8");
            config.addDataSourceProperty("useUnicode", true);

            dataSource = new HikariDataSource(config);

            Common.log(ChatColor.RESET, "Successfully established connection with MySQL database!");
            return true;
        } catch (Exception ignored){}
        return false;
    }

    public void executeUpdate(String statement){
        executeUpdate(statement, error -> {
            Common.log(ChatColor.RED, "An error occurred while running statement: " + statement);
            error.printStackTrace();
        });
    }

    public void executeUpdate(String statement, Consumer<SQLException> onFailure){
        try(Connection connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(replaceTable(statement))){
            preparedStatement.executeUpdate();
        } catch (SQLException ex){
            onFailure.accept(ex);
        }
    }

    public void executeQuery(String statement, QueryConsumer<ResultSet> callback){
        executeQuery(statement, callback, error -> {
            Common.log(ChatColor.RED, "An error occurred while running query: " + statement);
            error.printStackTrace();
        });
    }

    public void executeQuery(String statement, QueryConsumer<ResultSet> callback, Consumer<SQLException> onFailure){
        try(Connection connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(replaceTable(statement));
                ResultSet resultSet = preparedStatement.executeQuery()){
            callback.accept(resultSet);
        } catch (SQLException ex){
            onFailure.accept(ex);
        }
    }

    public void buildStatement(String query, QueryConsumer<PreparedStatement> consumer){
        buildStatement(query, consumer, error -> {
            Common.log(ChatColor.RED, "An error occurred while building statement: " + query);
            error.printStackTrace();
        });
    }

    public void buildStatement(String query, QueryConsumer<PreparedStatement> consumer, Consumer<SQLException> onFailure){
        try(Connection connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(replaceTable(query))){
            consumer.accept(preparedStatement);
        } catch (SQLException ex){
            onFailure.accept(ex);
        }
    }

    private String replaceTable(String message){
        return message
                .replace("{clan_table}", SQLHelper.CLAN_TABLE)
                .replace("{stats_table}", SQLHelper.STATS_TABLE)
                .replace("{bedwars_table}", SQLHelper.BEDWARS_TABLE)
                .replace("{player_table}", SQLHelper.PLAYER_TABLE);
    }

    public void close(){
        dataSource.close();
    }

    public interface QueryConsumer<T>{

        void accept(T value) throws SQLException;

    }

}
