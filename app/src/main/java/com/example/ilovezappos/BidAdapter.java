package com.example.ilovezappos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class BidAdapter extends RecyclerView.Adapter<BidAdapter.BidHolder> {

    Context context;
    ArrayList<OrderBookBids> bids;

    public BidAdapter(Context context, ArrayList<OrderBookBids> bids) {
        this.context = context;
        this.bids = bids;
    }

    @NonNull
    @Override
    public BidHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.orderbookentry,parent,false);
        BidHolder bidHolder = new BidHolder(view);

        return bidHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BidHolder holder, int position) {
        OrderBookBids bid = bids.get(position);
        holder.amount.setText(String.valueOf(bid.getAmount()));
        holder.bid.setText(String.valueOf(bid.getBid()));
    }

    @Override
    public int getItemCount() {
        return bids.size();
    }

    public class BidHolder extends RecyclerView.ViewHolder{
        TextView bid;
        TextView amount;

        public BidHolder(@NonNull View itemView) {

            super(itemView);
            this.bid =itemView.findViewById(R.id.orderBook_tv_right);
            this.amount=itemView.findViewById(R.id.orderbook_tv_left);
        }
    }
}
