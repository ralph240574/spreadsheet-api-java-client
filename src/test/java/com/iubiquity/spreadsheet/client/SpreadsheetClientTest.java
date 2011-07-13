package com.iubiquity.spreadsheet.client;

import java.io.IOException;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.api.client.http.HttpResponseException;
import com.iubiquity.spreadsheets.client.SpreadsheetClient;
import com.iubiquity.spreadsheets.model.CellEntry;
import com.iubiquity.spreadsheets.model.CellFeed;
import com.iubiquity.spreadsheets.model.SpreadsheetFeed;
import com.iubiquity.spreadsheets.model.WorksheetData;
import com.iubiquity.spreadsheets.model.WorksheetEntry;
import com.iubiquity.spreadsheets.model.WorksheetFeed;

/**
 * This test requires human interaction to run. After starting the test a
 * browser windo will open and ask to login to you google account and accept
 * permissions to use google docs. Any google account will work.
 * 
 * @author ralph
 * 
 */
public class SpreadsheetClientTest {

	private final static String SHARED_WORKSHEET_FEED = "https://spreadsheets.google.com/feeds/worksheets/ttOb9mZkUr3fIkkTOGxyL5w/private/full";
	private final static String SHARED_CELLFEED = "https://spreadsheets.google.com/feeds/cells/ttOb9mZkUr3fIkkTOGxyL5w/od6/private/full";

	private static JavaSpreadsheetClient client;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		client = new JavaSpreadsheetClient(
				ClientCredentials.ENTER_OAUTH_CONSUMER_KEY,
				ClientCredentials.ENTER_OAUTH_CONSUMER_SECRET);
		client.authorize();

		WorksheetData wd = client.getWorksheetData(SHARED_CELLFEED);

		wd.setValue("Column A", 1, 1);
		wd.setValue("Column B", 1, 2);
		wd.setValue("Column C", 1, 3);
		wd.setValue(null, 5, 1);
		wd.setValue(null, 5, 2);
		wd.setValue(null, 5, 3);
		wd.setValue("toDelete", 9, 1);
		wd.setValue(null, 15, 1);
		wd.setValue(null, 15, 2);
		wd.setValue(null, 15, 3);
		wd.setValue("A20", 20, 1);
		wd.setValue("B20", 20, 2);
		wd.setValue("C20", 20, 3);
		wd.setValue(null, 50, 1);
		wd.setValue(null, 50, 20);

		client.batchUpdate(wd);

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {

		client.shutdown();
	}

	@Test
	public void testAuthorize() {
		Assert.assertNotNull(client.requestFactory);
		Assert.assertNotNull(client.requestFactory.transport);
		Assert.assertNotNull(client.requestFactory.initializer);
	}

	@Test
	public void testExecuteDelete() throws IOException {
		WorksheetFeed w = client.executeGetWorksheetFeed(SHARED_WORKSHEET_FEED);
		WorksheetEntry we = w.getWorksheetEntry("Sheet1");
		CellFeed cf = client.executeGetCellFeed(we.getCellFeedLink());
		CellEntry cellEntry = cf.getCellEntry("A9");
		client.executeDelete(cellEntry);
	}

	@Test
	public void testExecuteInsertCellEntryBoolean() throws IOException {
		CellFeed cf = client.executeGetCellFeed(SHARED_CELLFEED);
		CellEntry entry = cf.getCellEntry("A10");
		Assert.assertNotNull(entry);
		String toInsert = "TEST";
		entry.cell.value = toInsert;
		CellEntry response = client.executeInsert(entry, true);
		Assert.assertEquals(toInsert, response.cell.value);
		try {
			response = client.executeInsert(entry, false);
		} catch (HttpResponseException ex) {
			Assert.assertEquals(SpreadsheetClient.CODE_412, ex.getMessage());
		}

	}

	@Test
	public void testBatchUpdateWorksheetData() throws IOException {
		WorksheetData wd = client.getWorksheetData(SHARED_CELLFEED);

		Assert.assertEquals("Column A", wd.getContent(1, 1));

		wd.setValue("yello!", 1, 1);
		wd.setValue("yello!", 1, 2);
		wd.setValue("yello!", 1, 3);
		wd.setValue("yello!", 15, 1);
		wd.setValue("yello!", 15, 2);
		wd.setValue("yello!", 15, 3);

		client.batchUpdate(wd);

		wd.setValue("Column A", 1, 1);
		wd.setValue("Column B", 1, 2);
		wd.setValue("Column C", 1, 3);
		wd.setValue(null, 15, 1);
		wd.setValue(null, 15, 2);
		wd.setValue(null, 15, 3);

		client.batchUpdate(wd);

	}

	@Test
	public void testAddCell() throws IOException {

		WorksheetFeed w = client.executeGetWorksheetFeed(SHARED_WORKSHEET_FEED);
		client.addCell(w.getWorksheetEntry("Sheet1"), "testing", 5, 1);
		client.addCell(w.getWorksheetEntry("Sheet1"), "testing", 5, 2);
		client.addCell(w.getWorksheetEntry("Sheet1"), "testing", 5, 3);
		client.addCell(w.getWorksheetEntry("Sheet1"), "testing", 50, 1);
		client.addCell(w.getWorksheetEntry("Sheet1"), "testing", 50, 20);

	}

	@Test
	public void testExecuteGetCellFeedString() throws IOException {
		CellFeed cf = client.executeGetCellFeed(SHARED_CELLFEED);
		Assert.assertEquals("test", cf.author.name);
		Assert.assertEquals("test@iubiquity.com", cf.author.email);

		WorksheetData wd = new WorksheetData(cf);

		Assert.assertEquals("Column A", wd.getContent(1, 1));
		Assert.assertEquals("Column B", wd.getContent(1, 2));
		Assert.assertEquals("Column C", wd.getContent(1, 3));
		Assert.assertEquals("A20", wd.getContent(20, 1));
		Assert.assertEquals("B20", wd.getContent(20, 2));

	}

	@Test
	public void testExecuteGetWorksheetFeedString() throws IOException {
		WorksheetFeed feed = client
				.executeGetWorksheetFeed(SHARED_WORKSHEET_FEED);
		Assert.assertEquals("TestSpreadsheetShared", feed.title);
		WorksheetEntry entry = feed.getWorksheetEntry("Sheet1");
		Assert.assertEquals("Sheet1", entry.title);
		Assert.assertNotNull(entry.updated);
		Assert.assertEquals(
				"https://spreadsheets.google.com/feeds/worksheets/ttOb9mZkUr3fIkkTOGxyL5w/od6",
				entry.id);
		WorksheetEntry entry2 = feed.getWorksheetEntry("Sheet2");
		Assert.assertEquals("Sheet2", entry2.title);
	}

	@Test
	public void testGetSpreadsheetMetafeed() throws IOException {
		SpreadsheetFeed metaFeed = client.getSpreadsheetMetafeed();
		Assert.assertEquals(3, metaFeed.links.size());
		Assert.assertNotNull(metaFeed.etag);
	}
}
