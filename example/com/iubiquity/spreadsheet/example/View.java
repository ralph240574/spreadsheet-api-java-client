package com.iubiquity.spreadsheet.example;



import java.util.Map.Entry;
import java.util.SortedMap;

import com.iubiquity.spreadsheets.model.CellEntry;
import com.iubiquity.spreadsheets.model.CellFeed;
import com.iubiquity.spreadsheets.model.Link;
import com.iubiquity.spreadsheets.model.SpreadsheetEntry;
import com.iubiquity.spreadsheets.model.SpreadsheetFeed;
import com.iubiquity.spreadsheets.model.WorksheetData;
import com.iubiquity.spreadsheets.model.WorksheetFeed;

public class View {

	static void header(String name) {
		System.out.println();
		System.out.println("============== " + name + " ==============");
		System.out.println();
	}

	static void display(SpreadsheetFeed feed) {
		for (SpreadsheetEntry entry : feed.getEntries()) {
			System.out.println();
			System.out
					.println("-----------------------------------------------");
			display(entry);
		}
	}

	static void display(CellFeed feed) {
		System.out.println(feed.title);
		for (CellEntry entry : feed.getEntries()) {
			System.out
					.println("-----------------------------------------------");
			display(entry);
		}
	}

	static void display(SpreadsheetEntry entry) {
		System.out.println("Title: " + entry.title);
		System.out.println("Updated: " + entry.updated);
		System.out.println("ID: " + entry.id);

		System.out.println("Author: " + entry.author.name);
		System.out.println("Author: " + entry.author.email);

		for (Link link : entry.links) {
			System.out.print(link.rel);
			System.out.println(" " + link.href);
		}
	}

	/**
	 * @param feed
	 */
	public static void display(WorksheetFeed feed) {
		System.out.println(feed.title);
		System.out.println(feed.author.name);
	}

	public static void display(WorksheetData wd) {
		for (Entry<Integer, SortedMap<Integer, CellEntry>> row : wd.rows
				.entrySet()) {
			System.out.print("Row " + row.getKey() + " \t|");
			for (Entry<Integer, CellEntry> entry : row.getValue().entrySet()) {
				System.out.print(entry.getValue().content + "|");
			}
			System.out.println();
		}
	}

	public static void display(CellEntry cellEntry) {
		System.out.println("id:" + cellEntry.id);
		System.out.println("title: " + cellEntry.title);
		System.out.println("batchid: " + cellEntry.batchId);
		System.out.println("content: " + cellEntry.content);
		if (cellEntry.cell != null) {
			System.out.println(cellEntry.cell.value);
			System.out.println(cellEntry.cell.row);
			System.out.println(cellEntry.cell.col);
		}

	}

}
