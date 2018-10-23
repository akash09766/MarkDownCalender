package com.developer.skylight.markdowncalender.util;

import android.app.ProgressDialog;
import android.content.Context;

public class Utils {
    public static ProgressDialog pd = null;
    public static Context myContext = null;

    public static void ShowProgressBar(Context context, String msg) {
        pd = ProgressDialog.show(context, msg, "Please wait...", true);
        pd.setCancelable(false);
    }

    public static Boolean ShowProgressBar(Context context) {
        pd = ProgressDialog.show(context, "Loading", "Please wait...", true);
        pd.setCancelable(false);
        return true;
    }
}
