package gblend.pattern;

import org.jdesktop.application.Task;

import java.io.IOException;

import gblend.GBlendView;
import prague.index.PragueIndex;
import prague.pattern.PatternGenerator;
import prague.pattern.PatternSetInfo;

/**
 * @author Andy
 */
public class PatternGenerateTask extends Task<Object, Void> {

  private final PatternGenerator patternGenerator;
  private final GBlendView gBlendView;

  public PatternGenerateTask(GBlendView gBlendView, PatternSetInfo patternSetInfo,
                             PragueIndex pi) throws IOException {
    super(gBlendView.getApplication());

    this.gBlendView = gBlendView;
    patternGenerator = new PatternGenerator(patternSetInfo, pi);
  }

  @Override
  protected Object doInBackground() throws Exception {
    setMessage("Generating Patterns:");
    patternGenerator.execute();

    // Prepare the patterns to put onto the GUI
    gBlendView.drawPatternSizeButtons();

    return null;
  }

}
