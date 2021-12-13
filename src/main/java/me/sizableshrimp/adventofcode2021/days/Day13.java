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

import me.sizableshrimp.adventofcode2021.helper.LineConvert;
import me.sizableshrimp.adventofcode2021.helper.Printer;
import me.sizableshrimp.adventofcode2021.templates.Coordinate;
import me.sizableshrimp.adventofcode2021.templates.Day;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day13 extends Day {
    private Set<Coordinate> startingCoords;
    private List<Fold> folds;

    public static void main(String[] args) {
        new Day13().run();
    }

    @Override
    protected Result evaluate() {
        int part1 = 0;
        Set<Coordinate> coords = new HashSet<>(startingCoords);

        for (int i = 0; i < folds.size(); i++) {
            Fold fold = folds.get(i);
            Set<Coordinate> newCoords = new HashSet<>();

            for (Coordinate coord : coords) {
                if (fold.horizontal && coord.x < fold.line || !fold.horizontal && coord.y < fold.line) {
                    // Not in the fold
                    newCoords.add(coord);
                } else if (fold.horizontal) {
                    // Fold on x line
                    newCoords.add(Coordinate.of(2 * fold.line - coord.x, coord.y));
                } else {
                    // Fold on y line
                    newCoords.add(Coordinate.of(coord.x, 2 * fold.line - coord.y));
                }
            }

            coords = newCoords;
            if (i == 0)
                part1 = coords.size();
        }

        return Result.of(part1, "\n" + Printer.toString(coords, (contains, coord) -> contains ? "##" : ".."));
    }

    @Override
    protected void parse() {
        startingCoords = new HashSet<>();
        folds = new ArrayList<>();

        boolean instructions = false;
        for (String line : lines) {
            if (line.isBlank()) {
                instructions = true;
                continue;
            }
            if (instructions) {
                folds.add(new Fold(line.contains("x"), LineConvert.ints(line).getInt(0)));
            } else {
                startingCoords.add(LineConvert.coordinate(line));
            }
        }
    }

    private record Fold(boolean horizontal, int line) {}
}
