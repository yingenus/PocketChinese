package com.yingenus.pocketchinese.presentation.views.suggestist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.ItemKeyProvider;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.RecyclerView;

import com.yingenus.pocketchinese.R;
import com.yingenus.pocketchinese.data.json.suggest.JSONObjects;
import com.yingenus.pocketchinese.domain.dto.Example;
import com.yingenus.pocketchinese.domain.dto.SuggestWord;
import com.yingenus.pocketchinese.domain.dto.SuggestWordGroup;
import com.yingenus.pocketchinese.view.holders.BlockAdapter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SuggestWordsAdapter extends BlockAdapter<SuggestWordsAdapter.BlockHeaderHolder, SuggestWordsAdapter.SuggestItemHolder> {

    private List<SuggestWordGroup> mGroups;
    private boolean[] expandBlocks;

    private SelectionTracker<Long> selectionTracker;

    private final View.OnClickListener toBlockListener;
    private final View.OnClickListener toItemListener;

    private View.OnClickListener mItemListener = null;
    private View.OnClickListener mBlockListener = null;

    private boolean isSelectable=false;

    public SuggestWordsAdapter(){
        super();
        toBlockListener = (View view) ->{
            if (mBlockListener != null){
                mBlockListener.onClick(view);
            }
        };
        toItemListener = (View view) ->{
            if (mItemListener != null){
                mItemListener.onClick(view);
            }
        };
    }

    public void setGroups(List<SuggestWordGroup> groups){
        mGroups = groups;

        expandBlocks = new boolean[mGroups.size()];
        Arrays.fill(expandBlocks, true);

        List<List<?>> items = new ArrayList<>();

        for (SuggestWordGroup group : groups){
            items.add(group.getWords());
        }

        super.setItems(items);
    }

    @Override
    public void setItems(List<List<?>> items) {

    }

    public void setSelectionTracker(@Nullable SelectionTracker<Long> selectionTracker) {
        this.selectionTracker = selectionTracker;
        if (selectionTracker != null)
            this.selectionTracker.addObserver(new SelectionObserver());
    }

    public void isExpandedBlock(int blockNumber, boolean isExpand){
        if (expandBlocks != null && expandBlocks.length == mGroups.size()){
            if (blockNumber >= 0  && blockNumber<expandBlocks.length){
                expandBlocks[blockNumber] = isExpand;

                List<List<?>> items = new ArrayList<>();

                for (int i = 0; i < mGroups.size(); i ++){

                    if (expandBlocks[i]){
                        items.add(mGroups.get(i).getWords());
                    }else {
                        items.add(new ArrayList<>());
                    }
                }
                super.setItems(items);
            }
        }
    }

    public void setBlockListener(View.OnClickListener listener){
        mBlockListener = listener;
    }
    public void setItemListener(View.OnClickListener listener){
        mItemListener = listener;
    }

    public void selectionStart(){
        isSelectable=true;
    }
    public void selectionEnd(){
        isSelectable=false;
    }

    @Override
    public BlockHeaderHolder onCreateBlockHolder(@NonNull ViewGroup parent) {
        BlockHeaderHolder holder = new BlockHeaderHolder(getInflater(parent),parent);
        holder.setOnClickListener(toBlockListener);
        return holder;
    }

    @Override
    public SuggestItemHolder onCreateItemHolder(@NonNull ViewGroup parent) {
        SuggestItemHolder holder = new SuggestItemHolder(getInflater(parent),parent);
        holder.setOnClickListener(toItemListener);
        return holder;
    }

    @Override
    public void onBindBlockHolder(BlockHeaderHolder holder, int blockNumber) {
        boolean isExpand = (expandBlocks == null) || expandBlocks[blockNumber];
        holder.bind(mGroups.get(blockNumber).getName(),getRealBlockPosition(blockNumber), isExpand,selectionTracker,this);
    }

    @Override
    public void onBindItemHolder(SuggestItemHolder holder, int blockNumber, int inBlockPosition) {
        holder.bind(mGroups.get(blockNumber).getWords().get(inBlockPosition),getRealItemPosition(blockNumber,inBlockPosition),selectionTracker,this);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        selectionTracker.clearSelection();
        selectionTracker = null;
        mItemListener = null;
        mBlockListener = null;
    }

    public SuggestWord[] getSelectedWords(){
        if (selectionTracker != null && selectionTracker.getSelection().size() != 0){
            ArrayList<SuggestWord> words = new ArrayList<>();

            int itemCount = getItemCount();
            for(int position = 0; position <itemCount; position++){
                if (!calcHelper.isBLock(position) && selectionTracker.isSelected((long) position)){
                    int blockIndex = calcHelper.whichBlock(position);
                    int itemIndex = calcHelper.getItemInBlock(position);

                    words.add(mGroups.get(blockIndex).getWords().get(itemIndex));
                }
                if (calcHelper.isBLock(position) && selectionTracker.isSelected((long) position)){
                    int index = calcHelper.whichBlock(position);
                    if (!expandBlocks[index]){
                        words.addAll(mGroups.get(index).getWords());
                    }
                }
            }
            return words.toArray(new SuggestWord[0]);
        }
        return new SuggestWord[0];
    }


    private LayoutInflater getInflater(ViewGroup viewGroup){
        return  (LayoutInflater) viewGroup.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    public static abstract class SuggestWordsAdapterHolder extends RecyclerView.ViewHolder{

        protected WeakReference<View.OnClickListener> mListenerRef = new WeakReference<>(null);

        public SuggestWordsAdapterHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void setOnClickListener(View.OnClickListener listener){
            mListenerRef = new WeakReference<>(listener);
        }

    }

    public static class SuggestItemHolder extends SuggestWordsAdapterHolder implements View.OnClickListener{
        private SuggestWord mWord;
        private final SuggestDetails details;

        private final TextView chnText;
        private final TextView pinyinText;
        private final TextView trnText;
        private final CheckBox selectedBox;
        private final TextView counter;
        private final View extraIndicator;
        private final View examplesIndicator;

        public SuggestItemHolder(LayoutInflater layoutInflater, ViewGroup parent) {
            super(layoutInflater.inflate(R.layout.holder_suggestwords_item,parent,
                    false));

            itemView.setOnClickListener(this);

            chnText = super.itemView.findViewById(R.id.dictionary_item_chin_text);
            pinyinText = super.itemView.findViewById(R.id.dictionary_item_pinyin);
            trnText = super.itemView.findViewById(R.id.dictionary_item_second_language);
            selectedBox = super.itemView.findViewById(R.id.selected_box);
            examplesIndicator = super.itemView.findViewById(R.id.examples_icon);
            extraIndicator = super.itemView.findViewById(R.id.extra_icon);
            counter = super.itemView.findViewById(R.id.counter);
            counter.setVisibility(View.VISIBLE);

            details=new SuggestDetails();
        }

        public void bind(SuggestWord word, int position, SelectionTracker<Long> tracker,SuggestWordsAdapter adapter){

            mWord= word;
            chnText.setText(mWord.getWord());
            pinyinText.setText(mWord.getPinyin());
            trnText.setText(mWord.getTranslation());
            counter.setText(Integer.toString(adapter.calcHelper.getItemInBlock(position)+1));

            details.position=position;

            if (tracker!=null){
                bindSelection(tracker,adapter.isSelectable);
            }

            List<Example> examples = word.getExamples();

            if (examples != null && !examples.isEmpty()){
                examplesIndicator.setVisibility(View.VISIBLE);
            }else {
                examplesIndicator.setVisibility(View.GONE);
            }

        }

        public void bindSelection(SelectionTracker<Long> tracker, boolean isSelectable){
            boolean isChecked = tracker.isSelected(details.getSelectionKey());
            int visibility=(isSelectable)? View.VISIBLE : View.GONE;
            selectedBox.setVisibility(visibility);
            selectedBox.setChecked(isChecked);
        }

        @Override
        public void onClick(View v) {
            if (mListenerRef.get() != null){
                mListenerRef.get().onClick(v);
            }
        }

        public SuggestDetails getDetails() {
            return details;
        }

        public SuggestWord getWord() {
            return mWord;
        }
    }

    public static class BlockHeaderHolder extends SuggestWordsAdapterHolder implements View.OnClickListener {
        private final SuggestDetails details;

        private boolean isDown;

        private final TextView textView;
        private final CheckBox selectedBox;
        private final View dropDownIcon;

        public BlockHeaderHolder(LayoutInflater layoutInflater, ViewGroup parent) {
            super(layoutInflater.inflate(R.layout.holder_words_header,parent,
                    false));
            itemView.setOnClickListener(this);

            textView = super.itemView.findViewById(R.id.holder_block_name);
            selectedBox = super.itemView.findViewById(R.id.selected_box);
            dropDownIcon = super.itemView.findViewById(R.id.dropdown_icon);

            details=new SuggestDetails();
        }

        public void bind(String name, int position,boolean down,SelectionTracker<Long> tracker,SuggestWordsAdapter adapter){

            this.isDown = down;

            textView.setText(name);

            details.position=position;

            confDropDown();

            if (tracker!=null){
                bindSelection(tracker,adapter.isSelectable);
            }
        }

        @Override
        public void onClick(View v) {
            if (mListenerRef.get() != null){
                startAnimation();
                isDown = !isDown;
                mListenerRef.get().onClick(v);
            }
        }

        public void bindSelection(SelectionTracker<Long> tracker, boolean isSelectable){
            boolean isChecked=tracker.isSelected(details.getSelectionKey());
            int visibility=(isSelectable)? View.VISIBLE : View.GONE;
            selectedBox.setVisibility(visibility);
            selectedBox.setChecked(isChecked);

            int dropIconVisibility = (isSelectable)? View.GONE :View.VISIBLE;
            dropDownIcon.setVisibility(dropIconVisibility);
        }

        public SuggestDetails getDetails() {
            return details;
        }

        private void confDropDown(){
            if (isDown){
                dropDownIcon.setRotation(0);
            }else {
                dropDownIcon.setRotation(180);
            }
        }

        private void startAnimation(){
            if(dropDownIcon.getVisibility() == View.VISIBLE){

                int from = (isDown)? 180 : 0;
                int to = (isDown)? 0 : 180;

                RotateAnimation animation = new RotateAnimation(from, to);
                animation.setDuration(500);
                dropDownIcon.startAnimation(animation);

            }
        }
    }

    private class SelectionObserver extends SelectionTracker.SelectionObserver<Long>{
        private boolean blockReaction = false;

        @Override
        public void onItemStateChanged(@NonNull Long key, boolean selected) {
            int position = key.intValue();

            if (calcHelper.isBLock(position) ) {

                    if (blockReaction) {
                        blockReaction = false;
                        return;
                    }
                    int block = calcHelper.blockIndex(position);
                    int lastChild = calcHelper.getLastKeyPosition(block) - 1;

                    Long[] keys = new Long[lastChild - position];

                    for (int i = key.intValue() + 1; i <= lastChild; i++) {
                        keys[i - 1 - position] = (long) i;
                    }

                    selectionTracker.setItemsSelected(Arrays.asList(keys), selected);

            }else {

                    int block = calcHelper.whichBlock(position);
                    int blockPosition = calcHelper.getBlockPosition()[block];
                    int lastChild = calcHelper.getLastKeyPosition(block) - 1;

                    boolean stats = true;
                    for (int pos = blockPosition + 1; pos <= lastChild; pos++) {
                        boolean select = selectionTracker.isSelected((long) pos);
                        stats &= select;
                    }

                    if (selectionTracker.isSelected((long) blockPosition) != stats)
                        blockReaction = true;
                    selectionTracker.setItemsSelected(Collections.singleton((long) blockPosition), stats);

            }

        }
    }

    public static class SuggestDetails extends ItemDetailsLookup.ItemDetails<Long>{
        long position;
        boolean isItem;

        @Override
        public int getPosition() {
            return (int) position;
        }

        @Nullable
        @Override
        public Long getSelectionKey() {
            return position;
        }

        @Override
        public boolean inSelectionHotspot(@NonNull MotionEvent e) {
            return false;
        }

        @Override
        public boolean inDragRegion(@NonNull MotionEvent e) {
            return true;
        }

        public boolean isItem(){
            return isItem;
        }

    }


    public static class SuggestDetailsLookUp extends ItemDetailsLookup<Long> {

        private final RecyclerView recyclerView;

        public SuggestDetailsLookUp(RecyclerView recyclerView) {
            super();
            this.recyclerView=recyclerView;
        }

        @Nullable
        @Override
        public ItemDetails<Long> getItemDetails(@NonNull MotionEvent e) {
            View view =recyclerView.findChildViewUnder(e.getX(),e.getY());
            if (view!=null){
                RecyclerView.ViewHolder viewHolder=recyclerView.getChildViewHolder(view);
                if (viewHolder instanceof SuggestItemHolder){
                    return ((SuggestItemHolder)viewHolder).getDetails();
                }
                if (viewHolder instanceof BlockHeaderHolder){
                    return ((BlockHeaderHolder)viewHolder).getDetails();
                }
            }
            return null;
        }
    }


    public static class SuggestKeyProvider extends ItemKeyProvider<Long> {
        public SuggestKeyProvider() {
            super(ItemKeyProvider.SCOPE_MAPPED);
        }

        @Nullable
        @Override
        public Long getKey(int position) {
            return (long) position;
        }

        @Override
        public int getPosition(@NonNull Long key) {
            return key.intValue();
        }
    }

}
