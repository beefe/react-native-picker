package com.beefe.picker;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
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
 *
 * Created by heng on 16/9/5.
 *
 * Edited by heng on 16/9/22.
 *  1. PopupWindow height : full screen -> assignation
 *  2. Added pickerToolBarHeight support
 */

public class PickerViewModule extends ReactContextBaseJavaModule {

    private static final String REACT_CLASS = "BEEPickerManager";

    private static final String PICKER_DATA = "pickerData";
    private static final String SELECTED_VALUE = "selectedValue";
    private static final String IS_LOOP = "isLoop";
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

    private View view;
    private PopupWindow popupWindow = null;

    private boolean isLoop = true;

    private String confirmText;
    private String cancelText;
    private String titleText;

    private int[] pickerColor = new int[4];
    private int[] barBgColor = new int[4];
    private int[] confirmTextColor = new int[4];
    private int[] cancelTextColor = new int[4];
    private int[] titleTextColor = new int[4];

    private ArrayList<String> curSelectedList = new ArrayList<>();

    private RelativeLayout barLayout;
    private TextView cancelTV;
    private TextView titleTV;
    private TextView confirmTV;
    private PickerViewLinkage pickerViewLinkage;
    private PickerViewAlone pickerViewAlone;

    private int pickerViewHeight;
    private int barViewHeight;

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
            view = activity.getLayoutInflater().inflate(R.layout.popup_picker_view, null);
            barLayout = (RelativeLayout) view.findViewById(R.id.barLayout);
            cancelTV = (TextView) view.findViewById(R.id.cancel);
            titleTV = (TextView) view.findViewById(R.id.title);
            confirmTV = (TextView) view.findViewById(R.id.confirm);
            pickerViewLinkage = (PickerViewLinkage) view.findViewById(R.id.pickerViewLinkage);
            pickerViewAlone = (PickerViewAlone) view.findViewById(R.id.pickerViewAlone);

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
                for (int i = 0; i < array.size(); i++) {
                    if (i == 3) {
                        barBgColor[i] = (int) (array.getDouble(i) * 255);
                    } else {
                        barBgColor[i] = array.getInt(i);
                    }
                }
                barLayout.setBackgroundColor(Color.argb(barBgColor[3], barBgColor[0], barBgColor[1], barBgColor[2]));
            }


            if (options.hasKey(CONFIRM_TEXT)) {
                confirmText = options.getString(CONFIRM_TEXT);
            }
            confirmTV.setText(!TextUtils.isEmpty(confirmText) ? confirmText : "");

            if (options.hasKey(CONFIRM_TEXT_COLOR)) {
                ReadableArray array = options.getArray(CONFIRM_TEXT_COLOR);
                for (int i = 0; i < array.size(); i++) {
                    if (i == 3) {
                        confirmTextColor[i] = (int) (array.getDouble(i) * 255);
                    } else {
                        confirmTextColor[i] = array.getInt(i);
                    }
                }
                confirmTV.setTextColor(Color.argb(confirmTextColor[3], confirmTextColor[0], confirmTextColor[1], confirmTextColor[2]));
            }
            confirmTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
                for (int i = 0; i < array.size(); i++) {
                    if (i == 3) {
                        titleTextColor[i] = (int) (array.getDouble(i) * 255);
                    } else {
                        titleTextColor[i] = array.getInt(i);
                    }
                }
                titleTV.setTextColor(Color.argb(titleTextColor[3], titleTextColor[0], titleTextColor[1], titleTextColor[2]));
            }

            if (options.hasKey(CANCEL_TEXT)) {
                cancelText = options.getString(CANCEL_TEXT);
            }
            cancelTV.setText(!TextUtils.isEmpty(cancelText) ? cancelText : "");
            if (options.hasKey(CANCEL_TEXT_COLOR)) {
                ReadableArray array = options.getArray(CANCEL_TEXT_COLOR);
                for (int i = 0; i < array.size(); i++) {
                    if (i == 3) {
                        cancelTextColor[i] = (int) (array.getDouble(i) * 255);
                    } else {
                        cancelTextColor[i] = array.getInt(i);
                    }
                }
                cancelTV.setTextColor(Color.argb(cancelTextColor[3], cancelTextColor[0], cancelTextColor[1], cancelTextColor[2]));
            }
            cancelTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    commonEvent(EVENT_KEY_CANCEL);
                    hide();
                }
            });

            if (options.hasKey(IS_LOOP)) {
                isLoop = options.getBoolean(IS_LOOP);
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

            if (options.hasKey(PICKER_BG_COLOR)) {
                ReadableArray array = options.getArray(PICKER_BG_COLOR);
                for (int i = 0; i < array.size(); i++) {
                    if (i == 3) {
                        pickerColor[i] = (int) (array.getDouble(i) * 255);
                    } else {
                        pickerColor[i] = array.getInt(i);
                    }
                }
            }

            ReadableArray pickerData = options.getArray(PICKER_DATA);

            String name = pickerData.getType(0).name();
            switch (name) {
                case "Map":
                    pickerViewLinkage.setVisibility(View.VISIBLE);
                    pickerViewAlone.setVisibility(View.GONE);
                    pickerViewLinkage.setPickerData(pickerData, curSelectedList);
                    pickerViewLinkage.setIsLoop(isLoop);
                    if (options.hasKey(PICKER_BG_COLOR)) {
                        pickerViewLinkage.setBackgroundColor(Color.argb(pickerColor[3], pickerColor[0], pickerColor[1], pickerColor[2]));
                    }
                    pickerViewLinkage.setOnSelectListener(new OnSelectedListener() {
                        @Override
                        public void onSelected(ArrayList<String> selectedList) {
                            curSelectedList = selectedList;
                            commonEvent(EVENT_KEY_SELECTED);
                        }
                    });
                    pickerViewLinkage.setSelectValue(selectValue, curSelectedList);
                    pickerViewHeight = pickerViewLinkage.getViewHeight();
                    break;
                default:
                    pickerViewAlone.setVisibility(View.VISIBLE);
                    pickerViewLinkage.setVisibility(View.GONE);

                    pickerViewAlone.setPickerData(pickerData, curSelectedList);
                    pickerViewAlone.setIsLoop(isLoop);
                    if (options.hasKey(PICKER_BG_COLOR)) {
                        pickerViewAlone.setBackgroundColor(Color.argb(pickerColor[3], pickerColor[0], pickerColor[1], pickerColor[2]));
                    }

                    pickerViewAlone.setOnSelectedListener(new OnSelectedListener() {
                        @Override
                        public void onSelected(ArrayList<String> selectedList) {
                            curSelectedList = selectedList;
                            commonEvent(EVENT_KEY_SELECTED);
                        }
                    });

                    pickerViewAlone.setSelectValue(selectValue, curSelectedList);
                    pickerViewHeight = pickerViewAlone.getViewHeight();
                    break;
            }


            if (popupWindow == null) {
                int height = barViewHeight + pickerViewHeight;
                popupWindow = new PopupWindow(WindowManager.LayoutParams.MATCH_PARENT, height);
                popupWindow.setBackgroundDrawable(new ColorDrawable());
                popupWindow.setAnimationStyle(R.style.PopAnim);
            }
            popupWindow.setContentView(view);
            popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        }
    }

    @ReactMethod
    public void initOK(Callback callback) {
        callback.invoke(popupWindow != null);
    }

    @ReactMethod
    public void toggle() {
        if (popupWindow == null)
            return;
        if (popupWindow.isShowing()) {
            hide();
        } else {
            show();
        }
    }

    @ReactMethod
    public void show() {
        if (popupWindow != null) {
            popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        }
    }

    @ReactMethod
    public void hide() {
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
    }

    @ReactMethod
    public void isPickerShow(Callback callback) {
        if (popupWindow == null) {
            callback.invoke(ERROR_NOT_INIT);
        } else {
            callback.invoke(null, popupWindow.isShowing());
        }
    }

    private void commonEvent(String eventKey) {
        WritableMap map = Arguments.createMap();
        WritableArray array = Arguments.createArray();
        for (String item : curSelectedList) {
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
}
