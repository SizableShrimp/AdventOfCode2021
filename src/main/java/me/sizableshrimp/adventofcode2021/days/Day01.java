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

// https://adventofcode.com/2021/day/1 - Sonar Sweep
public class Day01 extends SeparatedDay {
    private IntList depths;

    public static void main(String[] args) {
        new Day01().run();
    }

    @Override
    protected Object part1() {
        // return IntStream.range(0, depths.size() - 1).filter(i -> depths.getInt(i + 1) > depths.getInt(i)).count();
        int increased = 0;

        for (int i = 0; i < depths.size() - 1; i++) {
            if (depths.getInt(i + 1) > depths.getInt(i))
                increased++;
        }

        return increased;
    }

    @Override
    protected Object part2() {
        // return IntStream.range(0, depths.size()).filter(i -> i < depths.size() - 3 && depths.getInt(i + 3) > depths.getInt(i)).count();
        int increased = 0;

        for (int i = 0; i < depths.size() - 3; i++) {
            // a + b + c < b + c + d is equivalent to a < d or d(i) < d(i + 3)
            if (depths.getInt(i + 3) > depths.getInt(i))
                increased++;
        }

        return increased;
    }

    @Override
    protected void parse() {
        depths = ListConvert.ints(lines);
    }
}
