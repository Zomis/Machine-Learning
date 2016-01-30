package net.zomis.combinatorics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import net.zomis.minesweeper.analyze.FieldGroup;
import net.zomis.minesweeper.analyze.FieldRule;
import net.zomis.minesweeper.analyze.GroupValues;

public class IntegerPoints {

	public static FieldRule<Integer> positionValue(int x, int y, int i, int size) {
		return new FieldRule<Integer>(pos(x, y, size), Arrays.asList(pos(x, y, size)), i);
	}

	public static List<Integer> createLine(int x, int y, int size, int offsetX, int offsetY) {
		List<Integer> fields = new ArrayList<Integer>();
		while (x < size && y < size && x >= 0 && y >= 0) {
			fields.add(pos(x, y, size));
			y += offsetY;
			x += offsetX;
		}
		return fields;
	}
	
	public static String map(GroupValues<Integer> values, int size) {
		char[][] fields = new char[size][size];
		for (Entry<FieldGroup<Integer>, Integer> group : values.entrySet()) {
			Integer pos = group.getKey().get(0);
			Integer value = group.getValue();
			char ch = ' ';
			if (value == 1) {
				ch = '1';
			}
			else if (value == 0) {
				ch = '0';
			}
			fields[pos % size][pos / size] = ch;
		}
		
		StringBuilder str = new StringBuilder();
		for (int y = 0; y < size; y++) {
			for (int x = 0; x < size; x++) {
				if (fields[x][y] == 0) {
					str.append(' ');
				}
				else str.append(fields[x][y]);
			}
			str.append("\n");
		}
		return str.toString();
	}
	
	public static Integer pos(int x, int y, int size) {
		return y * size + x;
	}
	
}
