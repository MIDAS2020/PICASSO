/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.claribole.zgrviewer;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import prague.result.ChooseResultsInterface;
import gblend.GBlendView;
import java.awt.Color;
import java.util.Map;
import org.netbeans.api.visual.widget.ConnectionWidget;
import prague.query.QueryEdge;

/**
 *
 * @author kaihuang
 */
public class ZGRViewerList {

    private ExploreUIControl uicontrol;
    private TEDExploreUIControl teduicontrol;
    private int reRuntime=1;
    public ZGRViewerList() {
        uicontrol = new ExploreUIControl();
        teduicontrol = new TEDExploreUIControl();
    }
    public void setZGRViewerList(GBlendView gblendview, ChooseResultsInterface crs, int runtimes, JPanel drawpane, boolean isSimilar) {
        if(uicontrol.getReRuntime() == 100000) {
            reRuntime = runtimes;
            runtimes = 1;
             uicontrol = new ExploreUIControl();
             uicontrol.addControlPanel(gblendview, crs, runtimes, drawpane, isSimilar);
        }else
        {
            runtimes = runtimes - reRuntime + 1;
            
            uicontrol.addControlPanel(gblendview, crs, runtimes, drawpane, isSimilar);
        }
    }
    public void setZGRViewerListTED(GBlendView gblendview, ChooseResultsInterface crs, int runtimes, JPanel drawpane,boolean isSimilar) {
        
        if(teduicontrol.getReRuntime() == 100000) {
            reRuntime = runtimes;
            runtimes = 1;
            teduicontrol = new TEDExploreUIControl();
            teduicontrol.addControlPanel(gblendview, crs, runtimes, isSimilar);
        }
        
        else
        {
            runtimes = runtimes - reRuntime + 1;
            
            teduicontrol.addControlPanel(gblendview, crs, runtimes, isSimilar);
            
            
            
        }

    }
}
