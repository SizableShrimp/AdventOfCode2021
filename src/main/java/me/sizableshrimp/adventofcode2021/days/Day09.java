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
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import me.sizableshrimp.adventofcode2021.helper.GridHelper;
import me.sizableshrimp.adventofcode2021.templates.Coordinate;
import me.sizableshrimp.adventofcode2021.templates.Day;
import me.sizableshrimp.adventofcode2021.templates.Direction;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// https://adventofcode.com/2021/day/9 - Smoke Basin
public class Day09 extends Day {
    private int[][] grid;

    public static void main(String[] args) {
        new Day09().run();
    }

    @Override
    protected Result evaluate() {
        int part1 = 0;
        List<Coordinate> lowPoints = new ArrayList<>();

        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[0].length; x++) {
                Coordinate coord = Coordinate.of(x, y);
                if (isLowPoint(coord)) {
                    lowPoints.add(coord);
                    part1 += grid[coord.y][coord.x] + 1;
                }
            }
        }

        IntList basins = new IntArrayList();
        Deque<Coordinate> queue = new ArrayDeque<>();
        Set<Coordinate> seen = new HashSet<>();

        for (Coordinate lowPoint : lowPoints) {
            queue.clear();
            queue.add(lowPoint);
            seen.clear();
            int size = 0;
            while (!queue.isEmpty()) {
                Coordinate coord = queue.removeFirst();
                size++;
                int cur = grid[coord.y][coord.x];

                for (Direction dir : Direction.cardinalDirections()) {
                    Coordinate neighbor = coord.resolve(dir);
                    if (GridHelper.isValid(grid, neighbor)) {
                        int neighborHeight = grid[neighbor.y][neighbor.x];
                        if (neighborHeight != 9 && neighborHeight >= cur) {
                            if (seen.add(neighbor))
                                queue.add(neighbor);
                        }
                    }
                }
            }
            basins.add(size);
        }

        basins.sort(IntComparators.OPPOSITE_COMPARATOR);
        return Result.of(part1, basins.getInt(0) * basins.getInt(1) * basins.getInt(2));
    }

    private boolean isLowPoint(Coordinate coord) {
        int cur = grid[coord.y][coord.x];
        boolean valid = true;
        for (Direction dir : Direction.cardinalDirections()) {
            Coordinate neighbor = coord.resolve(dir);
            if (GridHelper.isValid(grid, neighbor) && grid[neighbor.y][neighbor.x] <= cur) {
                valid = false;
                break;
            }
        }
        return valid;
    }

    @Override
    protected void parse() {
        grid = GridHelper.convertInt(lines, c -> c - '0');
    }
}
