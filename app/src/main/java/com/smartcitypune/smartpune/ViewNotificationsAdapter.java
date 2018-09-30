package com.smartcitypune.smartpune;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Kapil on 12-09-2018.
 */
public class ViewNotificationsAdapter extends RecyclerView.Adapter<ViewNotificationsAdapter.MyViewHolder> {
    private ArrayList<NotificationCase> mDataset;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView dateTextView;
        public TextView categoryTextView;
        public TextView descriptionTextView;
        public CardView viralMessageCardView;

        public MyViewHolder(CardView cardView) {
            super(cardView);
            dateTextView = cardView.findViewById(R.id.viral_message_date);
            categoryTextView = cardView.findViewById(R.id.viral_message_category);
            descriptionTextView = cardView.findViewById(R.id.viral_message_description);
            viralMessageCardView = cardView.findViewById(R.id.viral_message_cardView);
        }
    }

    public ViewNotificationsAdapter(ArrayList<NotificationCase> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public ViewNotificationsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                    int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_notification_card_view, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(cardView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        NotificationCase notificationCase = mDataset.get(position);

        Date date = new Date(notificationCase.getTimestamp());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

        holder.dateTextView.setText(simpleDateFormat.format(date).toString());
        holder.categoryTextView.setText(notificationCase.getTitle());
        holder.descriptionTextView.setText(notificationCase.getBody());


    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}