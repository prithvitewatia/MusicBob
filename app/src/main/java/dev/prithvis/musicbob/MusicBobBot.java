package dev.prithvis.musicbob;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * The main "Music Bob" bot class.
 *
 * @author prithvisingh
 */
public class MusicBobBot extends TelegramLongPollingBot {
	private static final Logger LOGGER = LogManager.getLogger(MusicBobBot.class);
	private final MusicBobCommands commands = new MusicBobCommands();
	private final MusicBobMessageUtil messageUtil = new MusicBobMessageUtil();
	private boolean scream = false;
	private final String botToken;
	private final String botUserName;

	public MusicBobBot(String botToken,String botUserName) {
		this.botUserName = botUserName;
		this.botToken = botToken;
	}

	@Override
	public void onUpdateReceived(Update update) {
		Message msg = update.getMessage();
		User user = msg.getFrom();
		LOGGER.info("Received update " + update + " from " + msg);
		if (msg.isCommand()) {
			commands.process(msg.getText());
		} else if (scream) {
			messageUtil.sendText(user.getId(),msg.getText().toUpperCase());
		} else {
			messageUtil.sendText(user.getId(),msg.getText());
		}
	}

	@Override
	public String getBotToken() {
		return botToken;
	}

	@Override
	public String getBotUsername() {
		return botUserName;
	}

	public class MusicBobCommands {
		private static final Logger LOGGER = LogManager.getLogger(MusicBobCommands.class);

		public MusicBobCommands() {
		}

		public void process(String command) {
			switch (command) {
				case "/scream" -> scream = true;
				case "/whisper" -> scream = false;
				default -> LOGGER.error("Unknown command " + command);
			}
		}
	}

	public class MusicBobMessageUtil {
		private static final Logger LOGGER = LogManager.getLogger(MusicBobMessageUtil.class);
		private final ResourceBundle bundle;
		private final Locale locale;

		public MusicBobMessageUtil() {
			this.locale = Locale.ENGLISH;
			this.bundle = ResourceBundle.getBundle("messages",this.locale);
		}

		public MusicBobMessageUtil(Locale locale) {
			this.locale = locale;
			this.bundle = ResourceBundle.getBundle("messages",this.locale);
		}

		public void sendText(Long to,String text) {
			LOGGER.info("Sending text message " + text + " to " + to);
			SendMessage sm = SendMessage.builder().chatId(to).text(text).build();
			try {
				execute(sm);
			} catch (TelegramApiException e) {
				LOGGER.error("Could not send message");
			}
		}

		public String getCustomHelpMessage(String key) {
			return this.bundle.getString(key);
		}
	}
}
