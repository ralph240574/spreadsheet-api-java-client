package com.iubiquity.spreadsheet.client.debug;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.google.api.client.http.HttpResponse;

public class Helper {

	public static String toString(HttpResponse httpResponse) throws IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader in = new BufferedReader(new InputStreamReader(
				httpResponse.getContent()));
		String line = null;
		while ((line = in.readLine()) != null) {
			sb.append(line);
		}
		return sb.toString();
	}

}
