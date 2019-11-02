package oba.unified;

import oba.bank.bot.BankApplication;
import oba.war.bot.WarApplication;

public class UnifiedApplication {

	public static void main(String[] args) {
		BankApplication.start(true);
		WarApplication.start();
	}

}
