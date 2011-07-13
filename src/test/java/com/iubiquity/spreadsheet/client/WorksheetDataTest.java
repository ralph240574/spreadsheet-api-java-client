package com.iubiquity.spreadsheet.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import com.iubiquity.spreadsheets.model.Cell;
import com.iubiquity.spreadsheets.model.CellEntry;
import com.iubiquity.spreadsheets.model.CellFeed;
import com.iubiquity.spreadsheets.model.WorksheetData;

public class WorksheetDataTest {

	private final WorksheetData wd = makeWorksheetData();

	@Before
	public void setUp() throws Exception {
	}

	private WorksheetData makeWorksheetData() {

		CellFeed cellFeed = new CellFeed();
		CellEntry entry = new CellEntry();
		entry.content = "A";
		entry.cell = new Cell();
		entry.cell.row = 1;
		entry.cell.col = 1;
		cellFeed.cells.add(entry);

		entry = new CellEntry();
		entry.content = "B";
		entry.cell = new Cell();
		entry.cell.row = 1;
		entry.cell.col = 2;
		cellFeed.cells.add(entry);

		entry = new CellEntry();
		entry.content = "One";
		entry.cell = new Cell();
		entry.cell.row = 2;
		entry.cell.col = 1;
		cellFeed.cells.add(entry);

		entry = new CellEntry();
		entry.content = "Uno";
		entry.cell = new Cell();
		entry.cell.row = 2;
		entry.cell.col = 2;
		cellFeed.cells.add(entry);

		return new WorksheetData(cellFeed);
	}

	@Test
	public void testGetContent() {
		assertEquals("A", wd.getContent(1, 1));
		assertEquals("B", wd.getContent(1, 2));
		assertEquals("One", wd.getContent(2, 1));
		assertEquals("Uno", wd.getContent(2, 2));
		assertNull(wd.getContent(3, 1));
	}

	@Test
	public void testSetValue() {
		wd.setValue("YO", 1, 1);
		assertEquals("YO", wd.getContent(1, 1));
		wd.setValue("YO", 2, 1);
		assertEquals("YO", wd.getContent(2, 1));
		wd.setValue("YO", 2, 2);
		assertEquals("YO", wd.getContent(2, 2));

		// adding new cells
		wd.setValue("YO", 3, 3);
		assertEquals("YO", wd.getContent(3, 3));

		wd.setValue("YO", 20, 20);
		assertEquals("YO", wd.getContent(20, 20));

	}

}
