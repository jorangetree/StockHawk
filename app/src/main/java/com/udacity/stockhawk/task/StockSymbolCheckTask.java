package com.udacity.stockhawk.task;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;


public class StockSymbolCheckTask extends AsyncTask<String, Integer, StockSymbolCheckTask.StockStatus> {

    public enum StockStatus {SYMBOL_FOUND, SYMBOL_NOT_FOUND, NETWORK_OR_SERVICE_PROBLEM};

    private final Context mContext;

    public StockSymbolCheckTask(Context context) {
        mContext = context;
    }
    @Override
    protected StockStatus doInBackground(String... params) {
        String symbol = params[0];
        try {
            Stock stock = YahooFinance.get(symbol);
            if (stock.getName() != null) {
                return StockStatus.SYMBOL_FOUND;
            }
            else {
                return StockStatus.SYMBOL_NOT_FOUND;
            }
        } catch (IOException e) {
            return StockStatus.NETWORK_OR_SERVICE_PROBLEM;
        } catch (StringIndexOutOfBoundsException e) {
            return StockStatus.SYMBOL_NOT_FOUND;
        }
    }
}
