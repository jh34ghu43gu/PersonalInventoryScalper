package files;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.json.simple.JSONObject;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Element;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ch.qos.logback.classic.Logger;
import utils.Utils;

public class InventoryFilesManager {
	
	private static final Logger log = (Logger) LoggerFactory.getLogger(InventoryFilesManager.class);
	
	public static void makeDir() {
		File dir = new File("InventoryFiles");
		if(!dir.exists()) {
			log.info("Making InventoryFiles directory.");
			dir.mkdir();
		}
	}
	
	public static long getLastTime() {
		File invDir = new File("InventoryFiles");
		File[] invFiles = invDir.listFiles();
		long out = 0;
		for(File f : invFiles) {
			String fName = f.getName();
			fName = fName.replace(".txt", "");
			Long t = Long.parseLong(fName);
			if(t > out) { out = t; }
		}
		return out;
	}
	
	/**
	 * @param r
	 * @param start_time
	 * @param stop_time
	 * @return A status code, 22 if signed out, 0 if reached the end, or the next time to parse
	 * @throws IOException
	 */
	public static long parseAndCreateDoc(Response r, long start_time, long stop_time) throws IOException {
		if(r.statusCode() != 200) {
			log.warn("Non-OK status code: " + r.statusCode());
			return r.statusCode();
		}
		if(r.parse().title().equalsIgnoreCase("Sign In")) {
			log.warn("Sign in page recieved, must log back in.");
			return 22;
		}
		try {
			Element tradeTable = r.parse().getElementById("inventory_history_table");
			JSONObject json = new JSONObject();
			int objectCount = 0;
			String firstTime = ""; //Check for empty page, will return -1 later
			//Next page stuff
			Element js = tradeTable.parent().parent().parent().nextElementSibling().nextElementSibling(); //div mainContents < div BG_bottom < div class=pagecontent no_header -> div inventory_history_filters_dialog -> script
			String jsString = js.toString().substring(0, 1000);
			String locate = "g_historyCursor = {\"time\":";
			String lastTime = "";
			if(jsString.indexOf(locate) == -1) {
				//Last page
				lastTime = "-1";
			} else {
				int startStr = jsString.indexOf(locate) + locate.length();
				lastTime = jsString.substring(startStr, startStr+10);
			}
			//Actual parse
			boolean stop = false;
			for(Element tradeRow : tradeTable.select("div.tradehistoryrow")) {
				JSONObject tradeObject = new JSONObject();
				String date = tradeRow.select("div.tradehistory_date").text();
				//Break condition for updates, before setting firstTime so we don't write file if we happen to be at a page break
				if(Utils.dateToUnix(date) < stop_time) {
					log.info("Passed stop time, breaking...");
					stop = true;
					break;
				}
				if(firstTime.length() == 0) { firstTime = date; }
				String event_description = tradeRow.select("div.tradehistory_event_description").text();
				JSONObject tradePlus = new JSONObject();
				JSONObject tradeMinus = new JSONObject();
				for(Element trade : tradeRow.select("div.tradehistory_items_plusminus")) {
					int itemCount = 0;
					if(trade.text().equals("+")) {
						for(Element item : trade.parent().select("span.history_item_name")) {
							if(!item.parent().attr("data-appid").equals("440")) { continue; } //Can still get non-tf2 items if they were in the same trade.
							JSONObject newItem = new JSONObject();
							String itemName = item.text();
							String color = item.attr("style");
							if(color.indexOf("#") == -1) {
								log.warn("Could not find valid color for " + date + " (" + Utils.dateToUnix(date) + ") ItemName: " + itemName);
							} else {
								color = color.substring(color.indexOf("#"));
							}
							newItem.put("itemName", itemName);
							newItem.put("color", color);
							tradePlus.put("item" + itemCount, newItem);
							itemCount++;
						}
					} else {
						for(Element item : trade.parent().select("span.history_item_name")) {
							if(!item.parent().attr("data-appid").equals("440")) { continue; } //Can still get non-tf2 items if they were in the same trade.
							JSONObject newItem = new JSONObject();
							String itemName = item.text();
							String color = item.attr("style");
							color = color.substring(color.indexOf("#"));
							newItem.put("itemName", itemName);
							newItem.put("color", color);
							tradeMinus.put("item" + itemCount, newItem);
							itemCount++;
						}
					}
				}
				tradeObject.put("date", date);
				tradeObject.put("event_description", event_description);
				tradeObject.put("plus", tradePlus);
				tradeObject.put("minus", tradeMinus);
				json.put("trade" + objectCount, tradeObject);
				objectCount++;
			}
		
			if(firstTime.isEmpty()) { //LastTime probably makes this unnecessary 
				return -1;
			}
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String s = gson.toJson(json);
			FileWriter writer = new FileWriter("InventoryFiles/" + start_time + ".txt");
			writer.write(s);
			writer.flush();
			writer.close();
			log.info("Created " + start_time + ".txt");
			
			if(stop) { //Still want to create the doc with what we have
				return -1;
			}
			return Long.parseLong(lastTime);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			FileWriter writer = new FileWriter("lastErrorDoc.html");
			writer.write(r.body());
			writer.flush();
			writer.close();
		}
		
		return 0;
	}

}
