package com.citi.cpb.logger.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;

@Aspect
public class LoggerInterceptorAspect {

    @Around("execution(* org.slf4j.Logger.*(..))")
    public Object interceptLoggingStatements(ProceedingJoinPoint joinPoint)
        throws Throwable{
        Object args[] = joinPoint.getArgs();
        if(args != null){
            //System.out.println("args length - "+ args.length);
            if(args.length == 1){
                return joinPoint.proceed();
            }else if(args.length > 1){
                Object redactedArgs[] = new Object[args.length];
                String loggerStr = (String)args[0];
                //System.out.println("loggerStr - " + loggerStr);
                redactedArgs[0] = loggerStr;
                if(! (args[1] instanceof Object[])){
                    Object arg = args[1];
                    if(arg instanceof Boolean){
                        redactedArgs[1] = arg;
                    }else if (arg instanceof String ||
                            arg instanceof Integer ||
                            arg instanceof Long ||
                            arg instanceof Double) {
                        redactedArgs[1] = "**REDACTED**";
                    } else{
                        List<Annotation> annotations = Arrays.asList(arg.getClass().getAnnotations());
                        boolean annotationFound = false;
                        for(Annotation a : annotations){
                            if(a.annotationType().getCanonicalName() == NoPiiInfo.class.getCanonicalName()){
                                annotationFound = true;
                                break;
                            }
                        }
                        if(annotationFound){
                            redactedArgs[1] = arg;
                        }else{
                            redactedArgs[1] = "**REDACTED**";
                        }
                    }
                } else {
                    Object[] loggerArgs = (Object[]) args[1];
                    //System.out.println(loggerArgs.length);
                    Object loggerRedactedArgs[] = new Object[loggerArgs.length];
                    for (int i = 0; i < loggerArgs.length; i++) {
                        Object arg = loggerArgs[i];
                        if(arg instanceof Boolean){
                            loggerRedactedArgs[i] = arg;
                        }else if (arg instanceof String ||
                                arg instanceof Integer ||
                                arg instanceof Long ||
                                arg instanceof Double) {
                            loggerRedactedArgs[i] = "**REDACTED**";
                        } else if(arg instanceof Allow){
                            loggerRedactedArgs[i] = ((Allow)arg).getInnerObj();
                        } else{
                            List<Annotation> annotations = Arrays.asList(arg.getClass().getAnnotations());
                            boolean annotationFound = false;
                            for(Annotation a : annotations){
                                if(a.annotationType().getCanonicalName() == NoPiiInfo.class.getCanonicalName()){
                                    annotationFound = true;
                                    break;
                                }
                            }
                            if(annotationFound){
                                loggerRedactedArgs[i] = arg;
                            }else{
                                loggerRedactedArgs[i] = "**REDACTED**";
                            }
                        }
                    }
                    redactedArgs[1] = loggerRedactedArgs;
                }
                return joinPoint.proceed(redactedArgs);
            }
        }
        return joinPoint.proceed();
    }
}
