/*
 * DatabaseInfo.java
 *
 * Created on September 5, 2007, 9:11 AM
 *
 * To change this template, choose Tools | Template Manager and open the template in the editor.
 */
package gblend.db;

import static com.google.common.base.Preconditions.*;

import com.google.common.base.Converter;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;

import java.io.Serializable;

/**
 * This is a container for the database information. A database engine is also
 * provided. Any database related task should be performed through the database
 * engine API.
 *
 * @author Colin
 */
public class DatabaseInfo implements Serializable {

	private static final long serialVersionUID = 10000L;

//	private static final String[] dbLabels = { "C", "O", "Cu", "N", "S", "P",
//			"Cl", "Zn", "B", "Br", "Co", "Mn", "As", "Al", "Ni", "Se", "Si",
//			"V", "Sn", "I", "F", "Li", "Sb", "Fe", "Pd", "Hg", "Bi", "Na",
//			"Ca", "Ti", "Ho", "Ge", "Pt", "Ru", "Rh", "Cr", "Ga", "K", "Ag",
//			"Au", "Tb", "Ir", "Te", "Mg", "Pb", "W", "Cs", "Mo", "Re", "Cd",
//			"Os", "Pr", "Nd", "Sm", "Gd", "Yb", "Er", "U", "Tl", "Ac" };

	private static final String[] dbLabels = { "C", "O", "Cu", "N", "S", "P",
			"Cl", "Zn", "B", "Br", "Co", "Mn", "As", "Al", "Ni", "Se", "Si",
			"V", "Sn", "I", "F", "Li", "Sb", "Fe", "Pd", "Hg", "Bi", "Na",
			"Ca", "Ti", "Ho", "Ge", "Pt", "Ru", "Rh", "Cr", "Ga", "K", "Ag",
			"Au", "Tb", "Ir", "Te", "Mg", "Pb", "W", "Cs", "Mo", "Re", "Cd",
			"Os", "Pr", "Nd", "Sm", "Gd", "Yb", "Er", "U", "Tl", "Ac", "H",
			"Sr", "Ba", "Nb", "Rb", "Hf", "In", "Ce", "Zr", "Eu", "Tm", "Dy",
			"Y", "La", "Lu", "Ta", "Be", "Th", "Sc" };

	private static Converter<String, Integer> labelConverter;

	private DatabaseInfo() {
	}

	public static String[] getLabels() {
		return dbLabels;
	}

	private static Converter<String, Integer> getConverter() {
		if (labelConverter != null) {
			return labelConverter;
		}
		BiMap<String, Integer> map = HashBiMap.create();
		String[] realLabels = DatabaseInfo.getLabels();
		for (int i = 0; i < realLabels.length; i++) {
			map.put(realLabels[i], i);
		}
		labelConverter = Maps.asConverter(map);
		return labelConverter;
	}

	public static int convert(String label) {
		Integer nodeLabel = getConverter().convert(label);
		checkState(nodeLabel != null, "unexpected");
		return nodeLabel;
	}
}
