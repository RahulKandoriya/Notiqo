package com.abottech.apps.dailyquotesnotification.notiqo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class LibraryActivity extends AppCompatActivity {

    public TextView quoteText;
    public SharedPreferences prefs;

    public Button surpriseButton, updateQuoteButton;
    public ImageView shareQuote;
    public static String data;
    public CardView quoteCard;
    public String singleQuote;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        surpriseButton = findViewById(R.id.newQuote);
        updateQuoteButton = findViewById(R.id.updateQuote);
        quoteText = findViewById(R.id.quoteText);
        quoteCard =findViewById(R.id.quote_card);
        shareQuote = findViewById(R.id.share_quote);

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        ViewCompat.setBackgroundTintList(surpriseButton, ContextCompat.getColorStateList(this, R.color.colorBlue));


        findViewById(R.id.library_layout).setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        quoteText.setText("Get Random Quote from 10 offline quotes or Update the 10 quotes from online Database" );





    }

    public void share_quote(View view){
        Intent shareIntentLibrary = new Intent();
        shareIntentLibrary.setAction(Intent.ACTION_SEND);
        shareIntentLibrary.putExtra(Intent.EXTRA_TEXT, singleQuote + "\nGet Hourly Quotes Notifications\nInstall it Now:- https://goo.gl/L3RQYb");
        shareIntentLibrary.setType("text/plain");
        startActivity(shareIntentLibrary);
    }

    public void updateQuote(View view){

        Boolean isRunning = false;


        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        try {
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            isRunning =  (netInfo != null && netInfo.isConnected());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }



        if (isRunning){


            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            String uri = Uri.parse("https://andruxnet-random-famous-quotes.p.mashape.com/?cat=famous&count=10")
                    .buildUpon()
                    .build().toString();

            StringRequest stringRequest = new StringRequest(
                    Request.Method.GET, uri, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    data = response;
                }

            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VolleyError", error.toString());
                }

            }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String>  params = new HashMap<>();
                    params.put("X-Mashape-Key", "yMJFz000OfmshA8NZLos7gd5T3tfp1rp2XwjsnfDoOkdWUzBfS");
                    params.put("Accept", "text/plain");
                    return params;
                }
            };
            requestQueue.add(stringRequest);


            Snackbar.make(findViewById(R.id.library_layout),"Downloading...",Snackbar.LENGTH_LONG).show();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("quotes", data).apply();




        } else {
            Snackbar.make(findViewById(R.id.library_layout),"Check Internet Connection",Snackbar.LENGTH_SHORT).show();

        }

    }

    public void newQuote(View view){

        String quotes;

        String quotesData = "[{\"quote\":\"I begin by taking. I shall find scholars later to demonstrate my perfect right.\",\"author\":\"Frederick (II) the Great\",\"category\":\"Famous\"},{\"quote\":\"Human history becomes more and more a race between education and catastrophe.\",\"author\":\"H. G. Wells\",\"category\":\"Famous\"},{\"quote\":\"When you do the common things in life in an uncommon way, you will command the attention of the world.\",\"author\":\"George Washington Carver\",\"category\":\"Famous\"},{\"quote\":\"Some cause happiness wherever they go; others, whenever they go.\",\"author\":\"Oscar Wilde\",\"category\":\"Famous\"},{\"quote\":\"The only way to get rid of a temptation is to yield to it.\",\"author\":\"Oscar Wilde\",\"category\":\"Famous\"},{\"quote\":\"We have art to save ourselves from the truth.\",\"author\":\"Friedrich Nietzsche\",\"category\":\"Famous\"},{\"quote\":\"C makes it easy to shoot yourself in the foot; C++ makes it harder, but when you do, it blows away your whole leg.\",\"author\":\"Bjarne Stroustrup\",\"category\":\"Famous\"},{\"quote\":\"Life isn't about waiting for the storm to pass; it's about learning to dance in the rain.\",\"author\":\"Vivian Greene\",\"category\":\"Famous\"},{\"quote\":\"Dancing is silent poetry.\",\"author\":\"Simonides\",\"category\":\"Famous\"},{\"quote\":\"If a man does his best, what else is there?\",\"author\":\"General George S. Patton\",\"category\":\"Famous\"}]";


        try {
            quotes = prefs.getString("quotes",quotesData);


            GetQuote getQuote = new GetQuote(quotes);
            getQuote.getRandomQuote();

            singleQuote = "\n\n" + getQuote.singleRandomQuote() + "\n\n~" + getQuote.singleRandomCategory() ;

            quoteText.setText(String.valueOf(getQuote.randomNum) + ". " + singleQuote);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}