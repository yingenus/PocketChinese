package com.yingenus.pocketchinese.controller.fragment.train;

import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.yingenus.pocketchinese.R;
import com.yingenus.pocketchinese.controller.Settings;
import com.yingenus.pocketchinese.view.pintext.PinTextView;

import java.util.Arrays;

public abstract class TrainView extends RecyclerView.ViewHolder implements TextWatcher, View.OnAttachStateChangeListener {

    public static final int ANSWER_ID = R.id.user_answer;
    public static final int DISCLOSED_ID = R.id.user_answer;

    protected TextView mainText, secondText;
    protected FrameLayout frameLayout;
    protected Button nextButton, skipButton;
    protected PinTextView pinView;
    protected CheckBox showAnsBox;

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        super.itemView.setTag(ANSWER_ID, s.toString());
    }

    public TrainView(LayoutInflater inflater, ViewGroup parent) {
        super(inflater.inflate(R.layout.train_item_view,parent,false));

        mainText =super.itemView.findViewById(R.id.main_text);
        secondText =super.itemView.findViewById(R.id.second_text);
        frameLayout =super.itemView.findViewById(R.id.pin_text_view);
        nextButton =super.itemView.findViewById(R.id.next_button);
        skipButton =super.itemView.findViewById(R.id.skip_button);
        showAnsBox =super.itemView.findViewById(R.id.visibility_check_box);

        showAnsBox.setOnCheckedChangeListener(this::onCheckedChanged);

        super.itemView.addOnAttachStateChangeListener(this);

        pinView =loadTrain(inflater);

        pinView.addTextChangedListener(this);
        pinView.setFilters(new InputFilter[]{new SpaceFilter()});
        frameLayout.addView(pinView);
    }

    public void bind(String chinText, String pinText, String trnText){
        bindItem(chinText,pinText,trnText);
        pinView.getText().clear();
        showAnsBox.setChecked(false);
        showAnsBox.setClickable(true);
        showTestText(getHiddenWord());

    }

    public abstract void bindItem(String chinText,String pinText,String trnText);
    public abstract PinTextView loadTrain(LayoutInflater inflater);
    public abstract String getHiddenWord();

    public void setListeners(View.OnClickListener onAnswerClicked, View.OnClickListener onSkipClicked, View.OnClickListener onGiveUpClicked){
        nextButton.setOnClickListener(onAnswerClicked);
        showAnsBox.setOnClickListener(onGiveUpClicked);
        skipButton.setOnClickListener(onSkipClicked);
    }

    public String getAnswer(){
        return pinView.getText().toString();
    }

    public boolean isDisclosed(){
        return showAnsBox.isChecked();
    }

    private void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
        if (isChecked){
            pinView.setText(getHiddenWord());
            super.itemView.setTag(DISCLOSED_ID,true);
            showAnsBox.setClickable(false);
        }
    }

    protected void calcColumns(String text,int maxColumns){
        int row= (int) Math.ceil(((double)text.length()) /maxColumns);
        int[] rows=new int[row];
        int[] space=new int[text.length()];
        int textLength=text.length();
        for(int i=0; i<rows.length; i++){

            if (textLength<=maxColumns){
                rows[i]=textLength;
            }else{
                rows[i]=maxColumns;
                textLength-=maxColumns;
            }
        }
        int index=0;
        for (int i=0;i<text.length();i++){
            if (text.charAt(i)==' ') {
                space[index++]=i;
            }
        }
        pinView.setColumnsInRows(rows);
        pinView.setSpaceOnIndexes(Arrays.copyOfRange(space,0,index));
    }

    @Override
    public void onViewAttachedToWindow(View v) {
        super.itemView.requestLayout();
    }

    @Override
    public void onViewDetachedFromWindow(View v) {

    }

    private static class SpaceFilter implements InputFilter{
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            StringBuilder sequence= new StringBuilder();

            for (int i=start;i<end;i++){
                if (Character.isWhitespace(source.charAt(i))) continue;
                sequence.append(source.charAt(i));
            }
            return sequence.toString();
        }
    }

    protected void showTestText(String text){
        if (Settings.INSTANCE.DEBUG){
            TextView view = ((TextView)super.itemView.findViewById(R.id.visible_testing_text));
            view.setText(text);
            view.setVisibility(View.VISIBLE);
        }
    }


}
