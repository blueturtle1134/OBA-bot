package oba.bot;

import java.io.File;
import java.util.List;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import oba.money.Bank;

public class Listener extends ListenerAdapter {

	JDA discord = Application.getDiscord();
	Bank bank = Application.getBank();
	
	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
		MessageChannel channel = e.getChannel();
		if(channel.getId().equals(Application.getProperties().get("bank_channel"))) {
			String contentRaw = e.getMessage().getContentRaw();
			User author = e.getAuthor();
			long authorId = author.getIdLong();
			if(contentRaw.matches("^>transfer .+ \\d+")) {
				String[] line = contentRaw.split(" ",4);
				List<User> usersByName = discord.getUsersByName(line[1], true);
				if(usersByName.size()>0) {
					long dest = usersByName.get(0).getIdLong();
					int amount = Integer.parseInt(line[2]);
					if(bank.getBalance(authorId)>amount) {
						bank.change(authorId, -1*amount);
						bank.change(dest, amount);
						Application.log("Transferred "+amount+" Chrona from "+bank.getAccount(authorId).getName()+" to "+bank.getAccount(dest).getName());
						channel.sendMessage(amount+" Chrona delivered to "+discord.getUserById(dest).getAsMention()).queue();
					}
					else {
						channel.sendMessage(author.getAsMention()+", you do not have enough Chrona to send!").queue();
					}
				}
				else {
					channel.sendMessage("Name not recognized. Use the username without an @ or # and ID. Nicknames don't work yet.").queue();
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
					Application.log(author.getName()+" claims a daily reward of "+dailyReward+" Chrona.");
					bank.getAccount(authorId).resetDaily();
				}
				else {
					channel.sendMessage(author.getAsMention()+" has already claimed a reward in the last 24 hours!").queue();
				}
			}
			if(contentRaw.equals(">save")) {
				bank.save(new File((String) Application.getProperties().get("bank_file")));
				Application.log("Saved data.");
			}
		}
	}
}
