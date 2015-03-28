package com.yt.worlddatetime.tools;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class CheckUpdate {
	private Context con;
	ProgressDialog dialog = null;

	public CheckUpdate(Context context) {
		this.con = context;

		dialog = new ProgressDialog(con);
		// dialog.setTitle("下载文件");
		dialog.setMessage("检查版本中...");
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		dialog.setIcon(android.R.drawable.ic_input_add);
		dialog.setIndeterminate(false);
		dialog.setCancelable(true);

		dialog.show();

		new Thread() {
			@Override
			public void run() {
				String str = "";
				// 你要执行的方法
				try {
					str = getJsonString("http://www.jblv.com/com.yt.worlddatetime/version.xml");
					Log.i("YT", str);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// 执行完毕后给handler发送一个空消息
				Message msg = new Message();
				Bundle data = new Bundle();
				data.putString("result", str);
				msg.setData(data);
				handler.sendMessage(msg);
				// handler.sendEmptyMessage(0);
			}
		}.start();
	}

	//定义Handler对象
	Handler handler = new Handler() {
		@Override
		// 当有消息发送出来的时候就执行Handler的这个方法
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			// 处理UI
			Bundle bundle = msg.getData();
			String str = bundle.getString("result");
			if (dialog.isShowing())
				dialog.dismiss();
			UpdateClass data = XmlPar(str);
			if(data.version>getVersion()){
				downApk(data.url);
			}else{
				Toast.makeText(con, "版本已经是最新的！", 1).show();
			}
		}
	};
	


	public UpdateClass  XmlPar(String str) {
		// TODO Auto-generated method stub
		UpdateClass res  = null;
		
		try {
			
			XmlPullParserFactory pullParserFactory=XmlPullParserFactory.newInstance();
			//获取XmlPullParser的实例
            XmlPullParser file=pullParserFactory.newPullParser();// 创建一个资源ID
            Log.d("YT",str);
			file.setInput( new ByteArrayInputStream(str.getBytes()),"utf-8" );  
			int eventType = file.getEventType();
			try {
				while (eventType != file.END_DOCUMENT) {
				
					String nodename = file.getName();
					Log.d("YT","-------"+file.getAttributeCount());
					switch (eventType) {
					case XmlPullParser.START_DOCUMENT:
						res = new UpdateClass();
						break;
					case XmlPullParser.START_TAG:
						if ("version".equals(nodename)) {
							String v= file.nextText();
							res.version = Float.parseFloat(v);
						} else if ("downurl".equals(nodename)) {
							res.url= file.nextText();
						} 
						break;
					default:
						break;
					}
					eventType = file.next();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		catch (XmlPullParserException e) {
			e.printStackTrace();
		}
		return res;
	}

	public String getJsonString(String urlPath) throws Exception {

		URL url = new URL(urlPath);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.connect();
		InputStream inputStream = connection.getInputStream();

		Reader reader = new InputStreamReader(inputStream);
		BufferedReader bufferedReader = new BufferedReader(reader);

		String str = null;
		StringBuffer sb = new StringBuffer();
		while (((str = bufferedReader.readLine()) != null)) {
			sb.append(str);
		}
		reader.close();
		connection.disconnect();
		return sb.toString();

	}

	public void downApk(String url) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(url));
		con.startActivity(intent);
	}

	
	class UpdateClass{
		public float version = 0;
		public String url  = null;
	}
	
	/**
	 * 获取版本号
	 * 
	 * @return 当前应用的版本号
	 */
	public float getVersion() {
		try {
			PackageManager manager = con.getPackageManager();
			PackageInfo info = manager.getPackageInfo(con.getPackageName(), 0);
			String version = info.versionName;
			return Float.parseFloat(version);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
}
