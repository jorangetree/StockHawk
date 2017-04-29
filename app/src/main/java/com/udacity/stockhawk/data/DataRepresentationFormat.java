package com.udacity.stockhawk.data;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class DataRepresentationFormat {

    public static DecimalFormat dollarFormatWithPlus() {
        DecimalFormat format = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        format.setPositivePrefix("+$");
        return format;
    }

    public static DecimalFormat dollarFormat() {
        return (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
    }

    public static DecimalFormat percentageFormat() {
        DecimalFormat format = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());
        format.setMaximumFractionDigits(2);
        format.setMinimumFractionDigits(2);
        format.setPositivePrefix("+");
        return format;
    }

}
