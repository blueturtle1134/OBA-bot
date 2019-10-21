package oba.money;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import net.dv8tion.jda.api.entities.User;
import oba.bot.Application;

public class Bank {

	private File saveFile;
	private Map<Long, Account> accounts;
	
	public Bank(String fileLocation) {
		saveFile = new File(fileLocation);
		if(saveFile.exists()) {
			try {
				Scanner scanner = new Scanner(saveFile);
				while(scanner.hasNextLine()) {
					Account next = Account.load(scanner.nextLine());
					accounts.put(next.getId(), next);
				}
				scanner.close();
			} catch (FileNotFoundException e) {
				// Should never happen because we checked for existence
				e.printStackTrace();
			}
		}
		
		accounts = new HashMap<Long, Account>();
	}
	
	public Account getAccount(long id) {
		if(!accounts.containsKey(id)) {
			open(Application.getDiscord().getUserById(id));
		}
		return accounts.get(id);
	}
	
	public int getBalance(long id) {
		if(!accounts.containsKey(id)) {
			open(Application.getDiscord().getUserById(id));
		}
		return accounts.get(id).getBalance();
	}
	
	public void open(User user) {
		Account newAccount = new Account(user.getIdLong(), user.getName());
		accounts.put(user.getIdLong(), newAccount);
		Application.log("Created new account for "+newAccount.getName()+" ("+user.getIdLong()+")");
	}
	
	public void change(long id, int delta) {
		if(!accounts.containsKey(id)) {
			open(Application.getDiscord().getUserById(id));
		}
		Account account = accounts.get(id);
		account.setBalance(account.getBalance()+delta);
		save();
	}
	
	public void save() {
		try {
			PrintWriter output = new PrintWriter(saveFile);
			for(Entry<Long,Account> x : accounts.entrySet()) {
				output.println(x.getValue().save());
			}
			output.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
