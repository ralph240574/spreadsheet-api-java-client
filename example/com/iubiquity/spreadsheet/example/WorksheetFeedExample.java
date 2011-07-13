package com.iubiquity.spreadsheet.example;

import com.google.api.client.http.HttpResponseException;
import com.iubiquity.spreadsheet.client.ClientCredentials;
import com.iubiquity.spreadsheet.client.SpreadsheetClient;
import com.iubiquity.spreadsheets.model.SpreadsheetEntry;
import com.iubiquity.spreadsheets.model.SpreadsheetFeed;
import com.iubiquity.spreadsheets.model.WorksheetFeed;

/**
 * This example displays the specified SpreadsheetEntry and WorksheetFeed
 * 
 * @author ralph
 * 
 */
public class WorksheetFeedExample {

	public static void main(String[] args) {

		SpreadsheetClient client = new SpreadsheetClient(
				ClientCredentials.ENTER_OAUTH_CONSUMER_KEY,
				ClientCredentials.ENTER_OAUTH_CONSUMER_SECRET);

		/*
		 * use name of an existing spreadsheet
		 */
		final String spreadsheetName = "SampleDeck";

		try {
			try {
				client.authorize();
				SpreadsheetFeed feed = client.getSpreadsheetMetafeed();
				SpreadsheetEntry spreadsheet = feed
						.getSpreadsheetEntry(spreadsheetName);
				View.display(spreadsheet);
				WorksheetFeed w = client.executeGetWorksheetFeed(spreadsheet
						.getWorksheetFeedLink());
				View.display(w);
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
