package com.yingenus.pocketchinese.controller.fragment;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yingenus.pocketchinese.R;
import com.yingenus.pocketchinese.controller.activity.SuggestWordsActivityKt;
import com.yingenus.pocketchinese.model.UtilsKt;
import com.yingenus.pocketchinese.model.words.suggestwords.JSONObjects;
import com.yingenus.pocketchinese.presenters.SuggestListPresenter;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SuggestListsFragment extends Fragment implements SuggestListsInterface,MaterialButtonToggleGroup.OnButtonCheckedListener, View.OnClickListener {

    private SuggestListPresenter presenter = new SuggestListPresenter(this);

    private CheckBox checkBox;
    private RecyclerView recycleView;
    private MaterialButtonToggleGroup sortToggleGroup;
    private View sortLayout;
    private View newFirstLayout;
    private View showTagsLayout;
    private ChipGroup tagsGroup;
    private View downIndicator;

    private Map<String, Boolean> tagsState;
    private boolean isTagsShowed = true;


    SuggestListsFragment(){
        super(R.layout.given_study_list_fragment);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= super.onCreateView(inflater,container,savedInstanceState);

        checkBox=view.findViewById(R.id.new_first_checkBox);
        sortToggleGroup = view.findViewById(R.id.sort_toggle_group);
        recycleView =view.findViewById(R.id.expanded_recyclerview);

        sortLayout = view.findViewById(R.id.sort_layout);
        newFirstLayout = view.findViewById(R.id.new_first_layout);
        showTagsLayout = view.findViewById(R.id.show_tags);
        downIndicator = view.findViewById(R.id.dropdown_icon);

        tagsGroup = view.findViewById(R.id.tags_group);

        sortLayout.setOnClickListener(this);
        newFirstLayout.setOnClickListener(this);
        showTagsLayout.setOnClickListener(this);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                newFirst(isChecked);
            }
        });

        sortToggleGroup.check(R.id.az);
        sortToggleGroup.addOnButtonCheckedListener(this);

        recycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        recycleView.addItemDecoration(new BoundsDecorator());

        presenter.onCreate(getActivity());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.onResume(getContext());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (presenter != null){
            presenter.onDestroy();
        }
        presenter = null;
    }

    @Override
    public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
        if (group == sortToggleGroup && isChecked){
            if (checkedId == R.id.az)
                updateSortType(SortType.AZ);
            else if (checkedId == R.id.za)
                updateSortType(SortType.ZA);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == sortLayout){
            int selected = sortToggleGroup.getCheckedButtonId();
            if (selected == R.id.az){
                sortToggleGroup.check(R.id.za);
            }
            else if (selected == R.id.za){
                sortToggleGroup.check(R.id.az);
            }
            return;
        }
        if (v == newFirstLayout){
            boolean state = checkBox.isChecked();
            checkBox.setChecked(!state);
            return;
        }
        if (v == showTagsLayout){
            isTagsShowed = !isTagsShowed;
            statRotationAnimation(downIndicator,!isTagsShowed);
            statExpandTagsAnimation(!isTagsShowed);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.onDestroy();
        presenter = null;
    }

    @Override
    public void setItems(@NotNull List<Item> items) {
        updateAdapter(items);
    }

    @Override
    public void updateItems(@NotNull List<Item> items) {
        if (!items.isEmpty())
            recycleView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void setTags(@NotNull List<String> tags) {
        initTags(tags);
    }

    @Override
    public void newFires(boolean itIs) {
        checkBox.setChecked(itIs);
    }

    @Override
    public void setSortType(@NotNull SuggestListsInterface.SortType type) {
        if (type == SortType.AZ){
            sortToggleGroup.check(R.id.za);
        }
        else if (type == SortType.ZA){
            sortToggleGroup.check(R.id.az);
        }
    }

    @Override
    public void showWordsList(@NotNull JSONObjects.FileInfo item) {
        Intent intent = SuggestWordsActivityKt.getSuggestActivityIntent(getContext(),item);
        startActivity(intent);
    }

    private void onHolderClicked(Item item){
        presenter.onItemClicked(item.getWordsList());
    }

    private void initTags(List<String> tags){
        tagsGroup.removeAllViews();
        tagsState = new HashMap<>();
        for (String tag : tags){
            int id = View.generateViewId();
            tagsState.put(tag,true);
            Chip chip = (Chip) getLayoutInflater().inflate(R.layout.cat_chip_group_item_choice, tagsGroup, false);
            chip.setText(tag);
            chip.setId(id);
            tagsGroup.addView(chip);
            tagsGroup.check(id);
            chip.setOnCheckedChangeListener(this::onChipCheckChanged);
        }
    }

    private void onChipCheckChanged(CompoundButton buttonView, boolean isChecked){
        List<String> activeTags = new ArrayList<>();
        if(tagsState != null ){
            if (tagsState.containsKey(buttonView.getText().toString())){
                tagsState.put(buttonView.getText().toString(), isChecked);
            }
            for (String key : tagsState.keySet()){
                if (tagsState.get(key)){
                    activeTags.add(key);
                }
            }
        }
        presenter.selectedTagsRangeChanged(activeTags);
    }

    private void updateSortType(SortType sortType){
        presenter.onSortTypeUpdate(sortType);
    }
    private void newFirst(boolean value){
        presenter.onNewFirstChanged(value);
    }

    private void statRotationAnimation(View view, boolean up){
            int from = (up)? 180 : 0;
            int to = (up)? 0 : 180;

            RotateAnimation animation = new RotateAnimation(from, to,view.getWidth()/2, view.getHeight()/2);
            animation.setFillAfter(true);
            animation.setDuration(250);
            view.startAnimation(animation);
    }

    private void statExpandTagsAnimation(boolean up){
        int initHeight = tagsGroup.getHeight();
        int finalHeight;

        if (up){
            finalHeight = 0;
        }else {
            int measureSpeckW = View.MeasureSpec.makeMeasureSpec(tagsGroup.getWidth(), View.MeasureSpec.EXACTLY);
            int measureSpeckH = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

            tagsGroup.measure(measureSpeckW, measureSpeckH);

            finalHeight = tagsGroup.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = tagsGroup.getLayoutParams();

        ValueAnimator animator = ValueAnimator.ofInt(initHeight, finalHeight);

        animator.setDuration(250);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                params.height =(int) animation.getAnimatedValue();
                tagsGroup.requestLayout();
                tagsGroup.invalidate();
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!up){
                    params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    tagsGroup.requestLayout();
                    tagsGroup.invalidate();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();

    }

    private void updateAdapter(@NonNull List<Item> list){
        if (recycleView.getAdapter() ==null) {
            SuggestAdapter adapter = new SuggestAdapter(list);
            adapter.setListener(this::onHolderClicked);
            recycleView.setAdapter(adapter);
        }
        else {
            ((SuggestAdapter) recycleView.getAdapter()).setList(list);
            recycleView.getAdapter().notifyDataSetChanged();
        }

    }

    private static class SuggestAdapter extends RecyclerView.Adapter<SuggestViewHolder>{
        private List<Item> list;

        private OnItemsClicked mListener;

        SuggestAdapter(List<Item> list){
            this.list=list;
        }

        @NonNull
        @Override
        public SuggestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            SuggestViewHolder holder = new SuggestViewHolder((LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE),parent);
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && list.size() > position){
                        if (mListener != null){
                            mListener.onItemsClicked(list.get(position));
                        }
                    }
                }
            });

            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull SuggestViewHolder holder, int position) {
            Item pair=list.get(position);
            holder.bind(pair.getWordsList(),pair.isNew());
        }

        @Override
        public int getItemCount() {
            return list.size();
        }


        public void setListener(OnItemsClicked listener) {
            this.mListener = listener;
        }

        public void setList(List<Item> list) {
            this.list = list;
        }

        public List<Item> getList() {
            return list;
        }

        @Override
        public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
            super.onDetachedFromRecyclerView(recyclerView);
            mListener = null;
        }
    }
    private static class SuggestViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final TextView words;
        private final TextView version;
        private final View newView;
        private final ImageView imageView;

        private JSONObjects.FileInfo mInfo;


        SuggestViewHolder(LayoutInflater inflater,ViewGroup viewGroup){
            super(inflater.inflate(R.layout.holder_suggest_list,viewGroup,false));

            name = super.itemView.findViewById(R.id.suggest_list_name);
            words = super.itemView.findViewById(R.id.suggest_list_words);
            version = super.itemView.findViewById(R.id.suggest_list_version);
            newView = super.itemView.findViewById(R.id.suggest_list_new);
            imageView = super.itemView.findViewById(R.id.image_view);

        }

        public void bind(JSONObjects.FileInfo fInfo, boolean isNew){
            this.mInfo = fInfo;
            name.setText(fInfo.getName());
            words.setText(buildWords(fInfo));
            version.setText(buildVersion(fInfo));
            newView.setActivated(isNew);

            bindImage(super.itemView.getContext());

        }

        private void bindImage(Context context){
            Bitmap bitmap = UtilsKt.getBitmapFromAssets(context, UtilsKt.imageDir+"/"+mInfo.getImage());
            imageView.setImageBitmap(bitmap);
        }

        private String buildWords(JSONObjects.FileInfo givenList){
            return  super.itemView.getContext().getString(R.string.count_words)+ givenList.getWordsSize();
        }
        private String buildVersion(JSONObjects.FileInfo givenList){
            return "v"+ givenList.getVersion();
        }
    }

    private interface OnItemsClicked{
        void onItemsClicked(Item item);
    }

    private static class BoundsDecorator extends RecyclerView.ItemDecoration{
        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {

            if (parent.getAdapter().getItemCount()-1 == parent.getChildAdapterPosition(view)){
                outRect.bottom += 200;
            }
        }
    }
}
