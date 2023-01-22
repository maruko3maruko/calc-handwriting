package com.selvasai.handwriting.math;
import android.os.AsyncTask;

import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.DataOutputStream;
import java.io.IOException;

public class WolframCloudCall  extends AsyncTask<String, Void, String> {
    private Exception exception;
    @Override
    protected String doInBackground(String... x) {
        URL _url = null;
        try {
            _url = new URL("https://www.wolframcloud.com/obj/2f9dced6-0529-4de3-9ddc-0a6d6519464b");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection _conn = null;
        try {
            _conn = (HttpURLConnection) _url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            _conn.setRequestMethod("POST");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        _conn.setDoOutput(true);
        _conn.setDoInput(true);
        _conn.setUseCaches(false);
        _conn.setAllowUserInteraction(false);
        _conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
        _conn.setRequestProperty("User-Agent", "EmbedCode-Java/1.0");
        DataOutputStream _out = null;
        try {
            _out = new DataOutputStream(_conn.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            _out.writeBytes("x");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            _out.writeBytes("=");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            _out.writeBytes(URLEncoder.encode("" + x, "UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            _out.writeBytes("&");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            _out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            if (_conn.getResponseCode() != 200) {
                throw new IOException(_conn.getResponseMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        BufferedReader _rdr = null;
        try {
            _rdr = new BufferedReader(new InputStreamReader(_conn.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        StringBuilder _sb = new StringBuilder();
        String _line = null;
        while (true) {
            try {
                if (!((_line = _rdr.readLine()) != null)) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            _sb.append(_line);
        }
        try {
            _rdr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        _conn.disconnect();
        return _sb.toString();
    }

    protected void onPostExecute(String result) {

    }




}
