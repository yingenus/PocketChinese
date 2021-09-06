package com.yingenus.pocketchinese.controller.fragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yingenus.pocketchinese.R;
import com.yingenus.pocketchinese.controller.Settings;
import com.yingenus.pocketchinese.controller.dialog.CharacterSheetDialog;
import com.yingenus.pocketchinese.controller.holders.ViewViewHolder;
import com.yingenus.pocketchinese.model.database.dictionaryDB.ChinChar;
import com.yingenus.pocketchinese.presenters.DictionaryPresenter;
import com.yingenus.pocketchinese.presenters.UtilsKt;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButtonToggleGroup;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;


public class DictionaryFragment extends Fragment implements DictionaryInterface{
    private static final String CHIN_CHAR ="com.example.pocketchinese.dictionaryfragment.showedchin";


    private final DictionaryPresenter presenter = new DictionaryPresenter( this);
    private EditText searchPanel;
    private RecyclerView dictionaryRecycle;
    private TextView headerText;
    private MaterialButtonToggleGroup toggleGroup;

    private Observable<String> textInputObserver;

    private List<ChinChar> history;

    public DictionaryFragment(){
        super(R.layout.dictionary_fragment);
    }

    static class CharacterFragmentFactory extends FragmentFactory{
        private ChinChar showedChinChar;

        public void setShowedChinChar(ChinChar showedChinChar) {
            this.showedChinChar = showedChinChar;
        }

        public ChinChar getShowedChinChar() {
            return showedChinChar;
        }

        @NonNull
        @Override
        public Fragment instantiate(@NonNull ClassLoader classLoader, @NonNull String className) {
            if(className.equals(CharacterSheetDialog.class.getName()))
                return new CharacterSheetDialog(showedChinChar);
            return super.instantiate(classLoader, className);
        }
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        CharacterFragmentFactory factory = new CharacterFragmentFactory();
        if (savedInstanceState != null){
            factory.setShowedChinChar((ChinChar) savedInstanceState.getSerializable(CHIN_CHAR));
        }
        getChildFragmentManager().setFragmentFactory(factory);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        FragmentFactory ff = getChildFragmentManager().getFragmentFactory();
        if (ff instanceof CharacterFragmentFactory){
            ChinChar showed = ((CharacterFragmentFactory)ff).getShowedChinChar();
            if (showed != null){
                outState.putSerializable(CHIN_CHAR,showed);
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
        updateRecyclerView(null);

        setSearchTypeChangedListener();

        UnFilledAdapter unFilledAdapter = new UnFilledAdapter();
        unFilledAdapter.setMessageView(R.layout.dictionary_start_msg);
        unFilledAdapter.setHistory(presenter.getHistory(getContext()));

        presenter.onCreate(getContext());

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

    private void updateRecyclerView(List<ChinChar> chinChars){
        RecyclerView.Adapter adapter = dictionaryRecycle.getAdapter();

        if(chinChars==null || chinChars.isEmpty()){
            UnFilledAdapter unAdapter;

            if (!(adapter instanceof UnFilledAdapter)){
                unAdapter = new UnFilledAdapter();
                unAdapter.setChinCharListener(this::onChnCharClicked);
                dictionaryRecycle.setAdapter(unAdapter);
            }else {
                unAdapter = (UnFilledAdapter) adapter;
            }
            unAdapter.setHistory(history);
            if (searchPanel.getText().length() == 0){
                unAdapter.setMessageView(R.layout.dictionary_start_msg);
                unAdapter.setHistory(presenter.getHistory(getContext()));
            }else {
                unAdapter.setMessageView(R.layout.holder_empty_dictionary_item);
            }
            unAdapter.notifyDataSetChanged();
        }else {
            DictionaryItemAdapter diAdapter;
            if (!(adapter instanceof DictionaryItemAdapter)) {
                diAdapter = new DictionaryItemAdapter();
                diAdapter.setChinCharListener(this::onChnCharClicked);
                dictionaryRecycle.setAdapter(diAdapter);
            }else {
                diAdapter = (DictionaryItemAdapter) adapter;
            }
            diAdapter.setChinCharacters(chinChars);
            diAdapter.notifyDataSetChanged();
            showDefaultHeader();
        }
    }

    private void onChnCharClicked(ChinChar chinChar){
        ((CharacterFragmentFactory)getChildFragmentManager().getFragmentFactory()).setShowedChinChar(chinChar);
        BottomSheetDialogFragment bottomSheetDialog=(BottomSheetDialogFragment)
                getChildFragmentManager().getFragmentFactory().instantiate(getContext()
                                .getClassLoader(),CharacterSheetDialog.class.getName());

        bottomSheetDialog.show(getChildFragmentManager(),"testtestestestes");
        addToSetting(chinChar);
        tryUpdateHistory();
    }

    private void addToSetting( ChinChar chinChar){
        Settings.INSTANCE.addSearchItem(getContext(),chinChar.getId());
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
    public void showEmpty() {
        updateRecyclerView(null);
    }

    @Override
    public void setHistory(@NotNull List<? extends ChinChar> history) {

    }

    @Override
    public void showItems(@NotNull List<? extends ChinChar> results) {
        updateRecyclerView((List<ChinChar>)results);
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
    public void showChinChar(@NotNull ChinChar chinChar) {
        onChnCharClicked(chinChar);
    }

    private static class DictionaryItemHolder extends RecyclerView.ViewHolder {
        private ChinChar chinChar;

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

        public void bind(ChinChar chinChar){
            this.chinChar=chinChar;
            mChnText.setText(chinChar.getChinese());
            mPinyinText.setText(chinChar.getPinyin());
            mTrnText.setText(compose(chinChar.getTranslations()));

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

        public ChinChar getChinChar() {
            return chinChar;
        }
    }

    private class DictionaryItemAdapter extends AdapterWithChinCharItems<DictionaryItemHolder>{


        private List<ChinChar> mChinChars = Collections.EMPTY_LIST;


        DictionaryItemAdapter(){
        }

        public void setChinCharacters(List<ChinChar> chinChars) {
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
            ChinChar chinChar=mChinChars.get(position);
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

        private List<ChinChar> mHistory;
        private View mMassage;
        private int mViewRes = -1;

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (viewType == MESSAGE){
                return getMesView(inflater);
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
            if (holder.getClass() == DictionaryItemHolder.class){
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
            if (position == 1){
                return HISTORY_HEADER;
            }
            if (position > 1){
                return HISTORY;
            }
            return MESSAGE;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        private RecyclerView.ViewHolder getMesView(LayoutInflater inflater){
            if (mMassage != null){
                return new ViewViewHolder(mMassage);
            }
            else if (mViewRes != -1){
                return new ViewViewHolder(mViewRes, inflater);
            }
            else return new ViewViewHolder(new View(inflater.getContext()));
        }

        public void setMessageView(View view){
            mMassage = view;
            notifyItemChanged(0);
        }

        public void setMessageView(@LayoutRes int res){
            mViewRes= res;
            notifyItemChanged(0);
        }

        public void setHistory( List<ChinChar> history){
            mHistory = history;
            notifyItemRangeChanged(2, getItemCount() - 2);
        }

        public List<ChinChar> getHistory(){
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
        void onChinCharClick(ChinChar chinChar);
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
