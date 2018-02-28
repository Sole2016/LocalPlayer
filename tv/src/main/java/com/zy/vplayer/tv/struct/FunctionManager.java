package com.zy.vplayer.tv.struct;

import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

/**
 * @author ZhiTouPC
 */
public class FunctionManager {
    private static FunctionManager ourInstance;
    private ArrayMap<String, BaseFunctionNoParamNoResult> mNoParamNoResultMap;
    private ArrayMap<String, BaseFunctionWithParamAndResult> mWithParamAndResultMap;
    private ArrayMap<String, BaseFunctionWithParamOnly> mWithParamOnlyMap;
    private ArrayMap<String, BaseFunctionWithResultOnly> mWithResultOnlyMap;

    public static FunctionManager getInstance() {
        if(ourInstance == null){
            synchronized (FunctionManager.class){
                if(ourInstance == null){
                    ourInstance = new FunctionManager();
                }
            }
        }
        return ourInstance;
    }

    private FunctionManager() {
        mNoParamNoResultMap = new ArrayMap<>();
        mWithParamAndResultMap = new ArrayMap<>();
        mWithParamOnlyMap = new ArrayMap<>();
        mWithResultOnlyMap = new ArrayMap<>();
    }

    public FunctionManager addFunction(BaseFunctionNoParamNoResult function) {
        mNoParamNoResultMap.put(function.getFunctionName(), function);
        return this;
    }

    public FunctionManager addFunction(BaseFunctionWithParamOnly function){
        mWithParamOnlyMap.put(function.getFunctionName(),function);
        return this;
    }

    public FunctionManager addFunction(BaseFunctionWithResultOnly function){
        mWithResultOnlyMap.put(function.getFunctionName(),function);
        return this;
    }

    public FunctionManager addFunction(BaseFunctionWithParamAndResult function){
        mWithParamAndResultMap.put(function.getFunctionName(),function);
        return this;
    }

    public void invokeNoParamNoResultFunc(String functionName) {
        if (TextUtils.isEmpty(functionName)) {
            return;
        }
        if (mNoParamNoResultMap != null) {
            BaseFunctionNoParamNoResult baseFunction = mNoParamNoResultMap.get(functionName);
            if (baseFunction != null) {
                baseFunction.function();
            } else {
                throw new NullPointerException("not found this function ,functionName is " + functionName);
            }
        }
    }

    public <Param> void invokeParamOnly(String functionName,Param c){
        if (TextUtils.isEmpty(functionName)) {
            return;
        }
        if(mWithParamOnlyMap != null){
            BaseFunctionWithParamOnly f = mWithParamOnlyMap.get(functionName);
            if (f != null) {
                f.function(c);
            }else{
                throw new NullPointerException("not found this function ,name is "+functionName);
            }
        }
    }

    public <Result> Result invokeResultOnly(String functionName,Class<Result> c){
        if (TextUtils.isEmpty(functionName)) {
            return null;
        }
        if(mWithResultOnlyMap != null){
            BaseFunctionWithResultOnly f = mWithResultOnlyMap.get(functionName);
            if (f != null) {
                if (c != null) {
                    return c.cast(f.function());
                }else{
                    return (Result)f.function();
                }
            }else{
                throw new NullPointerException("not found this function ,name is "+functionName);
            }
        }
        return null;
    }

    public <Param,Result> Result invokeParamWithResult(String functionName,Param p,Class<Result> r){
        if (TextUtils.isEmpty(functionName)) {
            return null;
        }

        if(mWithParamAndResultMap != null){
            BaseFunctionWithParamAndResult f = mWithParamAndResultMap.get(functionName);
            if (f != null) {
                if (r != null) {
                    return r.cast(f.function(p));
                }else{
                    return (Result)f.function(p);
                }
            }else{
                throw new NullPointerException("not found this function ,name is "+functionName);
            }
        }
        return null;
    }




    public void removeNoParamNoResult(BaseFunctionNoParamNoResult function) {
        if (mNoParamNoResultMap != null && mNoParamNoResultMap.containsKey(function.getFunctionName())) {
            mNoParamNoResultMap.remove(function.getFunctionName());
        }
    }


    public void removeWithParamOnly(BaseFunctionWithParamOnly function) {
        if (mWithParamOnlyMap.containsKey(function.getFunctionName())) {
            mWithParamOnlyMap.remove(function.getFunctionName());
        }
    }
}
