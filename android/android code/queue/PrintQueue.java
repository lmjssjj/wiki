package com.nuumobile.emenu.services.queue;

import android.os.Handler;
import android.os.Looper;

import java.util.LinkedList;
import java.util.Queue;

public class PrintQueue {
    private Queue<PrintAction> mQueue = new LinkedList<>();
    private Handler mMainHandler;

    public PrintQueue(Handler mainHandler) {
        this.mMainHandler = mainHandler;
    }

    public void enqueue(final PrintAction action) {
        if (mQueue.isEmpty()
                && Thread.currentThread() == Looper.getMainLooper().getThread()) {
            action.run();
            return;
        }

        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                enqueueAction(action);
            }
        });
    }

    private void enqueueAction(PrintAction action) {
        mQueue.add(action);
        if (mQueue.size() == 1) {
            handleAction();
        }
    }

    private void handleAction() {
        if (mQueue.isEmpty()) return;

        PrintAction action = mQueue.peek();
        action.run();

        executeNextAction(action);
    }

    private void executeNextAction(PrintAction action) {
        if (action.action == PrintAction.ACTION_BILL) {

        } else if (action.action == PrintAction.ACTION_KITCHENRECEIPTS) {

        }

        mMainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mQueue.poll();
                handleAction();
            }
        }, action.duration);
    }

}
