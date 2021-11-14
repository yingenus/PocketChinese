package com.yingenus.pocketchinese.controller.activity;

import androidx.fragment.app.Fragment;

import com.yingenus.pocketchinese.presentation.views.dictionary.DictionaryFragment;

public class DictionaryActivity extends SingleFragmentActivity {

    @Override
    public Fragment createFragment() {
        return new DictionaryFragment();
    }

}
