package pratofiorito.renderer.swing;

import pratofiorito.PratoFiorito;
import pratofiorito.utils.GameConstants;

public class CustomDifficultyDialog extends javax.swing.JDialog implements GameConstants {
    private final PratoFiorito game;
    
    public CustomDifficultyDialog(PratoFiorito game, 
                                  SwingRenderManager parent) {
        super(parent, true);
        
        this.game = game;
        
        initComponents();
        
        getRootPane().setDefaultButton(okBtn);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        optionsPanel = new javax.swing.JPanel();
        rowsLabel = new javax.swing.JLabel();
        rowsTxtField = new javax.swing.JTextField();
        colsLabel = new javax.swing.JLabel();
        colsTxtField = new javax.swing.JTextField();
        minesLabel = new javax.swing.JLabel();
        minesTxtField = new javax.swing.JTextField();
        buttonsPanel = new javax.swing.JPanel();
        okBtn = new javax.swing.JButton();
        cancelBtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Custom field");
        setResizable(false);
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.LINE_AXIS));

        optionsPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(30, 15, 30, 15));
        optionsPanel.setLayout(new java.awt.GridLayout(3, 2, 0, 4));

        rowsLabel.setText("Height:");
        optionsPanel.add(rowsLabel);

        rowsTxtField.setColumns(5);
        optionsPanel.add(rowsTxtField);

        colsLabel.setText("Width:");
        optionsPanel.add(colsLabel);

        colsTxtField.setColumns(5);
        optionsPanel.add(colsTxtField);

        minesLabel.setText("Mines:");
        optionsPanel.add(minesLabel);

        minesTxtField.setColumns(5);
        optionsPanel.add(minesTxtField);

        getContentPane().add(optionsPanel);

        buttonsPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(30, 15, 30, 15));
        buttonsPanel.setLayout(new java.awt.GridLayout(2, 1));

        okBtn.setText("Ok");
        okBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okBtnActionPerformed(evt);
            }
        });
        buttonsPanel.add(okBtn);

        cancelBtn.setText("Cancel");
        cancelBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelBtnActionPerformed(evt);
            }
        });
        buttonsPanel.add(cancelBtn);

        getContentPane().add(buttonsPanel);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancelBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelBtnActionPerformed
        setVisible(false);
    }//GEN-LAST:event_cancelBtnActionPerformed

    private void okBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okBtnActionPerformed
        int rows;
        try {
            rows = Math.max(BEGINNER_ROWS, Integer.parseInt(rowsTxtField.getText()));
        } catch(Exception e) {
            rows = BEGINNER_ROWS;
        }
        
        int cols;
        try {
            cols = Math.max(BEGINNER_COLS, Integer.parseInt(colsTxtField.getText()));
        } catch(Exception e) {
            cols = BEGINNER_COLS;
        }
        
        int bombs;
        try {
            final int parsedBombs = Integer.parseInt(minesTxtField.getText());
            final int maxBombs = (rows - 1) * (cols - 1);
            bombs = Math.max(BEGINNER_BOMBS, Math.min(maxBombs, parsedBombs));
        } catch(Exception e) {
            bombs = BEGINNER_BOMBS;
        }
        
        game.loadNewBoard(rows, cols, bombs);
        setVisible(false);
    }//GEN-LAST:event_okBtnActionPerformed

    @Override
    public void setVisible(boolean isVisible) {
        if(isVisible) {
            rowsTxtField.setText(String.valueOf(game.getBoard().getRows()));
            colsTxtField.setText(String.valueOf(game.getBoard().getCols()));
            minesTxtField.setText(String.valueOf(game.getBoard().getBombs()));
        }
        super.setVisible(isVisible);
    }
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buttonsPanel;
    private javax.swing.JButton cancelBtn;
    private javax.swing.JLabel colsLabel;
    private javax.swing.JTextField colsTxtField;
    private javax.swing.JLabel minesLabel;
    private javax.swing.JTextField minesTxtField;
    private javax.swing.JButton okBtn;
    private javax.swing.JPanel optionsPanel;
    private javax.swing.JLabel rowsLabel;
    private javax.swing.JTextField rowsTxtField;
    // End of variables declaration//GEN-END:variables
}
