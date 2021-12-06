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
import me.sizableshrimp.adventofcode2021.helper.LineConvert;
import me.sizableshrimp.adventofcode2021.templates.Day;

import java.util.stream.LongStream;

public class Day06 extends Day {
    private IntList startingFish;

    public static void main(String[] args) {
        new Day06().run();
    }

    @Override
    protected Result evaluate() {
        long[] stages = new long[9];
        for (int age : startingFish) {
            stages[age]++;
        }
        long part1 = 0;
        for (int day = 1; day <= 256; day++) {
            long[] newStages = new long[9];
            for (int i = 0; i < 9; i++) {
                if (i == 0) {
                    newStages[6] += stages[0];
                    newStages[8] += stages[0];
                } else {
                    newStages[i - 1] += stages[i];
                }
            }
            stages = newStages;
            if (day == 80)
                part1 = sum(stages);
        }
        return Result.of(part1, sum(stages));
    }

    private long sum(long[] stages) {
        long sum = 0;
        for (long stage : stages) {
            sum += stage;
        }
        return sum;
    }

    @Override
    protected void parse() {
        String[] split = lines.get(0).split(",");
        startingFish = new IntArrayList();
        for (String n : split) {
            startingFish.add(Integer.parseInt(n));
        }
    }
}
