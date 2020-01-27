package com.example.ilovezappos;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MyJob extends JobService {

    @Override
    public boolean onStartJob(JobParameters job) {

        Log.d("job started","here");
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL("https://www.bitstamp.net/api/v2/ticker_hour/btcusd/");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.connect();

                    if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){

                        String json = IOUtils.toString(connection.getInputStream(), "UTF-8");
                        JSONObject root = new JSONObject(json);
                        Float lastPrice = Float.valueOf(root.getString("last"));


                        Float comparePrice = MainActivity.sharedPreferences.getFloat("userPrice",0);
                        Log.d("demoFromJob", comparePrice+"  "+lastPrice);
                        if (lastPrice < comparePrice){
                            Log.d("condition satisfied", "here");
                            NotificationChannel myChannel = new NotificationChannel("priceUnderChannel","myChannel", NotificationManager.IMPORTANCE_DEFAULT);
                            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                            if (notificationManager != null) {
                                notificationManager.createNotificationChannel(myChannel);
                            }

                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

                            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "priceUnderChannel")
                                    .setSmallIcon(R.drawable.ic_notification_important_24px)
                                    .setContentTitle("IloveZappos")
                                    .setContentText("The bitcoin price just went under "+String.valueOf(comparePrice))
                                    .setContentIntent(pendingIntent)
                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
                            notificationManagerCompat.notify(100, builder.build());
                        }



                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }
}
