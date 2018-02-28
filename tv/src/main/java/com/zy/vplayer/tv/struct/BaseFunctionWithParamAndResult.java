package com.zy.vplayer.tv.struct;

/**
 * @author ZhiTouPC
 */
public abstract class BaseFunctionWithParamAndResult<Param,Result> extends BaseFunction{
    public BaseFunctionWithParamAndResult(String functionName) {
        super(functionName);
    }

    /**
     * function
     * @param param 参数
     * @return 回传值
     */
    public abstract Result function(Param param);
}
