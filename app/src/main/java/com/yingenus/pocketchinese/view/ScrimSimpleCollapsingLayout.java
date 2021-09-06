package com.yingenus.pocketchinese.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewParent;
import android.widget.FrameLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

import com.yingenus.pocketchinese.R;
import com.google.android.material.appbar.AppBarLayout;

public class ScrimSimpleCollapsingLayout extends FrameLayout {

    private OffsetChangedListener mOffsetChangedListener;


    private int scrimAlpha=0x00;
    private Drawable scrimDrawable=null;

    public ScrimSimpleCollapsingLayout(@NonNull Context context) {
        super(context);
        setWillNotDraw(false);
    }

    public ScrimSimpleCollapsingLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ScrimSimpleCollapsingLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public ScrimSimpleCollapsingLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs){
        TypedArray array=getContext().getTheme()
                .obtainStyledAttributes(attrs, com.google.android.material.R.styleable.CollapsingToolbarLayout,0,0);
        try {
            setScrimDrawable(array.getDrawable(com.google.android.material.R.styleable.CollapsingToolbarLayout_contentScrim));
        }finally {
            array.recycle();
        }

        setWillNotDraw(false);
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        ViewParent parent =getParent();
        if (parent instanceof AppBarLayout){

            ViewCompat.setFitsSystemWindows(this, ViewCompat.getFitsSystemWindows((View) parent));

            if (mOffsetChangedListener==null){
                mOffsetChangedListener=new OffsetChangedListener();
            }
            ((AppBarLayout)parent).addOnOffsetChangedListener(mOffsetChangedListener);

            ViewCompat.requestApplyInsets(this);
        }

    }

    @Override
    protected void onDetachedFromWindow() {
        final ViewParent parent = getParent();
        if (mOffsetChangedListener != null && parent instanceof AppBarLayout) {
            ((AppBarLayout) parent).removeOnOffsetChangedListener(mOffsetChangedListener);
        }
        super.onDetachedFromWindow();

    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        if (scrimDrawable != null && scrimAlpha > 0) {
            scrimDrawable.setAlpha(scrimAlpha);
            scrimDrawable.draw(canvas);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        for (int i = 0, z = getChildCount(); i < z; i++) {
            getViewOffsetHelper(getChildAt(i)).onViewLayout();
        }

        for (int i = 0, z = getChildCount(); i < z; i++) {
            getViewOffsetHelper(getChildAt(i)).applyOffsets();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (scrimDrawable != null) {
            scrimDrawable.setBounds(0, 0, w, h);
        }
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();

        int[] states=getDrawableState();
        boolean isChanged=false;

        if (scrimDrawable!=null &&scrimDrawable.isStateful()){
            isChanged|=scrimDrawable.setState(states);
        }
        if (isChanged){
            invalidate();
        }
    }

    @Override
    protected boolean verifyDrawable(@NonNull Drawable who) {
        return super.verifyDrawable(who) || who==scrimDrawable;
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);

        boolean visible = ( visibility == VISIBLE );
        if (scrimDrawable != null && scrimDrawable.isVisible() != visible) {
            scrimDrawable.setVisible(visible, false);
        }

    }

    @NonNull
    static ViewOffsetHelper getViewOffsetHelper(@NonNull View view) {
        ViewOffsetHelper offsetHelper = (ViewOffsetHelper) view.getTag(R.id.view_offset_helper);
        if (offsetHelper == null) {
            offsetHelper = new ViewOffsetHelper(view);
            view.setTag(R.id.view_offset_helper, offsetHelper);
        }
        return offsetHelper;
    }

    public void setScrimAlpha(int scrimAlpha) {
        this.scrimAlpha = scrimAlpha;
    }

    public void setScrimDrawable(Drawable scrimDrawable) {
        Drawable drawable=null;
        if (scrimDrawable!=null){
            drawable=scrimDrawable.mutate();

            if (drawable.isStateful())
                drawable.setState(getDrawableState());

            drawable.setVisible(getVisibility()==View.VISIBLE,false);
            drawable.setCallback(this);
            drawable.setAlpha(scrimAlpha);
        }
        this.scrimDrawable = drawable;
    }
    public void setScrimDrawableRes(@DrawableRes int scrimDrawableRes) {
        setScrimDrawable(getResources().getDrawable(scrimDrawableRes));
    }
    public void setScrimDrawableColorRes(@ColorRes int scrimColorRes) {
        setScrimDrawableColor(getResources().getColor(scrimColorRes));
    }
    public void setScrimDrawableColor(@ColorInt int scrimColor) {
        setScrimDrawable(new ColorDrawable(scrimColor));
    }

    private class OffsetChangedListener implements AppBarLayout.OnOffsetChangedListener{
        @Override
        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

            int scrollRange=appBarLayout.getTotalScrollRange();
            if (scrimDrawable!=null && scrollRange!=0){
                scrimAlpha= 0xFF *Math.abs(verticalOffset)/scrollRange;
            }

            for (int i = 0, z = getChildCount(); i < z; i++) {
                final View child = getChildAt(i);

                final ViewOffsetHelper offsetHelper = getViewOffsetHelper(child);

                offsetHelper.setTopAndBottomOffset(Math.round(-verticalOffset ));


            }
            invalidate();
        }
    }

    protected static class ViewOffsetHelper {

        private final View view;

        private int layoutTop;
        private int layoutLeft;
        private int offsetTop;
        private int offsetLeft;
        private boolean verticalOffsetEnabled = true;
        private boolean horizontalOffsetEnabled = true;

        public ViewOffsetHelper(View view) {
            this.view = view;
        }

        void onViewLayout() {
            // Grab the original top and left
            layoutTop = view.getTop();
            layoutLeft = view.getLeft();
        }

        void applyOffsets() {
            ViewCompat.offsetTopAndBottom(view, offsetTop - (view.getTop() - layoutTop));
            ViewCompat.offsetLeftAndRight(view, offsetLeft - (view.getLeft() - layoutLeft));
        }

        /**
         * Set the top and bottom offset for this {@link ViewOffsetHelper}'s view.
         *
         * @param offset the offset in px.
         * @return true if the offset has changed
         */
        public boolean setTopAndBottomOffset(int offset) {
            if (verticalOffsetEnabled && offsetTop != offset) {
                offsetTop = offset;
                applyOffsets();
                return true;
            }
            return false;
        }

        /**
         * Set the left and right offset for this {@link ViewOffsetHelper}'s view.
         *
         * @param offset the offset in px.
         * @return true if the offset has changed
         */
        public boolean setLeftAndRightOffset(int offset) {
            if (horizontalOffsetEnabled && offsetLeft != offset) {
                offsetLeft = offset;
                applyOffsets();
                return true;
            }
            return false;
        }

        public int getTopAndBottomOffset() {
            return offsetTop;
        }

        public int getLeftAndRightOffset() {
            return offsetLeft;
        }

        public int getLayoutTop() {
            return layoutTop;
        }

        public int getLayoutLeft() {
            return layoutLeft;
        }

        public void setVerticalOffsetEnabled(boolean verticalOffsetEnabled) {
            this.verticalOffsetEnabled = verticalOffsetEnabled;
        }

        public boolean isVerticalOffsetEnabled() {
            return verticalOffsetEnabled;
        }

        public void setHorizontalOffsetEnabled(boolean horizontalOffsetEnabled) {
            this.horizontalOffsetEnabled = horizontalOffsetEnabled;
        }

        public boolean isHorizontalOffsetEnabled() {
            return horizontalOffsetEnabled;
        }
    }
}
