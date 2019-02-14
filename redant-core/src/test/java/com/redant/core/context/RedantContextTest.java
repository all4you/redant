package com.redant.core.context;

/**
 * @author houyi
 **/
public class RedantContextTest {

    public static void main(String[] args) {
        for(int i=0;i<10;i++){
            new Thread(new ContextRunner()).start();
        }
    }

    private static class ContextRunner implements Runnable {
        @Override
        public void run() {
            RedantContext context = RedantContext.currentContext();
            System.out.println(context);
        }
    }


}
