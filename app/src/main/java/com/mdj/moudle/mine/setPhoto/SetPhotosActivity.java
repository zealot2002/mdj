package com.mdj.moudle.mine.setPhoto;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.mdj.R;
import com.mdj.application.MyApp;
import com.mdj.cache.CacheManager;
import com.mdj.constant.CommonConstant;
import com.mdj.constant.HttpConstant;
import com.mdj.constant.SPConstant;
import com.mdj.moudle.BaseActivity;
import com.mdj.moudle.user.UserBean;
import com.mdj.utils.AESUtils;
import com.mdj.utils.CommonUtil;
import com.mdj.utils.Constant;
import com.mdj.utils.DisplayImageOptionsUtil;
import com.mdj.utils.MdjLog;
import com.mdj.utils.MdjUtils;
import com.mdj.utils.SPUtils;
import com.mdj.view.RoundImageView;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.util.LinkedHashMap;

/**
 * @author 吴世文
 * @Description: 设置个人信息
 */
public class SetPhotosActivity extends BaseActivity implements View.OnClickListener {
    /**
     * 头像文件名称
     */
    private final String PHOTO_NAME = "head.jpg";
    /**
     * 临时图片，设置成功后删除
     */
    private final String TEMP_NAME = "MdjPhoto.png";
    /**
     * 头像文件路径
     */
    private final String PHOTO_PATH = Environment.getExternalStorageDirectory() + File.separator + "mdj";

    /* 请求识别码 */
    private static final int CODE_GALLERY_REQUEST = 0;
    private static final int CODE_CAMERA_REQUEST = 1; //拍照请求码
    private static final int CODE_RESULT_REQUEST = 2;//拍照修剪请求码
    private boolean hasPic = false;
    File deleteFile;

    private ImageView ivReturn;
    private TextView tvSure, photograph, album, cancel, tvChange;
    private RelativeLayout rlChangePhoto;
    private EditText etNickname;
    private RoundImageView riUserPhoto;
    private LinearLayout llChoiceAlbum;
    private boolean iskitkat;
    private LinearLayout ll_status;
    private UserBean userBean;
    private Uri uri1;

    @Override
    public void findViews() {
        setContentView(R.layout.setphotos);
        mContext = this;

        ll_status = (LinearLayout) findViewById(R.id.ll_status);
        ll_status.setVisibility(View.VISIBLE);
        tvChange = (TextView) findViewById(R.id.tvChange);
        tvSure = (TextView) findViewById(R.id.tvSure);
        ivReturn = (ImageView) findViewById(R.id.ivReturn);
        etNickname = (EditText) findViewById(R.id.etNickname);
        riUserPhoto = (RoundImageView) findViewById(R.id.riUserPhoto);
        rlChangePhoto = (RelativeLayout) findViewById(R.id.rlChangePhoto);

        llChoiceAlbum = (LinearLayout) findViewById(R.id.llChoiceAlbum);
        photograph = (TextView) findViewById(R.id.photograph);
        album = (TextView) findViewById(R.id.album);
        cancel = (TextView) findViewById(R.id.cancel);


        userBean = CacheManager.getInstance().getUserBean();
        MyApp.instance.getImageLoad().displayImage(userBean.getImgUrl(), riUserPhoto, DisplayImageOptionsUtil.getCommonCacheOptions());
        etNickname.setText(userBean.getName());

        ivReturn.setOnClickListener(this);
        tvSure.setOnClickListener(this);
        album.setOnClickListener(this);
        rlChangePhoto.setOnClickListener(this);
        photograph.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivReturn:
                finish();
                break;
            case R.id.tvSure:
                final String nickName = etNickname.getText().toString().trim();

                if(TextUtils.isEmpty(nickName)){
                    showShortToast("名称不能为空");
                }else if(userBean.getName().equals(nickName) && !hasPic){
                    finish();
                }else{
                    uploadHeaderAndName(nickName);
                }
                break;
            case R.id.rlChangePhoto:
                llChoiceAlbum.setVisibility(View.VISIBLE);
                CommonUtil.closeKeybord(etNickname, mContext); // 强制隐藏键盘
                break;
            case R.id.photograph:
                choseHeadImageFromCameraCapture();
                break;
            case R.id.album:
                choseHeadImageFromGallery();
                break;
            case R.id.cancel:
                llChoiceAlbum.setVisibility(View.INVISIBLE);
                break;
        }
    }

    /** 上传头像
     * @param nickName
     * */
    private void uploadHeaderAndName(String nickName){
        try {
            String url = HttpConstant.NEW_SERVER_URL+HttpConstant.MODIFY_INFORMATION;
            RequestParams params = new RequestParams();
            params.addBodyParameter("phone", userBean.getPhone());
            params.addBodyParameter("name", nickName);
            params.addBodyParameter("uId", userBean.getId());

            if (hasPic)
                params.addBodyParameter("file", new File(PHOTO_PATH,PHOTO_NAME));
            else
                params.addBodyParameter("file", "");

            /*add header start*/
            params.addHeader("User-Agent", MdjUtils.getMdjUserAgent(MyApp.getInstance()));
            LinkedHashMap<String, String> fMap = new LinkedHashMap<>();
            fMap.put("phone", userBean.getPhone());
            fMap.put("name", nickName);
            fMap.put("uId",userBean.getId());
            if(!hasPic){
                fMap.put("file","");
            }
            params.addHeader("Auth",MdjUtils.getMDJAuth(fMap));
            params.addHeader("Token", SPUtils.getString(mContext, SPConstant.TOKEN,""));
            /*add header end*/

            HttpUtils http = new HttpUtils();
            http.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
                @Override
                public void onStart() {
                    showLoading();
                }

                @Override
                public void onLoading(long total, long current, boolean isUploading) {
                }

                @Override
                public void onSuccess(ResponseInfo<String> responseInfo) {
                    try{
                        String msg = responseInfo.result;
                        String decrypt = AESUtils.decrypt(msg, Constant.AES_PASSWORD, Constant.AES_IV);
                        JSONTokener jsonParser = new JSONTokener(decrypt);
                        JSONObject obj = (JSONObject) jsonParser.nextValue();
                        MdjLog.log("修改个人信息返回数据 ："+obj.toString());
                        int err = obj.getInt("err");
                        if (err == 0) {
                            closeLoading();
                            setResult(Activity.RESULT_OK);
                            finish();
                        } else {
                            closeLoading();
                            String errMsg = obj.getString("msg");
                            showShortToast(errMsg);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFailure(HttpException error, String msg) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // 启动手机相机拍摄照片作为头像
    private void choseHeadImageFromCameraCapture() {
        // 判断存储卡是否可用，存储照片文件，
        if (CommonUtil.isExitsSdcard()) {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            deleteFile = new File(PHOTO_PATH, TEMP_NAME);
            if (deleteFile != null) {
                if (!deleteFile.getParentFile().exists()) {
                    deleteFile.getParentFile().mkdirs();
                }
            }
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(deleteFile));
            startActivityForResult(cameraIntent, CODE_CAMERA_REQUEST);
        } else {
            showShortToast("请插入SD卡!");

        }
    }

    // 从本地相册选取图片作为头像
    private void choseHeadImageFromGallery() {
        Intent intentFromGallery = new Intent();
        // 设置文件类型
        intentFromGallery.setType("image/*");
        intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intentFromGallery, CODE_GALLERY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        } else {
            switch (requestCode) {
                case CODE_CAMERA_REQUEST://拍照
                    if (CommonUtil.isExitsSdcard()) {
                        resizeImage(Uri.fromFile(new File(PHOTO_PATH, TEMP_NAME)));
                    } else {
                        showShortToast("未找到存储卡，无法存储照片！");
                    }
                    break;
                case CODE_GALLERY_REQUEST://从图库选择
                    resizeImage(data.getData());
                    break;
                case CODE_RESULT_REQUEST:
                    if (data != null) {
                        try {
//                            Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri1));
                            showResizeImage(data);
                            llChoiceAlbum.setVisibility(View.INVISIBLE);
                            hasPic = true;
                            tvChange.setVisibility(View.GONE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 显示修改过的图片，将拍照得到的原图删除
     */
    @SuppressWarnings("deprecation")
    private void showResizeImage(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            Drawable drawable = new BitmapDrawable(photo);
            riUserPhoto.setImageDrawable(drawable);
            if (deleteFile != null) {
                deleteFile.delete();
            }

        }
//        Drawable drawable = new BitmapDrawable(data);
//        riUserPhoto.setImageDrawable(drawable);
//        if (deleteFile != null) {
//            deleteFile.delete();
//            }
    }

    /**
     * 裁剪原始的图片
     */
    private void resizeImage(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        /**
         * 此方法返回的图片只能是小图片（sumsang测试为高宽160px的图片）
         * 故将图片保存在Uri中，调用时将Uri转换为Bitmap，此方法还可解决miui系统不能return data的问题
         */
        intent.putExtra("return-data", true);

//        uri1 = Uri.fromFile(new File(PHOTO_PATH, PHOTO_NAME));
        File tempFile = new File(PHOTO_PATH, PHOTO_NAME);
        intent.putExtra("output", Uri.fromFile(tempFile));  // 保存至指定路径
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
        startActivityForResult(intent, CODE_RESULT_REQUEST);
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            MobclickAgent.onPageStart(CommonUtil.generateTag());
            MobclickAgent.onResume(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            MobclickAgent.onPageEnd(CommonUtil.generateTag());
            MobclickAgent.onPause(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
