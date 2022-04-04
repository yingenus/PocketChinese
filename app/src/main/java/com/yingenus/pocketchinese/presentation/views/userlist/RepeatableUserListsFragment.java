package com.yingenus.pocketchinese.presentation.views.userlist;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yingenus.pocketchinese.R;
import com.yingenus.pocketchinese.presentation.views.stydylist.StudyListActivity;
import com.yingenus.pocketchinese.view.holders.ViewViewHolder;
import com.yingenus.pocketchinese.domain.entitiys.UtilsVariantParams;
import com.yingenus.pocketchinese.domain.entitiys.database.pocketDB.StudyList;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class RepeatableUserListsFragment extends Fragment implements UserListsInterface {
    private static final String DIALOG_TAG="create_new_list";

    private RepeatableUserListsPresenter presenter = new RepeatableUserListsPresenter(this);

    private RecyclerView recyclerView;

    public RepeatableUserListsFragment(){
        super(R.layout.my_study_list_fragment);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=super.onCreateView(inflater, container, savedInstanceState);

        TextView headerView = view.findViewById(R.id.header_text);
        headerView.setText(getContext().getString(R.string.repeated_header));

        recyclerView =view.findViewById(R.id.expanded_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        UserStudyWordsAdapter adapter = new UserStudyWordsAdapter();
        adapter.setOnStudyListClicked(this::onItemSelected);

        recyclerView.setAdapter(adapter);

        presenter.onCreate(getContext());

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
        if (!(recyclerView.getAdapter() instanceof UserStudyWordsAdapter)){
            setListInfo(Collections.singletonList(list));
        }else {
            recyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    public void updateStudyLists(@NotNull List<StudyListInfo> lists) {
        if (!(recyclerView.getAdapter() instanceof UserStudyWordsAdapter)){
            setListInfo(lists);
        }else {
            recyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    */

    private void setListInfo(List<StudyListInfo> studyListInfos){
        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        if (!(adapter instanceof UserStudyWordsAdapter)){
            UserStudyWordsAdapter userStudyWordsAdapter = new UserStudyWordsAdapter();
            userStudyWordsAdapter.setOnStudyListClicked(this::onItemSelected);
            recyclerView.setAdapter(userStudyWordsAdapter);
        }
        ((UserStudyWordsAdapter) recyclerView.getAdapter()).userStudyLists=studyListInfos;
        recyclerView.getAdapter().notifyDataSetChanged();

    }

    private void onItemSelected(StudyList list){
        Intent intent= StudyListActivity.Companion.getIntent(getContext(),list.getUuid());
        startActivity(intent);
    }

    private static class UserStudyWordsHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final TextView words;
        private final TextView lastRep;
        private final TextView success;
        private final View backgroundView;

        UserStudyWordsHolder(LayoutInflater inflater, ViewGroup viewGroup){
            super(inflater.inflate(R.layout.holder_user_study_words_litem,viewGroup,false));

            name = super.itemView.findViewById(R.id.user_study_words_list_name);
            words = super.itemView.findViewById(R.id.user_study_word_count_words);
            lastRep = super.itemView.findViewById(R.id.user_study_words_list_days);
            success =super.itemView.findViewById(R.id.user_study_words_success_value);
            backgroundView = super.itemView.findViewById(R.id.background);

        }

        public void bind(StudyListInfo usStudyList){
            Resources resources = super.itemView.getResources();
            StudyListInfo list = usStudyList;
            name.setText(list.getStudyList().getName());
            words.setText(resources.getQuantityString(R.plurals.words,list.getWordsCount(),list.getWordsCount()));

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
            }
            //backgroundView.setBackgroundColor(UtilsVariantParams.INSTANCE.getLstRepeatColor(resources,usStudyList.getExpered()));
        }

    }

    private static class UserStudyWordsAdapter extends RecyclerView.Adapter<UserStudyWordsHolder>{
        List<StudyListInfo> userStudyLists= Collections.emptyList();

        private OnStudyListClicked mListener;

        public void setOnStudyListClicked(OnStudyListClicked onClickListener){
            mListener = onClickListener;
        }

        @NonNull
        @Override
        public UserStudyWordsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            LayoutInflater inflater =(LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            UserStudyWordsHolder holder = new UserStudyWordsHolder(inflater,parent);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && userStudyLists.size() > position){
                        if (mListener != null){
                            mListener.onStudyListClicked(userStudyLists.get(position).getStudyList());
                        }
                    }
                }
            });
            return holder ;
        }

        @Override
        public void onBindViewHolder(@NonNull UserStudyWordsHolder holder, int position) {
            holder.bind(userStudyLists.get(position));
        }

        @Override
        public int getItemCount() {
            return userStudyLists.size();
        }

        @Override
        public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
            super.onDetachedFromRecyclerView(recyclerView);
            mListener = null;
        }
    }

    private interface OnStudyListClicked{
        void onStudyListClicked(StudyList studyList);
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
}
