package com.yingenus.pocketchinese.presentation.views.suggestist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.ItemKeyProvider;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.RecyclerView;

import com.yingenus.pocketchinese.R;
import com.yingenus.pocketchinese.domain.entitiys.UtilsVariantParams;
import com.yingenus.pocketchinese.domain.entitiys.database.pocketDB.StudyWord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class StudyListAdapter extends RecyclerView.Adapter{
    private static final int VIEW_TYPE_BLOCK = 0;
    private static final int VIEW_TYPE_ITEM = 1;

    private Map<Integer, List<StudyWord>> map;
    private  int[] blockPositions;

    private SelectionTracker<Long> selectionTracker;
    private boolean isSelectable = false;

    private OnWordClicked onLongClicked;
    private OnWordClicked onClicked;

    public void setItems(Map<Integer,List<StudyWord>> map){
        this.map=map;
        blockPositions = getBlockPosition();
    }

    @Override
    public int getItemViewType(int position) {
        if(isBLock(position))
            return VIEW_TYPE_BLOCK;
        else
            return VIEW_TYPE_ITEM;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) parent.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        switch (viewType){
            case VIEW_TYPE_BLOCK: return new BlockViewHolder(inflater,parent);
            case VIEW_TYPE_ITEM:{
                WordViewHolder holder = new  WordViewHolder(inflater,parent);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = holder.getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            StudyWord word = getItem(position);
                            if (word != null && onClicked != null){
                                onClicked.onWordClicked(word);
                            }
                        }
                    }
                });
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        int position = holder.getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            StudyWord word = getItem(position);
                            if (word != null && onLongClicked != null){
                                onLongClicked.onWordClicked(word);
                                return true;
                            }
                        }
                        return false;
                    }
                });
                return holder;
            }
        }
        throw new RuntimeException("Unknown item type.");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof WordViewHolder){
            if(!isBLock(position))
                ((WordViewHolder)holder).bind(getItem(position),position,selectionTracker,isSelectable);
        }
        if (holder instanceof BlockViewHolder){
            if(isBLock(position))
                ((BlockViewHolder)holder).bind(blockIndex(position),position,selectionTracker,isSelectable);
        }
    }

    @Override
    public int getItemCount() {
        if (map == null || map.isEmpty()) return 0;
        int maxBlock = Collections.max(map.keySet());
        return getLastKeyPosition(maxBlock);
    }



    public void setSelectionTracker(SelectionTracker<Long> selectionTracker) {
        this.selectionTracker = selectionTracker;
        this.selectionTracker.addObserver(new SelectionObserver());
    }

    public void selectionStart(){
        isSelectable = true;
    }
    public void selectionEnd(){
        isSelectable = false;
    }

    public void setOnWordClickedListener(OnWordClicked listener){
       onClicked = listener;
    }

    public void setOnWordLongClickedListener(OnWordClicked listener){
        onLongClicked = listener;
    }

    private boolean isBLock(int position){
        for (int block = 1; block < blockPositions.length; block++){
            if (position == blockPositions[block])
                return true;
        }
        return false;
    }
    private int blockIndex(int position){
        for (int block = 1; block < blockPositions.length; block++){
            if (position == blockPositions[block])
                return block;
        }
        return -1;
    }

    public StudyWord getItem(int position){
        for (int block = 1; block < blockPositions.length; block++){
            int blockPosition = blockPositions[block];
            List<StudyWord> words = map.get(block);
            if (words == null){
                words = Collections.EMPTY_LIST;
            }
            if (position == blockPosition)
                return null;
            else if( (position > blockPosition ) && ( position <= blockPosition+words.size())){
                return words.get(position - blockPosition - 1);
            }
        }
        return null;
    }

    public StudyWord[] getSelectedWords(){
        if (selectionTracker != null && selectionTracker.getSelection().size() != 0){
            ArrayList<StudyWord> words = new ArrayList<>();

            int itemCount = getItemCount();
            for(int position = 0; position <itemCount; position++){
                if (!isBLock(position) && selectionTracker.isSelected((long) position)){
                    words.add(getItem(position));
                }
            }
            return words.toArray(new StudyWord[0]);
        }
        return new StudyWord[0];
    }

    private int getLastKeyPosition(int block){
        int count=0;
        for (int i = 1; i <= block; i++) {
            count += 1;
            List<StudyWord> words = map.get(i);
            if (words != null){
                count += words.size();
            }
        }
        return count;
    }

    private int whatBlock(int position){
        if(isBLock(position)){
            return blockIndex(position);
        }else {

            for (int block = 1; block < blockPositions.length; block++){
                if (block + 1 == blockPositions.length){
                    return block;
                }else if (position>blockPositions[block] && position<blockPositions[block + 1]) {
                    return block;
                }
            }
        }
        return -1;
    }
    public int[] getBlockPosition() {
        int maxBlock = 0;
        if (!map.isEmpty()){
            maxBlock = Collections.max(map.keySet());
        }
        int[] blockPosition = new int[maxBlock + 1];
        int count = 0;
        for (int block = 1; block <= maxBlock; block++){
            blockPosition[block] = count;
            count += 1;
            List<StudyWord> words = map.get(block);
            if (words != null){
                count += words.size();
            }
        }
        return blockPosition;
    }

    private class SelectionObserver extends SelectionTracker.SelectionObserver<Long>{
        private boolean blockReaction = false;

        @Override
        public void onItemStateChanged(@NonNull Long key, boolean selected) {
            int position = key.intValue();

            if (isBLock(position) ) {
                if (blockReaction){
                    blockReaction = false;
                    return;
                }
                int block = blockIndex(position);
                int lastChild = getLastKeyPosition(block) - 1;

                Long[] keys = new Long[lastChild - position];

                for (int i = key.intValue()+1; i <= lastChild; i++){
                    keys[i-1-position] = (long) i;
                }

                selectionTracker.setItemsSelected(Arrays.asList(keys),selected);
            }else {
                int block = whatBlock(position);
                int blockPosition = blockPositions[block];
                int lastChild = getLastKeyPosition(block)-1;

                boolean stats= true;
                for (int pos = blockPosition+1 ; pos<=lastChild ; pos++){
                    boolean select = selectionTracker.isSelected((long) pos);
                    stats &= select;
                }

                if (selectionTracker.isSelected((long) blockPosition) != stats) blockReaction = true;
                selectionTracker.setItemsSelected(Collections.singleton((long) blockPosition), stats);
            }

        }
    }

    public static class BlockViewHolder extends RecyclerView.ViewHolder {
        private final TextView block;
        private final CheckBox selectedBox;
        private final StudyListDetails details;

        BlockViewHolder(LayoutInflater inflater, ViewGroup viewGroup){
            super(inflater.inflate(R.layout.holder_block_item,viewGroup,false));
            block = super.itemView.findViewById(R.id.holder_block_name);
            selectedBox = super.itemView.findViewById(R.id.selected_box);
            details = new StudyListDetails();
            details.isItem = false;
        }

        public void bind(int blockIndex, int position, SelectionTracker<Long> tracker, boolean isSelectable){
            String str = super.itemView.getResources().getString(R.string.block,blockIndex);
            block.setText(str);
            details.position=position;

            if (tracker != null){
                bindSelection(tracker,isSelectable);
            }
        }

        public void bindSelection(SelectionTracker<Long> tracker, boolean isSelectable){
            boolean isChecked=tracker.isSelected(details.getSelectionKey());
            int visibility=(isSelectable)? View.VISIBLE : View.GONE;
            selectedBox.setVisibility(visibility);
            selectedBox.setChecked(isChecked);
        }

        public StudyListDetails getDetails() {
            return details;
        }
    }

    public static class WordViewHolder extends RecyclerView.ViewHolder{
        private final StudyListDetails details;


        private final TextView chnText;
        private final TextView pinyinText;
        private final TextView trnText;
        private final CheckBox selectedBox;
        private final TextView counter;
        private final TextView success;

        private StudyWord word;

        WordViewHolder(LayoutInflater inflater,ViewGroup viewGroup){
            super(inflater.inflate(R.layout.holder_dictionary_item,viewGroup,false));

            chnText = super.itemView.findViewById(R.id.dictionary_item_chin_text);
            pinyinText = super.itemView.findViewById(R.id.dictionary_item_pinyin);
            trnText = super.itemView.findViewById(R.id.dictionary_item_second_language);
            selectedBox = super.itemView.findViewById(R.id.selected_box);
            counter = super.itemView.findViewById(R.id.counter);
            success = super.itemView.findViewById(R.id.success);
            success.setVisibility(View.VISIBLE);

            details = new StudyListDetails();
            details.isItem = true;
        }

        public void bind(StudyWord word, int position, SelectionTracker<Long> tracker,boolean isSelectable){
            this.word = word;

            chnText.setText(word.getChinese());
            trnText.setText(word.getTranslate());
            pinyinText.setText(word.getPinyin());

            success.setText(UtilsVariantParams.INSTANCE
                    .getSuccess(super.itemView.getResources(),word.getLevel()));
            success.setTextColor(UtilsVariantParams.INSTANCE
                    .getColor(super.itemView.getResources(),word.getLevel()));

            details.position=position;

            if (tracker != null){
                bindSelection(tracker,isSelectable);
            }

        }

        public void bindSelection(SelectionTracker<Long> tracker,boolean isSelectable){
            boolean isChecked = tracker.isSelected(details.getSelectionKey());
            int visibility = (isSelectable)? View.VISIBLE : View.GONE;
            int textVisible = (!isSelectable)? View.VISIBLE : View.GONE;
            selectedBox.setVisibility(visibility);
            success.setVisibility(textVisible);

            selectedBox.setChecked(isChecked);
        }

        public StudyWord getWord() {
            return word;
        }

        public StudyListDetails getDetails() {
            return details;
        }
    }

    public static class StudyListDetails extends ItemDetailsLookup.ItemDetails<Long>{
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

    public static class StudyListDetailsLookUp extends ItemDetailsLookup<Long>{

        private final RecyclerView recyclerView;

        public StudyListDetailsLookUp(RecyclerView recyclerView) {
            super();
            this.recyclerView = recyclerView;
        }

        @Nullable
        @Override
        public ItemDetails<Long> getItemDetails(@NonNull MotionEvent e) {
            View view = recyclerView.findChildViewUnder(e.getX(),e.getY());
            if (view != null){
                RecyclerView.ViewHolder viewHolder = recyclerView.getChildViewHolder(view);
                if (viewHolder instanceof WordViewHolder){
                    return ((WordViewHolder)viewHolder).getDetails();
                }
                if (viewHolder instanceof BlockViewHolder){
                    return ((BlockViewHolder)viewHolder).getDetails();
                }
            }
            return null;
        }
    }

    public interface OnWordClicked{
        void onWordClicked(StudyWord studyWord);
    }

    public static class StudyListKeyProvider extends ItemKeyProvider<Long> {
        public StudyListKeyProvider() {
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
