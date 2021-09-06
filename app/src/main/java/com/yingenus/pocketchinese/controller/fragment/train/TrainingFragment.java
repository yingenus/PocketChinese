package com.yingenus.pocketchinese.controller.fragment.train;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.yingenus.pocketchinese.R;
import com.yingenus.pocketchinese.controller.KeyboardCallbackInterface;
import com.yingenus.pocketchinese.controller.Settings;
import com.yingenus.pocketchinese.model.LanguageCase;
import com.yingenus.pocketchinese.model.database.pocketDB.PocketBaseHelper;
import com.yingenus.pocketchinese.model.database.PocketDBOpenManger;
import com.yingenus.pocketchinese.model.words.statistic.FibRepeatHelper;
import com.yingenus.pocketchinese.model.words.RepeatManager;
import com.yingenus.pocketchinese.model.database.pocketDB.StudyWord;
import com.yingenus.pocketchinese.view.MultiColorProgressBar;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TrainingFragment extends Fragment{
    private MultiColorProgressBar progressBar;
    private ViewPager2 viewPager;

    private RepeatManager repeatManager;

    private LanguageCase trainingLang;
    private UUID studyList;
    private int block;

    private MultiColorProgressBar.ProgressElement good;
    private MultiColorProgressBar.ProgressElement bed;

    private TrainingFragment(){
        super(R.layout.training_fragment);
    }

    public TrainingFragment(LanguageCase languageCase,UUID studyList,int block){
        this();
        trainingLang =languageCase;
        this.studyList =studyList;
        this.block =block;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PocketBaseHelper helper = (PocketBaseHelper) PocketDBOpenManger.getHelper(getContext());
        repeatManager = new RepeatManager(helper,
                studyList, new FibRepeatHelper(), trainingLang, block);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        repeatManager.safe();
        PocketDBOpenManger.releaseHelper();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Activity activity = getActivity();
        if (!(activity instanceof KeyboardCallbackInterface)){
            throw new RuntimeException("TrainingFragment can only be attached to implemented KeyboardCallbackInterface Activity");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= super.onCreateView(inflater, container, savedInstanceState);

        assert view != null;
        progressBar =view.findViewById(R.id.train_color_bar);
        viewPager =view.findViewById(R.id.train_view_pager);

        good=new MultiColorProgressBar.ProgressElement();
        good.setProgressValue(0);
        good.setProgressBackground(R.drawable.tests);
        good.setProgressColor(getResources().getColor(R.color.success_color_9));
        bed=new MultiColorProgressBar.ProgressElement();
        bed.setProgressValue(0);
        bed.setProgressBackground(R.drawable.tests);
        bed.setProgressColor(getResources().getColor(R.color.success_color_4));

        progressBar.addProgressElement(good);
        progressBar.addProgressElement(bed);

        Adapter adapter =new Adapter(getBuilder());
        adapter.setDisclosedClicked(this::onGiveUpClicked);
        adapter.setOnNextClicked(this::onNextClicked);
        adapter.setSkipClicked(this::onSkipClicked);
        adapter.setKeyboardCallback((KeyboardCallbackInterface) getActivity());
        viewPager.setAdapter(adapter);
        viewPager.setUserInputEnabled(false);
        viewPager.setOffscreenPageLimit(1);

        initWords();

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        repeatManager.safe();
    }
    @Override
    public void onResume() {
        super.onResume();
    }

    private  void initWords(){
        updatePresenter();
        updateState();
    }

    private void updateState(){
        List<StudyWord> words = repeatManager.getRepeatQueueSnapshot();
        if (!words.isEmpty()) {
            ((Adapter)viewPager.getAdapter()).setStudyWords(words);
            viewPager.getAdapter().notifyDataSetChanged();
            viewPager.setCurrentItem(0);
        }else {
            itsWasLast();
        }
    }

    public void onNextClicked(View view) {
        if (repeatManager !=null){
            StudyWord word= ((Adapter) viewPager.getAdapter()).mWords.get(viewPager.getCurrentItem());

            RecyclerView recyclerView=(RecyclerView) viewPager.getChildAt(0);
            RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(viewPager.getCurrentItem());
            if (holder instanceof TrainView){
                TrainView trainView = (TrainView) holder;
                String answer = (String) trainView.getAnswer();
                Boolean isWasDisclosed = (Boolean) trainView.isDisclosed();

                if (answer == null && isWasDisclosed == null){
                    return;
                }

                if (isWasDisclosed == null || !isWasDisclosed ){
                    boolean isCorrect;
                    if (answer != null){
                        isCorrect = repeatManager.checkWord(word,answer);
                    }else {
                        isCorrect = repeatManager.checkWord(word,"");
                    }

                    if (isCorrect){
                        switchToNextDelayed(500);
                    }

                }else if(isWasDisclosed != null && isWasDisclosed){
                    switchToNextDelayed(0);
                }
            }
            updatePresenter();
        }

    }

    public void onSkipClicked(View view) {
        StudyWord word= ((Adapter) viewPager.getAdapter()).mWords.get(viewPager.getCurrentItem());
        repeatManager.skipWord(word);
        updatePresenter();
        switchToNextDelayed(0);

    }

    public void onGiveUpClicked( View view) {
        StudyWord word= ((Adapter) viewPager.getAdapter()).mWords.get(viewPager.getCurrentItem());
        repeatManager.discloseWord(word);
        updatePresenter();
    }

    private void switchToNextDelayed(long delay){
        switchToNext();
    }

    private void switchToNext(){

        int position= viewPager.getCurrentItem();
        position++;

        if (position< ((Adapter) viewPager.getAdapter()).getItemCount()){
            viewPager.setCurrentItem(position);
        }else{
            updateState();
        }
    }

    private void itsWasLast(){
        getActivity().finish();
    }

    private void updatePresenter(){
        progressBar.setMaxProgress(repeatManager.getTotal());
        good.setProgressValue(repeatManager.getPassed());
        bed.setProgressValue(repeatManager.getFailed());
        progressBar.notifiProgressChanged();
    }

    private TrainViewBuilder getBuilder(){
        switch (trainingLang){
            case Trn:
                return new TrnTrainView.Builder();
            case Pin:
                return new PinTrainView.Builder();
            case Chin:
                return new ChnTrainView.Builder();
        }
        return null ;
    }

    private static class Adapter extends RecyclerView.Adapter<TrainView>{

        private View.OnClickListener onNext;
        private View.OnClickListener onSkip;
        private View.OnClickListener onDisclosed;
        private KeyboardCallbackInterface keyboardCallback;


        private List<StudyWord> mWords= new ArrayList();
        private TrainViewBuilder viewBuilder;

        Adapter(TrainViewBuilder builder){
            this.viewBuilder = builder;
        }

        public void setStudyWords(List<StudyWord> words){
            this.mWords=words;
        }

        @NonNull
        @Override
        public TrainView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            TrainView holder =  viewBuilder.build((LayoutInflater) parent.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE),parent);
            holder.setListeners(onNext,onSkip,onDisclosed);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull TrainView holder, int position) {
            StudyWord word=mWords.get(position);
            holder.bind(word.getChinese(),word.getPinyin(),word.getTranslate());
            if (Settings.INSTANCE.useAppKeyboard(holder.itemView.getContext()) && holder instanceof PinTrainView){
                addKeyBoardObserver(holder);
            }
        }

        @SuppressLint("ClickableViewAccessibility")
        private void addKeyBoardObserver(TrainView holder){
            if (keyboardCallback != null)
            {
                EditText editText=holder.pinView;
                if (keyboardCallback != null){
                    editText.setOnClickListener(keyboardCallback::showAppKeyboard);
                }
                editText.setOnFocusChangeListener((v, hasFocus) -> {
                    EditText editText1 =(EditText) v;
                    if (keyboardCallback!= null){
                        if (hasFocus){
                            keyboardCallback.showAppKeyboard(editText1);
                        }else {
                            keyboardCallback.hideAppKeyboard(editText1);
                        }
                    }
                    editText1.setSelection(editText1.length());
                });
                editText.setOnTouchListener((v, event) -> {
                    EditText editText12 =(EditText) v;
                    int inputType= editText12.getInputType();
                    editText12.setInputType(InputType.TYPE_NULL);
                    editText12.onTouchEvent(event);
                    editText12.setInputType(inputType);
                    if (keyboardCallback!= null) {
                        keyboardCallback.showAppKeyboard(editText12);
                    }
                    editText12.setSelection(editText12.length());
                    return true;
                });
            }
        }

        @Override
        public int getItemCount() {
            return mWords.size();
        }

        @Override
        public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
            super.onDetachedFromRecyclerView(recyclerView);
            onNext = null;
            onDisclosed = null;
            onSkip = null;
            keyboardCallback = null;
        }

        private void setOnNextClicked(View.OnClickListener listener){
            onNext = listener;
        }
        private void setSkipClicked(View.OnClickListener listener){
            onSkip = listener;
        }
        private void setDisclosedClicked(View.OnClickListener listener){
            onDisclosed = listener;
        }
        private void setKeyboardCallback(KeyboardCallbackInterface callback){
            keyboardCallback = callback;
        }
    }

}
