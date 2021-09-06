package com.yingenus.pocketchinese.view;


import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;


import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;

import com.yingenus.pocketchinese.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class MultiColorProgressBar extends View  {
    public static final int TEXT_TYPE_PERCENTS=0;
    public static final int TEXT_TYPE_TRUTHS=1;
    public static final int TEXT_TYPE_USER=2;

    private List<ProgressElement> mProgressItems = new ArrayList<>();
    private int[] mValues;

    private Drawable[] mElementDrawable;
    private Bitmap[] mElementBitmaps;
    private Bitmap mBackgroundBitmap;
    private Bitmap mCashedBitmap;

    private String commonText;

    private boolean mShowText;
    private boolean mSpecialColorForAllElements;
    private boolean mEnableAnimation;
    private int mTextColor;
    private int mTextSize;
    private int mTextType;
    private int mMaxProgress;
    private String mElementsTextSeparator;

    private PorterDuffXfermode modeBackground=new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP);
    private PorterDuffXfermode modeProgress =new PorterDuffXfermode(PorterDuff.Mode.DST_OVER);

    private Paint mTextPainter=new TextPaint();
    private Paint mBitMapPainter=new Paint();

    public MultiColorProgressBar(Context context) {
        super(context);
    }

    public MultiColorProgressBar(Context context,  AttributeSet attrs) {
        super(context,attrs);

        TypedArray array=context.getTheme()
                .obtainStyledAttributes(attrs, R.styleable.MultiColorProgressBar,0,0);

        try {
            mShowText=array
                    .getBoolean(R.styleable.MultiColorProgressBar_mlt_showText,true);
            mSpecialColorForAllElements=array
                    .getBoolean(R.styleable.MultiColorProgressBar_mlt_specialColorForAllElements,false);
            mTextColor=array.getColor(R.styleable.MultiColorProgressBar_mlt_textColor, Color.BLACK);
            mTextSize=array.getDimensionPixelSize(R.styleable.MultiColorProgressBar_mlt_textSize,18);
            mTextType=array.getInt(R.styleable.MultiColorProgressBar_mlt_textType,TEXT_TYPE_TRUTHS);
            mEnableAnimation=array.getBoolean(R.styleable.MultiColorProgressBar_mlt_enableAnimation,false);
            mMaxProgress=array.getInteger(R.styleable.MultiColorProgressBar_mlt_maxProgress,100);
            mElementsTextSeparator=array.getString(R.styleable.MultiColorProgressBar_mlt_elementTextSeparator);
            if (mElementsTextSeparator==null){
                mElementsTextSeparator="/";
            }
        }finally {
            array.recycle();
        }
    }

    public MultiColorProgressBar(Context context,  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    public MultiColorProgressBar(Context context,  AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr,defStyleRes);
    }

    private void init(){
        mTextPainter.setTextSize(mTextSize);
        mTextPainter.setColor(mTextColor);
        mTextPainter.setStyle(Paint.Style.FILL);
        mTextPainter.setAntiAlias(true);
        mTextPainter.setSubpixelText(true);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width, height;

        int minWidth=getPaddingLeft()+getPaddingRight()+getSuggestedMinimumWidth();
        width=resolveSizeAndState(minWidth,widthMeasureSpec,0);

        int minHeight=getPaddingTop()+getPaddingBottom()+mTextSize;
        height=resolveSizeAndState(minHeight,heightMeasureSpec,0);

        setMeasuredDimension(width,height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        prepare();
        invalidateDraw();

    }


    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width=getWidth(),
                height=getHeight();


        mCashedBitmap=drawBitmap();

        canvas.drawBitmap(mCashedBitmap,0,0,null);

        if(mShowText) {
            Rect bonds = new Rect();
            mTextPainter.getTextBounds(commonText, 0, commonText.length(), bonds);
            int textWight = bonds.width();
            int textHeight = bonds.height();
            canvas.drawText(commonText, width / 2 - textWight / 2, height / 2 + textHeight / 2, mTextPainter);
        }
    }

    private Bitmap drawBitmap(){
        int width=getWidth(),
                height=getHeight();
        Bitmap bitmap=Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        bitmap.setHasAlpha(true);
        Canvas canvas=new Canvas(bitmap);

        mBitMapPainter.setXfermode(modeProgress);
        for (int i = 0; i< mElementBitmaps.length; i++){
            canvas.drawBitmap(mElementBitmaps[i],0,0, mBitMapPainter);
        }

        mBitMapPainter.setXfermode(modeBackground);
        canvas.drawBitmap(mBackgroundBitmap,0,0, mBitMapPainter);
        mBitMapPainter.setXfermode(null);

        return bitmap;
    }

    public void setTypeFace(Typeface typeFace){
        if (mTextPainter!=null)
            mTextPainter.setTypeface(typeFace);
    }

    public void setShowText(boolean showText) {
        this.mShowText = showText;
        updateUI();
    }

    public void setSpecialColorForAllElements(boolean specialColorForAllElements) {
        this.mSpecialColorForAllElements = specialColorForAllElements;
        updateUI();
    }

    public void setTextColor(@ColorInt int textColor) {
        this.mTextColor = textColor;
        mTextPainter.setColor(mTextColor);
    }

    public void setTextSize(int textSize) {
        this.mTextSize = textSize;
        mTextPainter.setTextSize(mTextSize);
    }

    public void setTextType(int textType){
        if (textType<0||textType>2)
            mTextType = TEXT_TYPE_TRUTHS;
        else
            this.mTextType=textType;
        updateUI();
    }

    public void setEnableAnimation(boolean enableAnimation) {
        this.mEnableAnimation = enableAnimation;
    }

    public void setMaxProgress(int maxProgress) {
        this.mMaxProgress = maxProgress;
    }

    public void setElementsTextSeparator(String elementsTextSeparator) {
        this.mElementsTextSeparator = elementsTextSeparator;
    }

    public String getElementsTextSeparator() {
        return mElementsTextSeparator;
    }

    public int getMaxProgress() {
        return mMaxProgress;
    }

    public boolean isEnableAnimation() {
        return mEnableAnimation;
    }

    public boolean isShowText() {
        return mShowText;
    }

    public boolean isSpecialColorForAllElements() {
        return mSpecialColorForAllElements;
    }

    public int getTextColor() {
        return mTextColor;
    }

    public int getTextSize() {
        return mTextSize;
    }

    public int getTextType() {
        return mTextType;
    }

    public void notifiProgressChanged(){
          updateUI();
    }

    private void prepare(){
        mValues =new int[mProgressItems.size()];
        mElementDrawable=new Drawable[mProgressItems.size()];
        commonText="";

        for (int i = 0; i< mValues.length; i++){
            mValues[i]=Math.min(mMaxProgress, mProgressItems.get(i).getProgressValue());
            commonText+= getText(mProgressItems.get(i))+mElementsTextSeparator;
            mElementDrawable[i]=loadDrawable(mProgressItems.get(i).getProgressBackground(),
                    mProgressItems.get(i).getProgressColor());


        }
        commonText=commonText.substring(0,commonText.length()-1);
        commonText+= getTextEnd();

    }

    //return Drawable if exist, null if not
    @NotNull
    private Drawable loadDrawable(int resDraw, int color){
        Drawable ans;
        try {
            ans= getResources().getDrawable(resDraw);
        }catch (Resources.NotFoundException exception){
            ans= null;
        }
        if(ans==null){
            return new PaintDrawable(color);
        }
        else {
            if(Color.TRANSPARENT==color)
                return ans;
            ColorFilter filter=new LightingColorFilter(0,color);
            ans.setColorFilter(filter);
            return ans;
        }
    }

    private void invalidateDraw(){
        mElementBitmaps =new Bitmap[mElementDrawable.length];
        int width=getWidth(),height=getHeight();
        float coef=(float) width/mMaxProgress;
        int start=0,end=0;
        for (int i = 0; i< mElementBitmaps.length; i++){
            start=end;
            end=(int)(start+coef* mValues[i]);
            mElementDrawable[i].setBounds(0,0,end,height);
            mElementBitmaps[i]=createBitMap(mElementDrawable[i],width,height);
        }
        Drawable background=getBackground();
        background.setBounds(0,0,width,height);
        mBackgroundBitmap=createBitMap(background,width,height);
    }

    private void updateUI(){
        if(isLaidOut()) {
            prepare();
            invalidateDraw();
        }
        invalidate();
    }

    private String getTextEnd(){
        if (mTextType == TEXT_TYPE_TRUTHS) {
            return mElementsTextSeparator+String.valueOf(mMaxProgress);
        }
        return "";
    }
    private String getText(ProgressElement element){
        switch (mTextType){
            case TEXT_TYPE_PERCENTS:
                return getPercents(element.getProgressValue(),mMaxProgress);
            case TEXT_TYPE_TRUTHS:
                return String.valueOf(element.getProgressValue());
            case TEXT_TYPE_USER:
                return element.getSetText();
            default:
                return "";
        }
    }

    private Bitmap createBitMap(Drawable drawable, int wight,int height){
        Bitmap bitmap=Bitmap.createBitmap(wight,height,Bitmap.Config.ARGB_8888);
        bitmap.setHasAlpha(true);
        Canvas canvas=new Canvas(bitmap);
        drawable.draw(canvas);
        return bitmap;
    }

    private String getPercents(int cur,int max){
        int percents=(cur*100)/max;
        return String.valueOf(percents);
    }

    public void addProgressElement(ProgressElement element){
        mProgressItems.add(element);
        updateUI();
    }
    public void addProgressElements(Collection<ProgressElement> elements){
        mProgressItems.addAll(elements);
        updateUI();
    }
    public ProgressElement getProgressElement(int index){
        if (mProgressItems.size()<index)
            return null;
        return mProgressItems.get(index);
    }
    public int getProgressElementsSize(){
        return mProgressItems.size();
    }
    public void deleteProgressElement(int index){
        mProgressItems.remove(index);
        updateUI();
    }
    public void deleteAll(){
        mProgressItems.clear();
        updateUI();
    }


    public static  class ProgressElement {

        private int mProgressColor= Color.TRANSPARENT;
        private int mProgressBackground=Resources.ID_NULL;
        private int mProgressTextColor=Color.BLACK;
        private int mProgressValue=0;

        private String mSetText="";

        public void setProgressColor(@ColorInt int progressColor) {
            this.mProgressColor = progressColor;
        }
        public void setProgressTextColor(@ColorInt int progressTextColor){
            this.mProgressTextColor=progressTextColor;
        }
        public void setProgressValue(int progressValue){
            this.mProgressValue=progressValue;
        }

        public void setSetText(String text) {
            this.mSetText = text;
        }

        public void setProgressBackground(@DrawableRes int reference){
            mProgressBackground=reference;
            //mBackground=getContext().getDrawable(reference);
        }

        public int getProgressBackground() {
            return mProgressBackground;
        }

        public int getProgressColor() {
            return mProgressColor;
        }

        public int getProgressTextColor() {
            return mProgressTextColor;
        }

        public int getProgressValue() {
            return mProgressValue;
        }

        public String getSetText(){
            return mSetText;
        }

        private void initPainter(){

        }

    }

}
