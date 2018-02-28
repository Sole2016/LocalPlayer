package com.zy.vplayer.tv.struct;

/**
 * @author ZhiTouPC
 */
public abstract class BaseFunctionNoParamNoResult extends BaseFunction {
    public BaseFunctionNoParamNoResult(String functionName) {
        super(functionName);
    }

    /**
     * 函数实现
     */
    public abstract void function();
}
