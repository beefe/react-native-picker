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

    private ReadableArray array;
    private ReadableMap map;
    private ReadableMap childMap;

    private int selectOneIndex;
    private int selectTwoIndex;

    private void checkItems(LoopView loopView, ArrayList<String> list) {
        if (list != null && list.size() > 0) {
            loopView.setItems(list);
            loopView.setSelectedPosition(0);
        }
    }

    public void setLinkageData(final ReadableMap map, final ArrayList<String> curSelectedList) {
        this.map = map;
        ReadableMapKeySetIterator iterator = map.keySetIterator();
        while (iterator.hasNextKey()) {
            String key = iterator.nextKey();
            oneList.add(key);
        }
        checkItems(loopViewOne, oneList);
        if (curSelectedList.size() > 0) {
            curSelectedList.set(0, oneList.get(0));
        } else {
            curSelectedList.add(0, oneList.get(0));
        }
        String name = map.getType(oneList.get(0)).name();
        switch (name) {
            case "Map":
                setRow(3);
                childMap = map.getMap(oneList.get(0));
                ReadableMapKeySetIterator childIterator = childMap.keySetIterator();
                twoList.clear();
                while (childIterator.hasNextKey()) {
                    String key = childIterator.nextKey();
                    twoList.add(key);
                }
                checkItems(loopViewTwo, twoList);
                if (curSelectedList.size() > 1) {
                    curSelectedList.set(1, twoList.get(0));
                } else {
                    curSelectedList.add(1, twoList.get(0));
                }

                array = childMap.getArray(twoList.get(0));
                threeList.clear();
                threeList = arrayToList(array);
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

                        childMap = map.getMap(item);
                        ReadableMapKeySetIterator childIterator = childMap.keySetIterator();
                        twoList.clear();
                        while (childIterator.hasNextKey()) {
                            String key = childIterator.nextKey();
                            twoList.add(key);
                        }
                        checkItems(loopViewTwo, twoList);
                        curSelectedList.set(1, twoList.get(0));

                        array = childMap.getArray(twoList.get(0));
                        threeList.clear();
                        threeList = arrayToList(array);
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
                        array = childMap.getArray(item);
                        threeList.clear();
                        threeList = arrayToList(array);
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
                break;
            case "Array":
                setRow(2);
                loopViewOne.setListener(new OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(String item, int index) {
                        selectOneIndex = index;
                        array = map.getArray(item);
                        twoList = arrayToList(array);
                        checkItems(loopViewTwo, twoList);
                        curSelectedList.set(0, item);
                        curSelectedList.set(1, twoList.get(0));
                        if (onSelectedListener != null) {
                            onSelectedListener.onSelected(curSelectedList);
                        }
                    }
                });

                array = map.getArray(oneList.get(0));
                twoList = arrayToList(array);
                checkItems(loopViewTwo, twoList);
                curSelectedList.add(1, twoList.get(0));
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
                break;
            default:
                break;
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

    public void setSelectValue(String[] selectValue, final ArrayList<String> curSelectedList) {
        if (curRow <= selectValue.length) {
            String[] values = Arrays.copyOf(selectValue, curRow);
            selectValues(values, curSelectedList);
        } else {
            switch (selectValue.length) {
                case 1:
                    if (loopViewOne.hasItem(selectValue[0])) {
                        selectOneIndex = loopViewOne.getItemPosition(selectValue[0]);
                        loopViewOne.setSelectedPosition(selectOneIndex);
                        curSelectedList.set(0, loopViewOne.getIndexItem(selectOneIndex));
                    } else {
                        loopViewOne.setSelectedPosition(0);
                        curSelectedList.set(0, loopViewOne.getIndexItem(0));
                    }
                    switch (curRow) {
                        case 3:
                            childMap = map.getMap(oneList.get(selectOneIndex));
                            ReadableMapKeySetIterator childIterator = childMap.keySetIterator();
                            twoList.clear();
                            while (childIterator.hasNextKey()) {
                                String key = childIterator.nextKey();
                                twoList.add(key);
                            }

                            loopViewTwo.setItems(twoList);
                            loopViewTwo.setSelectedPosition(0);
                            curSelectedList.set(1, loopViewTwo.getIndexItem(0));

                            array = childMap.getArray(twoList.get(0));
                            threeList.clear();
                            threeList = arrayToList(array);
                            loopViewThree.setItems(threeList);
                            loopViewThree.setSelectedPosition(0);
                            curSelectedList.set(2, loopViewThree.getIndexItem(0));

                            break;
                        case 2:
                            array = map.getArray(oneList.get(selectOneIndex));
                            twoList = arrayToList(array);
                            loopViewTwo.setItems(twoList);
                            loopViewTwo.setSelectedPosition(0);
                            curSelectedList.set(1, loopViewTwo.getIndexItem(0));
                            break;
                    }
                    break;
                case 2:
                    switch (curRow) {
                        case 3:
                            if (loopViewOne.hasItem(selectValue[0])) {
                                selectOneIndex = loopViewOne.getItemPosition(selectValue[0]);
                                loopViewOne.setSelectedPosition(selectOneIndex);
                                curSelectedList.set(0, loopViewOne.getIndexItem(selectOneIndex));
                            } else {
                                loopViewOne.setSelectedPosition(0);
                                curSelectedList.set(0, loopViewOne.getIndexItem(0));
                            }

                            childMap = map.getMap(oneList.get(selectOneIndex));
                            ReadableMapKeySetIterator childIterator = childMap.keySetIterator();
                            twoList.clear();
                            while (childIterator.hasNextKey()) {
                                String key = childIterator.nextKey();
                                twoList.add(key);
                            }
                            loopViewTwo.setItems(twoList);
                            if (loopViewTwo.hasItem(selectValue[1])) {
                                selectTwoIndex = loopViewTwo.getItemPosition(selectValue[1]);
                                loopViewTwo.setSelectedPosition(selectTwoIndex);
                                curSelectedList.set(1, loopViewTwo.getIndexItem(selectTwoIndex));
                            } else {
                                loopViewTwo.setSelectedPosition(0);
                                curSelectedList.set(1, loopViewTwo.getIndexItem(0));
                            }

                            array = childMap.getArray(twoList.get(selectTwoIndex));
                            threeList.clear();
                            threeList = arrayToList(array);
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
                if (loopViewOne.hasItem(values[0])) {
                    selectOneIndex = loopViewOne.getItemPosition(values[0]);
                    loopViewOne.setSelectedPosition(selectOneIndex);
                    curSelectedList.set(0, loopViewOne.getIndexItem(selectOneIndex));
                } else {
                    loopViewOne.setSelectedPosition(0);
                    curSelectedList.set(0, loopViewOne.getIndexItem(0));
                }

                childMap = map.getMap(oneList.get(selectOneIndex));
                ReadableMapKeySetIterator childIterator = childMap.keySetIterator();
                twoList.clear();
                while (childIterator.hasNextKey()) {
                    String key = childIterator.nextKey();
                    twoList.add(key);
                }
                loopViewTwo.setItems(twoList);
                if (loopViewTwo.hasItem(values[1])) {
                    selectTwoIndex = loopViewTwo.getItemPosition(values[1]);
                    loopViewTwo.setSelectedPosition(selectTwoIndex);
                    curSelectedList.set(1, loopViewTwo.getIndexItem(selectTwoIndex));
                } else {
                    loopViewTwo.setSelectedPosition(0);
                    curSelectedList.set(1, loopViewTwo.getIndexItem(0));
                }

                array = childMap.getArray(twoList.get(selectTwoIndex));
                threeList.clear();
                threeList = arrayToList(array);
                loopViewThree.setItems(threeList);
                if (loopViewThree.hasItem(values[2])) {
                    int selectThreeIndex = loopViewThree.getItemPosition(values[2]);
                    loopViewThree.setSelectedPosition(selectThreeIndex);
                    curSelectedList.set(2, loopViewThree.getIndexItem(selectThreeIndex));
                } else {
                    loopViewThree.setSelectedPosition(0);
                    curSelectedList.set(2, loopViewThree.getIndexItem(0));
                }
                break;
            case 2:
                if (loopViewOne.hasItem(values[0])) {
                    selectOneIndex = loopViewOne.getItemPosition(values[0]);
                    loopViewOne.setSelectedPosition(selectOneIndex);
                    curSelectedList.set(0, loopViewOne.getIndexItem(selectOneIndex));
                } else {
                    loopViewOne.setSelectedPosition(0);
                    curSelectedList.set(0, loopViewOne.getIndexItem(0));
                }

                array = map.getArray(oneList.get(selectOneIndex));
                twoList = arrayToList(array);
                loopViewTwo.setItems(twoList);
                if (loopViewTwo.hasItem(values[1])) {
                    selectTwoIndex = loopViewTwo.getItemPosition(values[1]);
                    loopViewTwo.setSelectedPosition(selectTwoIndex);
                    curSelectedList.set(1, loopViewTwo.getIndexItem(selectTwoIndex));
                } else {
                    loopViewTwo.setSelectedPosition(0);
                    curSelectedList.set(1, loopViewTwo.getIndexItem(0));
                }
                break;
            default:
                break;
        }
    }


    public void setIsLoop(boolean isLoop) {
        if (!isLoop) {
            loopViewOne.setNotLoop();
            loopViewTwo.setNotLoop();
            loopViewThree.setNotLoop();
        }
    }

    public void setOnSelectListener(OnSelectedListener listener) {
        this.onSelectedListener = listener;
    }

}
