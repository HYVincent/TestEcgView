package com.example.administrator.testecgview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class EcgBgView extends View {

    public EcgBgView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //初始化宽高
        mViewWidth = w;
        mViewHeight = h;
    }
    //数据数组
    private List<EcgDataBean> dataBeans = new ArrayList<>();
    //设置数据
    public void setDataBeans(List<EcgDataBean> dataBeans) {
        if(dataBeans != null){
            this.dataBeans.addAll(dataBeans);
        }
        invalidate();
    }

    /**
     * 添加数据点
     * @param ecgDataBean
     */
    public void addData(EcgDataBean ecgDataBean){
        if(dataBeans != null){
            if(dataBeans.size()>mMaxDataNum){
                //移除最后一个
//                this.dataBeans.remove(dataBeans.size()-1);
                this.dataBeans.remove(0);
            }
            //添加始终是第一个位置
//            this.dataBeans.add(0,ecgDataBean);
            this.dataBeans.add(ecgDataBean);
            Log.d("受到数据", "addData: "+ecgDataBean.getData());
        }
        invalidate();
    }

    private Context mContext;
    //View的宽度
    private float mViewWidth;
    //View的高度
    private float mViewHeight;
    //左边文字矩形的宽度
    private float mLeftTextWidth = 60;
    //画笔
    private Paint mLeftEcgTagPaint;
    //左边心率Tag文字的大小
    private float mLeftEcgDataTagTextSize = 18;
    private String mLeftEcgDataTag = "心率";
    //左边心率Tag文字的颜色
    private int mLeftEcgDataTagTextColor;
    //左边心率数据画笔
    private Paint mLeftEcgPaint;
    private String mLeftEcgUnit = "次/分";
    //左边心率数据颜色
    private int mLeftEcgDataTextColor;
    private float mLeftEcgDataTextSize = 12;
    //左边的tag和数据之间的间距
    private float mLeftTagAndEcgMargin = 10;
    //心电图tag和心电数据距离View上下边缘的距离 = (view的高度- tag文字的高度 - mLeftTagAndEcgMargin - ecg的高度)/2;
    private float mLeftTextButtomMargin;
    //最小值
    private float mMinValue = 0;
    //最大值
    private float mMaxValue = 200;
    //两个tag之间的值间隔
    private float mItemValue = 10;
    private Paint mYLineTagPaint;
    //数据和左边的间距
    private float tagAndEcgLeftMargin = 20;
    //纵向的线条的起点坐标
    private float mEcgStartX;
    //当前的心率数据
    private float currentEcgData;
    //两个数据点之间的间隔
    private float mItemDataMargin = 2;
    //表示屏幕上显示的最大的点的个数
    private float mMaxDataNum = 0f;
    private Path mEcgPath;
    private void init(Context mContext, AttributeSet attrs){
        this.mContext = mContext;
        mLeftTextWidth = DpUtil.dp2px(mContext,mLeftTextWidth);
        mLeftEcgDataTagTextSize = DpUtil.dp2px(mContext,mLeftEcgDataTagTextSize);
        mLeftEcgDataTagTextColor = ContextCompat.getColor(mContext,R.color.color_blue_31c2bf);
        mLeftEcgDataTextColor =  ContextCompat.getColor(mContext,R.color.color_white_color);
        mLeftEcgDataTextSize = DpUtil.dp2px(mContext,mLeftEcgDataTextSize);
        mLeftTagAndEcgMargin = DpUtil.dp2px(mContext,mLeftTagAndEcgMargin);
        tagAndEcgLeftMargin = DpUtil.dp2px(mContext,tagAndEcgLeftMargin);
        mItemDataMargin = DpUtil.dp2px(mContext,mItemDataMargin);

        mEcgStartX = mLeftTextWidth + tagAndEcgLeftMargin;
        //绘制  心率 文字画笔
        mLeftEcgTagPaint = new Paint();
        mLeftEcgTagPaint.setAntiAlias(true);
        mLeftEcgTagPaint.setTextSize(mLeftEcgDataTagTextSize);
        mLeftEcgTagPaint.setColor(mLeftEcgDataTagTextColor);
        //左边的心率画笔
        mLeftEcgPaint = new Paint();
        mLeftEcgPaint.setAntiAlias(true);
        mLeftEcgPaint.setTextSize(mLeftEcgDataTextSize);
        mLeftEcgPaint.setColor(mLeftEcgDataTextColor);

        mYLineTagPaint = new Paint();
        mYLineTagPaint.setAntiAlias(true);
        mYLineTagPaint.setColor(ContextCompat.getColor(mContext,R.color.color_blue_3c8093));
        mYLineTagPaint.setTextSize(DpUtil.dp2px(mContext,8));
        mYLineTagPaint.setStyle(Paint.Style.STROKE);

        mEcgPath = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制背景
        canvas.drawColor(ContextCompat.getColor(mContext,R.color.color_blue_2a284f));
        drawLeftText(canvas);
        drawBgLine(canvas);
        drawEcgData(canvas);
    }

    /**
     * 绘制心电数据
     * @param canvas
     */
    private void drawEcgData(Canvas canvas) {
        //计算屏幕上能够展示的最大的点的个数
        mMaxDataNum = (mViewWidth - mLeftTextWidth - tagAndEcgLeftMargin) / mItemDataMargin;
        //相当于 mEcgPath = new Path();
        mEcgPath.reset();
        if(dataBeans.size()>0){
            //移动到第一个点的位置
            mEcgPath.moveTo(mEcgStartX,getY(dataBeans.get(0)));
            //开始瞄点
            mEcgPath.lineTo(mEcgStartX,getY(dataBeans.get(0)));
            for (int i = 0;i<dataBeans.size();i++){
                mEcgPath.lineTo(mEcgStartX + mItemDataMargin * i,getY(dataBeans.get(i)));
                if(i % 20 == 0){
                    //绘制下面的时间
                    Rect timeRect = new Rect();
                    String timeStr = String.valueOf(dataBeans.get(i).getTime()) +"s";
                    mYLineTagPaint.getTextBounds(timeStr,0,timeStr.length(),timeRect);
                    //绘制事件文本
                    canvas.drawText(timeStr,mEcgStartX + mItemDataMargin * i - timeRect.width()/2,mViewHeight - timeRect.height()/2,mYLineTagPaint);
                }
            }
            //绘制心电数据路径
            canvas.drawPath(mEcgPath,mYLineTagPaint);
        }
    }

    //绘制背景线条
    private void drawBgLine(Canvas canvas) {
        //有多少个item
        float mItemNum = (mMaxValue - mMinValue)/mItemValue+1;
        //mItemHeightY 每个item的高度 比如0到10这个区间对应的高度
        float mItemHeightY;
        float mItemTextHeight = mViewHeight;
        for (int i = 0;i < mItemNum;i++){
            Rect mItemTagRect = new Rect();
            String textTag = String.valueOf((int)mItemValue * i);
            mYLineTagPaint.getTextBounds(textTag,0,textTag.length(),mItemTagRect);
            mItemTextHeight = mItemTagRect.height();
            mItemHeightY = (mViewHeight - mItemTagRect.height() - DpUtil.dp2px(mContext,5) * 2)/mItemNum;
            //根据效果图可知，每两个横向的短线条就有一个长线条
            if(i % 3 == 0){
                canvas.drawText(textTag,mEcgStartX-DpUtil.dp2px(mContext,3)-mItemTagRect.width(),
                        mViewHeight-mItemTagRect.height()/2 - DpUtil.dp2px(mContext,5)-i * mItemHeightY,mYLineTagPaint);
                //绘制长线条横线的
                canvas.drawLine(mEcgStartX,mViewHeight-mItemTagRect.height() - DpUtil.dp2px(mContext,5)-i * mItemHeightY,
                        mViewWidth,mViewHeight-mItemTagRect.height() - DpUtil.dp2px(mContext,5)-i * mItemHeightY,mYLineTagPaint);
            }else  {
                //绘制短线条  横向的
                canvas.drawLine(mEcgStartX,mViewHeight-mItemTagRect.height() - DpUtil.dp2px(mContext,5)-i * mItemHeightY,
                        mEcgStartX + DpUtil.dp2px(mContext,3),mViewHeight-mItemTagRect.height() - DpUtil.dp2px(mContext,5)-i * mItemHeightY,mYLineTagPaint);
                if(i == 20){
                    //把200也画出来
                    canvas.drawText(textTag,mEcgStartX-DpUtil.dp2px(mContext,3)-mItemTagRect.width(),
                            mViewHeight-mItemTagRect.height()/2 - DpUtil.dp2px(mContext,5)-i * mItemHeightY,mYLineTagPaint);
                }
            }
        }
        //绘制纵向的竖线
        canvas.drawLine(mEcgStartX,mViewHeight-mItemTextHeight-DpUtil.dp2px(mContext,5), mEcgStartX,DpUtil.dp2px(mContext,5),mYLineTagPaint);
        //绘制纵向的渐变色线
        Rect lineRect = new Rect();
        lineRect.set((int)(mLeftTextWidth),0,
                (int)(mLeftTextWidth+ DpUtil.dp2px(mContext,2)),
                (int) mViewHeight);
        // 渐变的颜色
       LinearGradient lg = new LinearGradient((int)(mLeftTextWidth), 0, (int)(mLeftTextWidth+ DpUtil.dp2px(mContext,2)), (int) (mViewHeight), Color.parseColor("#00ffffff"),
                Color.parseColor("#ffffff"), Shader.TileMode.CLAMP);// CLAMP重复最后一个颜色至最后
        int colors[] = new int[3];
        float positions[] = new float[3];
        // 第1个点
        colors[0] = 0xFF239097;
        positions[0] = 1;
        // 第2个点
        colors[1] = 0xFF239097;
        positions[1] = 0.5f;
        // 第3个点
        colors[2] = 0xFF239097;
        positions[2] = 0;

        LinearGradient shader = new LinearGradient(
                0, 0,
                0, mViewHeight,
                colors,
                positions,
                Shader.TileMode.MIRROR);
        mYLineTagPaint.setShader(shader);
        mYLineTagPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawRect(lineRect, mYLineTagPaint);
        mYLineTagPaint.setStyle(Paint.Style.STROKE);
    }

    /**
     * 绘制左边的心率文字tag
     * @param canvas
     */
    private void drawLeftText(Canvas canvas) {
        if(dataBeans.size() >0){
            currentEcgData = dataBeans.get(dataBeans.size()-1).getData();
            Log.d("绘制心电图", "drawLeftText: "+currentEcgData);
        }else {
            //这里是测试值，写为70
            currentEcgData = 70;
        }
        //绘制心率
        Rect leftEcgTagRect = new Rect();
        mLeftEcgTagPaint.getTextBounds(mLeftEcgDataTag,0,mLeftEcgDataTag.length(),leftEcgTagRect);
        //leftEcgRect 计算文字的宽高
        Rect leftEcgRect = new Rect();
        //清空之前的文字
        mLeftEcgUnit = "次/分";
        mLeftEcgUnit = String.valueOf((int)currentEcgData) +mLeftEcgUnit;
        mLeftEcgPaint.getTextBounds(mLeftEcgUnit,0,mLeftEcgUnit.length(),leftEcgRect);
        mLeftTextButtomMargin = (mViewHeight - leftEcgTagRect.height() - leftEcgRect.height() - mLeftTagAndEcgMargin)/2;
        //绘制左边的tag文字
        canvas.drawText(mLeftEcgDataTag,mLeftTextWidth/2 - leftEcgTagRect.width()/2,mLeftTextButtomMargin,mLeftEcgTagPaint);
        //绘制数据tag文字
        canvas.drawText(mLeftEcgUnit,mLeftTextWidth/2-leftEcgRect.width()/2,mLeftTextButtomMargin+leftEcgTagRect.height()+mLeftTagAndEcgMargin,mLeftEcgPaint);
    }

    //根据数值获取Y轴的坐标值
    private float getY(EcgDataBean ecgDataBean){
        double percentage = ecgDataBean.getData() * 1.0 / (mMaxValue+mItemValue);
        float y = (mViewHeight - DpUtil.dp2px(mContext,5)) * (1 - (float)percentage);
        return y > (mViewHeight-DpUtil.dp2px(mContext,5))?(mViewHeight-DpUtil.dp2px(mContext,5)):y;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //默认测量模式为EXACTLY，否则请使用上面的方法并指定默认的宽度和高度
        mViewWidth = MeasureSpec.getSize(widthMeasureSpec);
        mViewHeight = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension((int)mViewWidth, (int) mViewHeight);
    }

}
