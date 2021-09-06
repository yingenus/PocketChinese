package com.yingenus.pocketchinese.controller.activity;

import androidx.fragment.app.Fragment;

import com.yingenus.pocketchinese.controller.fragment.DictionaryFragment;

public class DictionaryActivity extends SingleFragmentActivity {

    @Override
    public Fragment createFragment() {
        return new DictionaryFragment();
    }

}
