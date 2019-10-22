package oba.money;

public class Account {
	private static final long DAY_LENGTH = 1000*60*60*24;
	long id;
	String user;
	int balance;
	long lastDaily;
	
	Account(long id, String user, int balance, long lastDaily) {
		this.id = id;
		this.user = user;
		this.balance = balance;
		this.lastDaily = lastDaily;
	}
	
	Account(long id, String user) {
		this.id = id;
		this.user = user;
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
		return user;
	}

	public void setUser(String user) {
		this.user = user;
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
	
	public static Account load(String string) {
		String[] line = string.split(" ");
		return new Account(Long.parseLong(line[0]), line[1], Integer.parseInt(line[2]), Long.parseLong(line[3]));
	}
	
	public String save() {
		return getId()+" "+getName()+" "+getBalance()+" "+this.lastDaily;
	}
}
