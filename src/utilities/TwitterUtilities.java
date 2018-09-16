package utilities;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterUtilities {

	// change the next 4 lines to include your personal keys and tokens
	private static final String myPersonalTwitterAPIKey = "BoVeFs9llLfp011KgvFidO7vk"; // put your Consumer Key (API Key) in between the quotes
	private static final String myPersonalSecretTwitterAPIKey = "jEJilMOnOjgrJR4KPvIdM1ofT8Srwc4XczRJkO3kt5LbyI9bLW"; // put your Consumer Secret (API Secret) in between the quotes
	private static final String myPersonalTwitterAccessToken = "408724543-tTerczQAHNOwggxoxRvUiVOFeIrf3wrekH7zlap8"; // put your Access Token in between the quotes
	private static final String myPersonalSecretTwitterAccessToken = "OyHr2fo0CG4SIzF80SvfHhbJdrXmAvmL6uCgob7Fls5lK"; // put your Access Token Secret in between the quotes





	static Twitter twitter = null;


	public static void main(String[] args) {
		Twitter twitter = createTwitterObject();

		try {
			List<String> tweetsByDJT = searchTweetsForUserName(twitter, "realDonaldTrump");
			int numberOfTweets = tweetsByDJT.size();
			System.out.println("# tweets ="+numberOfTweets);
			for (Iterator<String> iterator = tweetsByDJT.iterator(); iterator.hasNext();) {
				String string = (String) iterator.next();
				System.out.println("tweet by DJT:"+string);
			}
		} catch (TwitterException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			List<String> tweets = searchTweetsForWord(twitter, "pepsi");
			if (tweets.size() == 0) {
				System.out.println("no tweets");
			}
			for (String tweet : tweets) {
				System.out.println("Tweet is:"+tweet);
			}

		} catch (TwitterException e) {
			e.printStackTrace();
			System.out.println("Failed to search tweets: " + e.getMessage());
			System.exit(-1);
		}
	}




	private static Twitter createTwitterObject() {
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		.setOAuthConsumerKey(myPersonalTwitterAPIKey)
		.setOAuthConsumerSecret(myPersonalSecretTwitterAPIKey)
		.setOAuthAccessToken(myPersonalTwitterAccessToken)
		.setOAuthAccessTokenSecret(myPersonalSecretTwitterAccessToken)
		.setTweetModeExtended(true);

		TwitterFactory tf = new TwitterFactory(cb.build());
		Twitter twitter = tf.getInstance();
		return twitter;
	}




	public static List<String> searchTweetsForWord(Twitter twitter, String wordToSearchFor) throws TwitterException {
		String queryString = wordToSearchFor;
		return getQueryResults(twitter, queryString);
	}


	/*
	 * userName should not include the initial '@'
	 */
	public static List<String> searchTweetsForUserName(Twitter twitter, String userName) throws TwitterException {
		String properUserName = userName;
		if (properUserName.startsWith("@")) {
			properUserName = properUserName.substring(1);
		}
		//		System.out.println("properUserName="+properUserName);
		String queryString = "from:"+ userName;
		return getQueryResults(twitter, queryString);
	}



	// get tweets that include the word
	private static List<String> getQueryResults(Twitter twitter, String queryString) throws TwitterException {
		List<String> results = new ArrayList<String>();
		List<Status> tweets = getTweetsWithQuery(twitter, queryString);
		for (Iterator<Status> iterator = tweets.iterator(); iterator.hasNext();) {
			Status status = (Status) iterator.next();
			String tweetText = status.getText();
			results.add(tweetText);
		}
	return results;	
//		return tweets.stream().map(item -> item.getText()).collect(Collectors.toList());
	}




	public static List<Status> getTweetsWithQuery(String queryString) throws TwitterException {
		if (twitter == null)
			twitter = createTwitterObject();

		queryString += " lang:en " + " +exclude:retweets ";
		Query query = new Query(queryString);
		query.setCount(100);		// set to 100 to maximize data collection given Twitter API rate limits
		QueryResult result = twitter.search(query);

		List<Status> tweets = result.getTweets();
		return tweets;
	}

	private static List<Status> getTweetsWithQuery(Twitter twitter, String queryString) throws TwitterException {
		Query query = new Query(queryString);
		QueryResult result = twitter.search(query);

		List<Status> tweets = result.getTweets();
		return tweets;
	}



}
