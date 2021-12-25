/*
 * AdventOfCode2021
 * Copyright (C) 2021 SizableShrimp
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.sizableshrimp.adventofcode2021.days;

import it.unimi.dsi.fastutil.chars.CharOpenHashSet;
import it.unimi.dsi.fastutil.chars.CharSet;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntMaps;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import me.sizableshrimp.adventofcode2021.helper.Processor;
import me.sizableshrimp.adventofcode2021.templates.Day;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// https://adventofcode.com/2021/day/8 - Seven Segment Search
public class Day08 extends Day {
    // The product of the overlaps for a given digit and 1, 4, and 8 make for a unique identifier that can be pre-calculated
    // Not my idea; credit goes to /u/4HbQ
    private static final Int2IntMap PRODUCTS_MAP = Int2IntMaps.unmodifiable(new Int2IntOpenHashMap(Map.of(
            36, 0,
            8, 1,
            10, 2,
            30, 3,
            32, 4,
            15, 5,
            18, 6,
            12, 7,
            56, 8,
            48, 9)));
    private List<Display> displays;

    public static void main(String[] args) {
        new Day08().run();
    }

    @Override
    protected Result evaluate() {
        int part1 = 0;
        int sum = 0;

        for (Display display : displays) {
            CharSet one = null;
            CharSet four = null;
            CharSet eight = null;

            for (String pattern : display.patterns) {
                switch (pattern.length()) {
                    case 2 -> one = charSet(pattern);
                    case 4 -> four = charSet(pattern);
                    case 7 -> eight = charSet(pattern);
                }
            }

            int code = 0;

            for (String digitStr : display.output) {
                CharSet chars = charSet(digitStr);
                int product = Processor.intersection(one, chars).size() * Processor.intersection(four, chars).size() * Processor.intersection(eight, chars).size();
                int digit = PRODUCTS_MAP.get(product);
                switch (digit) {
                    case 1, 4, 7, 8 -> part1++;
                }
                code = 10 * code + digit;
            }

            sum += code;
        }

        return Result.of(part1, sum);
    }

    private CharSet charSet(String s) {
        return CharOpenHashSet.of(s.toCharArray());
    }

    @Override
    protected void parse() {
        displays = new ArrayList<>();

        for (String line : lines) {
            String[] halves = line.split(" \\| ");
            String[] patterns = halves[0].split(" ");
            String[] output = halves[1].split(" ");

            displays.add(new Display(patterns, output));
        }
    }

    private record Display(String[] patterns, String[] output) {}
}
