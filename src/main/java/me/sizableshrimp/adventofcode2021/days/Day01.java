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

import it.unimi.dsi.fastutil.ints.IntList;
import me.sizableshrimp.adventofcode2021.helper.ListConvert;
import me.sizableshrimp.adventofcode2021.templates.SeparatedDay;

public class Day01 extends SeparatedDay {
    private IntList depths;

    public static void main(String[] args) {
        new Day01().run();
    }

    @Override
    protected Object part1() {
        int increased = 0;

        for (int i = 1; i < depths.size(); i++) {
            if (depths.getInt(i) > depths.getInt(i - 1))
                increased++;
        }

        return increased;
    }

    @Override
    protected Object part2() {
        int increased = 0;
        int prev = -1;

        for (int i = 0; i < depths.size() - 2; i++) {
            int depth = depths.getInt(i) + depths.getInt(i + 1) + depths.getInt(i + 2);
            if (depth > prev && prev != -1)
                increased++;
            prev = depth;
        }

        return increased;
    }

    @Override
    protected void parse() {
        depths = ListConvert.ints(lines);
    }
}
