# PersonalInventoryScalper
 Tf2 inventory history tool

 This tool scrolls through your steam inventory history from https://steamcommunity.com/my/inventoryhistory/?start_time=&app%5B%5D=440 and formats all the results into smaller json .txt files (didn't do .json bc I'm idiot). After these files are retrieved you are able to do a few* data retrieval methods to get some stats about your item history.
 
 *Currently only mvm-related stats and some unbox info.
 
 To use after compiling you must run the jar in the same folder as the tf2weapons.json & tf2cases.json files through command line "java -jar PersonalInventoryScalper.jar" On first time run the program will create a config file which you can put your steam account details in and your secret key if you know it. The program will ask for all of these (2fa code instead of secret key) if you leave them untouched. You can also adjust the cooldown between each website request, 7 seconds worked fine for me; you are welcome to try your luck with lower times (minimum 1 second, does not parse non-int). You can use NextInventoryTime to start your inventory fetching at a specific time using a unix timestamp. After running again you will be prompted to refresh your files, since you have no files you should enter "y" and it will begin collecting your files. After your files are collected, close and reopen the program this time entering "n" in order to reach the data selection menu. Self explanatory from there.
 
 If you actually refresh your files you should delete all existing files as new trades will end up throwing everything off leading to inaccurate data results later. If you want to do multiple accounts just make a folder and move the txt files around based on which account you want. Maybe will have a feature for multi-accounts later.
 
 Yes there is a gui package/class, no I did not finish it.
 
 
 Feel free to manipulate the inventory json files how you want, I made it gather what I thought the most important things were, color code is for quality. 
 
 # WARNINGS
 You will be logged out of your desktop account while doing this (family share enabled may stop this).
 There is an approx. rate limit of 600 /InventoryHistory/ requests per ip per ~12(?) hours. If you have a large trade/market hstory collecting all your files will require multiple days (my main account had 728 requests so thus was done over the course of 2 days). You get 50 results per request so if you want an estimate do your (trades + market history + mvm mission count)/50 and then add another 50-200 depending on how much crafting you do and how long you've played.