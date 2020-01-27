package com.example.ilovezappos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AsksAdapter extends RecyclerView.Adapter<AsksAdapter.AskHolder>{

    Context context;
    ArrayList<OrderBookAsks> asks;

    public AsksAdapter(Context context, ArrayList<OrderBookAsks> asks) {
        this.context = context;
        this.asks = asks;
    }

    @NonNull
    @Override
    public AskHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.orderbookasks,parent,false);
        AskHolder askHolder = new AskHolder(view);
        return askHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AskHolder holder, int position) {
        OrderBookAsks ask = asks.get(position);
        holder.ask.setText(String.valueOf(ask.ask));
        holder.amount.setText(String.valueOf(ask.amount));

    }

    @Override
    public int getItemCount() {
        return asks.size();
    }

    public class AskHolder extends RecyclerView.ViewHolder{
        TextView ask;
        TextView amount;

        public AskHolder(@NonNull View itemView) {

            super(itemView);
            this.ask =itemView.findViewById(R.id.tv_ask_left);
            this.amount=itemView.findViewById(R.id.tv_right_ask);
        }
    }
}
