package com.yingenus.pocketchinese.controller.fragment;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;
import androidx.recyclerview.selection.SelectionPredicates;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yingenus.pocketchinese.R;
import com.yingenus.pocketchinese.controller.UtilsKt;
import com.yingenus.pocketchinese.controller.activity.CreateWordActivity;
import com.yingenus.pocketchinese.controller.activity.EditWordActivity;
import com.yingenus.pocketchinese.controller.dialog.DeleteDialog;
import com.yingenus.pocketchinese.controller.dialog.NumbPickerDialog;
import com.yingenus.pocketchinese.controller.dialog.StartTrainingSheetDialog;
import com.yingenus.pocketchinese.controller.holders.StudyListAdapter;
import com.yingenus.pocketchinese.model.RepeatType;
import com.yingenus.pocketchinese.model.UtilsVariantParams;
import com.yingenus.pocketchinese.model.database.pocketDB.StudyWord;
import com.yingenus.multipleprogressbar.MultipleProgressBar;

import com.yingenus.pocketchinese.presenters.StudyListPresenter;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class StudyListFragment extends Fragment implements ActionMode.Callback, StudyListInterface {
    private static final String STUDY_UUID ="com.example.pocketchinese.studyListFragment.studyuuid";

    private StudyListPresenter presenter = new StudyListPresenter(this);

    private TextView lastRepeat;
    private TextView wordsCount;
    private TextView nextRepeat;
    private TextView chnProgress;
    private TextView pinProgress;
    private TextView trnProgress;
    private NestedScrollView header;
    private FloatingActionButton fabAdd;
    private FloatingActionButton fabStart;
    private MultipleProgressBar progressBar;
    private AppBarLayout appBarLayout;
    private RecyclerView recyclerView;
    private Toolbar toolbar;

    private ValueAnimator alphaAnimator;
    private int initialMargin;

    private UUID studyListUUID;

    private SelectionTracker selectionTracker;

    private ActionMode actionMode;

    private final @DrawableRes int notifyIcon = R.drawable.on_off_notify;

    static class StartTrainFF extends FragmentFactory {
        private UUID studyListUUID;

        public void setStudyList(UUID uuid) {
            this.studyListUUID = uuid;
        }

        public UUID getStudyList() {
            return studyListUUID;
        }

        @NonNull
        @Override
        public Fragment instantiate(@NonNull ClassLoader classLoader, @NonNull String className) {
            if(className.equals(StartTrainingSheetDialog.class.getName()))
                return new StartTrainingSheetDialog(studyListUUID);
            return super.instantiate(classLoader, className);
        }
    }

    private StudyListFragment(){
        super(R.layout.study_list_fragment);
    }
    public StudyListFragment(UUID studyListUUID){
        this();
        this.studyListUUID =studyListUUID;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.study_list_action_mode_manu,menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        if (item.getItemId()==R.id.edit){
            if (getSelectedWords().length==1){
                onEditItemClicked();
                return true;
            }
        }
        if (item.getItemId()==R.id.move){
            onMoveItemsClicked();
            return true;
        }
        if (item.getItemId()==R.id.delete){
            onDeleteItemsClicked();
            return true;
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        selectionTracker.clearSelection();
        blockAppBar(false);

        if (alphaAnimator!=null&&alphaAnimator.isRunning()){
            alphaAnimator.cancel();
        }
        startRisingAnimation();
        this.actionMode = null;
    }

    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.notify){
            boolean notify = item.isChecked();
            item.setChecked(!notify);
            setMenuItemNotify(!notify);
            presenter.setNotify(!notify);
            return true;
        }
        return false;
    }

    public void onNavigationClicked(View view) {
        getActivity().finish();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        StartTrainFF factory = new StartTrainFF();
        if (savedInstanceState != null){
            factory.setStudyList(UUID.fromString(savedInstanceState.getString(STUDY_UUID)));
        }
        getChildFragmentManager().setFragmentFactory(factory);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= super.onCreateView(inflater, container, savedInstanceState);

        lastRepeat= view.findViewById(R.id.list_days);
        nextRepeat = view.findViewById(R.id.next_days);
        wordsCount= view.findViewById(R.id.word_count);
        chnProgress= view.findViewById(R.id.chn_success);
        pinProgress= view.findViewById(R.id.pin_success);
        trnProgress = view.findViewById(R.id.trn_success);
        appBarLayout = view.findViewById(R.id.app_bar_layout);
        header= view.findViewById(R.id.scroll_header);
        recyclerView= view.findViewById(R.id.expanded_recyclerview);
        toolbar = view.findViewById(R.id.toolbar);

        fabAdd= view.findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(this::onAddNewWordClicked);

        fabStart= view.findViewById(R.id.fab_start);
        fabStart.setOnClickListener(this::onStartTrainClicked);

        toolbar.setNavigationOnClickListener(this::onNavigationClicked);
        toolbar.setOnMenuItemClickListener(this::onMenuItemClick);

        progressBar = view.findViewById(R.id.progress_bar);
        progressBar.setOnTouchListener(this::onProgressBarTouch);

        recyclerView.setAdapter(new StudyListAdapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new BoundsDecorator(UtilsKt.dp2px(45,getContext())));

        StudyListAdapter.StudyListKeyProvider keyProvider=new StudyListAdapter.StudyListKeyProvider();
        selectionTracker= new SelectionTracker.Builder(
                "dictionary_selection",
                recyclerView, keyProvider,
                new StudyListAdapter.StudyListDetailsLookUp(recyclerView),
                StorageStrategy.createLongStorage())
                .withSelectionPredicate(SelectionPredicates.createSelectAnything())
                .build();
        ((StudyListAdapter)recyclerView.getAdapter()).setSelectionTracker(selectionTracker);

        selectionTracker.addObserver(new SelectionTracker.SelectionObserver() {
            @Override
            public void onSelectionChanged() {
                StudyListFragment.this.onSelectionChanged();
            }
        });

        presenter.onCreate(getContext(),studyListUUID);

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        FragmentFactory ff = getChildFragmentManager().getFragmentFactory();
        if (ff instanceof StartTrainFF){
            UUID uuid = ((StartTrainFF)ff).getStudyList();
            if (uuid != null){
                outState.putString(STUDY_UUID,uuid.toString());
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.onResume();
        if (recyclerView.getAdapter() != null && recyclerView.getAdapter().getItemCount() == 0){
            collapseAppBar();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
        selectionTracker.clearSelection();
        presenter = null;
    }

    private boolean onProgressBarTouch(View v, MotionEvent me){
        int action = me.getAction();
        if ( action == MotionEvent.ACTION_DOWN){
            progressBar.getProgressItemById(R.id.progress_chn).setShowProgressText(true);
            progressBar.getProgressItemById(R.id.progress_pin).setShowProgressText(true);
            progressBar.getProgressItemById(R.id.progress_trn).setShowProgressText(true);
        }else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL){
            progressBar.getProgressItemById(R.id.progress_chn).setShowProgressText(false);
            progressBar.getProgressItemById(R.id.progress_pin).setShowProgressText(false);
            progressBar.getProgressItemById(R.id.progress_trn).setShowProgressText(false);
        }
        return true;
    }

    @Override
    public void setName(@NotNull String name) {
        toolbar.setTitle(name);
    }

    @Override
    public void setWordsCount(int items) {
        fabStart.setEnabled(items > 0);
        if (items == -1)
            wordsCount.setText("0");
        else
            wordsCount.setText(UtilsVariantParams.INSTANCE.getWords(getResources(),items));
    }

    @Override
    public void setChnStat(int percent) {
        if (percent == -1){
            progressBar.getProgressItemById(R.id.progress_chn).setProgress(1);
            chnProgress.setText("NoN");
            return;
        }
        progressBar.getProgressItemById(R.id.progress_chn).setProgress(percent);
        chnProgress.setText(percent+"%");
    }

    @Override
    public void setPinStat(int percent) {
        if (percent == -1){
            progressBar.getProgressItemById(R.id.progress_pin).setProgress(1);
            pinProgress.setText("NoN");
            return;
        }
        progressBar.getProgressItemById(R.id.progress_pin).setProgress(percent);
        pinProgress.setText(percent+"%");
    }

    @Override
    public void setTrnStat(int percent) {
        if (percent == -1){
            progressBar.getProgressItemById(R.id.progress_trn).setProgress(1);
            trnProgress.setText("NoN");
            return;
        }
        progressBar.getProgressItemById(R.id.progress_trn).setProgress(percent);
        trnProgress.setText(percent+"%");
    }

    @Override
    public void lastRepeat(@org.jetbrains.annotations.Nullable Date date) {
        if (date ==null){
            lastRepeat.setText("NoN");
        } else {
            lastRepeat.setText(UtilsVariantParams.INSTANCE.getLstRepeat(getResources(),date));
        }
    }

    @Override
    public void nextRepeat(@org.jetbrains.annotations.Nullable Date date) {
        if (date ==null){
            nextRepeat.setText("NoN");
        }else {
            nextRepeat.setText(UtilsVariantParams.INSTANCE.getNextRepeat(getResources(),date));
        }
    }

    @Override
    public void setRepeatType(@NotNull RepeatType type) {
        if (type.getIgnoreCHN()){
            setChnStat(0);
        }
        if (type.getIgnorePIN()){
            setPinStat(0);
        }
        if (type.getIgnoreTRN()){
            setTrnStat(0);
        }
    }

    @Override
    public void setStudyWords(@NotNull Map<Integer, ? extends List<StudyWord>> stList) {
        if (stList.isEmpty()){
            collapseAppBar();
        }else if (!appBarLayout.isLiftOnScroll()){
            deployedAppBar();
        }
        ((StudyListAdapter) recyclerView.getAdapter()).setItems((Map<Integer, List<StudyWord>>) stList);
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void suggestMoveWord(@NotNull List<StudyWord> words, int maxBlock) {
        NumbPickerDialog dialog = new NumbPickerDialog();
        dialog.setFromValue(1);
        dialog.setToValue(maxBlock+1);
        dialog.setObserver(new NumbPickerDialog.Observer() {
            @Override
            public void itemPicked(int i) {
                selectionTracker.clearSelection();
                presenter.moveWords(words, i);
            }

            @Override
            public void cancel() {

            }
        });
        dialog.show(getChildFragmentManager(),"numb_picker_dialog");
    }

    @Override
    public void suggestDeleteWords(@NotNull List<StudyWord> words) {
        DeleteDialog dialog = new DeleteDialog();
        dialog.setObserver(new DeleteDialog.Decision() {
            @Override
            public void delete() {
                selectionTracker.clearSelection();
                presenter.removeWords(words);

            }
            @Override
            public void cancel() {

            }
        });
        dialog.setMMes(" "+words.size()+" "+ getString(R.string.items));
        dialog.show(getChildFragmentManager(),"del_dialog");
    }

    @Override
    public void addWord(@NotNull UUID studyListUUID) {
        Intent intent= CreateWordActivity.Companion.getIntent(getActivity(), studyListUUID);
        startActivity(intent);
    }

    @Override
    public void startTrain(@NotNull UUID studyListUUID) {
        FragmentFactory ff = getChildFragmentManager().getFragmentFactory();
        if (ff instanceof StartTrainFF){
            ((StartTrainFF) ff).setStudyList(studyListUUID);
        }
        StartTrainingSheetDialog dialog = (StartTrainingSheetDialog) getChildFragmentManager().getFragmentFactory().instantiate(getContext().getClassLoader(),StartTrainingSheetDialog.class.getName());
        dialog.show(getChildFragmentManager(),"training_dialog");
    }

    @Override
    public void editWord(@NotNull StudyWord studyWord, @NotNull UUID studyListUUID) {
        Intent intent = EditWordActivity.Companion
               .getIntent(getContext(),studyWord,studyListUUID);
        startActivity(intent);
    }

    @Override
    public void setNotify(boolean canNotify) {
        setMenuItemNotify(canNotify);
        if (toolbar != null){
            MenuItem item = toolbar.getMenu().findItem(R.id.notify);
            if (item != null){
                item.setChecked(canNotify);
            }
        }
    }

    private void collapseAppBar(){
        appBarLayout.setExpanded(false, true);
        blockAppBar(true);
    }

    private void deployedAppBar(){
        appBarLayout.setExpanded(true,true);
        blockAppBar(false);
    }

    private static class NoScrollCallBack extends AppBarLayout.Behavior.DragCallback{
        @Override
        public boolean canDrag(@NonNull AppBarLayout appBarLayout) {
            return false;
        }
    }

    private void setMenuItemNotify(boolean isChecked){
        StateListDrawable stateDrawable = (StateListDrawable) getResources().getDrawable(notifyIcon,getActivity().getTheme());
        int[] state = {isChecked? android.R.attr.state_checked : android.R.attr.state_empty};
        stateDrawable.setState(state);
        MenuItem item = toolbar.getMenu().findItem(R.id.notify);
        if (item != null){
            item.setIcon(stateDrawable.getCurrent());
        }
    }

    private void onSelectionChanged(){
        if (selectionTracker.getSelection().size() > 0) {

            if (actionMode == null) {
                appBarLayout.setExpanded(false);
                startCollapseAnimation();
                blockAppBar(true);
                actionMode = ((AppCompatActivity)getActivity()).startSupportActionMode(this);

                ((StudyListAdapter)recyclerView.getAdapter()).selectionStart();
                recyclerView.getAdapter().notifyDataSetChanged();

            }

            StudyWord[] selectedWords=getSelectedWords();

            MenuItem item = actionMode.getMenu().findItem(R.id.edit);
            if (item!=null) {
                item.setVisible(selectedWords.length == 1);
            }

            actionMode.setTitle(String.valueOf(selectedWords.length));

        }else if(actionMode != null) {
            selectionTracker.clearSelection();
            actionMode.finish();
            ((StudyListAdapter)recyclerView.getAdapter()).selectionEnd();
            recyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    private void blockAppBar(boolean state){
        recyclerView.setNestedScrollingEnabled(!state);
        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior)
                ((CoordinatorLayout.LayoutParams)appBarLayout.getLayoutParams())
                        .getBehavior();

        if (behavior != null){
            if (state){
                behavior.setDragCallback(new NoScrollCallBack());
            }else {
                behavior.setDragCallback(null);
            }
        }
    }

    private void startCollapseAnimation(){

        CoordinatorLayout.LayoutParams params=(CoordinatorLayout.LayoutParams)recyclerView.getLayoutParams();
        initialMargin=header.getHeight();

        alphaAnimator = ValueAnimator.ofInt(initialMargin,0);
        alphaAnimator.setDuration(300);
        alphaAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value=(int)animation.getAnimatedValue();
                params.topMargin=(int)animation.getAnimatedValue();
                header.setAlpha(1f*value/initialMargin);
                recyclerView.requestLayout();
            }
        });
        alphaAnimator.start();

        fabStart.hide();
        fabAdd.hide();
    }

    private void startRisingAnimation(){

        CoordinatorLayout.LayoutParams params=(CoordinatorLayout.LayoutParams)recyclerView.getLayoutParams();

        alphaAnimator = ValueAnimator.ofInt(0,initialMargin);
        alphaAnimator.setDuration(300);
        alphaAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value=(int)animation.getAnimatedValue();
                params.topMargin=(int)animation.getAnimatedValue();
                header.setAlpha(1f*value/initialMargin);
                recyclerView.requestLayout();
            }
        });
        alphaAnimator.start();

        fabStart.show();
        fabAdd.show();
    }

    private void onEditItemClicked(){
        presenter.editClicked(getSelectedWords()[0]);
        selectionTracker.clearSelection();
    }

    private void onMoveItemsClicked(){
        StudyWord[] words = getSelectedWords();
        presenter.moveClicked(Arrays.asList(words));
    }

    private void onDeleteItemsClicked(){
        StudyWord[] words = getSelectedWords();
        presenter.deleteClicked(Arrays.asList(words));
    }

    private StudyWord[] getSelectedWords(){
       return ((StudyListAdapter)recyclerView.getAdapter()).getSelectedWords();
    }

    private void onStartTrainClicked(View view){
        presenter.trainClicked();
    }
    private void onAddNewWordClicked(View view){
        presenter.addWordClicked();
    }

    private static class BoundsDecorator extends RecyclerView.ItemDecoration{
        private final int pix;

        BoundsDecorator(int pix){
            this.pix = pix;
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {

            if (parent.getAdapter().getItemCount()-1 == parent.getChildAdapterPosition(view)){
                outRect.bottom += pix;
            }
        }
    }
}
