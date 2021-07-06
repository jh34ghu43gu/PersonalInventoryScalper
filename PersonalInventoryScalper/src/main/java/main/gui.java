package main;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import in.dragonbra.javasteam.enums.EPersonaState;
import in.dragonbra.javasteam.enums.EResult;
import in.dragonbra.javasteam.steam.handlers.steamfriends.SteamFriends;
import in.dragonbra.javasteam.steam.handlers.steamfriends.callback.FriendMsgCallback;
import in.dragonbra.javasteam.steam.handlers.steamfriends.callback.FriendsListCallback;
import in.dragonbra.javasteam.steam.handlers.steamuser.LogOnDetails;
import in.dragonbra.javasteam.steam.handlers.steamuser.SteamUser;
import in.dragonbra.javasteam.steam.handlers.steamuser.callback.LoggedOffCallback;
import in.dragonbra.javasteam.steam.handlers.steamuser.callback.LoggedOnCallback;
import in.dragonbra.javasteam.steam.steamclient.SteamClient;
import in.dragonbra.javasteam.steam.steamclient.callbackmgr.CallbackManager;
import in.dragonbra.javasteam.steam.steamclient.callbacks.ConnectedCallback;
import in.dragonbra.javasteam.steam.steamclient.callbacks.DisconnectedCallback;

//CBA maybe I'll get this working one day.
public class gui extends JPanel implements ActionListener, Runnable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static String LOGIN = "Login";
	
	private JFrame mainFrame;
	private JButton submitButton;
	private JTextField usernameBox;
	private JTextField steamGuardBox;
	private JPasswordField passwordBox;
	
	private boolean loggedIn = false;
	private SteamClient steamClient;
	private SteamUser steamUser;
	private SteamFriends steamFriends;
	
	private String username;
	private String password;
	private String steamGuardCode;
	
	public void createGUI() {
		mainFrame = new JFrame("Inventory History Tool");
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setSize(500, 170);
		
		submitButton = new JButton("Login");
		submitButton.setActionCommand(LOGIN);
		submitButton.addActionListener(this);
		
		JLabel usernameLabel = new JLabel("Username: ");
		usernameBox = new JTextField(32);
		
		JLabel passwordLabel = new JLabel("Password: ");
		passwordBox = new JPasswordField(32);
		
		JLabel steamGuardLabel = new JLabel("SteamGuard: ");
		steamGuardBox = new JTextField(10);
		
		JPanel textPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		textPanel.add(usernameLabel);
		textPanel.add(usernameBox);
		textPanel.add(passwordLabel);
		textPanel.add(passwordBox);
		textPanel.add(steamGuardLabel);
		textPanel.add(steamGuardBox);
		textPanel.add(submitButton);
		
		mainFrame.add(textPanel);
		mainFrame.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		
		if(LOGIN.equals(cmd)) {
			username = usernameBox.getText();
			steamGuardCode = steamGuardBox.getText();
			password = parsePassword(passwordBox.getPassword());
			System.out.println("Info retrieved");
			
			if(!loggedIn) {
				this.run();
			} else {
				
			}
		}
		
	}
	
	private String parsePassword(char[] input) {
		String password = "";
		for(char c : input) {
			password += c;
		}
		return password;
	}
	
	public void run() {
		loggedIn = true; //No login button spam
		steamClient = new SteamClient();
		CallbackManager manager = new CallbackManager(steamClient);
		steamFriends = steamClient.getHandler(SteamFriends.class);
		steamUser = steamClient.getHandler(SteamUser.class);
		
		//Register events
		manager.subscribe(ConnectedCallback.class, this::onConnected);
		manager.subscribe(DisconnectedCallback.class, this::onDisconnected);
		
		manager.subscribe(FriendsListCallback.class, this::friendsList);
        manager.subscribe(FriendMsgCallback.class, this::friendMessage);

        manager.subscribe(LoggedOnCallback.class, this::onLoggedOn);
        manager.subscribe(LoggedOffCallback.class, this::onLoggedOff);
        
        System.out.println("Events registered, connecting...");
        steamClient.connect();
	}
	
	//===================================================
	//============= Steam Login Related =================
	//===================================================
	
	public void onConnected(ConnectedCallback callback) {
		System.out.println("Steam connection established, logging in...");
		
		LogOnDetails details = new LogOnDetails();
		details.setUsername(username);
		details.setPassword(password);
		details.setTwoFactorCode(steamGuardCode);
		
		details.setShouldRememberPassword(false);
		
		steamUser.logOn(details);
	}
	
	public void onDisconnected(DisconnectedCallback callback) {
		System.out.println("Disconnected from Steam, reconnecting in 5...");

        try {
            Thread.sleep(5000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        steamClient.connect();
	}
	
	public void onLoggedOn(LoggedOnCallback callback) {
		boolean steamGuardFail = (callback.getResult() == EResult.AccountLogonDenied);
		
		if(steamGuardFail) { //Probably if we generate a code during the last second we will run into a bad code
			System.out.println("Login failed with bad 2auth code, attempting again in 5 seconds...");
			steamClient.disconnect();
			return;
		}
		
		if(callback.getResult() != EResult.OK) { //Some other issue
			System.out.println("Login failed: " + callback.getResult() + "\n More Details: \n" + callback.getExtendedResult());
			loggedIn = false;
			steamClient.disconnect();
			return;
		}
		
		System.out.println("Login successful!");
		loggedIn = true;
		steamFriends.setPersonaState(EPersonaState.Online);
		//Startup actions go here if we have any later
	}
	
	public void onLoggedOff(LoggedOffCallback callback) {
		System.out.println("Logged off of Steam: " + callback.getResult());
        loggedIn = false;
	}
	
	//===================================================
	//=========== Steam Messages Related ================
	//===================================================
	
	
	//f.getSteamID().convertToUInt64()) - community id
	public void friendsList(FriendsListCallback callback) {
		System.out.println("FriendsListCallback event fired.");		
	}
	
	public void friendMessage(FriendMsgCallback callback) {
		System.out.println("Message callback");		
	}

}
