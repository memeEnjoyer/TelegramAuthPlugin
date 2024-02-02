package me.huisos.TelegramAuth;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class TelegramAuth extends JavaPlugin {
	
	Logger log = getLogger();
	
	@Override
	public void onEnable() {
		log.info("TelegramAuth enabled");
		getCommand("auth").setExecutor(new TelegramAuthCommandExecutor(this));
		Bukkit.getServer().setDefaultGameMode(GameMode.ADVENTURE);
		
		TimerTask Task = new TimerTask() {
			public void run() {
				for(Player p : Bukkit.getServer().getOnlinePlayers()) {
					boolean auth = TelegramAuthCommandExecutor.isAuth(p);
					if(!auth) {
						String authCheckMessage = "Вы не привязаны к аккаунту Telegram. Для привязки напишите боту @McTgAuthBot в Telegram.";
						p.sendMessage(authCheckMessage);
					}
				}
			}
		};
		Timer timer = new Timer();
		
		timer.schedule(Task, 0L, 10000L);
	}
	
	public void onDisable() {
		log.info("TelegramAuth disabled");
	}
	
}
