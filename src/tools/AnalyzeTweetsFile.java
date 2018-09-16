package tools;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.FileWriter;


public class AnalyzeTweetsFile {

	// no DC because ambiguous with Washington
	static String[] stateNames = {"Alabama","Alaska","Arizona","Arkansas","California",
								  "Colorado","Connecticut","Delaware","Florida","Georgia",
								  "Hawaii","Idaho","Illinois","Indiana","Iowa",
								  "Kansas","Kentucky","Louisiana","Maine","Maryland",
								  "Massachusetts","Michigan","Minnesota","Mississippi","Missouri",
								  "Montana","Nebraska","Nevada","New Hampshire","New Jersey",
								  "New Mexico","New York","North Carolina","North Dakota","Ohio",
								  "Oklahoma","Oregon","Pennsylvania","Rhode Island","South Carolina",
								  "South Dakota","Tennessee","Texas","Utah","Vermont",
								  "Virginia","Washington","West Virginia","Wisconsin","Wyoming"};
	
	static String[] stateAbbrevs = {"AL","AK","AZ","AR","CA",
			  						"CO","CT","DE","FL","GA",
								    "HI","ID","IL","IN","IA",
								    "KS","KY","LA","ME","MD",
								    "MA","MI","MN","MS","MO",
								    "MT","NE","NV","NH","NJ",
								    "NM","NY","NC","ND","OH",
								    "OK","OR","PA","RI","SC",
								    "SD","TN","TX","UT","VT",
								    "VA","WA","WV","WI","WY"};
	
	// empty ArrayList for analysis results to be appended to
	static ArrayList<String> tweetResults = new ArrayList<String>();

	// politics words
	static String[] wordsThatIndicatePolitics = {
			"trump",
			"republican",
			"democrat",
			"liberal",
			"conservative",
			"constitution",
			"politic",
			"vote",
			"potus",
			"president",
			"democracy",
			"@GOP",
			"comey",
			"clinton",
			"obama"
	};

	// sports words
	static String[] wordsThatIndicateSports = {
			"nfl",
			"basketball",
			"football",
			"warriors",
			"celtics",
			"cavs",
			"rockets",
			"durant",
			"lebron",
			"team usa",
			"sport",
			"world cup",
			"soccer",
			"cristiano",
			"ronaldo"
	};

	public static void main(String[] args) {
		processTweetFile();
		writeTweetInformationToFile();
		System.out.println("FINISHED.");
	}

	// function to create HashMap for states and baby names
	private static HashMap<String,String> makeMap(boolean isGeo, String fileName)
	{
		BufferedReader br = null;
		FileReader fr = null;
		HashMap<String,String> map = new HashMap<String, String>();
		ArrayList<String> collisionList = new ArrayList<String>();
		
		try
		{
			fr = new FileReader(fileName);
			br = new BufferedReader(fr);
			String line;	    
	    
		    while((line=br.readLine())!=null)
		    {
		        String str[] = line.split(",");
		        if (isGeo)
		        {
			        // remove all collisions
			        if (map.get(str[0]) != null)
			        {
			        	map.remove(str[0]);
			        	collisionList.add(str[0]);
			        }
			        else if (!collisionList.contains(str[0])) { map.put(str[0], str[1]); }
		        }
		        else
		        {
		        	// remove collisions that have different values
		        	if ((map.get(str[0]) != null) && !(map.get(str[0]).equals(str[1])))
			        {
			        	map.remove(str[0]);
			        	collisionList.add(str[0]);
			        }
			        else if (!collisionList.contains(str[0])) { map.put(str[0], str[1]); }
		        }
		    }
		}
		catch (IOException e) { e.printStackTrace(); }
		
		try
		{
			if (br != null) { br.close(); }
			if (fr != null) { fr.close(); }
		}
		catch (IOException e) { e.printStackTrace(); }

	    return map;
	}
	
	private static void processTweetFile() {
		convertProgrammerWordListsToLowercase(); // matching is easier if everything is lowercase

		HashMap<String,String> geoMap = makeMap(true, "NewGeotargets.csv");
		HashMap<String,String> genderMap = makeMap(false, "baby-names.csv");
		
		String lineRead = null;
		//String tweetFileName = SearchTwitterAndStoreTweetsInFile.tweetsFileName + ".csv";
		String tweetFileName = "combine.csv";

		try {
			FileReader fileReader = new FileReader(tweetFileName);
			BufferedReader bufferedReader =  new BufferedReader(fileReader);

			while((lineRead = bufferedReader.readLine()) != null) {
				String[] parts = lineRead.split("\\t");
				if (parts.length > 0) {// tweet text exists
					String tweetText = parts[0];
					String tweetUser = parts[1];
					String tweetLocation = parts[2];
					String tweetScreenName = parts[3];

					AnalyzeTweetInformation(tweetText, tweetUser, tweetLocation, tweetScreenName, geoMap, genderMap);
				}
			}

			bufferedReader.close();         
		}
		catch(FileNotFoundException ex) {
			System.out.println("Unable to open file '" + tweetFileName + "'");  
			ex.printStackTrace();
		}
		catch(IOException ex) {
			System.out.println("Error reading file '" + tweetFileName + "'");                  
			ex.printStackTrace();
		}
	}
	
	// convert politics and sports wordlists to lowercase
	private static void convertProgrammerWordListsToLowercase() {
		for (int i = 0; i < wordsThatIndicatePolitics.length; i++) {
			String searchWord = wordsThatIndicatePolitics[i];
			wordsThatIndicatePolitics[i] = searchWord.toLowerCase();
		}

		for (int i = 0; i < wordsThatIndicateSports.length; i++) {
			String searchWord = wordsThatIndicateSports[i];
			wordsThatIndicateSports[i] = searchWord.toLowerCase();
		}		
	}

	// analyze a single tweet
	private static void AnalyzeTweetInformation(String tweetText, String tweetUser, String tweetLocation, String tweetScreenName, HashMap<String,String> geoMap, HashMap<String,String> genderMap) {
		String tweetTextLowercase = tweetText.toLowerCase();

		int politicsWordsIntweet = countPoliticsWordsInTweet(tweetTextLowercase);
		int sportsWordsIntweet = countSportsWordsInTweet(tweetTextLowercase);

		String location = getStateFromLocation(tweetLocation, stateNames, geoMap);
		
		String nameGender = getNameGender(tweetUser, tweetScreenName, genderMap);
		System.out.println(tweetLocation + ": " + location);	
		System.out.println(tweetUser + " " + tweetScreenName + ": " + nameGender);
		System.out.println("tweet text: "+ tweetText);
		System.out.println("Politics: " + politicsWordsIntweet);
		System.out.println("Sports: " + sportsWordsIntweet);
		System.out.println();
		
		tweetText = cleanTweettext(tweetText);
		String resultString = tweetText + "\t" + tweetLocation + "\t" + tweetUser + "\t" + tweetScreenName + "\t" 
						      + location + "\t" + nameGender + "\t" + politicsWordsIntweet + "\t" + sportsWordsIntweet;
		tweetResults.add(resultString);
	}

	private static int countPoliticsWordsInTweet(String tweetText) {
		return countMatchingWordsInTweet(tweetText, wordsThatIndicatePolitics);
	}


	private static int countSportsWordsInTweet(String tweetText) {
		return countMatchingWordsInTweet(tweetText, wordsThatIndicateSports);
	}

	private static int countMatchingWordsInTweet(String tweetText, String[] wordList) {
		int matchingWordCount = 0;
		for (int i = 0; i < wordList.length; i++) {
			String searchWord = wordList[i];

			if (tweetText.contains(searchWord)) {
				matchingWordCount++;
			}// found match in word list

		}
		return matchingWordCount;
	}
	
	// return a State String given the tweet location
	private static String getStateFromLocation(String tweetLocation, String[] stateList, HashMap<String, String> geoMap)
	{
		String resultState = "NotAState";
		//System.out.println();
		//System.out.println(tweetLocation);
		
		// discard common mismatches - https://blog.cudoo.com/which-countries-have-the-most-english-speakers
		String discardRegex = "(london|england|scotland|ireland|india|pakistan|philippines|nigeria|australia|south africa|sierra leone)";
		Matcher m = Pattern.compile(discardRegex,Pattern.CASE_INSENSITIVE).matcher(tweetLocation);
		if (!m.find() )
		{
			HashMap<String, String> abbrevMap = new HashMap<String, String>();
			int j = 0;
			while (j < stateNames.length && j < stateAbbrevs.length) { //for safety i will check on both sizes
				abbrevMap.put(stateAbbrevs[j], stateNames[j]);
			    j++;
			}
			// one off matches
			abbrevMap.put("NYC", "New York");
			abbrevMap.put("Cali", "California");
			
			// match State on "City, State"
			String[] locationParts = tweetLocation.split(",");
			if (locationParts.length > 1)
			{
				for (String abbrev : abbrevMap.keySet())
				{
					String compareLocation = locationParts[locationParts.length-1].toLowerCase().replaceAll("\\s","");
					// contains state name or exact abbreviation match
					if (compareLocation.contains(abbrevMap.get(abbrev).toLowerCase()) || compareLocation.equals(abbrev.toLowerCase()))
					{
						resultState = abbrevMap.get(abbrev); 
						//System.out.println("----#1----");
						break;
					}
				}
			}
			
			// match State on "City State"
			if (resultState.equals("NotAState"))
			{
				locationParts = tweetLocation.split("\\s+");
				if (locationParts.length > 1)
				{
					for (String abbrev : abbrevMap.keySet())
					{
						String compareLocation = locationParts[locationParts.length-1].toLowerCase().replaceAll("\\s","");
						// contains state name or exact abbreviation match
						if (compareLocation.contains(abbrevMap.get(abbrev).toLowerCase()) || compareLocation.equals(abbrev.toLowerCase()))
						{ 
							resultState = abbrevMap.get(abbrev); 
							//System.out.println("----#2----");
							break;
						}
					}
				}
			}			
			
			// match on "State"
			if (resultState.equals("NotAState"))
			{
				for (String abbrev : abbrevMap.keySet())
				{
					if (tweetLocation.toLowerCase().contains(abbrevMap.get(abbrev).toLowerCase()) || tweetLocation.toLowerCase().equals(abbrev.toLowerCase()))
					{
						// contains state name or exact abbreviation match
						if (!(abbrevMap.get(abbrev).equals("Washington") && (tweetLocation.toLowerCase().contains("dc") || tweetLocation.toLowerCase().contains("d.c"))))
						{
							resultState = abbrevMap.get(abbrev); 
							//System.out.println("----#3----");
							break;
						}
					}
				}
			}			
			
			// match City on "City State"
			locationParts = tweetLocation.split("\\s+");
			if (locationParts.length > 0 && resultState.equals("NotAState"))
			{
				for (String key : geoMap.keySet()) 
				{
					// exact city match
				    if (locationParts[0].toLowerCase().equals(key.toLowerCase())) 
				    { 
				    	resultState = geoMap.get(key);
				    	//System.out.println("----#4----");
			    	}
				}
			}
			
			// match City on "City, State"
			locationParts = tweetLocation.split(",");
			if (locationParts.length > 0 && resultState.equals("NotAState"))
			{
				for (String key : geoMap.keySet()) 
				{
					// exact city match
				    if (locationParts[0].toLowerCase().equals(key.toLowerCase())) 
				    { 
				    	resultState = geoMap.get(key);
				    	//System.out.println("----#5----");
			    	}
				}
			}
		}
		return resultState;
	}

	// return a gender String given the user name and screenname
	private static String getNameGender(String tweetUser, String tweetScreenName, HashMap<String, String> nameMap)
	{
		String resultGenderName = "Ambiguous";
		String resultGenderSN = "Ambiguous";
		
		// user name
		String [] userParts = tweetUser.split("\\s+");
		if (userParts.length > 0)
		{
			for (String name : nameMap.keySet())
			{
				String compareUser = userParts[0].toLowerCase();
				// 2-character strings are too short and have errors
				// exact match on user name
				if (compareUser.length() > 2 && compareUser.equals(name.toLowerCase()))
				{ 							
					resultGenderName = nameMap.get(name); 
					//System.out.println("----#1---- " + name);
					break;
				}			
			}
		}
		
		// screen name
		String [] snParts = tweetScreenName.split("\\s+");
		if (snParts.length > 0 && resultGenderName.equals("Ambiguous"))
		{
			Boolean found = false;
			for (String name : nameMap.keySet())
			{
				// since I'm doing contains below, we want to only match on longer names
				if (!found && name.length() > 4)
				{
					for (int i = 0; i < snParts.length; i++)
					{
						String compareSN = snParts[i].toLowerCase();
						// 2-character strings are too short and have errors
						// contains match on screen name
						if (compareSN.length() > 2 && compareSN.contains(name.toLowerCase()))
						{
							// if there are multiple screen name matches, all of them have to give the same gender (conservative)
							if (resultGenderSN.equals(nameMap.get(name)) || resultGenderSN.equals("Ambiguous"))
							{
								resultGenderSN = nameMap.get(name); 
								//System.out.println("----#2---- " + name);
							}
							// multiple screen name matches yielded different genders
							else
							{
								resultGenderSN = "Ambiguous"; 
								//System.out.println("----#2no---- " + name);
								found = true;
								break;
							}
						}
					}
				}
			}
		}
		
		// name match is more accurate than screenname match
		if (resultGenderName.equals("boy")) { return "Male"; }
		else if (resultGenderName.equals("girl")) { return "Female"; }
		else if (resultGenderName.equals("Ambiguous"))
		{
			if (resultGenderSN.equals("boy")) { return "Male"; }
			else if (resultGenderSN.equals("girl")) { return "Female"; }
		}
		else { return "Ambiguous"; }
		return resultGenderName;
	}

	private static void writeTweetInformationToFile()
	{
		try {
			FileWriter writer = new FileWriter("TweetResults.csv");
			//BufferedWriter bw = new BufferedWriter(writer);
		    for(String str: tweetResults){
		        writer.write(str);
		        writer.write("\n");
		    }
		    writer.close();
		}		
		catch (IOException e) {
		    e.printStackTrace();
		}		
	}

	private static String cleanTweettext(String tweetText) {
		if (tweetText.contains("\t")) {
			tweetText = tweetText.replace('\t', ' ');
		}
		if (tweetText.contains("\n")) {
			tweetText = tweetText.replace('\n', ' ');
		}
		return tweetText;
	}
}


