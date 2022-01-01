package gblend.pattern;

import org.jdesktop.application.Task;

import java.io.IOException;

import gblend.GBlendView;
import prague.index.PragueIndex;
import prague.pattern.TEDPatternGenerator;
import prague.pattern.PatternSetInfo;

/**
 * @author Andy
 */
public class TEDPatternGenerateTask extends Task<Object, Void> {

  private final TEDPatternGenerator tedpatternGenerator;
  private final GBlendView gBlendView;

  public TEDPatternGenerateTask(GBlendView gBlendView, PatternSetInfo patternSetInfo,
                             PragueIndex pi) throws IOException {
    super(gBlendView.getApplication());

    this.gBlendView = gBlendView;
    tedpatternGenerator = new TEDPatternGenerator(patternSetInfo, pi);
  }

  @Override
  protected Object doInBackground() throws Exception {
    setMessage("Generating Patterns:");
    tedpatternGenerator.execute();

    // Prepare the patterns to put onto the GUI
    gBlendView.drawPatternSizeButtons();

    return null;
  }

}
