package com.yingenus.pocketchinese.presentation.views.dictionary;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

import com.yingenus.pocketchinese.ISettings;

import com.yingenus.pocketchinese.PocketApplication;
import com.yingenus.pocketchinese.R;
import com.yingenus.pocketchinese.controller.InPutUtilsKt;
import com.yingenus.pocketchinese.Settings;

import com.yingenus.pocketchinese.domain.entitiys.UtilsVariantParams;
import com.yingenus.pocketchinese.presentation.views.character.CharacterSheetDialog;
import com.yingenus.pocketchinese.view.holders.ViewViewHolder;

import com.yingenus.pocketchinese.domain.usecase.WordsSearchUseCase;

import com.yingenus.pocketchinese.presentation.dialogs.radicalsearch.RadicalSearchDialog;
import com.yingenus.pocketchinese.domain.dto.DictionaryItem;
import com.yingenus.pocketchinese.domain.repository.DictionaryItemRepository;
import com.yingenus.pocketchinese.domain.repository.ExampleRepository;
import com.yingenus.pocketchinese.domain.repository.ToneRepository;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButtonToggleGroup;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import kotlin.Pair;


public class DictionaryFragment extends Fragment implements DictionaryInterface {
    private static final String CHIN_CHAR ="com.example.pocketchinese.dictionaryfragment.showedchin";

    @Inject
    public DictionaryPresenter.Factory dictionaryPresenterFactory;

    private DictionaryPresenter presenter;
    private EditText searchPanel;
    private RecyclerView dictionaryRecycle;
    private TextView headerText;
    private MaterialButtonToggleGroup toggleGroup;
    private View searchByRadical;

    private Observable<String> textInputObserver;

    private List<DictionaryItem> history;

    @Inject public DictionaryItemRepository dictionaryItemRepository;

    public DictionaryFragment(){
        super(R.layout.dictionary_fragment);
    }

    private boolean isAnimationAnimation = false;
    private boolean isSearchEmptyBannerShowing = false;
    private boolean isSearchNothingBannerShowing = false;
    private boolean isPresenterInited = false;


    static class CharacterFragmentFactory extends FragmentFactory{

        private DictionaryItem showedDictionaryItem;

        public void setShowedChinChar(DictionaryItem showedDictionaryItem) {
            this.showedDictionaryItem = showedDictionaryItem;
        }

        public com.yingenus.pocketchinese.domain.dto.DictionaryItem getShowedDictionaryItem() {
            return showedDictionaryItem;
        }

        @NonNull
        @Override
        public Fragment instantiate(@NonNull ClassLoader classLoader, @NonNull String className) {
            if(className.equals(CharacterSheetDialog.class.getName()))
                return new CharacterSheetDialog(showedDictionaryItem);
            return super.instantiate(classLoader, className);
        }
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        CharacterFragmentFactory factory = new CharacterFragmentFactory();
        PocketApplication.Companion.getAppComponent().injectDictionaryFragment(this);
        presenter = dictionaryPresenterFactory.create(this);
        if (savedInstanceState != null){
            int id = savedInstanceState.getInt(CHIN_CHAR);
            DictionaryItem dictionaryItem = dictionaryItemRepository.findById(id);
            factory.setShowedChinChar(dictionaryItem);
        }
        getChildFragmentManager().setFragmentFactory(factory);

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        FragmentFactory ff = getChildFragmentManager().getFragmentFactory();
        if (ff instanceof CharacterFragmentFactory){
            DictionaryItem showed = ((CharacterFragmentFactory)ff).getShowedDictionaryItem();
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

        searchByRadical = view.findViewById(R.id.radical_layout);
        searchByRadical.setOnClickListener(v -> showChooseCharacterDialog());

        toggleGroup = view.findViewById(R.id.button_group);
        toggleGroup.check(R.id.fuzzy);

        headerText = view.findViewById(R.id.header_text);
        headerText.setText(inflater.getContext()
                .getResources()
                .getText(R.string.dictionary_header));

        AppBarLayout appBar = view.findViewById(R.id.app_bar);
        appBar.setStatusBarForegroundColor(getResources().getColor(R.color.appColor));

        RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext());
        manager.setAutoMeasureEnabled(true);

        dictionaryRecycle.setLayoutManager(manager);
        dictionaryRecycle.addItemDecoration(new BoundsDecorator());
        dictionaryRecycle.addOnScrollListener( new HistoryInvisibleObserver());
        //dictionaryRecycle.setAdapter(new UnFilledAdapter());

        showSearchEmptyQuery(true);

        setSearchTypeChangedListener();

        UnFilledAdapter unFilledAdapter = new UnFilledAdapter();
        unFilledAdapter.setMessageView(R.layout.dictionary_start_msg);
        //unFilledAdapter.setHistory(presenter.getHistory(getContext()));

        //presenter.onCreate();

        registerHideTouchListener(view);

        PocketApplication.Companion.postStartActivity(getActivity(),false)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> {
                            presenter.onCreate();
                            isPresenterInited = true;
                        }
                ,onError ->{/*To do nothing yet*/}
                );

        return view;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isPresenterInited){
            presenter.onDestroy();
        }
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
        if (isPresenterInited){
            presenter.onResume();
        }
        //tryUpdateHistory();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void showChooseCharacterDialog(){
        RadicalSearchDialog bottomSheetDialog = new RadicalSearchDialog();
        bottomSheetDialog.setCallback(new RadicalSearchDialog.RadicalSearchCallback() {
            @Override
            public void onCharacterSelected(@NotNull String character) {
                searchPanel.getText().append(character);
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetDialog.show(getChildFragmentManager(),"testtestestestes");

    }

    private void tryUpdateHistory(){
        if(dictionaryRecycle.getAdapter() instanceof UnFilledAdapter){
            UnFilledAdapter adapter = (UnFilledAdapter) dictionaryRecycle.getAdapter();

            if (searchPanel.getText().length() ==0){
                adapter.setMessageView(R.layout.dictionary_start_msg);
            }
            adapter.setHistory(history);
            adapter.notifyDataSetChanged();
        }
    }

    private void onChnCharClicked(com.yingenus.pocketchinese.domain.dto.DictionaryItem dictionaryItem){
        presenter.chinCharClicked(dictionaryItem);
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
        String text;
        try {
            text = getText(R.string.dictionary_history_header).toString();
        }catch (IllegalStateException exception){
            text = null;
        }

        if (text != null && headerText != null){
            headerText.setText(text);
        }

    }
    private void showDefaultHeader(){
        String text;
        try {
            text = getText(R.string.dictionary_header).toString();
        }catch (IllegalStateException exception){
            text = null;
        }

        if (text != null && headerText != null){
            headerText.setText(text);
        }
    }

    @Override
    public void setHistory(@NotNull List<DictionaryItem> history) {
        this.history = history;
        if(dictionaryRecycle.getAdapter() instanceof UnFilledAdapter){
            UnFilledAdapter adapter = (UnFilledAdapter) dictionaryRecycle.getAdapter();

            if (searchPanel.getText().length() ==0){
                adapter.setMessageView(R.layout.dictionary_start_msg);
            }
            adapter.setHistory(history);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void setSearchResult(@NotNull Pair<Integer, DictionaryItem> item) {
        Log.d("dictionary fragment", "try add word: "+item.toString());
        if (dictionaryRecycle == null) return;

        RecyclerView.Adapter adapter = dictionaryRecycle.getAdapter();


        DictionaryItemAdapter diAdapter;
        if (!(adapter instanceof DictionaryItemAdapter)) {
            diAdapter = new DictionaryItemAdapter();
            diAdapter.setChinCharListener(this::onChnCharClicked);
            dictionaryRecycle.setAdapter(diAdapter);
        }else {
            diAdapter = (DictionaryItemAdapter) adapter;
        }
        diAdapter.setDictionaryItem(item.getSecond(),item.getFirst());
        diAdapter.notifyDataSetChanged();
        showDefaultHeader();
    }

    @Override
    public void clearSearchResult() {
        if (dictionaryRecycle == null) return;

        RecyclerView.Adapter adapter = dictionaryRecycle.getAdapter();


        DictionaryItemAdapter diAdapter;
        if (!(adapter instanceof DictionaryItemAdapter)) {
            diAdapter = new DictionaryItemAdapter();
            diAdapter.setChinCharListener(this::onChnCharClicked);
            dictionaryRecycle.setAdapter(diAdapter);
        }else {
            diAdapter = (DictionaryItemAdapter) adapter;
        }
        diAdapter.clearDictionaryItems();
        diAdapter.notifyDataSetChanged();
        showDefaultHeader();
    }

    @Override
    public void showSearchError(@NotNull String msg) {
        // Позже
    }

    @Override
    public void showSearchNothing(boolean show) {
        if (dictionaryRecycle == null) return;
        if (isSearchNothingBannerShowing == show) return;


        RecyclerView.Adapter adapter = dictionaryRecycle.getAdapter();

        UnFilledAdapter unAdapter;

        if (!(adapter instanceof UnFilledAdapter)) {
            unAdapter = new UnFilledAdapter();
            unAdapter.setChinCharListener(this::onChnCharClicked);
            dictionaryRecycle.setAdapter(unAdapter);
            unAdapter.setHistory(history);
        } else {
            unAdapter = (UnFilledAdapter) adapter;
        }
        if (show){
            unAdapter.setMessageView(R.layout.holder_empty_dictionary_item);
        }
        else {
            unAdapter.setMessageView(R.layout.dictionary_start_msg);
        }
        unAdapter.notifyDataSetChanged();

        isSearchNothingBannerShowing = show;
    }

    @Override
    public void showSearchEmptyQuery(boolean show) {
        Log.d("Dictionary fragment", "showSearchEmptyQuery :"+show);

        if (dictionaryRecycle == null) return;
        if (isSearchEmptyBannerShowing == show) return;
        if (show){
            RecyclerView.Adapter adapter = dictionaryRecycle.getAdapter();


            UnFilledAdapter unAdapter;

            if (!(adapter instanceof UnFilledAdapter)){
                unAdapter = new UnFilledAdapter();
                unAdapter.setChinCharListener(this::onChnCharClicked);
                dictionaryRecycle.setAdapter(unAdapter);
                unAdapter.setHistory(history);
            }else {
                unAdapter = (UnFilledAdapter) adapter;
            }

            unAdapter.setMessageView(R.layout.dictionary_start_msg);

            unAdapter.notifyDataSetChanged();
        }
        isSearchEmptyBannerShowing = show;
    }

    @Override
    public void showSearchingAnimation(boolean show) {

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
    public void showChinChar(@NotNull DictionaryItem dictionaryItem) {
        //onChnCharClicked(dictionaryItem);
        ((CharacterFragmentFactory)getChildFragmentManager().getFragmentFactory()).setShowedChinChar(dictionaryItem);
        BottomSheetDialogFragment bottomSheetDialog=(BottomSheetDialogFragment)
                getChildFragmentManager().getFragmentFactory().instantiate(getContext()
                        .getClassLoader(),CharacterSheetDialog.class.getName());

        bottomSheetDialog.show(getChildFragmentManager(),"testtestestestes");
        //addToSetting(dictionaryItem);
        //tryUpdateHistory();
    }

    private static class DictionaryItemHolder extends RecyclerView.ViewHolder {
        private com.yingenus.pocketchinese.domain.dto.DictionaryItem dictionaryItem;

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

        public void bind(com.yingenus.pocketchinese.domain.dto.DictionaryItem dictionaryItem){
            this.dictionaryItem = dictionaryItem;
            mChnText.setText(dictionaryItem.getChinese());
            mPinyinText.setText(dictionaryItem.getPinyin());
            mTrnText.setText(compose(dictionaryItem.getTranslation()));

        }

        private String compose(String[] trns){
            String result = "";

            int counter = 1;
            for (String trn : trns) {

                if (trn.contains("link") /*|| !UtilsKt.isRussian(trn)*/) {
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

        public com.yingenus.pocketchinese.domain.dto.DictionaryItem getDictionaryItem() {
            return dictionaryItem;
        }
    }

    private class DictionaryItemAdapter extends AdapterWithChinCharItems<DictionaryItemHolder>{


        private List<DictionaryItem> mDictionaryItems = new ArrayList<DictionaryItem>();


        DictionaryItemAdapter(){
        }

        public void setDictionaryItems(List<DictionaryItem> dictionaryItems) {
            mDictionaryItems = dictionaryItems;
        }

        public void setDictionaryItem( DictionaryItem item, int position){
            if (mDictionaryItems.size() > position){
                mDictionaryItems.remove(position);
                mDictionaryItems.add(position,item);
            }else {
                mDictionaryItems.add(item);
            }
        }

        public void clearDictionaryItems( ){
            mDictionaryItems = new ArrayList<DictionaryItem>();
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
                    if (position != RecyclerView.NO_POSITION && mDictionaryItems.size() > position){
                        if (mListener != null){
                            mListener.onChinCharClick( mDictionaryItems.get(position));
                        }
                    }
                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull DictionaryItemHolder holder, int position) {
            DictionaryItem dictionaryItem = mDictionaryItems.get(position);
            holder.bind(dictionaryItem);
        }

        @Override
        public int getItemCount() {
            return mDictionaryItems.size();
        }
    }

    private static class UnFilledAdapter extends AdapterWithChinCharItems<RecyclerView.ViewHolder>{
        private static final int MESSAGE = 0;
        private static final int HISTORY_HEADER = 1;
        private static final int HISTORY = 2;

        private List<DictionaryItem> mHistory;
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

        public void setHistory( List<DictionaryItem> history){
            mHistory = history;
        }

        public List<DictionaryItem> getHistory(){
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
        void onChinCharClick(DictionaryItem dictionaryItem);
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
