/*
 * AddDbDialog.java
 * 
 * Created on December 18, 2008, 10:13 AM
 */

package gblend.db;

import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.WindowConstants;

import prague.data.DataParams;

/**
 * @author cjjin
 */
public class AddDbDialog extends JDialog {

  private static final long serialVersionUID = 1L;

  private DataParams p;
  private boolean returnStatus = false;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JTextField dataSetSize;
    private JTextField dbName;
    private JTextField memorySize;
    private JTextField sigma;
    private JTextField support;
    // End of variables declaration//GEN-END:variables

  public AddDbDialog(Frame parent) {
    super(parent, true);
    initComponents2();
  }

  public boolean isOk() {
    return returnStatus;
  }

  
  private void initComponents2() {

        JButton okButton = new JButton();
        JButton cancelButton = new JButton();
        JPanel panel = new JPanel();
        JLabel dbLabel = new JLabel();
        dbName = new JTextField();
        JLabel dataSizeLabel = new JLabel();
        dataSetSize = new JTextField();
        JLabel supportLabel = new JLabel();
        support = new JTextField();
        JLabel msLabel = new JLabel();
        memorySize = new JTextField();
        JLabel sigmaLabel = new JLabel();
        sigma = new JTextField();

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        ResourceBundle bundle = ResourceBundle.getBundle("gblend/db/resources/AddDbDialog"); // NOI18N
        setTitle(bundle.getString("title")); // NOI18N
        setFont(new Font("Times New Roman", 1, 14)); // NOI18N
        setResizable(false);

        okButton.setFont(new Font("Times New Roman", 1, 14)); // NOI18N
        okButton.setText(bundle.getString("OKButton.text")); // NOI18N
        okButton.setName("okButton"); // NOI18N
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.setFont(new Font("Times New Roman", 1, 14)); // NOI18N
        cancelButton.setText(bundle.getString("cancelButton.text")); // NOI18N
        cancelButton.setName("cancelButton"); // NOI18N
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        panel.setBorder(BorderFactory.createTitledBorder("Database Information"));
        panel.setFont(new Font("Times New Roman", 1, 14)); // NOI18N
        panel.setName("panel"); // NOI18N

        dbLabel.setFont(new Font("Times New Roman", 1, 14)); // NOI18N
        dbLabel.setText(bundle.getString("dbLabel.text")); // NOI18N
        dbLabel.setName("dbLabel"); // NOI18N

        dbName.setFont(new Font("Times New Roman", 1, 14)); // NOI18N
        dbName.setText(bundle.getString("dbName.text")); // NOI18N
        dbName.setName("dbName"); // NOI18N

        dataSizeLabel.setFont(new Font("Times New Roman", 1, 14)); // NOI18N
        dataSizeLabel.setText(bundle.getString("dataSizeLabel.text")); // NOI18N
        dataSizeLabel.setName("dataSizeLabel"); // NOI18N

        dataSetSize.setFont(new Font("Times New Roman", 1, 14)); // NOI18N
        dataSetSize.setText(bundle.getString("dataSetSize.text")); // NOI18N
        dataSetSize.setName("dataSetSize"); // NOI18N

        supportLabel.setFont(new Font("Times New Roman", 1, 14)); // NOI18N
        supportLabel.setText(bundle.getString("supportLabel.text")); // NOI18N
        supportLabel.setName("supportLabel"); // NOI18N
        
        supportLabel.setVisible(false);
        
        support.setFont(new Font("Times New Roman", 1, 14)); // NOI18N
        support.setText(bundle.getString("support.text")); // NOI18N
        support.setName("support"); // NOI18N
        support.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                supportActionPerformed(evt);
            }
        });
        support.setVisible(false);

        msLabel.setFont(new Font("Times New Roman", 1, 14)); // NOI18N
        msLabel.setText(bundle.getString("msLabel.text")); // NOI18N
        msLabel.setName("msLabel"); // NOI18N
        msLabel.setVisible(false);
        
        memorySize.setFont(new Font("Times New Roman", 1, 14)); // NOI18N
        memorySize.setText(bundle.getString("memorySize.text")); // NOI18N
        memorySize.setName("memorySize"); // NOI18N
        memorySize.setVisible(false);
        
        sigmaLabel.setFont(new Font("Times New Roman", 1, 14)); // NOI18N
        sigmaLabel.setText(bundle.getString("sigmaLabel.text")); // NOI18N
        sigmaLabel.setName("sigmaLabel"); // NOI18N

        sigma.setFont(new Font("Times New Roman", 1, 14)); // NOI18N
        sigma.setText(bundle.getString("sigma.text")); // NOI18N
        sigma.setName("sigma"); // NOI18N
        sigma.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                sigmaActionPerformed(evt);
            }
        });

        GroupLayout panelLayout = new GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(panelLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                    .addComponent(dbLabel, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(msLabel, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(dataSizeLabel, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                    .addGroup(panelLayout.createSequentialGroup()
                        .addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                            .addComponent(dataSetSize, GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
                            .addComponent(memorySize))
                        .addGap(18, 18, 18)
                        .addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(supportLabel)
                            .addComponent(sigmaLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                            .addComponent(support, GroupLayout.DEFAULT_SIZE, 46, Short.MAX_VALUE)
                            .addComponent(sigma)))
                    .addComponent(dbName))
                .addGap(0, 22, Short.MAX_VALUE))
        );
        panelLayout.setVerticalGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(panelLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(dbName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(dbLabel))
                .addGap(18, 18, 18)
                .addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(panelLayout.createSequentialGroup()
                        .addComponent(dataSizeLabel)
                        .addGap(12, 12, 12)
                        .addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(msLabel)
                            .addComponent(memorySize, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(supportLabel)
                            .addComponent(support, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                    .addComponent(dataSetSize, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(sigmaLabel)
                    .addComponent(sigma, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addContainerGap(48, Short.MAX_VALUE))
        );

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(okButton)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cancelButton)
                .addGap(11, 11, 11))
            .addGroup(layout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addComponent(panel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(78, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(panel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(okButton)
                    .addComponent(cancelButton))
                .addContainerGap())
        );
    }
  
  // @formatter:off
  /**
   * This method is called from within the constructor to initialize the form. WARNING: Do NOT
   * modify this code. The content of this method is always regenerated by the Form Editor.
   */
  @SuppressWarnings("all")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        JButton okButton = new JButton();
        JButton cancelButton = new JButton();
        JPanel panel = new JPanel();
        JLabel dbLabel = new JLabel();
        dbName = new JTextField();
        JLabel dataSizeLabel = new JLabel();
        dataSetSize = new JTextField();
        JLabel supportLabel = new JLabel();
        support = new JTextField();
        JLabel msLabel = new JLabel();
        memorySize = new JTextField();
        JLabel sigmaLabel = new JLabel();
        sigma = new JTextField();

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        ResourceBundle bundle = ResourceBundle.getBundle("gblend/db/resources/AddDbDialog"); // NOI18N
        setTitle(bundle.getString("title")); // NOI18N
        setFont(new Font("Times New Roman", 1, 14)); // NOI18N
        setResizable(false);

        okButton.setFont(new Font("Times New Roman", 1, 14)); // NOI18N
        okButton.setText(bundle.getString("OKButton.text")); // NOI18N
        okButton.setName("okButton"); // NOI18N
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.setFont(new Font("Times New Roman", 1, 14)); // NOI18N
        cancelButton.setText(bundle.getString("cancelButton.text")); // NOI18N
        cancelButton.setName("cancelButton"); // NOI18N
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        panel.setBorder(BorderFactory.createTitledBorder("Database Information"));
        panel.setFont(new Font("Times New Roman", 1, 14)); // NOI18N
        panel.setName("panel"); // NOI18N

        dbLabel.setFont(new Font("Times New Roman", 1, 14)); // NOI18N
        dbLabel.setText(bundle.getString("dbLabel.text")); // NOI18N
        dbLabel.setName("dbLabel"); // NOI18N

        dbName.setFont(new Font("Times New Roman", 1, 14)); // NOI18N
        dbName.setText(bundle.getString("dbName.text")); // NOI18N
        dbName.setName("dbName"); // NOI18N

        dataSizeLabel.setFont(new Font("Times New Roman", 1, 14)); // NOI18N
        dataSizeLabel.setText(bundle.getString("dataSizeLabel.text")); // NOI18N
        dataSizeLabel.setName("dataSizeLabel"); // NOI18N

        dataSetSize.setFont(new Font("Times New Roman", 1, 14)); // NOI18N
        dataSetSize.setText(bundle.getString("dataSetSize.text")); // NOI18N
        dataSetSize.setName("dataSetSize"); // NOI18N
        dataSetSize.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                dataSetSizeActionPerformed(evt);
            }
        });

        supportLabel.setFont(new Font("Times New Roman", 1, 14)); // NOI18N
        supportLabel.setText(bundle.getString("supportLabel.text")); // NOI18N
        supportLabel.setName("supportLabel"); // NOI18N

        support.setFont(new Font("Times New Roman", 1, 14)); // NOI18N
        support.setText(bundle.getString("support.text")); // NOI18N
        support.setName("support"); // NOI18N
        support.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                supportActionPerformed(evt);
            }
        });

        msLabel.setFont(new Font("Times New Roman", 1, 14)); // NOI18N
        msLabel.setText(bundle.getString("msLabel.text")); // NOI18N
        msLabel.setName("msLabel"); // NOI18N

        memorySize.setFont(new Font("Times New Roman", 1, 14)); // NOI18N
        memorySize.setText(bundle.getString("memorySize.text")); // NOI18N
        memorySize.setName("memorySize"); // NOI18N

        sigmaLabel.setFont(new Font("Times New Roman", 1, 14)); // NOI18N
        sigmaLabel.setText(bundle.getString("sigmaLabel.text")); // NOI18N
        sigmaLabel.setName("sigmaLabel"); // NOI18N

        sigma.setFont(new Font("Times New Roman", 1, 14)); // NOI18N
        sigma.setText(bundle.getString("sigma.text")); // NOI18N
        sigma.setName("sigma"); // NOI18N
        sigma.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                sigmaActionPerformed(evt);
            }
        });

        GroupLayout panelLayout = new GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(panelLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                    .addComponent(dbLabel, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(msLabel, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(dataSizeLabel, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                    .addGroup(panelLayout.createSequentialGroup()
                        .addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                            .addComponent(dataSetSize, GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
                            .addComponent(memorySize))
                        .addGap(18, 18, 18)
                        .addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(supportLabel)
                            .addComponent(sigmaLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                            .addComponent(support, GroupLayout.DEFAULT_SIZE, 46, Short.MAX_VALUE)
                            .addComponent(sigma)))
                    .addComponent(dbName))
                .addGap(0, 22, Short.MAX_VALUE))
        );
        panelLayout.setVerticalGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(panelLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(dbName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(dbLabel))
                .addGap(18, 18, 18)
                .addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(panelLayout.createSequentialGroup()
                        .addComponent(dataSizeLabel)
                        .addGap(12, 12, 12)
                        .addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(msLabel)
                            .addComponent(memorySize, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(supportLabel)
                            .addComponent(support, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                    .addComponent(dataSetSize, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(sigma, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(sigmaLabel, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(48, Short.MAX_VALUE))
        );

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(okButton)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cancelButton)
                .addGap(11, 11, 11))
            .addGroup(layout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addComponent(panel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(78, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(panel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(okButton)
                    .addComponent(cancelButton))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
  // @formatter:on

  @SuppressWarnings("UnusedParameters")
  private void okButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
    String db = dbName.getText();
    int k = Integer.parseInt(dataSetSize.getText());
    String a = support.getText();
    int b = Integer.parseInt(memorySize.getText());
    int s = Integer.parseInt(sigma.getText());
    p = new DataParams(db, k, a, b, s);

    try {
      if ("".equals(db)) {
        throw new Exception("Database Name is empty!");
      }

    } catch (Exception ep) {
      JOptionPane.showMessageDialog(this, ep.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }

    doClose(true);
  }//GEN-LAST:event_okButtonActionPerformed

  @SuppressWarnings("UnusedParameters")
  private void cancelButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
    doClose(false);
  }//GEN-LAST:event_cancelButtonActionPerformed

    private void sigmaActionPerformed(ActionEvent evt) {//GEN-FIRST:event_sigmaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_sigmaActionPerformed

    private void supportActionPerformed(ActionEvent evt) {//GEN-FIRST:event_supportActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_supportActionPerformed

    private void dataSetSizeActionPerformed(ActionEvent evt) {//GEN-FIRST:event_dataSetSizeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_dataSetSizeActionPerformed

  private void doClose(boolean retStatus) {
    returnStatus = retStatus;
    setVisible(false);
    dispose();
  }

  public DataParams getParameters() {
    return p;
  }
}
