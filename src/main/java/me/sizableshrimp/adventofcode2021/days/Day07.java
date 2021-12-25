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

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import me.sizableshrimp.adventofcode2021.templates.SeparatedDay;

// // https://adventofcode.com/2021/day/7 - The Treachery of Whales
public class Day07 extends SeparatedDay {
    private IntList positions;

    public static void main(String[] args) {
        new Day07().run();
    }

    @Override
    protected Object part1() {
        positions.sort(null);
        // Median in the sorted set is the most optimal.
        // If even size - either median will work, but using integer division floors, so this will always pick the lower median.
        return calculateFuel(positions, positions.getInt(positions.size() / 2), false);
    }

    @Override
    protected Object part2() {
        int avg = 0;
        for (int pos : positions) {
            avg += pos;
        }
        avg /= positions.size();
        int minFuel = Integer.MAX_VALUE;
        // Don't ask me why, but the mean is almost accurate here give or take 1! (something something skew)
        for (int target = avg - 1; target <= avg + 1; target++) {
            minFuel = Math.min(calculateFuel(positions, avg, true), minFuel);
        }
        return minFuel;
    }

    private int calculateFuel(IntList positions, int target, boolean triangular) {
        int fuel = 0;
        for (int pos : positions) {
            int diff = Math.abs(pos - target);
            // https://en.wikipedia.org/wiki/Triangular_number
            fuel += triangular ? diff * (diff + 1) / 2 : diff;
        }
        return fuel;
    }

    @Override
    protected void parse() {
        String[] split = lines.get(0).split(",");
        positions = new IntArrayList(split.length);
        for (String s : split) {
            positions.add(Integer.parseInt(s));
        }
    }
}
