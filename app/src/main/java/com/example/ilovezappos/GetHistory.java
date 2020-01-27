package com.example.ilovezappos;

import android.os.AsyncTask;

import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class GetHistory extends AsyncTask<String, Void, LineData> {
    @Override
    protected LineData doInBackground(String... strings) {

        ArrayList<Entry> entries = new ArrayList<>();

        LineData lineData = null;

        HttpURLConnection connection= null;
        try {
            URL url = new URL(strings[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
//                Log.d("demo", "im in");
                String json = IOUtils.toString(connection.getInputStream(), "UTF-8");
//                Log.d("demo", json);

                JSONArray root = new JSONArray(json);
//                Log.d("demo", root.length()+" ");

                for(int i=root.length()-1; i>=0; i--){
                    JSONObject data = root.getJSONObject(i);
                    Pair pair = new Pair();
                    pair.setEpochDate(data.getLong("date"));
                    pair.setPrice(Float.valueOf(data.getString("price")));
                    pair.setDate(pair.getEpochDate());

                    Entry entry = new Entry(Float.valueOf(pair.getEpochDate()),Float.valueOf(pair.getPrice()));

                    entries.add(entry);
//                    Log.d("demo", pair.getPrice()+String.valueOf(pair.getDate()));
                }

                LineDataSet lineDataSet = new LineDataSet(entries,"Bitcoin prices");
                lineDataSet.setDrawCircles(false);
                lineDataSet.setColor(R.color.colorPrimaryDark);

                lineData = new LineData(lineDataSet);


            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return lineData;
    }

    @Override
    protected void onPostExecute(LineData lineData) {
        super.onPostExecute(lineData);
        MainActivity.lineChart.setData(lineData);
        MainActivity.lineChart.invalidate();

        Description description = MainActivity.lineChart.getDescription();
        description.setEnabled(false);
        MainActivity.lineChart.setData(lineData);
        XAxis xAxis = MainActivity.lineChart.getXAxis();
        YAxis yAxis = MainActivity.lineChart.getAxisLeft();
        YAxis yAxisRight = MainActivity.lineChart.getAxisRight();
        xAxis.setDrawLabels(false);
        yAxis.setDrawLabels(true);
        yAxisRight.setDrawLabels(false);
        xAxis.setDrawGridLines(false);
        yAxis.setDrawGridLines(false);
        yAxisRight.setDrawGridLines(false);
//
//        Log.d("linedata", lineData.toString());



    }
}
