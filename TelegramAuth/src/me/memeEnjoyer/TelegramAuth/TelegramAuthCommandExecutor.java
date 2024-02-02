package me.huisos.TelegramAuth;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TelegramAuthCommandExecutor implements CommandExecutor {
	private static boolean auth = false;
	public static boolean isAuth(Player p) {
		return auth;
	}
	
	private TelegramAuth plugin;
	
	public TelegramAuthCommandExecutor(TelegramAuth plugin) {
		this.plugin = plugin;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		Player player = null;
		
		if(sender instanceof Player) {
			player = (Player) sender;
		}
		
		if(cmd.getName().equals("auth")) {
			try(Connection con = DriverManager.getConnection("jdbc:mysql://localhost", "root", "root")) {
				String query = "select count(mcUser) from mcUsers where mcUser = '" + player.getName() + "';";
				
				Statement stmt = con.createStatement();
				
				ResultSet rs = stmt.executeQuery(query);
				
				int rsCount = 0;
				
				while(rs.next()) {
					rsCount = rs.getInt("count(mcUser)");
				}
				
				if(rsCount >= 1) {
					auth = true;
					player.setGameMode(GameMode.SURVIVAL);
					player.sendMessage("Приятной игры!");
				} else {
					player.sendMessage("Такой пользователь не привязан к Telegram аккаунту.");
				}
				
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}
}
