package com.yingenus.pocketchinese.presentation.dialogs;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.yingenus.pocketchinese.R;
import com.yingenus.pocketchinese.view.MultiColorProgressBar;

public class TrainPartsHolder extends RecyclerView.ViewHolder {
    protected CardView innerCardView;
    protected MultiColorProgressBar progressBar;
    protected MultiColorProgressBar.ProgressElement green,red;
    protected View foggingView;


    public TrainPartsHolder(LayoutInflater inflater, ViewGroup parent) {
        super(inflater.inflate(R.layout.holder_train_parts,parent,false));

        innerCardView = super.itemView.findViewById(R.id.train_parts_inner_card_view);
        progressBar =super.itemView.findViewById(R.id.train_parts_progress_bar);
        foggingView =super.itemView.findViewById(R.id.fogging_view);

        green=new MultiColorProgressBar.ProgressElement();
        green.setProgressColor(inflater.getContext().getResources().getColor(R.color.success_color_10));
        progressBar.addProgressElement(green);

        red=new MultiColorProgressBar.ProgressElement();
        red.setProgressColor(inflater.getContext().getResources().getColor(R.color.success_color_2));
        progressBar.addProgressElement(red);

    }

    public void addInnerView(View view){
        ViewGroup.LayoutParams params=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        innerCardView.addView(view,params);
    }

    public void bind(int progressGreen,int progressRed,int maxProgress){
        green.setProgressValue(progressGreen);
        red.setProgressValue(progressRed);
        progressBar.setMaxProgress(maxProgress);
        progressBar.notifiProgressChanged();
    }

}
