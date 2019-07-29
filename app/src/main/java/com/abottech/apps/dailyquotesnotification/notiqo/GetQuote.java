package com.abottech.apps.dailyquotesnotification.notiqo;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;


public class GetQuote {


    public String quotes[];
    public String categories[];

    public static String singleQuote;
    public static String singleCategory;
    int randomNum;
    Random r = new Random();


    String quotes100data;


    public GetQuote(String quotes){

        quotes100data = quotes;
        randomNum = r.nextInt( 10) + 1;

    }


    public String singleRandomQuote() {

        singleQuote = quotes[randomNum];
        return singleQuote;
    }

    public String singleRandomCategory(){

        singleCategory = categories[randomNum];
        return singleCategory;

    }



    public void getRandomQuote(){
        quotes = new String[10];
        categories = new String[10];


        JSONArray ja_100;
        //String author100;


        try {

            Log.i("quoteData", quotes100data);
            ja_100 = new JSONArray(quotes100data);

            for (int l = 0; l<ja_100.length(); l++){
                JSONObject jsonObject100 = ja_100.getJSONObject(l);

                //author100 = (String) jsonObject100.get("author");
                quotes[l] = "\"" + jsonObject100.get("quote") + "\"";
                categories[l] = (String) jsonObject100.get("author");
                //categories[l] = categories[l].substring(0,1).toUpperCase() + categories[l].substring(1);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


}