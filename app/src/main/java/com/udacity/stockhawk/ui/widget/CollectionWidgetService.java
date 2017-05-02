package com.udacity.stockhawk.ui.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.DataRepresentationFormat;
import com.udacity.stockhawk.ui.MainActivity;

import java.text.DecimalFormat;

public class CollectionWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new CollectionWidgetFactory(getApplicationContext());
    }
}

class CollectionWidgetFactory implements RemoteViewsService.RemoteViewsFactory {

    private final Context mContext;
    private Cursor mData = null;

    public CollectionWidgetFactory(Context applicationContext) {
        mContext = applicationContext;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        if (mData != null) {
            mData.close();
        }

        final long identityToken = Binder.clearCallingIdentity();
        Uri uri = Contract.Quote.URI;
        mData = mContext.getContentResolver().query(uri,
                null,
                null,
                null,
                null);
        Binder.restoreCallingIdentity(identityToken);
    }

    @Override
    public void onDestroy() {
        if (mData != null) {
            mData.close();
            mData = null;
        }
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (position == AdapterView.INVALID_POSITION ||
                mData == null || !mData.moveToPosition(position)) {
            return null;
        }

        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(),
                R.layout.list_item_quote);
        remoteViews.setTextViewText(R.id.symbol,
                mData.getString(Contract.Quote.POSITION_SYMBOL));
        DecimalFormat priceFormat = DataRepresentationFormat.dollarFormat();
        remoteViews.setTextViewText(R.id.price, priceFormat.format(mData.getFloat(Contract.Quote.POSITION_PRICE)));
        float percentageChange = mData.getFloat(Contract.Quote.POSITION_PERCENTAGE_CHANGE);
        if (percentageChange > 0) {
            remoteViews.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_green);
        } else {
            remoteViews.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_red);
        }
        DecimalFormat percentageFormat = DataRepresentationFormat.percentageFormat();
        remoteViews.setTextViewText(R.id.change, percentageFormat.format(percentageChange / 100.0));

        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return new RemoteViews(mContext.getPackageName(), R.layout.list_item_quote);
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        if (mData.moveToPosition(position))
            return mData.getLong(Contract.Quote.POSITION_ID);
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}