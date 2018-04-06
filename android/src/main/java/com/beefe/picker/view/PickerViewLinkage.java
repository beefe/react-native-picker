package com.beefe.picker.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.beefe.picker.R;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by <a href="https://github.com/shexiaoheng">heng</a> on 2016/09/01
 *
 * Edited by heng on 2016/12/26
 * 1. Fixed returnData bug
 * 2. Added LoopView TextColor and TextSize support
 */

public class PickerViewLinkage extends LinearLayout {

    private LoopView loopViewOne;
    private LoopView loopViewTwo;
    private LoopView loopViewThree;

    private OnSelectedListener onSelectedListener;

    private int curRow;

    public PickerViewLinkage(Context context) {
        super(context);
        init(context);
    }

    public PickerViewLinkage(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PickerViewLinkage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.picker_view_linkage, this);
        loopViewOne = (LoopView) view.findViewById(R.id.loopViewOne);
        loopViewTwo = (LoopView) view.findViewById(R.id.loopViewTwo);
        loopViewThree = (LoopView) view.findViewById(R.id.loopViewThree);
    }

    private void setRow(int row) {
        switch (row) {
            case 2:
                curRow = 2;
                loopViewTwo.setVisibility(VISIBLE);
                loopViewOne.setVisibility(VISIBLE);
                loopViewThree.setVisibility(GONE);
                break;
            case 3:
                curRow = 3;
                loopViewOne.setVisibility(VISIBLE);
                loopViewTwo.setVisibility(VISIBLE);
                loopViewThree.setVisibility(VISIBLE);
                break;
            default:
                break;
        }
    }

    private ArrayList<String> oneList = new ArrayList<>();
    private ArrayList<String> twoList = new ArrayList<>();
    private ArrayList<String> threeList = new ArrayList<>();

    private ArrayList<ReadableMap> data = new ArrayList<>();
    private int selectOneIndex;
    private int selectTwoIndex;

    private ArrayList<ReturnData> curSelectedList;

    private ReturnData returnData;
    private ReturnData returnData1;
    private ReturnData returnData2;

    private void checkItems(LoopView loopView, ArrayList<String> list) {
        if (list != null && list.size() > 0) {
            loopView.setItems(list);
            loopView.setSelectedPosition(0);
        }
    }

    private void setWeights(double[] weights) {
        LayoutParams paramsOne = new LayoutParams(0, LayoutParams.MATCH_PARENT);
        LayoutParams paramsTwo = new LayoutParams(0, LayoutParams.MATCH_PARENT);
        LayoutParams paramsThree = new LayoutParams(0, LayoutParams.MATCH_PARENT);

        switch (curRow) {
            case 2:
                switch (weights.length) {
                    case 1:
                        paramsOne.weight = (float) weights[0];
                        paramsTwo.weight = 1.0f;
                        break;
                    default:
                        paramsOne.weight = (float) weights[0];
                        paramsTwo.weight = (float) weights[1];
                        break;
                }
                loopViewOne.setLayoutParams(paramsOne);
                loopViewTwo.setLayoutParams(paramsTwo);
                break;
            case 3:
                switch (weights.length) {
                    case 1:
                        paramsOne.weight = (float) weights[0];
                        paramsTwo.weight = 1.0f;
                        paramsThree.weight = 1.0f;
                        break;
                    case 2:
                        paramsOne.weight = (float) weights[0];
                        paramsTwo.weight = (float) weights[1];
                        paramsThree.weight = 1.0f;
                        break;
                    default:
                        paramsOne.weight = (float) weights[0];
                        paramsTwo.weight = (float) weights[1];
                        paramsThree.weight = (float) weights[2];
                        break;
                }
                loopViewOne.setLayoutParams(paramsOne);
                loopViewTwo.setLayoutParams(paramsTwo);
                loopViewThree.setLayoutParams(paramsThree);
                break;
        }
    }

    /**
     * ReadableArray getMap will remove the item.
     * <a href="https://github.com/facebook/react-native/issues/8557"></a>
     */
    public void setPickerData(ReadableArray array, double[] weights) {
        curSelectedList = new ArrayList<>();
        returnData = new ReturnData();
        returnData1 = new ReturnData();
        returnData2 = new ReturnData();
        oneList.clear();
        for (int i = 0; i < array.size(); i++) {
            ReadableMap map = array.getMap(i);
            data.add(map);
            ReadableMapKeySetIterator iterator = map.keySetIterator();
            if (iterator.hasNextKey()) {
                String oneValue = iterator.nextKey();
                oneList.add(oneValue);
            }
        }
        checkItems(loopViewOne, oneList);

        returnData.setItem(oneList.get(0));
        returnData.setIndex(loopViewOne.getSelectedIndex());
        if (curSelectedList.size() > 0) {
            curSelectedList.set(0, returnData);
        } else {
            curSelectedList.add(0, returnData);
        }

        ReadableArray childArray = data.get(0).getArray(oneList.get(0));
        String name = childArray.getType(0).name();
        if (name.equals("Map")) {
            setRow(3);

            twoList.clear();
            getTwoListData();
            checkItems(loopViewTwo, twoList);
            returnData1.setItem(twoList.get(0));
            returnData1.setIndex(loopViewTwo.getSelectedIndex());
            if (curSelectedList.size() > 1) {
                curSelectedList.set(1, returnData1);
            } else {
                curSelectedList.add(1, returnData1);
            }

            ReadableMap childMap = data.get(0).getArray(oneList.get(0)).getMap(0);
            String key = childMap.keySetIterator().nextKey();
            ReadableArray sunArray = childMap.getArray(key);
            threeList.clear();
            threeList = arrayToList(sunArray);
            checkItems(loopViewThree, threeList);

            if(threeList!=null&&threeList.size()>0){
                returnData2.setItem(threeList.get(0));
                returnData2.setIndex(loopViewThree.getSelectedIndex());
                if (curSelectedList.size() > 2) {
                    curSelectedList.set(2, returnData2);
                } else {
                    curSelectedList.add(2, returnData2);
                }
            }

            loopViewOne.setListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(String item, int index) {
                    selectOneIndex = index;
                    returnData = new ReturnData();
                    returnData.setIndex(index);
                    returnData.setItem(item);
                    curSelectedList.set(0, returnData);
                    twoList.clear();
                    ReadableArray arr = data.get(index).getArray(item);
                    for (int i = 0; i < arr.size(); i++) {
                        ReadableMap map = arr.getMap(i);
                        ReadableMapKeySetIterator ite = map.keySetIterator();
                        if (ite.hasNextKey()) {
                            twoList.add(ite.nextKey());
                        }
                    }
                    checkItems(loopViewTwo, twoList);
                    returnData1 = new ReturnData();
                    returnData1.setItem(twoList.get(0));
                    returnData1.setIndex(loopViewTwo.getSelectedIndex());
                    curSelectedList.set(1, returnData1);


                    ReadableArray ar = data.get(index).getArray(item);
                    ReadableMap childMap = ar.getMap(0);
                    String key = childMap.keySetIterator().nextKey();
                    ReadableArray sunArray = childMap.getArray(key);
                    threeList.clear();
                    threeList = arrayToList(sunArray);
                    checkItems(loopViewThree, threeList);
                    returnData2 = new ReturnData();

                    if (threeList!=null&&threeList.size()>0){
                        returnData2.setItem(threeList.get(0));
                        returnData2.setIndex(loopViewThree.getSelectedIndex());
                        curSelectedList.set(2, returnData2);
                        if (onSelectedListener != null) {
                            onSelectedListener.onSelected(curSelectedList);
                        }
                    }

                }
            });

            loopViewTwo.setListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(String item, int index) {
                    selectTwoIndex = index;

                    ReadableArray arr = data.get(selectOneIndex).getArray(oneList.get(selectOneIndex));
                    int arrSize = arr.size();
                    //fix IndexOutOfBoundsException
                    //by zooble @2018-1-10
                    if(index > arrSize){
                        index = arrSize - 1;
                    }
                    ReadableMap childMap = arr.getMap(index);
                    String key = childMap.keySetIterator().nextKey();
                    ReadableArray sunArray = childMap.getArray(key);
                    threeList.clear();
                    threeList = arrayToList(sunArray);
                    checkItems(loopViewThree, threeList);

                    returnData = new ReturnData();
                    returnData.setItem(oneList.get(selectOneIndex));
                    returnData.setIndex(loopViewOne.getSelectedIndex());
                    curSelectedList.set(0, returnData);

                    returnData1 = new ReturnData();
                    returnData1.setItem(item);
                    returnData1.setIndex(index);
                    curSelectedList.set(1, returnData1);

                    returnData2 = new ReturnData();

                    if (threeList!=null&&threeList.size()>0){
                        returnData2.setItem(threeList.get(0));
                        returnData2.setIndex(loopViewThree.getSelectedIndex());
                        curSelectedList.set(2, returnData2);
                        if (onSelectedListener != null) {
                            onSelectedListener.onSelected(curSelectedList);
                        }

                    }


                }
            });

            loopViewThree.setListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(String item, int index) {
                    //fix IndexOutOfBoundsException
                    //by zooble @2018-1-10
                    int arrOneSize = oneList.size();
                    if(selectOneIndex >= arrOneSize){
                        selectOneIndex = arrOneSize - 1;
                    }
                    int arrTwoSize = twoList.size();
                    if(selectTwoIndex >= arrTwoSize){
                        selectTwoIndex = arrTwoSize - 1;
                    }
                    
                    returnData = new ReturnData();
                    returnData.setItem(oneList.get(selectOneIndex));
                    returnData.setIndex(loopViewOne.getSelectedIndex());
                    curSelectedList.set(0, returnData);

                    returnData1 = new ReturnData();
                    returnData1.setItem(twoList.get(selectTwoIndex));
                    returnData1.setIndex(loopViewTwo.getSelectedIndex());
                    curSelectedList.set(1, returnData1);

                    returnData2 = new ReturnData();
                    returnData2.setItem(item);
                    returnData2.setIndex(index);
                    curSelectedList.set(2, returnData2);
                    if (onSelectedListener != null) {
                        onSelectedListener.onSelected(curSelectedList);
                    }
                }
            });
        } else {
            setRow(2);
            loopViewOne.setListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(String item, int index) {
                    selectOneIndex = index;
                    ReadableArray arr = data.get(index).getArray(item);
                    twoList.clear();
                    twoList = arrayToList(arr);
                    checkItems(loopViewTwo, twoList);
                    returnData = new ReturnData();
                    returnData.setItem(item);
                    returnData.setIndex(index);
                    curSelectedList.set(0, returnData);
                    returnData1 = new ReturnData();
                    returnData1.setItem(twoList.get(0));
                    returnData1.setIndex(loopViewTwo.getSelectedIndex());
                    curSelectedList.set(1, returnData1);
                    if (onSelectedListener != null) {
                        onSelectedListener.onSelected(curSelectedList);
                    }
                }
            });

            twoList.clear();
            twoList = arrayToList(childArray);
            checkItems(loopViewTwo, twoList);
            returnData1 = new ReturnData();
            returnData1.setItem(twoList.get(0));
            returnData1.setIndex(loopViewTwo.getSelectedIndex());
            if (curSelectedList.size() > 1) {
                curSelectedList.set(1, returnData1);
            } else {
                curSelectedList.add(1, returnData1);
            }
            loopViewTwo.setListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(String item, int index) {
                    returnData = new ReturnData();
                    returnData.setItem(oneList.get(selectOneIndex));
                    returnData.setIndex(loopViewOne.getSelectedIndex());
                    curSelectedList.set(0, returnData);

                    returnData1 = new ReturnData();
                    returnData1.setIndex(index);
                    returnData1.setItem(item);
                    curSelectedList.set(1, returnData1);
                    if (onSelectedListener != null) {
                        onSelectedListener.onSelected(curSelectedList);
                    }
                }
            });
        }
        if (weights != null) {
            setWeights(weights);
        }
    }

    private ArrayList<String> arrayToList(ReadableArray array) {
        try {
            ArrayList<String> list = new ArrayList<>();
            for (int i = 0; i < array.size(); i++) {
                String values = "";
                switch (array.getType(i).name()) {
                    case "Boolean":
                        values = String.valueOf(array.getBoolean(i));
                        break;
                    case "Number":
                        try {
                            values = String.valueOf(array.getInt(i));
                        } catch (Exception e) {
                            values = String.valueOf(array.getDouble(i));
                        }
                        break;
                    case "String":
                        values = array.getString(i);
                        break;
                }
                list.add(values);
            }
            return list;
        } catch (Exception e) {
            return null;
        }
    }

    public void setSelectValue(String[] selectValue) {
        if (curRow <= selectValue.length) {
            String[] values = Arrays.copyOf(selectValue, curRow);
            selectValues(values, curSelectedList);
        } else {
            switch (selectValue.length) {
                case 1:
                    selectOneLoop(selectValue, curSelectedList);
                    switch (curRow) {
                        case 3:
                            twoList.clear();
                            getTwoListData();
                            loopViewTwo.setItems(twoList);
                            loopViewTwo.setSelectedPosition(0);
                            returnData1 = new ReturnData();
                            returnData1.setItem(loopViewTwo.getIndexItem(0));
                            returnData1.setIndex(loopViewTwo.getSelectedIndex());
                            curSelectedList.set(1, returnData1);

                            threeList.clear();
                            getThreeListData();
                            loopViewThree.setItems(threeList);
                            loopViewThree.setSelectedPosition(0);
                            returnData2 = new ReturnData();
                            returnData2.setItem(loopViewThree.getIndexItem(0));
                            returnData2.setIndex(loopViewThree.getSelectedIndex());
                            curSelectedList.set(2, returnData2);

                            break;
                        case 2:
                            twoList.clear();
                            getAllTwoListData();
                            loopViewTwo.setItems(twoList);
                            loopViewTwo.setSelectedPosition(0);
                            returnData1 = new ReturnData();
                            returnData1.setItem(loopViewTwo.getIndexItem(0));
                            returnData1.setIndex(loopViewTwo.getSelectedIndex());
                            curSelectedList.set(1, returnData1);
                            break;
                    }
                    break;
                case 2:
                    switch (curRow) {
                        case 3:
                            selectOneLoop(selectValue, curSelectedList);

                            twoList.clear();
                            getTwoListData();
                            selectTwoLoop(selectValue, curSelectedList);


                            threeList.clear();
                            getThreeListData();
                            loopViewThree.setItems(threeList);
                            loopViewThree.setSelectedPosition(0);
                            returnData2 = new ReturnData();
                            returnData2.setItem(loopViewThree.getIndexItem(0));
                            returnData2.setIndex(loopViewThree.getSelectedIndex());
                            curSelectedList.set(2, returnData2);
                            break;
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void selectValues(String[] values, final ArrayList<ReturnData> curSelectedList) {
        switch (values.length) {
            case 3:
                selectOneLoop(values, curSelectedList);

                twoList.clear();
                getTwoListData();
                selectTwoLoop(values, curSelectedList);

                threeList.clear();
                getThreeListData();
                selectThreeLoop(values, curSelectedList);
                break;
            case 2:
                selectOneLoop(values, curSelectedList);

                twoList.clear();
                getAllTwoListData();
                selectTwoLoop(values, curSelectedList);
                break;
            default:
                break;
        }
    }

    /**
     * 设置第一个滚轮选中的值
     */
    private void selectOneLoop(String[] values, final ArrayList<ReturnData> curSelectedList) {
        if (loopViewOne.hasItem(values[0])) {
            selectOneIndex = loopViewOne.getItemPosition(values[0]);
        } else {
            selectOneIndex = 0;
        }
        loopViewOne.setSelectedPosition(selectOneIndex);

        returnData = new ReturnData();
        returnData.setItem(loopViewOne.getIndexItem(selectOneIndex));
        returnData.setIndex(loopViewOne.getSelectedIndex());
        curSelectedList.set(0, returnData);
    }

    /**
     * 设置第二个滚轮选中的值
     */
    private void selectTwoLoop(String[] values, final ArrayList<ReturnData> curSelectedList) {
        loopViewTwo.setItems(twoList);
        if (loopViewTwo.hasItem(values[1])) {
            selectTwoIndex = loopViewTwo.getItemPosition(values[1]);
        } else {
            selectTwoIndex = 0;
        }
        returnData1 = new ReturnData();
        loopViewTwo.setSelectedPosition(selectTwoIndex);
        returnData1.setItem(loopViewTwo.getIndexItem(selectTwoIndex));
        returnData1.setIndex(loopViewTwo.getSelectedIndex());
        curSelectedList.set(1, returnData1);
    }

    /**
     * 设置第三个滚轮选中的值
     */
    private void selectThreeLoop(String[] values, final ArrayList<ReturnData> curSelectedList) {
        loopViewThree.setItems(threeList);
        int selectThreeIndex;
        if (loopViewThree.hasItem(values[2])) {
            selectThreeIndex = loopViewThree.getItemPosition(values[2]);
        } else {
            selectThreeIndex = 0;
        }
        returnData2 = new ReturnData();
        loopViewThree.setSelectedPosition(selectThreeIndex);
        returnData2.setItem(loopViewThree.getIndexItem(selectThreeIndex));
        returnData2.setIndex(loopViewThree.getSelectedIndex());
        curSelectedList.set(2, returnData2);
    }

    /**
     * 只有两个滚轮
     * 获取第二个滚轮的值
     */
    private void getAllTwoListData() {
        ReadableArray arr = data.get(selectOneIndex).getArray(oneList.get(selectOneIndex));
        twoList = arrayToList(arr);
    }

    /**
     * 有三个滚轮
     * 获取第二个滚轮的值
     */
    private void getTwoListData() {
        ReadableArray childArray = data.get(selectOneIndex).getArray(oneList.get(selectOneIndex));
        for (int i = 0; i < childArray.size(); i++) {
            ReadableMap map = childArray.getMap(i);
            ReadableMapKeySetIterator iterator = map.keySetIterator();
            if (iterator.hasNextKey()) {
                twoList.add(iterator.nextKey());
            }
        }
    }

    /**
     * 获取第三个滚轮的值
     */
    private void getThreeListData() {
        ReadableMap childMap = data.get(selectOneIndex).getArray(oneList.get(selectOneIndex)).getMap(selectTwoIndex);
        String key = childMap.keySetIterator().nextKey();
        ReadableArray sunArray = childMap.getArray(key);
        threeList = arrayToList(sunArray);
    }

    public void setTextSize(float size){
        switch (curRow) {
            case 2:
                loopViewOne.setTextSize(size);
                loopViewTwo.setTextSize(size);
                break;
            case 3:
                loopViewOne.setTextSize(size);
                loopViewTwo.setTextSize(size);
                loopViewThree.setTextSize(size);
                break;
        }
    }

    public void setTypeface(Typeface typeface){
        switch (curRow) {
            case 2:
                loopViewOne.setTypeface(typeface);
                loopViewTwo.setTypeface(typeface);
                break;
            case 3:
                loopViewOne.setTypeface(typeface);
                loopViewTwo.setTypeface(typeface);
                loopViewThree.setTypeface(typeface);
                break;
        }
    }

    public void setTextEllipsisLen(int len){
        switch (curRow) {
            case 2:
                loopViewOne.setTextEllipsisLen(len);
                loopViewTwo.setTextEllipsisLen(len);
                break;
            case 3:
                loopViewOne.setTextEllipsisLen(len);
                loopViewTwo.setTextEllipsisLen(len);
                loopViewThree.setTextEllipsisLen(len);
                break;
        }
    }

    public void setTextColor(int color){
        switch (curRow) {
            case 2:
                loopViewOne.setTextColor(color);
                loopViewTwo.setTextColor(color);
                break;
            case 3:
                loopViewOne.setTextColor(color);
                loopViewTwo.setTextColor(color);
                loopViewThree.setTextColor(color);
                break;
        }
    }

    public void setIsLoop(boolean isLoop) {
        if (!isLoop) {
            switch (curRow) {
                case 2:
                    loopViewOne.setNotLoop();
                    loopViewTwo.setNotLoop();
                    break;
                case 3:
                    loopViewOne.setNotLoop();
                    loopViewTwo.setNotLoop();
                    loopViewThree.setNotLoop();
                    break;
            }
        }
    }

    public int getViewHeight() {
        return loopViewOne.getViewHeight();
    }

    public ArrayList<ReturnData> getSelectedData() {
        return this.curSelectedList;
    }

    public void setOnSelectListener(OnSelectedListener listener) {
        this.onSelectedListener = listener;
    }
}