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
	
	public static long parseAndCreateDoc(Response r, long start_time) throws IOException {
		if(r.statusCode() != 200) {
			log.warn("Non-OK status code: " + r.statusCode());
			return r.statusCode();
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
			for(Element tradeRow : tradeTable.select("div.tradehistoryrow")) {
				JSONObject tradeObject = new JSONObject();
				String date = tradeRow.select("div.tradehistory_date").text();
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
