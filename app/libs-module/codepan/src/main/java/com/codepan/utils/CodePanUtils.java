package com.codepan.utils;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader.TileMode;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings.Secure;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.codepan.R;
import com.codepan.database.SQLiteAdapter;
import com.codepan.widget.CodePanLabel;
import com.codepan.widget.CustomTypefaceSpan;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CodePanUtils {

	public static void cancelAlarm(Context context, Class<?> receiver, int requestCode) {
		Intent intent = new Intent(context, receiver);
		PendingIntent pi = PendingIntent.getBroadcast(context, requestCode, intent, 0);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(pi);
	}

	public static void closePane(Context context, final View view, int id) {
		Animation anim = AnimationUtils.loadAnimation(context, id);
		view.setAnimation(anim);
		anim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation animation) {
				view.setVisibility(View.GONE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationStart(Animation animation) {
			}
		});
		view.startAnimation(anim);
	}

	public static Drawable convertBitmapToDrawable(Context context, Bitmap bitmap) {
		return new BitmapDrawable(context.getResources(), bitmap);
	}

	public static long convertDateToMillis(String date) {
		return convertDateTimeToMillis(date, "00:00:00");
	}

	public static long convertDateTimeToMillis(String date, String time) {
		long millis = 0;
		if(date == null || time == null || date.isEmpty() || time.isEmpty()) {
			return millis;
		}
		String dateArray[] = date.split("\\-");
		String timeArray[] = time.split("\\:");
		int year = Integer.parseInt(dateArray[0]);
		int month = Integer.parseInt(dateArray[1]);
		int day = Integer.parseInt(dateArray[2]);
		int hour = Integer.parseInt(timeArray[0]);
		int min = Integer.parseInt(timeArray[1]);
		int sec = Integer.parseInt(timeArray[2]);
		Calendar cal = Calendar.getInstance();
		cal.set(year, month - 1, day, hour, min, sec);
		millis = cal.getTimeInMillis();
		return millis;
	}

	public static String convertMilitaryToNormalTime(String militaryTime, boolean hasSecond) {
		String normalTime = "";
		String dDesc = "";
		if(militaryTime == null || militaryTime.isEmpty()) {
			return normalTime;
		}
		String[] timeArray = militaryTime.split(":");
		int hour_24 = Integer.parseInt(timeArray[0]);
		int hour = 0;
		if(hour_24 > 12) {
			hour = hour_24 - 12;
			dDesc = "pm";
		}
		else if(hour_24 == 0) {
			hour = 12;
			dDesc = "am";
		}
		else if(hour_24 == 12) {
			hour = 24 - hour_24;
			dDesc = "pm";
		}
		else {
			hour = hour_24;
			dDesc = "am";
		}
		String sHour = String.format(Locale.ENGLISH, "%02d", hour);
		if(!hasSecond) {
			normalTime = sHour + ":" + timeArray[1] + " " + dDesc;
		}
		else {
			normalTime = sHour + ":" + timeArray[1] + ":" + timeArray[2];
		}
		return normalTime;
	}

	public static String convertMinutesToTime(int minutes) {
		int hour = minutes / 60;
		int min = minutes % 60;
		int sec = 0;
		String time = String.format(Locale.ENGLISH, "%02d", hour) + ":" + String.format(Locale.ENGLISH, "%02d", min) + ":" + String.format(Locale.ENGLISH, "%02d", sec);
		return time;
	}


	public static String convertSecondsToTime(int seconds) {
		int hour = seconds / 3600;
		int min = (seconds % 3600) / 60;
		int sec = (seconds % 3600) % 60;
		;
		String time = String.format(Locale.ENGLISH, "%02d", hour) + ":" + String.format(Locale.ENGLISH, "%02d", min) + ":" + String.format(Locale.ENGLISH, "%02d", sec);
		return time;
	}

	public static int convertTimeToMinutes(String militarytime) {
		int totalMinutes = 0;
		if(militarytime == null || militarytime.isEmpty()) {
			return totalMinutes;
		}
		String[] timeArray = militarytime.split(":");
		int timeHour = Integer.parseInt(timeArray[0]);
		int timeMin = Integer.parseInt(timeArray[1]);
		totalMinutes = (timeHour * 60) + timeMin;
		return totalMinutes;
	}

	public static int convertTimeToSeconds(String militarytime) {
		int totaSeconds = 0;
		if(militarytime.isEmpty()) {
			return totaSeconds;
		}
		String[] timeArray = militarytime.split(":");
		int timeHour = Integer.parseInt(timeArray[0]);
		int timeMin = Integer.parseInt(timeArray[1]);
		int timeSec = Integer.parseInt(timeArray[2]);
		totaSeconds = (((timeHour * 60) + timeMin) * 60) + timeSec;
		return totaSeconds;
	}

	public static String convertUnicodeToString(String text) {
		String utf8 = "";
		try {
			byte[] convertToBytes = text.getBytes("UTF-8");
			utf8 = new String(convertToBytes, Charset.forName("UTF-8"));
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return utf8;
	}

	@SuppressWarnings("resource")
	public static void copyFile(File src, File dst) throws IOException {
		FileChannel inChannel = new FileInputStream(src).getChannel();
		FileChannel outChannel = new FileOutputStream(dst).getChannel();
		try {
			inChannel.transferTo(0, inChannel.size(), outChannel);
		}
		finally {
			if(inChannel != null)
				inChannel.close();
			if(outChannel != null)
				outChannel.close();
		}
	}

	public static NotificationCompat.Builder createNotificationBuilder(Context context, int resID) {
		NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
		builder.setSmallIcon(resID);
		builder.setPriority(Notification.PRIORITY_HIGH);
		builder.setOnlyAlertOnce(true);
		builder.setAutoCancel(true);
		return builder;
	}

	public static String createParsableString(ArrayList<String> list, String delimeter) {
		String parsableString = "";
		int position = 0;
		for(String s : list) {
			if(position == list.size() - 1) {
				parsableString = parsableString + s;
			}
			else {
				parsableString = parsableString + s + delimeter;
			}
			position++;
		}
		return parsableString;
	}

	public static boolean decryptFile(Context context, String folderName, String fileName, String password, String extFile) {
		boolean result = false;
		String path = context.getDir(folderName, Context.MODE_PRIVATE).getPath() + "/" + fileName;
		if(fileName.contains(extFile)) {
			return true;
		}
		try {
			File file = new File(path);
			FileInputStream fis = new FileInputStream(file);
			FileOutputStream fos = new FileOutputStream(path + extFile);
			byte[] key = generateKey(password, 16);
			SecretKeySpec sks = new SecretKeySpec(key, "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, sks);
			CipherInputStream cis = new CipherInputStream(fis, cipher);
			int b = 0;
			byte[] d = new byte[8];
			while((b = cis.read(d)) != -1) {
				fos.write(d, 0, b);
			}
			fos.flush();
			fos.close();
			cis.close();
			result = file.delete();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static boolean deleteFile(String path) {
		boolean result = false;
		File file = new File(path);
		if(file.exists() && !file.isDirectory()) {
			result = file.delete();
		}
		return result;
	}

	public static void deleteFiles(String path) {
		File file = new File(path);
		if(file.exists()) {
			String deleteCmd = "rm -r " + path;
			Runtime runtime = Runtime.getRuntime();
			try {
				runtime.exec(deleteCmd);
			}
			catch(IOException e) {
			}
		}
	}

	public static boolean deleteFilesInDir(Context context, String folder) {
		boolean result = false;
		String path = context.getDir(folder, Context.MODE_PRIVATE).getPath();
		File dir = new File(path);
		if(dir.exists() && dir.isDirectory()) {
			String[] child = dir.list();
			if(child.length > 0) {
				for(String file : child) {
					result = new File(path + "/" + file).delete();
				}
			}
			else {
				result = true;
			}
		}
		else {
			result = true;
		}
		return result;
	}

	public static boolean downloadFile(Context context, String urlLink, String folder,
									   String fileName) {
		boolean result = false;
		File dir = context.getDir(folder, Context.MODE_PRIVATE);
		if(!dir.exists()) {
			dir.mkdir();
		}
		try {
			URL url = new URL(urlLink);
			URLConnection connection = url.openConnection();
			connection.connect();
			//int fileLength = connection.getContentLength();
			InputStream input = new BufferedInputStream(url.openStream());
			OutputStream output = new FileOutputStream(dir + "/" + fileName);
			byte data[] = new byte[1024];
			long total = 0;
			int count;
			while((count = input.read(data)) != -1) {
				total += count;
				output.write(data, 0, count);
			}
			output.flush();
			output.close();
			input.close();
			result = true;
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static String encryptString(String text, String password) {
		String encrypted = null;
		try {
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			final byte[] ivData = new byte[cipher.getBlockSize()];
			final SecureRandom sr = new SecureRandom();
			sr.nextBytes(ivData);
			IvParameterSpec iv = new IvParameterSpec(ivData);
			byte[] key = generateKey(password, 32);
			SecretKeySpec sks = new SecretKeySpec(key, "AES");
			byte[] inputByte = text.getBytes("UTF-8");
			cipher.init(Cipher.ENCRYPT_MODE, sks, iv);
			encrypted = new String(Base64.encode(cipher.doFinal(inputByte), Base64.DEFAULT));
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return encrypted;
	}

	public static boolean encryptFile(Context context, String folderName, String fileName, String password, String extFile) {
		boolean result = false;
		String pathIn = context.getDir(folderName, Context.MODE_PRIVATE).getPath() + "/" + fileName;
		String pathOut = context.getDir(folderName, Context.MODE_PRIVATE).getPath() + "/" + fileName.replace(extFile, "");
		try {
			File file = new File(pathIn);
			FileInputStream fis = new FileInputStream(file);
			FileOutputStream fos = new FileOutputStream(pathOut);
			byte[] key = generateKey(password, 16);
			SecretKeySpec sks = new SecretKeySpec(key, "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, sks);
			CipherOutputStream cos = new CipherOutputStream(fos, cipher);
			int b;
			byte[] d = new byte[8];
			while((b = fis.read(d)) != -1) {
				cos.write(d, 0, b);
			}
			cos.flush();
			cos.close();
			fis.close();
			result = file.delete();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static void expandView(final View view, final boolean isVertical) {
		view.setVisibility(View.VISIBLE);
		view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
		int value = isVertical ? view.getMeasuredHeight() : view.getMeasuredWidth();
		ValueAnimator animator = ValueAnimator.ofInt(0, value);
		animator.setDuration(250);
		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator valueAnimator) {
				int value = (Integer) valueAnimator.getAnimatedValue();
				LayoutParams layoutParams = view.getLayoutParams();
				if(isVertical) {
					layoutParams.height = value;
				}
				else {
					layoutParams.width = value;
				}
				view.setLayoutParams(layoutParams);
			}
		});
		animator.addListener(new AnimatorListener() {

			@Override
			public void onAnimationCancel(Animator arg0) {
			}

			@Override
			public void onAnimationEnd(Animator arg0) {
				if(isVertical) {
					view.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
				}
				else {
					view.getLayoutParams().width = LayoutParams.WRAP_CONTENT;
				}
			}

			@Override
			public void onAnimationRepeat(Animator arg0) {
			}

			@Override
			public void onAnimationStart(Animator arg0) {
			}
		});
		animator.start();
	}

	public static void collapseView(final View view, final boolean isVertical) {
		int value = isVertical ? view.getMeasuredHeight() : view.getMeasuredWidth();
		ValueAnimator animator = ValueAnimator.ofInt(value, 0);
		animator.setDuration(250);
		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator valueAnimator) {
				int value = (Integer) valueAnimator.getAnimatedValue();
				LayoutParams layoutParams = view.getLayoutParams();
				if(isVertical) {
					layoutParams.height = value;
				}
				else {
					layoutParams.width = value;
				}
				view.setLayoutParams(layoutParams);
			}
		});
		animator.addListener(new AnimatorListener() {

			@Override
			public void onAnimationCancel(Animator arg0) {
			}

			@Override
			public void onAnimationEnd(Animator arg0) {
				view.setVisibility(View.GONE);
				if(isVertical) {
					view.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
				}
				else {
					view.getLayoutParams().width = LayoutParams.WRAP_CONTENT;
				}
			}

			@Override
			public void onAnimationRepeat(Animator arg0) {
			}

			@Override
			public void onAnimationStart(Animator arg0) {
			}
		});
		animator.start();
	}

	@SuppressWarnings("resource")
	public static boolean extractDatabase(Context context, String folderName, String databaseName) {
		boolean result = false;
		try {
			File dir = new File(Environment.getExternalStorageDirectory().toString() + "/	" + folderName);
			if(!dir.exists()) {
				dir.mkdir();
			}
			if(dir.canWrite()) {
				String currentDBPath = "//data//" + context.getPackageName() + "//databases//" + databaseName;
				File data = Environment.getDataDirectory();
				File currentDB = new File(data, currentDBPath);
				File backupDB = new File(dir, databaseName);
				if(currentDB.exists()) {
					FileChannel src = new FileInputStream(currentDB).getChannel();
					FileChannel dst = new FileOutputStream(backupDB).getChannel();
					dst.transferFrom(src, 0, src.size());
					src.close();
					dst.close();
				}
			}
			result = true;
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static byte[] generateKey(String password, int length) {
		byte[] key = null;
		try {
			key = password.getBytes("UTF-8");
			MessageDigest sha = MessageDigest.getInstance("SHA-1");
			key = sha.digest(key);
			key = Arrays.copyOf(key, length);
		}
		catch(UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		catch(NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return key;
	}

	public static String getAlphaDate(String date, boolean isAbbrev, boolean withYear) {
		String alphaDate = "";
		if(date != null && !date.isEmpty()) {
			String[] array = date.split("\\-");
			int year = Integer.parseInt(array[0]);
			int month = Integer.parseInt(array[1]);
			int day = Integer.parseInt(array[2]);
			String nameOfMonths = getNameOfMonths(month, isAbbrev, false);
			if(withYear) {
				alphaDate = nameOfMonths + " " + String.format(Locale.ENGLISH, "%02d", day) + ", " + year;
			}
			else {
				alphaDate = nameOfMonths + " " + String.format(Locale.ENGLISH, "%02d", day);
			}
		}
		return alphaDate;
	}

	public static int getBackStackCount(FragmentActivity activity) {
		return activity.getSupportFragmentManager().getBackStackEntryCount();
	}

	public static int getBatteryLevel(Context context) {
		IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		Intent intent = context.getApplicationContext().registerReceiver(null, filter);
		if(intent != null) {
			int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
			int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
			return (level * 100) / scale;
		}
		else {
			return 0;
		}
	}

	public static Bitmap getBitmapImage(Context context, String folderName, String fileName) {
		String path = context.getDir(folderName, Context.MODE_PRIVATE).getPath() + "/" + fileName;
		File image = new File(path);
		return BitmapFactory.decodeFile(image.getAbsolutePath());
	}

	public static boolean getBooleanSharedPref(Context context, String sharedPref, String key) {
		SharedPreferences prefs = context.getSharedPreferences(sharedPref, Context.MODE_PRIVATE);
		return prefs.getBoolean(key, false);
	}

	public static String getDateAfter(String date, int noOfDays) {
		if(date != null) {
			long millis = convertDateTimeToMillis(date, "00:00:00");
			long output = millis + (noOfDays * 86400000);
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(output);
			return String.format(Locale.ENGLISH, "%tF", cal);
		}
		else {
			return null;
		}
	}

	public static Calendar getCalendar(String date) {
		Calendar cal = Calendar.getInstance();
		if(date != null) {
			String array[] = date.split("\\-");
			int year = Integer.parseInt(array[0]);
			int month = Integer.parseInt(array[1]);
			int day = Integer.parseInt(array[2]);
			cal.set(year, month - 1, day);
		}
		return cal;
	}

	public static String getDeviceID(Context context) {
		TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		if(manager.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE) {
			return Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
		}
		else {
			return manager.getDeviceId();
		}
	}

	public static HashMap<String, Integer> getPhoneInfo(Context context) {
		HashMap<String, Integer> map = new HashMap<>();
		TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		if(manager.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM) {
			String networkOperator = manager.getNetworkOperator();
			int mcc = Integer.parseInt(networkOperator.substring(0, 3));
			int mnc = Integer.parseInt(networkOperator.substring(3));
			map.put("mcc", mcc);
			map.put("mnc", mnc);
			final GsmCellLocation location = (GsmCellLocation) manager.getCellLocation();
			if(location != null) {
				int cid = location.getCid();
				int lac = location.getLac();
				map.put("cid", cid);
				map.put("lac", lac);
			}
		}
		return map;
	}

	public static String getFilePath(Context context, Uri uri) {
		String[] projection = {MediaStore.Images.Media.DATA};
		Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
		int column_index = 0;
		try {
			column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
		}
		catch(Exception e) {
			return uri.getPath();
		}
		return cursor.getString(column_index);
	}

	public static String getHttpResponse(String uri, List<NameValuePair> nameValuePairs) {
		int timeOut = 60000 * 3;
		final int INDENT = 4;
		String response = "";
		String inputLine = null;
		String message = null;
		String exception = null;
		boolean result = false;
		try {
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nameValuePairs);
			String length = Long.toString(entity.getContentLength());
			URL url = new URL(uri);
			System.setProperty("http.keepAlive", "false");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setReadTimeout(timeOut);
			connection.setConnectTimeout(timeOut);
			connection.setRequestMethod(HttpPost.METHOD_NAME);
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setRequestProperty("Content-Length", length);
			connection.setRequestProperty("Content-Language", "en-US");
			connection.setRequestProperty("connection", "close");
			connection.setRequestProperty("Accept-Encoding", "");
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);
			connection.connect();
			OutputStream os = connection.getOutputStream();
			entity.writeTo(os);
			os.flush();
			os.close();
			int responseCode = connection.getResponseCode();
			if(responseCode == HttpStatus.SC_OK) {
				InputStream is = connection.getInputStream();
				InputStreamReader reader = new InputStreamReader(is);
				BufferedReader in = new BufferedReader(reader);
				while((inputLine = in.readLine()) != null) {
					response += inputLine;
				}
				in.close();
				result = true;
			}
		}
		catch(SocketTimeoutException ste) {
			ste.printStackTrace();
			exception = ste.toString();
			message = "Connection timed out, the server is taking too long to respond. " +
					"Please check your internet connection and try again.";
		}
		catch(IOException ioe) {
			ioe.printStackTrace();
			exception = ioe.toString();
			message = "You are getting weak internet connection. " +
					"Please find a reliable source to continue.";
		}
		if(!result) {
			try {
				JSONObject error = new JSONObject();
				JSONObject field = new JSONObject();
				field.put("message", message);
				field.put("exception", exception);
				error.put("error", field);
				response = error.toString(INDENT);
			}
			catch(JSONException je) {
				je.printStackTrace();
			}
		}
		return response;
	}

	public static String uploadFile(String url, String params, String name, File file) {
		String response = null;
		String message = null;
		String exception = null;
		final int INDENT = 4;
		boolean result = false;
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(url);
		try {
			MultipartEntityBuilder entity = MultipartEntityBuilder.create();
			entity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
			entity.addTextBody("params", params);
			entity.addPart(name, new FileBody(file));
			httppost.setEntity(entity.build());
			HttpResponse httpResponse = httpclient.execute(httppost);
			HttpEntity httpEntity = httpResponse.getEntity();
			response = EntityUtils.toString(httpEntity);
			result = true;
		}
		catch(ClientProtocolException cpe) {
			exception = cpe.toString();
			message = cpe.getMessage();
		}
		catch(IOException ioe) {
			exception = ioe.toString();
			message = "You are getting weak internet connection. " +
					"Please find a reliable source to continue.";
		}
		if(!result) {
			try {
				JSONObject error = new JSONObject();
				JSONObject field = new JSONObject();
				field.put("message", message);
				field.put("exception", exception);
				error.put("error", field);
				response = error.toString(INDENT);
			}
			catch(JSONException je) {
				je.printStackTrace();
			}
		}
		return response;
	}

	public static String getImagePath(Context context, Uri uri) {
		String path = null;
		Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
		if(cursor != null) {
			cursor.moveToFirst();
			String documentID = cursor.getString(0);
			documentID = documentID.substring(documentID.lastIndexOf(":") + 1);
			cursor.close();
			cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
					null, MediaStore.Images.Media._ID + " = ? ", new String[]{documentID}, null);
			if(cursor != null) {
				cursor.moveToFirst();
				path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
				cursor.close();
			}
		}
		return path;
	}

	public static int getIntSharedPref(Context context, String sharedPref, String key) {
		SharedPreferences prefs = context.getSharedPreferences(sharedPref, Context.MODE_PRIVATE);
		int value = prefs.getInt(key, 0);
		return value;
	}

	public static String getKeyHash(Context context) {
		String keyHash = "";
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
			for(Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				keyHash = Base64.encodeToString(md.digest(), Base64.DEFAULT);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return keyHash;
	}

	public static int getLastRecordID(SQLiteAdapter db, String table) {
		int lastRecordID = 0;
		String query = "SELECT ID FROM " + table + " ORDER BY ID DESC LIMIT 1";
		lastRecordID = db.getInt(query);
		return lastRecordID;
	}

	public static String getMySQLPassword(String plainText) {
		String password = "";
		try {
			byte[] utf8 = plainText.getBytes("UTF-8");
			password = "*" + DigestUtils.shaHex(DigestUtils.sha(utf8)).toUpperCase();
		}
		catch(UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return password;
	}

	public static String getNameOfMonths(int month, boolean isAbbrev, boolean isUpperCase) {
		String nameOfMonths = "";
		switch(month) {
			case 1:
				nameOfMonths = "January";
				break;
			case 2:
				nameOfMonths = "February";
				break;
			case 3:
				nameOfMonths = "March";
				break;
			case 4:
				nameOfMonths = "April";
				break;
			case 5:
				nameOfMonths = "May";
				break;
			case 6:
				nameOfMonths = "June";
				break;
			case 7:
				nameOfMonths = "July";
				break;
			case 8:
				nameOfMonths = "August";
				break;
			case 9:
				nameOfMonths = "September";
				break;
			case 10:
				nameOfMonths = "October";
				break;
			case 11:
				nameOfMonths = "November";
				break;
			case 12:
				nameOfMonths = "December";
				break;
		}
		if(isAbbrev) {
			nameOfMonths = nameOfMonths.substring(0, 3);
		}
		if(isUpperCase) {
			String upperCase = "";
			for(int x = 0; x < nameOfMonths.length(); x++) {
				Character temp;
				temp = nameOfMonths.charAt(x);
				temp = Character.toUpperCase(temp);
				upperCase = upperCase + temp.toString();
			}
			nameOfMonths = upperCase;
		}
		return nameOfMonths;
	}

	public static String getStringSharedPref(Context context, String sharedPref, String key) {
		SharedPreferences prefs = context.getSharedPreferences(sharedPref, Context.MODE_PRIVATE);
		String value = prefs.getString(key, null);
		return value;
	}

	public static String getDay(String date, String time) {
		long timestamp = convertDateTimeToMillis(date, time);
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timestamp);
		return String.format(Locale.ENGLISH, "%tA", cal);
	}

	public static String getDate() {
		Calendar cal = Calendar.getInstance();
		return String.format(Locale.ENGLISH, "%tF", cal);
	}

	public static String getDate(long timestamp) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timestamp);
		return String.format(Locale.ENGLISH, "%tF", cal);
	}

	public static String getTime() {
		Calendar cal = Calendar.getInstance();
		return String.format(Locale.ENGLISH, "%tT", cal);
	}

	public static String getTime(long timestamp) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timestamp);
		return String.format(Locale.ENGLISH, "%tT", cal);
	}

	public static String getUTCTime() {
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		return sdf.format(new Date(System.currentTimeMillis()));
	}

	public static String getVersionName(Context context) {
		String versionName = "2.0";
		final PackageManager packageManager = context.getPackageManager();
		if(packageManager != null) {
			try {
				PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
				versionName = packageInfo.versionName;
			}
			catch(NameNotFoundException e) {
				versionName = "2.0";
			}
		}
		return versionName;
	}

	public static String handleNullString(String text) {
		String result = "";
		if(text == null || text.equals("null")) {
			result = "";
		}
		else {
			result = text;
		}
		return result;
	}

	public static String handleQuotesUniCodeToSQLite(String text) {
		String result = "";
		if(text != null && !text.equals("null")) {
			result = text
					.replace("u0027", "''")
					.replace("u0022", "\"");
		}
		return result;
	}

	public static void showKeyboard(View v, Context context) {
		InputMethodManager manager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
		manager.showSoftInput(v, 0);
	}

	public static void showKeyboard(View v, Activity activity) {
		InputMethodManager manager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
		manager.showSoftInput(v, 0);
	}

	public static void hideKeyboard(View v, Activity activity) {
		InputMethodManager manager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
		manager.hideSoftInputFromWindow(v.getWindowToken(), 0);
	}

	public static void hideKeyboard(View v, Context context) {
		InputMethodManager manager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
		manager.hideSoftInputFromWindow(v.getWindowToken(), 0);
	}

	public static boolean isGpsEnabled(Context context) {
		boolean result = false;
		LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		result = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		return result;
	}

	public static boolean isInternetConnected(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo network = cm.getActiveNetworkInfo();
		if(network != null) {
			return network.isAvailable();
		}
		return false;
	}

	public static boolean isMockEnabled(Context context) {
		boolean result = false;
		if(!Secure.getString(context.getContentResolver(), Secure.ALLOW_MOCK_LOCATION).equals("0")) {
			result = true;
		}
		return result;
	}

	public static boolean isNetEnabled(Context context) {
		boolean result = false;
		LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		result = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		return result;
	}

	public static boolean isNumeric(String string) {
		if(string == null || string.length() == 0) {
			return false;
		}
		int l = string.length();
		String f = "";
		int dotCount = 0;
		for(int i = 0; i < l; i++) {
			if(!Character.isDigit(string.codePointAt((i)))) {
				f = string.substring(i, i + 1);
				if(!f.equals(".")) {
					return false;
				}
				else {
					dotCount++;
					if(dotCount >= 2) {
						return false;
					}
				}
			}
		}
		return true;
	}

	public static boolean isServiceRunning(Context context, Class<?> c) {
		boolean result = false;
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		for(RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if(c.getName().equals(service.service.getClassName())) {
				result = true;
			}
		}
		return result;
	}

	public static boolean isSubsetOf(Collection<String> subset, Collection<String> superset) {
		for(String string : subset) {
			if(!superset.contains(string)) {
				return false;
			}
		}
		return true;
	}

	public static boolean isTimeEqual(int allowanceMin, String dateToCompare, String timeToCompare,
									  String baseDate, String baseTime) {
		boolean result = false;
		long millisToCompare = convertDateTimeToMillis(dateToCompare, timeToCompare);
		long millisBase = convertDateTimeToMillis(baseDate, baseTime);
		long millisAllowance = allowanceMin * 60000;
		long difference = millisToCompare > millisBase ? millisToCompare - millisBase : millisBase - millisToCompare;
		if(difference <= millisAllowance) {
			result = true;
		}
		return result;
	}

	public static boolean isValidEmail(String email) {
		return Patterns.EMAIL_ADDRESS.matcher(email).matches();
	}

	public static void removeNotification(Context context, int notifID) {
		NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		manager.cancel(notifID);
	}

	public static void setAlarm(Context context, Class<?> receiver, int durationMin, int requestCode) {
		if(durationMin > 0) {
			int timeRemainingSecs = durationMin * 60;
			Intent intent = new Intent(context, receiver);
			PendingIntent pi = PendingIntent.getBroadcast(context, requestCode, intent, 0);
			AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (timeRemainingSecs * 1000), pi);
		}
	}

	public static void setBooleanSharedPref(Context context, String sharedPref, String key, boolean value) {
		SharedPreferences prefs = context.getSharedPreferences(sharedPref, Context.MODE_PRIVATE);
		prefs.edit().putBoolean(key, value).apply();
	}

	public static void setCircle(ImageView imageView) {
		Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
		Bitmap circleBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		BitmapShader shader = new BitmapShader(bitmap, TileMode.CLAMP, TileMode.CLAMP);
		Paint paint = new Paint();
		paint.setShader(shader);
		Canvas c = new Canvas(circleBitmap);
		c.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, bitmap.getWidth() / 2, paint);
		imageView.setImageBitmap(circleBitmap);
	}

	public static void setCrashHandler(final Context context, String folder, String password) {
		Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(context, folder, password));
	}

	public static boolean setErrorMsg(Context context, String message, String folder, String password) {
		boolean result = false;
		String errorMsg = "Error: " + message;
		errorMsg = errorMsg.replace("\n", "\r\n");
		String fileName = CodePanUtils.getDate() + "_" + CodePanUtils.getTime() + ".txt";
		fileName = fileName.replace(":", "-");
		result = CodePanUtils.writeText(context, folder, fileName, errorMsg);
		if(result) {
			result = CodePanUtils.encryptFile(context, folder, fileName, password, ".txt");
		}
		return result;
	}

	public static boolean setErrorMsg(Context context, String message, String jsonString,
									  String response, String folder, String password) {
		boolean result = false;
		String errorMsg = "Error: " + message + "\nParams: " + jsonString + "\nResponse: " + response;
		errorMsg = errorMsg.replace("\n", "\r\n");
		String fileName = CodePanUtils.getDate() + "_" + CodePanUtils.getTime() + ".txt";
		fileName = fileName.replace(":", "-");
		result = CodePanUtils.writeText(context, folder, fileName, errorMsg);
		if(result) {
			result = CodePanUtils.encryptFile(context, folder, fileName, password, ".txt");
		}
		return result;
	}

	public static void setGPS(Context context, boolean isEnabled) {
		try {
			Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
			intent.putExtra("enabled", isEnabled);
			context.sendBroadcast(intent);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void setIntSharedPref(Context context, String sharedPref, String key, int value) {
		SharedPreferences prefs = context.getSharedPreferences(sharedPref, Context.MODE_PRIVATE);
		prefs.edit().putInt(key, value).apply();
	}

	public static void setNotification(Context context, int ID, NotificationCompat.Builder builder) {
		@SuppressWarnings("static-access")
		NotificationManager notifManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
		notifManager.notify(ID, builder.build());
	}

	public static void setNotification(Context context, String title, String message, int resource,
									   int ID, int requestCode, boolean isVibrate, Intent intent, String uri) {
		NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
		builder.setSmallIcon(resource);
		builder.setContentTitle(title);
		builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
		builder.setPriority(Notification.PRIORITY_HIGH);
		builder.setLights(Color.GREEN, 500, 500);
		builder.setOnlyAlertOnce(true);
		builder.setAutoCancel(true);
		if(isVibrate) {
			builder.setVibrate(new long[]{500, 500});
		}
		Uri url = Uri.parse(uri);
		builder.setSound(url);
		builder.setContentText(message);
		PendingIntent pi = PendingIntent.getActivity(context, requestCode,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		;
		builder.setContentIntent(pi);
		@SuppressWarnings("static-access")
		NotificationManager notifManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
		notifManager.notify(ID, builder.build());
	}

	public static void setNotification(Context context, String title, String message, int resource,
									   int ID, boolean isVibrate, String uri) {
		NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
		builder.setSmallIcon(resource);
		builder.setContentTitle(title);
		builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
		builder.setPriority(Notification.PRIORITY_HIGH);
		builder.setLights(Color.GREEN, 500, 500);
		builder.setOnlyAlertOnce(true);
		builder.setAutoCancel(true);
		if(isVibrate) {
			builder.setVibrate(new long[]{500, 500});
		}
		Uri url = Uri.parse(uri);
		builder.setSound(url);
		builder.setContentText(message);
		@SuppressWarnings("static-access")
		NotificationManager notifManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
		notifManager.notify(ID, builder.build());
	}

	public static void setStringSharedPref(Context context, String sharedPref, String key, String value) {
		SharedPreferences prefs = context.getSharedPreferences(sharedPref, Context.MODE_PRIVATE);
		prefs.edit().putString(key, value).apply();
	}

	public static String throwableToString(Throwable th) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		th.printStackTrace(pw);
		return sw.toString();
	}

	public static boolean writeText(Context context, String folderName, String fileName, String text) {
		boolean result = false;
		try {
			String path = context.getDir(folderName, Context.MODE_PRIVATE).getPath() + "/" + fileName;
			File file = new File(path);
			FileWriter writer = new FileWriter(file);
			writer.append(text);
			writer.flush();
			writer.close();
			result = true;
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static boolean zipFile(Context context, String fileName, String folderName, String zipFileName) {
		String pathtozip = context.getDir(folderName, Context.MODE_PRIVATE).getPath() + "/" + fileName;
		String pathforzip = context.getDir(folderName, Context.MODE_PRIVATE).getPath() + "/" + zipFileName;
		int BUFFER = 80000;
		boolean result = false;
		try {
			BufferedInputStream origin = null;
			FileOutputStream dest = new FileOutputStream(pathforzip);
			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
			byte data[] = new byte[BUFFER];
			FileInputStream fi = new FileInputStream(pathtozip);
			origin = new BufferedInputStream(fi, BUFFER);
			ZipEntry entry = new ZipEntry(pathtozip.substring(pathtozip.lastIndexOf("/") + 1));
			out.putNextEntry(entry);
			int count;
			while((count = origin.read(data, 0, BUFFER)) != -1) {
				out.write(data, 0, count);
			}
			origin.close();
			out.close();
			result = true;
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static boolean zipFolder(Context context, String folderToZip, String folderForZip, String zipFileName) {
		String pathtozip = context.getDir(folderToZip, Context.MODE_PRIVATE).getPath();
		String pathforzip = context.getDir(folderForZip, Context.MODE_PRIVATE).getPath() + "/" + zipFileName;
		boolean result = false;
		try {
			FileOutputStream fos = new FileOutputStream(pathforzip);
			ZipOutputStream zos = new ZipOutputStream(fos);
			File srcFile = new File(pathtozip);
			File[] files = srcFile.listFiles();
			for(int i = 0; i < files.length; i++) {
				byte[] buffer = new byte[1024];
				FileInputStream fis = new FileInputStream(files[i]);
				zos.putNextEntry(new ZipEntry(files[i].getName()));
				int length;
				while((length = fis.read(buffer)) > 0) {
					zos.write(buffer, 0, length);
				}
				zos.closeEntry();
				fis.close();
			}
			zos.close();
			result = true;
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static boolean zipFolderExternal(Context context, String folderToZip, String folderName, String zipFileName) {
		String pathtozip = context.getDir(folderToZip, Context.MODE_PRIVATE).getPath();
		String pathforzip = Environment.getExternalStorageDirectory() + "/" + folderName + "/" + zipFileName;
		String folder = Environment.getExternalStorageDirectory() + "/" + folderName;
		File dir = new File(folder);
		if(!dir.exists()) {
			dir.mkdir();
		}
		boolean result = false;
		try {
			FileOutputStream fos = new FileOutputStream(pathforzip);
			ZipOutputStream zos = new ZipOutputStream(fos);
			File srcFile = new File(pathtozip);
			File[] files = srcFile.listFiles();
			for(int i = 0; i < files.length; i++) {
				byte[] buffer = new byte[1024];
				FileInputStream fis = new FileInputStream(files[i]);
				zos.putNextEntry(new ZipEntry(files[i].getName()));
				int length;
				while((length = fis.read(buffer)) > 0) {
					zos.write(buffer, 0, length);
				}
				zos.closeEntry();
				fis.close();
			}
			zos.close();
			result = true;
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static void showAlertToast(FragmentActivity activity, String message, int duration) {
		int offsetY = activity.getResources().getDimensionPixelSize(R.dimen.one_hundred);
		LayoutInflater inflater = activity.getLayoutInflater();
		View layout = inflater.inflate(R.layout.alert_toast_layout, (ViewGroup) activity.findViewById(R.id.rlAlertToast));
		CodePanLabel text = (CodePanLabel) layout.findViewById(R.id.tvMessageAlertToast);
		text.setText(message);
		Toast toast = new Toast(activity);
		toast.setGravity(Gravity.BOTTOM, 0, offsetY);
		toast.setDuration(duration);
		toast.setView(layout);
		toast.show();
	}

	public static void showAlertToast(FragmentActivity activity, String message, int duration, ArrayList<SpannableList> list, Typeface typeface) {
		int offsetY = activity.getResources().getDimensionPixelSize(R.dimen.one_hundred);
		LayoutInflater inflater = activity.getLayoutInflater();
		View layout = inflater.inflate(R.layout.alert_toast_layout, (ViewGroup) activity.findViewById(R.id.rlAlertToast));
		CodePanLabel text = (CodePanLabel) layout.findViewById(R.id.tvMessageAlertToast);
		SpannableStringBuilder ssb = new SpannableStringBuilder(message);
		for(SpannableList obj : list) {
			ssb.setSpan(new CustomTypefaceSpan(typeface), obj.start, obj.end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		}
		text.setText(ssb);
		Toast toast = new Toast(activity);
		toast.setGravity(Gravity.BOTTOM, 0, offsetY);
		toast.setDuration(duration);
		toast.setView(layout);
		toast.show();
	}

	public static void triggerHeartbeat(Context context) {
		context.sendBroadcast(new Intent("com.google.android.intent.action.GTALK_HEARTBEAT"));
		context.sendBroadcast(new Intent("com.google.android.intent.action.MCS_HEARTBEAT"));
	}

	public static String convertBengaliNumerals(String text) {
		String eng = text.replace("০", "0")
				.replace("১", "1")
				.replace("২", "2")
				.replace("৩", "3")
				.replace("৪", "4")
				.replace("৫", "5")
				.replace("৬", "6")
				.replace("৭", "7")
				.replace("৮", "8")
				.replace("৯", "9");
		return eng;
	}

	public static boolean saveBitmap(Context context, String folder, String fileName, Bitmap bitmap) {
		boolean result = false;
		String path = context.getDir(folder, Context.MODE_PRIVATE).getPath() + "/" + fileName;
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(path);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
			out.flush();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				if(out != null) {
					out.close();
					result = true;
				}
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public static boolean isThreadRunning(String name) {
		boolean result = false;
		Set<Thread> i = Thread.getAllStackTraces().keySet();
		for(Thread bg : i) {
			if(bg.getName().equals(name)) {
				result = true;
				break;
			}
		}
		return result;
	}

	public static boolean isThreadRunning(String arg1, String arg2) {
		boolean result = false;
		Set<Thread> i = Thread.getAllStackTraces().keySet();
		for(Thread bg : i) {
			String name = bg.getName();
			if(name.equals(arg1) || name.equals(arg2)) {
				result = true;
				break;
			}
		}
		return result;
	}

	public static Bitmap getBitmapThumbnails(Context context, String folderName, String fileName, int size) {
		String path = context.getDir(folderName, Context.MODE_PRIVATE).getPath() + "/" + fileName;
		//String path = Environment.getExternalStorageDirectory() + "/" + folderName + "/" +fileName;
		File image = new File(path);
		BitmapFactory.Options bounds = new BitmapFactory.Options();
		bounds.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(image.getPath(), bounds);
		if((bounds.outWidth == -1) || (bounds.outHeight == -1)) {
			return null;
		}
		int originalSize = (bounds.outHeight > bounds.outWidth) ? bounds.outHeight : bounds.outWidth;
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inSampleSize = originalSize / size;
		return BitmapFactory.decodeFile(image.getPath(), opts);
	}

	public static int getSupportedNoOfCol(Context context, int numCol) {
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		float widthDp = metrics.widthPixels / metrics.density;
		if(numCol != 0) {
			if(metrics.widthPixels % numCol == 0) {
				return numCol;
			}
			else {
				if(widthDp <= 360) {
					return numCol - 1;
				}
				else {
					return numCol + 1;
				}
			}
		}
		else {
			return numCol;
		}
	}

	public static int convertPixelToDp(Context context, int numCol) {
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		return (numCol * (int) metrics.density);
	}

	public static int convertPixelToDpOffset(Context context, int numCol) {
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		return (int) (numCol * metrics.density);
	}

	public static int getWidth(View view) {
		int width = 0;
		if(view != null) {
			view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
			width = view.getMeasuredWidth();
		}
		return width;
	}

	public static int getHeight(View view) {
		int height = 0;
		if(view != null) {
			view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
			height = view.getMeasuredHeight();
		}
		return height;
	}

	public static void animateView(Context context, final View view, final int resID, final int filler) {
		Animation anim = AnimationUtils.loadAnimation(context, resID);
		anim.setFillAfter(true);
		view.startAnimation(anim);
		anim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				if(view instanceof ImageView) {
					((ImageView) view).setImageResource(filler);
				}
				else {
					view.setBackgroundResource(filler);
				}
			}

			@Override
			public void onAnimationEnd(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}
		});
	}

	public static void fadeIn(final View view) {
		view.setVisibility(View.VISIBLE);
		Animation fadeIn = new AlphaAnimation(0.00f, 1.00f);
		fadeIn.setDuration(250);
		fadeIn.setInterpolator(new DecelerateInterpolator());
		fadeIn.setAnimationListener(new AnimationListener() {
			public void onAnimationStart(Animation animation) {
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationEnd(Animation animation) {
				view.setEnabled(true);
			}
		});
		view.startAnimation(fadeIn);
	}

	public static void fadeOut(final View view) {
		view.setEnabled(false);
		Animation fadeOut = new AlphaAnimation(1.00f, 0.00f);
		fadeOut.setDuration(250);
		fadeOut.setAnimationListener(new AnimationListener() {
			public void onAnimationStart(Animation animation) {
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationEnd(Animation animation) {
				view.setVisibility(View.GONE);
			}
		});
		view.startAnimation(fadeOut);
	}

	public static String handleUniCodeToSQLite(String text) {
		String result = "";
		if(text != null && !text.equals("null")) {
			result = text.replace("'", "''").
					replace("u0027", "''").
					replace("u0022", "\"");
			result = convertUnicodeToString(result);
		}
		return result;
	}

	public static String handleNextLine(String text, boolean isHTML) {
		String result = "";
		if(text != null && !text.equals("null")) {
			if(isHTML) {
				result = text.replace("\n", "&NewLine;");
			}
			else {
				result = text.replace("&NewLine;", "\n");
			}
		}
		return result;
	}

	public static boolean isAppInstalled(Context context, String packageName) {
		try {
			PackageManager pm = context.getPackageManager();
			pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
			return true;
		}
		catch(NameNotFoundException e) {
			return false;
		}
	}

	public static int getWindowHeight(Activity activity) {
		return activity.getWindowManager().getDefaultDisplay().getHeight();
	}

	public static int getWindowWidth(Activity activity) {
		return activity.getWindowManager().getDefaultDisplay().getWidth();
	}

	public static void setStatusBarColor(Activity activity, int resID) {
		if(resID != 0) {
			int color = activity.getResources().getColor(resID);
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				Window window = activity.getWindow();
				window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
				window.setStatusBarColor(color);
			}
		}
	}

	public static String[] getAppPermissions(Context context) {
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(),
					PackageManager.GET_PERMISSIONS);
			return info.requestedPermissions;
		}
		catch(NameNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void requestPermission(Activity activity, int requestCode) {
		String[] permissions = getDeniedPermission(activity);
		if(permissions != null) {
			ActivityCompat.requestPermissions(activity, permissions, requestCode);
		}
	}

	public static boolean isPermissionHidden(Activity activity) {
		String[] permissions = getDeniedPermission(activity);
		if(permissions != null) {
			for(String permission : permissions) {
				boolean result = ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
				if(!result) {
					return true;
				}
			}
		}
		return false;
	}


	public static String[] getDeniedPermission(Context context) {
		String[] permissions = getAppPermissions(context);
		if(permissions != null) {
			List<String> deniedList = new ArrayList<>();
			for(String permission : permissions) {
				int result = ContextCompat.checkSelfPermission(context, permission);
				if(result == PackageManager.PERMISSION_DENIED) {
					deniedList.add(permission);
				}
			}
			return deniedList.toArray(new String[deniedList.size()]);
		}
		return null;
	}

	public static boolean isPermissionGranted(Context context) {
		String[] permissions = getAppPermissions(context);
		if(permissions != null) {
			for(String permission : permissions) {
				int result = ContextCompat.checkSelfPermission(context, permission);
				if(result == PackageManager.PERMISSION_DENIED) {
					return false;
				}
			}
			return true;
		}
		return true;
	}

	public static String getDisplayDate(String date) {
		Calendar cal = getCalendar(date);
		String dayOfWeek = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.ENGLISH);
		return dayOfWeek + ", " + CodePanUtils.getAlphaDate(date, true, false);
	}

	public static String getDisplayYear(String date) {
		if(date != null) {
			return date.split("\\-")[0];
		}
		return null;
	}
}