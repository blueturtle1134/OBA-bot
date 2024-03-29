package oba.bank.bot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;

import javax.security.auth.login.LoginException;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import oba.bank.gui.Controls;
import oba.bank.money.Bank;
import oba.bank.money.Rank;

public class BankApplication {

	private static Properties properties;
	private static JDA discord;
	private static TextChannel log;
	private static Bank bank;
	private static Controls controls = null;
	private static final DateFormat timestampFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
	private static Guild guild;
	static {
		timestampFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	public static void main(String[] args) {
		start(false);
	}

	public static void start(boolean gui) {
		//Read the properties

		properties = new Properties();
		try {
			//Read all data
			FileReader configFile = new FileReader("config.txt");
			properties.load(configFile);
			FileReader tokenFile = new FileReader("tokens.txt");
			properties.load(tokenFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//Open up Discord

		try {
			discord = new JDABuilder(properties.getProperty("bank_token")).build().awaitReady();
		} catch (LoginException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//Grab the log channel
		log = discord.getTextChannelById(properties.getProperty("log_channel"));

		//Make the bank
		ObjectMapper mapper = new ObjectMapper();
		try {
			File file = new File(properties.getProperty("bank_file"));
			if(file.exists()) {
				bank = mapper.readValue(file, Bank.class);
			}
			else {
				bank = new Bank();
				bank.resetWring();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		if(gui){
			//Start the panel
			controls = new Controls();
		}

		//Start listener
		discord.addEventListener(new Listener());

		//Say hello
		log("Initializing OBA Bot version "+getVersion());

		//Grab the server location
		guild = discord.getGuildById(Long.parseLong(properties.getProperty("server")));

		//Kick the ranks system
		try {
			Rank.readRanks(new File(properties.getProperty("rank_file")));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//Save for changing versions and stuff
		save();
	}

	public static void save() {
		bank.save(new File(properties.getProperty("bank_file")));
		log("Data saved.");
	}

	public static void log(String s) {
		Date now = new Date();
		s = "["+timestampFormat.format(now)+"] "+s;
		System.out.println(s);
		log.sendMessage(s).queue();
		FileWriter output;
		try {
			output = new FileWriter(properties.getProperty("log_file"), true);
			output.write("\n"+s);
			output.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(controls!=null) {
			controls.print(s);
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

	public static void stop() {
		log("Stopping bot.");
		System.exit(0);
	}

	public static String getVersion() {
		return "1.0.0";
	}

	public static Guild getGuild() {
		return guild;
	}
}
