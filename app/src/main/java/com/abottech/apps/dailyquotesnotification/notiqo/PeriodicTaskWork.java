package com.abottech.apps.dailyquotesnotification.notiqo;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class PeriodicTaskWork extends Worker {

    private final String CHANNEL_ID = "notiqo_code";
    private  static final int NOTIFICATION_ID = 1;

    public String quotes;
    SharedPreferences prefsTask;

    String quotesData = "[{\"quote\":\"I begin by taking. I shall find scholars later to demonstrate my perfect right.\",\"author\":\"Frederick (II) the Great\",\"category\":\"Famous\"},{\"quote\":\"Human history becomes more and more a race between education and catastrophe.\",\"author\":\"H. G. Wells\",\"category\":\"Famous\"},{\"quote\":\"When you do the common things in life in an uncommon way, you will command the attention of the world.\",\"author\":\"George Washington Carver\",\"category\":\"Famous\"},{\"quote\":\"Some cause happiness wherever they go; others, whenever they go.\",\"author\":\"Oscar Wilde\",\"category\":\"Famous\"},{\"quote\":\"The only way to get rid of a temptation is to yield to it.\",\"author\":\"Oscar Wilde\",\"category\":\"Famous\"},{\"quote\":\"We have art to save ourselves from the truth.\",\"author\":\"Friedrich Nietzsche\",\"category\":\"Famous\"},{\"quote\":\"C makes it easy to shoot yourself in the foot; C++ makes it harder, but when you do, it blows away your whole leg.\",\"author\":\"Bjarne Stroustrup\",\"category\":\"Famous\"},{\"quote\":\"Life isn't about waiting for the storm to pass; it's about learning to dance in the rain.\",\"author\":\"Vivian Greene\",\"category\":\"Famous\"},{\"quote\":\"Dancing is silent poetry.\",\"author\":\"Simonides\",\"category\":\"Famous\"},{\"quote\":\"If a man does his best, what else is there?\",\"author\":\"General George S. Patton\",\"category\":\"Famous\"}]";




    private Context mContext;


    public PeriodicTaskWork(
            @NonNull Context appContext,
            @NonNull WorkerParameters workerParams) {
        super(appContext, workerParams);
    }



    @NonNull
    @Override
    public Result doWork() {

        mContext = getApplicationContext();

        try {


            displayNotification();
            return Result.success();

        } catch (Throwable throwable) {

            return Result.failure();
        }

    }

    private void buildNotificationChannel()
    {
        if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.O)
        {
            CharSequence name="Quotes Notifications";
            String description="Contains all Quotes notification";
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,name, NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription(description);
            NotificationManager notificationManager=(NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);

            try {
                notificationManager.createNotificationChannel(notificationChannel);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

        }
    }

    private void displayNotification() {



        buildNotificationChannel();
        String singleQuote;
        String singleCategory;
        PendingIntent pendingActionIntent, pendingShareIntent, pendingDismissIntent;
        Intent actionIntent, shareIntentPeriodic, dismissIntent;

        prefsTask = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        quotes = prefsTask.getString("quotes",quotesData);

        GetQuote getQuote = new GetQuote(quotes);
        getQuote.getRandomQuote();



        singleQuote = getQuote.singleRandomQuote();
        singleCategory = getQuote.singleRandomCategory();



        actionIntent = new Intent(mContext, MainActivity.class);
        pendingActionIntent = PendingIntent.getActivity(mContext, NOTIFICATION_ID, actionIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        shareIntentPeriodic = new Intent();
        shareIntentPeriodic.setAction(Intent.ACTION_SEND);
        shareIntentPeriodic.putExtra(Intent.EXTRA_TEXT, singleQuote + "\n\n~" + singleCategory + "\nGet Hourly Quotes Notifications\nInstall Now https://goo.gl/L3RQYb");
        shareIntentPeriodic.setType("text/plain");
        pendingShareIntent = PendingIntent.getActivity(mContext, NOTIFICATION_ID, shareIntentPeriodic, PendingIntent.FLAG_UPDATE_CURRENT);

        dismissIntent = new Intent(mContext, DismissReceiver.class);
        pendingDismissIntent = PendingIntent.getBroadcast(mContext, NOTIFICATION_ID, dismissIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(mContext, CHANNEL_ID);
        nBuilder.setSmallIcon(R.drawable.ic_format_quote_black_24dp);
        nBuilder.setSound(Uri.parse("android.resource://"+mContext.getPackageName()+"/"+R.raw.sound));
        nBuilder.setContentTitle(singleCategory);
        nBuilder.setContentText(singleQuote);
        nBuilder.setStyle(new NotificationCompat.BigTextStyle()
                .bigText(singleQuote));
        nBuilder.setContentIntent(pendingActionIntent);
        nBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        nBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        nBuilder.setOngoing(true);
        nBuilder.addAction(R.drawable.ic_cancel_black_24dp, "DISMISS", pendingDismissIntent);
        nBuilder.addAction(R.drawable.ic_share_black_24dp, "SHARE", pendingShareIntent);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(mContext);
        notificationManagerCompat.notify(NOTIFICATION_ID,nBuilder.build());

    }

}