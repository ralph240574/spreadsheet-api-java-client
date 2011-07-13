package com.iubiquity.spreadsheet.example;

import com.google.api.client.http.HttpResponseException;
import com.iubiquity.spreadsheet.client.ClientCredentials;
import com.iubiquity.spreadsheet.client.SpreadsheetClient;
import com.iubiquity.spreadsheet.client.model.SpreadsheetFeed;

/**
 * 
 * This example displays the spreadsheet meta feed
 * 
 * @author ralph
 * 
 */
public class SpreadsheetFeedExample {

	public static void main(String[] args) {

		SpreadsheetClient client = new SpreadsheetClient(
				ClientCredentials.ENTER_OAUTH_CONSUMER_KEY,
				ClientCredentials.ENTER_OAUTH_CONSUMER_SECRET);

		try {
			try {
				client.authorize();
				SpreadsheetFeed feed = client.getSpreadsheetMetafeed();
				View.display(feed);
				client.shutdown();
			} catch (HttpResponseException e) {
				System.err.println(e.response.parseAsString());
				throw e;
			}
		} catch (Throwable t) {
			t.printStackTrace();
			client.shutdown();
			System.exit(1);
		}
	}
}
