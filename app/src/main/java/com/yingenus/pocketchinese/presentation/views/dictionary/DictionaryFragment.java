package com.yingenus.pocketchinese.presentation.views.dictionary;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yingenus.pocketchinese.R;
import com.yingenus.pocketchinese.controller.InPutUtilsKt;
import com.yingenus.pocketchinese.controller.Settings;
import com.yingenus.pocketchinese.controller.dialog.CharacterSheetDialog;
import com.yingenus.pocketchinese.controller.holders.ViewViewHolder;
import com.yingenus.pocketchinese.di.ServiceLocator;
import com.yingenus.pocketchinese.domain.dto.ChinChar;
import com.yingenus.pocketchinese.domain.repository.ChinCharRepository;
import com.yingenus.pocketchinese.domain.repository.ExampleRepository;
import com.yingenus.pocketchinese.domain.repository.ToneRepository;
import com.yingenus.pocketchinese.presenters.DictionaryPresenter;
import com.yingenus.pocketchinese.presenters.UtilsKt;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButtonToggleGroup;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;


public class DictionaryFragment extends Fragment implements DictionaryInterface {
    private static final String CHIN_CHAR ="com.example.pocketchinese.dictionaryfragment.showedchin";


    private final DictionaryPresenter presenter;
    private EditText searchPanel;
    private RecyclerView dictionaryRecycle;
    private TextView headerText;
    private MaterialButtonToggleGroup toggleGroup;

    private Observable<String> textInputObserver;

    private List<com.yingenus.pocketchinese.domain.dto.ChinChar> history;

    private ChinCharRepository chinCharRepository;
    private ExampleRepository exampleRepository;
    private ToneRepository toneRepository;

    public DictionaryFragment(ChinCharRepository chinCharRepository, ExampleRepository exampleRepository,ToneRepository toneRepository){
        super(R.layout.dictionary_fragment);
        presenter = new DictionaryPresenter( this, chinCharRepository);
        this.chinCharRepository = chinCharRepository;
        this.exampleRepository =  exampleRepository;
        this.toneRepository = toneRepository;
    }

    static class CharacterFragmentFactory extends FragmentFactory{
        private com.yingenus.pocketchinese.domain.dto.ChinChar showedChinChar;
        private ChinCharRepository chinCharRepository;
        private ExampleRepository exampleRepository;
        private ToneRepository toneRepository;

        public void setShowedChinChar(com.yingenus.pocketchinese.domain.dto.ChinChar showedChinChar, ChinCharRepository charRepository, ExampleRepository exampleRepository, ToneRepository toneRepository) {
            this.showedChinChar = showedChinChar;
            this.chinCharRepository = charRepository;
            this.exampleRepository = exampleRepository;
            this.toneRepository = toneRepository;
        }

        public com.yingenus.pocketchinese.domain.dto.ChinChar getShowedChinChar() {
            return showedChinChar;
        }

        @NonNull
        @Override
        public Fragment instantiate(@NonNull ClassLoader classLoader, @NonNull String className) {
            if(className.equals(CharacterSheetDialog.class.getName()))
                return new CharacterSheetDialog(showedChinChar,chinCharRepository,exampleRepository,toneRepository);
            return super.instantiate(classLoader, className);
        }
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        CharacterFragmentFactory factory = new CharacterFragmentFactory();
        if (savedInstanceState != null){
            int id = savedInstanceState.getInt(CHIN_CHAR);
            //factory.setShowedChinChar((com.yingenus.pocketchinese.domain.dto.ChinChar) savedInstanceState.getSerializable(CHIN_CHAR));
            com.yingenus.pocketchinese.domain.dto.ChinChar chinChar = chinCharRepository.findById(id);
            factory.setShowedChinChar(chinChar, chinCharRepository, exampleRepository,toneRepository);
        }
        getChildFragmentManager().setFragmentFactory(factory);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        FragmentFactory ff = getChildFragmentManager().getFragmentFactory();
        if (ff instanceof CharacterFragmentFactory){
            com.yingenus.pocketchinese.domain.dto.ChinChar showed = ((CharacterFragmentFactory)ff).getShowedChinChar();
            if (showed != null){
                outState.putInt(CHIN_CHAR,showed.getId());
            }
        }
    }

    @SuppressLint("ResourceType")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        searchPanel = view.findViewById(R.id.top_bar_search_text);
        dictionaryRecycle = view.findViewById(R.id.expanded_recyclerview);

        toggleGroup = view.findViewById(R.id.button_group);
        toggleGroup.check(R.id.fuzzy);

        headerText = view.findViewById(R.id.header_text);
        showDefaultHeader();

        AppBarLayout appBar = view.findViewById(R.id.app_bar);
        appBar.setStatusBarForegroundColor(getResources().getColor(R.color.appColor));

        RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext());
        manager.setAutoMeasureEnabled(true);

        dictionaryRecycle.setLayoutManager(manager);
        dictionaryRecycle.addItemDecoration(new BoundsDecorator());
        dictionaryRecycle.addOnScrollListener( new HistoryInvisibleObserver());
        //dictionaryRecycle.setAdapter(new UnFilledAdapter());

        setResults(Results.NoQuery.INSTANCE);

        setSearchTypeChangedListener();

        UnFilledAdapter unFilledAdapter = new UnFilledAdapter();
        unFilledAdapter.setMessageView(R.layout.dictionary_start_msg);
        unFilledAdapter.setHistory(presenter.getHistory(getContext()));

        presenter.onCreate(getContext());

        registerHideTouchListener(view);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();

    }

    private void initObserver(){
        Observable<String> observable = Observable.create(subscribe ->{
            searchPanel.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    subscribe.onNext(s.toString());
                }
            });
        });
        textInputObserver = observable.publish().autoConnect();
    }

    private void setSearchTypeChangedListener(){
        toggleGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (checkedId == R.id.fuzzy && isChecked){
                    presenter.searchTypeChanged(States.SORT_FUZZY);
                }else if (checkedId == R.id.match && isChecked){
                    presenter.searchTypeChanged(States.SORT_MATCH);
                }
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        tryUpdateHistory();
    }

    @Override
    public void onStop() {
        super.onStop();
    }



    private void tryUpdateHistory(){
        history = presenter.getHistory(getContext());

        if(dictionaryRecycle.getAdapter() instanceof UnFilledAdapter){
            UnFilledAdapter adapter = (UnFilledAdapter) dictionaryRecycle.getAdapter();

            if (searchPanel.getText().length() ==0){
                adapter.setMessageView(R.layout.dictionary_start_msg);
            }
            adapter.setHistory(history);
            adapter.notifyDataSetChanged();
        }
    }

    private void onChnCharClicked(com.yingenus.pocketchinese.domain.dto.ChinChar chinChar){
        ((CharacterFragmentFactory)getChildFragmentManager().getFragmentFactory()).setShowedChinChar(chinChar,chinCharRepository,exampleRepository,toneRepository);
        BottomSheetDialogFragment bottomSheetDialog=(BottomSheetDialogFragment)
                getChildFragmentManager().getFragmentFactory().instantiate(getContext()
                                .getClassLoader(),CharacterSheetDialog.class.getName());

        bottomSheetDialog.show(getChildFragmentManager(),"testtestestestes");
        addToSetting(chinChar);
        tryUpdateHistory();
    }

    private void addToSetting( com.yingenus.pocketchinese.domain.dto.ChinChar chinChar){
        Settings.INSTANCE.addSearchItem(getContext(),chinChar.getId());
    }

    private void registerHideTouchListener(View view){
        if (! (view instanceof EditText)){
            view.setOnTouchListener((View v, MotionEvent event) ->{
                View focusView = getActivity().getCurrentFocus();
                if (focusView != null){
                    InPutUtilsKt.hideKeyboard(focusView);
                }
                return false;
            });
        }

        if (view instanceof ViewGroup){
            ViewGroup group = (ViewGroup) view;
            for(int child = 0; child < group.getChildCount(); child++){
                registerHideTouchListener(group.getChildAt(child));
            }
        }

    }

    class HistoryInvisibleObserver extends RecyclerView.OnScrollListener{
        private boolean isWasVisible = true;

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            if(recyclerView.getAdapter() instanceof UnFilledAdapter && recyclerView.getAdapter().getItemCount() > 3){
                RecyclerView.LayoutManager rm = recyclerView.getLayoutManager();

                View history = rm.findViewByPosition(1);

                boolean isVisible;
                if (history == null){
                    isVisible = false;
                }else {
                    isVisible = rm.isViewPartiallyVisible(history,true,true);
                }

                if (isWasVisible != isVisible){
                    if (isVisible){
                        showDefaultHeader();
                    }else {
                        showHistoryHeader();
                    }
                }

                isWasVisible = isVisible;
            }
        }
    }

    private void showHistoryHeader(){
        headerText.setText( getText(R.string.dictionary_history_header));
    }
    private void showDefaultHeader(){
        headerText.setText( getText(R.string.dictionary_header));
    }

    @Override
    public void setHistory(@NotNull List<ChinChar> history) {

    }

    @Override
    public void setResults(@NotNull Results results) {
        RecyclerView.Adapter adapter = dictionaryRecycle.getAdapter();


        if (results instanceof Results.NoQuery || results instanceof  Results.NoMatches){
            UnFilledAdapter unAdapter;

            if (!(adapter instanceof UnFilledAdapter)){
                unAdapter = new UnFilledAdapter();
                unAdapter.setChinCharListener(this::onChnCharClicked);
                dictionaryRecycle.setAdapter(unAdapter);
                unAdapter.setHistory(history);
            }else {
                unAdapter = (UnFilledAdapter) adapter;
            }
            if ( results instanceof Results.NoQuery){
                unAdapter.setMessageView(R.layout.dictionary_start_msg);
            }else {
                unAdapter.setMessageView(R.layout.holder_empty_dictionary_item);
            }
            unAdapter.notifyDataSetChanged();
        }else if (results instanceof Results.Matches){
            DictionaryItemAdapter diAdapter;
            if (!(adapter instanceof DictionaryItemAdapter)) {
                diAdapter = new DictionaryItemAdapter();
                diAdapter.setChinCharListener(this::onChnCharClicked);
                dictionaryRecycle.setAdapter(diAdapter);
            }else {
                diAdapter = (DictionaryItemAdapter) adapter;
            }
            diAdapter.setChinCharacters(((Results.Matches) results).getChars());
            diAdapter.notifyDataSetChanged();
            showDefaultHeader();
        }
    }

    @NotNull
    @Override
    public Observable<String> getSearchObserver() {
        if (textInputObserver == null){
            initObserver();
        }
        return textInputObserver;
    }

    @NotNull
    @Override
    public String getSearchQuery() {
        return searchPanel.getText().toString();
    }

    @SuppressLint("ResourceType")
    @Override
    public void setSearchStates(@NotNull States state) {
        if (state == States.SORT_FUZZY)
            toggleGroup.check(R.id.fuzzy);
        if (state == States.SORT_MATCH)
            toggleGroup.check(R.id.match);
    }

    @Override
    public void showChinChar(@NotNull com.yingenus.pocketchinese.domain.dto.ChinChar chinChar) {
        onChnCharClicked(chinChar);
    }

    private static class DictionaryItemHolder extends RecyclerView.ViewHolder {
        private com.yingenus.pocketchinese.domain.dto.ChinChar chinChar;

        private final TextView mChnText;
        private final TextView mPinyinText;
        private final TextView mTrnText;

        public DictionaryItemHolder(LayoutInflater layoutInflater, ViewGroup parent) {
            super(layoutInflater.inflate(R.layout.holder_dictionary_item,parent,
                    false));

            mChnText = super.itemView.findViewById(R.id.dictionary_item_chin_text);
            mPinyinText = super.itemView.findViewById(R.id.dictionary_item_pinyin);
            mTrnText = super.itemView.findViewById(R.id.dictionary_item_second_language);
        }

        public void bind(com.yingenus.pocketchinese.domain.dto.ChinChar chinChar){
            this.chinChar=chinChar;
            mChnText.setText(chinChar.getChinese());
            mPinyinText.setText(chinChar.getPinyin());
            mTrnText.setText(compose(chinChar.getTranslation()));

        }

        private String compose(String[] trns){
            String result = "";

            int counter = 1;
            for (String trn : trns) {

                if (trn.contains("link") || !UtilsKt.isRussian(trn)) {
                    continue;
                }
                if (trns.length == 1)
                    result +=  " "+trn+"  ";
                else
                result += counter + ") " + trn+"  ";
                counter++;
            }
            
            return result;
        }

        public com.yingenus.pocketchinese.domain.dto.ChinChar getChinChar() {
            return chinChar;
        }
    }

    private class DictionaryItemAdapter extends AdapterWithChinCharItems<DictionaryItemHolder>{


        private List<com.yingenus.pocketchinese.domain.dto.ChinChar> mChinChars = Collections.EMPTY_LIST;


        DictionaryItemAdapter(){
        }

        public void setChinCharacters(List<com.yingenus.pocketchinese.domain.dto.ChinChar> chinChars) {
            mChinChars = chinChars;
        }

        @NonNull
        @Override
        public DictionaryItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater=LayoutInflater.from(getActivity());
            DictionaryItemHolder holder = new DictionaryItemHolder(layoutInflater, parent);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && mChinChars.size() > position){
                        if (mListener != null){
                            mListener.onChinCharClick( mChinChars.get(position));
                        }
                    }
                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull DictionaryItemHolder holder, int position) {
            com.yingenus.pocketchinese.domain.dto.ChinChar chinChar=mChinChars.get(position);
            holder.bind(chinChar);
        }

        @Override
        public int getItemCount() {
            return mChinChars.size();
        }
    }

    private static class UnFilledAdapter extends AdapterWithChinCharItems<RecyclerView.ViewHolder>{
        private static final int MESSAGE = 0;
        private static final int HISTORY_HEADER = 1;
        private static final int HISTORY = 2;

        private List<com.yingenus.pocketchinese.domain.dto.ChinChar> mHistory;
        private int massageContainerId = View.NO_ID;
        private View mMassage;
        private int mViewRes = -1;
        private boolean shouldUpdateMassage = true;

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (viewType == MESSAGE){
                return getMesViewHolder(inflater);
            }
            if (viewType == HISTORY_HEADER){
                return new ViewViewHolder(R.layout.history_header,inflater);
            }
            else {
                DictionaryItemHolder holder = new DictionaryItemHolder(inflater, parent);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = holder.getAdapterPosition()-2;
                        if (position != RecyclerView.NO_POSITION && mHistory.size() > position){
                            if (mListener != null){
                                mListener.onChinCharClick(mHistory.get(position));
                            }
                        }
                    }
                });
                return holder;
            }

        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof ViewViewHolder && getItemViewType(position) == MESSAGE){
                if (shouldUpdateMassage){
                    shouldUpdateMassage = false;
                    FrameLayout container = ((FrameLayout) holder.itemView.findViewById(massageContainerId));
                    if (container != null){
                        container.removeAllViews();
                        container.addView(getMassageView(holder.itemView.getContext()));
                    }
                }
            }
            if (holder instanceof DictionaryItemHolder){
                ((DictionaryItemHolder) holder).bind(mHistory.get(position - 2));
            }
        }

        @Override
        public int getItemCount() {
            if (mHistory != null && !mHistory.isEmpty()){
                return 2+mHistory.size();
            }else {
                return 1;
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0){
                return MESSAGE;
            }
            if (position == 1){
                return HISTORY_HEADER;
            }
            if (position > 1){
                return HISTORY;
            }
            throw new RuntimeException("UnFilledAdapter no such type");
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        private RecyclerView.ViewHolder getMesViewHolder(LayoutInflater inflater){

            FrameLayout container = new FrameLayout(inflater.getContext());
            container.setLayoutParams(new
                    FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            massageContainerId = View.generateViewId();
            container.setId(massageContainerId);
            return new ViewViewHolder(container);
        }

        private View getMassageView(Context context){
            if (mMassage != null){
                return mMassage;
            }
            else if (mViewRes != -1){
                return ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(mViewRes,null);
            }
            else return new View(context);
        }

        public void setMessageView(View view){
            if (mMassage != view){
                shouldUpdateMassage = true;
                mMassage = view;
            }
        }

        public void setMessageView(@LayoutRes int res){
            if (mViewRes != res){
                shouldUpdateMassage = true;
                mViewRes= res;
            }
        }

        public void setHistory( List<com.yingenus.pocketchinese.domain.dto.ChinChar> history){
            mHistory = history;
        }

        public List<com.yingenus.pocketchinese.domain.dto.ChinChar> getHistory(){
            return mHistory;
        }

    }

    private abstract static class AdapterWithChinCharItems<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH>{
        protected OnChinCharClickListener mListener;

        public void setChinCharListener(OnChinCharClickListener listener){
            this.mListener = listener;
        }
    }

    private interface OnChinCharClickListener{
        void onChinCharClick(com.yingenus.pocketchinese.domain.dto.ChinChar chinChar);
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
