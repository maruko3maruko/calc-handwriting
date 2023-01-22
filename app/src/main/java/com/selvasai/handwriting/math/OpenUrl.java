package com.selvasai.handwriting.math;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class OpenUrl extends AsyncTask<String, Void, String> {

        URL url;
        @Override
        protected String doInBackground(String... x) {
            try {
                // get URL content

                String a="https://www.wolframalpha.com/";
                url = new URL(a);
                URLConnection conn = url.openConnection();

                // open the stream and put it into BufferedReader
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));

                String inputLine;
                while ((inputLine = br.readLine()) != null) {
                    System.out.println(inputLine);
                }
                br.close();

                System.out.println("Done");

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "";



    }

}
