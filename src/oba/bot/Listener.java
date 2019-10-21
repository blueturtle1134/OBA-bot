package oba.bot;

import java.util.List;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import oba.money.Bank;

public class Listener extends ListenerAdapter {

	JDA discord = Application.getDiscord();
	Bank bank = Application.getBank();
	
	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
		MessageChannel channel = e.getChannel();
		if(channel.getId() == Application.getProperties().get("bank_channel")) {
			String contentRaw = e.getMessage().getContentRaw();
			String[] line = contentRaw.split(" ",4);
			User author = e.getAuthor();
			long authorId = author.getIdLong();
			if(line[0].equalsIgnoreCase("Transfer")) {
				List<User> usersByName = discord.getUsersByName(line[1], true);
				if(usersByName.size()>0) {
					long dest = usersByName.get(0).getIdLong();
					int amount = Integer.parseInt(line[3]);
					if(bank.getBalance(authorId)>amount) {
						bank.change(authorId, -1*amount);
						bank.change(dest, amount);
						Application.log("Transferred "+amount+" Chrona from "+bank.getAccount(authorId).getName()+" to "+bank.getAccount(dest).getName());
						channel.sendMessage(amount+" Chrona delivered to "+discord.getUserById(dest).getAsMention());
					}
					else {
						channel.sendMessage(author.getAsMention()+", you do not have enough Chrona to send!");
					}
				}
			}
			if(contentRaw.contains("balance")) {
				channel.sendMessage(author.getAsMention()+", you have "+bank.getBalance(author.getIdLong())+" Chrona.");
			}
			if(contentRaw.contains("daily")) {
				if(bank.getAccount(authorId).canDaily()) {
					int dailyReward = 5;
					bank.change(authorId, dailyReward);
					channel.sendMessage(author.getAsMention()+" claims a daily reward of "+dailyReward+" Chrona.");
					Application.log(author.getName()+" ("+authorId+") claims a daily reward of "+dailyReward+" Chrona.");
					bank.getAccount(authorId).resetDaily();
				}
			}
		}
	}
}
