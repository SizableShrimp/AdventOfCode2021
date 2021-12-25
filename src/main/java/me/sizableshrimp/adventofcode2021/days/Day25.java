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
import me.sizableshrimp.adventofcode2021.templates.Direction;
import me.sizableshrimp.adventofcode2021.templates.EnumState;
import me.sizableshrimp.adventofcode2021.templates.SeparatedDay;

// https://adventofcode.com/2021/day/25 - Sea Cucumber
public class Day25 extends SeparatedDay {
    private State[][] startingGrid;

    public static void main(String[] args) {
        new Day25().run();
    }

    @Override
    protected Object part1() {
        int steps = 0;
        State[][] grid = this.startingGrid;

        boolean changed;
        do {
            changed = false;
            State[][] next = GridHelper.copyFast(State[][]::new, grid);

            if (moveAll(grid, next, Direction.EAST, State.RIGHT))
                changed = true;

            // Don't let south-bound sea cucumbers overwrite east-bound sea cucumbers accidentally!
            grid = GridHelper.copy(next);

            if (moveAll(grid, next, Direction.SOUTH, State.DOWN))
                changed = true;

            grid = next;
            steps++;
        } while (changed);

        return steps;
    }

    private boolean moveAll(State[][] grid, State[][] next, Direction dir, State state) {
        boolean changed = false;

        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[0].length; x++) {
                if (grid[y][x] == state && move(grid, next, x, y, dir, state))
                    changed = true;
            }
        }

        return changed;
    }

    private boolean move(State[][] grid, State[][] next, int x, int y, Direction dir, State state) {
        int targetX = (x + dir.x) % grid[0].length;
        int targetY = (y + dir.y) % grid.length;

        if (grid[targetY][targetX] == State.EMPTY) {
            next[y][x] = State.EMPTY;
            next[targetY][targetX] = state;
            return true;
        }

        return false;
    }

    @Override
    protected Object part2() {
        // There is no part 2 :)
        return null;
    }

    @Override
    protected void parse() {
        startingGrid = GridHelper.convert((y, x) -> new State[y][x], lines);
    }

    private enum State implements EnumState<State> {
        EMPTY('.'),
        DOWN('v'),
        RIGHT('>');

        private final char c;

        State(char c) {
            this.c = c;
        }

        @Override
        public char getMappedChar() {
            return c;
        }
    }
}
