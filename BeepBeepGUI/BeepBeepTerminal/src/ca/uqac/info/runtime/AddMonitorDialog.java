/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.uqac.info.runtime;

import ca.uqac.info.ltl.Operator;
import ca.uqac.info.monitor.EventNotifierGui;
import ca.uqac.info.monitor.MonitorFactory;
import ca.uqac.info.monitor.Monitor;
import ca.uqac.info.util.FileReadWrite;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JFileChooser;

/**
 *
 * @author Armand
 */
public class AddMonitorDialog extends javax.swing.JDialog {

    private Object[] result = new Object[2];//Retourne [Monitor, Metadata]
    private boolean formulaFromFile;
    private EventNotifierGui en;
    /**
     * Return codes
     */
    public static final int ERR_OK = 0;
    public static final int ERR_PARSE = 2;
    public static final int ERR_IO = 3;
    public static final int ERR_ARGUMENTS = 4;
    public static final int ERR_RUNTIME = 6;

    /**
     * Creates new form AddMonitorDialog
     */
    public AddMonitorDialog(java.awt.Frame parent, boolean modal, EventNotifierGui en) {
        super(parent, modal);
        this.formulaFromFile = false;
        this.en = en;
        initComponents();
        //Delete after tests.
        this.formulaTextField.setText("F (/message/x > 7)");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        formulaTextField = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        descriptionTextArea = new javax.swing.JTextArea();
        addMonitorButton = new javax.swing.JButton();
        FormulaSymbolsButton = new javax.swing.JButton();
        nameTextField = new javax.swing.JTextField();
        loadFormulaButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Add monitor");
        setAlwaysOnTop(true);

        formulaTextField.setText("Formula");

        descriptionTextArea.setColumns(20);
        descriptionTextArea.setRows(5);
        descriptionTextArea.setText("Description");
        jScrollPane1.setViewportView(descriptionTextArea);
        descriptionTextArea.getAccessibleContext().setAccessibleName("DescriptionTextArea");

        addMonitorButton.setText("Add monitor");
        addMonitorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addMonitorButtonActionPerformed(evt);
            }
        });

        FormulaSymbolsButton.setText("Formula symbols");

        nameTextField.setText("Name");

        loadFormulaButton.setText("Load Formula");
        loadFormulaButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadFormulaButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 269, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(FormulaSymbolsButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(addMonitorButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(nameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(formulaTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(loadFormulaButton, javax.swing.GroupLayout.PREFERRED_SIZE, 94, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(formulaTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(nameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(loadFormulaButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(FormulaSymbolsButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(addMonitorButton)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        formulaTextField.getAccessibleContext().setAccessibleName("formulaTextField");
        addMonitorButton.getAccessibleContext().setAccessibleName("addMonitorButton");
        FormulaSymbolsButton.getAccessibleContext().setAccessibleName("FormulaSymbolsButton");
        nameTextField.getAccessibleContext().setAccessibleName("nameTextField");
        loadFormulaButton.getAccessibleContext().setAccessibleName("loadFormulaButton");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void addMonitorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addMonitorButtonActionPerformed
        try {
            final MonitorFactory mf = new MonitorFactory();
            String formula_contents = null;
            String formula_filename = null;
            if (String.valueOf(this.formulaTextField.getText().charAt(0)).equals("/")) {
                this.formulaFromFile = true;
            }
            if (this.formulaFromFile) {//Si on choisi de charger un fichier de formule depuis un fichier
                //System.out.println("Reading from : " + this.formulaTextField.getText());
                formula_contents = FileReadWrite.readFile(this.formulaTextField.getText());
                formula_filename = this.nameTextField.getText();
            } else {//Sinon, la formule est écrite dans la barre de formule
                //System.out.println("No / detected");
                formula_contents = this.formulaTextField.getText();
            }
            Operator op = Operator.parseFromString(formula_contents);
            op.accept(mf);
            Monitor mon = mf.getMonitor();
            Map<String, String> metadata = getMetadata(formula_contents);
            metadata.put("Filename", formula_filename);
            metadata.put("Name", this.nameTextField.getText());
            metadata.put("Caption", this.descriptionTextArea.getText());
            metadata.put("Formula", mon.toString());
            this.en.addMonitor(mon, metadata);
            this.result[0] = mon;
            this.result[1] = metadata;
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(ERR_IO);
        } catch (Operator.ParseException e) {
            System.err.println("Error parsing input formula");
            System.exit(ERR_PARSE);
        }   
//        } catch(Exception e){
//            JOptionPane.showMessageDialog(this,
//                    "Error creating Monitor, please try again.",
//                    "Monitor error",
//                    JOptionPane.ERROR_MESSAGE);
//        }
        setVisible(false);
        dispose();
    }//GEN-LAST:event_addMonitorButtonActionPerformed

    private void loadFormulaButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadFormulaButtonActionPerformed
        final JFileChooser fc = new JFileChooser();
        int returnVal = fc.showOpenDialog(AddMonitorDialog.this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            this.formulaFromFile = true;
            File file = fc.getSelectedFile();
            this.formulaTextField.setText(file.getAbsolutePath());
            this.nameTextField.setText(file.getName());
        }
    }//GEN-LAST:event_loadFormulaButtonActionPerformed

    Object[] showDialog() {
        this.setVisible(true);
        return this.result;
    }

    /**
     * Parses optional metadata that can be found in a formula's input file.
     * The metadata must appear in the comment lines (those beginning with
     * a "#" symbol) and must be of the form:
     * <pre>
     * # @Param("some value");
     * </pre>
     * where Param is some (user-defined) parameter name. Parameters may
     * span multiple lines, which still must each begin with the comment
     * symbol, as follows:
     * <pre>
     * # @Param("some value
     * #     that spans multiple lines");
     * </pre>
     * The "#" and extraneous spaces are removed on parsing. Currently BeepBeep
     * uses the parameter "Caption", if present, to display a name for each
     * monitor. All other parameters are presently ignored.
     * @param file_contents The string contents of the formula file
     * @return A map associating parameters to values
     */
    public static Map<String, String> getMetadata(String fileContents) {
        Map<String, String> out_map = new HashMap<String, String>();
        StringBuilder comment_contents = new StringBuilder();
        Pattern pat = Pattern.compile("^\\s*?#(.*?)$", Pattern.MULTILINE);
        Matcher mat = pat.matcher(fileContents);
        while (mat.find()) {
            String line = mat.group(1).trim();
            comment_contents.append(line).append(" ");
        }
        pat = Pattern.compile("@(\\w+?)\\((.*?)\\);");
        mat = pat.matcher(comment_contents);
        while (mat.find()) {
            String key = mat.group(1);
            String val = mat.group(2).trim();
            if (val.startsWith("\"") && val.endsWith("\"")) {
                // Trim double quotes if any
                val = val.substring(1, val.length() - 1);
            }
            out_map.put(key, val);
        }
        return out_map;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton FormulaSymbolsButton;
    private javax.swing.JButton addMonitorButton;
    private javax.swing.JTextArea descriptionTextArea;
    private javax.swing.JTextField formulaTextField;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton loadFormulaButton;
    private javax.swing.JTextField nameTextField;
    // End of variables declaration//GEN-END:variables
}
