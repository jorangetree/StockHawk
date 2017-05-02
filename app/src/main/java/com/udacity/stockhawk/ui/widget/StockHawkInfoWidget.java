package com.udacity.stockhawk.ui.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.DataRepresentationFormat;
import com.udacity.stockhawk.sync.QuoteIntentService;
import com.udacity.stockhawk.sync.QuoteSyncJob;

import java.text.DecimalFormat;

/**
 * Implementation of App Widget functionality.
 */
public class StockHawkInfoWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        Intent intent = new Intent(context, QuoteIntentService.class);
        context.startService(intent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (QuoteSyncJob.ACTION_DATA_UPDATED.equals(intent.getAction())) {
            updateWidgets(context);
        }
    }

    private void updateWidgets(final Context context) {
        ComponentName thisWidget = new ComponentName(context, StockHawkInfoWidget.class);
        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] widgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

        for (final int widgetId: widgetIds) {
            SharedPreferences prefs =
                    PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
            String symbol = prefs.getString(Integer.toString(widgetId), null);
            Cursor cursor =
                    context.getContentResolver().query(Contract.Quote.makeUriForStock(symbol),
                            null, null, null, null);

            if ((cursor != null) && (cursor.getCount() > 0)) {
                cursor.moveToFirst();
                RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                        R.layout.list_item_quote);
                remoteViews.setTextViewText(R.id.symbol,
                        cursor.getString(Contract.Quote.POSITION_SYMBOL));
                DecimalFormat priceFormat = DataRepresentationFormat.dollarFormat();
                remoteViews.setTextViewText(R.id.price, priceFormat.format(cursor.getFloat(Contract.Quote.POSITION_PRICE)));
                float percentageChange = cursor.getFloat(Contract.Quote.POSITION_PERCENTAGE_CHANGE);
                if (percentageChange > 0) {
                    remoteViews.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_green);
                } else {
                    remoteViews.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_red);
                }
                DecimalFormat percentageFormat = DataRepresentationFormat.percentageFormat();
                remoteViews.setTextViewText(R.id.change, percentageFormat.format(percentageChange / 100.0));
                appWidgetManager.updateAppWidget(widgetId, remoteViews);
                cursor.close();
            }
        }
    }

    @Override
    public void onEnabled(Context context) {
    }

    @Override
    public void onDisabled(Context context) {
    }
}

