package oba.bot;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import javax.security.auth.login.LoginException;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import oba.money.Bank;

public class Application {
	
	private static Properties properties;
	private static JDA discord;
	private static TextChannel log;
	private static Bank bank;

	public static void main(String[] args) {
		
		//Read the properties

		properties = new Properties();
		try {
			//Read all data
			FileReader configFile = new FileReader("config.txt");
			properties.load(configFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Open up Discord
		
		try {
			discord = new JDABuilder((String) properties.get("token")).build().awaitReady();
		} catch (LoginException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Grab the log channel
		log = discord.getTextChannelById((String) properties.get("log_channel"));
		
		//Make the bank
		ObjectMapper mapper = new ObjectMapper();
		try {
			File file = new File((String) properties.get("bank_file"));
			if(file.exists()) {
				bank = mapper.readValue(file, Bank.class);
			}
			else {
				bank = new Bank();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Start listener
		discord.addEventListener(new Listener());
		
		//Say hello
		log("Bot initialized.");
	}

	public static void log(String s) {
		System.out.println(s);
		log.sendMessage(s).queue();
		FileWriter output;
		try {
			output = new FileWriter((String) properties.get("log_file"), true);
			output.write("\n"+s);
			output.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static JDA getDiscord() {
		return discord;
	}
	
	public static Properties getProperties() {
		return properties;
	}
	
	public static Bank getBank() {
		return bank;
	}
}
