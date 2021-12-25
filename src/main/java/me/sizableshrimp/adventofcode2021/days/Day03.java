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

import me.sizableshrimp.adventofcode2021.templates.SeparatedDay;

import java.util.ArrayList;
import java.util.List;

// https://adventofcode.com/2021/day/3 - Binary Diagnostic
public class Day03 extends SeparatedDay {
    private int binaryLength;

    public static void main(String[] args) {
        new Day03().run();
    }

    @Override
    protected Object part1() {
        StringBuilder gamma = new StringBuilder();
        StringBuilder epsilon = new StringBuilder();

        for (int i = 0; i < binaryLength; i++) {
            int count = getCount(lines, i);
            gamma.append(count >= 0 ? '1' : '0');
            epsilon.append(count < 0 ? '1' : '0');
        }

        return Integer.parseInt(gamma.toString(), 2) * Integer.parseInt(epsilon.toString(), 2);
    }

    @Override
    protected Object part2() {
        return Integer.parseInt(getRating(true), 2) * Integer.parseInt(getRating(false), 2);
    }

    private String getRating(boolean greatest) {
        List<String> nums = new ArrayList<>(lines);

        for (int i = 0; i < binaryLength && nums.size() > 1; i++) {
            int count = getCount(nums, i);
            char target = greatest == count >= 0 ? '1' : '0';
            int finalI = i;
            nums.removeIf(s -> s.charAt(finalI) != target);
        }

        return nums.get(0);
    }

    /**
     * Positive - more 1s
     * Negative - more 0s
     * Zero - equal
     */
    private int getCount(List<String> nums, int index) {
        int count = 0;
        for (String n : nums) {
            count += n.charAt(index) == '1' ? 1 : -1;
        }
        return count;
    }

    @Override
    protected void parse() {
        binaryLength = lines.get(0).length();
    }
}
