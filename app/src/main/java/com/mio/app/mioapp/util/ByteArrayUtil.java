package com.mio.app.mioapp.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

public class ByteArrayUtil {

	public static byte[] getBytes(InputStream input) {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		int nRead;
		byte[] data = new byte[2048];

		try {
			while ((nRead = input.read(data, 0, data.length)) != -1) {
				buffer.write(data, 0, nRead);
			}

			buffer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return buffer.toByteArray();
	}

	public static Bitmap getImage(byte[] array) {
		return BitmapFactory.decodeByteArray(array, 0, array.length);
	}
	public static byte[] getBytesURL(String url)
	{
		byte[] tmp = null;
		try {
			InputStream input = new java.net.URL(url).openStream();
			tmp               = getBytes(input);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return tmp;
	}
	
	public static byte[] getBytesBitmap(Bitmap bitmap) {
		
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		if(bitmap != null)
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
		return stream.toByteArray();
	}

}
