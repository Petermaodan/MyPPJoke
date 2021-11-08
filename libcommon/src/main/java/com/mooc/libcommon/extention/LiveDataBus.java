package com.mooc.libcommon.extention;

public class LiveDataBus {

    private static class Lazy{
        static LiveDataBus sLiveDataBus=new LiveDataBus();
    }
    public static LiveDataBus get(){
        return Lazy.sLiveDataBus;
    }
}
