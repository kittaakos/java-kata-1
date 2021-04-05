package org.echocat.kata.java.part1.ui;

import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Text;

public abstract class DelayedModifyListener implements ModifyListener {

    private Text host;
    private long delayMillis;
    private Timer timer;

    public DelayedModifyListener(Text host, long delayMillis) {
        this.host = host;
        this.delayMillis = delayMillis;
        this.host.addDisposeListener(e -> cleanTimer());
    }

    @Override
    public final void modifyText(ModifyEvent e) {
        cleanTimer();
        timer = new Timer();
        String value = host.getText();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                host.getDisplay().asyncExec(() -> onTextDidModify(e, value));
            }

        }, delayMillis);
    }

    private void cleanTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }

    /**
     * Runs on the UI thread.
     */
    protected abstract void onTextDidModify(ModifyEvent e, String value);

}