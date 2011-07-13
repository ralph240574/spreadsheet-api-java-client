package com.iubiquity.spreadsheet.client;

import java.io.IOException;

import com.google.api.client.auth.oauth.OAuthParameters;
import com.google.api.client.googleapis.GoogleHeaders;
import com.google.api.client.googleapis.GoogleUrl;
import com.google.api.client.http.HttpExecuteInterceptor;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.HttpUnsuccessfulResponseHandler;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.xml.atom.AtomParser;
import com.google.api.client.xml.XmlNamespaceDictionary;
import com.iubiquity.spreadsheets.client.AbstractSpreadsheetClient;

public class SpreadsheetClient extends AbstractSpreadsheetClient {

	private static final String GOOGLE_SPREADSHEET_SAMPLE = "Google-Spreadsheet-API";

	private final HttpTransport transport = new NetHttpTransport();

	private String consumerKey;

	private String consumerSecret;

	public SpreadsheetClient(String enterOauthConsumerKey,
			String enterOauthConsumerSecret) {
		this.consumerKey = enterOauthConsumerKey;
		this.consumerSecret = enterOauthConsumerSecret;
	}

	public void authorize() throws Exception {
		final OAuthParameters oauthParameters = OAuth.authorize(transport,
				consumerKey, consumerSecret); // OAuth
		super.requestFactory = transport
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

}
