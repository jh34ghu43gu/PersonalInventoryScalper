package main;

import java.util.Scanner;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import files.ConfigHelper;
import files.InventoryFilesManager;
import files.InventoryFilesParser;
import steam.Client;

public class Main {
	
	private static final Logger log = (Logger) LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {
		log.info("Starting...");
		
		//Make/Get config
		ConfigHelper CH = new ConfigHelper();
		CH.addOption("Username", " ");
		CH.addOption("Password", " ");
		CH.addOption("SecretKey", "Secret Key if you have it to auto generate auth codes");
		CH.addOption("RateLimit", "7");
		CH.addOption("NextInventoryTime", " ");
		
		if(!CH.exists()) { //file doesn't exist or the amount of options has changed
			CH.build();
			log.warn("Config not found, config has been made and program will now terminate.\n"
					+ "Please fill out the config and relaunch or relaunch with username/password blank to manually enter at runtime.");
			return;
		} else if (CH.size() != CH.fileSize()) {
			CH.copyFile();
			CH.build();
		} else {
			CH.clear();
		}
		CH.getOptions();
		log.info("Config setup complete.");
		
		//Make directory
		InventoryFilesManager.makeDir();
		
		Scanner s = new Scanner(System.in);
		System.out.println("Would you like to refresh your files? (Y/N)");
		String answer = s.nextLine();
		if(answer.equalsIgnoreCase("Y")) {
			//See what we need from user to login
			String username = CH.getOption("Username");
			String password = CH.getOption("Password");
			if(username.length() < 2) {
				System.out.println("No username provided please enter your username: ");
				username = s.nextLine();
			}
			if(password.length() < 2) {
				System.out.println("No password provided please enter your password\n"
						+ "(NOTE your text will NOT be censored in console): ");
				password = s.nextLine();
			}
			
			//Login
			String secret = CH.getOption("SecretKey");
			if(secret.startsWith("Secret Key") || secret.length() < 2) {
				System.out.println("No secret key provided please enter a steam guard code or press enter (leave blank) if not enabled.\n"
	        			+ "If you are using email 2fa you will have to press enter and try again after the code is sent to you.\n"
	        			+ "2FA Code: ");
	        	secret = s.nextLine();
			}
			s.close();
			Client client = new Client(username, password, secret, CH.getOption("RateLimit"));
			client.run();
		} else {
			InventoryFilesParser IFP = new InventoryFilesParser();
			InventoryFilesParser.makeDir();
			String baseOut = "Please select what data you would like to parse: \n"
					+ "[1] MvM Loot \n"
					+ "[2] Squad Surplus Loot\n"
					+ "[3] Crate unlocks\n"
					+ "[4] Item Drops\n"
					+ "[0] Exit program\n";
			int p = 0;
			do {
			if(p == 1) {
				if(InventoryFilesParser.mvmFileExists()) {
					System.out.println("Would you like to \n"
							+ "[1]refreash the mvm results file \n"
							+ "[2]or just view it?");
					p = s.nextInt();
					if(p == 1) {
						IFP.createMvmFile();
					}
				} else {
					IFP.createMvmFile();
				}
				IFP.outputMvmFile();
			} else if(p == 2) {
				if(InventoryFilesParser.surplusFileExists()) {
					System.out.println("Would you like to \n"
							+ "[1]refreash the surplus results file \n"
							+ "[2]or just view it?");
					p = s.nextInt();
					if(p == 1) {
						IFP.createSurplusFile();
					}
				} else {
					IFP.createSurplusFile();
				}
				IFP.outputSurplusFile();
			} else if(p == 3) {
				if(InventoryFilesParser.unboxFileExists()) {
					System.out.println("Would you like to \n"
							+ "[1]refreash the unbox results file \n"
							+ "[2]or just view it?");
					p = s.nextInt();
					if(p == 1) {
						IFP.createUnboxFile();
					}
				} else {
					IFP.createUnboxFile();
				}
				IFP.outputUnboxFile();
			} else if(p == 4) {
				if(InventoryFilesParser.dropsFileExists()) {
					System.out.println("Would you like to \n"
							+ "[1]refreash the item drops results file \n"
							+ "[2]or just view it?");
					p = s.nextInt();
					if(p == 1) {
						IFP.createDropsFile();
					}
				} else {
					IFP.createDropsFile();
				}
				IFP.outputDropsFile();
			}
			System.out.println(baseOut);
			p = s.nextInt();
			} while(p != 0);
		}
		
		
	}

}
