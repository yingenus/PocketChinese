package com.yingenus.pocketchinese.presentation.views;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.yingenus.pocketchinese.R;
import com.yingenus.pocketchinese.PocketApplication;
import com.yingenus.pocketchinese.domain.entitiys.UtilsVariantParams;
import com.yingenus.pocketchinese.presentation.views.dictionary.DictionaryFragment;
import com.yingenus.pocketchinese.presentation.views.suggestist.SuggestListsFragment;
import com.yingenus.pocketchinese.presentation.views.userlist.UserListsFragment;
import com.yingenus.pocketchinese.view.EmptyFragment;
import com.yingenus.pocketchinese.presentation.views.settings.SettingsFragment;
import com.yingenus.pocketchinese.view.bubblecust.BubbleTabBarCust;

public class MainActivity  extends AppCompatActivity {

    static class MainFragmentFactory  extends FragmentFactory{

        public MainFragmentFactory(){
            super();
        }
        @NonNull
        @Override
        public Fragment instantiate(@NonNull ClassLoader classLoader, @NonNull String className) {
            if (className.equals(DictionaryFragment.class.getName())) {
                Log.d("MainFragmentFactory","create Fragment: DictionaryFragment");
                return new DictionaryFragment();
            }
            if (className.equals(SuggestListsFragment.class.getName())) {
                Log.d("MainFragmentFactory","create Fragment: TrainListsFragment");
                return new SuggestListsFragment();
            }
            if (className.equals(UserListsFragment.class.getName())) {
                Log.d("MainFragmentFactory","create Fragment: EmptyFragment");
                return new UserListsFragment();
            }
            if (className.equals(SettingsFragment.class.getName())) {
                Log.d("MainFragmentFactory","create Fragment: SettingsFragment");
                return new SettingsFragment();
            }
            return super.instantiate(classLoader, className);
        }
    }


    private final static String SEARCH_F_TAG="serchf";
    private final static String STUDY_F_TAG="studyf";
    private final static String GRAMMAR_F_TAG="grammarf";
    private final static String SETTINGS_F_TAG="settingsf";
    private final static String LAST_ACTIVE_FRAGMENT = "lastActiveFragment";

    private String active;

    private BubbleTabBarCust buttonNavigationMenu;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        SplashScreen splashScreen= SplashScreen.installSplashScreen(this);

        PocketApplication.Companion.setupApplication();

        getSupportFragmentManager().setFragmentFactory(
                new MainFragmentFactory());

        super.onCreate(savedInstanceState);

        PocketApplication.Companion.postStartActivity(this,true);
        setContentView(R.layout.main_activity);

        buttonNavigationMenu=findViewById(R.id.main_bubble_tab_bar);

        if (savedInstanceState != null && savedInstanceState.containsKey(LAST_ACTIVE_FRAGMENT)){
            String lastTAG = savedInstanceState.getString(LAST_ACTIVE_FRAGMENT);
            showFragment(lastTAG);
            active = lastTAG;
        }else {
            showFragment(SEARCH_F_TAG);
            active = SEARCH_F_TAG;
        }

        int bubbleId = getBubbleId(active);
        buttonNavigationMenu.setSelectedWithId(bubbleId,false);

        buttonNavigationMenu.addBubbleListener(this::onBubbleClick);
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(LAST_ACTIVE_FRAGMENT,active);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        finishAfterTransition();
    }

    private void onBubbleClick(int i){
        switch (i){
            case R.id.search: {
                showFragment(SEARCH_F_TAG);
                break;
            }
            case R.id.study:{
                showFragment(STUDY_F_TAG);
                break;
            }

            case R.id.suggest: {
                showFragment(GRAMMAR_F_TAG);
                break;
            }
            case R.id.settings:{
                showFragment(SETTINGS_F_TAG);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        buttonNavigationMenu.addBubbleListener(null);
    }

    private void showFragment(String TAG){
        if (TAG.equals(active)){
            return;
        }
        if (TAG.equals(GRAMMAR_F_TAG) || TAG.equals(STUDY_F_TAG)){
            getWindow().setStatusBarColor(UtilsVariantParams.INSTANCE.resolveColorAttr(this,R.attr.colorSurface));
        }else {
            getWindow().setStatusBarColor(UtilsVariantParams.INSTANCE.resolveColorAttr(this,R.attr.colorPrimaryVariant));
        }
        Fragment targetFragment = getSupportFragmentManager().findFragmentByTag(TAG);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        for (Fragment fragment : getSupportFragmentManager().getFragments()){
            transaction.hide(fragment);
        }
        if (targetFragment == null){
            targetFragment = getFragment(TAG);
            transaction.add(R.id.main_layout,targetFragment,TAG);
        }else {
            transaction.show(targetFragment);
        }
        active = TAG;
        transaction.commit();
    }

    private int getBubbleId(String tag){
        switch (tag){
            case SEARCH_F_TAG: {
                return R.id.search;
            }
            case STUDY_F_TAG:{
                return R.id.study;
            }
            /**
            case GRAMMAR_F_TAG: {
                return R.id.grammar;
            }*/
            case SETTINGS_F_TAG:{
                return R.id.settings;
            }
            default:{
                throw new IllegalArgumentException("Illegal tag");
            }
        }

    }

    private Fragment getFragment(String TAG){
        FragmentFactory ff = getSupportFragmentManager().getFragmentFactory();
        if (TAG.equals(SEARCH_F_TAG))
            return ff.instantiate(getClassLoader(),DictionaryFragment.class.getName());
        if (TAG.equals(STUDY_F_TAG))
            return ff.instantiate(getClassLoader(), SuggestListsFragment.class.getName());
        if (TAG.equals(GRAMMAR_F_TAG))
            return ff.instantiate(getClassLoader(), UserListsFragment.class.getName());
        if (TAG.equals(SETTINGS_F_TAG))
            return ff.instantiate(getClassLoader(),SettingsFragment.class.getName());
        throw new RuntimeException("invalid tag:"+TAG);
    }
}
