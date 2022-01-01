package gblend.db;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * @author zhouyong
 */
class DbComboBoxRenderer extends JLabel implements ListCellRenderer<String> {

  private static final Font FONT = new Font("Tahoma", Font.BOLD, 12);

  public DbComboBoxRenderer() {
    setOpaque(true);
    setIconTextGap(10);
    setHorizontalAlignment(LEADING);
    setVerticalAlignment(CENTER);
  }

  @Override
  public Component getListCellRendererComponent(JList<? extends String> list, String value,
                                                int index, boolean isSelected,
                                                boolean cellHasFocus) {
    if (isSelected) {
      setBackground(list.getSelectionBackground());
      setForeground(list.getSelectionForeground());
    } else {
      setBackground(list.getBackground());
      setForeground(list.getForeground());
    }

    if (value != null) {
      setText(value.trim());
    }
    setFont(FONT);
    return this;
  }

}
