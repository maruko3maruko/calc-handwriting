package com.selvasai.handwriting.math;


import java.util.StringTokenizer;

public class Calculation {


public static  String calcLinearEquation(String results){

    StringTokenizer st=new StringTokenizer(results,"+=");
    String aa=st.nextToken();
    int x= Character.getNumericValue(aa.charAt(0)); //char to int conversion.
    int y=Integer.parseInt(st.nextToken());
    int z=Integer.parseInt(st.nextToken());
    int res=(z-y)/x;
    return String.valueOf(res);
}

}
