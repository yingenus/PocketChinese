package com.yingenus.pocketchinese.presentation.views.suggestist;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.selection.SelectionPredicates;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;

import com.yingenus.pocketchinese.R;

import com.yingenus.pocketchinese.Settings;
import com.yingenus.pocketchinese.controller.activity.SuggestWordsActivityKt;
import com.yingenus.pocketchinese.presentation.views.addword.AddWordsSheetDialog;
import com.yingenus.pocketchinese.presentation.views.addword.AddWordFragment;
import com.yingenus.pocketchinese.domain.entitiys.UtilsKt;

import com.yingenus.pocketchinese.domain.entitiys.words.suggestwords.JSONHelper;
import com.yingenus.pocketchinese.domain.entitiys.words.suggestwords.JSONObjects;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.transition.MaterialArcMotion;
import com.google.android.material.transition.MaterialContainerTransform;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class SuggestWordsActivity extends AppCompatActivity implements android.view.ActionMode.Callback {

    private JSONObjects.FileInfo fInfo;
    private JSONObjects.WordList suggestWords;
    private boolean[] blocksState;

    private RecyclerView recycler;
    private Toolbar toolbar;
    private TextView mDescription;
    private CardView expandedView;
    private ImageView imageView;
    private CoordinatorLayout root;
    private AppBarLayout appBarLayout;

    private View coweredView;

    private View startView;

    private SelectionTracker selectionTracker;
    private android.view.ActionMode actionMode;

    @Override
    public boolean onCreateActionMode(android.view.ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.giwen_action_mode_manu,menu);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        return true;

    }

    @Override
    public boolean onPrepareActionMode(android.view.ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(android.view.ActionMode mode, MenuItem item) {
        if (item.getItemId()==R.id.all){
            if (true){
                int items= recycler.getAdapter().getItemCount();
                Long[] keys= new Long[items];
                for (int i=0;i<items;i++){
                    keys[i]=(long)i;
                }
                selectionTracker.setItemsSelected(Arrays.asList(keys),true);

            }
            return true;
        }
        if (item.getItemId()==R.id.next && selectionTracker.getSelection().size()!=0){
            showAddDialog();
            return true;
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(android.view.ActionMode mode) {
        blockAppBar(false);
        selectionTracker.clearSelection();
        this.actionMode = null;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        ((SuggestWordsAdapter) recycler.getAdapter()).selectionEnd();
        recycler.getAdapter().notifyDataSetChanged();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.suggest_list_layout);

        getDataFromIntent();

        recycler = findViewById(R.id.expanded_recyclerview);
        toolbar = findViewById(R.id.toolbar);
        mDescription = findViewById(R.id.text_description);
        expandedView = findViewById(R.id.expanded_view);
        coweredView=findViewById(R.id.cowered_all_layout);
        imageView = findViewById(R.id.collapsing_backdrop);
        appBarLayout= findViewById(R.id.app_bar_layout);

        root=findViewById(R.id.root);

        recycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(this::onNavigationButtonClicked);

        SuggestWordsAdapter adapter=new  SuggestWordsAdapter();
        adapter.setItemListener(this::onItemClicked);
        adapter.setBlockListener(this::onBlockClicked);
        recycler.setAdapter(adapter);

        SuggestWordsAdapter.SuggestKeyProvider keyProvider=new SuggestWordsAdapter.SuggestKeyProvider();
        selectionTracker= new SelectionTracker.Builder(
                "dictionary_selection",
                recycler, keyProvider,
                new SuggestWordsAdapter.SuggestDetailsLookUp(recycler),
                StorageStrategy.createLongStorage())
                .withSelectionPredicate(SelectionPredicates.createSelectAnything())
               .build();
        adapter.setSelectionTracker(selectionTracker);

        selectionTracker.addObserver( getSelectionObserver());

        loadWords();
        fillToolbar();
        setViewed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        selectionTracker.clearSelection();
        selectionTracker = null; // test
        SuggestWordsAdapter adapter = ((SuggestWordsAdapter)recycler.getAdapter());
        adapter.setBlockListener(null);
        adapter.setSelectionTracker(null);
        adapter.setItemListener(null);

    }

    private void onNavigationButtonClicked(View view){
        finish();
    }

    private SelectionTracker.SelectionObserver getSelectionObserver(){
        return new SelectionTracker.SelectionObserver() {
            @Override
            public void onSelectionChanged() {
                super.onSelectionChanged();
                SuggestWordsActivity.this.onSelectionChanged();
            }
        };
    }

    private void setViewed(){
        String intValue = fInfo.getVersion().replace(".","");
        Settings.INSTANCE.setViewItem(getApplicationContext(),Integer.parseInt(intValue),fInfo.getName());
    }

    private void getDataFromIntent(){
        fInfo =(JSONObjects.FileInfo) getIntent().getSerializableExtra(SuggestWordsActivityKt.INNER_INTENT_GIVEN_LIST);
    }

    private void fillToolbar(){
        if (suggestWords!=null){
            getSupportActionBar().setTitle(suggestWords.getName());
            mDescription.setText(suggestWords.getDescription());
            imageView.setImageBitmap(UtilsKt.getBitmapFromAssets(getApplicationContext(),"image/"+suggestWords.getImage()));
        }
    }

    private void onSelectionChanged() {

        if (selectionTracker.getSelection().size() > 0) {

            if (actionMode == null) {
                appBarLayout.setExpanded(false);
                blockAppBar(true);
                actionMode = toolbar.startActionMode(this);

                ((SuggestWordsAdapter) recycler.getAdapter()).selectionStart();
                recycler.getAdapter().notifyDataSetChanged();

            }
            actionMode.setTitle(Integer.toString(((SuggestWordsAdapter) recycler.getAdapter()).getSelectedWords().length));

            if (!actionMode.getMenu().getItem(1).isVisible()){
                actionMode.getMenu().getItem(1).setVisible(true);
            }

        }else if(actionMode != null) {
            selectionTracker.clearSelection();
            actionMode.finish();
            ((SuggestWordsAdapter) recycler.getAdapter()).selectionEnd();
            recycler.getAdapter().notifyDataSetChanged();
        }
    }

    private void blockAppBar(boolean state){
        recycler.setNestedScrollingEnabled(!state);
        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior)
                ((CoordinatorLayout.LayoutParams)appBarLayout.getLayoutParams())
                        .getBehavior();

        if (state){
            behavior.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
                @Override
                public boolean canDrag(@NonNull AppBarLayout appBarLayout) {
                    return false;
                }
            });
        }else {
            behavior.setDragCallback(null);
        }
    }

    private void showAddDialog(){
        List<JSONObjects.Word> words = Arrays.asList(((SuggestWordsAdapter) recycler.getAdapter()).getSelectedWords());
        selectionTracker.clearSelection();
        AddWordsSheetDialog dialog = new AddWordsSheetDialog();
        dialog.setWords(words);
        dialog.setCallback(new AddWordFragment.AddWordsCallbacks() {
            @Override
            public void onError() {
                dialog.dismiss();
                showAddStateDialog(false);

            }

            @Override
            public void onClose() {
                dialog.dismiss();
            }

            @Override
            public void onAdded() {
                dialog.dismiss();
                showAddStateDialog(true);

            }
        });
        dialog.show(getSupportFragmentManager(),"addWordsSheetDialog");
    }

    private void showAddStateDialog(boolean isSuccess){
        DialogFragment fragment;
        if (isSuccess)
            fragment = new SuccessDialog();
        else
            fragment = new ErrorDialog();
        fragment.show(getSupportFragmentManager(),"message_dialog");
    }

    private void onItemClicked(View view){
        RecyclerView.ViewHolder viewHolder= recycler.getChildViewHolder(view);

        if (actionMode==null && viewHolder instanceof SuggestWordsAdapter.SuggestItemHolder){

            JSONObjects.Word word=((SuggestWordsAdapter.SuggestItemHolder)viewHolder).getWord();

            if(word.getExamples()!=null&& !word.getExamples().isEmpty()) {

                startView=view;

                getExpandedViewHelper(expandedView).bind(word);
                MaterialContainerTransform transform = buildContainerTransform(false);
                transform.setStartView(view);
                transform.setEndView(expandedView);
                transform.addTarget(expandedView);

                TransitionManager.beginDelayedTransition(root, transform);
                coweredView.setVisibility(View.VISIBLE);
                coweredView.setOnTouchListener(this::onOutTouch);
                expandedView.setVisibility(View.VISIBLE);
                view.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void onBlockClicked(View view){
        if (blocksState != null){
            RecyclerView.ViewHolder viewHolder= recycler.getChildViewHolder(view);
            if (actionMode==null && viewHolder instanceof SuggestWordsAdapter.BlockHeaderHolder){
                SuggestWordsAdapter adapter = ((SuggestWordsAdapter)recycler.getAdapter());

                int position = ((SuggestWordsAdapter.BlockHeaderHolder)viewHolder).getDetails().getPosition();
                int index = adapter.blockIndex(position);

                blocksState[index] = !blocksState[index];
                adapter.isExpandedBlock(index, blocksState[index]);

                adapter.notifyDataSetChanged();
            }
        }
    }

    private boolean onOutTouch(View v, MotionEvent event){
        root.setOnClickListener(null);

        coweredView.setVisibility(View.GONE);
        coweredView.setOnTouchListener(null);

        if (startView!=null){
            MaterialContainerTransform transform = buildContainerTransform(true);
            transform.setStartView(expandedView);
            transform.setEndView(startView);
            transform.addTarget(startView);

            TransitionManager.beginDelayedTransition(root, transform);

            startView.setVisibility(View.VISIBLE);
            expandedView.setVisibility(View.INVISIBLE);
        }
        startView=null;
        return true;
    }

    @NonNull
    @SuppressLint("WrongConstant")
    private MaterialContainerTransform buildContainerTransform(boolean in) {
        Context context = getApplicationContext();
        MaterialContainerTransform transform = new MaterialContainerTransform(context, in);
        transform.setDuration(500);
        transform.setFitMode(MaterialContainerTransform.FADE_MODE_IN);
        transform.setPathMotion(new MaterialArcMotion());
        transform.setDrawingViewId(root.getId());
        if (!in)
            transform.setScrimColor(Color.TRANSPARENT);
        else
            transform.setScrimColor(Color.TRANSPARENT);

        return transform;
    }

    private void loadWords(){

        try {
            InputStream ips = getApplicationContext().getAssets().open("suggest/"+fInfo.getFileName().toLowerCase());
            suggestWords =  JSONHelper.INSTANCE.loadWordList(ips);;
            blocksState = new boolean[suggestWords.getWords().size()];
            Arrays.fill(blocksState, true);

            fillToolbar();
            RecyclerView.Adapter adapter= recycler.getAdapter();
            if (adapter instanceof SuggestWordsAdapter){
                ((SuggestWordsAdapter)adapter).setGroups( suggestWords.getWords());
                adapter.notifyDataSetChanged();
            }

        }catch (IOException e){
            Log.e("SuggestWordsActivity",e.toString(),e);
            finish();
        }
    }

    private static ExpandedViewHelper getExpandedViewHelper(View view){
        ExpandedViewHelper helper=(ExpandedViewHelper) view.getTag(R.id.expanded_view_helper);
        if (helper==null){
            helper=new ExpandedViewHelper(view);
            view.setTag(R.id.expanded_view_helper,helper);
        }
        return helper;
    }

    private static class ExpandedViewHelper{
        private final View view;

        private TextView chnText;
        private TextView pinText;
        private TextView trnText;
        private TextView descriptionText;

        private ListView examplesList;

        private View descriptionContainer;
        private View exampleContainer;

        ExpandedViewHelper(View view){
            this.view=view;
            init();
        }

        private void init(){
            view.setClickable(false);
            chnText=view.findViewById(R.id.chin_text);
            pinText=view.findViewById(R.id.pinyin_text);
            trnText=view.findViewById(R.id.translate_text);
            descriptionText=view.findViewById(R.id.description_text);
            examplesList =view.findViewById(R.id.example_list);
            descriptionContainer=view.findViewById(R.id.description_container);
            exampleContainer=view.findViewById(R.id.example_container);
            examplesList.setClickable(false);
        }

        public void bind(JSONObjects.Word word){
            chnText.setText(word.getWord());
            pinText.setText(word.getPinyin());
            trnText.setText(word.getTranslation());

            /** TODO : добавить вывод описания когда будет модефицировани контенер */
            if (false){
                descriptionContainer.setVisibility(View.VISIBLE);
                descriptionText.setText("");
            }

            if (word.getExamples()!=null&&!word.getExamples().isEmpty()){
                exampleContainer.setVisibility(View.VISIBLE);
                JSONObjects.Example[] examples=new JSONObjects.Example[ word.getExamples().size()];
                word.getExamples().toArray(examples);
                examplesList.setAdapter(new ExampleAdapter(view.getContext(),examples));
            }

        }

        private static class ExampleAdapter extends ArrayAdapter<JSONObjects.Example>{
            private final JSONObjects.Example[] examples;

            public ExampleAdapter(@NonNull Context context, @NonNull JSONObjects.Example[] objects) {
                super(context, -1, objects);

                this.examples=objects.clone();
            }

            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                LayoutInflater inflater=(LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view=inflater.inflate(R.layout.example_holder,parent,false);

                view.setClickable(false);

                TextView chn=view.findViewById(R.id.chin);
                TextView pin=view.findViewById(R.id.pinyin);
                TextView trn=view.findViewById(R.id.language);

                chn.setText(examples[position].getChinese());
                pin.setText(examples[position].getPinyin());
                trn.setText(examples[position].getTranslation());

                return view;
            }
        }
    }

    public static class SuccessDialog extends DialogFragment{
        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

            builder.setTitle(getString(R.string.success_insert_words));

            builder.setPositiveButton(android.R.string.cancel,null);
            return builder.create();
        }
    }
    public static class ErrorDialog extends DialogFragment{
        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

            builder.setTitle(getString(R.string.error_insert_words));

            builder.setPositiveButton(android.R.string.cancel,null);
            return builder.create();
        }
    }
}
