package com.mio.app.mioapp.util;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class RequestHttp {

	public static final int RESQUEST_DELETE = 0;
	public static final int RESQUEST_GET = 1;
	public static final int RESQUEST_POST = 2;

	private List<BasicNameValuePair> params;
	private HttpPost httppost;
	private HttpDelete httpDelete;
	private HttpGet httpGet;
	private int tipo = 0;
	private String webservice = "";
    private int timeOut = 0;
	/*
	 * constructor
	 */
	public RequestHttp(String webservice, int tipo) {

		this.tipo = tipo;
		
		params = new ArrayList<BasicNameValuePair>();
		if(tipo == RESQUEST_POST)
			httppost = new HttpPost(webservice);
		else
			webservice = webservice+"?";
		
		this.webservice = webservice;
			

	}
	public void setTimeOut(int timeOut)
	{
		this.timeOut = timeOut;
	}

	/**
	 * envia la peticion al servidor
	 * 
	 * @return la respuesta del servidor
	 */
	public String send() {
		
		Log.d("eka", " " + webservice);

		for (BasicNameValuePair c : params) {
			Log.d("eka", " " + c.getName() + " ***  " + c.getValue());

		}
		HttpParams httpParameters = new BasicHttpParams();
        
		HttpConnectionParams.setConnectionTimeout(httpParameters, (timeOut!=0)?timeOut:60000);
		HttpConnectionParams.setSoTimeout(httpParameters, (timeOut!=0)?timeOut:60000);
		HttpClient httpclient = new DefaultHttpClient(httpParameters);
		HttpResponse httpResponse = null;
		try {
			switch (tipo) {
			case RESQUEST_DELETE:
				httpDelete = new HttpDelete(webservice);
				httpResponse = httpclient.execute(httpDelete);
				break;

			case RESQUEST_GET:
				httpGet = new HttpGet(webservice);
				httpResponse = httpclient.execute(httpGet);
				break;
			case RESQUEST_POST:
				httppost.setEntity(new UrlEncodedFormEntity(params));
				httpResponse = httpclient.execute(httppost);
				break;

			default:
				break;
			}

			InputStream inputStream = httpResponse.getEntity().getContent();
			InputStreamReader inputStreamReader = new InputStreamReader(
					inputStream);

			BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);

			StringBuilder stringBuilder = new StringBuilder();

			String bufferedStrChunk = null;

			while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
				stringBuilder.append(bufferedStrChunk);
			}
			Log.e("eka", "" + stringBuilder.toString());
			return stringBuilder.toString();

		} catch (ClientProtocolException e) {
			Log.e("eka", "" + e.getMessage());
			return null;
		} catch (IOException e) {
			Log.e("eka", "" + e.getMessage());
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("eka", "" + e.getMessage());
			return null;
		}

	}

	public void addParam(String key, String value) {
		
		if(tipo == RESQUEST_POST)
			params.add(new BasicNameValuePair(key, value));
		else
			webservice = webservice + key +"=" +value+"&";
			
		
	}

	public void setHeader(String key, String value) {
		httppost.setHeader(key, value);
	}

}
