package com.mediatek.aop;

import java.lang.reflect.Modifier;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;

import android.util.Log;

/**
 * @author vend_john.bai
 * The Aspect is for logging the message exchange between BT java framework & BT native layer
 */
 
public aspect BtAppTraceAspect {
    private final static String TAG = "Aspect-BTAPP";
    private final static String INDENT = "        ";

    // pointcut to intercept a method in the application project

    ///Intercept GAP logs
    protected pointcut traceGapRequestNative(): 
        call(* *Native(..))
        && target(com.android.bluetooth.btservice.AdapterService)
        && if(Modifier.isNative(thisJoinPointStaticPart.getSignature().getModifiers()));

    Object around() : traceGapRequestNative() {
        Signature sig = thisJoinPointStaticPart.getSignature();
        Object[] args = thisJoinPoint.getArgs();

        log("Java => Native: [" + sig.getName() + "]");

        ///Log arguments
        printArgs(args);

        return proceed();//Invoke the current joint-point method
    }

    protected pointcut traceGapResponse(): 
        execution(* *Callback(..))
        && within(com.android.bluetooth.btservice.JniCallbacks);

    before() : traceGapResponse() {
        Signature sig = thisJoinPointStaticPart.getSignature();
        Object[] args = thisJoinPoint.getArgs();

        log("Java <= Native: [" + sig.getName() + "]");

        ///Log arguments
        printArgs(args);
    }

    ///Intercept HFP logs
    protected pointcut traceHfpRequestNative(): 
        call(* *Native(..))
        && target(com.android.bluetooth.hfp.HeadsetStateMachine)
        && if(Modifier.isNative(thisJoinPointStaticPart.getSignature().getModifiers()));

    Object around() : traceHfpRequestNative() {
        Signature sig = thisJoinPointStaticPart.getSignature();
        Object[] args = thisJoinPoint.getArgs();

        log("Java => Native: [" + sig.getName() + "]");

        ///Log arguments
        printArgs(args);

        return proceed();//Invoke the current joint-point method
    }

    protected pointcut traceHfpResponse(): 
        execution(private * on*(..))
        && within(com.android.bluetooth.hfp.HeadsetStateMachine);

    before() : traceHfpResponse() {
        Signature sig = thisJoinPointStaticPart.getSignature();
        Object[] args = thisJoinPoint.getArgs();

        log("Java <= Native: [" + sig.getName() + "]");

        ///Log arguments
        printArgs(args);
    }

    ///Print the arguments' content based on different typess
    protected void printArgs(Object[] args)
    {   
        boolean bIsNeedIndent = false;

        for(int i = 0; i< args.length; i++){

            StringBuffer sb = new StringBuffer();
            sb.append(INDENT + "Arg");
            sb.append(i).append(":[");

            ///Layout log string based on different argument types
            if(args[i] instanceof int[])
            {
                int[] temp = (int[])args[i];
                for(int j = 0; j < temp.length; j++)
                {
                    sb.append(temp[j]);

                    if(j != temp.length - 1)
                        sb.append(",");
                }
            }
            else if(args[i] instanceof byte[][])
            {
                bIsNeedIndent = true;

                byte[][] temp = (byte[][])args[i];

                for(int j = 0; j < temp.length; j++)
                {
                    if(j == 0)
                        sb.append("\n");

                    sb.append(INDENT).append(INDENT).
                    append("["+j+"] = ");

                    for(int k = 0; k < temp[j].length; k++)
                    {
                        sb.append(temp[j][k]);
                        if(k != temp[j].length - 1)
                            sb.append(",");
                    }
                    if(j != temp.length)
                    {
                        sb.append("\n");
                    }
                    else
                    {
                        sb.append("\n").append(INDENT);
                    }
                }
            }
            else if(args[i] instanceof byte[])
            {
                sb.append(addressToString((byte[])args[i]));
            }
            else
            {
                sb.append(args[i]);
            }

            if(bIsNeedIndent)
                sb.append(INDENT);

            sb.append("]");

            log(sb.toString());

            ///Separate Args
            //if(i == args.length - 1)
            //    log("        \n");
        }
    }

    protected void log(String msg) {
        Log.i(TAG, msg);
    }

    protected String addressToString(byte[] address) {
        String s = null;

        for(int i = 0; i< address.length ; i++)
        {
            if(s == null)
                s  = String.format("%h", address[i] & 0xFF);
            else
                s  = s + String.format(":%h", address[i] & 0xFF);
        }
        return s;
    }
}
