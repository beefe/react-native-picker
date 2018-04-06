package com.beefe.picker.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.beefe.picker.R;
import com.facebook.react.bridge.ReadableArray;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by <a href="https://github.com/shexiaoheng">heng</a> on 16/9/6.
 * <p>
 * Edited by heng on 16/10/09:
 * 修复滚动后返回值错误的bug
 *
 * Edited by heng on 2016/12/26
 * 1. Fixed returnData bug
 * 2. Added LoopView TextColor and TextSize support
 */

public class PickerViewAlone extends LinearLayout {

    private LinearLayout pickerViewAloneLayout;

    private OnSelectedListener onSelectedListener;

    private ArrayList<ReturnData> curSelectedList;

    public PickerViewAlone(Context context) {
        super(context);
        init(context);
    }

    public PickerViewAlone(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.picker_view_alone, this);
        pickerViewAloneLayout = (LinearLayout) view.findViewById(R.id.pickerViewAloneLayout);
    }

    public void setOnSelectedListener(OnSelectedListener listener) {
        this.onSelectedListener = listener;
    }

    public void setPickerData(ReadableArray array, double[] weights) {
        curSelectedList = new ArrayList<>();
        switch (array.getType(0).name()) {
            case "Array":
                setMultipleData(array, weights);
                break;
            default:
                setAloneData(array);
                break;
        }
    }

    public ArrayList<ReturnData> getSelectedData() {
        return this.curSelectedList;
    }

    private void setAloneData(ReadableArray array) {
        ArrayList<String> values = arrayToList(array);
        final LoopView loopView = new LoopView(getContext());
        LayoutParams params = new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        params.weight = 1.0f;
        loopView.setLayoutParams(params);
        loopView.setItems(values);
        loopView.setSelectedPosition(0);
        ReturnData returnData = new ReturnData();
        returnData.setItem(values.get(0));
        returnData.setIndex(loopView.getSelectedIndex());
        if (curSelectedList.size() > 0) {
            curSelectedList.set(0, returnData);
        } else {
            curSelectedList.add(0, returnData);
        }
        loopView.setListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(String item, int index) {
                if (onSelectedListener != null) {
                    ReturnData returnData1 = new ReturnData();
                    returnData1.setItem(item);
                    returnData1.setIndex(index);
                    curSelectedList.set(0, returnData1);
                    onSelectedListener.onSelected(curSelectedList);
                }
            }
        });
        pickerViewAloneLayout.addView(loopView);
    }

    private void setMultipleData(ReadableArray array, double[] weights) {
        final String[] selectedItems = new String[array.size()];
        final int[] selectedIndexes = new int[array.size()];
        for (int i = 0; i < array.size(); i++) {
            switch (array.getType(i).name()) {
                case "Array":
                    ReadableArray childArray = array.getArray(i);
                    ArrayList<String> values = arrayToList(childArray);
                    final LoopView loopView = new LoopView(getContext());
                    LayoutParams params = new LayoutParams(0, LayoutParams.MATCH_PARENT);
                    if (weights != null) {
                        if (i < weights.length) {
                            params.weight = (float) weights[i];
                        } else {
                            params.weight = 1.0f;
                        }
                    } else {
                        params.weight = 1.0f;
                    }
                    loopView.setLayoutParams(params);
                    loopView.setItems(values);
                    loopView.setTag(i);
                    loopView.setSelectedPosition(0);

                    ReturnData returnData = new ReturnData();
                    returnData.setItem(values.get(0));
                    returnData.setIndex(loopView.getSelectedIndex());
                    if (curSelectedList.size() > i) {
                        curSelectedList.set(i, returnData);
                    } else {
                        curSelectedList.add(i, returnData);
                    }
                    selectedItems[i] = values.get(0);
                    loopView.setListener(new OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(String item, int index) {
                            int viewCount = pickerViewAloneLayout.getChildCount();
                            for (int k = 0; k < viewCount; k++) {
                                View view = pickerViewAloneLayout.getChildAt(k);
                                if (view instanceof LoopView) {
                                    LoopView loop = (LoopView) view;
                                    selectedItems[k] = loop.getSelectedItem();
                                    selectedIndexes[k] = loop.getSelectedIndex();
                                }
                            }

                            if (onSelectedListener != null) {
                                for (int i = 0; i < selectedItems.length; i++) {
                                    ReturnData returnData1 = new ReturnData();
                                    returnData1.setItem(selectedItems[i]);
                                    returnData1.setIndex(selectedIndexes[i]);
                                    curSelectedList.set(i, returnData1);
                                }
                                onSelectedListener.onSelected(curSelectedList);
                            }
                        }
                    });
                    pickerViewAloneLayout.addView(loopView);
                    break;
                default:
                    break;
            }
        }
    }

    public void setSelectValue(String[] selectValue) {
        int viewCount = pickerViewAloneLayout.getChildCount();
        int valueCount = selectValue.length;
        if (valueCount <= viewCount) {
            setSelect(valueCount, selectValue, curSelectedList);
        } else {
            String[] values = Arrays.copyOf(selectValue, viewCount);
            setSelect(viewCount, values, curSelectedList);
        }
    }

    private void setSelect(int size, String[] values, ArrayList<ReturnData> curSelectedList) {
        for (int i = 0; i < size; i++) {
            View view = pickerViewAloneLayout.getChildAt(i);
            if (view instanceof LoopView) {
                LoopView loop = (LoopView) view;
                if (loop.hasItem(values[i])) {
                    loop.setSelectedItem(values[i]);
                    ReturnData returnData = new ReturnData();
                    returnData.setItem(values[i]);
                    returnData.setIndex(loop.getSelectedIndex());
                    curSelectedList.set(i, returnData);
                }
            }
        }
    }

    public void setTextColor(int color){
        int viewCount = pickerViewAloneLayout.getChildCount();
        for (int i = 0; i < viewCount; i++) {
            View view = pickerViewAloneLayout.getChildAt(i);
            if (view instanceof LoopView) {
                LoopView loopView = (LoopView) view;
                loopView.setTextColor(color);
            }
        }
    }

    public void setTextSize(float size){
        int viewCount = pickerViewAloneLayout.getChildCount();
        for (int i = 0; i < viewCount; i++) {
            View view = pickerViewAloneLayout.getChildAt(i);
            if (view instanceof LoopView) {
                LoopView loopView = (LoopView) view;
                loopView.setTextSize(size);
            }
        }
    }

    public void setTypeface(Typeface typeface){
        int viewCount = pickerViewAloneLayout.getChildCount();
        for (int i = 0; i < viewCount; i++) {
            View view = pickerViewAloneLayout.getChildAt(i);
            if (view instanceof LoopView) {
                LoopView loopView = (LoopView) view;
                loopView.setTypeface(typeface);
            }
        }
    }

    public void setTextEllipsisLen(int len){
        int viewCount = pickerViewAloneLayout.getChildCount();
        for (int i = 0; i < viewCount; i++) {
            View view = pickerViewAloneLayout.getChildAt(i);
            if (view instanceof LoopView) {
                LoopView loopView = (LoopView) view;
                loopView.setTextEllipsisLen(len);
            }
        }
    }

    public void setIsLoop(boolean isLoop) {
        if (!isLoop) {
            int viewCount = pickerViewAloneLayout.getChildCount();
            for (int i = 0; i < viewCount; i++) {
                View view = pickerViewAloneLayout.getChildAt(i);
                if (view instanceof LoopView) {
                    LoopView loopView = (LoopView) view;
                    loopView.setNotLoop();
                }
            }
        }
    }

    public int getViewHeight() {
        int viewHeight = 0;
        View view = pickerViewAloneLayout.getChildAt(0);
        if (view instanceof LoopView) {
            LoopView loopView = (LoopView) view;
            viewHeight = loopView.getViewHeight();
        }
        return viewHeight;
    }

    private ArrayList<String> arrayToList(ReadableArray array) {
        ArrayList<String> values = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            String value = "";
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
            values.add(value);
        }
        return values;
    }

}