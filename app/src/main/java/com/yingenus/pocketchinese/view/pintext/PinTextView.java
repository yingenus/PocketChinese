package com.yingenus.pocketchinese.view.pintext;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Build;
import android.text.InputFilter;
import android.util.AttributeSet;

import androidx.annotation.ColorInt;
import androidx.annotation.StyleableRes;
import androidx.appcompat.widget.AppCompatEditText;

import com.yingenus.pocketchinese.R;

import java.util.Arrays;

public class PinTextView extends AppCompatEditText {
    public static final int ITEM_GRAVITY_LEFT=0;
    public static final int ITEM_GRAVITY_CENTER=1;
    public static final int ITEM_GRAVITY_RIGHT=2;


    private ColorStateList mTextColorStates = null;
    private ColorStateList mCharUnderlineColorStates = null;
    private ColorStateList mBlankCharUnderlineColorStates = null;
    private ColorStateList mItemBackgroundColorStates = null;
    private ColorStateList mItemShadowColorStates = null;


    private int mTextColor=Color.BLACK;
    private int mTextSize;
    private int mItemHeight;
    private int mItemWight;
    private int mCharUnderlineColor;
    private int mCharUnderlineHeight;
    private int mBlankCharUnderlineColor;
    private int mMaxRows;
    private int mMaxColumns;
    private int mItemGravity;
    private int mBetweenItemHeight;
    private int mBetweenItemWight;
    private int mCharUnderlineBottomPadding;
    private int mItemBackgroundColor;
    private int mItemShadowColor;
    private int mItemBackgroundRound;
    private int mItemElevation;
    private int mCharUnderlineElevation;
    private int mTextElevation;

    private String mText;

    private ShapeDrawable mItemBackground;
    private ShapeDrawable mUnderline;
    private InputFilter mLengthFilter;
    private InputFilter[] mFilters;

    private boolean smtChanged = false;
    private Bitmap bufferedCanvas = null;
    private Canvas mCanvas = new Canvas();

    private Paint mBitmapPainter;
    private Paint mTextPainter;
    private Paint mItemShadowPainter;
    private Paint mCharUnderlineShadowPainter;

    private int[] mRows=new int[0];
    private int[] mSpace=new int[0];

    public PinTextView(Context context) {
        super(context);
        initAttrs(context,null);
    }

    public PinTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context,attrs);
    }

    public PinTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context,attrs);
    }

    private void initAttrs(Context context, AttributeSet attrs){
        TypedArray array=context.getTheme().
                obtainStyledAttributes(attrs,R.styleable.PinTextView,0,0);

        try {
            mTextColorStates = getColorStateList(array,R.styleable.PinTextView_ptv_textColor,Color.BLACK);
            mTextSize=array.getDimensionPixelSize(R.styleable.PinTextView_ptv_textSize,dp2pix(18));
            mItemHeight=array.getDimensionPixelSize(R.styleable.PinTextView_ptv_itemHeight,dp2pix(25));
            mItemWight=array.getDimensionPixelSize(R.styleable.PinTextView_ptv_itemWight,dp2pix(20));

            mCharUnderlineColorStates = getColorStateList(array,R.styleable.PinTextView_ptv_charUnderlineColor,Color.BLUE);
            mCharUnderlineHeight=array.getDimensionPixelSize(R.styleable.PinTextView_ptv_charUnderlineHeight,dp2pix(5));
            mCharUnderlineBottomPadding=array.getDimensionPixelSize(R.styleable.PinTextView_ptv_charUnderlineBottomPadding,dp2pix(5));

            mBlankCharUnderlineColorStates = getColorStateList(array,R.styleable.PinTextView_ptv_blankCharUnderlineColor,Color.GRAY);
            mMaxRows=array.getInt(R.styleable.PinTextView_ptv_maxRows,1);
            mMaxColumns=array.getInt(R.styleable.PinTextView_ptv_maxColumns,5);
            mItemGravity=array.getInt(R.styleable.PinTextView_ptv_itemGravity,ITEM_GRAVITY_CENTER);
            mBetweenItemHeight=array.getDimensionPixelSize(R.styleable.PinTextView_ptv_betweenItemHeight,dp2pix(8));
            mBetweenItemWight=array.getDimensionPixelSize(R.styleable.PinTextView_ptv_betweenItemWight,dp2pix(8));
            mText=array.getString(R.styleable.PinTextView_ptv_text);
            mItemBackgroundColorStates = getColorStateList(array,R.styleable.PinTextView_ptv_itemBackgroundColor,Color.TRANSPARENT);
            mItemShadowColorStates = getColorStateList(array,R.styleable.PinTextView_ptv_itemShadowColor, mItemBackgroundColorStates);
            mItemBackgroundRound=array.getDimensionPixelSize(R.styleable.PinTextView_ptv_itemBackgroundRound,dp2pix(0));
            mItemElevation=array.getDimensionPixelSize(R.styleable.PinTextView_ptv_itemElevation,dp2pix(0));
            mCharUnderlineElevation=array.getDimensionPixelSize(R.styleable.PinTextView_ptv_charUnderlineElevation,dp2pix(0));
            mTextElevation=array.getDimensionPixelSize(R.styleable.PinTextView_ptv_textElevation,dp2pix(0));
        }finally {
            array.recycle();
        }
    }

    private static ColorStateList getColorStateList(TypedArray attrs, @StyleableRes int index, ColorStateList defStateList){
        ColorStateList stateColor = attrs.getColorStateList(index);
        if (stateColor != null){
            return stateColor;
        }
        else {
            return defStateList;
        }
    }
    private static ColorStateList getColorStateList(TypedArray attrs, @StyleableRes int index, @ColorInt int defColor){
        return getColorStateList(attrs,index, ColorStateList.valueOf(defColor));
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        invalidateColorStateStates();
        mRows=new int[mMaxRows];
        Arrays.fill(mRows, mMaxColumns);
        init();
        initItemBackground();
        limitCharsToNoOfFields();
        super.setWillNotDraw(false);
        super.setMaxLines(1);
        super.setSingleLine(true);
        super.setTextIsSelectable(false);
        super.setBackgroundTintMode(PorterDuff.Mode.CLEAR);
        Drawable transparentDraw= new ColorDrawable(Color.TRANSPARENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            super.setTextCursorDrawable(transparentDraw);
            super.setTextSelectHandleLeft(transparentDraw);
            super.setTextSelectHandleRight(transparentDraw);
        }

    }

    @Override
    public boolean onCheckIsTextEditor() {
      return true;
    }

    @Override
    public void setWillNotDraw(boolean willNotDraw) {
        super.setWillNotDraw(willNotDraw);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int wight,height;
        int minWight=0,minHeight=0;

        int maxColumn=longestColumn();
        minWight=getPaddingLeft()+maxColumn*mItemWight+
                (maxColumn-1)*mBetweenItemWight+getPaddingRight();
        minHeight=getPaddingTop()+mRows.length*mItemHeight+
                (mRows.length-1)*mBetweenItemHeight+getPaddingBottom();


        wight=resolveSizeAndState(minWight,widthMeasureSpec,0);
        height=resolveSizeAndState(minHeight,heightMeasureSpec,0);

        setMeasuredDimension(wight,height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        //super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        invalidateColorStateStates();
    }

    private void invalidateColorStateStates(){
        int[] states = getDrawableState();
        if (mTextColorStates != null){
            int newColor = mTextColorStates.getColorForState(states,mTextColorStates.getDefaultColor());
            if (newColor !=  mTextColor){
                mTextColor = newColor;
                smtChanged = true;
            }
        }
        if (mCharUnderlineColorStates != null){
            int newColor = mCharUnderlineColorStates
                    .getColorForState(states,mCharUnderlineColorStates.getDefaultColor());
            if (newColor !=  mCharUnderlineColor){
                mCharUnderlineColor = newColor;
                smtChanged = true;
            }
        }
        if (mBlankCharUnderlineColorStates != null){
            int newColor = mBlankCharUnderlineColorStates
                    .getColorForState(states,mBlankCharUnderlineColorStates.getDefaultColor());
            if (newColor !=  mBlankCharUnderlineColor){
                mBlankCharUnderlineColor = newColor;
                smtChanged = true;
            }
        }
        if ( mItemBackgroundColorStates != null){
            int newColor = mItemBackgroundColorStates
                    .getColorForState(states, mItemBackgroundColorStates.getDefaultColor());
            if (newColor !=  mItemBackgroundColor){
                mItemBackgroundColor = newColor;
                smtChanged = true;
            }
        }
        if ( mItemShadowColorStates != null){
            int newColor = mItemShadowColorStates
                    .getColorForState(states, mItemShadowColorStates.getDefaultColor());
            if (newColor !=   mItemShadowColor){
                mItemShadowColor = newColor;
                smtChanged = true;
            }
        }
    }

    private void init(){
        mTextPainter=new Paint();
        mTextPainter.setColor(mTextColor);
        mTextPainter.setTextSize(mTextSize);
        mTextPainter.setSubpixelText(true);
        mTextPainter.setAntiAlias(true);
        mTextPainter.setShadowLayer(mTextElevation,0,0,mTextColor);

        mItemShadowPainter=new Paint();
        mItemShadowPainter.setShadowLayer(mItemElevation,0,0,mItemShadowColor);

        mCharUnderlineShadowPainter=new Paint();

        //super.setInputType(InputType.TYPE_CLASS_TEXT);
        //super.setTextIsSelectable(false);
        //super.setWillNotDraw(true);
    }

    private void initItemBackground(){
        float[] round=new float[8];
        Arrays.fill(round,(float)mItemBackgroundRound);
        mItemBackground=new ShapeDrawable(new RoundRectShape(round,null,null));
        mItemBackground.getPaint().setColor(mItemBackgroundColor);
        mItemBackground.getPaint().setShadowLayer(mItemElevation,0,0,mItemShadowColor);
    }


    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        smtChanged = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (bufferedCanvas == null || smtChanged){
            drawBuffer();
        }
        canvas.drawBitmap(bufferedCanvas,0,0,null);
    }

    private void drawBuffer(){
        if (bufferedCanvas == null || smtChanged){
            int wight = Math.max(1, getWidth());
            int height = Math.max(1,getHeight());
            Bitmap createdBitmap = Bitmap.createBitmap(wight,height, Bitmap.Config.ARGB_8888);
            bufferedCanvas = createdBitmap;
            mCanvas = new Canvas(bufferedCanvas);
        }
        mCanvas.save();
        Canvas canvas = mCanvas;

        //super.onDraw(canvas);
        //Находим и создаем подчеркивание
        int shapeWight=mItemWight*80/100;
        ShapeDrawable underLine=getCharUnderLine(shapeWight,mCharUnderlineHeight);
        //находим начало отрисовки подчеркивания
        int shapeStartX=mItemWight/2-shapeWight/2;
        int shapeStartY=mItemHeight-mCharUnderlineHeight;
        mTextPainter.setTextAlign(Paint.Align.CENTER);

        String editText=super.getText().toString();

        int textPointer=0;
        int position=0;
        int startY=getPaddingTop();
        for (int row = 0; row <mRows.length; row++){
            int startX=getStartX(mRows[row]);

            for (int column=0;column<mRows[row];column++){
                if (!needToSkip(position)) {

                    mItemBackground.setBounds(startX, startY, startX+mItemWight,startY+mItemHeight);
                    mItemBackground.draw(canvas);
                    if (textPointer >= editText.length()) {
                        underLine.getPaint().setColor(mBlankCharUnderlineColor);
                        underLine.setBounds(startX+shapeStartX,startY+shapeStartY-mCharUnderlineBottomPadding,
                                startX+shapeStartX+shapeWight,startY+shapeStartY+mCharUnderlineHeight-mCharUnderlineBottomPadding);
                        underLine.draw(canvas);
                    } else {
                        char ch = editText.charAt(textPointer);
                        underLine.getPaint().setColor(mCharUnderlineColor);
                        underLine.setBounds(startX+shapeStartX,startY+shapeStartY-mCharUnderlineBottomPadding,
                                startX+shapeStartX+shapeWight,startY+shapeStartY+mCharUnderlineHeight-mCharUnderlineBottomPadding);
                        underLine.draw(canvas);
                        Rect textRect=new Rect();
                        mTextPainter.getTextBounds(new char[]{ch},0,1,textRect);
                        int textStartX=mItemWight/2;
                        int textStartY=((shapeStartY)/2)-(int)((mTextPainter.descent() + mTextPainter.ascent()) / 2);
                        canvas.drawText(new char[]{ch},0,1,startX+textStartX,startY+textStartY,mTextPainter);

                    }

                    textPointer++;
                }
                startX+=mItemWight+mBetweenItemWight;
                position++;
            }

            startY+=getItemHeight()+getBetweenItemHeight();
        }

        mCanvas.restore();
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
        setSelection(super.getText().length());
    }



    private int getStartX(int columns){
        int itemsWight =columns*mItemWight+(columns-1)*mBetweenItemWight;
        int availableWight=getWidth()-getPaddingLeft()-getPaddingRight();

        int startInAvailable=0;

        if (mItemGravity==ITEM_GRAVITY_RIGHT){
            startInAvailable=availableWight- itemsWight;
        }else if (mItemGravity==ITEM_GRAVITY_CENTER){
            startInAvailable=availableWight/2-itemsWight/2;
        }else if (mItemGravity==ITEM_GRAVITY_LEFT){
            startInAvailable=0;
        }
        return startInAvailable+getPaddingLeft();
    }
    private ShapeDrawable getCharUnderLine(int wight,int height){
        float wightCon=(float)wight /2;
        float[] outer =new float[8];
        Arrays.fill(outer,wightCon);
        ShapeDrawable drawable=new ShapeDrawable(new RoundRectShape(outer,null,null));
        drawable.setIntrinsicHeight(height);
        drawable.setIntrinsicWidth(wight);
        return drawable;
    }

    private boolean needToSkip(int position){
        if (mSpace==null) return false;
        for(int index:mSpace){
            if (index==position) return true;
        }
        return false;
    }

    private int longestColumn(){
        int longest=0;
        for (int column:mRows){
            if (column>longest) longest=column;
        }
        return longest;
    }

    private int maxCharsValue(){
        int length=0;
        for (int column:mRows){
            length+=column;
        }
        for (int space:mSpace){
            length--;
        }
        return Math.max(length, 0);
    }

    public void setColumnsInRows(int[] rows){
        this.mRows=rows;
        limitCharsToNoOfFields();
        smtChanged = true;
        requestLayout();
    }

    public void setSpaceOnIndexes(int[] spaceIndex){
        this.mSpace=spaceIndex;
        limitCharsToNoOfFields();
        smtChanged = true;
        invalidate();
    }

    public int[] getColumnsInRows(){
        return this.mRows;
    }

    public int[] getSpaceOnIndexes(){
        return this.mSpace;
    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(@ColorInt int textColor) {
        mTextColorStates = ColorStateList.valueOf(textColor);
        invalidateColorStateStates();
        invalidate();
    }

    public float getTextSize() {
        return mTextSize;
    }

    public void setTextSize(int textSize) {
        this.mTextSize = textSize;
        mTextPainter.setTextSize(mTextSize);
        smtChanged = true;
        invalidate();
    }

    public int getItemHeight() {
        return mItemHeight;
    }

    public void setItemHeight(int itemHeight) {
        this.mItemHeight = dp2pix(itemHeight);
        requestLayout();
    }

    public int getItemWight() {
        return mItemWight;
    }

    public void setItemWight(int itemWight) {
        this.mItemWight = dp2pix(itemWight);
        smtChanged = true;
        requestLayout();
    }

    public int getCharUnderlineColor() {
        return mCharUnderlineColor;
    }

    public void setCharUnderlineColor(@ColorInt int charUnderlineColor) {
        mCharUnderlineColorStates = ColorStateList.valueOf(charUnderlineColor);
        invalidateColorStateStates();
        invalidate();
    }

    public int getCharUnderlineHeight() {
        return mCharUnderlineHeight;
    }

    public void setCharUnderlineHeight(int charUnderlineHeight) {
        this.mCharUnderlineHeight = dp2pix(charUnderlineHeight);
        smtChanged = true;
        invalidate();
    }

    public int getBlankCharUnderlineColor() {
        return mBlankCharUnderlineColor;
    }

    public void setBlankCharUnderlineColor(@ColorInt int blankCharUnderlineColor) {
        mBlankCharUnderlineColorStates = ColorStateList.valueOf(blankCharUnderlineColor);
        invalidateColorStateStates();
        invalidate();
    }

    public int getMaxRows() {
        return mMaxRows;
    }

    public void setMaxRows(int maxRows) {
        this.mMaxRows = maxRows;
        smtChanged = true;
        requestLayout();
    }

    public int getMaxColumns() {
        return mMaxColumns;
    }

    public void setMaxColumns(int maxColumns) {
        this.mMaxColumns = maxColumns;
        smtChanged = true;
        requestLayout();
    }

    public int getItemGravity() {
        return mItemGravity;
    }

    public void setItemGravity(int itemGravity) {
        this.mItemGravity = itemGravity;
        smtChanged = true;
        requestLayout();
    }

    public int getBetweenItemHeight() {
        return mBetweenItemHeight;
    }

    public void setBetweenItemHeight(int betweenItemHeight) {
        this.mBetweenItemHeight = dp2pix(betweenItemHeight);
        smtChanged = true;
        requestLayout();
    }

    public int getBetweenItemWight() {
        return mBetweenItemWight;
    }

    public void setBetweenItemWight(int betweenItemWight) {
        this.mBetweenItemWight = dp2pix(betweenItemWight);
        smtChanged = true;
        requestLayout();
    }

    public int getItemBackgroundColor() {
        return mItemBackgroundColor;
    }

    public void setItemBackgroundColor(@ColorInt int color) {
        mItemBackgroundColorStates = ColorStateList.valueOf( color);
        invalidateColorStateStates();
        invalidate();
    }

    public int getItemShadowColor() {
        return mItemShadowColor;
    }

    public void setItemShadowColor(@ColorInt int itemShadowColor) {
        mItemShadowColorStates = ColorStateList.valueOf(itemShadowColor);
        invalidateColorStateStates();
        invalidate();
    }

    public int getItemBackgroundRound() {
        return mItemBackgroundRound;
    }

    public void setItemBackgroundRound(int itemBackgroundRound) {
        this.mItemBackgroundRound = dp2pix(itemBackgroundRound);
    }

    public int getItemElevation() {
        return mItemElevation;
    }

    public void setItemElevation(int itemElevation) {
        this.mItemElevation = dp2pix(itemElevation);
    }

    public int getCharUnderlineBottomPadding() {
        return mCharUnderlineBottomPadding;
    }

    public void setCharUnderlineBottomPadding(int mCharUnderlineBottomPadding) {
        this.mCharUnderlineBottomPadding = dp2pix(mCharUnderlineBottomPadding);
        smtChanged = true;
        invalidate();
    }

    @Override
    public boolean onPreDraw() {
        return true;
    }

    @Override
    public void setFilters(InputFilter[] filters) {
        mFilters=filters;
        InputFilter[] newFilters;
        if (mLengthFilter!=null){
            if (mFilters!=null)
                newFilters = Arrays.copyOf(mFilters, mFilters.length + 1);
            else
                newFilters=new InputFilter[1];
            newFilters[newFilters.length-1]=mLengthFilter;
        }else{
            newFilters=mFilters;
        }
        super.setFilters(newFilters);
    }
    private void addLengthFilter(InputFilter filter){
        mLengthFilter=filter;
        setFilters(mFilters);
    }

    private void limitCharsToNoOfFields() {
        addLengthFilter(new InputFilter.LengthFilter(maxCharsValue()));
    }

    private int  dp2pix(int dp){
        return  (int)getResources().getDisplayMetrics().density*dp;
    }

}
