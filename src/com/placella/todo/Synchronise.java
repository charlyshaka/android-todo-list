package com.placella.todo;

import java.net.*;
import java.util.*;

import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.*;

import android.app.*;
import android.app.Notification.Builder;
import android.content.*;
import android.net.Uri;
import android.os.Bundle;

public abstract class Synchronise {
	private static String response = "";
	private static Context context;
	private static List<Item> list;

	public static void start(Context context, List<Item> list) {
		Synchronise.context = context;
		Synchronise.list = list;
		new Thread(
			new Runnable() {
				public void run() {
					Synchronise.request();
				}
			}
		).start();
	}
	
	private static synchronized void request() {
		boolean success = put(createJson(list), context);
				
		Intent intent;
		if (! success) {
			intent = new Intent(context, Activity_Notification.class);
	        Bundle b = new Bundle();
	        b.putString("response", response);
	        b.putBoolean("success", false);
	        intent.putExtras(b);
		} else {
			System.out.println(response);
			intent = new Intent(Intent.ACTION_VIEW, Uri.parse(response));
		}
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);
        Builder builder = new Notification.Builder(context);
        if (success) {
        	builder
	            .setContentTitle(context.getResources().getString(R.string.sync_ok))
	            .setContentText(context.getResources().getString(R.string.sync_ok_full)).setSmallIcon(R.drawable.ic_menu_about);
        } else {
        	builder
            	.setContentTitle(context.getResources().getString(R.string.sync_failed))
	            .setContentText(context.getResources().getString(R.string.sync_failed_full)).setSmallIcon(R.drawable.ic_menu_about);
        }
        Notification n = builder.setContentIntent(pIntent).build();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        n.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, n);
	}
	
	private static boolean put(String data, Context context) {
		boolean success = true;
		try {
			HttpResponse httpResponse;
			URI url;
			url = new URI("http://todo.placella.com/");
			HttpClient client = new DefaultHttpClient();
			HttpPut put = new HttpPut(url);
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("data", data));
			put.setEntity(new UrlEncodedFormEntity(pairs));
			httpResponse = client.execute(put);
			int statuscode = httpResponse.getStatusLine().getStatusCode();
			if (statuscode == 200) {
				HttpEntity responseEntity = httpResponse.getEntity();
				if (responseEntity != null) {
					try {
						response = EntityUtils.toString(responseEntity);
					} catch (Exception e) {
						success = false;
						response = e.toString();
					}
				} else {
					success = false;
					response = "Can't decode response";
				}
			} else {
				success = false;
				response = "HTTP error " + statuscode;
			}
		} catch (Exception e) {
			success = false;
			response = e.toString();
		}
		return success;
	}
	
	private static String createJson(List<Item> list) {
    	JSONArray root = new JSONArray();
        try {
        	for (Item i : list) {
                JSONObject itemObj = new JSONObject();
                itemObj.put("name", i.getName());
                itemObj.put("type", i.getType());
                itemObj.put("note", i.getNotecontent());
            	JSONArray array = new JSONArray();
            	for (Item inner : i.getListcontent()) {
                    JSONObject innerObj = new JSONObject();
            		innerObj.put("txt", inner.getName());
            		innerObj.put("sel", inner.getState());
            		array.put(innerObj);
            	}
            	itemObj.put("list", array);
            	root.put(itemObj);
        	}
		} catch (JSONException e) {
			e.printStackTrace();
		}
        return root.toString();
	}
}
