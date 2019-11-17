package oba.war.bot;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import oba.war.sudoku.SudokuListener;

public class WarApplication {

	private static Properties properties;

	private static JDA discord;
	
	public static void main(String[] args) {
		start();
	}

	public static void start() {
		
		//Read the properties
		properties = new Properties();
		try {
			//Read all data
			FileReader configFile = new FileReader("war_config.txt");
			properties.load(configFile);
			FileReader tokenFile = new FileReader("tokens.txt");
			properties.load(tokenFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//Open up Discord

		try {
			discord = new JDABuilder((String) properties.get("war_token")).build().awaitReady();
		} catch (LoginException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//Start Sudoku listener
		SudokuListener sudoku = new SudokuListener(discord.getTextChannelById((String) properties.get("slow_channel")));
		sudoku.readState(new File((String) properties.get("sudoku_file")));
		discord.addEventListener(sudoku);
		
		sudoku.sendGrid();
	}

	public static Properties getProperties() {
		return properties;
	}

}
