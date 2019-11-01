package oba.bank.money;

public class Account {
	private static final long DAY_LENGTH = 1000*60*60*24;
	long id;
	String name;
	int balance;
	long lastDaily;
	
	public Account() {
		
	}
	
	public Account(long id, String name, int balance, long lastDaily) {
		this.id = id;
		this.name = name;
		this.balance = balance;
		this.lastDaily = lastDaily;
	}
	
	public Account(long id, String name) {
		this.id = id;
		this.name = name;
		this.balance = 0;
		this.lastDaily = 0;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getBalance() {
		return balance;
	}

	public void setBalance(int balance) {
		this.balance = balance;
	}
	
	public boolean canDaily() {
		return System.currentTimeMillis()-lastDaily > DAY_LENGTH;
	}
	
	public void resetDaily() {
		this.lastDaily = System.currentTimeMillis();
	}
	
	public long getLastDaily() {
		return lastDaily;
	}

	public void setLastDaily(long lastDaily) {
		this.lastDaily = lastDaily;
	}

	public static Account load(String string) {
		String[] line = string.split(" ");
		return new Account(Long.parseLong(line[0]), line[1], Integer.parseInt(line[2]), Long.parseLong(line[3]));
	}
	
	public String save() {
		return getId()+" "+getName()+" "+getBalance()+" "+this.lastDaily;
	}
}
