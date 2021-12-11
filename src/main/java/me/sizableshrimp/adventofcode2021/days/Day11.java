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
import me.sizableshrimp.adventofcode2021.templates.Direction;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

public class Day11 extends Day {
    public static void main(String[] args) {
        new Day11().run();
    }

    @Override
    protected Result evaluate() {
        int[][] grid = GridHelper.convertInt(lines, c -> c - '0');
        int totalSquid = grid.length * grid[0].length;
        int part1 = 0;
        Set<Coordinate> seen = new HashSet<>();
        Deque<Coordinate> flashes = new ArrayDeque<>();

        for (int step = 1; ; step++) {
            seen.clear();
            flashes.clear();

            for (int y = 0; y < grid.length; y++) {
                for (int x = 0; x < grid[0].length; x++) {
                    // Increase energy and check if the new value is greater than 9
                    if (++grid[y][x] > 9) {
                        flashes.add(Coordinate.of(x, y));
                    }
                }
            }

            // Continue flashing until there is no more cascading
            while (!flashes.isEmpty()) {
                Coordinate coord = flashes.pop();
                if (!seen.add(coord))
                    continue;

                for (Direction dir : Direction.cardinalOrdinalDirections()) {
                    Coordinate neighbor = coord.resolve(dir);
                    if (GridHelper.isValid(grid, neighbor)) {
                        // Increase energy and check if the new value is greater than 9, then cascade
                        if (++grid[neighbor.y][neighbor.x] > 9) {
                            flashes.add(neighbor);
                        }
                    }
                }
            }

            // Set all the flashers back to 0
            for (Coordinate coord : seen) {
                grid[coord.y][coord.x] = 0;
            }

            if (step <= 100)
                part1 += seen.size();
            if (seen.size() == totalSquid) {
                return Result.of(part1, step);
            }
        }
    }
}
