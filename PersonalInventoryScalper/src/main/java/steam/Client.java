package steam;

import java.util.concurrent.TimeUnit;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import files.ConfigHelper;
import files.InventoryFilesManager;
import in.dragonbra.javasteam.enums.EResult;
import in.dragonbra.javasteam.steam.handlers.steamuser.LogOnDetails;
import in.dragonbra.javasteam.steam.handlers.steamuser.SteamUser;
import in.dragonbra.javasteam.steam.handlers.steamuser.callback.LoggedOffCallback;
import in.dragonbra.javasteam.steam.handlers.steamuser.callback.LoggedOnCallback;
import in.dragonbra.javasteam.steam.steamclient.SteamClient;
import in.dragonbra.javasteam.steam.steamclient.callbackmgr.CallbackManager;
import in.dragonbra.javasteam.steam.steamclient.callbacks.ConnectedCallback;
import in.dragonbra.javasteam.steam.steamclient.callbacks.DisconnectedCallback;
import utils.Utils;

public class Client implements Runnable {
	
	private static final Logger log = (Logger) LoggerFactory.getLogger(Client.class);
	
	private SteamClient steamClient;
	private CallbackManager manager;
	private SteamUser steamUser;
	private boolean isRunning;
	private boolean loggedIn;
	private String username;
	private String password;
	private String secret;
	
	private int timeout;
	
	private String webApiUserNonce;
	private SteamWebHandler SWH;
	
	private ConfigHelper ch;
	
	public Client(String username, String password, String secret, String timeout) {
		this.username = username;
		this.password = password;
		this.secret = secret;
		this.timeout = Integer.parseInt(timeout);
		this.ch = new ConfigHelper();
		this.loggedIn = false;
	}

	@Override
	public void run() {
		steamClient = new SteamClient();
		manager = new CallbackManager(steamClient);
		steamUser = steamClient.getHandler(SteamUser.class);
		
		manager.subscribe(ConnectedCallback.class, this::onConnected);
        manager.subscribe(DisconnectedCallback.class, this::onDisconnected);

        manager.subscribe(LoggedOnCallback.class, this::onLoggedOn);
        manager.subscribe(LoggedOffCallback.class, this::onLoggedOff);

        isRunning = true;
        steamClient.connect();
		
        while(isRunning) {
        	manager.runWaitCallbacks(timeout*1000);
        	if(loggedIn) {
        		this.inventoryAction();
        	}
        }
	}

	private void onConnected(ConnectedCallback callback) {
        log.info("Connected to Steam! Logging in " + username + "...");

        LogOnDetails details = new LogOnDetails();
        details.setUsername(username);
        details.setPassword(password);
        
        //No length = no auth
        //Length 5 = code
        // >5 = secret key
        if(secret.length() > 5) { 
        	SteamGuard SG = new SteamGuard(secret);
        	details.setTwoFactorCode(SG.getCode());
        } else if(secret.length() == 5) {
        	details.setTwoFactorCode(secret);
        }
        
        steamUser.logOn(details);
    }

    private void onDisconnected(DisconnectedCallback callback) {
        log.warn("Disconnected from Steam");
        isRunning = false;
    }

    private void onLoggedOn(LoggedOnCallback callback) {
    	boolean steamGuardFail = (callback.getResult() == EResult.AccountLogonDenied);
		
		if(steamGuardFail) { //Probably if we generate a code during the last second we will run into a bad code
			log.warn("Login failed with bad 2auth code, attempting again soon...");
			steamClient.disconnect();
			return;
		}
		
		if(callback.getResult() != EResult.OK) { //Some other issue
			log.warn("Login failed: " + callback.getResult() + "\n More Details: \n" + callback.getExtendedResult());
			isRunning = false;
			steamClient.disconnect();
			return;
		}
		
		log.info("Login successful!");
		//Startup actions go here if we have any later
		webApiUserNonce = callback.getWebAPIUserNonce();
		SWH = SteamWebHandler.getInstance();
		loggedIn = true;
		/*
		SWH = SteamWebHandler.getInstance();
		SWH.setTimeout(timeout);
		if(SWH.authenticate(steamClient, webApiUserNonce)) {
			log.info("Authenticated our SteamWebHandler!");
			long time;
			long stopTime = 0;
			ch.getOptions();
			if(ch.getOptionFromFile("NextInventoryTime").length() > 2) {
				time = Long.parseLong(ch.getOptionFromFile("NextInventoryTime"));
			} else {
				time = Utils.getCurrentUnixTime();
				stopTime = InventoryFilesManager.getLastTime();
				log.info("Looking to stop at time: " + stopTime);
			}
			long oldTime = time;
			try {
				do {
					oldTime = time;
					time = SWH.getInventoryHistory(time, stopTime);
					if(time == 0) {
						ch.setOptionToFile("NextInventoryTime", String.valueOf(oldTime));
						log.info("Time returned 0, timeout for: " + timeout*10 + " seconds before retrying.");
						log.info("If this is the 2nd time you've seen this message you have been logged out and will need to exit and relog.");
						TimeUnit.SECONDS.sleep(timeout*10);
						time = oldTime;
						log.info("Retrying time: " + time);
						oldTime = 0l; //Switch old time so we can continue with the loop still
					}
				} while(oldTime != time && time != -1);
				ch.setOptionToFile("NextInventoryTime", " ");
				System.out.println("Finished file creation. Please close and relaunch the application to choose your parsing option.");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			log.warn("Failed to authenticate our SteamWebHandler!");
		}*/
    }

    private void onLoggedOff(LoggedOffCallback callback) {
    	loggedIn = false;
        System.out.println("Logged off of Steam: " + callback.getResult());
        isRunning = false;
    }
    
    public boolean isRunning() {
    	return this.isRunning;
    }
    
    public String getWebApiUserNonce() {
    	return this.webApiUserNonce;
    }
    
    public SteamClient getSteamClient() {
    	return this.steamClient;
    }
    
    private void inventoryAction() {
		if(SWH.isAuthenticated()) {
			long time;
			long stopTime = 0;
			
			ch.getOptions();
			if(ch.getOptionFromFile("NextInventoryTime").length() > 2) {
				time = Long.parseLong(ch.getOptionFromFile("NextInventoryTime"));
			} else {
				time = Utils.getCurrentUnixTime();
				stopTime = InventoryFilesManager.getLastTime();
				log.info("Looking to stop at time: " + stopTime);
			}
			long oldTime = time;
			try {
				oldTime = time;
				time = SWH.getInventoryHistory(time, stopTime);
				if(time == 0) { //0 = no table returned
					ch.setOptionToFile("NextInventoryTime", String.valueOf(oldTime));
					log.info("Time returned 0, timeout for: " + timeout*10 + " seconds before retrying.");
					//log.info("If this is the 2nd time you've seen this message you have been logged out and will need to exit and relog.");
					TimeUnit.SECONDS.sleep(timeout*10);
					time = oldTime;
					log.info("Retrying time: " + time);
					oldTime = 0l; //Switch old time so we don't stop early
				} else if(time == 22) { //22 = log in page
					ch.setOptionToFile("NextInventoryTime", String.valueOf(oldTime));
					log.info("Recieved sign in page, attempting to log back in...");
					SWH.unAuth();
					oldTime = 0l; //Switch old time so we don't stop early
				} else {
					ch.setOptionToFile("NextInventoryTime", String.valueOf(time));
				}
				
				if(oldTime == time || time == -1) {
					ch.setOptionToFile("NextInventoryTime", " ");
					System.out.println("Finished file creation. Please close and relaunch the application to choose your parsing option.");
					isRunning = false;
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			SWH.setTimeout(timeout);
			if(SWH.authenticate(steamClient, webApiUserNonce)) {
				log.debug("Authenticated our SteamWebHandler.");
			} else {
				log.warn("Failed to authenticate our SteamWebHandler!");
				try {
					TimeUnit.SECONDS.sleep(timeout*10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return;
			}
		}
    }
}
