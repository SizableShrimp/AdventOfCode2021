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
import me.sizableshrimp.adventofcode2021.templates.Direction;
import me.sizableshrimp.adventofcode2021.templates.SeparatedDay;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

// https://adventofcode.com/2021/day/15 - Chiton
public class Day15 extends SeparatedDay {
    private int[][] part1Grid;
    private int[][] part2Grid;

    public static void main(String[] args) {
        new Day15().run();
    }

    @Override
    protected Object part1() {
        return getMinimumRiskLevel(part1Grid);
    }

    @Override
    protected Object part2() {
        return getMinimumRiskLevel(part2Grid);
    }

    private int getMinimumRiskLevel(int[][] grid) {
        int height = grid[0].length;
        int width = grid.length;
        Coordinate target = Coordinate.of(width - 1, height - 1);

        // A* heuristic uses Manhattan distance to target
        Queue<Node> queue = new PriorityQueue<>(Comparator.comparingInt(n -> n.totalRisk + n.coord.distance(target)));
        queue.add(new Node(Coordinate.ORIGIN, 0));
        int[][] weights = new int[width][height];

        while (!queue.isEmpty()) {
            Node node = queue.poll();
            for (Direction dir : Direction.cardinalDirections()) {
                Coordinate neighbor = node.coord.resolve(dir);
                if (!neighbor.equals(Coordinate.ORIGIN) && GridHelper.isValid(grid, neighbor)) {
                    int newRiskLevel = node.totalRisk + grid[neighbor.y][neighbor.x];
                    int minDist = weights[neighbor.y][neighbor.x];
                    if (minDist == 0 || newRiskLevel < minDist) {
                        weights[neighbor.y][neighbor.x] = newRiskLevel;
                        Node newNode = new Node(neighbor, newRiskLevel);
                        if (!neighbor.equals(target)) {
                            queue.add(newNode);
                        }
                    }
                }
            }
        }

        return weights[target.y][target.x];
    }

    @Override
    protected void parse() {
        part1Grid = GridHelper.convertInt(lines, c -> c - '0');
        int height = part1Grid.length;
        int width = part1Grid[0].length;
        part2Grid = new int[height * 5][width * 5];

        for (int yTile = 0; yTile < 5; yTile++) {
            for (int xTile = 0; xTile < 5; xTile++) {
                int extra = Coordinate.of(xTile, yTile).distanceToOrigin();
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int value = part1Grid[y][x] + extra;
                        if (value > 9)
                            value = (value - 1) % 9 + 1;
                        part2Grid[yTile * height + y][xTile * width + x] = value;
                    }
                }
            }
        }
    }

    private record Node(Coordinate coord, int totalRisk) {}
}
