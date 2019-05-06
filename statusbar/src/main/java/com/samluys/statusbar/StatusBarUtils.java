package com.samluys.statusbar;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.IntDef;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class StatusBarUtils {

    public static float sVirtualDensity = -1;
    public static float sVirtualDensityDpi = -1;
    private static int sStatusbarHeight = -1;
    private final static int STATUS_BAR_DEFAULT_HEIGHT_DP = 25; // 大部分状态栏都是25dp

    private final static int STATUSBAR_TYPE_DEFAULT = 0;
    private final static int STATUSBAR_TYPE_MIUI = 1;
    private final static int STATUSBAR_TYPE_FLYME = 2;
    private final static int STATUSBAR_TYPE_ANDROID6 = 3;
    private static @StatusBarType int mType = STATUSBAR_TYPE_DEFAULT;
    private static boolean isOpenMonLayer = false; // 是否为状态栏开启蒙层效果

    private static boolean supportTranslucent() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
                // Essential Phone 不支持沉浸式
                && !Build.BRAND.toLowerCase().contains("essential");
    }

    /**
     * 开启沉浸式状态栏
     *
     * @param activity
     */
    public static void transparencyBar(Activity activity) {
        transparencyBar(activity,0x40000000);
    }

    /**
     * 开启沉浸式状态栏
     *
     * @param activity
     * @param isOpen 是否开启蒙层效果的状态栏（在5.0以上机型适用）
     */
    public static void transparencyBar(Activity activity, boolean isOpen) {
        isOpenMonLayer = isOpen;
        transparencyBar(activity,0x40000000);
    }
    /**
     * 沉浸式状态栏。
     * 支持 4.4 以上版本的 MIUI 和 Flyme，以及 5.0 以上版本的其他 Android。
     *
     * @param activity 需要被设置沉浸式状态栏的 Activity。
     * @param colorOn5x 状态栏背景色
     */
    @TargetApi(19)
    public static void transparencyBar(Activity activity, @ColorInt int colorOn5x) {
        if (!supportTranslucent()) {
            // 版本小于4.4，绝对不考虑沉浸式
            return;
        }
        // 小米和魅族4.4 以上版本支持沉浸式
        if ((SystemUtils.isMeizu() || SystemUtils.isMIUI()) && !isOpenMonLayer) {
            Window window = activity.getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            if (isOpenMonLayer) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(colorOn5x);
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && supportTransclentStatusBar6()) {
                    // android 6以后可以改状态栏字体颜色，因此可以自行设置为透明
                    // ZUK Z1是个另类，自家应用可以实现字体颜色变色，但没开放接口
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(Color.TRANSPARENT);
                } else {
                    // android 5不能修改状态栏字体颜色，因此直接用FLAG_TRANSLUCENT_STATUS，nexus表现为半透明
                    // 采取setStatusBarColor的方式，部分机型不支持，那就纯黑了，保证状态栏图标可见
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(colorOn5x);
                }
            }
        }
    }

    /**
     * 设置状态栏蒙层效果（同支付宝状态栏的效果）
     * @param isOpen
     */
    public static void OpenMonLayerMode(boolean isOpen) {
        isOpenMonLayer = isOpen;
    }


    /**
     * 设置状态栏黑色字体图标
     * 适配4.4以上版本MIUI、Flyme和6.0以上版本其他Android
     *
     * @param activity
     * @return 1:MIUUI 2:Flyme 3:android6.0
     */
    public static boolean StatusBarIconDark(Activity activity) {

        if (isOpenMonLayer) {
            return false;
        }

        if (activity == null) return false;
        // ZTK C2016只能时间和电池图标变色。。。。
        if (SystemUtils.isZTKC2016()) {
            return false;
        }

        if (mType != STATUSBAR_TYPE_DEFAULT) {
            return setStatusBarLightMode(activity, mType);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (isMIUICustomStatusBarLightModeImpl() && MIUISetStatusBarLightMode(activity.getWindow(), true)) {
                mType = STATUSBAR_TYPE_MIUI;
                return true;
            } else if (FlymeSetStatusBarLightMode(activity.getWindow(), true)) {
                mType = STATUSBAR_TYPE_FLYME;
                return true;
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Android6SetStatusBarLightMode(activity.getWindow(), true);
                mType = STATUSBAR_TYPE_ANDROID6;
                return true;
            } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP ||
                    Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP_MR1) {
                transparencyBar(activity, false);
                return true;
            }else {
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                SystemBarTintManager tintManager = new SystemBarTintManager(activity);
                tintManager.setStatusBarTintColor(Color.BLACK);
                tintManager.setStatusBarTintEnabled(true);
//                tintManager.setNavigationBarTintColor(Color.BLACK);
//                tintManager.setNavigationBarTintEnabled(true);
            }
        }
        return false;
    }

    /**
     * 设置状态栏白色字体图标
     * 适配4.4以上版本MIUIV、Flyme和6.0以上版本其他Android
     *
     * @param activity
     * @return
     */
    public static boolean StatusBarIconLight(Activity activity) {

        if (isOpenMonLayer) {
            return false;
        }

        if (activity == null) return false;
        if (mType == STATUSBAR_TYPE_DEFAULT) {
            // 默认状态，不需要处理
            return true;
        }

        if (mType == STATUSBAR_TYPE_MIUI) {
            return MIUISetStatusBarLightMode(activity.getWindow(), false);
        } else if (mType == STATUSBAR_TYPE_FLYME) {
            return FlymeSetStatusBarLightMode(activity.getWindow(), false);
        } else if (mType == STATUSBAR_TYPE_ANDROID6) {
            return Android6SetStatusBarLightMode(activity.getWindow(), false);
        }
        return true;
    }

    /**
     * 获取系统区分 1 为MIUI 2为Flyme 3 为6.0以上版本其他Android
     * @return
     */
    public static int getStatusbarType () {
        return mType;
    }

    /**
     * 更改状态栏图标、文字颜色的方案是否是MIUI自家的， MIUI9 && Android 6 之后用回Android原生实现
     * 见小米开发文档说明：https://dev.mi.com/console/doc/detail?pId=1159
     */
    private static boolean isMIUICustomStatusBarLightModeImpl() {
        if (SystemUtils.isMIUIV9() && Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            return true;
        }
        return SystemUtils.isMIUIV5() || SystemUtils.isMIUIV6() ||
                SystemUtils.isMIUIV7() || SystemUtils.isMIUIV8();
    }



    /**
     * 已知系统类型时，设置状态栏黑色字体图标。
     * 支持 4.4 以上版本 MIUI 和 Flyme，以及 6.0 以上版本的其他 Android
     *
     * @param activity 需要被处理的 Activity
     * @param type     StatusBar 类型，对应不同的系统
     */
    private static boolean setStatusBarLightMode(Activity activity, @StatusBarType int type) {
        if (type == STATUSBAR_TYPE_MIUI) {
            return MIUISetStatusBarLightMode(activity.getWindow(), true);
        } else if (type == STATUSBAR_TYPE_FLYME) {
            return FlymeSetStatusBarLightMode(activity.getWindow(), true);
        } else if (type == STATUSBAR_TYPE_ANDROID6) {
            return Android6SetStatusBarLightMode(activity.getWindow(), true);
        }
        return false;
    }

    /**
     * 设置状态栏字体图标为深色，Android 6
     *
     * @param window 需要设置的窗口
     * @param light  是否把状态栏字体及图标颜色设置为深色
     * @return boolean 成功执行返回true
     */
    @TargetApi(23)
    private static boolean Android6SetStatusBarLightMode(Window window, boolean light) {
        View decorView = window.getDecorView();
        int systemUi = light ? View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR : View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        systemUi = changeStatusBarModeRetainFlag(window, systemUi);
        decorView.setSystemUiVisibility(systemUi);
        if(SystemUtils.isMIUIV9()){
            // MIUI 9 低于 6.0 版本依旧只能回退到以前的方案
            // https://github.com/QMUI/QMUI_Android/issues/160
            MIUISetStatusBarLightMode(window, light);
        }
        return true;
    }

    @TargetApi(23)
    private static int changeStatusBarModeRetainFlag(Window window, int out) {
        out = retainSystemUiFlag(window, out, View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        out = retainSystemUiFlag(window, out, View.SYSTEM_UI_FLAG_FULLSCREEN);
        out = retainSystemUiFlag(window, out, View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        out = retainSystemUiFlag(window, out, View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        out = retainSystemUiFlag(window, out, View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        out = retainSystemUiFlag(window, out, View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        return out;
    }

    public static int retainSystemUiFlag(Window window, int out, int type) {
        int now = window.getDecorView().getSystemUiVisibility();
        if ((now & type) == type) {
            out |= type;
        }
        return out;
    }

    /**
     * 设置状态栏图标为深色和魅族特定的文字风格
     * 可以用来判断是否为Flyme用户
     *
     * @param window 需要设置的窗口
     * @param dark   是否把状态栏字体及图标颜色设置为深色
     * @return boolean 成功执行返回true
     */
    public static boolean FlymeSetStatusBarLightMode(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {

            // flyme 在 6.2.0.0A 支持了 Android 官方的实现方案，旧的方案失效
            Android6SetStatusBarLightMode(window, dark);

            try {
                WindowManager.LayoutParams lp = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class
                        .getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                window.setAttributes(lp);
                result = true;
            } catch (Exception e) {

            }
        }
        return result;
    }

    /**
     * 设置状态栏字体图标为深色，需要MIUIV6以上
     *
     * @param window 需要设置的窗口
     * @param light   是否把状态栏字体及图标颜色设置为深色
     * @return boolean 成功执行返回true
     */
    public static boolean MIUISetStatusBarLightMode(Window window, boolean light) {
        boolean result = false;
        if (window != null) {
            Class clazz = window.getClass();
            try {
                int darkModeFlag;
                Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                if (light) {
                    extraFlagField.invoke(window, darkModeFlag, darkModeFlag);//状态栏透明且黑色字体
                } else {
                    extraFlagField.invoke(window, 0, darkModeFlag);//清除黑色字体
                }
                result = true;
            } catch (Exception ignored) {

            }
        }
        return result;
    }


    /**
     * 检测 Android 6.0 是否可以启用 window.setStatusBarColor(Color.TRANSPARENT)。
     */
    public static boolean supportTransclentStatusBar6() {
        return !(SystemUtils.isZUKZ1() || SystemUtils.isZTKC2016());
    }

    @IntDef({STATUSBAR_TYPE_DEFAULT, STATUSBAR_TYPE_MIUI, STATUSBAR_TYPE_FLYME, STATUSBAR_TYPE_ANDROID6})
    @Retention(RetentionPolicy.SOURCE)
    private @interface StatusBarType {
    }

    /**
     * 获取状态栏高度
     *
     * @param activity
     * @return
     */
    public static int getStatusBarHeight(Activity activity) {
//        Rect rect = new Rect();
//        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
//        return rect.top == 0 ? 60 : rect.top;

        if (sStatusbarHeight == -1) {
            initStatusBarHeight(activity);
        }
        return sStatusbarHeight;
    }

    private static void initStatusBarHeight(Context context) {
        Class<?> clazz;
        Object obj = null;
        Field field = null;
        try {
            clazz = Class.forName("com.android.internal.R$dimen");
            obj = clazz.newInstance();
            if (SystemUtils.isMeizu()) {
                try {
                    field = clazz.getField("status_bar_height_large");
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
            if (field == null) {
                field = clazz.getField("status_bar_height");
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        if (field != null && obj != null) {
            try {
                int id = Integer.parseInt(field.get(obj).toString());
                sStatusbarHeight = context.getResources().getDimensionPixelSize(id);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        if (SystemUtils.isTablet(context)
                && sStatusbarHeight > dp2px(context, STATUS_BAR_DEFAULT_HEIGHT_DP)) {
            //状态栏高度大于25dp的平板，状态栏通常在下方
            sStatusbarHeight = 0;
        } else {
            if (sStatusbarHeight <= 0) {
                if (sVirtualDensity == -1) {
                    sStatusbarHeight = dp2px(context, STATUS_BAR_DEFAULT_HEIGHT_DP);
                } else {
                    sStatusbarHeight = (int) (STATUS_BAR_DEFAULT_HEIGHT_DP * sVirtualDensity + 0.5f);
                }
            }
        }
    }

    /**
     * @param context
     * @param dpValue
     * @return int
     * @description: 单位转换：dp转px
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
