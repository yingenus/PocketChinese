package com.yingenus.pocketchinese.functions.search;

public class PrefixSearcher {

    public static final int RUSSIAN = 0;
    public static final int PINYIN = 1;

    private long nativePointer = 0;

    static {
        System.loadLibrary("native_search");
    }

    public PrefixSearcher(){
        nativePointer = newNative();
    }

    public void init(String fileName){
        init(fileName,nativePointer);
    }

    public void setLanguage(int language){
        setLanguage(language,nativePointer);
    }

    public int[] find(byte[] query){
        return find(query,nativePointer);
    }

    public void dispose(){
        deleteNative(nativePointer);
    }
    
    private native long newNative();

    private native void init(String fileName,long pointer);

    private native void setLanguage(int language,long pointer);

    private native int[] find(byte[] query,long pointer);

    private native void deleteNative(long pointer);

}
