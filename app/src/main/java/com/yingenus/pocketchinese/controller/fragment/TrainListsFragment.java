package com.yingenus.pocketchinese.controller.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.yingenus.pocketchinese.R;
import com.yingenus.pocketchinese.controller.dialog.CreateNewListDialog;
import com.google.android.material.tabs.TabLayout;


public class TrainListsFragment extends Fragment {
    private static final String DIALOG_TAG="create_new_list";

    private static class TrainListFragmentFactory extends FragmentFactory{
        @NonNull
        @Override
        public Fragment instantiate(@NonNull ClassLoader classLoader, @NonNull String className) {
            if (className.equals(SuggestListsFragment.class.getName()))
                return new SuggestListsFragment();
            if (className.equals(UserListsFragment.class.getName()))
                return new UserListsFragment();

            return super.instantiate(classLoader, className);
        }
    }

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ImageButton imageButton;

    public TrainListsFragment(){
        super(R.layout.study_lists_fragment);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        getChildFragmentManager().setFragmentFactory(new TrainListFragmentFactory());
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        tabLayout = view.findViewById(R.id.study_lists_tab_layout);
        viewPager = view.findViewById(R.id.study_lists_view_pager);
        imageButton = view.findViewById(R.id.image_button);

        PagerAdapter pagerAdapter =new StudyListsPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        viewPager.setPageMargin((int)getResources().getDisplayMetrics().density*8);

        tabLayout.setupWithViewPager(viewPager,true);

        imageButton.setOnClickListener(this::onAddClicked);

        return view;
    }

    private void onAddClicked(View view){
        DialogFragment dialogFragment=new CreateNewListDialog();
        dialogFragment.show(getChildFragmentManager(),DIALOG_TAG);
    }

    private class StudyListsPagerAdapter extends FragmentStatePagerAdapter{

        public StudyListsPagerAdapter(@NonNull FragmentManager fm) {
            super(fm,FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0: {
                    return getString(R.string.learn_words_available);
                }
                case 1:{
                    return getString(R.string.learn_words_my);
                }
                default: return super.getPageTitle(position);
            }
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0: {
                    return getChildFragmentManager().getFragmentFactory()
                                .instantiate(getContext().getClassLoader(),SuggestListsFragment.class.getName());
                }
                case 1:{
                    return getChildFragmentManager().getFragmentFactory()
                                .instantiate(getContext().getClassLoader(),UserListsFragment.class.getName());
                }
                default: return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
