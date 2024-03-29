package com.yingenus.pocketchinese.view.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.yingenus.pocketchinese.R;
import com.yingenus.pocketchinese.view.activity.SingleFragmentActivityInterface;

public abstract class SingleFragmentActivityWithActionBar extends AppCompatActivity implements SingleFragmentActivityInterface {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        FragmentManager fragmentManager=getSupportFragmentManager();
        Fragment fragment=fragmentManager.findFragmentById(R.id.fragment_container);

        if(fragment==null){
            fragment=createFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container,fragment)
                    .commit();
        }
    }
}
