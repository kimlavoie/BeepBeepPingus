/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.uqac.info.monitor;

import ca.uqac.info.runtime.TerminalGuiFrame;
import ca.uqac.info.util.PipeCallback;
import ca.uqac.info.util.OnNotifyListener;
import ca.uqac.info.util.PipeCallback.CallbackException;
import java.io.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Vector;

/**
 *
 * @author armand
 */
public class EventNotifierGui implements PipeCallback<String> {

    Vector<Monitor> m_monitors;
    Vector<Monitor.Verdict> m_verdicts;
    Vector<Map<String, String>> m_metadatas;
    
    public String event_name = "message";
    public int m_numEvents = 0;
    public boolean m_notifyOnEvents = false;//Used notify events (not used here)
    public boolean m_notifyOnVerdict = true;//Used to print on console (not used here)
    public boolean m_mirrorEventsOnStdout = false;
    public boolean m_csvToStdout = false;
    public long m_totalTime = 0;
    public long heapSize = 0;
    public int m_slowdown = 0;
    private ArrayList<OnNotifyListener> listeners = new ArrayList<OnNotifyListener>();

    public EventNotifierGui(TerminalGuiFrame fr) {
        m_monitors = new Vector<Monitor>();
        m_verdicts = new Vector<Monitor.Verdict>();
        m_metadatas = new Vector<Map<String, String>>();
        this.listeners.add(fr);
    }

    public EventNotifierGui(boolean notifyOnEvents) {
        this(null);
        m_notifyOnEvents = notifyOnEvents;
    }

    public void addMonitor(Monitor w) {
        addMonitor(w, new HashMap<String, String>());
    }

    public void addMonitor(Monitor w, Map<String, String> metadata) {
        m_monitors.add(w);
        m_verdicts.add(Monitor.Verdict.INCONCLUSIVE);
        m_metadatas.add(metadata);
    }

    public int eventCount() {
        return m_numEvents;
    }

    @Override
    public void notify(String token, long buffer_size) throws CallbackException {
        //System.out.println("EventNotifierGui -> notify()");
        m_numEvents++;
        //System.out.println(ESC_HOME + ESC_CLEARLINE + "Event received");
        if (m_mirrorEventsOnStdout) {
            System.out.print(token);
        }
        // Update all monitors
        Event e = new XPathEvent(token);
        long processing_time = 0;
        for (int i = 0; i < m_monitors.size(); i++) {
            long clock_start = System.nanoTime();
            Monitor m = m_monitors.elementAt(i);
            Monitor.Verdict old_out = m_verdicts.elementAt(i);
            try {
                m.processEvent(e);
            } catch (MonitorException ex) {
                throw new CallbackException(ex.getMessage());
            }
            Monitor.Verdict new_out = m.getVerdict();
            if (m_slowdown > 0) {
                try {
                    // We force the monitor to slow down by sleeping N ms
                    Thread.sleep(m_slowdown);
                } catch (InterruptedException ie) {
                    // TODO Auto-generated catch block
                    ie.printStackTrace();
                }
            }
            long clock_end = System.nanoTime();
            processing_time = clock_end - clock_start;
            m_totalTime += processing_time;
            m_verdicts.set(i, m.getVerdict());
            if (old_out != new_out && m_notifyOnVerdict) {
                Map<String, String> metadata = m_metadatas.elementAt(i);
                String command = null;
                this.fireVerdictChangeEvent();//Quand un Monitor change d'état, on prévient tous les écouteurs
                if (new_out == Monitor.Verdict.TRUE) {
                    command = metadata.get("OnTrue");
                }
                if (new_out == Monitor.Verdict.FALSE) {
                    command = metadata.get("OnFalse");
                }
                if (command != null) {
                    try {
                        File f = new File(metadata.get("Filename"));
                        String absolute_path = f.getAbsolutePath();
                        String s_dir = absolute_path.substring(0, absolute_path.lastIndexOf(File.separator));
                        File dir = new File(s_dir);
                        Runtime.getRuntime().exec("./" + command, null, dir);
                    } catch (IOException ioe) {
                        // TODO Auto-generated catch block
                        ioe.printStackTrace();
                    }
                }
            }
            heapSize = Math.max(heapSize, Runtime.getRuntime().totalMemory());
            //System.out.println("EventNotifierGui -> this.fireOnNotifyEvent()");
            this.fireOnNotifyEvent();
        }


        /*
         * Class EventNotifier (original) : Show in console
         */
    }

    public void reset() {
        m_numEvents = 0;
        m_totalTime = 0;
        for (int i = 0; i < m_monitors.size(); i++) {
            Monitor m = m_monitors.elementAt(i);
            m.reset();
            m_verdicts.setElementAt(Monitor.Verdict.INCONCLUSIVE, i);
        }
        if (m_notifyOnEvents) {
            printHeader();
        }
    }

    public void reset(int i) {
        m_numEvents = 0;
        Monitor m = m_monitors.elementAt(i);
        m.reset();
        m_verdicts.setElementAt(Monitor.Verdict.INCONCLUSIVE, i);
    }

    protected void printHeader() {
        System.err.print("\nMsgs |Last   |Total     |Max heap |Buffer   |");
        for (Map<String, String> metadata : m_metadatas) {
            String caption = metadata.get("Caption");
            if (caption.isEmpty()) {
                System.err.print("· ");
            } else {
                System.err.print(caption + " ");
            }
        }
        System.err.println();
    }

    public Vector<Monitor> getAllMonitors() {
        return m_monitors;
    }

    public void addListener(OnNotifyListener l) {
        this.listeners.add(l);
    }

    public void removeListener(OnNotifyListener l) {
        this.listeners.remove(l);
    }

    private void fireVerdictChangeEvent() {
        for (OnNotifyListener listener : this.listeners) {
            listener.onVerdictChange();
        }
    }

    private void fireOnNotifyEvent() {
        //System.out.println("EventNotifierGui -> fireOnNotifyEvent()");
        for (OnNotifyListener listener : this.listeners) {
            listener.onNotify();
        }
    }
}
