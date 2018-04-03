package test;

import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;

import com.google.common.base.Splitter;

public class A {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// List<String> sequences = Splitter.onPattern("[*]").trimResults().splitToList("250*1**263*abc*1*97975506");
		List<String> sequences = Splitter.onPattern("[*]").trimResults().omitEmptyStrings().splitToList("250*1**263*abc*1*97975506");
		System.out.println(sequences);
		for(String input : sequences) {
			System.out.println(input + "   " + input.length());
		}

		List<Integer> arbre = new LinkedList<Integer>();
		arbre.add(0);
		arbre.add(1);
		arbre.add(101);
		arbre.add(261);

		String chaine = arbre.toString();
		chaine = chaine.replace("[", "");
		chaine = chaine.replace("]", "");
		chaine = chaine.replace(", ", ".");
		System.out.println(chaine + "  " + chaine.length());

		StringJoiner tree = new StringJoiner(".", ".", "");
		// tree.add("");
		// tree.add("e");
		tree.setEmptyValue("");
		System.out.println(tree.toString() + " " + tree.toString().length());
	}

}
