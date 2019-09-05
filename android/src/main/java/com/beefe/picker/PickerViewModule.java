package com.beefe.picker;

import android.content.res.AssetManager;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.os.Build;

import com.beefe.picker.util.MIUIUtils;
import com.beefe.picker.view.OnSelectedListener;
import com.beefe.picker.view.PickerViewAlone;
import com.beefe.picker.view.PickerViewLinkage;
import com.beefe.picker.view.ReturnData;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.util.ArrayList;

import static android.graphics.Color.argb;

/**
 * Author: <a href="https://github.com/shexiaoheng">heng</a>
 * <p>
 * Created by heng on 16/9/5.
 * <p>
 * Edited by heng on 16/9/22.
 * 1. PopupWindow height : full screen -> assignation
 * 2. Added pickerToolBarHeight support
 * <p>
 * Edited by heng on 2016/10/19.
 * 1. Added weights support
 * 2. Fixed return data bug
 * <p>
 * Edited by heng on 2016/11/16.
 * 1. Used WindowManager replace PopupWindow
 * 2. Removed method initOK() toggle() show() isPickerShow()
 * 3. Implements Application.ActivityLifecycleCallbacks
 * <p>
 * Edited by heng on 2016/11/17
 * 1. Used Dialog replace WindowManger
 * 2. Restore method show() isPickerShow()
 * <p>
 * Edited by heng on 2016/12/23
 * 1. Changed returnData type
 * 2. Added pickerToolBarFontSize
 * <p>
 * Edited by heng on 2016/12/26
 * 1. Fixed returnData bug
 * 2. Added pickerFontColor
 * 3. Added pickerFontSize
 * 4. Used LifecycleEventListener replace Application.ActivityLifecycleCallbacks
 * 5. Fixed other bug
 *
 * Edited by heng on 2017/01/17
 * 1. Added select(ReadableArray array, Callback callback)
 * 2. Optimization code
 */

public class PickerViewModule extends ReactContextBaseJavaModule implements LifecycleEventListener {
    
    private static final String FONTS = "fonts/";
    private static final String OTF = ".otf";
    private static final String TTF = ".ttf";

    private static final String REACT_CLASS = "BEEPickerManager";

    private static final String PICKER_DATA = "pickerData";
    private static final String SELECTED_VALUE = "selectedValue";

    private static final String IS_LOOP = "isLoop";

    private static final String WEIGHTS = "wheelFlex";

    private static final String PICKER_BG_COLOR = "pickerBg";

    private static final String PICKER_TOOL_BAR_BG = "pickerToolBarBg";
    private static final String PICKER_TOOL_BAR_HEIGHT = "pickerToolBarHeight";
    private static final String PICKER_TOOL_BAR_TEXT_SIZE = "pickerToolBarFontSize";

    private static final String PICKER_CONFIRM_BTN_TEXT = "pickerConfirmBtnText";
    private static final String PICKER_CONFIRM_BTN_COLOR = "pickerConfirmBtnColor";

    private static final String PICKER_CANCEL_BTN_TEXT = "pickerCancelBtnText";
    private static final String PICKER_CANCEL_BTN_COLOR = "pickerCancelBtnColor";

    private static final String PICKER_TITLE_TEXT = "pickerTitleText";
    private static final String PICKER_TITLE_TEXT_COLOR = "pickerTitleColor";

    private static final String PICKER_TEXT_COLOR = "pickerFontColor";
    private static final String PICKER_TEXT_SIZE = "pickerFontSize";
    private static final String PICKER_TEXT_ELLIPSIS_LEN = "pickerTextEllipsisLen";

    private static final String PICKER_FONT_FAMILY = "pickerFontFamily";

    private static final String PICKER_EVENT_NAME = "pickerEvent";
    private static final String EVENT_KEY_CONFIRM = "confirm";
    private static final String EVENT_KEY_CANCEL = "cancel";
    private static final String EVENT_KEY_SELECTED = "select";

    private static final String ERROR_NOT_INIT = "please initialize the component first";

    private Dialog dialog = null;

    private boolean isLoop = true;

    private String confirmText;
    private String cancelText;
    private String titleText;
    private int pickerTextEllipsisLen;

    private double[] weights;

    private ArrayList<ReturnData> returnData;

    private int curStatus;

    private PickerViewLinkage pickerViewLinkage;
    private PickerViewAlone pickerViewAlone;

    public PickerViewModule(ReactApplicationContext reactContext) {
        super(reactContext);
        reactContext.addLifecycleEventListener(this);
    }

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @ReactMethod
    public void _init(ReadableMap options) {
        Activity activity = getCurrentActivity();
        if (activity != null && options.hasKey(PICKER_DATA)) {
            View view = activity.getLayoutInflater().inflate(R.layout.picker_view, null);
            RelativeLayout barLayout = (RelativeLayout) view.findViewById(R.id.barLayout);
            TextView cancelTV = (TextView) view.findViewById(R.id.cancel);
            TextView titleTV = (TextView) view.findViewById(R.id.title);
            TextView confirmTV = (TextView) view.findViewById(R.id.confirm);
            RelativeLayout pickerLayout = (RelativeLayout) view.findViewById(R.id.pickerLayout);
            pickerViewLinkage = (PickerViewLinkage) view.findViewById(R.id.pickerViewLinkage);
            pickerViewAlone = (PickerViewAlone) view.findViewById(R.id.pickerViewAlone);

            int barViewHeight;
            if (options.hasKey(PICKER_TOOL_BAR_HEIGHT)) {
                try {
                    barViewHeight = options.getInt(PICKER_TOOL_BAR_HEIGHT);
                } catch (Exception e) {
                    barViewHeight = (int) options.getDouble(PICKER_TOOL_BAR_HEIGHT);
                }
            } else {
                barViewHeight = (int) (activity.getResources().getDisplayMetrics().density * 40);
            }
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    barViewHeight);
            barLayout.setLayoutParams(params);

            if (options.hasKey(PICKER_TOOL_BAR_BG)) {
                ReadableArray array = options.getArray(PICKER_TOOL_BAR_BG);
                int[] colors = getColor(array);
                barLayout.setBackgroundColor(argb(colors[3], colors[0], colors[1], colors[2]));
            }

            if (options.hasKey(PICKER_TOOL_BAR_TEXT_SIZE)) {
                int toolBarTextSize = options.getInt(PICKER_TOOL_BAR_TEXT_SIZE);
                cancelTV.setTextSize(toolBarTextSize);
                titleTV.setTextSize(toolBarTextSize);
                confirmTV.setTextSize(toolBarTextSize);
            }

            if (options.hasKey(PICKER_CONFIRM_BTN_TEXT)) {
                confirmText = options.getString(PICKER_CONFIRM_BTN_TEXT);
            }
            confirmTV.setText(!TextUtils.isEmpty(confirmText) ? confirmText : "");

            if (options.hasKey(PICKER_CONFIRM_BTN_COLOR)) {
                ReadableArray array = options.getArray(PICKER_CONFIRM_BTN_COLOR);
                int[] colors = getColor(array);
                confirmTV.setTextColor(argb(colors[3], colors[0], colors[1], colors[2]));
            }
            confirmTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (curStatus) {
                        case 0:
                            returnData = pickerViewAlone.getSelectedData();
                            break;
                        case 1:
                            returnData = pickerViewLinkage.getSelectedData();
                            break;
                    }
                    commonEvent(EVENT_KEY_CONFIRM);
                    hide();
                }
            });


            if (options.hasKey(PICKER_TITLE_TEXT)) {
                titleText = options.getString(PICKER_TITLE_TEXT);
            }
            titleTV.setText(!TextUtils.isEmpty(titleText) ? titleText : "");
            if (options.hasKey(PICKER_TITLE_TEXT_COLOR)) {
                ReadableArray array = options.getArray(PICKER_TITLE_TEXT_COLOR);
                int[] colors = getColor(array);
                titleTV.setTextColor(argb(colors[3], colors[0], colors[1], colors[2]));
            }

            if (options.hasKey(PICKER_CANCEL_BTN_TEXT)) {
                cancelText = options.getString(PICKER_CANCEL_BTN_TEXT);
            }
            cancelTV.setText(!TextUtils.isEmpty(cancelText) ? cancelText : "");
            if (options.hasKey(PICKER_CANCEL_BTN_COLOR)) {
                ReadableArray array = options.getArray(PICKER_CANCEL_BTN_COLOR);
                int[] colors = getColor(array);
                cancelTV.setTextColor(argb(colors[3], colors[0], colors[1], colors[2]));
            }
            cancelTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (curStatus) {
                        case 0:
                            returnData = pickerViewAlone.getSelectedData();
                            break;
                        case 1:
                            returnData = pickerViewLinkage.getSelectedData();
                            break;
                    }
                    commonEvent(EVENT_KEY_CANCEL);
                    hide();
                }
            });

            if(options.hasKey(PICKER_TEXT_ELLIPSIS_LEN)){
                pickerTextEllipsisLen = options.getInt(PICKER_TEXT_ELLIPSIS_LEN);
            }

            if (options.hasKey(IS_LOOP)) {
                isLoop = options.getBoolean(IS_LOOP);
            }

            if (options.hasKey(WEIGHTS)) {
                ReadableArray array = options.getArray(WEIGHTS);
                weights = new double[array.size()];
                for (int i = 0; i < array.size(); i++) {
                    switch (array.getType(i).name()) {
                        case "Number":
                            try {
                                weights[i] = array.getInt(i);
                            } catch (Exception e) {
                                weights[i] = array.getDouble(i);
                            }
                            break;
                        case "String":
                            try {
                                weights[i] = Double.parseDouble(array.getString(i));
                            } catch (Exception e) {
                                weights[i] = 1.0;
                            }
                            break;
                        default:
                            weights[i] = 1.0;
                            break;
                    }
                }
            }

            int pickerTextColor = 0xff000000;
            if (options.hasKey(PICKER_TEXT_COLOR)) {
                ReadableArray array = options.getArray(PICKER_TEXT_COLOR);
                int[] colors = getColor(array);
                pickerTextColor = Color.argb(colors[3], colors[0], colors[1], colors[2]);
            }

            int pickerTextSize = 16;
            if (options.hasKey(PICKER_TEXT_SIZE)) {
                try {
                    pickerTextSize = options.getInt(PICKER_TEXT_SIZE);
                } catch (Exception e) {
                    pickerTextSize = (int) options.getDouble(PICKER_TEXT_SIZE);
                }
            }

            ReadableArray pickerData = options.getArray(PICKER_DATA);

            int pickerViewHeight;
            String name = pickerData.getType(0).name();
            switch (name) {
                case "Map":
                    curStatus = 1;
                    pickerViewLinkage.setVisibility(View.VISIBLE);
                    pickerViewAlone.setVisibility(View.GONE);

                    pickerViewLinkage.setPickerData(pickerData, weights);
                    pickerViewLinkage.setTextColor(pickerTextColor);
                    pickerViewLinkage.setTextSize(pickerTextSize);
                    pickerViewLinkage.setTextEllipsisLen(pickerTextEllipsisLen);
                    pickerViewLinkage.setIsLoop(isLoop);

                    pickerViewLinkage.setOnSelectListener(new OnSelectedListener() {
                        @Override
                        public void onSelected(ArrayList<ReturnData> selectedList) {
                            returnData = selectedList;
                            commonEvent(EVENT_KEY_SELECTED);
                        }
                    });
                    pickerViewHeight = pickerViewLinkage.getViewHeight();
                    break;
                default:
                    curStatus = 0;
                    pickerViewAlone.setVisibility(View.VISIBLE);
                    pickerViewLinkage.setVisibility(View.GONE);

                    pickerViewAlone.setPickerData(pickerData, weights);
                    pickerViewAlone.setTextColor(pickerTextColor);
                    pickerViewAlone.setTextSize(pickerTextSize);
                    pickerViewAlone.setTextEllipsisLen(pickerTextEllipsisLen);
                    pickerViewAlone.setIsLoop(isLoop);

                    pickerViewAlone.setOnSelectedListener(new OnSelectedListener() {
                        @Override
                        public void onSelected(ArrayList<ReturnData> selectedList) {
                            returnData = selectedList;
                            commonEvent(EVENT_KEY_SELECTED);
                        }
                    });

                    pickerViewHeight = pickerViewAlone.getViewHeight();
                    break;
            }

            if (options.hasKey(PICKER_FONT_FAMILY)) {
                Typeface typeface = null;
                AssetManager assetManager = activity.getApplicationContext().getAssets();
                final String fontFamily = options.getString(PICKER_FONT_FAMILY);
                try {
                    String path = FONTS + fontFamily + TTF;
                    typeface = Typeface.createFromAsset(assetManager, path);
                } catch (Exception ignored) {
                    try {
                        String path = FONTS + fontFamily + OTF;
                        typeface = Typeface.createFromAsset(assetManager, path);
                    } catch (Exception ignored2) {
                        try {
                            typeface = Typeface.create(fontFamily, Typeface.NORMAL);
                        } catch (Exception ignored3) {
                        }
                    }
                }
                cancelTV.setTypeface(typeface);
                titleTV.setTypeface(typeface);
                confirmTV.setTypeface(typeface);

                pickerViewAlone.setTypeface(typeface);
                pickerViewLinkage.setTypeface(typeface);
            }

            if (options.hasKey(SELECTED_VALUE)) {
                ReadableArray array = options.getArray(SELECTED_VALUE);
                String[] selectedValue = getSelectedValue(array);
                select(selectedValue);
            }

            if (options.hasKey(PICKER_BG_COLOR)) {
                ReadableArray array = options.getArray(PICKER_BG_COLOR);
                int[] colors = getColor(array);
                pickerLayout.setBackgroundColor(argb(colors[3], colors[0], colors[1], colors[2]));
            }

            int height = barViewHeight + pickerViewHeight;
            if (dialog == null) {
                dialog = new Dialog(activity, R.style.Dialog_Full_Screen);
                dialog.setContentView(view);
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                Window window = dialog.getWindow();
                if (window != null) {
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                    }else{
                        if (MIUIUtils.isMIUI()) {
                            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION;
                        }else {
                            //layoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
                        }
                    }
                    layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                    layoutParams.format = PixelFormat.TRANSPARENT;
                    layoutParams.windowAnimations = R.style.PickerAnim;
                    layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                    layoutParams.height = height;
                    layoutParams.gravity = Gravity.BOTTOM;
                    window.setAttributes(layoutParams);   
                }
            } else {
                dialog.dismiss();
                dialog.setContentView(view);
            }
        }
    }

    @ReactMethod
    public void select(ReadableArray array, Callback callback) {
        if (dialog == null) {
            if (callback != null) {
                callback.invoke(ERROR_NOT_INIT);
            }
            return;
        }
        String[] selectedValue = getSelectedValue(array);
        select(selectedValue);
    }

    @ReactMethod
    public void show() {
        if (dialog == null) {
            return;
        }
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    @ReactMethod
    public void hide() {
        if (dialog == null) {
            return;
        }
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @ReactMethod
    public void isPickerShow(Callback callback) {
        if (callback == null)
            return;
        if (dialog == null) {
            callback.invoke(ERROR_NOT_INIT);
        } else {
            callback.invoke(null, dialog.isShowing());
        }
    }

    private int[] getColor(ReadableArray array) {
        int[] colors = new int[4];
        for (int i = 0; i < array.size(); i++) {
            switch (i) {
                case 0:
                case 1:
                case 2:
                    colors[i] = array.getInt(i);
                    break;
                case 3:
                    colors[i] = (int) (array.getDouble(i) * 255);
                    break;
                default:
                    break;
            }
        }
        return colors;
    }

    private String[] getSelectedValue(ReadableArray array) {
        String[] selectValue = new String[array.size()];
        String value = "";
        for (int i = 0; i < array.size(); i++) {
            switch (array.getType(i).name()) {
                case "Boolean":
                    value = String.valueOf(array.getBoolean(i));
                    break;
                case "Number":
                    try {
                        value = String.valueOf(array.getInt(i));
                    } catch (Exception e) {
                        value = String.valueOf(array.getDouble(i));
                    }
                    break;
                case "String":
                    value = array.getString(i);
                    break;
            }
            selectValue[i] = value;
        }
        return selectValue;
    }

    private void select(String[] selectedValue) {
        switch (curStatus) {
            case 0:
                pickerViewAlone.setSelectValue(selectedValue);
                break;
            case 1:
                pickerViewLinkage.setSelectValue(selectedValue);
                break;
        }
    }

    private void commonEvent(String eventKey) {
        WritableMap map = Arguments.createMap();
        map.putString("type", eventKey);
        WritableArray indexes = Arguments.createArray();
        WritableArray values = Arguments.createArray();
        for (ReturnData data : returnData) {
            indexes.pushInt(data.getIndex());
            values.pushString(data.getItem());
        }
        map.putArray("selectedValue", values);
        map.putArray("selectedIndex", indexes);
        sendEvent(getReactApplicationContext(), PICKER_EVENT_NAME, map);
    }

    private void sendEvent(ReactContext reactContext,
                           String eventName,
                           @Nullable WritableMap params) {
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }

    @Override
    public void onHostResume() {

    }

    @Override
    public void onHostPause() {
        hide();
        dialog = null;
    }

    @Override
    public void onHostDestroy() {
        hide();
        dialog = null;
    }
}
