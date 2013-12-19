/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import com.google.common.collect.Queues;
import event.EventBean;
import java.util.Collection;
import java.util.Queue;

/**
 *
 * @author epaln
 */
public abstract class EPAgent extends Thread {

    protected String _type;
    protected TopicReceiver _receiver;
    //private String identifier;
    protected String _info;
    protected Queue<EventBean> _selectedEvents;
    private boolean _process = false;
    private static final Object _mutex = new Object();
    private int numbertoSelect;

    public EPAgent() {
        _selectedEvents = Queues.newArrayDeque();
    }

    public abstract Collection<IOTerminal> getInputTerminal();

    public abstract Collection<IOTerminal> getOutputTerminal();

    public abstract void process();

    public abstract boolean select(int numbertoSelect);

    public boolean openIOchannels() {

        for (IOTerminal input : getInputTerminal()) {
            input.open();
        }

        for (IOTerminal output : getOutputTerminal()) {
            output.open();
        }
        return true;
    }

    public void signal(int i) {
        numbertoSelect = i;
        synchronized (_mutex) {
            _process = true;
        }
    }

    @Override
    public void run() {
        while (true) {
            synchronized (_mutex) { // use a synchronized queue here...
                if (_process) {
                    _process = false;
                    if (select(numbertoSelect)) {
                        process();
                    }

                }
            }

        }

    }
}
