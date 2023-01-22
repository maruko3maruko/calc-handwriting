package com.selvasai.handwriting.math;

import android.os.AsyncTask;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class Scraping extends AsyncTask<String, Void, String> {
    private Exception exception;
    @Override
    protected String doInBackground(String... x) {
        try {
            Document doc = Jsoup.connect("https://www.wolframalpha.com/input?i2d=true&i=3%2B1&lang=ja").get();
//            String outerHtml=doc.outerHtml();
//            Log.d("outerHtml",doc.outerHtml());

            return doc.title();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }



    }

    protected void onPostExecute(String result) {

    }




}
