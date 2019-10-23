package oba.money;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import net.dv8tion.jda.api.entities.User;
import oba.bot.Application;

@JsonSerialize(using = Bank.BankSerializer.class)
@JsonDeserialize(using = Bank.BankDeserializer.class)
public class Bank {

	private File saveFile;
	private Map<Long, Account> accounts;
	
	public Bank() {
		accounts = new HashMap<Long, Account>();
	}
	
	public Bank(File saveFile) {
		this.saveFile = saveFile;
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
		ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.writeValue(saveFile, this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static class BankSerializer extends StdSerializer<Bank> {
		
		private static final long serialVersionUID = 1L;

		public BankSerializer() {
			this(null);
		}

		public BankSerializer(Class<Bank> t) {
			super(t);
		}

		@Override
		public void serialize(Bank bank, JsonGenerator generator, SerializerProvider provider) throws IOException {
			generator.writeStartObject();
			generator.writeArrayFieldStart("accounts");
			for(Long a : bank.accounts.keySet()) {
				generator.writeObject(bank.accounts.get(a));
			}
			generator.writeEndArray();
			generator.writeStringField("file", bank.saveFile.getPath());
			generator.writeEndObject();
		}

	}
	
	public static class BankDeserializer extends StdDeserializer<Bank>{
		
		private static final long serialVersionUID = 1L;

		public BankDeserializer() {
			this(null);
		}

		public BankDeserializer(Class<Bank> vc) {
			super(vc);
		}

		@Override
		public Bank deserialize(JsonParser parser, DeserializationContext context)
				throws IOException, JsonProcessingException {
			Bank bank = new Bank();
			JsonNode node = parser.getCodec().readTree(parser);
			ObjectMapper mapper = new ObjectMapper();
			System.out.println(node.get("accounts"));
			Account[] treeToValue = mapper.treeToValue(node.get("accounts"), Account[].class);
			System.out.println(treeToValue);
			for(Account a : mapper.treeToValue(node.get("accounts"), Account[].class)) {
				bank.accounts.put(a.id, a);
			}
			bank.saveFile = new File(mapper.treeToValue(node.get("file"), String.class));
			return bank;
		}
		
	}
}
