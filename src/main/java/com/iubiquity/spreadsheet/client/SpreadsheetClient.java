package com.iubiquity.spreadsheet.client;

import java.io.IOException;
import java.util.List;

import com.google.api.client.auth.oauth.OAuthParameters;
import com.google.api.client.googleapis.GoogleHeaders;
import com.google.api.client.googleapis.GoogleUrl;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpExecuteInterceptor;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.HttpUnsuccessfulResponseHandler;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.xml.atom.AtomContent;
import com.google.api.client.http.xml.atom.AtomFeedContent;
import com.google.api.client.http.xml.atom.AtomParser;
import com.google.api.client.xml.XmlNamespaceDictionary;
import com.google.common.collect.Lists;
import com.iubiquity.spreadsheets.model.CellEntry;
import com.iubiquity.spreadsheets.model.CellFeed;
import com.iubiquity.spreadsheets.model.Feed;
import com.iubiquity.spreadsheets.model.Link;
import com.iubiquity.spreadsheets.model.SpreadsheetFeed;
import com.iubiquity.spreadsheets.model.SpreadsheetUrl;
import com.iubiquity.spreadsheets.model.WorksheetData;
import com.iubiquity.spreadsheets.model.WorksheetEntry;
import com.iubiquity.spreadsheets.model.WorksheetFeed;

public class SpreadsheetClient {

	/**
   * 
   */
	private static final String GOOGLE_SPREADSHEET_SAMPLE = "Google-Spreadsheet-API";

	public static final XmlNamespaceDictionary DICTIONARY = new XmlNamespaceDictionary()
			.set("", "http://www.w3.org/2005/Atom")
			.set("gd", "http://schemas.google.com/g/2005")
			.set("gs", "http://schemas.google.com/spreadsheets/2006")
			.set("gsx", "http://schemas.google.com/spreadsheets/2006/extended")
			.set("openSearch", "http://a9.com/-/spec/opensearch/1.1/")
			.set("xml", "http://www.w3.org/XML/1998/namespace")
			.set("app", "http://www.w3.org/2007/app");

	private final HttpTransport transport = new NetHttpTransport();

	HttpRequestFactory requestFactory;

	private String consumerKey;

	private String consumerSecret;

	public static final String CODE_412 = "412 Precondition Failed";

	public SpreadsheetClient(String enterOauthConsumerKey,
			String enterOauthConsumerSecret) {
		this.consumerKey = enterOauthConsumerKey;
		this.consumerSecret = enterOauthConsumerSecret;
	}

	public void authorize() throws Exception {
		final OAuthParameters oauthParameters = OAuth.authorize(transport,
				consumerKey, consumerSecret); // OAuth
		// TODO check if needed
		// final MethodOverride override = new MethodOverride(); // needed for
		// PATCH
		requestFactory = transport
				.createRequestFactory(new HttpRequestInitializer() {

					String gsessionid;

					@Override
					public void initialize(HttpRequest request) {
						GoogleHeaders headers = new GoogleHeaders();
						headers.setApplicationName(GOOGLE_SPREADSHEET_SAMPLE);
						headers.gdataVersion = "2";
						request.headers = headers;
						AtomParser parser = new AtomParser();
						parser.namespaceDictionary = DICTIONARY;
						request.addParser(parser);
						request.interceptor = new HttpExecuteInterceptor() {

							@Override
							public void intercept(HttpRequest request)
									throws IOException {
								request.url.set("gsessionid", gsessionid);
								// override.intercept(request);
								oauthParameters.intercept(request);
							}
						};
						request.unsuccessfulResponseHandler = new HttpUnsuccessfulResponseHandler() {

							@Override
							public boolean handleResponse(HttpRequest request,
									HttpResponse response,
									boolean retrySupported) {
								if (response.statusCode == 302) {
									GoogleUrl url = new GoogleUrl(
											response.headers.location);
									gsessionid = (String) url
											.getFirst("gsessionid");
									return true;
								}
								return false;
							}
						};
					}
				});
	}

	public void shutdown() {
		try {
			transport.shutdown();
		} catch (IOException exception) {
			exception.printStackTrace();
		}
		OAuth.revoke(transport, consumerKey);
	}

	public void initializeParser(HttpRequest request) {
		AtomParser parser = new AtomParser();
		parser.namespaceDictionary = DICTIONARY;
		request.addParser(parser);
	}

	public void executeDelete(CellEntry cellEntry) throws IOException {
		HttpRequest request = requestFactory.buildDeleteRequest(new GenericUrl(
				cellEntry.getEditLink()));
		request.headers.ifMatch = cellEntry.etag;
		request.execute().ignore();
	}

	public CellEntry executeInsert(CellEntry entry, boolean matchTag)
			throws IOException {
		AtomContent content = new AtomContent();
		content.namespaceDictionary = DICTIONARY;
		content.entry = entry;
		HttpRequest request = requestFactory.buildPutRequest(
				new SpreadsheetUrl(entry.getEditLink()), content);

		if (matchTag) {
			// this will only insert if there has been no modification.
			request.headers.ifMatch = ((CellEntry) entry).etag;
		} else {
			// this will only insert if there has been a modification
			request.headers.ifNoneMatch = ((CellEntry) entry).etag;
		}
		return request.execute().parseAs(entry.getClass());
	}

	public CellEntry addCellEntry(CellEntry cellEntry,
			SpreadsheetUrl cellfeedUrl) throws IOException {
		AtomContent content = new AtomContent();
		content.namespaceDictionary = DICTIONARY;
		content.entry = cellEntry;
		HttpRequest request = requestFactory.buildPostRequest(cellfeedUrl,
				content);
		request.headers.ifNoneMatch = ((CellEntry) cellEntry).etag;
		return request.execute().parseAs(cellEntry.getClass());
	}

	public CellFeed batchUpdate(final WorksheetData wd) throws IOException {
		String link = Link.find(wd.cellFeed.links, "self");
		SpreadsheetUrl url = new SpreadsheetUrl(link);
		for (CellEntry ce : wd.toInsert) {
			addCellEntry(ce, url);
		}
		return batchUpdate(wd.cellFeed);
	}

	public CellFeed batchUpdate(CellFeed feed) throws IOException {
		List<CellEntry> updatedCells = Lists.newArrayList();
		for (CellEntry ce : feed.cells) {
			if (ce.batchId != null) {
				updatedCells.add(ce);
			}
		}
		feed.cells = updatedCells;
		SpreadsheetUrl url = new SpreadsheetUrl(feed.getBatchLink());
		AtomFeedContent content = new AtomFeedContent();
		content.namespaceDictionary = DICTIONARY;
		content.feed = feed;
		HttpRequest request = requestFactory.buildPostRequest(url, content);
		request.headers.ifNoneMatch = "whatever";
		return request.execute().parseAs(CellFeed.class);
	}

	/**
	 * @param we
	 *            Worksheet to insert to
	 * @param value
	 *            String to insert
	 * @param row
	 *            starts with 1
	 * @param column
	 *            starts with 1
	 * @return
	 * @throws IOException
	 */
	public CellEntry addCell(WorksheetEntry we, String value, int row, int col)
			throws IOException {
		CellEntry ce = CellEntry.makeInstance(value, row, col);
		new CellEntry();
		String link = we.getCellFeedLink();
		return addCellEntry(ce, new SpreadsheetUrl(link));
	}

	public CellEntry executeInsert(CellEntry entry) throws IOException {
		return executeInsert(entry, true);
	}

	<F extends Feed> F executeGetFeed(SpreadsheetUrl url, Class<F> feedClass)
			throws IOException {
		HttpRequest request = requestFactory.buildGetRequest(url);
		return request.execute().parseAs(feedClass);
	}

	public HttpResponse execute(SpreadsheetUrl url) throws IOException {
		HttpRequest request = requestFactory.buildGetRequest(url);
		return request.execute();
	}

	public SpreadsheetFeed executeGetSpreadsheetFeed(SpreadsheetUrl url)
			throws IOException {
		return executeGetFeed(url, SpreadsheetFeed.class);
	}

	public CellFeed executeGetCellFeed(SpreadsheetUrl url) throws IOException {
		return executeGetFeed(url, CellFeed.class);
	}

	public CellFeed executeGetCellFeed(String url) throws IOException {
		return executeGetCellFeed(new SpreadsheetUrl(url));
	}

	public WorksheetFeed executeGetWorksheetFeed(SpreadsheetUrl url)
			throws IOException {
		return executeGetFeed(url, WorksheetFeed.class);
	}

	public WorksheetFeed executeGetWorksheetFeed(final String url)
			throws IOException {
		return executeGetWorksheetFeed(new SpreadsheetUrl(url));
	}

	public SpreadsheetFeed getSpreadsheetMetafeed() throws IOException {
		return executeGetSpreadsheetFeed(SpreadsheetUrl
				.forSpreadSheetMetafeed());
	}

	public WorksheetData getWorksheetData(final String cellFeedURL)
			throws IOException {
		return new WorksheetData(executeGetCellFeed(cellFeedURL));
	}

}
