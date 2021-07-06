package steam;

import ru.codebehind.steam.mobileauthentication.SteamGuardAccount;
import ru.codebehind.steam.mobileauthentication.SteamGuardAccount.Config;

public class SteamGuard {
	
	//sharedSecret, serialNumber, revocationCode, uri, serverTime, accountName, tokenGID, identitySecret, secret1
	//status, deviceID, fullyEnrolled 
	private Config cfg;
	private SteamGuardAccount SGA;

	public SteamGuard(String SharedSecret) {
		this.cfg = new Config();
		cfg.setSharedSecret(SharedSecret);
		//More values for config can be null since we only care about GenerateSteamGuardCode method
		
		this.SGA = new SteamGuardAccount(cfg);
	}
	
	public String getCode() {
		try {
			return SGA.GenerateSteamGuardCode();
		} catch (Throwable e) {
			e.printStackTrace();
			return "error";
		}
	}
}
