package com.yingenus.pocketchinese.presentation.views.dictionary;

import androidx.fragment.app.Fragment;

import com.yingenus.pocketchinese.view.activity.SingleFragmentActivity;

public class DictionaryActivity extends SingleFragmentActivity {

    @Override
    public Fragment createFragment() {
        return new DictionaryFragment();
    }

}
