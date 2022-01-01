package gblend.widget;

import org.netbeans.api.visual.border.Border;
import org.netbeans.api.visual.border.BorderFactory;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.Scene;

import java.awt.Color;

class NodeWidget extends LabelWidget {

  private static final Border BORDER_4 = BorderFactory.createLineBorder(4);
  private static final Color DEFAULT_NODE_COLOR = new Color(170, 226, 255);

  private final int id;

  public NodeWidget(Scene scene, String label, int id) {
    super(scene, label);
    this.id = id;
    setBorder(BORDER_4);
    setBackground(DEFAULT_NODE_COLOR);
    setOpaque(true);
  }

  public void setDefaultColor() {
    setBackground(DEFAULT_NODE_COLOR);
  }

  public int getId() {
    return id;
  }
}
