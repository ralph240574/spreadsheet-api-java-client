package com.iubiquity.spreadsheet.example;

import com.google.api.client.http.HttpResponseException;
import com.iubiquity.spreadsheet.client.ClientCredentials;
import com.iubiquity.spreadsheet.client.SpreadsheetClient;
import com.iubiquity.spreadsheets.model.CellFeed;
import com.iubiquity.spreadsheets.model.SpreadsheetEntry;
import com.iubiquity.spreadsheets.model.SpreadsheetFeed;
import com.iubiquity.spreadsheets.model.WorksheetData;
import com.iubiquity.spreadsheets.model.WorksheetFeed;


/**
 * This example demonstrated how to access and manipulate the individual cells
 * of a worksheet
 * 
 * @author ralph
 * 
 */
public class WorksheetDataExample {

	public static void main(String[] args) {

		SpreadsheetClient client = new SpreadsheetClient(
				ClientCredentials.ENTER_OAUTH_CONSUMER_KEY,
				ClientCredentials.ENTER_OAUTH_CONSUMER_SECRET);

		/*
		 * use name of an existing spreadsheet
		 */
		final String spreadsheetName = "SampleDeck";
		/*
		 * use name of an existing worksheet in spreadsheet
		 */
		final String worksheetName = "Sheet1";

		try {
			try {
				client.authorize();
				SpreadsheetFeed feed = client.getSpreadsheetMetafeed();
				SpreadsheetEntry spreadsheet = feed
						.getSpreadsheetEntry(spreadsheetName);
				View.display(spreadsheet);
				WorksheetFeed w = client.executeGetWorksheetFeed(spreadsheet
						.getWorksheetFeedLink());

				String cellFeedUrl = w.getWorksheetEntry(worksheetName)
						.getCellFeedLink();

				WorksheetData wd = client.getWorksheetData(cellFeedUrl);

				View.display(wd);

				// this gets the string in cell(1,1)
				wd.getContent(1, 1);

				// this will delete the entry in (2,1) and removes the cell from
				// the cellfeed
				wd.setValue(null, 2, 1);

				// this sets the value, if cell did not exist in the feed cell
				// will be created
				wd.setValue("yello", 3, 4);

				// this will put the change to the server
				CellFeed result = client.batchUpdate(wd);

				System.out.println(result.getBatchError());

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
