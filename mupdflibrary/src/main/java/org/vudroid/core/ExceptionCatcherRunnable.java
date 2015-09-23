package org.vudroid.core;

/**
 * Created by Brandon on 15/8/14.
 */
public class ExceptionCatcherRunnable implements Runnable {

    private ExceptionListener listener;
    private Runnable runnable;

    public ExceptionCatcherRunnable(ExceptionListener listener,Runnable runnable) {
        this.listener = listener;
        this.runnable =runnable;
    }

    @Override
    public void run() {

        try {
            runnable.run();
        } catch(Exception e) {
            listener.notifyThatDarnedExceptionHappened(e.getMessage());
        }

    }

    public interface ExceptionListener{
        void notifyThatDarnedExceptionHappened(String message);
    }
}
