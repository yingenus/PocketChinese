package com.yingenus.pocketchinese.view.holders;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public abstract class BlockAdapter<BockHolder extends RecyclerView.ViewHolder,ItemHolder extends RecyclerView.ViewHolder> extends RecyclerView.Adapter{

    private static final int VIEW_TYPE_BLOCK = 0;
    private static final int VIEW_TYPE_ITEM = 1;

    protected List<List<?>> mItems;
    private  int[] blockPositions;
    private boolean mShowBlock = true;

    protected CalcHelper calcHelper = new  WithBlocksHelper();

    public void setItems(List<List<?>> items){
        this.mItems = items;
        blockPositions=calcHelper.getBlockPosition();
    }

    public boolean isBlocksShowed() {
        return mShowBlock;
    }
    public void showBlocks(boolean show){
        mShowBlock = show;
        if (show && calcHelper.getClass() != WithBlocksHelper.class ){
            calcHelper = new WithBlocksHelper();
        }
        else if (!show && calcHelper.getClass() != WithOutBlocksHelper.class){
            calcHelper = new WithOutBlocksHelper();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(calcHelper.isBLock(position))
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
        switch (viewType){
            case VIEW_TYPE_BLOCK: return onCreateBlockHolder(parent);
            case VIEW_TYPE_ITEM: return onCreateItemHolder(parent);
        }
        throw new RuntimeException("Unknown item type.");
    }

    public abstract BockHolder onCreateBlockHolder(@NonNull ViewGroup parent);
    public abstract ItemHolder onCreateItemHolder(@NonNull ViewGroup parent);

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(!calcHelper.isBLock(position))
                onBindItemHolder((ItemHolder) holder,calcHelper.whichBlock(position), calcHelper.getItemInBlock(position));
        if(calcHelper.isBLock(position))
                onBindBlockHolder((BockHolder) holder, calcHelper.blockIndex(position));

    }

    public abstract void onBindBlockHolder(BockHolder holder, int blockNumber);
    public abstract void onBindItemHolder(ItemHolder holder, int blockNumber, int inBlockPosition);

    @Override
    public int getItemCount() {
        if (mItems == null) return 0;
        int maxBlock = mItems.size() -1;
        return calcHelper.getLastKeyPosition(maxBlock);
    }

    public int getRealBlockPosition(int blockNumber){
        return calcHelper.getRealBlockPosition(blockNumber);
    }
    public int getRealItemPosition(int blockNumber, int inBlockPosition){
        return calcHelper.getRealItemPosition(blockNumber,inBlockPosition);
    }

    public int getItemInBlock(int position){
        return calcHelper.getItemInBlock(position);
    }

    public int blockIndex(int position){
        return calcHelper.blockIndex(position);
    }

    public int[] getBlockPosition() {
        return calcHelper.getBlockPosition();
    }

    protected interface CalcHelper{
        int getRealBlockPosition(int blockNumber);
        int getRealItemPosition(int blockNumber, int inBlockPosition);
        boolean isBLock(int position);
        int blockIndex(int position);
        int getItemInBlock(int position);
        int getLastKeyPosition(int block);
        int whichBlock(int position);
        int[] getBlockPosition();
    }

    protected class WithBlocksHelper implements CalcHelper{
        @Override
        public int getRealBlockPosition(int blockNumber){
            if (blockNumber>= 0 && blockNumber < blockPositions.length){
                return blockPositions[blockNumber];
            }
            return -1;
        }
        @Override
        public int getRealItemPosition(int blockNumber, int inBlockPosition){
            int blockPosition = getRealBlockPosition(blockNumber)+1;
            if (blockPosition != -1 && inBlockPosition >= 0 && inBlockPosition < mItems.get(blockNumber).size()){
                return blockPosition+inBlockPosition;
            }
            return -1;
        }
        @Override
        public boolean isBLock(int position){
            for (int block = 0; block < blockPositions.length; block++){
                if (position == blockPositions[block])
                    return true;
            }
            return false;
        }

        @Override
        public int blockIndex(int position){
            for (int block = 0; block < blockPositions.length; block++){
                if (position == blockPositions[block])
                    return block;
            }
            return -1;
        }
        @Override
        public int getItemInBlock(int position){
            for (int block = 0; block < blockPositions.length; block++){
                int blockPosition = blockPositions[block];
                if (position == blockPosition)
                    return -1;
                else if( (position > blockPosition ) && ( position <= blockPosition+mItems.get(block).size())){
                    return position - blockPosition - 1;
                }
            }
            return -1;
        }

        @Override
        public int getLastKeyPosition(int block){
            int count=0;
            for (int i = 0; i <= block; i++) {
                count += 1;
                count += mItems.get(i).size();
            }
            return count;
        }

        @Override
        public int whichBlock(int position){
            if(isBLock(position)){
                return blockIndex(position);
            }else {

                for (int block = 0; block < blockPositions.length; block++){
                    if (block + 1 == blockPositions.length){
                        return block;
                    }else if (position>blockPositions[block] && position<blockPositions[block + 1]) {
                        return block;
                    }
                }
            }
            return -1;
        }

        @Override
        public int[] getBlockPosition() {
            int[] blockPosition = new int[mItems.size()];
            int count = 0;
            for (int block = 0; block < mItems.size(); block++){
                blockPosition[block] = count;
                count += 1;
                count += mItems.get(block).size();
            }
            return blockPosition;
        }
    }

    protected class WithOutBlocksHelper implements CalcHelper{
        @Override
        public int getRealBlockPosition(int blockNumber){
            return -1;
        }
        @Override
        public int getRealItemPosition(int blockNumber, int inBlockPosition){
            if (blockNumber>= 0 && blockNumber < blockPositions.length  && inBlockPosition >= 0 && inBlockPosition < mItems.get(blockNumber).size()){
                int startOffset = 0;

                for (int block = 0; block < blockNumber; block++){
                    startOffset += mItems.get(block).size();
                }

                return startOffset + inBlockPosition;
            }
            return -1;
        }
        @Override
        public boolean isBLock(int position){
            return false;
        }

        @Override
        public int blockIndex(int position){
            return -1;
        }
        @Override
        public int getItemInBlock(int position){
            int startOffset = 0;

            for (int block = 0; block < blockPositions.length; block++){
                if (position>= startOffset && position< mItems.get(block).size()){
                    return position - startOffset;
                }
                startOffset += mItems.get(block).size();
            }
            return -1;
        }

        @Override
        public int getLastKeyPosition(int block){
            int count=0;
            for (int i = 0; i <= block; i++) {
                count += mItems.get(i).size();
            }
            return count;
        }

        @Override
        public int whichBlock(int position){

            int startOffset = 0;

            for (int block = 0; block < blockPositions.length; block++){
                if (position>= startOffset && position< mItems.get(block).size()){
                    return block;
                }
                startOffset += mItems.get(block).size();
            }
            return -1;
        }

        @Override
        public int[] getBlockPosition() {
            int[] blockPosition = new int[mItems.size()];
            int count = 0;
            for (int block = 1; block < mItems.size() + 1; block++){
                blockPosition[block] = count;
                count += 1;
                count += mItems.get(block).size();
            }
            return blockPosition;
        }
    }
}
