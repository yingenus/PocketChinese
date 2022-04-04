package com.yingenus.pocketchinese.view.activity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.yingenus.pocketchinese.R;
import com.yingenus.pocketchinese.controller.InPutUtilsKt;
import com.yingenus.pocketchinese.view.utils.KeyboardCallbackInterface;
import com.yingenus.pocketchinese.view.keyboard.PinyinPocketKeyboard;

public abstract class SingleFragmentActivityWithKeyboard extends AppCompatActivity implements KeyboardCallbackInterface, SingleFragmentActivityInterface {
    private static final int CONTAINER_ID=43256549;


    private FrameLayout mLayout;
    private PinyinPocketKeyboard mKeyboard;
    private FrameLayout mFragmentContainer;

    private boolean isKeyboardShow=false;
    private boolean mAdjustResize=false;

    @Override
    public void adjustResize(boolean b) {
        mAdjustResize=b;
    }

    @Override
    public void showKeyboard(View view) {
        InPutUtilsKt.showKeyboard(view);
    }

    @Override
    public void hideKeyboard(View view) {
        if (isKeyboardShow){
            hideAppKeyboard(view);
        }
        InPutUtilsKt.hideKeyboard(view);
    }

    @Override
    public void showAppKeyboard(View view) {
        hideKeyboard(view);
        if (view instanceof EditText){
            mKeyboard.setListener((EditText) view);
        }
        showKeyboardView();
    }

    @Override
    public void hideAppKeyboard(View view) {
        if (view instanceof EditText){
            mKeyboard.deleteListener((EditText) view);
        }
        hideKeyboardView();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewGroup.LayoutParams frameLayoutParams= new ViewGroup.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);

        mLayout=new FrameLayout(getApplicationContext());
        mLayout.setLayoutParams(frameLayoutParams);

        setContentView(mLayout);

        FrameLayout.LayoutParams containerParam=
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        containerParam.gravity=Gravity.TOP;
        mFragmentContainer=new FrameLayout(getApplicationContext());
        mFragmentContainer.setId(CONTAINER_ID);

        mLayout.addView(mFragmentContainer,containerParam);

        FrameLayout.LayoutParams keyboardParam=
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT );
        keyboardParam.gravity= Gravity.BOTTOM;
        mKeyboard= new PinyinPocketKeyboard(getApplicationContext());
        mKeyboard.setVisibility(View.GONE);

        mLayout.addView(mKeyboard,keyboardParam);

        FragmentManager fm=getSupportFragmentManager();
        fm.beginTransaction().
                add(CONTAINER_ID,createFragment())
                .commit();


        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null){
            setActionBar(toolbar);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        mKeyboard.setListener(null);
        hideKeyboardView();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        if (isKeyboardShow){
            hideKeyboardView();
        }else {
            super.onBackPressed();
        }
    }

    private void showKeyboardView(){
        mKeyboard.setVisibility(View.VISIBLE);
        isKeyboardShow=true;
    }

    private void hideKeyboardView(){
        mKeyboard.setVisibility(View.GONE);
        isKeyboardShow=false;
    }

}
