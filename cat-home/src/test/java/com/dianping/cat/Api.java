package com.dianping.cat;

import java.io.IOException;
import java.io.InputStream;

import org.unidal.helper.Urls;
import org.unidal.webres.helper.Files;
import org.unidal.webres.json.JsonArray;
import org.unidal.webres.json.JsonObject;

public class Api {

	private static String BU_API = "http://api.cmdb.dp/api/v0.1/bu";

	private static String PROJECT_API = "http://api.cmdb.dp/api/v0.1/bu/%s/products?page=%s";

	public static void main(String args[]) throws Exception {
		String content = fetchContent(BU_API);

		JsonObject object = new JsonObject(content);
		JsonArray projectArray = object.getJSONArray("bu");
		int length = projectArray.length();

		for (int i = 0; i < length; i++) {
			JsonObject project = projectArray.getJSONObject(i);
			String bu = project.getString("bu_name");

			String nextUrl = String.format(PROJECT_API, bu, String.valueOf(1));
			String detailContent = fetchContent(nextUrl);
			print(bu, detailContent);

			findNextProjects(bu, detailContent);
			
			System.out.println();
		}
	}

	private static void findNextProjects(String bu, String detailContent) throws Exception, IOException {
		JsonObject jobject = new JsonObject(detailContent);
		int number = jobject.getInt("numfound");
		int index = (int) Math.ceil(number * 1.0 / 25.0);


		for (int j = 2; j <= index; j++) {
			String nextUrl = String.format(PROJECT_API, bu, String.valueOf(j));
			String content = fetchContent(nextUrl);

			print(bu, content);
		}
	}

	private static void print(String bu, String detailContent) throws Exception {
		JsonObject object = new JsonObject(detailContent);
		JsonArray projectArray = object.getJSONArray("products");
		int length = projectArray.length();

		for (int i = 0; i < length; i++) {
			JsonObject project = projectArray.getJSONObject(i);
			String projectName = project.getString("product_name");

			System.out.println(bu + "\t" + projectName);
		}
	}

	private static String fetchContent(String url) throws IOException {
		InputStream in = Urls.forIO().readTimeout(1000).connectTimeout(1000).openStream(url);
		String content = Files.forIO().readFrom(in, "utf-8");
		return content;
	}

}
