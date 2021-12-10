package com.nuumobile.emenu.services.queue;

public abstract class PrintAction {

    public static final int ACTION_NORMAL = 0;
    public static final int ACTION_BILL = 1;
    public static final int ACTION_KITCHENRECEIPTS = 2;


    public int action = ACTION_NORMAL;
    public long duration = 0;

    public PrintAction() {
    }

    public PrintAction(int action) {
        this.action = action;
    }

    public abstract void run();
}
