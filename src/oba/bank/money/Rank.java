package oba.bank.money;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import oba.bank.bot.BankApplication;

public class Rank {
	private final String name;
	private final long id;
	private final int cost;
	private final Role role;
	
	private static Rank[] ranks;
	
	public Rank(String name, long id, int cost) {
		this.name = name;
		this.id = id;
		this.cost = cost;
		this.role = BankApplication.getGuild().getRoleById(id);
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
	
	public static void readRanks(File file) throws FileNotFoundException {
		Scanner scanner = new Scanner(file);
		LinkedList<Rank> rankList = new LinkedList<Rank>();
		while(scanner.hasNextLine()) {
			String[] line = scanner.nextLine().split(" ");
			rankList.add(new Rank(line[0], Long.parseLong(line[2]), Integer.parseInt(line[1])));
		}
		scanner.close();
		ranks = new Rank[rankList.size()];
		ranks = rankList.toArray(ranks);
	}
	
	public static int getRank(Member member) {
		List<Role> memberRoles = member.getRoles();
		for(int i = 0; i<ranks.length; i++) {
			if(memberRoles.contains(ranks[i].role)) {
				return i;
			}
		}
		return -1;
	}
	
	public static void changeRank(Member member, int rankNumber) {
		List<Role> memberRoles = member.getRoles();
		for(int i = 0; i<ranks.length; i++) {
			if(memberRoles.contains(ranks[i].role)) {
				BankApplication.getGuild().removeRoleFromMember(member, ranks[i].role);
			}
		}
		BankApplication.getGuild().addRoleToMember(member, ranks[rankNumber].role);
	}
}
