package oba.bank.money;

public class Rank {
	private final String name;
	private final long id;
	private final int cost;
	
	public Rank(String name, long id, int cost) {
		this.name = name;
		this.id = id;
		this.cost = cost;
	}
	public String getName() {
		return name;
	}
	public long getId() {
		return id;
	}
	public int getCost() {
		return cost;
	}
	
}
