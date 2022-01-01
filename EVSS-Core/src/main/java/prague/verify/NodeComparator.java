/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prague.verify;

import java.util.Comparator;
import prague.graph.Vertex;
/**
 *
 * @author kaihuang
 */
public class NodeComparator implements Comparator<Vertex> {

    public int compare(Vertex o1, Vertex o2) {
        if (o1.getLabel() > o2.getLabel()
                || (o1.getLabel()==o2.getLabel() && o1.getDegree() > o2.getDegree())) {
            return -1;
        } else if (o1.getLabel()== o2.getLabel() && o1.getDegree() == o2.getDegree()) {
            return 0;
        } else {
            return 1;
        }
    }
}