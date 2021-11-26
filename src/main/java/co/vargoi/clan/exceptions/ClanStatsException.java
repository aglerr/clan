package co.vargoi.clan.exceptions;

import co.vargoi.clan.clan.objects.ClanStats;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;

public class ClanStatsException extends Exception{

    private final String uuid;
    private final int balance;
    private final int points;
    private final String message;

    public ClanStatsException(ClanStats clanStats, String message){
        this.uuid = clanStats.getClanUUID();
        this.balance = clanStats.getBalance();
        this.points = clanStats.getPoints();
        this.message = message;
    }

    public ClanStatsException(String uuid, int balance, int points, String message){
        this.uuid = uuid;
        this.balance = balance;
        this.points = points;
        this.message = message;
    }

    @Override
    public void printStackTrace(){
        ConsoleCommandSender sender = Bukkit.getConsoleSender();
        sender.sendMessage(ChatColor.RED + "[VargoiClan] " + message);
        sender.sendMessage(ChatColor.RED + "[VargoiClan] UUID: " + uuid);
        sender.sendMessage(ChatColor.RED + "[VargoiClan] Balance: " + balance);
        sender.sendMessage(ChatColor.RED + "[VargoiClan] Points: " + points);

        super.printStackTrace();
    }

}
