package com.mdj.moudle.qrcode;

import com.mdj.moudle.referee.RefereeBean;
import com.mdj.utils.MdjLog;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by tt on 2016/9/1.
 */
public class QrCodeHelper {
    public static final int QRCODE_TYPE_BEAUTICIAN = 1;//技师二维码

    public static Object parseQrCode(int qrType,String data) throws Exception{
        switch (qrType){
            case QRCODE_TYPE_BEAUTICIAN:
                return parseBeauticianQrCode(data);
            default:
                break;
        }
        return null;
    }

    private static Object parseBeauticianQrCode(String data) throws UnsupportedEncodingException {
        String decodeResult = URLDecoder.decode(data, "utf-8");
        String avatar="",name="",phone="",beauticianId="",couponId="";

        MdjLog.log("decodeResult:" + decodeResult);
        try{
            int avatarS = decodeResult.indexOf("avatar=")+"avatar=".length();
            int avatarE = decodeResult.indexOf("&name=");
            avatar = decodeResult.substring(avatarS, avatarE);

            int nameS = decodeResult.indexOf("name=")+"name=".length();
            int nameE = decodeResult.indexOf("&",nameS+1);
            name = decodeResult.substring(nameS, nameE);

            int phoneS = decodeResult.indexOf("tel=")+"tel=".length();
            int phoneE = decodeResult.indexOf("&", phoneS+1);
            phone = decodeResult.substring(phoneS, phoneE);

            int beauticianIdS = decodeResult.indexOf("bid=")+"bid=".length();
            int beauticianIdE = decodeResult.indexOf("&", beauticianIdS+1);
            beauticianId = decodeResult.substring(beauticianIdS, beauticianIdE);

            int couponIdS = decodeResult.indexOf("couponId=")+"couponId=".length();
            int couponIdE = decodeResult.indexOf("&", couponIdS+1);
            couponId = decodeResult.substring(couponIdS, couponIdE);
        }catch (Exception e){
            e.printStackTrace();
        }

        RefereeBean refereeBean = new RefereeBean(beauticianId,name,phone,avatar,couponId);

        return refereeBean;
    }

}
