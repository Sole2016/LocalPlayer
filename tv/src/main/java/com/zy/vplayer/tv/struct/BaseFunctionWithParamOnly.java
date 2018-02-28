package com.zy.vplayer.tv.struct;

/**
 * @author ZhiTouPC
 */
public abstract class BaseFunctionWithParamOnly<Param> extends BaseFunction {
    public BaseFunctionWithParamOnly(String functionName) {
        super(functionName);
    }

    /**
     * 回调实现
     * @param param 参数类型
     */
    public abstract void function(Param param);
}
