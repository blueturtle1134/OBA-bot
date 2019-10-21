package oba.money;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Map.Entry;

import oba.bot.Application;

import java.util.Scanner;

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
	}
	
	public Account getAccount(long id) {
		return accounts.get(id);
	}
	
	public int getBalance(long id) {
		if(accounts.containsKey(id)) {
			return accounts.get(id).getBalance();
		}
		else {
			return 0;
		}
	}
	
	public void change(long id, int delta) {
		if(!accounts.containsKey(id)) {
			Account newAccount = new Account(id, Application.getDiscord().getUserById(id).getName());
			accounts.put(id, newAccount);
			Application.log("Created new account for "+newAccount.getName()+" ("+id+")");
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
