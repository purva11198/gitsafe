package com.smartcitypune.smartpune;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Kapil on 12-09-2018.
 */
public class ViewViralMessagesAdapter extends RecyclerView.Adapter<ViewViralMessagesAdapter.MyViewHolder> {
    private ArrayList<ViralMessage> mDataset;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView dateTextView;
        public TextView statusTextView;
        public TextView categoryTextView;
        public TextView descriptionTextView;
        public LinearLayout statusLinearLayout;
        public CardView viralMessageCardView;

        public MyViewHolder(CardView cardView) {
            super(cardView);
            dateTextView = cardView.findViewById(R.id.viral_message_date);
            statusTextView = cardView.findViewById(R.id.viral_message_status_textView);
            categoryTextView = cardView.findViewById(R.id.viral_message_category);
            descriptionTextView = cardView.findViewById(R.id.viral_message_description);
            statusLinearLayout = cardView.findViewById(R.id.viral_message_status);
            viralMessageCardView = cardView.findViewById(R.id.viral_message_cardView);
        }
    }

    public ViewViralMessagesAdapter(ArrayList<ViralMessage> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public ViewViralMessagesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                    int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viral_message_card_view, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(cardView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ViralMessage viralMessage = mDataset.get(position);

        holder.dateTextView.setText(Utilities.getDate(viralMessage.getDate()));
        holder.categoryTextView.setText(viralMessage.getCategory());
        holder.descriptionTextView.setText(viralMessage.getMessage_text());
        holder.viralMessageCardView.setTag(viralMessage);

        if (viralMessage.getStatus().toString().equals("fake")) {
            holder.statusLinearLayout.setBackgroundResource(android.R.color.holo_red_dark);
            holder.statusTextView.setText("FAKE");
        } else if (viralMessage.getStatus().toString().equals("real")) {
            holder.statusLinearLayout.setBackgroundResource(android.R.color.holo_green_dark);
            holder.statusTextView.setText("REAL");
        } else {
            holder.statusLinearLayout.setBackgroundResource(android.R.color.holo_blue_dark);
            holder.statusTextView.setText("PENDING");
            holder.statusTextView.setTextSize(15);
        }

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}