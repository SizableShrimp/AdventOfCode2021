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

public class Day02 extends SeparatedDay {
    private List<Command> commands;

    public static void main(String[] args) {
        new Day02().run();
    }

    @Override
    protected Object part1() {
        int depth = 0;
        int horiz = 0;
        for (Command command : commands) {
            switch (command.type) {
                case "forward" -> horiz += command.count;
                case "down" -> depth += command.count;
                case "up" -> depth -= command.count;
                default -> throw new IllegalArgumentException();
            }
        }
        return depth * horiz;
    }

    @Override
    protected Object part2() {
        int aim = 0;
        int depth = 0;
        int horiz = 0;
        for (Command command : commands) {
            switch (command.type) {
                case "forward" -> {
                    horiz += command.count;
                    depth += aim * command.count;
                }
                case "down" -> aim += command.count;
                case "up" -> aim -= command.count;
                default -> throw new IllegalArgumentException();
            }
        }
        return depth * horiz;
    }

    @Override
    protected void parse() {
        commands = new ArrayList<>(lines.size());
        for (String line : lines) {
            String[] split = line.split(" ");
            commands.add(new Command(split[0], Integer.parseInt(split[1])));
        }
    }

    private record Command(String type, int count) {}
}
