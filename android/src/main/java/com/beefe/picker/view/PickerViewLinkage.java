package com.beefe.picker.view;

import android.content.Context;
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
 * Created by heng on 16/9/1.
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

    private ArrayList<String> curSelectedList;

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
        if (curSelectedList.size() > 0) {
            curSelectedList.set(0, oneList.get(0));
        } else {
            curSelectedList.add(0, oneList.get(0));
        }

        ReadableArray childArray = data.get(0).getArray(oneList.get(0));
        String name = childArray.getType(0).name();
        if (name.equals("Map")) {
            setRow(3);

            twoList.clear();
            getTwoListData();
            checkItems(loopViewTwo, twoList);
            if (curSelectedList.size() > 1) {
                curSelectedList.set(1, twoList.get(0));
            } else {
                curSelectedList.add(1, twoList.get(0));
            }

            ReadableMap childMap = data.get(0).getArray(oneList.get(0)).getMap(0);
            String key = childMap.keySetIterator().nextKey();
            ReadableArray sunArray = childMap.getArray(key);
            threeList.clear();
            threeList = arrayToList(sunArray);
            checkItems(loopViewThree, threeList);
            if (curSelectedList.size() > 2) {
                curSelectedList.set(2, threeList.get(0));
            } else {
                curSelectedList.add(2, threeList.get(0));
            }

            loopViewOne.setListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(String item, int index) {
                    selectOneIndex = index;
                    curSelectedList.set(0, item);
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
                    curSelectedList.set(1, twoList.get(0));


                    ReadableArray ar = data.get(index).getArray(item);
                    ReadableMap childMap = ar.getMap(0);
                    String key = childMap.keySetIterator().nextKey();
                    ReadableArray sunArray = childMap.getArray(key);
                    threeList.clear();
                    threeList = arrayToList(sunArray);
                    checkItems(loopViewThree, threeList);
                    curSelectedList.set(2, threeList.get(0));

                    if (onSelectedListener != null) {
                        onSelectedListener.onSelected(curSelectedList);
                    }
                }
            });

            loopViewTwo.setListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(String item, int index) {
                    selectTwoIndex = index;

                    ReadableArray arr = data.get(selectOneIndex).getArray(oneList.get(selectOneIndex));
                    ReadableMap childMap = arr.getMap(index);
                    String key = childMap.keySetIterator().nextKey();
                    ReadableArray sunArray = childMap.getArray(key);
                    threeList.clear();
                    threeList = arrayToList(sunArray);
                    checkItems(loopViewThree, threeList);

                    curSelectedList.set(0, oneList.get(selectOneIndex));
                    curSelectedList.set(1, item);
                    curSelectedList.set(2, threeList.get(0));
                    if (onSelectedListener != null) {
                        onSelectedListener.onSelected(curSelectedList);
                    }
                }
            });

            loopViewThree.setListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(String item, int index) {
                    curSelectedList.set(0, oneList.get(selectOneIndex));
                    curSelectedList.set(1, twoList.get(selectTwoIndex));
                    curSelectedList.set(2, item);
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
                    curSelectedList.set(0, item);
                    curSelectedList.set(1, twoList.get(0));
                    if (onSelectedListener != null) {
                        onSelectedListener.onSelected(curSelectedList);
                    }
                }
            });

            twoList.clear();
            twoList = arrayToList(childArray);
            checkItems(loopViewTwo, twoList);
            if (curSelectedList.size() > 1) {
                curSelectedList.set(1, twoList.get(0));
            } else {
                curSelectedList.add(1, twoList.get(0));
            }
            loopViewTwo.setListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(String item, int index) {
                    curSelectedList.set(0, oneList.get(selectOneIndex));
                    curSelectedList.set(1, item);
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
                            curSelectedList.set(1, loopViewTwo.getIndexItem(0));

                            threeList.clear();
                            getThreeListData();
                            loopViewThree.setItems(threeList);
                            loopViewThree.setSelectedPosition(0);
                            curSelectedList.set(2, loopViewThree.getIndexItem(0));

                            break;
                        case 2:
                            twoList.clear();
                            getAllTwoListData();
                            loopViewTwo.setItems(twoList);
                            loopViewTwo.setSelectedPosition(0);
                            curSelectedList.set(1, loopViewTwo.getIndexItem(0));
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
                            curSelectedList.set(2, loopViewThree.getIndexItem(0));
                            break;
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void selectValues(String[] values, final ArrayList<String> curSelectedList) {
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
    private void selectOneLoop(String[] values, final ArrayList<String> curSelectedList) {
        if (loopViewOne.hasItem(values[0])) {
            selectOneIndex = loopViewOne.getItemPosition(values[0]);
            loopViewOne.setSelectedPosition(selectOneIndex);
            curSelectedList.set(0, loopViewOne.getIndexItem(selectOneIndex));
        } else {
            selectOneIndex = 0;
            loopViewOne.setSelectedPosition(0);
            curSelectedList.set(0, loopViewOne.getIndexItem(0));
        }
    }

    /**
     * 设置第二个滚轮选中的值
     */
    private void selectTwoLoop(String[] values, final ArrayList<String> curSelectedList) {
        loopViewTwo.setItems(twoList);
        if (loopViewTwo.hasItem(values[1])) {
            selectTwoIndex = loopViewTwo.getItemPosition(values[1]);
            loopViewTwo.setSelectedPosition(selectTwoIndex);
            curSelectedList.set(1, loopViewTwo.getIndexItem(selectTwoIndex));
        } else {
            selectTwoIndex = 0;
            loopViewTwo.setSelectedPosition(0);
            curSelectedList.set(1, loopViewTwo.getIndexItem(0));
        }
    }

    /**
     * 设置第三个滚轮选中的值
     */
    private void selectThreeLoop(String[] values, final ArrayList<String> curSelectedList) {
        loopViewThree.setItems(threeList);
        if (loopViewThree.hasItem(values[2])) {
            int selectThreeIndex = loopViewThree.getItemPosition(values[2]);
            loopViewThree.setSelectedPosition(selectThreeIndex);
            curSelectedList.set(2, loopViewThree.getIndexItem(selectThreeIndex));
        } else {
            loopViewThree.setSelectedPosition(0);
            curSelectedList.set(2, loopViewThree.getIndexItem(0));
        }
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

    public ArrayList<String> getSelectedData(){
        return this.curSelectedList;
    }

    public void setOnSelectListener(OnSelectedListener listener) {
        this.onSelectedListener = listener;
    }

}
