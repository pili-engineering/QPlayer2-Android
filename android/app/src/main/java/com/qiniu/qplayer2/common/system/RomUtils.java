
package com.qiniu.qplayer2.common.system;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import java.util.Locale;

/**
 * Created by feifan on 2017/2/10.
 * Contacts me:404619986@qq.com
 */

public class RomUtils {

    private static final String TAG = "RomUtils";

    public static boolean checkSupportVersion(){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return false;
        }
        return true;
    }

    public static String getSystemProperty(String propName) {
        return SystemProperties.get(propName, null);
    }

    /**
     * 判断是否为华为系统
     */
    public static boolean isHuaweiRom() {
        String manufacturer = Build.MANUFACTURER;
        return !TextUtils.isEmpty(manufacturer) && manufacturer.contains("HUAWEI");
    }

    /**
     * 判断是否为荣耀系统
     */
    public static boolean isHonorRom() {
        String manufacturer = Build.MANUFACTURER;
        return !TextUtils.isEmpty(manufacturer) && manufacturer.contains("HONOR");
    }

    /**
     * 判断是否为小米系统
     */
    public static boolean isMiuiRom() {
        return !TextUtils.isEmpty(getSystemProperty("ro.miui.ui.version.name"));
    }

    /**
     * 获取小米MIUI版本号，失败默认返回-1
     */
    public static int getMiuiVersion() {
        String version = getSystemProperty("ro.miui.ui.version.name");
        if (!TextUtils.isEmpty(version)) {
            try {
                return Integer.parseInt(version.substring(1));
            } catch (Exception e) {
                Log.e(TAG, "get miui version code error");
            }
        }
        return -1;
    }

    /**
     * 判断是否为魅族系统
     */
    public static boolean isMeizuRom() {
        String meizuFlymeOSFlag = getSystemProperty("ro.build.display.id");
        String meizuVersionFlag = getSystemProperty("ro.build.flyme.version");
        return !TextUtils.isEmpty(meizuVersionFlag)
                || (!TextUtils.isEmpty(meizuFlymeOSFlag) && meizuFlymeOSFlag.toLowerCase(Locale.getDefault()).contains("flyme"));
    }

    /**
     * 判断是否为360系统
     */
    public static boolean checkIs360Rom() {
        String manufacturer = Build.MANUFACTURER;
        return !TextUtils.isEmpty(manufacturer) && manufacturer.contains("QiKU");
    }

    /**
     * 判断是否为乐视系统
     */
    public static boolean isLetvRom() {
        return !TextUtils.isEmpty(getSystemProperty("ro.letv.eui"));
    }

    /**
     * 判断是否为Oppo系统
     */
    public static boolean isOppoRom() {
        String a = getSystemProperty("ro.product.brand");
        return !TextUtils.isEmpty(a) && a.toLowerCase(Locale.getDefault()).contains("oppo");
    }

    /**
     * 判断是否为Vivo系统
     */
    public static boolean isVivoRom() {
        String a = getSystemProperty("ro.vivo.os.name");
        return !TextUtils.isEmpty(a) && a.toLowerCase(Locale.getDefault()).contains("funtouch");
    }

    /**
     * 判断是否为联想系统
     */
    public static boolean isLenovoRom() {
        String fingerPrint = Build.FINGERPRINT;
        if (!TextUtils.isEmpty(fingerPrint)) {
            return fingerPrint.contains("VIBEUI_V2");
        }
        String a = getSystemProperty("ro.build.version.incremental");
        return !TextUtils.isEmpty(a) && a.contains("VIBEUI_V2");
    }

    /**
     * 判断是否为CoolPad系统
     */
    public static boolean isCoolPadRom() {
        String model = Build.MODEL;
        String fingerPrint = Build.FINGERPRINT;
        return (!TextUtils.isEmpty(model) && model.toLowerCase(Locale.getDefault()).contains("coolpad"))
                || (!TextUtils.isEmpty(fingerPrint) && fingerPrint.toLowerCase(Locale.getDefault()).contains("coolpad"));
    }

    /**
     * 判断是否为中兴系统
     */
    public static boolean isZTERom() {
        String manufacturer = Build.MANUFACTURER;
        if (!TextUtils.isEmpty(manufacturer)) {
            return manufacturer.toLowerCase(Locale.getDefault()).contains("nubia") || manufacturer.toLowerCase(Locale.getDefault()).contains("zte");
        }
        String fingerPrint = Build.FINGERPRINT;
        return !TextUtils.isEmpty(fingerPrint) && (fingerPrint.toLowerCase(Locale.getDefault()).contains("nubia") || fingerPrint.toLowerCase(Locale.getDefault()).contains("zte"));
    }

    /**
     * 判断是否为三星系统
     */
    @SuppressWarnings("SimplifiableIfStatement")
    public static boolean isSamsungRom() {
        String fingerPrint = Build.FINGERPRINT;
        if (!TextUtils.isEmpty(fingerPrint)) {
            return fingerPrint.toLowerCase(Locale.getDefault()).contains("samsung");
        }
        String manufacturer = Build.MANUFACTURER;
        if (!TextUtils.isEmpty(manufacturer)) {
            return manufacturer.toLowerCase(Locale.getDefault()).contains("samsung");
        }
        return false;
    }

    /**
     * 判断是否为锤子系统
     */
    public static boolean isSmartisanRom() {
        // TODO: 2017/2/15
        return false;
    }

    /**
     * 判断是否为海尔系统
     */
    public static boolean isHaierRom() {
        // TODO: 2017/2/15
        return false;
    }

    /**
     * 判断是否为OnePlus系统
     */
    public static boolean isOnePlusRom() {
        String fingerPrint = Build.FINGERPRINT;
        if (!TextUtils.isEmpty(fingerPrint)) {
            return fingerPrint.toLowerCase(Locale.getDefault()).contains("oneplus");
        }
        String manufacturer = Build.MANUFACTURER;
        if (!TextUtils.isEmpty(manufacturer)) {
            return manufacturer.toLowerCase(Locale.getDefault()).contains("oneplus");
        }
        return false;
    }

    /**
     * 判断系统是否是支持插帧的OnePlus系统[in2020, in2021, in2023, in2025]
     */
    public static boolean isOnePlusInsertingFrameRom() {
        String model = Build.MODEL.toLowerCase();
        return isOnePlusRom() && (model.contains("in2020") || model.contains("in2021") || model.contains("in2023") || model.contains("in2025"));
    }

    public static boolean isDomesticSpecialRom() {
        return RomUtils.isMiuiRom()
                || RomUtils.isHuaweiRom()
                || RomUtils.isMeizuRom()
                || RomUtils.checkIs360Rom()
                || RomUtils.isOppoRom()
                || RomUtils.isVivoRom()
                || RomUtils.isLetvRom()
                || RomUtils.isZTERom()
                || RomUtils.isLenovoRom()
                || RomUtils.isCoolPadRom();
    }
}
