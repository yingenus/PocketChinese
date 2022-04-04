package com.yingenus.pocketchinese.presentation.views.userlist;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yingenus.pocketchinese.R;
import com.yingenus.pocketchinese.presentation.views.stydylist.StudyListActivity;
import com.yingenus.pocketchinese.presentation.dialogs.ActioneSheetDialog;
import com.yingenus.pocketchinese.presentation.dialogs.DeleteDialog;
import com.yingenus.pocketchinese.presentation.dialogs.RenameDialog;

import com.yingenus.pocketchinese.view.holders.ViewViewHolder;
import com.yingenus.pocketchinese.domain.entitiys.UtilsVariantParams;
import com.yingenus.pocketchinese.domain.entitiys.database.pocketDB.StudyList;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class UserListsFragment extends Fragment implements UserListsInterface {
    private static final String DIALOG_TAG="create_new_list";

    private UserListsPresenter presenter = new UserListsPresenter(this);

    private RecyclerView recyclerView;

    public UserListsFragment(){
        super(R.layout.my_study_list_fragment);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=super.onCreateView(inflater, container, savedInstanceState);

        recyclerView =view.findViewById(R.id.expanded_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        UserStudyWordsAdapter adapter = new UserStudyWordsAdapter();
        adapter.setOnClickListener(this::onItemSelected);
        adapter.setOnLongClickListener(this::onLongItemClick);

        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new BoundsDecorator());

        presenter.onCreate(getActivity().getApplicationContext());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
        presenter = null;
    }

    @Override
    public void showStartView() {
        recyclerView.setAdapter(new StartAdapter());
    }

    @Override
    public void setStudyLists(@NotNull List<StudyListInfo> lists) {
        setListInfo(lists);
    }

    /*
    @Override
    public void addStudyList(@NotNull StudyListInfo list) {
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void updateStudyLists(@NotNull List<StudyListInfo> lists) {
        recyclerView.getAdapter().notifyDataSetChanged();
    }
    */

    private void onLongItemClick(StudyList studyList){
        ActioneSheetDialog bottomSheetDialog=new ActioneSheetDialog();
        bottomSheetDialog.show(getChildFragmentManager(),"action_dialog");

        bottomSheetDialog.setObserver(new ActioneSheetDialog.EditSheetDialogCallback() {
            @Override
            public void onRemove() {
                bottomSheetDialog.dismiss();
                removeItem(studyList);

            }

            @Override
            public void onRename() {
                RenameDialog dialog=new RenameDialog(studyList);
                dialog.setObserver(new RenameDialog.Observer() {
                    @Override
                    public void onRename() {
                        itemRenamed(studyList);
                    }
                });
                dialog.show(getChildFragmentManager(),"rename_dialog");
            }
        });

    }

    private void removeItem(StudyList studyList){
        DeleteDialog dialog = new DeleteDialog();
        dialog.setObserver(new DeleteDialog.Decision() {
            @Override
            public void delete() {
                presenter.removeStudyList(studyList);
            }

            @Override
            public void cancel() {

            }
        });
        dialog.setMMes(" "+studyList.getName()+" ?");
        dialog.show(getChildFragmentManager(),"del_dialog");
    }

    private void itemRenamed(StudyList studyList){
        presenter.renameStudyList(studyList);
    }

    private void setListInfo(List<StudyListInfo> studyListInfos){
        if (recyclerView.getAdapter() instanceof UserStudyWordsAdapter){
            ((UserStudyWordsAdapter) recyclerView.getAdapter()).setStudyListInfos(studyListInfos);
            recyclerView.getAdapter().notifyDataSetChanged();
        }
        else {
            UserStudyWordsAdapter adapter = new UserStudyWordsAdapter();
            adapter.setOnClickListener(this::onItemSelected);
            adapter.setOnLongClickListener(this::onLongItemClick);
            adapter.setStudyListInfos(studyListInfos);
            recyclerView.setAdapter(adapter);
        }
    }

    private void onItemSelected(StudyList list){
        Intent intent= StudyListActivity.Companion.getIntent(getContext(),list.getUuid());
        startActivity(intent);
    }

    private static class UserStudyWordsHolder extends RecyclerView.ViewHolder{
        private final TextView name;
        private final TextView words;
        private final TextView lastRep;
        private final TextView success;
        private final View backgroundView;
        private final CheckBox notifyBox;

        UserStudyWordsHolder(LayoutInflater inflater, ViewGroup viewGroup){
            super(inflater.inflate(R.layout.holder_user_study_words_litem,viewGroup,false));

            name = super.itemView.findViewById(R.id.user_study_words_list_name);
            words = super.itemView.findViewById(R.id.user_study_word_count_words);
            lastRep = super.itemView.findViewById(R.id.user_study_words_list_days);
            success =super.itemView.findViewById(R.id.user_study_words_success_value);
            backgroundView = super.itemView.findViewById(R.id.background);
            notifyBox = super.itemView.findViewById(R.id.notify);

            notifyBox.setVisibility(View.VISIBLE);
        }

        public void bind(StudyListInfo usStudyList){
            Resources resources = super.itemView.getResources();
            StudyListInfo list = usStudyList;
            name.setText(list.getStudyList().getName());
            words.setText(resources.getQuantityString(R.plurals.words,list.getWordsCount(),list.getWordsCount()));
            notifyBox.setChecked(list.getStudyList().getNotifyUser());

            if (usStudyList.getWordsCount() <= 0){
                success.setText(resources.getString(R.string.no_info));
                lastRep.setText(resources.getString(R.string.no_info));
            }else {
                success.setText(UtilsVariantParams.INSTANCE.getSuccess(resources, list.getSuccess()));
                success.setTextColor(UtilsVariantParams.INSTANCE.getColor(resources, list.getSuccess()));

                lastRep.setText(UtilsVariantParams.INSTANCE.getLstRepeat(resources, list.getLastRepeat()));
            }
            if (usStudyList.getExpered() != Expired.NON){
                name.setTextColor(UtilsVariantParams.INSTANCE.getLstRepeatColor(resources,usStudyList.getExpered()));
            }else {
                name.setTextColor(Color.BLACK);
            }

            //backgroundView.setBackgroundColor(UtilsVariantParams.INSTANCE.getLstRepeatColor(resources,usStudyList.getExpered()));

        }
    }

    private static class UserStudyWordsAdapter extends RecyclerView.Adapter<UserStudyWordsHolder>{
       // List<StudyListInfo> userStudyLists= Collections.emptyList();

        private AsyncListDiffer<StudyListInfo> userStudyListsDiffer = new AsyncListDiffer<StudyListInfo>(this,studyListsDifferCallback);


        private OnStudyListClicked mOnClickListener;
        private OnStudyListClicked mOnLongClickListener;

        public void setOnClickListener(OnStudyListClicked onClickListener){
            mOnClickListener = onClickListener;
        }
        public void setOnLongClickListener(OnStudyListClicked onLongClickListener){
            mOnLongClickListener = onLongClickListener;
        }

        public void setStudyListInfos(List<StudyListInfo> infos){
            userStudyListsDiffer.submitList(infos);
        }

        @NonNull
        @Override
        public UserStudyWordsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            UserStudyWordsHolder holder = new UserStudyWordsHolder((LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE),parent);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && userStudyListsDiffer.getCurrentList().size() > position){
                        if (mOnClickListener != null){
                            mOnClickListener.onStudyListClicked(userStudyListsDiffer.getCurrentList().get(position).getStudyList());
                        }
                    }
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = holder.getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && userStudyListsDiffer.getCurrentList().size() > position){
                        if (mOnLongClickListener != null){
                            mOnLongClickListener.onStudyListClicked(userStudyListsDiffer.getCurrentList().get(position).getStudyList());
                            return true;
                        }
                    }
                    return false;
                }
            });

            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull UserStudyWordsHolder holder, int position) {
            holder.bind(userStudyListsDiffer.getCurrentList().get(position));
        }

        @Override
        public int getItemCount() {
            return userStudyListsDiffer.getCurrentList().size();
        }

        @Override
        public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
            super.onDetachedFromRecyclerView(recyclerView);
            mOnLongClickListener = null;
            mOnClickListener = null;
        }


        private static final DiffUtil.ItemCallback studyListsDifferCallback = new DiffUtil.ItemCallback<StudyListInfo>() {
            @Override
            public boolean areItemsTheSame(@NonNull StudyListInfo oldItem, @NonNull StudyListInfo newItem) {
                return oldItem.component1().getUuid().equals(oldItem.component1().getUuid());
            }

            @Override
            public boolean areContentsTheSame(@NonNull StudyListInfo oldItem, @NonNull StudyListInfo newItem) {
                return oldItem.equals(newItem)
                        && oldItem.component1().getName().equals(newItem.component1().getName())
                        && oldItem.component1().getNotifyUser() == newItem.component1().getNotifyUser();
            }
        };
    }

    private static class StartAdapter extends RecyclerView.Adapter<ViewViewHolder>{
        @NonNull
        @Override
        public ViewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            return new ViewViewHolder(inflater.inflate(R.layout.holder_create_new_item,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 1;
        }
    }

    private interface OnStudyListClicked{
        void onStudyListClicked(StudyList studyList);
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
