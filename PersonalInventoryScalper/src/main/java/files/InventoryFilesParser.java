package files;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ch.qos.logback.classic.Logger;
import utils.Utils;

public class InventoryFilesParser {
	
	private static final Logger log = (Logger) LoggerFactory.getLogger(InventoryFilesParser.class);
	private static String inventoryDirectory = "InventoryFiles";
	private static String directory = "Results";
	private static String mvmFileName = "mvm.txt";
	private static String surplusFileName = "squadSurplus.txt";
	private static String unboxFileName = "unboxes.txt";
	private static String dropsFileName = "itemDrops.txt";
	private static String storePurchaseFileName = "storePurchases.txt";
	
	public static void makeDir() {
		File dir = new File(directory);
		if(!dir.exists()) {
			log.info("Making " + directory + " directory.");
			dir.mkdir();
		}
	}
	
	public static boolean mvmFileExists() {
		File mvm = new File(directory + "/" + mvmFileName);
		return mvm.exists();
	}
	
	public static boolean surplusFileExists() {
		File surplus = new File(directory + "/" + surplusFileName);
		return surplus.exists();
	}
	
	public static boolean unboxFileExists() {
		File surplus = new File(directory + "/" + unboxFileName);
		return surplus.exists();
	}
	
	public static boolean dropsFileExists() {
		File surplus = new File(directory + "/" + dropsFileName);
		return surplus.exists();
	}
	
	public static boolean storePurchaseFileExists() {
		File surplus = new File(directory + "/" + storePurchaseFileName);
		return surplus.exists();
	}
	
	public boolean createMvmFile() {
		File invDir = new File(inventoryDirectory);
		File[] invFiles = invDir.listFiles();
		JSONParser parser = new JSONParser();
		JSONObject mvmObject = new JSONObject();
		HashMap<String, Integer> itemsGained = new HashMap<String, Integer>();
		HashMap<String, Integer> osItemsGained = new HashMap<String, Integer>();
		HashMap<String, Integer> stItemsGained = new HashMap<String, Integer>();
		HashMap<String, Integer> meItemsGained = new HashMap<String, Integer>();
		HashMap<String, Integer> tcItemsGained = new HashMap<String, Integer>();
		HashMap<String, Integer> ggItemsGained = new HashMap<String, Integer>();
		for(File f : invFiles) {
			try {
				JSONObject obj = (JSONObject) parser.parse(new FileReader(f));
				Set<String> set = obj.keySet();
				String operation = "";
				for(String s : set) {
					JSONObject trade = (JSONObject) obj.get(s);
					if(trade.get("event_description").equals("Played MvM Mann Up Mode")) {
						JSONObject plusObj = (JSONObject) trade.get("plus");
						Set<String> tradeSet = plusObj.keySet();
						for(String s2 : tradeSet) {
							JSONObject item = (JSONObject) plusObj.get(s2);
							String itemName = (String) item.get("itemName");
							if(itemName.startsWith("Operation")) {
								operation = itemName;
							}
							if(itemsGained.containsKey(itemName)) { //Already in map, iterate
								int amt = itemsGained.get(itemName)+1;
								itemsGained.put(itemName, amt);
							} else { //Not in map, add it
								itemsGained.put(itemName, 1);
							}
						}
						if(operation.equalsIgnoreCase("Operation Oil Spill Badge")) {
							for(Map.Entry<String, Integer> entry : itemsGained.entrySet()) {
								if(osItemsGained.containsKey(entry.getKey())) { //Already in map, iterate
									int amt = osItemsGained.get(entry.getKey())+1;
									osItemsGained.put(entry.getKey(), amt);
								} else { //Not in map, add it
									osItemsGained.put(entry.getKey(), entry.getValue());
								}
							}
						} else if(operation.equalsIgnoreCase("Operation Steel Trap Badge")) {
							for(Map.Entry<String, Integer> entry : itemsGained.entrySet()) {
								if(stItemsGained.containsKey(entry.getKey())) { //Already in map, iterate
									int amt = stItemsGained.get(entry.getKey())+1;
									stItemsGained.put(entry.getKey(), amt);
								} else { //Not in map, add it
									stItemsGained.put(entry.getKey(), entry.getValue());
								}
							}
						} else if(operation.equalsIgnoreCase("Operation Mecha Engine Badge")) {
							for(Map.Entry<String, Integer> entry : itemsGained.entrySet()) {
								if(meItemsGained.containsKey(entry.getKey())) { //Already in map, iterate
									int amt = meItemsGained.get(entry.getKey())+1;
									meItemsGained.put(entry.getKey(), amt);
								} else { //Not in map, add it
									meItemsGained.put(entry.getKey(), entry.getValue());
								}
							}
						} else if(operation.equalsIgnoreCase("Operation Two Cities Badge")) {
							for(Map.Entry<String, Integer> entry : itemsGained.entrySet()) {
								if(tcItemsGained.containsKey(entry.getKey())) { //Already in map, iterate
									int amt = tcItemsGained.get(entry.getKey())+1;
									tcItemsGained.put(entry.getKey(), amt);
								} else { //Not in map, add it
									tcItemsGained.put(entry.getKey(), entry.getValue());
								}
							}
						} else if(operation.equalsIgnoreCase("Operation Gear Grinder Badge")) {
							for(Map.Entry<String, Integer> entry : itemsGained.entrySet()) {
								if(ggItemsGained.containsKey(entry.getKey())) { //Already in map, iterate
									int amt = ggItemsGained.get(entry.getKey())+1;
									ggItemsGained.put(entry.getKey(), amt);
								} else { //Not in map, add it
									ggItemsGained.put(entry.getKey(), entry.getValue());
								}
							}
						}
						itemsGained.clear(); //Clear map for next loop
					}
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}
		//Create objects and put into the mvm object
		JSONObject os = new JSONObject();
		for(Map.Entry<String, Integer> entry : osItemsGained.entrySet()) {
			os.put(entry.getKey(), entry.getValue());
		}
		mvmObject.put("Oil Spill", os);
		JSONObject st = new JSONObject();
		for(Map.Entry<String, Integer> entry : stItemsGained.entrySet()) {
			st.put(entry.getKey(), entry.getValue());
		}
		mvmObject.put("Steel Trap", st);
		JSONObject me = new JSONObject();
		for(Map.Entry<String, Integer> entry : meItemsGained.entrySet()) {
			me.put(entry.getKey(), entry.getValue());
		}
		mvmObject.put("Mecha Engine", me);
		JSONObject tc = new JSONObject();
		for(Map.Entry<String, Integer> entry : tcItemsGained.entrySet()) {
			tc.put(entry.getKey(), entry.getValue());
		}
		mvmObject.put("Two Cities", tc);
		JSONObject gg = new JSONObject();
		for(Map.Entry<String, Integer> entry : ggItemsGained.entrySet()) {
			gg.put(entry.getKey(), entry.getValue());
		}
		mvmObject.put("Gear Grinder", gg);
		
		//Make file
		try {
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String s = gson.toJson(mvmObject);
			FileWriter writer = new FileWriter(directory + "/" + mvmFileName);
			writer.write(s);
			writer.flush();
			writer.close();
			log.info("Created mvm file.");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean createSurplusFile() {
		File invDir = new File(inventoryDirectory);
		File[] invFiles = invDir.listFiles();
		JSONParser parser = new JSONParser();
		JSONObject surplusObject = new JSONObject();
		HashMap<String, Integer> itemsGained = new HashMap<String, Integer>();
		for(File f : invFiles) {
			try {
				JSONObject obj = (JSONObject) parser.parse(new FileReader(f));
				Set<String> set = obj.keySet();
				for(String s : set) {
					JSONObject trade = (JSONObject) obj.get(s);
					if(trade.get("event_description").equals("MvM Squad Surplus bonus")) {
						JSONObject plusObj = (JSONObject) trade.get("plus");
						Set<String> tradeSet = plusObj.keySet();
						for(String s2 : tradeSet) {
							JSONObject item = (JSONObject) plusObj.get(s2);
							String itemName = (String) item.get("itemName");
							if(itemsGained.containsKey(itemName)) { //Already in map, iterate
								int amt = itemsGained.get(itemName)+1;
								itemsGained.put(itemName, amt);
							} else { //Not in map, add it
								itemsGained.put(itemName, 1);
							}
						}
					}
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}
		for(Map.Entry<String, Integer> entry : itemsGained.entrySet()) {
			surplusObject.put(entry.getKey(), entry.getValue());
		}
		try {
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String s = gson.toJson(surplusObject);
			FileWriter writer = new FileWriter(directory + "/" + surplusFileName);
			writer.write(s);
			writer.flush();
			writer.close();
			log.info("Created squad surplus file.");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean createUnboxFile() {
		File invDir = new File(inventoryDirectory);
		File[] invFiles = invDir.listFiles();
		JSONParser parser = new JSONParser();
		JSONObject unboxObject = new JSONObject();
		
		JSONObject giftTemp = new JSONObject();
		for(File f : invFiles) {
			try {
				JSONObject obj = (JSONObject) parser.parse(new FileReader(f));
				Set<String> set = obj.keySet();
				String tradeStr;
				for(int i = set.size()-1; i >= 0; i--) { //read in reverse order to go chronologically
					tradeStr = ("trade" + String.valueOf(i));
					JSONObject trade = (JSONObject) obj.get(tradeStr);
					//Unlocked class crates are special
					if(trade.get("event_description").equals("Received a gift")) {
						//log.info("giftTemp made for " + ((JSONObject)trade.get("plus")).get("item0").toString());
						giftTemp = trade;
						continue;
					}
					if(trade.get("event_description").equals("Used")) {
						if(!giftTemp.isEmpty()) { //Unlocked crate 99%
							unboxObject = this.parseUnboxObj(giftTemp, unboxObject, trade, true);
						}
					}
					//Normal crates
					if(trade.get("event_description").equals("Unlocked a crate")) {
						unboxObject = this.parseUnboxObj(trade, unboxObject, trade, false);
					}
					//Clear gift obj if we didn't just set it
					giftTemp = new JSONObject();
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}
		try {
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String s = gson.toJson(unboxObject);
			FileWriter writer = new FileWriter(directory + "/" + unboxFileName);
			writer.write(s);
			writer.flush();
			writer.close();
			log.info("Created unboxe file.");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private JSONObject parseUnboxObj(JSONObject unboxEvent, JSONObject resultObject, JSONObject optionalUsedEvent, Boolean usedEvent) {
		JSONObject plus = (JSONObject) unboxEvent.get("plus");
		HashMap<String, Integer> plusMap = new HashMap<String, Integer>();
		HashMap<String, Integer> minusMap = new HashMap<String, Integer>();
		HashMap<String, Integer> colorMap = new HashMap<String, Integer>();
		HashMap<String, Integer> rarityMap = new HashMap<String, Integer>();
		JSONObject crateObj = new JSONObject(); //Contains total, minusItems, plusItems, colors
		JSONObject minusItems = new JSONObject();
		JSONObject plusItems = new JSONObject();
		JSONObject colors = new JSONObject();
		JSONObject rarity = new JSONObject();
		String crateName = "Unknown";
		//Unlocked crate
		if(usedEvent) {
			JSONObject usedObj = (JSONObject) optionalUsedEvent.get("minus");
			crateName = (String) ((JSONObject)usedObj.get("item0")).get("itemName");
			if(resultObject.containsKey(crateName)) {
				plusItems = (JSONObject) ((JSONObject) resultObject.get(crateName)).get("plus");
				minusItems = (JSONObject) ((JSONObject) resultObject.get(crateName)).get("minus");
				if(plusItems.containsKey("colors")) {
					colors = (JSONObject) plusItems.get("colors");
				}
				if(plusItems.containsKey("rarities")) {
					rarity = (JSONObject) plusItems.get("rarities");
				}
			}
			Set<String> items = plus.keySet();
			for(String s : items) {
				JSONObject itemObj = (JSONObject) plus.get(s);
				String itemName = (String) itemObj.get("itemName");
				String noQualityItemName = itemName;
				if(noQualityItemName.startsWith("Strange ")) {
					noQualityItemName = noQualityItemName.substring(8);
				}
				if(noQualityItemName.startsWith("Unusual ")) {
					noQualityItemName = noQualityItemName.substring(8);
				}
				if(noQualityItemName.startsWith("The ")) {
					noQualityItemName = noQualityItemName.substring(4);
				}
				String color = (String) itemObj.get("color");
				if(plusMap.containsKey(itemName)) {
					int amt = plusMap.get(itemName) + 1;
					plusMap.put(itemName, amt);
				} else {
					plusMap.put(itemName, 1);
				}
				if(colorMap.containsKey(color)) {
					int amt = colorMap.get(color) + 1;
					colorMap.put(color, amt);
				} else {
					colorMap.put(color, 1);
				}
				HashMap<String, String> rarities = InventoryFilesParser.getCaseRarities(crateName);
				if(!rarities.isEmpty() && rarities.containsKey(noQualityItemName)) {
					String rarityVal = rarities.get(noQualityItemName);
					if(rarityMap.containsKey(rarityVal)) {
						int amt = rarityMap.get(rarityVal) + 1;
						rarityMap.put(rarityVal, amt);
					} else {
						rarityMap.put(rarityVal, 1);
					}
				}
			}
			for(Map.Entry<String, Integer> entry : plusMap.entrySet()) {
				int amt = entry.getValue();
				if(plusItems.containsKey(entry.getKey())) {
					amt += (int) plusItems.get(entry.getKey());
					plusItems.replace(entry.getKey(), amt);
				} else {
					plusItems.put(entry.getKey(), amt);
				}
			}
			for(Map.Entry<String, Integer> entry : colorMap.entrySet()) {
				int amt = entry.getValue();
				if(colors.containsKey(entry.getKey())) {
					amt += (int) colors.get(entry.getKey());
					colors.replace(entry.getKey(), amt);
				} else {
					colors.put(entry.getKey(), amt);
				}
			}
			for(Map.Entry<String, Integer> entry : rarityMap.entrySet()) {
				int amt = entry.getValue();
				if(rarity.containsKey(entry.getKey())) {
					amt += (int) rarity.get(entry.getKey());
					rarity.replace(entry.getKey(), amt);
				} else {
					rarity.put(entry.getKey(), amt);
				}
			}
		} else { //Normal crate
			JSONObject minus = (JSONObject) unboxEvent.get("minus");
			//Minus object loop
			Set<String> items = minus.keySet();
			for(String s : items) {
				JSONObject itemObj = (JSONObject) minus.get(s);
				String itemName = (String) itemObj.get("itemName");
				if(itemName.contains("Key") && !itemName.contains("Keyless")) {
					minusMap.put(itemName, 1);
				} else {
					crateName = itemName;
					//total tracks this don't need to add to map
				}
			}
			if(resultObject.containsKey(crateName)) {
				plusItems = (JSONObject) ((JSONObject) resultObject.get(crateName)).get("plus");
				minusItems = (JSONObject) ((JSONObject) resultObject.get(crateName)).get("minus");
				if(plusItems.containsKey("colors")) {
					colors = (JSONObject) plusItems.get("colors");
				}
				if(plusItems.containsKey("rarities")) {
					rarity = (JSONObject) plusItems.get("rarities");
				}
			}
			//Plus object loop
			HashMap<String, String> rarities = InventoryFilesParser.getCaseRarities(crateName);
			items = plus.keySet();
			for(String s : items) {
				JSONObject itemObj = (JSONObject) plus.get(s);
				String itemName = (String) itemObj.get("itemName");
				String noQualityItemName = itemName;
				if(noQualityItemName.startsWith("Strange ")) {
					noQualityItemName = noQualityItemName.substring(8);
				}
				if(noQualityItemName.startsWith("Unusual ")) {
					noQualityItemName = noQualityItemName.substring(8);
				}
				if(noQualityItemName.startsWith("The ")) {
					noQualityItemName = noQualityItemName.substring(4);
				}
				
				String color = (String) itemObj.get("color");
				if(plusMap.containsKey(itemName)) {
					int amt = plusMap.get(itemName) + 1;
					plusMap.put(itemName, amt);
				} else {
					plusMap.put(itemName, 1);
				}
				if(colorMap.containsKey(color)) {
					int amt = colorMap.get(color) + 1;
					colorMap.put(color, amt);
				} else {
					colorMap.put(color, 1);
				}
				if(!rarities.isEmpty() && rarities.containsKey(noQualityItemName)) {
					String rarityVal = rarities.get(noQualityItemName);
					if(rarityMap.containsKey(rarityVal)) {
						int amt = rarityMap.get(rarityVal) + 1;
						rarityMap.put(rarityVal, amt);
					} else {
						rarityMap.put(rarityVal, 1);
					}
				} else if(!rarities.isEmpty()) {
					boolean doWarn = true;
					//Manual ones that java can't figure out
					if(noQualityItemName.endsWith("Polished War Paint")) {
						String rarityVal = "commando";
						if(rarityMap.containsKey(rarityVal)) {
							int amt = rarityMap.get(rarityVal) + 1;
							rarityMap.put(rarityVal, amt);
						} else {
							rarityMap.put(rarityVal, 1);
						}
						doWarn = false;
					}
					//Do a warning for future problems
					String[] blacklist = {
							"Name Tag", "Description Tag", "Part: ", "Count Transfer Tool",
							"Festivizer", "Backpack Expander", "Tour of Duty Ticket", "Taunt: ",
							"Gift Wrap", "Decal Tool", "Muskelmannbraun", "Dueling Mini-Game",
							"A Mann's Mint", "Giftapult", "Mann Co. Orange", "Noble Hatter's Violet",
							"Color No. 216-190-216", "An Air of Debonair", "Ye Olde Rustic Colour",
							"Bitter Taste of Defeat and Lime", "Drably Olive", "Balaclavas Are Forever",
							"Indubitably Green", "Team Spirit", "After Eight", "Aged Moustache Grey",
							"Radigan Conagher Brown", "Value of Teamwork", "Peculiarly Drab Tincture",
							"Color of a Gentlemann's Business Pants", "Waterlogged Lab Coat",
							"Zepheniah's Greed", "A Deep Commitment to Purple", "An Extraordinary Abundance of Tinge",
							"Australium Gold", "Pug Mug", "Rolfe Copter", "Mannvich",
							"A Distinctive Lack of Hue", "Dark Salmon Injustice", "Eingineer",
							"Crocodile Mun-Dee", "Scoper's Scales", "Dell in the Shell",
							"A Shell of a Mann", "Aerobatics Demonstrator", "Pink as Hell",
							"Remorseless Raptor", "Final Frontier Freighter", "Avian Amante",
							"Operator's Overalls", "Hovering Hotshot", "War Blunder", "Wild Whip"
					};
					for(String b : blacklist) {
						if(noQualityItemName.startsWith(b)) {
							doWarn = false;
							break;
						}
					}
					if(doWarn) {
						log.warn("Item should have a rarity but could not be matched! " + noQualityItemName);
				
					}
				}
			}
			//Map -> obj loops
			for(Map.Entry<String, Integer> entry : plusMap.entrySet()) {
				int amt = entry.getValue();
				if(plusItems.containsKey(entry.getKey())) {
					amt += (int) plusItems.get(entry.getKey());
					plusItems.replace(entry.getKey(), amt);
				} else {
					plusItems.put(entry.getKey(), amt);
				}
			}
			for(Map.Entry<String, Integer> entry : colorMap.entrySet()) {
				int amt = entry.getValue();
				if(colors.containsKey(entry.getKey())) {
					amt += (int) colors.get(entry.getKey());
					colors.replace(entry.getKey(), amt);
				} else {
					colors.put(entry.getKey(), amt);
				}
			}
			for(Map.Entry<String, Integer> entry : minusMap.entrySet()) { //Overkill for 1 item but maybe there's a chance 2 items got used
				int amt = entry.getValue();
				if(minusItems.containsKey(entry.getKey())) {
					amt += (int) minusItems.get(entry.getKey());
					minusItems.replace(entry.getKey(), amt);
				} else {
					minusItems.put(entry.getKey(), amt);
				}
			}
			for(Map.Entry<String, Integer> entry : rarityMap.entrySet()) {
				int amt = entry.getValue();
				if(rarity.containsKey(entry.getKey())) {
					amt += (int) rarity.get(entry.getKey());
					rarity.replace(entry.getKey(), amt);
				} else {
					rarity.put(entry.getKey(), amt);
				}
			}
		}
		ArrayList<String> blacklist = new ArrayList<String>();
		blacklist.add("Smissmas 2015 Festive Gift");
		blacklist.add("Mann Co. Store Package");
		blacklist.add("Secret Saxton");
		if(blacklist.contains(crateName)) {
			return resultObject;
		}
		plusItems.put("colors", colors);
		plusItems.put("rarities", rarity);
		crateObj.put("plus", plusItems);
		crateObj.put("minus", minusItems);
		if(resultObject.containsKey(crateName)) { //Has the crate, add to it
			int amt = (int) ((JSONObject) resultObject.get(crateName)).get("total") + 1;
			crateObj.put("total", amt);
			resultObject.replace(crateName, crateObj);
		} else { //Doesn't already have this crate
			crateObj.put("total", 1);
			resultObject.put(crateName, crateObj);
		}
		
		return resultObject;
	}
	
	private static HashMap<String, String> getCaseRarities(String caseName) {
		String fileName = "tf2cases.json";
		HashMap<String, String> out = new HashMap<String, String>();
		ArrayList<String> rarities = new ArrayList<String>();
		rarities.add("civilian");
		rarities.add("freelance");
		rarities.add("mercenary");
		rarities.add("commando");
		rarities.add("assassin");
		rarities.add("elite");
		JSONParser parser = new JSONParser();
		
		try {
			JSONObject cases = (JSONObject) parser.parse(new FileReader(fileName));
			if(!cases.containsKey(caseName)) { //Leave early if case doesn't exist
				return out;
			}
			JSONObject caseObj = (JSONObject) cases.get(caseName);
			for(String s : rarities) {
				JSONArray r = (JSONArray) caseObj.get(s);
				for(int i = 0; i < r.size(); i++) {
					out.put((String)r.get(i), s);
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return out;
	}
	
	public boolean createDropsFile() {
		File invDir = new File(inventoryDirectory);
		File[] invFiles = invDir.listFiles();
		JSONParser parser = new JSONParser();
		JSONObject dropsObject = new JSONObject();
		HashMap<String, Integer> itemsGained = new HashMap<String, Integer>();
		for(File f : invFiles) {
			try {
				JSONObject obj = (JSONObject) parser.parse(new FileReader(f));
				Set<String> set = obj.keySet();
				for(String s : set) {
					JSONObject trade = (JSONObject) obj.get(s);
					if(trade.get("event_description").equals("Found")) {
						JSONObject plusObj = (JSONObject) trade.get("plus");
						Set<String> tradeSet = plusObj.keySet();
						for(String s2 : tradeSet) {
							JSONObject item = (JSONObject) plusObj.get(s2);
							String itemName = (String) item.get("itemName");
							if(itemsGained.containsKey(itemName)) { //Already in map, iterate
								int amt = itemsGained.get(itemName)+1;
								itemsGained.put(itemName, amt);
							} else { //Not in map, add it
								itemsGained.put(itemName, 1);
							}
						}
					}
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}
		for(Map.Entry<String, Integer> entry : itemsGained.entrySet()) {
			dropsObject.put(entry.getKey(), entry.getValue());
		}
		try {
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String s = gson.toJson(dropsObject);
			FileWriter writer = new FileWriter(directory + "/" + dropsFileName);
			writer.write(s);
			writer.flush();
			writer.close();
			log.info("Created item drops file.");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean createStorePurchaseFile() {
		File invDir = new File(inventoryDirectory);
		File[] invFiles = invDir.listFiles();
		JSONParser parser = new JSONParser();
		JSONObject purchaseObject = new JSONObject();
		HashMap<String, Integer> itemsGained = new HashMap<String, Integer>();
		for(File f : invFiles) {
			try {
				JSONObject obj = (JSONObject) parser.parse(new FileReader(f));
				Set<String> set = obj.keySet();
				for(String s : set) {
					JSONObject trade = (JSONObject) obj.get(s);
					if(trade.get("event_description").equals("Purchased from the store")) {
						JSONObject plusObj = (JSONObject) trade.get("plus");
						Set<String> tradeSet = plusObj.keySet();
						for(String s2 : tradeSet) {
							JSONObject item = (JSONObject) plusObj.get(s2);
							String itemName = (String) item.get("itemName");
							if(itemsGained.containsKey(itemName)) { //Already in map, iterate
								int amt = itemsGained.get(itemName)+1;
								itemsGained.put(itemName, amt);
							} else { //Not in map, add it
								itemsGained.put(itemName, 1);
							}
						}
					}
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}
		for(Map.Entry<String, Integer> entry : itemsGained.entrySet()) {
			purchaseObject.put(entry.getKey(), entry.getValue());
		}
		try {
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String s = gson.toJson(purchaseObject);
			FileWriter writer = new FileWriter(directory + "/" + storePurchaseFileName);
			writer.write(s);
			writer.flush();
			writer.close();
			log.info("Created store purchases file.");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public void outputMvmFile() {
		if(!mvmFileExists()) { 
			log.warn("Could not read mvm file, it does not exist!");
			return;
		}
		JSONParser parser = new JSONParser();
		//Lists of items
		//2c
		ArrayList<String> pros = new ArrayList<String>();
		HashMap<String, Long> prosMap = new HashMap<String, Long>();
		ArrayList<String> specs = new ArrayList<String>();
		HashMap<String, Long> specsMap = new HashMap<String, Long>();
		ArrayList<String> normals = new ArrayList<String>();
		HashMap<String, Long> normalsMap = new HashMap<String, Long>();
		
		ArrayList<String> pristine = new ArrayList<String>();
		HashMap<String, Long> pristineMap = new HashMap<String, Long>();
		ArrayList<String> battleWorn = new ArrayList<String>();
		HashMap<String, Long> battleWornMap = new HashMap<String, Long>();
		ArrayList<String> reinforced = new ArrayList<String>();
		HashMap<String, Long> reinforcedMap = new HashMap<String, Long>();
		
		ArrayList<String> tcWeapons = new ArrayList<String>();
		HashMap<String, Long> tcWeaponsMap = new HashMap<String, Long>();
		ArrayList<String> tcItems = new ArrayList<String>();
		HashMap<String, Long> tcItemsMap = new HashMap<String, Long>();
		ArrayList<String> tcAussies = new ArrayList<String>();
		HashMap<String, Long> tcAussiesMap = new HashMap<String, Long>();
		//Botkillers
		ArrayList<String> rust = new ArrayList<String>();
		HashMap<String, Long> rustMap = new HashMap<String, Long>();
		ArrayList<String> blood = new ArrayList<String>();
		HashMap<String, Long> bloodMap = new HashMap<String, Long>();
		
		ArrayList<String> silverI = new ArrayList<String>();
		HashMap<String, Long> silverIMap = new HashMap<String, Long>();
		ArrayList<String> goldI = new ArrayList<String>();
		HashMap<String, Long> goldIMap = new HashMap<String, Long>();
		
		ArrayList<String> silverII = new ArrayList<String>();
		HashMap<String, Long> silverIIMap = new HashMap<String, Long>();
		ArrayList<String> goldII = new ArrayList<String>();
		HashMap<String, Long> goldIIMap = new HashMap<String, Long>();
		
		ArrayList<String> carbonado = new ArrayList<String>();
		HashMap<String, Long> carbonadoMap = new HashMap<String, Long>();
		ArrayList<String> diamond = new ArrayList<String>();
		HashMap<String, Long> diamondMap = new HashMap<String, Long>();
		//All
		ArrayList<String> weapons = new ArrayList<String>();
		HashMap<String, Long> weaponsMap = new HashMap<String, Long>();
		ArrayList<String> items = new ArrayList<String>();
		HashMap<String, Long> itemsMap = new HashMap<String, Long>();
		ArrayList<String> aussies = new ArrayList<String>();
		HashMap<String, Long> aussiesMap = new HashMap<String, Long>();
		//OS all
		ArrayList<String> osWeapons = new ArrayList<String>();
		HashMap<String, Long> osWeaponsMap = new HashMap<String, Long>();
		ArrayList<String> osItems = new ArrayList<String>();
		HashMap<String, Long> osItemsMap = new HashMap<String, Long>();
		//ST all
		ArrayList<String> stWeapons = new ArrayList<String>();
		HashMap<String, Long> stWeaponsMap = new HashMap<String, Long>();
		ArrayList<String> stItems = new ArrayList<String>();
		HashMap<String, Long> stItemsMap = new HashMap<String, Long>();
		ArrayList<String> stAussies = new ArrayList<String>();
		HashMap<String, Long> stAussiesMap = new HashMap<String, Long>();
		//ME all
		ArrayList<String> meWeapons = new ArrayList<String>();
		HashMap<String, Long> meWeaponsMap = new HashMap<String, Long>();
		ArrayList<String> meItems = new ArrayList<String>();
		HashMap<String, Long> meItemsMap = new HashMap<String, Long>();
		ArrayList<String> meAussies = new ArrayList<String>();
		HashMap<String, Long> meAussiesMap = new HashMap<String, Long>();
		//GG all
		ArrayList<String> ggWeapons = new ArrayList<String>();
		HashMap<String, Long> ggWeaponsMap = new HashMap<String, Long>();
		ArrayList<String> ggItems = new ArrayList<String>();
		HashMap<String, Long> ggItemsMap = new HashMap<String, Long>();
		ArrayList<String> ggAussies = new ArrayList<String>();
		HashMap<String, Long> ggAussiesMap = new HashMap<String, Long>();
		int missions = 0; //CBA individual missions, looking at badges already tells you that
		try {
			JSONObject obj = (JSONObject) parser.parse(new FileReader(directory + "/" + mvmFileName));
			JSONObject weaponsObj = (JSONObject) parser.parse(new FileReader("tf2weapons.json"));
			JSONArray weaponsJSONArr = (JSONArray) weaponsObj.get("weapons");
			ArrayList<String> weaponsArr = new ArrayList<String>();
			for (int i = 0; i < weaponsJSONArr.size(); i++) {
				JSONObject wep = (JSONObject) weaponsJSONArr.get(i);
				String wepStr = wep.values().toString().replace('[', ' ');
				wepStr = wepStr.replace(']', ' ');
				wepStr = wepStr.trim();
				weaponsArr.add(wepStr);
			}
			Set<String> set = obj.keySet();
			for(String tourString : set) {
				JSONObject tour = (JSONObject) obj.get(tourString);
				Set<String> tourSet = tour.keySet();
				for(String s : tourSet) {
					if(s.startsWith("Killstreak")) {
						long amt = (long) tour.get(s);
						for(int i = 0; i < amt; i++) {
							normals.add(s);
						}
						normalsMap.put(s, amt);
					} else if(s.startsWith("Specialized")) {
						long amt = (long) tour.get(s);
						for(int i = 0; i < amt; i++) {
							specs.add(s);
						}
						specsMap.put(s, amt);
					} else if(s.startsWith("Professional")) {
						long amt = (long) tour.get(s);
						for(int i = 0; i < amt; i++) {
							pros.add(s);
						}
						prosMap.put(s, amt);
					} else if(s.startsWith("Pristine")) {
						long amt = (long) tour.get(s);
						for(int i = 0; i < amt; i++) {
							pristine.add(s);
						}
						pristineMap.put(s, amt);
					} else if(s.startsWith("Battle-Worn")) {
						long amt = (long) tour.get(s);
						for(int i = 0; i < amt; i++) {
							battleWorn.add(s);
						}
						battleWornMap.put(s, amt);
					} else if(s.startsWith("Reinforced")) {
						long amt = (long) tour.get(s);
						for(int i = 0; i < amt; i++) {
							reinforced.add(s);
						}
						reinforcedMap.put(s, amt);
					} else if(s.startsWith("Strange Australium")) {
						long amt = (long) tour.get(s);
						for(int i = 0; i < amt; i++) {
							aussies.add(s);
							if(tourString.equalsIgnoreCase("Steel Trap")) {
								stAussies.add(s);
							} else if(tourString.equalsIgnoreCase("Mecha Engine")) {
								meAussies.add(s);
							} else if(tourString.equalsIgnoreCase("Two Cities")) {
								tcAussies.add(s);
							} else if(tourString.equalsIgnoreCase("Gear Grinder")) {
								ggAussies.add(s);
							}
						}
						//Add to individual tours map
						if(tourString.equalsIgnoreCase("Steel Trap")) {
							stAussiesMap.put(s, amt);
						} else if(tourString.equalsIgnoreCase("Mecha Engine")) {
							meAussiesMap.put(s, amt);
						} else if(tourString.equalsIgnoreCase("Two Cities")) {
							tcAussiesMap.put(s, amt);
						} else if(tourString.equalsIgnoreCase("Gear Grinder")) {
							ggAussiesMap.put(s, amt);
						}
						//Overall total need logic to add to existing amts
						if(aussiesMap.containsKey(s)) { //Already in map, add
							amt = aussiesMap.get(s) + amt;
							aussiesMap.put(s, amt);
						} else { //Not in map, add it
							aussiesMap.put(s, amt);
						}
					} else if(s.startsWith("Strange Rust")) {
						long amt = (long) tour.get(s);
						for(int i = 0; i < amt; i++) {
							rust.add(s);
						}
						rustMap.put(s, amt);
					} else if(s.startsWith("Strange Blood")) {
						long amt = (long) tour.get(s);
						for(int i = 0; i < amt; i++) {
							blood.add(s);
						}
						bloodMap.put(s, amt);
					} else if(s.startsWith("Strange Silver")) {
						boolean mkI = s.endsWith("Mk.I");
						long amt = (long) tour.get(s);
						for(int i = 0; i < amt; i++) {
							if(mkI) {
								silverI.add(s);
							} else {
								silverII.add(s);
							}
						}
						if(mkI) {
							silverIMap.put(s, amt);
						} else {
							silverIIMap.put(s, amt);
						}
					} else if(s.startsWith("Strange Gold")) {
						boolean mkI = s.endsWith("Mk.I");
						long amt = (long) tour.get(s);
						for(int i = 0; i < amt; i++) {
							if(mkI) {
								goldI.add(s);
							} else {
								goldII.add(s);
							}
						}
						if(mkI) {
							goldIMap.put(s, amt);
						} else {
							goldIIMap.put(s, amt);
						}
					} else if(s.startsWith("Strange Carbonado")) {
						long amt = (long) tour.get(s);
						for(int i = 0; i < amt; i++) {
							carbonado.add(s);
						}
						carbonadoMap.put(s, amt);
					} else if(s.startsWith("Strange Diamond")) {
						long amt = (long) tour.get(s);
						for(int i = 0; i < amt; i++) {
							diamond.add(s);
						}
						diamondMap.put(s, amt);
					} else if(s.startsWith("Operation")) {
						long amt = (long) tour.get(s);
						missions += amt;
					} else {
						String name = s;
						if(name.startsWith("The")) {
							name = name.substring(3).trim();
						} else {
							//log.info(name);
						}
						
						//Doesn't like the special punctuation so special case, idk why cloak doesn't work and CBA
						if(weaponsArr.contains(name) || name.startsWith("Claidheamh") || name.startsWith("Cloak and Dagger")) {
							//Is weapon
							long amt = (long) tour.get(s);
							for(int i = 0; i < amt; i++) {
								weapons.add(s);
								if(tourString.equalsIgnoreCase("Oil Spill")) {
									osWeapons.add(s);
								} else if(tourString.equalsIgnoreCase("Steel Trap")) {
									stWeapons.add(s);
								} else if(tourString.equalsIgnoreCase("Mecha Engine")) {
									meWeapons.add(s);
								} else if(tourString.equalsIgnoreCase("Two Cities")) {
									tcWeapons.add(s);
								} else if(tourString.equalsIgnoreCase("Gear Grinder")) {
									ggWeapons.add(s);
								}
							}
							//Add to individual tours map
							if(tourString.equalsIgnoreCase("Oil Spill")) {
								osWeaponsMap.put(s, amt);
							} else if(tourString.equalsIgnoreCase("Steel Trap")) {
								stWeaponsMap.put(s, amt);
							} else if(tourString.equalsIgnoreCase("Mecha Engine")) {
								meWeaponsMap.put(s, amt);
							} else if(tourString.equalsIgnoreCase("Two Cities")) {
								tcWeaponsMap.put(s, amt);
							} else if(tourString.equalsIgnoreCase("Gear Grinder")) {
								ggWeaponsMap.put(s, amt);
							}
							//Overall total need logic to add to existing amts
							if(weaponsMap.containsKey(s)) { //Already in map, add
								amt = weaponsMap.get(s) + amt;
								weaponsMap.put(s, amt);
							} else { //Not in map, add it
								weaponsMap.put(s, amt);
							}
						} else {
							//Is item
							long amt = (long) tour.get(s);
							for(int i = 0; i < amt; i++) {
								items.add(s);
								if(tourString.equalsIgnoreCase("Oil Spill")) {
									osItems.add(s);
								} else if(tourString.equalsIgnoreCase("Steel Trap")) {
									stItems.add(s);
								} else if(tourString.equalsIgnoreCase("Mecha Engine")) {
									meItems.add(s);
								} else if(tourString.equalsIgnoreCase("Two Cities")) {
									tcItems.add(s);
								} else if(tourString.equalsIgnoreCase("Gear Grinder")) {
									ggItems.add(s);
								}
							}
							//Add to individual tours map
							if(tourString.equalsIgnoreCase("Oil Spill")) {
								osItemsMap.put(s, amt);
							} else if(tourString.equalsIgnoreCase("Steel Trap")) {
								stItemsMap.put(s, amt);
							} else if(tourString.equalsIgnoreCase("Mecha Engine")) {
								meItemsMap.put(s, amt);
							} else if(tourString.equalsIgnoreCase("Two Cities")) {
								tcItemsMap.put(s, amt);
							} else if(tourString.equalsIgnoreCase("Gear Grinder")) {
								ggItemsMap.put(s, amt);
							}
							//Overall total need logic to add to existing amts
							if(itemsMap.containsKey(s)) { //Already in map, add
								amt = itemsMap.get(s) + amt;
								itemsMap.put(s, amt);
							} else { //Not in map, add it
								itemsMap.put(s, amt);
							}
						}
					}
				}
			}
			String out = "MvM Loot Results: \n"
					+ "\tOverall: \n"
						+ "\t\tMissions: " + missions + "\n"
						+ "\t\t[2]Australiums: " + aussies.size() + "\n"
						+ "\t\t[3]Weapons: " + weapons.size() + "\n"
						+ "\t\t[4]Other (hats/paints/etc...): " + items.size() + "\n"
					+ "\tOil Spill:\n"
						+ "\t\t[5]Weapons: " + osWeapons.size() + "\n"
						+ "\t\t[6]Other (hats/paints/etc...): " + osItems.size() + "\n"
						+ "\t\t[7]Rust botkillers: " + rust.size() + "\n"
						+ "\t\t[8]Blood botkillers: " + blood.size() + "\n"
					+ "\tSteel Trap:\n"
						+ "\t\t[9]Weapons: " + stWeapons.size() + "\n"
						+ "\t\t[10]Other (hats/paints/etc...): " + stItems.size() + "\n"
						+ "\t\t[11]Silver Mk.I botkillers: " + silverI.size() + "\n"
						+ "\t\t[12]Gold Mk.I botkillers: " + goldI.size() + "\n"
						+ "\t\t[13]Australiums: " + stAussies.size() + "\n"
					+ "\tMecha Engine:\n"
						+ "\t\t[14]Weapons: " + meWeapons.size() + "\n"
						+ "\t\t[15]Other (hats/paints/etc...): " + meItems.size() + "\n"
						+ "\t\t[16]Silver Mk.II botkillers: " + silverII.size() + "\n"
						+ "\t\t[17]Gold Mk.II botkillers: " + goldII.size() + "\n"
						+ "\t\t[18]Australiums: " + meAussies.size() + "\n"
					+ "\tTwo Cities:\n"
						+ "\t\t[19]Weapons: " + tcWeapons.size() + "\n"
						+ "\t\t[20]Other (hats/paints/etc...): " + tcItems.size() + "\n"
						+ "\t\t[21]Pristine parts: " + pristine.size() + "\n"
						+ "\t\t[22]Battle-Worn parts: " + battleWorn.size() + "\n"
						+ "\t\t[23]Reinforced parts: " + reinforced.size() + "\n"
						+ "\t\t[24]Killstreak kits: " + normals.size() + "\n"
						+ "\t\t[25]Specialized killstreak fabricators: " + specs.size() + "\n"
						+ "\t\t[26]Professional killstreak fabricators: " + pros.size() + "\n"
						+ "\t\t[27]Australiums: " + tcAussies.size() + "\n"
					+ "\tGear Grinder:\n"
						+ "\t\t[28]Weapons: " + ggWeapons.size() + "\n"
						+ "\t\t[29]Other (hats/paints/etc...): " + ggItems.size() + "\n"
						+ "\t\t[30]Carbonado botkillers: " + carbonado.size() + "\n"
						+ "\t\t[31]Diamond botkillers: " + diamond.size() + "\n"
						+ "\t\t[32]Australiums: " + ggAussies.size() + "\n"
					+ "\n"
					+ "Select a number for all items, [1] to view this list again, or [0] to go back.";
			System.out.println(out);
			Scanner scan = new Scanner(System.in);
			int choice = scan.nextInt();
			while(choice != 0) {
				if(choice == 1) {
					System.out.println(out);
				} else if(choice == 2) {
					System.out.println(mapToString(aussiesMap));
				} else if(choice == 3) {
					System.out.println(mapToString(weaponsMap));
				} else if(choice == 4) {
					System.out.println(mapToString(itemsMap));
				} else if(choice == 5) {
					System.out.println(mapToString(osWeaponsMap));
				} else if(choice == 6) {
					System.out.println(mapToString(osItemsMap));
				} else if(choice == 7) {
					System.out.println(mapToString(rustMap));
				} else if(choice == 8) {
					System.out.println(mapToString(bloodMap));
				} else if(choice == 9) {
					System.out.println(mapToString(stWeaponsMap));
				} else if(choice == 10) {
					System.out.println(mapToString(stItemsMap));
				} else if(choice == 11) {
					System.out.println(mapToString(silverIMap));
				} else if(choice == 12) {
					System.out.println(mapToString(goldIMap));
				} else if(choice == 13) {
					System.out.println(mapToString(stAussiesMap));
				} else if(choice == 14) {
					System.out.println(mapToString(meWeaponsMap));
				} else if(choice == 15) {
					System.out.println(mapToString(meItemsMap));
				} else if(choice == 16) {
					System.out.println(mapToString(silverIIMap));
				} else if(choice == 17) {
					System.out.println(mapToString(goldIIMap));
				} else if(choice == 18) {
					System.out.println(mapToString(meAussiesMap));
				} else if(choice == 19) {
					System.out.println(mapToString(tcWeaponsMap));
				} else if(choice == 20) {
					System.out.println(mapToString(tcItemsMap));
				} else if(choice == 21) {
					System.out.println(mapToString(pristineMap));
				} else if(choice == 22) {
					System.out.println(mapToString(battleWornMap));
				} else if(choice == 23) {
					System.out.println(mapToString(reinforcedMap));
				} else if(choice == 24) {
					System.out.println(mapToString(normalsMap));
				} else if(choice == 25) {
					System.out.println(mapToString(specsMap));
				} else if(choice == 26) {
					System.out.println(mapToString(prosMap));
				} else if(choice == 27) {
					System.out.println(mapToString(tcAussiesMap));
				} else if(choice == 28) {
					System.out.println(mapToString(ggWeaponsMap));
				} else if(choice == 29) {
					System.out.println(mapToString(ggItemsMap));
				} else if(choice == 30) {
					System.out.println(mapToString(carbonadoMap));
				} else if(choice == 31) {
					System.out.println(mapToString(diamondMap));
				} else if(choice == 32) {
					System.out.println(mapToString(ggAussiesMap));
				}
				choice = scan.nextInt();
			}
			scan.close();
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			log.error("Error parsing mvm file.");
			e.printStackTrace();
		}
	}
	
	public void outputSurplusFile() {
		if(!surplusFileExists()) { 
			log.warn("Could not read surplus file, it does not exist!");
			return;
		}
		JSONParser parser = new JSONParser();
		ArrayList<String> weapons = new ArrayList<String>();
		HashMap<String, Long> weaponsMap = new HashMap<String, Long>();
		ArrayList<String> items = new ArrayList<String>();
		HashMap<String, Long> itemsMap = new HashMap<String, Long>();
		
		try {
			JSONObject obj = (JSONObject) parser.parse(new FileReader(directory + "/" + surplusFileName));
			JSONObject weaponsObj = (JSONObject) parser.parse(new FileReader("tf2weapons.json"));
			JSONArray weaponsJSONArr = (JSONArray) weaponsObj.get("weapons");
			ArrayList<String> weaponsArr = new ArrayList<String>();
			for (int i = 0; i < weaponsJSONArr.size(); i++) {
				JSONObject wep = (JSONObject) weaponsJSONArr.get(i);
				String wepStr = wep.values().toString().replace('[', ' ');
				wepStr = wepStr.replace(']', ' ');
				wepStr = wepStr.trim();
				weaponsArr.add(wepStr);
			}
			Set<String> set = obj.keySet();
			for(String s : set) {
				String name = s;
				if(name.startsWith("The")) {
					name = name.substring(3).trim();
				} else {
					//log.info(name);
				}
				
				//Doesn't like the special punctuation so special case, idk why cloak doesn't work and CBA
				if(weaponsArr.contains(name) || name.startsWith("Claidheamh") || name.startsWith("Cloak and Dagger")) {
					//Is weapon
					long amt = (long) obj.get(s);
					for(int i = 0; i < amt; i++) {
						weapons.add(name);
					}
					weaponsMap.put(name, amt);
				} else {
					//Is item
					long amt = (long) obj.get(s);
					for(int i = 0; i < amt; i++) {
						items.add(name);
					}
					itemsMap.put(name, amt);
				}
			}
			String out = "Squad Surplus Loot Results: \n"
					+ "\t[2]Weapons: " + weapons.size() + "\n"
					+ "\t[3]Other (hats/paints/etc...): " + items.size() + "\n"
					+ "Select a number for all items, [1] to view this list again, or [0] to go back.";
			
			System.out.println(out);
			Scanner scan = new Scanner(System.in);
			int choice = scan.nextInt();
			while(choice != 0) {
				if(choice == 1) {
					System.out.println(out);
				} else if(choice == 2) {
					System.out.println(mapToString(weaponsMap));
				} else if(choice == 3) {
					System.out.println(mapToString(itemsMap));
				}
				choice = scan.nextInt();
			}
			scan.close();
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			log.error("Error parsing surplus file.");
			e.printStackTrace();
		}
	}
	
	public void outputUnboxFile() {
		if(!unboxFileExists()) { 
			log.warn("Could not read unbox file, it does not exist!");
			return;
		}
		JSONParser parser = new JSONParser();
		//Colors aka qualities
		String uniqueColor = "#7D6D00";
		String strangeColor = "#CF6A32";
		String unusualColor = "#8650AC";
		String wepColor = "#FAFAFA";
		String hauntedColor = "#38f3ab";
		int totalUnique = 0;
		int totalStrange = 0;
		int totalUnusual = 0;
		int totalWep = 0;
		int totalHaunted = 0;
		
		//Case Rarities
		ArrayList<String> rarityStrings = new ArrayList<String>();
		rarityStrings.add("civilian");
		rarityStrings.add("freelance");
		rarityStrings.add("mercenary");
		rarityStrings.add("commando");
		rarityStrings.add("assassin");
		rarityStrings.add("elite");
		int totalCiv = 0;
		int totalFreelance = 0;
		int totalMerc = 0;
		int totalComm = 0;
		int totalAss = 0;
		int totalElite = 0;
		int totalRarities = 0;
		
		int totalCrates = 0;
		
		HashMap<Integer, HashMap<Integer, String>> crateMap = new HashMap<Integer, HashMap<Integer, String>>();
		
		
		int freeI = 2; //0 is quit, 1 is redisplay
		int freeTotal = 0;
		HashMap<Integer, String> freeCrateMap = new HashMap<Integer, String>();
		crateMap.put(2, freeCrateMap);
		ArrayList<String> freeCrateList = new ArrayList<String>();
		freeCrateList.add("Gift-Stuffed Stocking 2018");
		freeCrateList.add("Gift-Stuffed Stocking 2017");
		freeCrateList.add("'Contract Campaigner' War Paint Civilian Grade Keyless Case");
		freeCrateList.add("Antique Halloween Goodie Cauldron");
		freeCrateList.add("Gift-Stuffed Stocking 2019");
		freeCrateList.add("Gift-Stuffed Stocking 2020");
		freeCrateList.add("Halloween Gift Cauldron");
		freeCrateList.add("'Decorated War Hero' War Paint Mercenary Grade Keyless Case");
		freeCrateList.add("'Decorated War Hero' War Paint Freelance Grade Keyless Case");
		freeCrateList.add("'Decorated War Hero' War Paint Civilian Grade Keyless Case");
		freeCrateList.add("'Contract Campaigner' War Paint Mercenary Grade Keyless Case");
		freeCrateList.add("'Contract Campaigner' War Paint Freelance Grade Keyless Case");
		//freeCrateList.add("Secret Saxton"); //FIXed
		freeCrateList.add("Gift-Stuffed Stocking");
		freeCrateList.add("Halloween Package");
		
		int unlockedI = 2; //0 is quit, 1 is redisplay
		int unlockedTotal = 0;
		HashMap<Integer, String> unlockedCrateMap = new HashMap<Integer, String>();
		crateMap.put(3, unlockedCrateMap);
		ArrayList<String> unlockedCrateList = new ArrayList<String>();
		
		int cosmeticI = 2; //0 is quit, 1 is redisplay
		int cosmeticTotal = 0;
		HashMap<Integer, String> cosmeticCaseMap = new HashMap<Integer, String>();
		crateMap.put(4, cosmeticCaseMap);
		ArrayList<String> cosmeticCaseList = new ArrayList<String>();
		cosmeticCaseList.add("Violet Vermin Case");
		cosmeticCaseList.add("Wicked Windfall Case");
		cosmeticCaseList.add("Creepy Crawly Case");
		cosmeticCaseList.add("Gargoyle Case");
		cosmeticCaseList.add("Spooky Spoils Case");
		cosmeticCaseList.add("Quarantined Collection Case");
		cosmeticCaseList.add("Confidential Collection Case");
		
		int skinI = 2; //0 is quit, 1 is redisplay
		int skinTotal = 0;
		HashMap<Integer, String> skinCaseMap = new HashMap<Integer, String>();
		crateMap.put(5, skinCaseMap);
		ArrayList<String> skinCaseList = new ArrayList<String>();
		
		int paintI = 2; //0 is quit, 1 is redisplay
		int paintTotal = 0;
		HashMap<Integer, String> warPaintCaseMap = new HashMap<Integer, String>();
		crateMap.put(6, warPaintCaseMap);
		ArrayList<String> warPaintCaseList = new ArrayList<String>();
		
		int otherI = 2; //0 is quit, 1 is redisplay
		int otherTotal = 0;
		HashMap<Integer, String> otherCrateMap = new HashMap<Integer, String>();
		crateMap.put(7, otherCrateMap);
		
		String outString = "Unbox Loot Results: \n";
		String outStringEnd = "Select a number for all items, [1] to view this list again, or [0] to go back.";
			
			try {
				JSONObject obj = (JSONObject) parser.parse(new FileReader(directory + "/" + unboxFileName));
				Set<String> set = obj.keySet();
				
				for(String s : set) {
					JSONObject crateObj = (JSONObject) obj.get(s);
					JSONObject cratePlus = (JSONObject) crateObj.get("plus");
					JSONObject colors = (JSONObject) cratePlus.get("colors");
					JSONObject rarities = (JSONObject) cratePlus.get("rarities");
					if(colors.containsKey(uniqueColor)) {
						totalUnique += (long) colors.get(uniqueColor);
					}
					if(colors.containsKey(strangeColor)) {
						totalStrange += (long) colors.get(strangeColor);
					}
					if(colors.containsKey(unusualColor)) {
						totalUnusual += (long) colors.get(unusualColor);
					}
					if(colors.containsKey(wepColor)) {
						totalWep += (long) colors.get(wepColor);
					}
					if(colors.containsKey(hauntedColor)) {
						totalHaunted += (long) colors.get(hauntedColor);
					}
					//Different than ^ bc easy to copy paste arrayList from getCaseRarities method )
					//Debug temps
					long tempMerc = 0;
					long tempComm = 0;
					long tempAss = 0;
					long tempElite = 0;
					if(rarities.containsKey(rarityStrings.get(0))) {
						totalCiv += (long) rarities.get(rarityStrings.get(0));
					}
					if(rarities.containsKey(rarityStrings.get(1))) {
						totalFreelance += (long) rarities.get(rarityStrings.get(1));
					}
					if(rarities.containsKey(rarityStrings.get(2))) {
						tempMerc = (long) rarities.get(rarityStrings.get(2));
						totalMerc += tempMerc;
					}
					if(rarities.containsKey(rarityStrings.get(3))) {
						tempComm= (long) rarities.get(rarityStrings.get(3));
						totalComm += tempComm;
					}
					if(rarities.containsKey(rarityStrings.get(4))) {
						tempAss = (long) rarities.get(rarityStrings.get(4));
						totalAss += tempAss;
					}
					if(rarities.containsKey(rarityStrings.get(5))) {
						tempElite = (long) rarities.get(rarityStrings.get(5));
						totalElite += tempElite;
					}
					int tempRarities = (int) (tempMerc+tempComm+tempAss+tempElite);
					if(freeCrateList.contains(s)) {
						freeCrateMap.put(freeI, s);
						freeI++;
						long amt = (long) crateObj.get("total");
						freeTotal += amt;
					} else if(unlockedCrateList.contains(s) || s.contains("Unlocked")) {
						unlockedCrateMap.put(unlockedI, s);
						unlockedI++;
						long amt = (long) crateObj.get("total");
						if(amt != tempRarities && tempRarities != 0) {
							log.warn("Rarities and total crates opened does not match for crate " + s + " " + amt + "!=" + tempRarities);
						}
						unlockedTotal += amt;
					} else if(cosmeticCaseList.contains(s) || s.contains("Cosmetic Case")) {
						cosmeticCaseMap.put(cosmeticI, s);
						cosmeticI++;
						long amt = (long) crateObj.get("total");
						if(amt != tempRarities && tempRarities != 0) {
							log.warn("Rarities and total crates opened does not match for crate " + s + " " + amt + "!=" + tempRarities);
						}
						cosmeticTotal += amt;
					} else if(skinCaseList.contains(s) || s.contains("Collection") || s.contains("Weapons Case")) {
						skinCaseMap.put(skinI, s);
						skinI++;
						long amt = (long) crateObj.get("total");
						if(amt != tempRarities && tempRarities != 0) {
							log.warn("Rarities and total crates opened does not match for crate " + s + " " + amt + "!=" + tempRarities);
						}
						skinTotal += amt;
					} else if(warPaintCaseList.contains(s) || s.contains("War Paint")) {
						warPaintCaseMap.put(paintI, s);
						paintI++;
						long amt = (long) crateObj.get("total");
						if(amt != tempRarities && tempRarities != 0) {
							log.warn("Rarities and total crates opened does not match for crate " + s + " " + amt + "!=" + tempRarities);
						}
						paintTotal += amt;
					} else {
						otherCrateMap.put(otherI, s);
						otherI++;
						long amt = (long) crateObj.get("total");
						otherTotal += amt;
					}
				}
				totalRarities = totalMerc+totalComm+totalAss+totalElite;
				double civP = (double)Math.round(((double)totalCiv/(double)totalRarities)*10000) / 100;
				double freP = (double)Math.round(((double)totalFreelance/(double)totalRarities)*10000) / 100;
				double merP = (double)Math.round(((double)totalMerc/(double)totalRarities)*10000) / 100;
				double comP = (double)Math.round(((double)totalComm/(double)totalRarities)*10000) / 100;
				double assP = (double)Math.round(((double)totalAss/(double)totalRarities)*10000) / 100;
				double eliP = (double)Math.round(((double)totalElite/(double)totalRarities)*10000) / 100;
				totalCrates = freeTotal+unlockedTotal+cosmeticTotal+skinTotal+paintTotal+otherTotal;
				outString += "\t[2] Free Crates : " + freeTotal + "\n";
				outString += "\t[3] Unlocked Crates : " + unlockedTotal + "\n";
				outString += "\t[4] Cosmetic Cases : " + cosmeticTotal + "\n";
				outString += "\t[5] Weapon Skin Cases : " + skinTotal + "\n";
				outString += "\t[6] War Paint Cases : " + paintTotal + "\n";
				outString += "\t[7] Crates : " + otherTotal + "\n";
				outString += "\n\tTotal Uniques : " + totalUnique + "\n"
						+ "\tTotal Stranges : " + totalStrange + "\n"
						+ "\tTotal Decorated (Weapons) : " + totalWep + "\n"
						+ "\tTotal Haunted : " + totalHaunted + "\n"
						+ "\tTotal Unusuals : " + totalUnusual + "\n";
				outString += "\n\tTotal Graded Items (No civ/freelance): " + totalRarities + "\n"
						+ "\t\tCivilian : " + totalCiv + "\n"
						+ "\t\tFreelance : " + totalFreelance + "\n"
						+ "\t\tMercenary : " + totalMerc + " (" + merP + "%)\n"
						+ "\t\tCommando : " + totalComm + " (" + comP + "%)\n"
						+ "\t\tAssassin : " + totalAss + " (" + assP + "%)\n"
						+ "\t\tElite : " + totalElite + " (" + eliP + "%)\n";
				outString += "\n\tTotal Unboxes : " + totalCrates + " (" + (totalCrates-freeTotal) + " ignoring free crates)\n";
				System.out.println(outString + outStringEnd);
				//Details
				Scanner scan = new Scanner(System.in);
				int choice = scan.nextInt();
				while(choice != 0) {
					String detailedOut = "";
					if(choice == 1) { //Reprint overview
						System.out.println(outString + outStringEnd);
					} else if(crateMap.containsKey(choice)) { //Specific category
						String categoryOut = "";
						HashMap<Integer, String> category = crateMap.get(choice);
						for(Entry<Integer, String> e : category.entrySet()) {
							JSONObject crate = (JSONObject) obj.get(e.getValue());
							categoryOut += "[" + e.getKey() + "]" + " " + e.getValue() + " : " + crate.get("total") + "\n";
						}
						System.out.println(categoryOut + outStringEnd);
						int choice2 = scan.nextInt();
						while(choice2 != 0) {
							if(choice2 == 1) {
								System.out.println(categoryOut + outStringEnd);
							} else if(category.containsKey(choice2)) {
								String crateName = category.get(choice2);
								detailedOut = crateName + " details: \n";
								JSONObject crate = (JSONObject)obj.get(crateName);
								JSONObject cratePlus = (JSONObject)crate.get("plus");
								Set<String> plusSet = cratePlus.keySet();
								long total = (long) crate.get("total");
								long q = 0; //quality total
								double p = 0; //percent
								//Individual item list first
								detailedOut += "\tItems Gained: \n";
								for(String s : plusSet) {
									if(s.equals("colors") || s.equals("rarities")) {
										continue;
									}
									q = (long) cratePlus.get(s);
									p = (double)Math.round(((double)q/(double)total)*10000) / 100;
									detailedOut += "\t\t" + s + " : " + q + " (" + p + "%)\n";
								}
								
								//Quality of items if they exist
								detailedOut += "\n\tQuality Totals: \n";
								JSONObject colors = (JSONObject) cratePlus.get("colors");
								if(colors.containsKey(uniqueColor)) {
									q = (long) colors.get(uniqueColor);
									p = (double)Math.round(((double)q/(double)total)*10000) / 100;
									detailedOut += "\t\tUniques : " + q + " (" + p + "%)\n";
								}
								if(colors.containsKey(strangeColor)) {
									q = (long) colors.get(strangeColor);
									p = (double)Math.round(((double)q/(double)total)*10000) / 100;
									detailedOut += "\t\tStranges : " + q + "(" + p + "%)\n";
								}
								if(colors.containsKey(unusualColor)) {
									q = (long) colors.get(unusualColor);
									p = (double)Math.round(((double)q/(double)total)*10000) / 100;
									detailedOut += "\t\tUnusuals : " + q + "(" + p + "%)\n";
								}
								if(colors.containsKey(wepColor)) {
									q = (long) colors.get(wepColor);
									p = (double)Math.round(((double)q/(double)total)*10000) / 100;
									detailedOut += "\t\tDecorated : " + q + "(" + p + "%)\n";
								}
								if(colors.containsKey(hauntedColor)) {
									q = (long) colors.get(hauntedColor);
									p = (double)Math.round(((double)q/(double)total)*10000) / 100;
									detailedOut += "\t\tHaunted : " + q + "(" + p + "%)\n";
								}
								//Rarity totals if they exist
								JSONObject rarities = (JSONObject) cratePlus.get("rarities");
								if(!rarities.isEmpty()) {
									detailedOut += "\n\tRarity Totals:\n";
								}
								if(rarities.containsKey("civilian")) {
									q = (long) rarities.get("civilian");
									p = (double)Math.round(((double)q/(double)total)*10000) / 100;
									detailedOut += "\t\tCivilian : " + q + "(" + p + "%)\n";
								}
								if(rarities.containsKey("freelance")) {
									q = (long) rarities.get("freelance");
									p = (double)Math.round(((double)q/(double)total)*10000) / 100;
									detailedOut += "\t\tFreelance : " + q + "(" + p + "%)\n";
								}
								if(rarities.containsKey("mercenary")) {
									q = (long) rarities.get("mercenary");
									p = (double)Math.round(((double)q/(double)total)*10000) / 100;
									detailedOut += "\t\tMercenary : " + q + "(" + p + "%)\n";
								}
								if(rarities.containsKey("commando")) {
									q = (long) rarities.get("commando");
									p = (double)Math.round(((double)q/(double)total)*10000) / 100;
									detailedOut += "\t\tCommando : " + q + "(" + p + "%)\n";
								}
								if(rarities.containsKey("assassin")) {
									q = (long) rarities.get("assassin");
									p = (double)Math.round(((double)q/(double)total)*10000) / 100;
									detailedOut += "\t\tAssassin : " + q + "(" + p + "%)\n";
								}
								if(rarities.containsKey("elite")) {
									q = (long) rarities.get("elite");
									p = (double)Math.round(((double)q/(double)total)*10000) / 100;
									detailedOut += "\t\tElite : " + q + "(" + p + "%)\n";
								}
								//Total and print
								detailedOut += "\n\tTotal: " + total;
								System.out.println(detailedOut);
							}
							choice2 = scan.nextInt();
						}
						System.out.println(outString + outStringEnd);
					}
					choice = scan.nextInt();
				}
				scan.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}
	
	public void outputDropsFile() {
		if(!dropsFileExists()) { 
			log.warn("Could not read item drops file, it does not exist!");
			return;
		}
		JSONParser parser = new JSONParser();
		ArrayList<String> weapons = new ArrayList<String>();
		HashMap<String, Long> weaponsMap = new HashMap<String, Long>();
		ArrayList<String> crates = new ArrayList<String>();
		HashMap<String, Long> cratesMap = new HashMap<String, Long>();
		ArrayList<String> chemSets = new ArrayList<String>();
		HashMap<String, Long> chemSetsMap = new HashMap<String, Long>();
		ArrayList<String> items = new ArrayList<String>();
		HashMap<String, Long> itemsMap = new HashMap<String, Long>();
		
		try {
			JSONObject obj = (JSONObject) parser.parse(new FileReader(directory + "/" + dropsFileName));
			JSONObject weaponsObj = (JSONObject) parser.parse(new FileReader("tf2weapons.json"));
			JSONArray weaponsJSONArr = (JSONArray) weaponsObj.get("weapons");
			ArrayList<String> weaponsArr = new ArrayList<String>();
			for (int i = 0; i < weaponsJSONArr.size(); i++) {
				JSONObject wep = (JSONObject) weaponsJSONArr.get(i);
				String wepStr = wep.values().toString().replace('[', ' ');
				wepStr = wepStr.replace(']', ' ');
				wepStr = wepStr.trim();
				weaponsArr.add(wepStr);
			}
			Set<String> set = obj.keySet();
			for(String s : set) {
				String name = s;
				if(name.startsWith("The")) {
					name = name.substring(3).trim();
				} else {
					//log.info(name);
				}
				
				//Doesn't like the special punctuation so special case, idk why cloak doesn't work and CBA
				if(weaponsArr.contains(name) || name.startsWith("Claidheamh") || name.startsWith("Cloak and Dagger")) {
					//Is weapon
					long amt = (long) obj.get(s);
					for(int i = 0; i < amt; i++) {
						weapons.add(name);
					}
					weaponsMap.put(name, amt);
				} else if(name.contains("Case") || name.contains("Munition") || name.contains("Crate")
						|| name.equals("Mann Co. Audition Reel") || name.equals("Mann Co. Director's Cut Reel")) {
					//Is crate/case
					long amt = (long) obj.get(s);
					for(int i = 0; i < amt; i++) {
						crates.add(name);
					}
					cratesMap.put(name, amt);
				} else if(name.contains("Chemistry Set")) {
					//Is chem set
					long amt = (long) obj.get(s);
					for(int i = 0; i < amt; i++) {
						chemSets.add(name);
					}
					chemSetsMap.put(name, amt);
				} else {
					//Is item
					long amt = (long) obj.get(s);
					for(int i = 0; i < amt; i++) {
						items.add(name);
					}
					itemsMap.put(name, amt);
				}
			}
			String out = "Item Drops Results: \n"
					+ "\t[2]Weapons: " + weapons.size() + "\n"
					+ "\t[3]Crates: " + crates.size() + "\n"
					+ "\t[4]Chemistry Sets (Strangifier or collectors): " + chemSets.size() + "\n"
					+ "\t[5]Other (hats/paints/etc...): " + items.size() + "\n"
					+ "Select a number for all items, [1] to view this list again, or [0] to go back.";
			
			System.out.println(out);
			Scanner scan = new Scanner(System.in);
			int choice = scan.nextInt();
			while(choice != 0) {
				if(choice == 1) {
					System.out.println(out);
				} else if(choice == 2) {
					System.out.println(mapToString(weaponsMap));
				} else if(choice == 3) {
					System.out.println(mapToString(cratesMap));
				} else if(choice == 4) {
					System.out.println(mapToString(chemSetsMap));
				} else if(choice == 5) {
					System.out.println(mapToString(itemsMap));
				}
				choice = scan.nextInt();
			}
			scan.close();
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			log.error("Error parsing item drops file.");
			e.printStackTrace();
		}
	}
	
	public void outputStorePurchaseFile() {
		if(!dropsFileExists()) { 
			log.warn("Could not read store purchases file, it does not exist!");
			return;
		}
		JSONParser parser = new JSONParser();
		ArrayList<String> items = new ArrayList<String>();
		HashMap<String, Long> itemsMap = new HashMap<String, Long>();
		
		try {
			JSONObject obj = (JSONObject) parser.parse(new FileReader(directory + "/" + storePurchaseFileName));
			Set<String> set = obj.keySet();
			for(String s : set) {
				String name = s;
				if(name.startsWith("The")) {
					name = name.substring(3).trim();
				} else {
					//log.info(name);
				}
				//Is item
				long amt = (long) obj.get(s);
				for(int i = 0; i < amt; i++) {
					items.add(name);
				}
				itemsMap.put(name, amt);
				
			}
			String out = "Item Drops Results: \n"
					+ "\t[2]All items: " + items.size() + "\n"
					+ "Select a number for all items, [1] to view this list again, or [0] to go back.";
			
			System.out.println(out);
			Scanner scan = new Scanner(System.in);
			int choice = scan.nextInt();
			while(choice != 0) {
				if(choice == 1) {
					System.out.println(out);
				} else if(choice == 2) {
					System.out.println(mapToString(itemsMap));
				}
				choice = scan.nextInt();
			}
			scan.close();
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			log.error("Error parsing store purchases file.");
			e.printStackTrace();
		}
	}
	
	
	private static String mapToString(Map<String, Long> map) {
		String out = "";
		for(Entry e : map.entrySet()) {
			out += e.getKey() + " : " + e.getValue() + "\n";
		}
		return out;
	}
	
}
