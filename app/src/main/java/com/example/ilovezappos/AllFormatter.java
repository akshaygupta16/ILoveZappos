package com.example.ilovezappos;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.Date;

public class AllFormatter extends ValueFormatter {

    public AllFormatter() {
        super();
    }


    @Override
    public String getAxisLabel(float value, AxisBase axis) {

        Date date = new Date((long) (value*1000L));
        return String.valueOf(date);
    }

    @Override
    public String getPointLabel(Entry entry) {
        return super.getPointLabel(entry);
    }


}
