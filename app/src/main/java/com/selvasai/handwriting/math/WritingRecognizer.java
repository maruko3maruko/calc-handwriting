/*!
 *  @date 2020/01/17
 *  @file WritingRecognizer.java
 *  @author SELVAS AI
 *
 *  Copyright 2020. SELVAS AI Inc. All Rights Reserved.
 */

package com.selvasai.handwriting.math;

import android.content.Context;
import android.os.AsyncTask;


import com.selvy.spmath.DHWR;
import com.selvasai.handwriting.math .Calculation;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


// import java.util.Locale;

public class WritingRecognizer {

    private final static int MAX_CANDIDATES = 5;

    private Context mContext;
    private DHWR.Ink mInk;
    private DHWR.Setting mSetting;
    private DHWR.Result mResult;

    public WritingRecognizer(Context context) {
        mContext = context;
        initialize();
    }

    private int initialize() {
        final String filesPath = mContext.getFilesDir().getAbsolutePath();
        int status = DHWR.Create(filesPath + "/" + "license.key");
        DHWR.SetExternalResourcePath(filesPath.toCharArray());

        mInk = new DHWR.Ink();
        mSetting = new DHWR.Setting();
        mResult = new DHWR.Result();

        DHWR.SetRecognitionMode(mSetting.GetHandle(), DHWR.MULTICHAR);
        DHWR.SetCandidateSize(mSetting.GetHandle(), MAX_CANDIDATES);
        DHWR.ClearLanguage(mSetting.GetHandle());
        DHWR.AddLanguage(mSetting.GetHandle(), DHWR.DLANG_MATH_MIDDLE_EXPANSION, DHWR.DTYPE_MATH_EX);
        DHWR.SetAttribute(mSetting.GetHandle());

        return status;
    }

    public int destroy() {
        return DHWR.Close();
    }

    public void clearInk() {
        mInk.Clear();
    }

    public void addPoint(int x, int y) {
        mInk.AddPoint(x, y);
    }

    public void endStroke() {
        mInk.EndStroke();
    }


    //ここが表示
    public  ArrayList<String> recognize() throws IOException, ExecutionException, InterruptedException {
        int status = DHWR.Recognize(mInk, mResult);
        if (status == DHWR.ERR_SUCCESS) {
            String candidates = "";
            candidates += getCandidates(mResult);
            ArrayList<String> candidatesResult = new ArrayList<String>();
            candidatesResult.add(candidates);
//            AsyncTask<String, Void, String> aaa=new OpenUrl().execute("3");
//            AsyncTask<String, Void, String>  news=new WolframCloudCall().execute(candidates);
//            String result=news.get();
//            candidatesResult += result;
//            return candidatesResult;

//            if(candidates.contains("x")){
            candidatesResult.add(makeUrl(candidates));

                return candidatesResult;
//
//            }
//            else {
//                String candidatesResult = candidates+"=";
//                candidatesResult += calcX(candidates);
//                return candidatesResult;
//
//            }


        }
        return new ArrayList<String>();
    }

    public void setLanguage(int language, int option) {
        DHWR.ClearLanguage(mSetting.GetHandle());
        DHWR.AddLanguage(mSetting.GetHandle(), language, option);
        DHWR.SetAttribute(mSetting.GetHandle());
    }

    //ここ編集
    private String getCandidates(DHWR.Result result) {
        StringBuilder candidates = new StringBuilder();
        boolean exit = false;
        int resultSize = result.size();
        if (resultSize < 1) {
            return "";
        }

            for (int j = 0; j < resultSize; j++) {
                DHWR.Line line = result.get(j);
                for (int k = 0; k < line.size(); k++) {
                    DHWR.Block block = line.get(k);
                    if (block.candidates.size() <= 0) {
                        exit = true;
                        break;
                    }
                    if (k + 1 < line.size()) {
                        candidates.append(" ");

                    }
                    String se=block.candidates.get(0);

//                    String se2=block.candidates.get(1);

//                    int num = Integer.parseInt(se);

                    candidates.append(se);
//                    candidates.append("\n");
//                    candidates.append(se2);
                }
                if (exit) {
                    break;
                }
                if (j + 1 < result.size()) {
                    candidates.append("\n");
                }
            }

        return candidates.toString();
    }

    public String calcX(String candidates) throws IOException, ExecutionException, InterruptedException {
        String results = candidates.replace("\\times", "*").replace("\\div ", "/").replace("x", "*x");
        Expression expression = new ExpressionBuilder(results).build();
        double result = expression.evaluate();
         return String.valueOf(result);
    }

    public String makeUrl(String candidates) throws IOException, ExecutionException, InterruptedException {

        String ok[]=candidates.split("");
        String newCandidate="";
        for(int i=0;i<ok.length;i++){
            if(ok[i].equals("^")){
                int xIndex = i-1;
                int powLastIndex = candidates.indexOf("}", xIndex);
                newCandidate=newCandidate.substring(0, newCandidate.length() - 1);

                String x=candidates.substring(xIndex,xIndex+1);
                String after=candidates.substring(xIndex+2,powLastIndex+1);
                newCandidate+= "Power%5B"+x+"%2C"+after+"%5D";
                if(powLastIndex+1>=ok.length){
                    break;
                }
                    i=powLastIndex+1;
            }
            newCandidate+=ok[i];

        }
        if(newCandidate==""){
            newCandidate=candidates;
        }

        String results = newCandidate.replace("+", "%2B").replace("\\", "%5C").replace("=", "%3D").replace(" ", "+").replace("{", "%5C%28123%29").replace("}", "%5C%28125%29");


        return "https://www.wolframalpha.com/input?i2d=true&i="+results+"&lang=ja";
    }


    public String getVersion() {
        final int MAX_VERSION_LENGTH = 64;
        char[] version = new char[MAX_VERSION_LENGTH];
        DHWR.GetRevision(version);
        return String.valueOf(version).trim();
    }

}
