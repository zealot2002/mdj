package com.mdj.moudle.appUpdate;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Vibrator;
import android.text.method.ScrollingMovementMethod;
import android.widget.Scroller;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.mdj.application.MyApp;
import com.mdj.cache.CacheManager;
import com.mdj.constant.HttpConstant;
import com.mdj.constant.SPConstant;
import com.mdj.utils.CommonUtil;
import com.mdj.utils.Constant;
import com.mdj.utils.JsonObjectPostRequest;
import com.mdj.utils.MdjLog;
import com.mdj.utils.MdjUtils;
import com.mdj.utils.SPUtils;
import com.mdj.utils.ToastUtils;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by tt on 2016/4/25.
 */
public class AppUpdateManagement {
    private static final String TAG = "AppUpdateManagement";

    private static final String APP_NAME = "mdj";
    /***************************************************************************************************/
    //新app版本管理
    public static void checkAppUpdate(Context context) {
        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
        map.put("agent", "App.appVersionApi");
        map.put("appId", "1");//1，用户端；2，技师端
        map.put("device_type", Constant.DEVICE_TYPE);
        map.put("imei", CommonUtil.getPhoneImei(context));
        map.put("platformType", "1");//1，android；2，ios
        map.put("timestamp", MdjUtils.getTimes());
        map.put("versionCode", "" + CommonUtil.getAppVersionCode(context));
        requestServer(context, HttpConstant.OLD_SERVER_URL, map);
    }

    private static void parseCheckAppUpdate(String str) throws Exception {
        JSONTokener jsonParser = new JSONTokener(str);
        JSONObject obj = (JSONObject) jsonParser.nextValue();
        int err = obj.getInt("errno");
        if (err == 0) {
            JSONObject dataObj = obj.getJSONObject("data");
//			getInstallPackageFile
            JSONObject installPackageFileObj = dataObj.getJSONObject("installPackageFile");
            VersionBean versionBean = new VersionBean();

            versionBean.getInstallPackageFile().setVersionCode(Integer.valueOf(installPackageFileObj.getString("versionCode")));
            versionBean.getInstallPackageFile().setVersionName(installPackageFileObj.getString("versionName"));
            versionBean.getInstallPackageFile().setUrl(installPackageFileObj.getString("url"));
            versionBean.getInstallPackageFile().setChangeList(installPackageFileObj.getString("changeList"));
            versionBean.getInstallPackageFile().setAppId(Integer.valueOf(installPackageFileObj.getString("appId")));
            versionBean.getInstallPackageFile().setCreateTime(installPackageFileObj.getString("createTime"));
            versionBean.getInstallPackageFile().setIsMandatory(Integer.valueOf(installPackageFileObj.getString("isMandatory")));
            versionBean.getInstallPackageFile().setIsSilent(Integer.valueOf(installPackageFileObj.getString("isSilent")));

//			bundle

            //check valid

            //save
            CacheManager.getInstance().setVersionBean(versionBean);
        } else {
            String message = obj.getString("errmsg");
            throw new Exception(message);
        }
    }
//比较 当前app版本号和缓存版本号（mainActivity中拿到的最新版本号） 大小
    public static boolean needUpdate(Context context) {
        int versionCode = CommonUtil.getAppVersionCode(context);
        if (CacheManager.getInstance().getVersionBean().getInstallPackageFile().getVersionCode() > versionCode) {
            try{
                updataDialog(context);
                return true;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 下载
     */
    private static void download(Context context) {
        Intent intent = new Intent(context, DownAPKService.class);
        intent.putExtra("apk_url", CacheManager.getInstance().getVersionBean().getInstallPackageFile().getUrl());
        context.startService(intent);
        ToastUtils.show(context, "正在后台进行下载，稍后会自动安装", 0);
    }

    /**
     * 更新提示的对话框
     */
    private static void updataDialog(final Context context) {
        //服务器大于本地提示更新
        if (CacheManager.getInstance().getVersionBean().getInstallPackageFile().getIsMandatory() == 0) {
            AlertDialog dialog = new AlertDialog.Builder(context).setTitle("更新提示：").setMessage(CacheManager.getInstance().getVersionBean().getInstallPackageFile().getChangeList())
                    .setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            downloadApkFromServer(CacheManager.getInstance().getVersionBean().getInstallPackageFile().getUrl(), context);
                        }
                    }).setNegativeButton("下次再说", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                        }
                    }).setCancelable(false).show();

            dialog.setCanceledOnTouchOutside(false);
            TextView textView = (TextView) dialog.findViewById(android.R.id.message);
            textView.setMaxLines(5);
            textView.setScroller(new Scroller(context));
            textView.setVerticalScrollBarEnabled(true);
            textView.setMovementMethod(new ScrollingMovementMethod());

        } else {// 强制更新
//            new AlertDialog.Builder(context).setTitle("更新提示：").setMessage(CacheManager.getInstance().getVersionBean().getInstallPackageFile().getChangeList())
//                    .setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface arg0, int arg1) {
//                            downloadApkFromServer(CacheManager.getInstance().getVersionBean().getInstallPackageFile().getUrl(),context);
//                        }
//                    }).setCancelable(false).show().setCanceledOnTouchOutside(false);
            AlertDialog dialog = new AlertDialog.Builder(context).setTitle("更新提示：").setMessage(CacheManager.getInstance().getVersionBean().getInstallPackageFile().getChangeList())
                    .setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            downloadApkFromServer(CacheManager.getInstance().getVersionBean().getInstallPackageFile().getUrl(), context);
                        }
                    }).setCancelable(false).show();

            dialog.setCanceledOnTouchOutside(false);
            TextView textView = (TextView) dialog.findViewById(android.R.id.message);
            textView.setMaxLines(5);
            textView.setScroller(new Scroller(context));
            textView.setVerticalScrollBarEnabled(true);
            textView.setMovementMethod(new ScrollingMovementMethod());
        }
    }

    static ProgressDialog progressDialog = null;

    private static void downloadApkFromServer(String path,final Context context) {
        if(progressDialog == null){
            progressDialog = new ProgressDialog(context);
        }
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMessage("正在下载更新");
        progressDialog.setCancelable(false);
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
        new AsyncTask<String, Integer, File>() {
            @Override
            protected File doInBackground(String... path) {
                OutputStream output = null;
                File filecomplete = null;
                try {
                    URL url = new URL(path[0]);
                    HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
                    InputStream input = urlConn.getInputStream();
                    String temppath = Environment.getExternalStorageDirectory().getPath() + File.separator + APP_NAME;
                    File file = new File(temppath);
                    int total = urlConn.getContentLength();

                    if (!file.exists()) {
                        file.mkdir();
                    }
                    filecomplete = new File(file.getAbsolutePath() + File.separator + APP_NAME + ".apk");
                    output = new FileOutputStream(filecomplete);
                    byte buffer[] = new byte[1024];
                    int length = -1;
                    int hasread = 0;
                    while ((length = input.read(buffer)) != -1) {
                        output.write(buffer, 0, length);
                        hasread = hasread + length;
                        // Thread.sleep(5);//延迟下载，看到下载效果更明显

                        progressDialog.setProgress((Integer) (hasread * 100) / total);

                        publishProgress((Integer) (hasread * 100) / total);
                    }
                    output.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                    return filecomplete;
                } finally {
                    try {
                        if(output!=null){
                            output.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return filecomplete;
            }

            @Override
            protected void onProgressUpdate(Integer[] values) {
                super.onProgressUpdate(values);
                progressDialog.setProgress(values[0]);
            }

            @Override
            protected void onPreExecute() {
                progressDialog.setMax(100);
            }

            @Override
            protected void onPostExecute(File result) {
                if (result != null) {
                    progressDialog.dismiss();
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(result), "application/vnd.android.package-archive");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    //震动提示
                    Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(1000L);//参数是震动时间(long类型)
                    context.startActivity(intent);// 下载完成之后自动弹出安装界面
                    MyApp.getInstance().exit();
                }
            }

            ;
        }.execute(path);
    }

    public static void requestServer(final Context context, String URL, LinkedHashMap<String, String> map) {
        try {
            map.put("sign", MdjUtils.getSign(map));
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            JsonObjectPostRequest jsonObjectPostRequest = new JsonObjectPostRequest(URL, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if(response != null){
                        try{
                            parseCheckAppUpdate(response.toString());
                            needUpdate(context);

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    MdjLog.logE("zzy", "onErrorResponse " + error.toString());
                    String str = "exception: "+ error.toString();
                }
            }, map) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
//                    headers.put("User-Agent",  CacheManager.getInstance().());
                    headers.put("TOKEN", SPUtils.getString(context, SPConstant.TOKEN, ""));
                    return headers;
                }
            };
            requestQueue.add(jsonObjectPostRequest);
        } catch (Exception e) {
            e.printStackTrace();
            MdjLog.logE(TAG, e.toString());
        }
    }
}
