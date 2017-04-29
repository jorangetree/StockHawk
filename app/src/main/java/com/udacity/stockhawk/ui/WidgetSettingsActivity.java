package com.udacity.stockhawk.ui;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.PrefUtils;
import com.udacity.stockhawk.sync.QuoteSyncJob;
import com.udacity.stockhawk.task.StockSymbolCheckTask;

import butterknife.BindView;
import butterknife.ButterKnife;


public class WidgetSettingsActivity extends Activity {

    @BindView(R.id.symbolEditText)
    EditText symbolEditText;

    @BindView(R.id.widgetSearchButton)
    Button widgetSearchButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.widget_settings);
        ButterKnife.bind(this);

        Bundle extras = getIntent().getExtras();
        final int widgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

        widgetSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String symbol = symbolEditText.getText().toString();
                StockSymbolCheckTask task = new StockSymbolCheckTask(getApplicationContext()) {
                    @Override
                    protected void onPostExecute(StockStatus stockStatus) {
                        super.onPostExecute(stockStatus);
                        String message;
                        Intent resultValue = new Intent();
                        switch (stockStatus) {
                            case SYMBOL_FOUND:
                                PrefUtils.addStock(getApplicationContext(), symbol);
                                QuoteSyncJob.syncImmediately(getApplicationContext());

                                SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                                prefs.putString(Integer.toString(widgetId), symbol);
                                prefs.apply();

                                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());

                                RemoteViews views = new RemoteViews(getApplicationContext().getPackageName(),
                                        R.layout.list_item_quote);
                                appWidgetManager.updateAppWidget(widgetId, views);

                                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
                                setResult(RESULT_OK, resultValue);
                                finish();
                                break;
                            case SYMBOL_NOT_FOUND:
                                message = getString(R.string.toast_stock_not_found, symbol);
                                Toast.makeText(WidgetSettingsActivity.this, message, Toast.LENGTH_LONG).show();
                                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
                                setResult(RESULT_CANCELED, resultValue);
                                break;
                            case NETWORK_OR_SERVICE_PROBLEM:
                                message = getString(R.string.error_no_network);
                                Toast.makeText(WidgetSettingsActivity.this, message, Toast.LENGTH_LONG).show();
                                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
                                setResult(RESULT_CANCELED, resultValue);
                                break;
                        }
                    }
                };
                task.execute(symbol);
            }
        });
    }
}
