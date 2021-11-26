package com.yingenus.pocketchinese.controller.activity;

import androidx.fragment.app.Fragment;

import com.yingenus.pocketchinese.di.ServiceLocator;
import com.yingenus.pocketchinese.domain.repository.ChinCharRepository;
import com.yingenus.pocketchinese.domain.repository.ExampleRepository;
import com.yingenus.pocketchinese.domain.repository.ToneRepository;
import com.yingenus.pocketchinese.presentation.views.dictionary.DictionaryFragment;

public class DictionaryActivity extends SingleFragmentActivity {

    @Override
    public Fragment createFragment() {
        return new DictionaryFragment(
                ServiceLocator.INSTANCE.get(getApplicationContext(), ChinCharRepository.class.getName()),
                ServiceLocator.INSTANCE.get(getApplicationContext(), ExampleRepository.class.getName()),
                ServiceLocator.INSTANCE.get(getApplicationContext(), ToneRepository.class.getName())
        );
    }

}
