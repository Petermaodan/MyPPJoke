package com.mooc.myppjoke.utils;

public class StringConvert {

    public static String convertFeedUgc(int count){
        if (count<10000){
            return String.valueOf(count);
        }
        return count/10000+"万";
    }

    public static String convertTagFeedList(int num){
        if (num<10000){
            return num+"人观看";
        }else {
            return num/10000+"万人看";
        }
    }
}
