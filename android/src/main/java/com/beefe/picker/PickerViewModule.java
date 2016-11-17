package com.beefe.picker;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beefe.picker.view.OnSelectedListener;
import com.beefe.picker.view.PickerViewAlone;
import com.beefe.picker.view.PickerViewLinkage;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
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

/**
 * Author: heng <a href="https://github.com/shexiaoheng"/>
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
 */

public class PickerViewModule extends ReactContextBaseJavaModule implements Application.ActivityLifecycleCallbacks {

    private static final String REACT_CLASS = "BEEPickerManager";

    private static final String PICKER_DATA = "pickerData";
    private static final String SELECTED_VALUE = "selectedValue";

    private static final String IS_LOOP = "isLoop";

    private static final String WEIGHTS = "wheelFlex";

    private static final String PICKER_BG_COLOR = "pickerBg";

    private static final String TEXT_BAR_COLOR = "pickerToolBarBg";
    private static final String TEXT_BAR_HEIGHT = "pickerToolBarHeight";

    private static final String CONFIRM_TEXT = "pickerConfirmBtnText";
    private static final String CONFIRM_TEXT_COLOR = "pickerConfirmBtnColor";

    private static final String CANCEL_TEXT = "pickerCancelBtnText";
    private static final String CANCEL_TEXT_COLOR = "pickerCancelBtnColor";

    private static final String TITLE_TEXT = "pickerTitleText";
    private static final String TITLE_TEXT_COLOR = "pickerTitleColor";

    private static final String PICKER_EVENT_NAME = "pickerEvent";
    private static final String EVENT_KEY_CONFIRM = "confirm";
    private static final String EVENT_KEY_CANCEL = "cancel";
    private static final String EVENT_KEY_SELECTED = "select";

    private static final String ERROR_NOT_INIT = "please initialize";

    private Dialog dialog = null;

    private boolean isLoop = true;

    private String confirmText;
    private String cancelText;
    private String titleText;

    private double[] weights;

    private ArrayList<String> returnData;

    private int curStatus;

    public PickerViewModule(ReactApplicationContext reactContext) {
        super(reactContext);
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
            final PickerViewLinkage pickerViewLinkage = (PickerViewLinkage) view.findViewById(R.id.pickerViewLinkage);
            final PickerViewAlone pickerViewAlone = (PickerViewAlone) view.findViewById(R.id.pickerViewAlone);

            int barViewHeight;
            if (options.hasKey(TEXT_BAR_HEIGHT)) {
                try {
                    barViewHeight = options.getInt(TEXT_BAR_HEIGHT);
                } catch (Exception e) {
                    barViewHeight = (int) options.getDouble(TEXT_BAR_HEIGHT);
                }
            } else {
                barViewHeight = (int) (activity.getResources().getDisplayMetrics().density * 40);
            }
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    barViewHeight);
            barLayout.setLayoutParams(params);

            if (options.hasKey(TEXT_BAR_COLOR)) {
                ReadableArray array = options.getArray(TEXT_BAR_COLOR);
                int[] colors = getColor(array);
                barLayout.setBackgroundColor(Color.argb(colors[3], colors[0], colors[1], colors[2]));
            }


            if (options.hasKey(CONFIRM_TEXT)) {
                confirmText = options.getString(CONFIRM_TEXT);
            }
            confirmTV.setText(!TextUtils.isEmpty(confirmText) ? confirmText : "");

            if (options.hasKey(CONFIRM_TEXT_COLOR)) {
                ReadableArray array = options.getArray(CONFIRM_TEXT_COLOR);
                int[] colors = getColor(array);
                confirmTV.setTextColor(Color.argb(colors[3], colors[0], colors[1], colors[2]));
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


            if (options.hasKey(TITLE_TEXT)) {
                titleText = options.getString(TITLE_TEXT);
            }
            titleTV.setText(!TextUtils.isEmpty(titleText) ? titleText : "");
            if (options.hasKey(TITLE_TEXT_COLOR)) {
                ReadableArray array = options.getArray(TITLE_TEXT_COLOR);
                int[] colors = getColor(array);
                titleTV.setTextColor(Color.argb(colors[3], colors[0], colors[1], colors[2]));
            }

            if (options.hasKey(CANCEL_TEXT)) {
                cancelText = options.getString(CANCEL_TEXT);
            }
            cancelTV.setText(!TextUtils.isEmpty(cancelText) ? cancelText : "");
            if (options.hasKey(CANCEL_TEXT_COLOR)) {
                ReadableArray array = options.getArray(CANCEL_TEXT_COLOR);
                int[] colors = getColor(array);
                cancelTV.setTextColor(Color.argb(colors[3], colors[0], colors[1], colors[2]));
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

            String[] selectValue = {};
            if (options.hasKey(SELECTED_VALUE)) {
                ReadableArray array = options.getArray(SELECTED_VALUE);
                selectValue = new String[array.size()];
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
                    pickerViewLinkage.setIsLoop(isLoop);

                    pickerViewLinkage.setOnSelectListener(new OnSelectedListener() {
                        @Override
                        public void onSelected(ArrayList<String> selectedList) {
                            returnData = selectedList;
                            commonEvent(EVENT_KEY_SELECTED);
                        }
                    });
                    pickerViewLinkage.setSelectValue(selectValue);
                    pickerViewHeight = pickerViewLinkage.getViewHeight();
                    break;
                default:
                    curStatus = 0;
                    pickerViewAlone.setVisibility(View.VISIBLE);
                    pickerViewLinkage.setVisibility(View.GONE);

                    pickerViewAlone.setPickerData(pickerData, weights);
                    pickerViewAlone.setIsLoop(isLoop);

                    pickerViewAlone.setOnSelectedListener(new OnSelectedListener() {
                        @Override
                        public void onSelected(ArrayList<String> selectedList) {
                            returnData = selectedList;
                            commonEvent(EVENT_KEY_SELECTED);
                        }
                    });

                    pickerViewAlone.setSelectValue(selectValue);
                    pickerViewHeight = pickerViewAlone.getViewHeight();
                    break;
            }

            if (options.hasKey(PICKER_BG_COLOR)) {
                ReadableArray array = options.getArray(PICKER_BG_COLOR);
                int[] colors = getColor(array);
                pickerLayout.setBackgroundColor(Color.argb(colors[3], colors[0], colors[1], colors[2]));
            }

            int height = barViewHeight + pickerViewHeight;
            if (dialog == null) {
                dialog = new Dialog(activity, R.style.Dialog_Full_Screen);
                dialog.setContentView(view);
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                Window window = dialog.getWindow();
                if (window != null) {
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
            dialog.show();
        }
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

    private void commonEvent(String eventKey) {
        WritableMap map = Arguments.createMap();
        WritableArray array = Arguments.createArray();
        for (String item : returnData) {
            array.pushString(item);
        }
        map.putArray(eventKey, array);
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
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        hide();
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }
}
