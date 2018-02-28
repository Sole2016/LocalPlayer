package com.zy.vplayer.tv.struct;

/**
 * @author ZhiTouPC
 */
public abstract class BaseFunction {
    private String mFunctionName;

    public BaseFunction(String functionName) {
        this.mFunctionName = functionName;
    }

    public String getFunctionName() {
        return mFunctionName;
    }

}
