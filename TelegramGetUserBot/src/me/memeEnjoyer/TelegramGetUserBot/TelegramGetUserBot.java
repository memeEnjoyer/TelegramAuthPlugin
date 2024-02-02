package me.huisos.TelegramGetUserBot;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@SuppressWarnings("deprecation")
public class TelegramGetUserBot extends TelegramLongPollingBot {
	private boolean promptFlag = false;
	private boolean exec = false; // Работает ли бот?
	private String mcUser = null;
	
	@Override
	public void onUpdateReceived(Update update) {
		
		if(update.hasMessage() && update.getMessage().hasText()) {
			String message_text = update.getMessage().getText();
			long chat_id = update.getMessage().getChatId();
			
			SendMessage startMessage = new SendMessage();
			startMessage.setChatId(chat_id);
			startMessage.setText("Укажите ник на сервере майнкрафта для привязки к аккаунту.");
			
			SendMessage message = new SendMessage();
			
			String userName = null;
			
			User sender = update.getMessage().getFrom();
			userName = sender.getUserName();

			String promptText = "Вы действительно хотите привязать ник '" + message_text + "' к аккаунту @" + userName + " ?";
			
			SendMessage promptMessage = new SendMessage();
			promptMessage.setChatId(chat_id);
			promptMessage.setText(promptText);
			
			if(promptFlag) {
				mcUser = message_text;
			}
			/*
			 * Переписать логику ответов "да", "нет", создать отдельные объекты message_no, message_yes
			 * Для возможности отвязки аккаунтов обязательно
			 */
			
			if(message_text.equalsIgnoreCase("нет") && !promptFlag && exec) {
				message.setChatId(chat_id);
				message.setText("Жаль... Для продолжения работы бота введите команду /start.");
			} else if(message_text.equalsIgnoreCase("да") && !promptFlag && exec) {
				
				try(Connection con = DriverManager.getConnection("jdbc:mysql://localhost", "root", "root")) {
					
					String queryCheck = "select count(tgUser) from mcUsers where tgUser = '" + userName + "';";
					Statement stmtCheck = con.createStatement();
					ResultSet rs = stmtCheck.executeQuery(queryCheck);
					
					int rsCount = 0;
					
					while(rs.next()) {
					System.out.println(rs.getInt("count(tgUser)"));
					rsCount = rs.getInt("count(tgUser)");
					}
					
					if(rsCount == 0) {
					String queryInsert = "insert into mcUsers (tgUser, mcUser) values('" + userName + "', '" + mcUser + "');";
					
					Statement stmtInsert = con.createStatement();
					
					int resInsert = stmtInsert.executeUpdate(queryInsert);
					
					System.out.println("Added pair: " + resInsert + " tgUser: " + userName + " mcUser: " + mcUser);
					
					message.setChatId(chat_id);
					message.setText("Ваш аккаунт @" + userName + " был привязан к нику '" + mcUser + "'. Для продолжения работы бота введите команду /start.");
					} else {
						message.setChatId(chat_id);
						message.setText("Аккаунт @" + userName + " уже привязан к другому нику. Для продолжения работы бота введите команду /start.");
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
			try {
				if(message_text.equalsIgnoreCase("/start")) {
					execute(startMessage);
					promptFlag = true;
					exec = true;
				} else if(exec) {
					
					if(promptFlag){
						execute(promptMessage);
						promptFlag = false;
					} else {
						execute(message);
						exec = false;
					}
				}
			} catch (TelegramApiException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public String getBotUsername() {
		
		return "McTgAuthBot";
	}
	
	@Override
	public String getBotToken() {
		return "6840208069:AAGg2DG-uaV4B2PC5Yh8vyFTI2_EqrWPFzM";
	}
}
