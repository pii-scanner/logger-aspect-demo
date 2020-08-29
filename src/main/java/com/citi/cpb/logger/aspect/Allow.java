package com.citi.cpb.logger.aspect;

public class Allow {

    Object obj;

    public Allow(Object obj){
        this.obj = obj;
    }

    public Object getInnerObj(){
        return this.obj;
    }
}
