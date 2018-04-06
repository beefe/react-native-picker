package com.beefe.picker.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Edited by <a href="https://github.com/shexiaoheng">heng</a> on 2016/10/20
 * 1. Added method getY
 * 2. Changed line color 0xffc5c5c5 -> 0xffb8bbc2
 *
 * Edited by heng on 2016/12/26
 * 1. Added setTextColor
 * 2. Added setTextSize
 */
public class LoopView extends View {

    private float scaleX = 1.05F;

    enum ACTION {
        // 点击，滑翔(滑到尽头)，拖拽事件
        CLICK, FLING, DRAG
    }

    private Context context;

    Handler handler;
    private GestureDetector gestureDetector;
    OnItemSelectedListener onItemSelectedListener;

    private ScheduledExecutorService mExecutor = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> mFuture;

    private Paint paintOuterText;
    private Paint paintCenterText;
    private Paint paintIndicator;

    List<String> items;

    private int textSize;
    int maxTextHeight;

    // 条目间距倍数
    float lineSpacingMultiplier;
    boolean isLoop;

    // 第一条线Y坐标值
    private int firstLineY;
    private int secondLineY;

    int totalScrollY;
    int initPosition;
    private String selectedItem;
    private int selectedIndex;
    private int preCurrentIndex;
    private int textEllipsisLen = 7;


    // 显示几个条目
    private int itemsVisible;

    private int measuredHeight;

    // 半圆周长
    private int halfCircumference;
    // 半径
    private int radius;

    private int mOffset = 0;
    private float previousY;
    private long startTime = 0;

    private Rect tempRect = new Rect();

    public LoopView(Context context) {
        super(context);
        initLoopView(context);
    }

    public LoopView(Context context, AttributeSet attributeset) {
        super(context, attributeset);
        initLoopView(context);
    }

    public LoopView(Context context, AttributeSet attributeset, int defStyleAttr) {
        super(context, attributeset, defStyleAttr);
        initLoopView(context);
    }

    private void initLoopView(Context context) {
        this.context = context;
        handler = new MessageHandler(this);
        gestureDetector = new GestureDetector(context, new LoopViewGestureListener(this));
        gestureDetector.setIsLongpressEnabled(false);

        lineSpacingMultiplier = 2.0F;
        isLoop = true;
        itemsVisible = 9;
        textSize = (int) (context.getResources().getDisplayMetrics().density * 16);

        totalScrollY = 0;
        initPosition = -1;

        initPaints();
    }

    private void initPaints() {
        paintOuterText = new Paint();
        paintOuterText.setColor(0xffafafaf);
        paintOuterText.setAntiAlias(true);
        paintOuterText.setTypeface(Typeface.MONOSPACE);
        paintOuterText.setTextSize(textSize);

        paintCenterText = new Paint();
        paintCenterText.setColor(0xff000000);
        paintCenterText.setAntiAlias(true);
        paintCenterText.setTextScaleX(scaleX);
        paintCenterText.setTypeface(Typeface.MONOSPACE);
        paintCenterText.setTextSize(textSize);

        paintIndicator = new Paint();
        paintIndicator.setColor(0xffb8bbc2);
        paintIndicator.setAntiAlias(true);

        if (android.os.Build.VERSION.SDK_INT >= 11) {
            setLayerType(LAYER_TYPE_SOFTWARE, null);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        remeasure();
        setMeasuredDimension(widthMeasureSpec, measuredHeight);
    }

    private void remeasure() {
        if (items == null) {
            return;
        }
        maxTextHeight = textSize;

        halfCircumference = (int) (maxTextHeight * lineSpacingMultiplier * (itemsVisible - 1));
        measuredHeight = (int) ((halfCircumference * 2) / Math.PI);
        radius = (int) (halfCircumference / Math.PI);
        firstLineY = (int) ((measuredHeight - lineSpacingMultiplier * maxTextHeight) / 2.0F);
        secondLineY = (int) ((measuredHeight + lineSpacingMultiplier * maxTextHeight) / 2.0F);
        if (initPosition == -1) {
            if (isLoop) {
                initPosition = (items.size() + 1) / 2;
            } else {
                initPosition = 0;
            }
        }

        preCurrentIndex = initPosition;
    }

    void smoothScroll(ACTION action) {
        cancelFuture();
        if (action == ACTION.FLING || action == ACTION.DRAG) {
            float itemHeight = lineSpacingMultiplier * maxTextHeight;
            mOffset = (int) ((totalScrollY % itemHeight + itemHeight) % itemHeight);
            if ((float) mOffset > itemHeight / 2.0F) {
                mOffset = (int) (itemHeight - (float) mOffset);
            } else {
                mOffset = -mOffset;
            }
        }
        mFuture = mExecutor.scheduleWithFixedDelay(new SmoothScrollTimerTask(this, mOffset), 0, 10, TimeUnit.MILLISECONDS);
    }


    protected final void scrollBy(float velocityY) {
        cancelFuture();
        // 修改这个值可以改变滑行速度
        int velocityFling = 10;
        mFuture = mExecutor.scheduleWithFixedDelay(new InertiaTimerTask(this, velocityY), 0, velocityFling, TimeUnit.MILLISECONDS);
    }

    public void cancelFuture() {
        if (mFuture != null && !mFuture.isCancelled()) {
            mFuture.cancel(true);
            mFuture = null;
        }
    }

    public void setTextColor(int color){
        paintCenterText.setColor(color);
        invalidate();
    }

    public void setTypeface(Typeface typeface){
        paintOuterText.setTypeface(typeface);
        paintCenterText.setTypeface(typeface);
        invalidate();
    }

    public final void setNotLoop() {
        isLoop = false;
    }

    public final void setTextSize(float size) {
        if (size > 0.0F) {
            this.textSize = (int) (context.getResources().getDisplayMetrics().density * size);
            paintOuterText.setTextSize(textSize);
            paintCenterText.setTextSize(textSize);
            remeasure();
            invalidate();
        }
    }

    public final void setTextEllipsisLen(int len){
        textEllipsisLen = len;
    }

    public boolean hasItem(String item) {
        int result = items.indexOf(item);
        return result != -1;
    }

    public void setSelectedItem(String item) {
        int selectedIndex = items.indexOf(item);
        setSelectedPosition(selectedIndex);
    }

    public int getItemPosition(String item) {
        return items.indexOf(item);
    }

    public int getViewHeight(){
       return measuredHeight;
    }

    public final void setSelectedPosition(int initPosition) {
        if (initPosition < 0) {
            this.initPosition = 0;
        } else {
            if (items != null && items.size() > initPosition) {
                this.initPosition = initPosition;
            }
        }
        selectedIndex = initPosition;
        totalScrollY = 0;
        cancelFuture();
        invalidate();
    }

    public final void setListener(OnItemSelectedListener OnItemSelectedListener) {
        onItemSelectedListener = OnItemSelectedListener;
    }

    public final void setItems(List<String> items) {
        this.items = items;
        remeasure();
        invalidate();
    }

    public String getIndexItem(int index) {
        return items.get(index);
    }
    public String getSelectedItem() {
        return selectedItem;
    }

    public final int getSelectedIndex() {
        return selectedIndex;
    }

    protected final void onItemSelected() {
        if (onItemSelectedListener != null) {
            postDelayed(new OnItemSelectedRunnable(this), 200L);
        }
    }

    protected final void drawText(Canvas canvas, String text, float posX, float posY, Paint paint) {
        StringBuffer stringBuffer = new StringBuffer();
        char[] array = text.toCharArray();
        int sum = 0;
        for(int i=0;i<array.length;i++){
            if(sum >= (textEllipsisLen * 2)){
                break;
            }
            char bt = array[i];
            if(bt > 127 || bt == 94){
                sum += 2;
            }
            else{
                sum ++;
            }
            stringBuffer.append(String.valueOf(bt));
        }
        String string = "";
        if(array.length != stringBuffer.toString().toCharArray().length){
            string = stringBuffer.toString() + "...";
        }
        else{
            string = text;
        }
        canvas.drawText(string, posX, posY, paint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (items == null) {
            return;
        }

        String as[] = new String[itemsVisible];
        int change = (int) (totalScrollY / (lineSpacingMultiplier * maxTextHeight));
        preCurrentIndex = initPosition + change % items.size();

        if (!isLoop) {
            if (preCurrentIndex < 0) {
                preCurrentIndex = 0;
            }
            if (preCurrentIndex > items.size() - 1) {
                preCurrentIndex = items.size() - 1;
            }
        } else {
            if (preCurrentIndex < 0) {
                preCurrentIndex = items.size() + preCurrentIndex;
            }
            if (preCurrentIndex > items.size() - 1) {
                preCurrentIndex = preCurrentIndex - items.size();
            }
        }

        int j2 = (int) (totalScrollY % (lineSpacingMultiplier * maxTextHeight));
        // 设置as数组中每个元素的值
        int k1 = 0;
        while (k1 < itemsVisible) {
            int l1 = preCurrentIndex - (itemsVisible / 2 - k1);
            if (isLoop) {
                while (l1 < 0) {
                    l1 = l1 + items.size();
                }
                while (l1 > items.size() - 1) {
                    l1 = l1 - items.size();
                }
                as[k1] = items.get(l1);
            } else if (l1 < 0) {
                as[k1] = "";
            } else if (l1 > items.size() - 1) {
                as[k1] = "";
            } else {
                as[k1] = items.get(l1);
            }
            k1++;
        }
        canvas.drawLine(0.0F, firstLineY, getWidth(), firstLineY, paintIndicator);
        canvas.drawLine(0.0F, secondLineY, getWidth(), secondLineY, paintIndicator);

        int j1 = 0;
        while (j1 < itemsVisible) {
            canvas.save();
            // L(弧长)=α（弧度）* r(半径) （弧度制）
            // 求弧度--> (L * π ) / (π * r)   (弧长X派/半圆周长)
            float itemHeight = maxTextHeight * lineSpacingMultiplier;
            double radian = ((itemHeight * j1 - j2) * Math.PI) / halfCircumference;
            // 弧度转换成角度(把半圆以Y轴为轴心向右转90度，使其处于第一象限及第四象限
            float angle = (float) (90D - (radian / Math.PI) * 180D);
            if (angle >= 90F || angle <= -90F) {
                canvas.restore();
            } else {
                int translateY = (int) (radius - Math.cos(radian) * radius - (Math.sin(radian) * maxTextHeight) / 2D);
                canvas.translate(0.0F, translateY);
                canvas.scale(1.0F, (float) Math.sin(radian));
                String text = as[j1];
                if (translateY <= firstLineY && maxTextHeight + translateY >= firstLineY) {
                    // 条目经过第一条线
                    canvas.save();
                    canvas.clipRect(0, 0, getWidth(), firstLineY - translateY);
                    drawText(canvas, text, getX(text, paintOuterText), getY(paintOuterText), paintOuterText);
                    canvas.restore();
                    canvas.save();
                    canvas.clipRect(0, firstLineY - translateY, getWidth(), (int) (itemHeight));
                    drawText(canvas, text, getX(text, paintCenterText), getY(paintCenterText), paintCenterText);
                    canvas.restore();
                } else if (translateY <= secondLineY && maxTextHeight + translateY >= secondLineY) {
                    // 条目经过第二条线
                    canvas.save();
                    canvas.clipRect(0, 0, getWidth(), secondLineY - translateY);
                    drawText(canvas, text, getX(text, paintCenterText), getY(paintCenterText), paintCenterText);
                    canvas.restore();
                    canvas.save();
                    canvas.clipRect(0, secondLineY - translateY, getWidth(), (int) (itemHeight));
                    drawText(canvas, text, getX(text, paintOuterText), getY(paintOuterText), paintOuterText);
                    canvas.restore();
                } else if (translateY >= firstLineY && maxTextHeight + translateY <= secondLineY) {
                    // 中间条目
                    canvas.clipRect(0, 0, getWidth(), (int) (itemHeight));
                    drawText(canvas, text, getX(text, paintCenterText), getY(paintCenterText), paintCenterText);
                    selectedItem = text;
                    selectedIndex = items.indexOf(text);
                } else {
                    // 其他条目
                    canvas.clipRect(0, 0, getWidth(), (int) (itemHeight));
                    drawText(canvas, text, getX(text, paintOuterText), getY(paintOuterText), paintOuterText);
                }
                canvas.restore();
            }
            j1++;
        }
    }

    private float getX(String text, Paint paint) {
        paint.getTextBounds(text, 0, text.length(), tempRect);
        //return (getWidth() - tempRect.width() * scaleX) / 2;
        if((getWidth() - tempRect.width() * scaleX)/2 > 0){
            return (getWidth() - tempRect.width() * scaleX) / 2;
        }
        else{
            return 0;
        }
    }

    /**
     * Added by shexiaoheng
     * 让字体垂直方向居中
     * */
    private float getY(Paint paint) {
        Rect rect = new Rect(0, 0, getWidth(), maxTextHeight);
        RectF bounds = new RectF(rect);
        bounds.bottom = paint.descent() - paint.ascent();
        bounds.top += (rect.height() - bounds.bottom) / 2.0f;
        return bounds.top - paint.ascent();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean eventConsumed = gestureDetector.onTouchEvent(event);
        float itemHeight = lineSpacingMultiplier * maxTextHeight;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startTime = System.currentTimeMillis();
                cancelFuture();
                previousY = event.getRawY();
                break;

            case MotionEvent.ACTION_MOVE:
                float dy = previousY - event.getRawY();
                previousY = event.getRawY();

                totalScrollY = (int) (totalScrollY + dy);

                // 边界处理。
                if (!isLoop) {
                    float top = -initPosition * itemHeight;
                    float bottom = (items.size() - 1 - initPosition) * itemHeight;

                    if (totalScrollY < top) {
                        totalScrollY = (int) top;
                    } else if (totalScrollY > bottom) {
                        totalScrollY = (int) bottom;
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
            default:
                if (!eventConsumed) {
                    float y = event.getY();
                    double l = Math.acos((radius - y) / radius) * radius;
                    int circlePosition = (int) ((l + itemHeight / 2) / itemHeight);

                    float extraOffset = (totalScrollY % itemHeight + itemHeight) % itemHeight;
                    mOffset = (int) ((circlePosition - itemsVisible / 2) * itemHeight - extraOffset);

                    if ((System.currentTimeMillis() - startTime) > 120) {
                        // 处理拖拽事件
                        smoothScroll(ACTION.DRAG);
                    } else {
                        // 处理条目点击事件
                        smoothScroll(ACTION.CLICK);
                    }
                }
                break;
        }
        invalidate();
        return true;
    }
}