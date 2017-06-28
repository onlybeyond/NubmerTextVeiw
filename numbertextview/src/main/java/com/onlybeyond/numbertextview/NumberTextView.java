package com.onlybeyond.numbertextview;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;


/**
 * Created by only on 17/6/19.
 * Email: onlybeyond99@gmail.com
 */

public class NumberTextView extends TextView {

    //static
    private static String TAG = NumberTextView.class.getSimpleName();
    private static final int HANDLER_ROLL = 1;//滚动标识

    //base data
    private boolean isUseMax = false;//是否使用最大动画时间,默认不使用
    private int rollInt = 2;//滚动的整数差值,
    private int minRollInt = 3;//最小滚动
    private int animStatus = 0;//动画的状态 0,默认;1动画中
    private int scale;//数值的精度
    private int delayDuration = 0;//延迟时间,默认0ms,@没有延迟
    private int duration = 60;//滚动的时间间隔;
    private int maxAnimDuration = 1000;//最大动画时间
    private long startTime;//动画开始时间
    private double minRoll;//最小滚动,设置为0时将没有最小滚动
    private double currentNumberValue;//当前的值
    private String startNumberValue = "0";//开始滚动的值

    private String numberValue = "";//滚动的值
    private String numberValueUnit = "";//number 后面的单位
    private String numberValueSymbol="";//number 前面的符号

    //object data
    private Context mContext;
    private NumberTextViewListener numberTextViewListener;
    private double startNumberValueDouble;


    public interface NumberTextViewListener {
        void animStart(long startTime);

        void animEnd(long endTime, long animDuration);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            if (HANDLER_ROLL == what) {
                if (animStatus == 1 && !TextUtils.isEmpty(numberValue)) {
                    //保证没有停止动画或value值不为空
                    try {
                        double valueDouble = Double.parseDouble(numberValue);
                        long currentTime = System.currentTimeMillis();
                        long animDuration = currentTime - startTime;
                        if (valueDouble > 0) {
                            if (currentNumberValue < startNumberValueDouble) {
                                currentNumberValue = startNumberValueDouble;//设定 currentValue的初始值为startValue
                            }
                            currentNumberValue += (minRoll) * rollInt;

                            if (currentNumberValue < Double.parseDouble(numberValue) && (animDuration < maxAnimDuration)) {
                                BigDecimal bigDecimal = new BigDecimal(currentNumberValue);
                                setText(numberValueSymbol+bigDecimal.setScale(scale, BigDecimal.ROUND_HALF_UP).toString() + numberValueUnit);
                                handler.sendEmptyMessageDelayed(HANDLER_ROLL, duration);
                            } else {
                                animStatus = 0;
                                setText(numberValueSymbol+String.valueOf(numberValue) + numberValueUnit);
                                Log.d(TAG, "---anim duration" + (currentTime - startTime));
                                if (numberTextViewListener != null) {
                                    numberTextViewListener.animEnd(currentTime, animDuration);
                                }
                            }

                        } else {
                            if (currentNumberValue > startNumberValueDouble) {
                                currentNumberValue = startNumberValueDouble;//设定 currentValue的初始值为startValue
                            }
                            currentNumberValue -= (minRoll) * rollInt;
                            if (currentNumberValue > Double.parseDouble(numberValue) && (animDuration < maxAnimDuration)) {
                                BigDecimal bigDecimal = new BigDecimal(currentNumberValue);
                                setText(numberValueSymbol+bigDecimal.setScale(scale, BigDecimal.ROUND_HALF_UP).toString() + numberValueUnit);
                                handler.sendEmptyMessageDelayed(HANDLER_ROLL, duration);
                            } else {
                                animStatus = 0;
                                setText(numberValueSymbol+String.valueOf(numberValue) + numberValueUnit);
                                Log.d(TAG, "---anim duration" + (currentTime - startTime));
                                if (numberTextViewListener != null) {
                                    numberTextViewListener.animEnd(currentTime, animDuration);
                                }
                            }
                        }
                    } catch (NumberFormatException e) {
                        //数据转化异常
                        e.printStackTrace();
                        setText(numberValueSymbol+numberValue + numberValueUnit);

                    }
                } else {
                    setText(numberValueSymbol+numberValue + numberValueUnit);
                }

            }
        }
    };

    public NumberTextView(Context context) {
        this(context, null);
    }

    public NumberTextView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public NumberTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.NumberTextView);
        isUseMax = ta.getBoolean(R.styleable.NumberTextView_ntvUseMaxAnimDuration, true);
        duration = ta.getInt(R.styleable.NumberTextView_ntvChangeDuration, 60);
        maxAnimDuration = ta.getInt(R.styleable.NumberTextView_ntvMaxAnimDuration, 1000);
        minRoll = ta.getInt(R.styleable.NumberTextView_ntvMinRoll, 0);
        rollInt = ta.getInt(R.styleable.NumberTextView_ntvRollInt, 1);
        numberValueSymbol=ta.getString(R.styleable.NumberTextView_ntvNumberSymbol);
        numberValueUnit=ta.getString(R.styleable.NumberTextView_ntvNumberValueUnit);
        startNumberValue = ta.getString(R.styleable.NumberTextView_ntvStartValue);
        startNumberValueDouble = Double.parseDouble(startNumberValue);


        ta.recycle();
    }

    /**
     * 设置是否使用最大动画时间
     *
     * @param useMax
     */
    public void setUseMax(boolean useMax) {
        isUseMax = useMax;
    }


    /**
     * 设置每次滚动的时间间隔
     *
     * @param duration
     */
    public void setDuration(int duration) {
        this.duration = duration;
    }

    /**
     * 设置每次滚动倍数
     *
     * @param rollInt
     */
    public void setRollInt(int rollInt) {
        this.rollInt = rollInt;
    }

    /**
     * 设置最小滚动的倍数
     *
     * @param minRollInt
     */
    public void setMinRollInt(int minRollInt) {
        this.minRollInt = minRollInt;
    }


    /**
     * 设置动画延迟时间
     *
     * @param delayDuration
     */
    public void setDelayDuration(int delayDuration) {
        this.delayDuration = delayDuration;
    }

    public void setNumberTextViewListener(NumberTextViewListener numberTextViewListener) {
        this.numberTextViewListener = numberTextViewListener;
    }

    public void setNumberValueSymbol(String numberValueSymbol) {
        this.numberValueSymbol = numberValueSymbol;
    }

    public void setMaxAnimDuration(int maxAnimDuration) {
        this.maxAnimDuration = maxAnimDuration;
    }

    /**
     * 设置滚动的值
     *
     * @param numberValue
     */
    public void setNumberValue(String numberValue) {
        setNumberValue("0", numberValue);
    }

    /**
     * 设置滚动的值
     *
     * @param startNumberValue//开始滚动的值
     * @param value
     */
    public void setNumberValue(String startNumberValue, String value) {
        setNumberValue(startNumberValue, value, "");
    }

    /**
     * 设置滚动的值
     *
     * @param startValue 开始值
     * @param value      最终值
     * @param valueUnit  单位
     */
    public void setNumberValue(String startValue, String value, String valueUnit) {
        this.numberValue = value;
        this.numberValueUnit = valueUnit;
        try {
            if (!TextUtils.isEmpty(this.numberValue)) {
                this.startNumberValue = startValue;//
                startNumberValueDouble = Double.parseDouble(startNumberValue);

                currentNumberValue = 0;
                //当有动画时停止动画
                stopAnim();
                //获取value 的最小值设定滚动的精度
                BigDecimal bigDecimal = new BigDecimal(value);
                scale = bigDecimal.scale();
                minRoll = 1;
                for (int i = 0; i < scale; i++) {
                    minRoll *= 0.1;
                }
                //解决minRoll会有多位的情况
                BigDecimal bigDecimalTemp = new BigDecimal(minRoll);
                minRoll = bigDecimalTemp.setScale(scale, BigDecimal.ROUND_DOWN).doubleValue();
                Log.d(TAG, TAG + "scale" + scale + "min roll" + minRoll + "numberValue" + value);
                rollNum();
            }
        } catch (Exception e) {
            Toast.makeText(mContext, "请设置正确数值", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    /**
     * 滚动数字
     */
    public void rollNum() {
        if (!TextUtils.isEmpty(numberValue)) {
            double valueDouble = Double.parseDouble(numberValue);
            double distance = valueDouble - Double.parseDouble(startNumberValue);//差值
            double distanceTemp = (maxAnimDuration / duration) * (rollInt * minRoll);
            BigDecimal bigDecimal = new BigDecimal(distanceTemp);
            double distanceDouble = bigDecimal.setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();//解决double 类型多位bug
            if (valueDouble > 0) {
                //为正数的情况
                Log.d(TAG, "---distance" + distance);
                if ((distance < minRoll * minRollInt)) {
                    //小于最小滚动值,直接忽略变换
                    setText(numberValueSymbol+String.valueOf(numberValue) + numberValueUnit);
                } else {
                    animStatus = 1;
                    startTime = System.currentTimeMillis();
                    if (numberTextViewListener != null) {
                        numberTextViewListener.animStart(startTime);
                    }
                    if(isUseMax&&maxAnimDuration!=0) {
                        startNumberValue = "" + (Double.parseDouble(numberValue) - distanceDouble >
                                startNumberValueDouble ? (Double.parseDouble(numberValue) - distanceDouble) : startNumberValueDouble);
                    }
                    Log.d(TAG, "---distance temp" + distanceTemp + "  distance int" + distanceDouble + " start numberValue" + startNumberValue + "---distance" + distance);
                    BigDecimal bigDecimalStart = new BigDecimal(0);
                    setText(numberValueSymbol+bigDecimalStart.setScale(scale, BigDecimal.ROUND_HALF_UP).toString() + numberValueUnit);
                    handler.sendEmptyMessageDelayed(HANDLER_ROLL, delayDuration + duration);

                }
            } else {
                //为负数的情况
                if ((Math.abs(distance) < minRoll * minRollInt)) {
                    //小于最小滚动值,直接忽略变换
                    setText(numberValueSymbol+String.valueOf(numberValue) + numberValueUnit);
                } else {
                    animStatus = 1;
                    startTime = System.currentTimeMillis();
                    if(isUseMax&&maxAnimDuration!=0) {
                        startNumberValue = "" + ((Double.parseDouble(numberValue) + distanceDouble) <
                                startNumberValueDouble ? (Double.parseDouble(numberValue) + distanceDouble) : startNumberValueDouble);
                    }
                    Log.d(TAG, "---distance temp" + distanceTemp + "  distance int" + distanceDouble + " start numberValue" + startNumberValue + "---distance" + distance);
                    BigDecimal bigDecimalStart = new BigDecimal(0);
                    setText(numberValueSymbol+bigDecimalStart.setScale(scale, BigDecimal.ROUND_HALF_UP).toString() + numberValueUnit);
                    handler.sendEmptyMessageDelayed(HANDLER_ROLL, delayDuration + duration);

                }

            }
        }
    }


    /**
     * 开始动画
     *
     * @param delayDuration
     */
    public void startAnim(int delayDuration) {
        if (!TextUtils.isEmpty(numberValue) && animStatus == 0) {
            this.delayDuration = delayDuration;
            currentNumberValue = 0;
            rollNum();
        }
    }

    public void startAnim() {
        startAnim(300);
    }

    /**
     * 停止动画
     */
    public void stopAnim() {
        if (animStatus == 1) {
            animStatus = 0;
            setText(numberValueSymbol+String.valueOf(numberValue) + numberValueUnit);
            handler.removeMessages(HANDLER_ROLL);
        }
    }

    /**
     * 设置成默认值
     */
    public void setDefault() {
        stopAnim();
        this.numberValue = "";
    }


}
