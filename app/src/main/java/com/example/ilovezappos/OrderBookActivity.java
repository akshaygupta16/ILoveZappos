package com.example.ilovezappos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class OrderBookActivity extends AppCompatActivity {

    RecyclerView recyclerView_bids;
    RecyclerView recyclerView_asks;
    RecyclerView.LayoutManager layoutManager_bid;
    RecyclerView.LayoutManager layoutManager_ask;
    RecyclerView.Adapter bidsAdapter;
    RecyclerView.Adapter asksAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_book);

        recyclerView_bids = findViewById(R.id.recyclerViewBids);
        recyclerView_asks = findViewById(R.id.recyclerView2Asks);

        new GetOrderHistory().execute("https://www.bitstamp.net/api/v2/order_book/btcusd/");

        findViewById(R.id.refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new GetOrderHistory().execute("https://www.bitstamp.net/api/v2/order_book/btcusd/");
            }
        });
    }

    private class GetOrderHistory extends AsyncTask<String, Void, ArrayList> {
        @Override
        protected ArrayList<ArrayList> doInBackground(String... strings) {


            ArrayList<OrderBookBids> bidsArrayList = new ArrayList<>();
            ArrayList<OrderBookAsks> asksArrayList = new ArrayList<>();
            ArrayList<ArrayList> orderinfo = new ArrayList<>();

            HttpURLConnection httpURLConnection = null;
            try {
                URL url = new URL(strings[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.connect();

                if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK){

                    String json = IOUtils.toString(httpURLConnection.getInputStream(), "UTF-8");
//                    Log.d("demo", json);

                    JSONObject root = new JSONObject(json);
                    JSONArray bids = root.getJSONArray("bids");
                    JSONArray asks = root.getJSONArray("asks");


                    for(int i =0; i<bids.length(); i++){

                        JSONArray bid_json = bids.getJSONArray(i);

                        OrderBookBids order_bid = new OrderBookBids();

                        order_bid.setAmount(bid_json.getDouble(1));
                        order_bid.setBid(bid_json.getDouble(0));
//                        Log.d("demo", "Amount:"+order_bid.getAmount()+"Bid:"+order_bid.getBid()+"");

                        bidsArrayList.add(order_bid);
                    }
                    orderinfo.add(bidsArrayList);

                    for (int i =0; i<asks.length(); i++){

                        JSONArray ask_json = asks.getJSONArray(i);
                        OrderBookAsks order_ask = new OrderBookAsks();
                        order_ask.setAmount(ask_json.getDouble(1));
                        order_ask.setAsk(ask_json.getDouble(0));

                        asksArrayList.add(order_ask);

                    }
                    orderinfo.add(asksArrayList);
                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return orderinfo;
        }

        @Override
        protected void onPostExecute(ArrayList arrayList) {
            super.onPostExecute(arrayList);
            ArrayList<OrderBookBids> bidsArrayList = (ArrayList<OrderBookBids>) arrayList.get(0);
            ArrayList<OrderBookAsks> asksArrayList = (ArrayList<OrderBookAsks>) arrayList.get(1);

            Log.d("demobids", bidsArrayList.size()+"");

            layoutManager_bid = new LinearLayoutManager(OrderBookActivity.this);
            layoutManager_ask = new LinearLayoutManager(OrderBookActivity.this);

            recyclerView_bids.setLayoutManager(layoutManager_bid);
            recyclerView_asks.setLayoutManager(layoutManager_ask);

            bidsAdapter = new BidAdapter(OrderBookActivity.this, bidsArrayList);
            asksAdapter = new AsksAdapter(OrderBookActivity.this, asksArrayList);

            recyclerView_bids.setAdapter(bidsAdapter);
            recyclerView_asks.setAdapter(asksAdapter);

        }
    }
}
