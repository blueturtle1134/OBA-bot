package oba.war.sudoku;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import oba.bank.bot.BankApplication;
import oba.war.bot.WarApplication;

public class SudokuListener extends ListenerAdapter {
	
	MessageChannel channel;
	long channelID;
	SudokuState state;
	
	public SudokuListener(MessageChannel channel) {
		this.channel = channel;
		this.channelID = channel.getIdLong();
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
		String content = e.getMessage().getContentRaw();
		if(e.getChannel().getIdLong() == channelID && content.matches("\\d \\d \\d")) {
			String[] strung = content.split(" ");
			int x = Integer.parseInt(strung[0]);
			int y = Integer.parseInt(strung[1]);
			int v = Integer.parseInt(strung[2]);
			if(x>0&&y>0&&v>0) {
				if(state.addValue(x-1, y-1, v)) {
					channel.sendMessage("Placed!").queue();
					if(state.filled()) {
						channel.sendMessage("This board is done! "+e.getAuthor().getAsMention()+" wins one point!").queue();;
						this.state = SudokuState.getBlankState();
					}
					saveState(new File((String) WarApplication.getProperties().get("sudoku_file")));
				}
				else {
					channel.sendMessage("Invalid location!").queue();
				}
				sendGrid();
			}
		}
	}

	public void sendGrid() {
		channel.sendMessage("```"+state.printGrid()+"```").queue();
	}
	
	public void saveState(File file) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.writeValue(file, this.state);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void readState(File file) {
		if(file.exists()) {
			ObjectMapper mapper = new ObjectMapper();
			try {
				this.state = mapper.readValue(file, SudokuState.class);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {
			this.state = SudokuState.getBlankState();
		}
	}
}
