package net.zomis.combinatorics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import net.zomis.minesweeper.analyze.AnalyzeFactory;
import net.zomis.minesweeper.analyze.BoundedFieldRule;
import net.zomis.minesweeper.analyze.FieldRule;

public class Binero {
	static void readLine(AnalyzeFactory<Integer> puzzle, int y, String line) {
		for (int x = 0; x < line.length(); x++) {
			char ch = line.charAt(x);
			if (ch == '1') {
				puzzle.addRule(IntegerPoints.positionValue(x, y, 1, line.length()));
			}
			else if (ch == '0') {
				puzzle.addRule(IntegerPoints.positionValue(x, y, 0, line.length()));
			}
		}
	}

	static int readFromFile(InputStream file, AnalyzeFactory<Integer> analyze) throws IOException {
		BufferedReader bis = new BufferedReader(new InputStreamReader(file));
		String line = bis.readLine();
		int size = line.length();
		readLine(analyze, 0, line);
		for (int y = 1; y < size; y++) {
			line = bis.readLine();
			readLine(analyze, y, line);
		}
		bis.close();
		return size;
	}
	
	public static AnalyzeFactory<Integer> binero(InputStream file, AtomicInteger size) throws IOException {
		AnalyzeFactory<Integer> fact = new AnalyzeFactory<Integer>();
		int length = readFromFile(file, fact);
		setupBinero(fact, length);
		size.set(length);
		return fact;
	}
	
	private static void setupBinero(AnalyzeFactory<Integer> fact, int size) {
		List<List<Integer>> cols = new ArrayList<List<Integer>>();
		List<List<Integer>> rows = new ArrayList<List<Integer>>();
		for (int x = 0; x < size; x++) {
			fact.addRule(new FieldRule<Integer>(null, IntegerPoints.createLine(0, x, size, 1, 0), size / 2));
			fact.addRule(new FieldRule<Integer>(null, IntegerPoints.createLine(x, 0, size, 0, 1), size / 2));
			cols.add(IntegerPoints.createLine(x, 0, size, 0, 1));
			rows.add(IntegerPoints.createLine(0, x, size, 1, 0));
			
			sliding(fact, 0, x, size, 1, 0, 3);
			sliding(fact, x, 0, size, 0, 1, 3);
		}
		fact.addRule(new UniqueSequence<Integer>(0, cols));
		fact.addRule(new UniqueSequence<Integer>(0, rows));
	}

	private static void sliding(AnalyzeFactory<Integer> puzzle, int x, int y, int size, int offsetX, int offsetY, int count) {
		LinkedList<Integer> fields = new LinkedList<Integer>();
		while (x < size && y < size && x >= 0 && y >= 0) {
			fields.addLast(IntegerPoints.pos(x, y, size));
			x += offsetX;
			y += offsetY;
			if (fields.size() >= 3) {
				puzzle.addRule(new BoundedFieldRule<Integer>(null, new ArrayList<Integer>(fields), 1, 2));
				fields.removeFirst();
			}
		}
	}

}
