package steam;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import files.InventoryFilesManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;

import in.dragonbra.javasteam.steam.steamclient.SteamClient;
import in.dragonbra.javasteam.types.KeyValue;
import in.dragonbra.javasteam.types.SteamID;
import in.dragonbra.javasteam.util.KeyDictionary;
import in.dragonbra.javasteam.util.crypto.CryptoException;
import in.dragonbra.javasteam.util.crypto.CryptoHelper;
import in.dragonbra.javasteam.util.crypto.RSACrypto;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import steam.converter.VdfConverterFactory;
import utils.Utils;
import utils.WebHelpers;

//From https://github.com/steevp/UpdogFarmer/blob/f476c77aae1f1553456dc240c4c2407234f56dad/app/src/main/java/com/steevsapps/idledaddy/steam/SteamWebHandler.java#L71
public class SteamWebHandler {
	private final static String TAG = SteamWebHandler.class.getSimpleName();

    private final static int TIMEOUT_SECS = 30;

    private final static String STEAM_STORE = "https://store.steampowered.com/";
    private final static String STEAM_COMMUNITY = "https://steamcommunity.com/";
    private final static String STEAM_API = "https://api.steampowered.com/";

    private final static SteamWebHandler ourInstance = new SteamWebHandler();

    private boolean authenticated;
    private long steamId;
    private String sessionId;
    private String token;
    private String tokenSecure;
    private String steamParental;
    //private String apiKey = BuildConfig.SteamApiKey;
    private int timeout;

    private final SteamAPI api;

    private SteamWebHandler() {
        final Gson gson = new GsonBuilder()
                //.registerTypeAdapter(GamesOwnedResponse.class, new GamesOwnedResponseDeserializer())
                .create();

        final OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(TIMEOUT_SECS, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_SECS, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT_SECS, TimeUnit.SECONDS)
                .build();

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(STEAM_API)
                .addConverterFactory(VdfConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();

        api = retrofit.create(SteamAPI.class);
    }
    
    public void setTimeout(int timeout) {
    	this.timeout = timeout;
    }
    
    public boolean isAuthenticated() {
    	return this.authenticated;
    }
    
    /**
     * Set authenticated to false
     */
    public void unAuth() {
    	this.authenticated = false;
    }

    public static SteamWebHandler getInstance() {
        return ourInstance;
    }
	
	/**
     * Authenticate on the Steam website
     *
     * @param client the Steam client
     * @param webApiUserNonce the WebAPI User Nonce returned by LoggedOnCallback
     * @return true if authenticated
     */
    boolean authenticate(SteamClient client, String webApiUserNonce) {
        authenticated = false;
        final SteamID clientSteamId = client.getSteamID();
        if (clientSteamId == null) {
            return false;
        }
        steamId = clientSteamId.convertToUInt64();
        sessionId = Utils.bytesToHex(CryptoHelper.generateRandomBlock(4));

        // generate an AES session key
        final byte[] sessionKey = CryptoHelper.generateRandomBlock(32);

        // rsa encrypt it with the public key for the universe we're on
        final byte[] publicKey = KeyDictionary.getPublicKey(client.getUniverse());
        if (publicKey == null) {
            return false;
        }

        final RSACrypto rsa = new RSACrypto(publicKey);
        final byte[] cryptedSessionKey = rsa.encrypt(sessionKey);

        final byte[] loginKey = new byte[20];
        System.arraycopy(webApiUserNonce.getBytes(), 0, loginKey, 0, webApiUserNonce.length());

        // aes encrypt the loginkey with our session key
        final byte[] cryptedLoginKey;
        try {
            cryptedLoginKey = CryptoHelper.symmetricEncrypt(loginKey, sessionKey);
        } catch (CryptoException e) {
            e.printStackTrace();
            return false;
        }

        final KeyValue authResult;

        final Map<String,String> args = new HashMap<>();
        args.put("steamid", String.valueOf(steamId));
        args.put("sessionkey", WebHelpers.urlEncode(cryptedSessionKey));
        args.put("encrypted_loginkey", WebHelpers.urlEncode(cryptedLoginKey));
        args.put("format", "vdf");

        try {
            authResult = api.authenticateUser(args).execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        if (authResult == null) {
            return false;
        }

        token = authResult.get("token").asString();
        tokenSecure = authResult.get("tokenSecure").asString();

        authenticated = true;

        /*
        final String pin = PrefsManager.getParentalPin().trim();
        if (!pin.isEmpty()) {
            // Unlock family view
            steamParental = unlockParental(pin);
        } */

        return true;
    }

    /**
     * Generate Steam web cookies
     * @return Map of the cookies
     */
    private Map<String,String> generateWebCookies() {
        if (!authenticated) {
            return new HashMap<>();
        }

        final Map<String, String> cookies = new HashMap<>();
        cookies.put("sessionid", sessionId);
        cookies.put("steamLogin", token);
        cookies.put("steamLoginSecure", tokenSecure);
        //final String sentryHash = PrefsManager.getSentryHash().trim();
        //Shouldn't need this as we aren't doing trading. TODO if this turns out to tradeban the account for a week.
        final String sentryHash = "";
        if (!sentryHash.isEmpty()) {
            cookies.put("steamMachineAuth" + steamId, sentryHash);
        }
        if (steamParental != null) {
            cookies.put("steamparental", steamParental);
        }

        return cookies;
    }
    
    //https://steamcommunity.com/my/inventoryhistory?start_time=&app[0]=440
    public long getInventoryHistory(long start_time, long stop_time) {
    	final String url = STEAM_COMMUNITY + "my/inventoryhistory?start_time=" + start_time + "&app[0]=440";
		try {
			Response response = Jsoup.connect(url)
					.ignoreContentType(true)
					.maxBodySize(2048 * 10240)
					.cookies(generateWebCookies())
					.method(Connection.Method.POST)
					.data("sessionid", sessionId)
			        .data("queuetype", "0")
			        .execute();
			long time = InventoryFilesManager.parseAndCreateDoc(response, start_time, stop_time);
			TimeUnit.SECONDS.sleep(timeout);
			return time;
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0l;
		}
    }

}
