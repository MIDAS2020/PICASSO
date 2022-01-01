/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.claribole.zgrviewer;

import java.util.HashMap;
import java.util.Vector;

/**
 *
 * @author kaihuang
 */
public class ViewManager {
    private int numPerRow = 1;
    private HashMap<String, Object[][]> vTables = new HashMap<String, Object[][]>();
    public ViewManager(){
        
    }
    public  ViewTable getTable(Vector<ExploreUI.ViewResult> reslut) {
        Object[] columnNames = new Object[numPerRow];
        for (int i = 0; i < columnNames.length; i++) {
            columnNames[i] = "";
        }
        Object[][] data = new Object[reslut.size()/numPerRow][numPerRow];
        for(int i=0;i<reslut.size()/numPerRow;i++){
            for(int j=0;j<numPerRow;j++){
                ExploreUI.ViewResult r = (ExploreUI.ViewResult)reslut.get(i);
                data[i][j] = r._panelView;
            }
        }

        ViewTable table = new ViewTable(columnNames, data, 100);
        return table;
    }
}
