package com.yt.worlddatetime.tools;

/**
 * Created by Administrator on 2015-04-13.
 */

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.yt.worlddatetime.R;


public class CheckVersion
{
    /* 下载中 */
    private static final int DOWNLOAD = 1;
    /* 下载结束 */
    private static final int DOWNLOAD_FINISH = 2;
    /* 下载错误 */
    private static final int GET_UNDATAINFO_ERROR = 3;
    /* 更新客户端 */
    private static final int UPDATA_CLIENT = 4;
    /* 下载APK错误 */
    private static final int DOWN_ERROR = 5;   
    /* 没有网络连接 */
    private static final int NO_NETWORK = 6;   
    /* 已经是最新版本 */
    private static final int IS_NEWUPDATE = 7;

    /* 保存解析的XML信息 */
    HashMap<String, String> mHashMap;
    /* 下载保存路径 */
    private String mSavePath;
    /* 记录进度条数量 */
    private int progress;
    private float downCount,downSize;
    private TextView updatePre,updateSize;

    /* 是否取消更新 */
    private boolean cancelUpdate = false;

    private Context mContext;
    /* 更新进度条 */
    private ProgressBar mProgress;
    private Dialog mDownloadDialog;
    
    public boolean displayTip = false;

    private Handler mHandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                // 正在下载
                case DOWNLOAD:
                    // 设置进度条位置
                    //Log.d("YT","---------CCC"+progress);
                    updatePre.setText(progress+"%");
                    updateSize.setText(Math.floor(downCount/1024)+"Kb/"+Math.floor(downSize/1024)+"Kb");
                    mProgress.setProgress(progress);
                    break;
                case DOWNLOAD_FINISH:
                    // 安装文件
                    installApk();
                    break;
                default:
                    break;
            }
        };
    };

    public CheckVersion(Context context)
    {
        this.mContext = context;
    }

    /**
     * 检测软件更新
     */
    public void checkUpdate()
    {
        new Thread( new CheckVersionTask()).start();
    }



    /*
	 * 从服务器获取xml解析并进行比对版本号
	 */
    public class CheckVersionTask implements Runnable{

        public void run() {
            try {
            	
            	Network net = new Network();
            	if(net.isNetworkConnected(mContext)){
            		Log.d("YT","----------有网络");
	                // 获取当前软件版本
	                int versionCode = getVersionCode(mContext);
	                //从资源文件获取服务器 地址
	                //String path = getResources().getString(R.string.serverurl);
	                String path = "http://www.jblv.com/com.yt.worlddatetime/version.xml";
	                //包装成url的对象
	                URL url = new URL(path);
	                HttpURLConnection conn =  (HttpURLConnection) url.openConnection();
	                conn.setConnectTimeout(5000);
	                InputStream is =conn.getInputStream();
	
	                // 解析XML文件。 由于XML文件比较小，因此使用DOM方式进行解析
	                ParseXmlService service = new ParseXmlService();
	                try
	                {
	                    mHashMap = service.parseXml(is);
	                } catch (Exception e)
	                {
	                    e.printStackTrace();
	                }
	                if (null != mHashMap)
	                {
	                    int serviceCode = Integer.valueOf(mHashMap.get("version"));
	
	                    //Log.d("YT","------"+serviceCode +"---"+versionCode);
	                    // 版本判断
	                    if (serviceCode > versionCode)
	                    {
	                        //Log.i("YT","版本号不同 ,提示用户升级 ");
	                        Message msg = new Message();
	                        msg.what = UPDATA_CLIENT;
	                        handler.sendMessage(msg);
	                    }else{
	    	                //Log.i("YT","版本号相同无需升级");
	    	                //if(displayTip) Toast.makeText(mContext, R.string.soft_update_no, Toast.LENGTH_LONG).show();
	    	                Message msg = new Message();
	                        msg.what = IS_NEWUPDATE;
	                        handler.sendMessage(msg);     	
	                    }
	                }	                	               
                   
            	}else{
            		 Message msg = new Message();
                     msg.what = NO_NETWORK;
                     handler.sendMessage(msg);
            		//Log.d("YT","-------------没有网络连接");
            		//if(displayTip) Toast.makeText(mContext,"没有网络连接！", Toast.LENGTH_LONG).show();
            	}
            } catch (Exception e) {
                // 待处理
                Message msg = new Message();
                msg.what = GET_UNDATAINFO_ERROR;
                handler.sendMessage(msg);
                e.printStackTrace();
            }
        }
    }
  
    
    Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATA_CLIENT:
                    //对话框通知用户升级程序
                    showNoticeDialog();
                    break;
                case GET_UNDATAINFO_ERROR:
                    //服务器超时
                	if(displayTip) Toast.makeText(mContext, "获取服务器更新信息失败", Toast.LENGTH_LONG).show();
                    break;
                case DOWN_ERROR:
                    //下载apk失败
                	if(displayTip) Toast.makeText(mContext, "下载新版本失败", Toast.LENGTH_LONG).show();
                    break;
                case NO_NETWORK:
                	if(displayTip) Toast.makeText(mContext,"没有网络连接！", Toast.LENGTH_LONG).show();
                	break;
                case IS_NEWUPDATE:
                	if(displayTip) Toast.makeText(mContext, R.string.soft_update_no, Toast.LENGTH_LONG).show();
                	break;
            }
        }
    };




    /**
     * 获取软件版本号
     *
     * @param context
     * @return
     */
    private int getVersionCode(Context context)
    {
        int versionCode = 0;
        try
        {
            // 获取软件版本号，对应AndroidManifest.xml下android:versionCode
           versionCode = context.getPackageManager().getPackageInfo("com.yt.worlddatetime", 0).versionCode;
        } catch (NameNotFoundException e)
        {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 显示软件更新对话框
     */
    private void showNoticeDialog()
    {
        // 构造对话框
        AlertDialog.Builder builder = new Builder(mContext);

        builder.setTitle(R.string.soft_update_title);
        builder.setMessage(R.string.soft_update_info);
        builder.setIcon(R.drawable.ic_launcher);
        // 更新
        builder.setPositiveButton(R.string.soft_update_updatebtn, new OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                // 显示下载对话框
                showDownloadDialog();
            }
        });
        // 稍后更新
        builder.setNegativeButton(R.string.soft_update_later, new OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });
        Dialog noticeDialog = builder.create();
        noticeDialog.show();
    }

    /**
     * 显示软件下载对话框
     */
    private void showDownloadDialog()
    {
        // 构造软件下载对话框
        AlertDialog.Builder builder = new Builder(mContext);
        builder.setTitle(R.string.soft_updating);
        // 给下载对话框增加进度条
        final LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.softupdate_progress, null);
        mProgress = (ProgressBar) v.findViewById(R.id.update_progress);
        updatePre = (TextView)v.findViewById(R.id.progressPre);
        updateSize = (TextView)v.findViewById(R.id.progressSize);

        builder.setView(v);
        // 取消更新
        builder.setNegativeButton(R.string.soft_update_cancel, new OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                // 设置取消状态
                cancelUpdate = true;
            }
        });
        mDownloadDialog = builder.create();
        mDownloadDialog.show();
        // 现在文件
        downloadApk();
    }

    /**
     * 下载apk文件
     */
    private void downloadApk()
    {
        // 启动新线程下载软件
        new downloadApkThread().start();
    }

    /**
     * 下载文件线程
     *
     */
    private class downloadApkThread extends Thread
    {
        @Override
        public void run()
        {
            try
            {
                // 判断SD卡是否存在，并且是否具有读写权限
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
                {
                    // 获得存储卡的路径
                    String sdpath = Environment.getExternalStorageDirectory() + "/";
                    mSavePath = sdpath + "download";
                    URL url = new URL(mHashMap.get("downurl"));
                    // 创建连接
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestProperty("Accept-Encoding", "identity");
                    conn.connect();

                    // 获取文件大小
                    int length = conn.getContentLength();
                    // 创建输入流
                    InputStream is = conn.getInputStream();

                    File file = new File(mSavePath);
                    // 判断文件目录是否存在
                    if (!file.exists())
                    {
                        file.mkdir();
                    }
                    File apkFile = new File(mSavePath, "worlddatetime_"+mHashMap.get("version")+".apk");
                    FileOutputStream fos = new FileOutputStream(apkFile);
                    int count = 0;
                    // 缓存
                    byte buf[] = new byte[1024];
                    // 写入到文件中
                    do
                    {
                        int numread = is.read(buf);
                        count += numread;
                        // 计算进度条位置
                        downCount = count;
                        downSize = length;
                        progress = (int) (((float) count / length) * 100);
                        // 更新进度
                        mHandler.sendEmptyMessage(DOWNLOAD);
                        if (numread <= 0)
                        {
                            // 下载完成
                            mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
                            break;
                        }
                        // 写入文件
                        fos.write(buf, 0, numread);
                    } while (!cancelUpdate);// 点击取消就停止下载.
                    fos.close();
                    is.close();
                }
            } catch (MalformedURLException e)
            {
            	  Message msg = new Message();
            	  msg.what = DOWN_ERROR;
            	  handler.sendMessage(msg);
            	  e.printStackTrace();
            } catch (IOException e)
            {
            	  Message msg = new Message();
            	  msg.what = DOWN_ERROR;
            	  handler.sendMessage(msg);
            	  e.printStackTrace();
            }
            // 取消下载对话框显示
            mDownloadDialog.dismiss();
            
        }
    };

    /**
     * 安装APK文件
     */
    private void installApk()
    {
        File apkfile = new File(mSavePath, "worlddatetime_"+mHashMap.get("version")+".apk");
        if (!apkfile.exists())
        {
            return;
        }
        // 通过Intent安装APK文件
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
        mContext.startActivity(i);
    }


}

