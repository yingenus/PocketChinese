package com.yingenus.pocketchinese.presentation.views.train;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;
import com.yingenus.pocketchinese.R;
import com.yingenus.pocketchinese.Settings;

public abstract class TrainView extends RecyclerView.ViewHolder implements View.OnAttachStateChangeListener {

    protected TextView mainText, secondText;
    protected TextInputLayout pinView;

    public TrainView(LayoutInflater inflater, ViewGroup parent) {
        super(inflater.inflate(R.layout.train_item_view,parent,false));

        mainText =super.itemView.findViewById(R.id.main_text);
        secondText =super.itemView.findViewById(R.id.second_text);

        super.itemView.addOnAttachStateChangeListener(this);

        pinView = super.itemView.findViewById(R.id.edit);
        pinView.setHint(getEditHint(inflater.getContext()));
    }

    public void bind(String chinText, String pinText, String trnText, boolean shouldShowHidden, String hidden){
        bindItem(chinText,pinText,trnText);
        pinView.getEditText().getText().clear();
        showTestText(getHiddenWord());
        if (shouldShowHidden && hidden != null){
            pinView.getEditText().setText(hidden);
        }
    }

    public abstract void bindItem(String chinText,String pinText,String trnText);
    public abstract String getEditHint(Context context);
    public abstract String getHiddenWord();

    public String getAnswer(){
        return pinView.getEditText().getText().toString();
    }

    protected void inputLength(String text){
        pinView.setCounterMaxLength(text.length());
    }

    @Override
    public void onViewAttachedToWindow(View v) {
        super.itemView.requestLayout();
    }

    @Override
    public void onViewDetachedFromWindow(View v) {

    }

    protected void showTestText(String text){
        if (Settings.INSTANCE.getDEBUG()){
            TextView view = ((TextView)super.itemView.findViewById(R.id.visible_testing_text));
            view.setText(text);
            view.setVisibility(View.VISIBLE);
        }
    }


}
