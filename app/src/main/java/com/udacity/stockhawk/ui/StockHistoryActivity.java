package com.udacity.stockhawk.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.udacity.stockhawk.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class StockHistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_history);

        Bundle extras = getIntent().getExtras();
        String stockName = extras.getString(getResources().getString(R.string.stock_name_for_intent));
        String stockHistory = extras.getString(getResources().getString(R.string.stock_history_for_intent));

        List<Entry> entries = new ArrayList<>();
        for (String line: stockHistory != null ? stockHistory.split("\n") : new String[0]) {
            String[] lineSplit = line.split(",");
            entries.add(new Entry(Long.parseLong(lineSplit[0]),
                    Float.parseFloat(lineSplit[1])));
        }

        Collections.sort(entries, new Comparator<Entry>() {
            @Override
            public int compare(Entry o1, Entry o2) {
                if (o1.getX() == o2.getY()) return 0;
                return o1.getX() < o2.getX() ? -1: 1;
            }
        });

        TextView stockNameTextView = (TextView) findViewById(R.id.stockNameTextView);
        stockNameTextView.setText(stockName);

        LineChart chart = (LineChart) findViewById(R.id.stockHistoryChart);
        chart.getXAxis().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                Calendar cl = Calendar.getInstance();
                cl.setTimeInMillis((long) value);
                return "" + cl.get(Calendar.DAY_OF_MONTH) + ":" + cl.get(Calendar.MONTH) + ":" + cl.get(Calendar.YEAR);
            }
        });
        chart.getXAxis().setTextColor(getResources().getColor(R.color.colorPrimary));
        chart.getAxisLeft().setTextColor(getResources().getColor(R.color.colorPrimary));
        chart.getAxisRight().setTextColor(getResources().getColor(R.color.colorPrimary));
        chart.getLegend().setTextColor(getResources().getColor(R.color.colorAccent));
        chart.getDescription().setEnabled(false);
        chart.setScaleEnabled(false);

        LineDataSet dataSet = new LineDataSet(entries, getResources().getString(R.string.label_history, stockName));
        dataSet.setColor(getResources().getColor(R.color.colorAccent));
        dataSet.setDrawFilled(true);
        dataSet.setDrawValues(false);
        dataSet.setDrawCircles(false);
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate();
    }
}
