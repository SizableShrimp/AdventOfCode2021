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

import me.sizableshrimp.adventofcode2021.helper.GridHelper;
import me.sizableshrimp.adventofcode2021.templates.Coordinate;
import me.sizableshrimp.adventofcode2021.templates.Day;

import java.util.BitSet;

// https://adventofcode.com/2021/day/20 - Trench Map
public class Day20 extends Day {
    private boolean[][] startingGrid;
    private BitSet algorithm;
    private boolean defaultSwaps;

    public static void main(String[] args) {
        new Day20().run();
    }

    @Override
    protected Result evaluate() {
        int part1 = 0;
        boolean[][] grid = startingGrid;

        for (int i = 0; i < 50; i++) {
            grid = run(grid, defaultSwaps && i % 2 == 1);
            if (i == 1)
                part1 = GridHelper.countOccurrences(grid, true);
        }

        return Result.of(part1, GridHelper.countOccurrences(grid, true));
    }

    private boolean[][] run(boolean[][] input, boolean defaultVal) {
        int expansion = 1;
        boolean[][] output = new boolean[input.length + (2 * expansion)][input[0].length + (2 * expansion)];

        for (int y = 0; y < output.length; y++) {
            for (int x = 0; x < output[0].length; x++) {
                Coordinate coord = Coordinate.of(x - expansion, y - expansion);
                int index = 0;

                for (int dy = -1; dy <= 1; dy++) {
                    for (int dx = -1; dx <= 1; dx++) {
                        Coordinate neighbor = coord.resolve(dx, dy);
                        boolean val = defaultVal;
                        if (GridHelper.isValid(input, neighbor)) {
                            val = input[neighbor.y][neighbor.x];
                        }
                        index = index << 1 | (val ? 1 : 0);
                    }
                }

                output[y][x] = algorithm.get(index);
            }
        }

        return output;
    }

    @Override
    protected void parse() {
        String algorithmStr = lines.get(0);
        algorithm = new BitSet(512);
        for (int i = 0; i < algorithmStr.length(); i++) {
            if (algorithmStr.charAt(i) == '#')
                algorithm.set(i);
        }
        defaultSwaps = algorithm.get(0);
        startingGrid = GridHelper.convertBool(lines.subList(2, lines.size()), c -> c == '#');
    }
}
