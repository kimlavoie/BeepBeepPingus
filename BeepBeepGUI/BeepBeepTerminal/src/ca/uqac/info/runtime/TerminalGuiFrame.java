/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.uqac.info.runtime;

import ca.uqac.info.monitor.EventNotifierGui;
import ca.uqac.info.monitor.Monitor;
import ca.uqac.info.util.OnNotifyListener;
import ca.uqac.info.util.PipeReader;
import java.awt.Component;
import java.util.Map;
import javax.swing.BoxLayout;

/**
 *
 * @author Armand
 */
public class TerminalGuiFrame extends javax.swing.JFrame implements OnNotifyListener {

    EventNotifierGui en;
    PipeReader pr;
    private Thread tr;
    private int nbNotifyCalls; //Used to calculate median time.
    private long totalTime; //Used to calculate median time.

    /**
     * Creates new form TerminalGuiFrame
     */
    public TerminalGuiFrame() {
        initComponents();
        this.en = new EventNotifierGui(this);
        this.nbNotifyCalls = 0;
        this.totalTime = 0;
        this.monitorsListJPanel.setLayout(new BoxLayout(this.monitorsListJPanel, BoxLayout.Y_AXIS));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainTabbedPane = new javax.swing.JTabbedPane();
        monitorTabbedPane = new javax.swing.JPanel();
        monitorsListPanel = new javax.swing.JScrollPane();
        monitorsListJPanel = new javax.swing.JPanel();
        infosPanel = new javax.swing.JPanel();
        bufferLabel = new javax.swing.JLabel();
        timeLabel = new javax.swing.JLabel();
        resetInfosBarButton = new javax.swing.JButton();
        jProgressBar1 = new javax.swing.JProgressBar();
        currentTimeLabel = new javax.swing.JLabel();
        bufferSizeLabel = new javax.swing.JLabel();
        logTabbedPane = new javax.swing.JPanel();
        menuBar = new javax.swing.JMenuBar();
        fileMenuBar = new javax.swing.JMenu();
        trackingMenuItem = new javax.swing.JMenuItem();
        addMonitorMenuItem = new javax.swing.JMenuItem();
        infosMenuBar = new javax.swing.JMenu();
        helpMenuItem = new javax.swing.JMenuItem();
        aboutMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("BeepBeep Monitor");
        setResizable(false);

        mainTabbedPane.setBackground(new java.awt.Color(242, 241, 240));

        monitorsListPanel.setBackground(new java.awt.Color(255, 255, 204));
        monitorsListPanel.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        javax.swing.GroupLayout monitorsListJPanelLayout = new javax.swing.GroupLayout(monitorsListJPanel);
        monitorsListJPanel.setLayout(monitorsListJPanelLayout);
        monitorsListJPanelLayout.setHorizontalGroup(
            monitorsListJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 599, Short.MAX_VALUE)
        );
        monitorsListJPanelLayout.setVerticalGroup(
            monitorsListJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 308, Short.MAX_VALUE)
        );

        monitorsListPanel.setViewportView(monitorsListJPanel);
        monitorsListJPanel.getAccessibleContext().setAccessibleName("monitorsListJPanel");

        infosPanel.setBackground(new java.awt.Color(253, 243, 202));

        bufferLabel.setText("Buffer");

        timeLabel.setText("Time");

        resetInfosBarButton.setText("Reset");
        resetInfosBarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetInfosBarButtonActionPerformed(evt);
            }
        });

        currentTimeLabel.setText("00 (ms.)");

        bufferSizeLabel.setText("0");

        javax.swing.GroupLayout infosPanelLayout = new javax.swing.GroupLayout(infosPanel);
        infosPanel.setLayout(infosPanelLayout);
        infosPanelLayout.setHorizontalGroup(
            infosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(infosPanelLayout.createSequentialGroup()
                .addComponent(bufferLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bufferSizeLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 254, Short.MAX_VALUE)
                .addGap(6, 6, 6)
                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(timeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(currentTimeLabel)
                .addGap(40, 40, 40)
                .addComponent(resetInfosBarButton)
                .addContainerGap())
        );
        infosPanelLayout.setVerticalGroup(
            infosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(infosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(bufferLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
                .addComponent(bufferSizeLabel))
            .addGroup(infosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(resetInfosBarButton)
                .addComponent(currentTimeLabel)
                .addComponent(timeLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(infosPanelLayout.createSequentialGroup()
                .addComponent(jProgressBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        bufferLabel.getAccessibleContext().setAccessibleName("bufferLabel");
        timeLabel.getAccessibleContext().setAccessibleName("timeLabel");
        resetInfosBarButton.getAccessibleContext().setAccessibleName("resetInfosBarButton");

        javax.swing.GroupLayout monitorTabbedPaneLayout = new javax.swing.GroupLayout(monitorTabbedPane);
        monitorTabbedPane.setLayout(monitorTabbedPaneLayout);
        monitorTabbedPaneLayout.setHorizontalGroup(
            monitorTabbedPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(monitorsListPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 601, Short.MAX_VALUE)
            .addComponent(infosPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        monitorTabbedPaneLayout.setVerticalGroup(
            monitorTabbedPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, monitorTabbedPaneLayout.createSequentialGroup()
                .addComponent(monitorsListPanel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(infosPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        mainTabbedPane.addTab("Monitor", monitorTabbedPane);
        monitorTabbedPane.getAccessibleContext().setAccessibleName("monitorTabbedPane");

        javax.swing.GroupLayout logTabbedPaneLayout = new javax.swing.GroupLayout(logTabbedPane);
        logTabbedPane.setLayout(logTabbedPaneLayout);
        logTabbedPaneLayout.setHorizontalGroup(
            logTabbedPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 601, Short.MAX_VALUE)
        );
        logTabbedPaneLayout.setVerticalGroup(
            logTabbedPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 346, Short.MAX_VALUE)
        );

        mainTabbedPane.addTab("Log", logTabbedPane);
        logTabbedPane.getAccessibleContext().setAccessibleName("logTabbedPane");

        menuBar.setBackground(new java.awt.Color(253, 243, 202));

        fileMenuBar.setText("File");

        trackingMenuItem.setText("Start tracking...");
        trackingMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                trackingMenuItemActionPerformed(evt);
            }
        });
        fileMenuBar.add(trackingMenuItem);
        trackingMenuItem.getAccessibleContext().setAccessibleName("trackingMenuItem");

        addMonitorMenuItem.setLabel("Add monitor");
        addMonitorMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addMonitorMenuItemActionPerformed(evt);
            }
        });
        fileMenuBar.add(addMonitorMenuItem);
        addMonitorMenuItem.getAccessibleContext().setAccessibleName("addMonitorMenuItem");

        menuBar.add(fileMenuBar);
        fileMenuBar.getAccessibleContext().setAccessibleName("FileMenuBar");

        infosMenuBar.setText("?");

        helpMenuItem.setText("Help");
        helpMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                helpMenuItemActionPerformed(evt);
            }
        });
        infosMenuBar.add(helpMenuItem);

        aboutMenuItem.setText("About");
        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMenuItemActionPerformed(evt);
            }
        });
        infosMenuBar.add(aboutMenuItem);

        menuBar.add(infosMenuBar);
        infosMenuBar.getAccessibleContext().setAccessibleName("infosMenuBar");

        setJMenuBar(menuBar);
        menuBar.getAccessibleContext().setAccessibleName("menuBar");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainTabbedPane)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainTabbedPane)
        );

        mainTabbedPane.getAccessibleContext().setAccessibleName("mainTabbedPane");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Créé une JDialog de d'ajout de Monitor
     * @param evt 
     */
    private void addMonitorMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addMonitorMenuItemActionPerformed
        AddMonitorDialog addMonitorDialog = new AddMonitorDialog(this, rootPaneCheckingEnabled, this.en);
        Object[] returnValue = addMonitorDialog.showDialog();//Retourne [Monitor, Metadata]
        if (returnValue[0] != null) {
            MonitorPanel newMonPanel = new MonitorPanel((Monitor) returnValue[0], (Map<String, String>) returnValue[1]);
            this.monitorsListJPanel.add(newMonPanel);
            this.monitorsListJPanel.doLayout();
        }
    }//GEN-LAST:event_addMonitorMenuItemActionPerformed

    private void trackingMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_trackingMenuItemActionPerformed
        TrackingDialog trackingDialog = new TrackingDialog(this, rootPaneCheckingEnabled, this.en);
        this.pr = trackingDialog.showDialog();
        this.tr = new Thread(pr);
        tr.start();
        System.out.println("Pipe reader created : " + this.pr.toString());
    }//GEN-LAST:event_trackingMenuItemActionPerformed

    private void resetInfosBarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetInfosBarButtonActionPerformed
        this.bufferSizeLabel.setText("0");
        this.currentTimeLabel.setText("00 (ms.)");
        this.nbNotifyCalls = 0;
        this.totalTime = 0;
        this.repaint();
    }//GEN-LAST:event_resetInfosBarButtonActionPerformed

    private void helpMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helpMenuItemActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_helpMenuItemActionPerformed

    private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutMenuItemActionPerformed
        AboutDialog ad = new AboutDialog(this, rootPaneCheckingEnabled);
    }//GEN-LAST:event_aboutMenuItemActionPerformed

//    private void refreshGui() {
//        Vector<Monitor> monitorsVector = this.en.getAllMonitors();
//        for (int i = 0; i < monitorsVector.size(); i++) {
//            Monitor m = monitorsVector.elementAt(i);
//            System.out.println(m.toString());
//        }
//    }
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Metal".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;


                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TerminalGuiFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TerminalGuiFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TerminalGuiFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TerminalGuiFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new TerminalGuiFrame().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JMenuItem addMonitorMenuItem;
    private javax.swing.JLabel bufferLabel;
    private javax.swing.JLabel bufferSizeLabel;
    private javax.swing.JLabel currentTimeLabel;
    private javax.swing.JMenu fileMenuBar;
    private javax.swing.JMenuItem helpMenuItem;
    private javax.swing.JMenu infosMenuBar;
    private javax.swing.JPanel infosPanel;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JPanel logTabbedPane;
    private javax.swing.JTabbedPane mainTabbedPane;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JPanel monitorTabbedPane;
    private javax.swing.JPanel monitorsListJPanel;
    private javax.swing.JScrollPane monitorsListPanel;
    private javax.swing.JButton resetInfosBarButton;
    private javax.swing.JLabel timeLabel;
    private javax.swing.JMenuItem trackingMenuItem;
    // End of variables declaration//GEN-END:variables

    /**
     * TerminalGuiFrame est dans la liste des écouteurs de EventNotifierGui.
     * Quand l'un des Monitor change de verdict, onVerdictChange est appelée.
     * Dans ce cas, actualise la liste de panels des Monitors.
     */
    @Override
    public void onVerdictChange() {
        System.out.println("TerminalGUIFrame -> onVerdictChange");
        this.repaint();
    }

    @Override
    public void onNotify() {
        //System.out.println("TerminalGUIFrame -> onNotify");
        this.nbNotifyCalls++;
        this.totalTime += this.en.m_totalTime;
        long medianTime = this.totalTime / this.nbNotifyCalls;
        this.currentTimeLabel.setText(String.valueOf(this.en.m_totalTime + "(ms.) / " + String.valueOf(medianTime) + "(moy.)"));

        this.bufferSizeLabel.setText(String.valueOf(this.en.heapSize));
    }

    @Override
    public void repaint() {
        super.repaint();
        Component[] cs = this.monitorsListJPanel.getComponents();
        for (int i = 0; i < cs.length; i++) {
            cs[i].repaint();
        }
    }
}