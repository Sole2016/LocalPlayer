package com.zy.vplayer.tv.struct;

/**
 * @author ZhiTouPC
 */
public abstract class BaseFunctionWithResultOnly<Result> extends BaseFunction {
    public BaseFunctionWithResultOnly(String functionName) {
        super(functionName);
    }

    /**
     * function
     * @return 返回值
     */
    public abstract Result function();
}
