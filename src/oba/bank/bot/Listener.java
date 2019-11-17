package oba.bank.bot;

import java.io.File;
import java.util.List;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import oba.bank.money.Account;
import oba.bank.money.Bank;
import oba.bank.money.Rank;
import oba.bank.money.Wring;

public class Listener extends ListenerAdapter {

	private static final int PASSOVER_LIMIT = 30;
	public static final long IMAGE_CYCLE = 1000;
	public static final long PASSOVER_CYCLE = 20*60*1000;

	private static final String NOT_RECOGNIZED_MESSAGE = "Name not recognized. Use the username without an @ or # and ID. Nicknames don't work yet.";
	JDA discord = BankApplication.getDiscord();
	Bank bank = BankApplication.getBank();
	long lastImage = System.currentTimeMillis();
	long lastPassover = System.currentTimeMillis();
	long lastSave = System.currentTimeMillis();
	String bankChannel = BankApplication.getProperties().getProperty("bank_channel");
	String fedChannel = BankApplication.getProperties().getProperty("fed_channel");
	String bankFile = BankApplication.getProperties().getProperty("bank_file");
	long autosavePeriod = Long.parseLong(BankApplication.getProperties().getProperty("autosave_period"));

	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
		if(e.getAuthor().isBot()) {
			return;
		}
		MessageChannel channel = e.getChannel();
		String contentRaw = e.getMessage().getContentRaw();
		if(channel.getId().equals(bankChannel)) {
			User author = e.getAuthor();
			long authorId = author.getIdLong();
			if(contentRaw.matches("^>transfer \\d+ .+")) {
				String[] line = contentRaw.split(" ",3);
				User user = identifyUser(line[2]);
				if(user!=null) {
					long dest = user.getIdLong();
					int amount = Integer.parseInt(line[1]);
					transfer(channel, author, dest, amount);
				}
				else {
					channel.sendMessage(NOT_RECOGNIZED_MESSAGE).queue();
				}
			}
			if(contentRaw.equals(">balance")) {
				channel.sendMessage(author.getAsMention()+", you have "+bank.getBalance(author.getIdLong())+" Chrona.").queue();
			}
			if(contentRaw.equals(">daily")) {
				if(bank.getAccount(authorId).canDaily()) {
					int dailyReward = 5 + (int)(Math.random()*4);
					bank.change(authorId, dailyReward);
					channel.sendMessage(author.getAsMention()+" claims a daily reward of "+dailyReward+" Chrona.").queue();
					BankApplication.log(author.getName()+" claims a daily reward of "+dailyReward+" Chrona.");
					bank.getAccount(authorId).resetDaily();
					autosave();
				}
				else {
					channel.sendMessage(author.getAsMention()+" has already claimed a reward in the last 24 hours!").queue();
				}
			}
			if(contentRaw.equals(">save")) {
				BankApplication.save();
				lastSave = System.currentTimeMillis();
			}
			if(contentRaw.equals(">wring")) {
				int minutes = (int) ((System.currentTimeMillis()-bank.getWringTime())/(1000*60));
				bank.resetWring();
				int amount = Wring.wringAmount(minutes);
				if(amount>0) {
					channel.sendMessage("It has been **"+Wring.minutesToString(minutes)+"** since the last wring.\n"+author.getAsMention()+" has wrung **"+amount+"** Chrona.").queue();
				}
				else {
					channel.sendMessage("It has been **"+Wring.minutesToString(minutes)+"** since the last wring.\n"+author.getAsMention()+" has **lost one Chrona** for being impatient.").queue();
					amount = -1;
				}
				bank.change(authorId, amount);
				autosave();
				BankApplication.log(author.getName()+" wrings "+amount+" Chrona.");
			}
			if(contentRaw.equals(">rank")) {
				int rankNum = Rank.getRank(e.getMember());
				if(rankNum < 0) {
					channel.sendMessage(e.getAuthor().getAsMention()+", you don't have a rank role! Assigning the lowest rank to you now.").queue();
					Rank.changeRank(e.getMember(), 0);
				}
				else {
					Rank rank = Rank.getRankByNumber(rankNum);
					String output = e.getAuthor().getAsMention()+", your current rank is "+rank.getName();
					if(rankNum<Rank.rankCount()) {
						Rank next = Rank.getRankByNumber(rankNum+1);
						output += "\nThe next rank is **"+next.getName()+"** and costs **"+next.getCost()+"** Chrona.";
						if(next.getCost()<=bank.getBalance(authorId)) {
							output += "\nYou can buy this rank now! (Use `>rankup` to buy next rank)";
						}
						else {
							output += "\nYou need **"+(next.getCost()-bank.getBalance(authorId))+"** more Chrona to buy this rank.";
						}
					}
					else {
						output += "\nThat's the last rank.";
					}
					channel.sendMessage(output).queue();
				}
			}
			if(contentRaw.equals(">rankup")) {
				int rankNum = Rank.getRank(e.getMember());
				if(rankNum < 0) {
					channel.sendMessage(e.getAuthor().getAsMention()+", you don't have a rank role! Assigning the lowest rank to you now.").queue();
					Rank.changeRank(e.getMember(), 0);
				}
				else {
					if(rankNum<Rank.rankCount()) {
						Rank next = Rank.getRankByNumber(rankNum+1);
						if(next.getCost()<=bank.getBalance(authorId)) {
							channel.sendMessage(e.getAuthor().getAsMention()+", you have spent "+next.getCost()+" Chrona to buy the **"+next.getName()+"** rank.").queue();
							Rank.changeRank(e.getMember(), rankNum+1);
							bank.change(authorId, -1*next.getCost());
							BankApplication.log(author.getName()+" purchases "+next.getName()+" rank.");
							autosave();
						}
						else {

							channel.sendMessage(e.getAuthor().getAsMention()+", you need **"+(next.getCost()-bank.getBalance(authorId))+"** more Chrona to buy the next rank (**"+next.getName()+"**).").queue();
						}
					}
					else {
						channel.sendMessage(e.getAuthor().getAsMention()+" There are no more ranks to buy!").queue();
					}
				}
			}
		}
		if(channel.getId().contentEquals(fedChannel)) {
			if(contentRaw.matches("^>reward -?\\d+ .+")) {
				String[] line = contentRaw.split(" ",3);
				User user = identifyUser(line[2]);
				if(user!=null) {
					reward(channel, Integer.parseInt(line[1]), user);
					BankApplication.log(e.getAuthor().getName()+" rewards "+line[1]+" Chrona to "+user.getName());
					autosave();
				}
				else {
					channel.sendMessage(NOT_RECOGNIZED_MESSAGE).queue();
				}
			}
			if(contentRaw.matches("^>alias .+ .+")) {
				String[] line = contentRaw.split(" ",3);
				User user = identifyUser(line[2]);
				if(user!=null) {
					if(bank.addAlias(line[1], user.getIdLong())) {
						channel.sendMessage("Using "+line[1]+" will now refer to "+user.getAsMention()).queue();
						BankApplication.log("Alias added: "+line[1]+" to "+user.getName());
						autosave();
					}
					else {
						channel.sendMessage("Alias already taken.").queue();
					}
				}
				else {
					channel.sendMessage(NOT_RECOGNIZED_MESSAGE).queue();
				}
			}
		}
		else if(contentRaw.matches("^[hH][aA][iI][lL].+!+")&&System.currentTimeMillis()-lastImage>IMAGE_CYCLE) {
			e.getChannel().sendFile(new File("hail.jpg")).queue();
			lastImage = System.currentTimeMillis();
		}
		else if(contentRaw.contains("Christmas")&&System.currentTimeMillis()-lastPassover>PASSOVER_CYCLE&&contentRaw.length()>PASSOVER_LIMIT) {
			e.getChannel().sendMessage("> "+contentRaw.replace("Christmas", "Passover")+"\nftfy").queue();
		}
	}

	private User identifyUser(String string) {
		List<User> usersByName = discord.getUsersByName(string, true);
		if(usersByName.size()>0) {
			return usersByName.get(0);
		}
		else {
			Account alias = bank.getAccount(string);
			if(alias==null) {
				return null;
			}
			else {
				usersByName = discord.getUsersByName(alias.getName(), true);
				if(usersByName.size()>0) {
					return usersByName.get(0);
				}
				else {
					return null;
				}
			}
		}
	}
	
	private void autosave() {
		if(System.currentTimeMillis()-lastSave>autosavePeriod) {
			BankApplication.save();
			lastSave = System.currentTimeMillis();
		}
	}

	private void reward(MessageChannel channel, int amount, User user) {
		long dest = user.getIdLong();
		bank.change(dest, amount);
		channel.sendMessage(amount+" Chrona delivered to "+discord.getUserById(dest).getAsMention()).queue();
		BankApplication.log(amount+" Chrona rewarded to "+discord.getUserById(dest).getName());
	}

	private void transfer(MessageChannel channel, User author, long dest, int amount) {
		long authorId = author.getIdLong();
		if(bank.getBalance(authorId)>amount) {
			bank.change(authorId, -1*amount);
			bank.change(dest, amount);
			BankApplication.log("Transferred "+amount+" Chrona from "+bank.getAccount(authorId).getName()+" to "+bank.getAccount(dest).getName());
			channel.sendMessage(amount+" Chrona delivered to "+discord.getUserById(dest).getAsMention()).queue();
		}
		else {
			channel.sendMessage(author.getAsMention()+", you do not have enough Chrona to send!").queue();
		}
	}
}
