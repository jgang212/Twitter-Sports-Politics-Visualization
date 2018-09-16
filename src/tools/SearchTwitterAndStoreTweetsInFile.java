package tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.User;
import utilities.TwitterUtilities;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/*
 * This program actually calls Twitter three times every time you run it.
 * Therefore running it five times causes 15 Twitter calls, which is the limit for a 15 minute window.
 */
public class SearchTwitterAndStoreTweetsInFile {

	// want all Tweets
	static String queryString1 = "";

	public static String tweetsFileName = "Tweets";


	public static void main(String[] args) {
		
		// call this every 65 seconds to continuously collect data
		final long timeInterval = 65000;
		Runnable runnable = new Runnable()
		{
			public void run()
			{
				while (true)
				{
					try {
						List<Status> tweets = TwitterUtilities.getTweetsWithQuery(queryString1);

						writeTweetsToFile(tweets);
					} catch (TwitterException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					try {
						Thread.sleep(timeInterval);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		
		Thread thread = new Thread(runnable);
		thread.start();

	}


	private static void writeTweetsToFile(List<Status> tweets) {
		// add timestamp to filename to differentiate
		DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss");
		Date date = new Date();
		tweetsFileName = "Tweets_" + dateFormat.format(date) + ".csv";
		int lines = 0;
		PrintWriter pw;
		try {
			pw = new PrintWriter(new File(tweetsFileName));

			for (Iterator<Status> iterator = tweets.iterator(); iterator.hasNext();) {
				Status tweet = (Status) iterator.next();
				User user = tweet.getUser();
				String userName = "";
				String userLocation = "";
				String userScreenName = "";
				// only want tweets with user info
				if (user != null)
				{
					// only want English tweets
					if (user.getLang().equals("en") && !user.getLocation().equals(""))
					{
						userName = user.getName();
						userLocation = user.getLocation();
						userScreenName = user.getScreenName();

						String tweetText = tweet.getText();
						tweetText = cleanTweettext(tweetText);
						pw.println( tweetText + '\t' + userName + '\t' + userLocation + '\t' + userScreenName);
						lines++;
					}
				}				
			}

			pw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Wrote "+lines+" tweets to file "+tweetsFileName);
	}


	private static String cleanTweettext(String tweetText) {
		if (tweetText.contains("\t")) {
//			System.out.println("TWEET INCLUDES TAB, filtering");
//			System.out.println(tweetText);
			tweetText = tweetText.replace('\t', ' ');
//			System.out.println(" new tweetText:");
//			System.out.println(tweetText);
		}
		if (tweetText.contains("\n")) {
//			System.out.println("TWEET INCLUDES return, filtering");
//			System.out.println(tweetText);
			tweetText = tweetText.replace('\n', ' ');
//			System.out.println(" new tweetText:");
//			System.out.println(tweetText);
		}
		return tweetText;
	}

}
