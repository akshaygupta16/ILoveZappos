package com.example.ilovezappos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    public static LineChart lineChart;
    TextView date;
    TextView price;
    ProgressBar progressBar;
    EditText userPriceET;
    public static SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lineChart = findViewById(R.id.line_chart);
        date = findViewById(R.id.home_date);
        price = findViewById(R.id.home_price);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        userPriceET = findViewById(R.id.price_et);
        sharedPreferences = MainActivity.this.getPreferences(MODE_PRIVATE);
        editor = sharedPreferences.edit();


        new DisplayDetails().execute("https://www.bitstamp.net/api/v2/ticker_hour/btcusd/");
        new GetHistory().execute("https://www.bitstamp.net/api/v2/transactions/btcusd/");

        findViewById(R.id.refreshHome).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DisplayDetails().execute("https://www.bitstamp.net/api/v2/ticker_hour/btcusd/");
                new GetHistory().execute("https://www.bitstamp.net/api/v2/transactions/btcusd/");
            }
        });

        findViewById(R.id.order_book_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, OrderBookActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.submitPrice).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userPriceET.getText().toString().length() > 0 && userPriceET.getText() != null) {
                   try {
                       editor.putFloat("userPrice", Float.valueOf(userPriceET.getText().toString()));
                       editor.commit();
                       Toast.makeText(MainActivity.this, "We will remind you", Toast.LENGTH_SHORT).show();
                       scheduleJob(MainActivity.this);
                   }
                   catch (NumberFormatException e){
                       Toast.makeText(MainActivity.this, "Please enter a number", Toast.LENGTH_SHORT).show();
                   }
                }
                else{
                    Toast.makeText(MainActivity.this, "Please enter a Value", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }

    public static void scheduleJob(Context context) {
        //creating new firebase job dispatcher
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        //creating new job and adding it with dispatcher

        Job thejob = createJob(dispatcher);
        dispatcher.mustSchedule(thejob);
        Log.d("scheduled", "here");
    }

    public static Job createJob(FirebaseJobDispatcher dispatcher){

        Job job = dispatcher.newJobBuilder()

                .setLifetime(Lifetime.FOREVER)
                .setService(MyJob.class)
                .setTag("CheckPrice")
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(0, 10))
                .build();
        return job;
    }

    private class DisplayDetails extends AsyncTask<String, Void, Pair > {
        @Override
        protected Pair doInBackground(String... strings) {
            HttpURLConnection connection = null;
            Pair pair = new Pair();
            try {
                URL url = new URL("https://www.bitstamp.net/api/v2/ticker_hour/btcusd/");
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){

                    String json = IOUtils.toString(connection.getInputStream(), "UTF-8");
                    JSONObject root = new JSONObject(json);
                    pair.setEpochDate(root.getLong("timestamp"));
                    pair.setPrice(Float.valueOf(root.getString("last")));

                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return pair;
        }

        @Override
        protected void onPostExecute(Pair pair) {
            super.onPostExecute(pair);

            progressBar.setVisibility(View.INVISIBLE);
            Date datetime = new Date(pair.getEpochDate()*1000L);
            date.setText(String.valueOf(datetime));
            price.setText("$"+String.valueOf(pair.getPrice()));


        }
    }


}
