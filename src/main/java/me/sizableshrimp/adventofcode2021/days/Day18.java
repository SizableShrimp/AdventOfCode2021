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

import me.sizableshrimp.adventofcode2021.helper.Itertools;
import me.sizableshrimp.adventofcode2021.helper.MatchWrapper;
import me.sizableshrimp.adventofcode2021.templates.SeparatedDay;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day18 extends SeparatedDay {
    private static final Pattern REGULAR_PAIR_PATTERN = Pattern.compile("\\[(\\d+),(\\d+)]");
    private static final Pattern NUMBER_PATTERN = Pattern.compile("\\d+");

    public static void main(String[] args) {
        new Day18().run();
    }

    @Override
    protected Object part1() {
        StringBuilder cur = new StringBuilder(lines.get(0));

        for (int i = 1; i < lines.size(); i++) {
            cur.insert(0, '[').append(',').append(lines.get(i)).append(']');
            reduce(cur);
        }

        return getMagnitude(cur);
    }

    @Override
    protected Object part2() {
        int max = 0;

        for (List<String> combo : Itertools.combinations(lines, 2)) {
            String first = combo.get(0);
            String second = combo.get(1);
            int magnitude = getPairMagnitude(first, second);
            if (magnitude > max)
                max = magnitude;
            magnitude = getPairMagnitude(second, first);
            if (magnitude > max)
                max = magnitude;
        }

        return max;
    }

    private int getPairMagnitude(String first, String second) {
        StringBuilder cur = new StringBuilder(first).insert(0, '[').append(',').append(second).append(']');
        reduce(cur);
        int magnitude = getMagnitude(cur);
        return magnitude;
    }

    private void reduce(StringBuilder cur) {
        boolean changed;
        do {
            changed = false;
            int nestIdx = getNestIndex(cur);
            if (nestIdx != -1) {
                Matcher matcher = REGULAR_PAIR_PATTERN.matcher(cur);
                if (!matcher.find(nestIdx))
                    throw new IllegalStateException();
                MatchWrapper result = new MatchWrapper(matcher);
                int left = result.groupInt(1);
                int right = result.groupInt(2);
                cur.replace(result.start(), result.end(), "0");
                int lefterIdx = getLefterIndex(cur, result.start() - 1);
                int righterIdx = getRighterIndex(cur, result.start() + 1);
                if (lefterIdx != -1)
                    addNumber(cur, lefterIdx, left);
                if (righterIdx != -1)
                    addNumber(cur, righterIdx, right);
                changed = true;
                continue;
            }
            Matcher matcher = NUMBER_PATTERN.matcher(cur);
            MatchWrapper result = new MatchWrapper(matcher);
            while (matcher.find()) {
                int value = result.groupInt(0);
                if (value >= 10) {
                    int down = value / 2;
                    int up = value % 2 == 0 ? down : (value / 2 + 1);
                    cur.replace(result.start(), result.end(), "[" + down + "," + up + "]");
                    changed = true;
                    break;
                }
            }
        } while (changed);
    }

    private int getMagnitude(StringBuilder cur) {
        return getMagnitude(cur, 0, cur.length());
    }

    private int getMagnitude(StringBuilder cur, int start, int end) {
        int value = getNumber(cur.substring(start, end));
        if (value != -1)
            return value;
        int comma = getCommaIndex(cur, start, end);
        return 3 * getMagnitude(cur, start + 1, comma) + 2 * getMagnitude(cur, comma + 1, end - 1);
    }

    private int getNumber(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void addNumber(StringBuilder cur, int index, int add) {
        Matcher matcher = NUMBER_PATTERN.matcher(cur);
        if (!matcher.find(index))
            throw new IllegalStateException();
        MatchWrapper result = new MatchWrapper(matcher);
        int currentValue = result.groupInt(0);
        cur.replace(result.start(), result.end(), Integer.toString(currentValue + add));
    }

    private int getLefterIndex(StringBuilder cur, int start) {
        for (int i = start; i > 0; i--) {
            char c = cur.charAt(i);
            boolean wasDigit = Character.isDigit(c);
            while (Character.isDigit(c)) {
                i--;
                c = cur.charAt(i);
            }
            if (wasDigit)
                return i + 1;
        }

        return -1;
    }

    private int getRighterIndex(StringBuilder cur, int start) {
        for (int i = start; i < cur.length(); i++) {
            char c = cur.charAt(i);
            if (Character.isDigit(c))
                return i;
        }

        return -1;
    }

    private int getNestIndex(StringBuilder cur) {
        return getIndex(cur, '[', 0, cur.length(), 5);
    }

    private int getCommaIndex(StringBuilder cur, int start, int end) {
        return getIndex(cur, ',', start, end, 1);
    }

    private int getIndex(StringBuilder cur, char targetChar, int start, int end, int target) {
        int depth = 0;

        for (int i = start; i < end; i++) {
            char c = cur.charAt(i);
            switch (c) {
                case '[' -> depth++;
                case ']' -> depth--;
            }
            if (c == targetChar && depth == target)
                return i;
        }

        return -1;
    }

    @Override
    protected void parse() {
        super.parse();
    }
}
