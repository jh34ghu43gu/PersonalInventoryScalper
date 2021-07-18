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
		HashMap<String, Integer> itemsGained = new HashMap<String, Integer>();
		HashMap<String, Integer> itemsGainedColor = new HashMap<String, Integer>();
		HashMap<String, Integer> itemsLost = new HashMap<String, Integer>();
		for(File f : invFiles) {
			try {
				JSONObject obj = (JSONObject) parser.parse(new FileReader(f));
				Set<String> set = obj.keySet();
				for(String s : set) {
					JSONObject trade = (JSONObject) obj.get(s);
					if(trade.get("event_description").equals("Unlocked a crate")) {
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
							String color = (String) item.get("color");
							if(itemsGainedColor.containsKey(color)) { //Already in map, iterate
								int amt = itemsGainedColor.get(color)+1;
								itemsGainedColor.put(color, amt);
							} else { //Not in map, add it
								itemsGainedColor.put(color, 1);
							}
						}
						JSONObject minusObj = (JSONObject) trade.get("minus");
						tradeSet = minusObj.keySet();
						for(String s2 : tradeSet) {
							JSONObject item = (JSONObject) minusObj.get(s2);
							String itemName = (String) item.get("itemName");
							if(itemsLost.containsKey(itemName)) { //Already in map, iterate
								int amt = itemsLost.get(itemName)+1;
								itemsLost.put(itemName, amt);
							} else { //Not in map, add it
								itemsLost.put(itemName, 1);
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
		JSONObject plus = new JSONObject();
		JSONObject plusColor = new JSONObject();
		JSONObject minus = new JSONObject();
		for(Map.Entry<String, Integer> entry : itemsGained.entrySet()) {
			plus.put(entry.getKey(), entry.getValue());
		}
		for(Map.Entry<String, Integer> entry : itemsGainedColor.entrySet()) {
			plusColor.put(entry.getKey(), entry.getValue());
		}
		for(Map.Entry<String, Integer> entry : itemsLost.entrySet()) {
			minus.put(entry.getKey(), entry.getValue());
		}
		unboxObject.put("Gained", plus);
		unboxObject.put("GainedColors", plusColor);
		unboxObject.put("Lost", minus);
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
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			log.error("Error parsing mvm file.");
			e.printStackTrace();
		}
	}
	
	public void outputUnboxFile() {
		if(!unboxFileExists()) { 
			log.warn("Could not read unbox file, it does not exist!");
			return;
		}
		JSONParser parser = new JSONParser();		
		try {
			JSONObject obj = (JSONObject) parser.parse(new FileReader(directory + "/" + unboxFileName));
			JSONObject plus = (JSONObject) obj.get("Gained");
			JSONObject plusColor = (JSONObject) obj.get("GainedColors");
			JSONObject minus = (JSONObject) obj.get("Lost");
			
			
			//Get keys/cases used
			ArrayList<String> freeKeyless = new ArrayList<String>();
			HashMap<String, Long> freeKeylessMap = new HashMap<String, Long>();
			ArrayList<String> keyless = new ArrayList<String>();
			HashMap<String, Long> keylessMap = new HashMap<String, Long>();
			
			ArrayList<String> crateKey = new ArrayList<String>();
			HashMap<String, Long> crateKeyMap = new HashMap<String, Long>();
			ArrayList<String> cosmeticKey = new ArrayList<String>();
			HashMap<String, Long> cosmeticKeyMap = new HashMap<String, Long>();
			ArrayList<String> paintKey = new ArrayList<String>();
			HashMap<String, Long> paintKeyMap = new HashMap<String, Long>();
			ArrayList<String> skinKey = new ArrayList<String>();
			HashMap<String, Long> skinKeyMap = new HashMap<String, Long>();
			
			ArrayList<String> crate = new ArrayList<String>();
			HashMap<String, Long> crateMap = new HashMap<String, Long>();
			ArrayList<String> cosmeticCase = new ArrayList<String>();
			HashMap<String, Long> cosmeticCaseMap = new HashMap<String, Long>();
			ArrayList<String> paintCase = new ArrayList<String>();
			HashMap<String, Long> paintCaseMap = new HashMap<String, Long>();
			ArrayList<String> skinCase = new ArrayList<String>();
			HashMap<String, Long> skinCaseMap = new HashMap<String, Long>();
			
			ArrayList<String> specialCosmeticNames = new ArrayList<String>();
			specialCosmeticNames.add("Wicked Windfall");
			specialCosmeticNames.add("Gargoyle");
			specialCosmeticNames.add("Spooky Spoils");
			specialCosmeticNames.add("Creepy Crawly");
			specialCosmeticNames.add("Violet Vermin");
			specialCosmeticNames.add("Quarantined");
			specialCosmeticNames.add("Confidential");
			specialCosmeticNames.add("Invasion Community");
			ArrayList<String> specialSkinNames = new ArrayList<String>();
			specialSkinNames.add("Gun Mettle");
			specialSkinNames.add("Tough Break");
			
			Set<String> set = minus.keySet();
			int unboxes = 0;
			for(String s : set) {
				
				if(s.contains("Grade Keyless Case")) {
					long amt = (long) minus.get(s);
					for(int i = 0; i < amt; i++) {
						freeKeyless.add(s);
						unboxes++;
					}
					freeKeylessMap.put(s, amt);
				} else if(s.contains("Unlocked")) {
					long amt = (long) minus.get(s);
					for(int i = 0; i < amt; i++) {
						keyless.add(s);
						unboxes++;
					}
					keylessMap.put(s, amt);
				} else if(s.contains("Cosmetic Key")) {
					long amt = (long) minus.get(s);
					for(int i = 0; i < amt; i++) {
						cosmeticKey.add(s);
						unboxes++;
					}
					cosmeticKeyMap.put(s, amt);
				} else if(s.contains("War Paint Key")) {
					long amt = (long) minus.get(s);
					for(int i = 0; i < amt; i++) {
						paintKey.add(s);
						unboxes++;
					}
					paintKeyMap.put(s, amt);
				} else if(s.contains("Key")) { //Must be last key
					long amt = (long) minus.get(s);
					//Special cases bc valve bad
					boolean key = false;
					if(!key) {
						for(String s2 : specialCosmeticNames) {
							if(s.contains(s2)) {
								for(int i = 0; i < amt; i++) {
									cosmeticKey.add(s);
									unboxes++;
								}
								cosmeticKeyMap.put(s, amt);
								key = true;
								break;
							}
						}
					}
					if(!key) {
						for(String s2 : specialSkinNames) {
							if(s.contains(s2)) {
								for(int i = 0; i < amt; i++) {
									skinKey.add(s);
									unboxes++;
								}
								skinKeyMap.put(s, amt);
								key = true;
								break;
							}
						}
					}
					if(!key) {
						for(int i = 0; i < amt; i++) {
							crateKey.add(s);
							unboxes++;
						}
						crateKeyMap.put(s, amt);
					}
				} else if(s.contains("Cosmetic Case")) {
					long amt = (long) minus.get(s);
					for(int i = 0; i < amt; i++) {
						cosmeticCase.add(s);
					}
					cosmeticCaseMap.put(s, amt);
				} else if(s.contains("War Paint Case")) {
					long amt = (long) minus.get(s);
					for(int i = 0; i < amt; i++) {
						paintCase.add(s);
					}
					paintCaseMap.put(s, amt);
				} else if(s.contains("Weapons Case")) {
					long amt = (long) minus.get(s);
					for(int i = 0; i < amt; i++) {
						skinCase.add(s);
					}
					skinCaseMap.put(s, amt);
				} else { //Must be last for generic crates
					long amt = (long) minus.get(s);
					boolean c = false;
					if(!c) {
						for(String s2 : specialCosmeticNames) {
							if(s.contains(s2)) {
								for(int i = 0; i < amt; i++) {
									cosmeticCase.add(s);
								}
								cosmeticCaseMap.put(s, amt);
								c = true;
							}
						}
					}
					if(!c) {
						for(String s2 : specialSkinNames) {
							if(s.contains(s2)) {
								for(int i = 0; i < amt; i++) {
									skinCase.add(s);
								}
								skinCaseMap.put(s, amt);
								c = true;
							}
						}
					}
					if(!c) {
						for(int i = 0; i < amt; i++) {
							crate.add(s);
						}
						crateMap.put(s, amt);
					}
				}				
			}
			//Colors aka qualities
			String unique = "#7D6D00";
			String strange = "#CF6A32";
			String unusual = "#8650AC";
			String wep = "#FAFAFA";
			long uniques = (long) plusColor.get(unique);
			long stranges = (long) plusColor.get(strange);
			long unusuals = (long) plusColor.get(unusual);
			long weps = (long) plusColor.get(wep);
			
			
			
			String out = "Unboxes Results: \n"
					+ "\tTotal Unboxes: " + unboxes + "\n"
					+ "\tItems Used:\n"
						+ "\t\t[2]Free Keyless Cases: " + freeKeyless.size() + "\n"
						+ "\t\t[3]Unlocked Cases/Crates: " + keyless.size() + "\n"
						+ "\t\t[4]Cosmetic Cases: " + cosmeticCase.size() + "\n"
						+ "\t\t[5]War Paint Cases: " + paintCase.size() + "\n"
						+ "\t\t[6]Weapon Skin Cases: " + skinCase.size() + "\n"
						+ "\t\t[7]Other (crates): " + crate.size() + "\n"
					+ "\tItems Gained:\n"
						+ "\t\tUniques: " + uniques + "\n"
						+ "\t\tStranges: " + stranges + "\n"
						+ "\t\tUnusuals: " + unusuals + "\n"
						+ "\t\tOther (war paints/skins): " + weps + "\n"
					+ "Select a number for all items, [1] to view this list again, or [0] to go back.";
			
			System.out.println(out);
			Scanner scan = new Scanner(System.in);
			int choice = scan.nextInt();
			while(choice != 0) {
				if(choice == 1) {
					System.out.println(out);
				} else if(choice == 2) {
					System.out.println(mapToString(freeKeylessMap));
				} else if(choice == 3) {
					System.out.println(mapToString(keylessMap));
				} else if(choice == 4) {
					System.out.println(mapToString(cosmeticCaseMap));
					System.out.println(mapToString(cosmeticKeyMap));
				} else if(choice == 5) {
					System.out.println(mapToString(paintCaseMap));
					System.out.println(mapToString(paintKeyMap));
				} else if(choice == 6) {
					System.out.println(mapToString(skinCaseMap));
					System.out.println(mapToString(skinKeyMap));
				} else if(choice == 7) {
					System.out.println(mapToString(crateMap));
					System.out.println(mapToString(crateKeyMap));
				}
				choice = scan.nextInt();
			}
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			log.error("Error parsing mvm file.");
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
