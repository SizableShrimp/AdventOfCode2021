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

import me.sizableshrimp.adventofcode2021.helper.Parser;
import me.sizableshrimp.adventofcode2021.templates.Coordinate;
import me.sizableshrimp.adventofcode2021.templates.SeparatedDay;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

// https://adventofcode.com/2021/day/5 - Hydrothermal Venture
public class Day05 extends SeparatedDay {
    public static final Pattern SEGMENT_PATTERN = Pattern.compile("(\\d+),(\\d+) -> (\\d+),(\\d+)");
    private List<Segment> segments;

    public static void main(String[] args) {
        new Day05().run();
    }

    @Override
    protected Object part1() {
        return getOverlaps(false);
    }

    @Override
    protected Object part2() {
        return getOverlaps(true);
    }

    private int getOverlaps(boolean includeDiagonals) {
        Set<Coordinate> visited = new HashSet<>();
        Set<Coordinate> overlaps = new HashSet<>();
        for (Segment s : segments) {
            s.populate(visited, overlaps, includeDiagonals);
        }
        return overlaps.size();
    }

    @Override
    protected void parse() {
        segments = Parser.parseLinesStream(SEGMENT_PATTERN, lines)
                .map(mw -> new Segment(Coordinate.of(mw.groupInt(1), mw.groupInt(2)), Coordinate.of(mw.groupInt(3), mw.groupInt(4))))
                .toList();
    }

    private void printOverlaps(Set<Coordinate> overlaps) {
        int maxX = segments.stream().mapToInt(s -> Math.max(s.start.x, s.end.x)).max().orElseThrow();
        int maxY = segments.stream().mapToInt(s -> Math.max(s.start.y, s.end.y)).max().orElseThrow();
        for (int y = 0; y <= maxY; y++) {
            for (int x = 0; x <= maxX; x++) {
                System.out.print(overlaps.contains(Coordinate.of(x, y)) ? '#' : '.');
            }
            System.out.println();
        }
    }

    private record Segment(Coordinate start, Coordinate end) {
        void populate(Set<Coordinate> visited, Set<Coordinate> overlaps, boolean includeDiagonals) {
            if (start.y == end.y) {
                populateVertical(visited, overlaps);
            } else if (start.x == end.x) {
                populateHorizontal(visited, overlaps);
            } else if (includeDiagonals) {
                populateDiagonal(visited, overlaps);
            }
        }

        private void populateVertical(Set<Coordinate> visited, Set<Coordinate> overlaps) {
            int minX = Math.min(start.x, end.x);
            int maxX = Math.max(start.x, end.x);
            int y = start.y;
            for (int x = minX; x <= maxX; x++) {
                Coordinate coord = Coordinate.of(x, y);
                if (!visited.add(coord))
                    overlaps.add(coord);
            }
        }

        private void populateHorizontal(Set<Coordinate> visited, Set<Coordinate> overlaps) {
            int minY = Math.min(start.y, end.y);
            int maxY = Math.max(start.y, end.y);
            int x = start.x;
            for (int y = minY; y <= maxY; y++) {
                Coordinate coord = Coordinate.of(x, y);
                if (!visited.add(coord))
                    overlaps.add(coord);
            }
        }

        private void populateDiagonal(Set<Coordinate> visited, Set<Coordinate> overlaps) {
            int dirX = start.x < end.x ? 1 : -1;
            int dirY = start.y < end.y ? 1 : -1;
            int endX = end.x + dirX;
            int y = start.y;
            for (int x = start.x; x != endX; x += dirX) {
                Coordinate coord = Coordinate.of(x, y);
                if (!visited.add(coord))
                    overlaps.add(coord);
                y += dirY;
            }
        }
    }
}
