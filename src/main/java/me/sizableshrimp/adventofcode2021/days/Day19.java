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

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import me.sizableshrimp.adventofcode2021.helper.Processor;
import me.sizableshrimp.adventofcode2021.templates.Day;
import me.sizableshrimp.adventofcode2021.templates.ZCoordinate;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// https://adventofcode.com/2021/day/19 - Beacon Scanner
public class Day19 extends Day {
    private static final List<ZCoordinateMutator> ROTATION_REFLECTIONS = List.of(
            ZCoordinateMutator.IDENTITY,
            c -> ZCoordinate.of(c.y, c.z, c.x),
            c -> ZCoordinate.of(c.z, c.x, c.y),
            c -> ZCoordinate.of(-c.x, c.z, c.y),
            c -> ZCoordinate.of(c.z, c.y, -c.x),
            c -> ZCoordinate.of(c.y, -c.x, c.z),
            c -> ZCoordinate.of(c.x, c.z, -c.y),
            c -> ZCoordinate.of(c.z, -c.y, c.x),
            c -> ZCoordinate.of(-c.y, c.x, c.z),
            c -> ZCoordinate.of(c.x, -c.z, c.y),
            c -> ZCoordinate.of(-c.z, c.y, c.x),
            c -> ZCoordinate.of(c.y, c.x, -c.z),
            c -> ZCoordinate.of(-c.x, -c.y, c.z),
            c -> ZCoordinate.of(-c.y, c.z, -c.x),
            c -> ZCoordinate.of(c.z, -c.x, -c.y),
            c -> ZCoordinate.of(-c.x, c.y, -c.z),
            c -> ZCoordinate.of(c.y, -c.z, -c.x),
            c -> ZCoordinate.of(-c.z, -c.x, c.y),
            c -> ZCoordinate.of(c.x, -c.y, -c.z),
            c -> ZCoordinate.of(-c.y, -c.z, c.x),
            c -> ZCoordinate.of(-c.z, c.x, -c.y),
            c -> ZCoordinate.of(-c.x, -c.z, -c.y),
            c -> ZCoordinate.of(-c.z, -c.y, -c.x),
            c -> ZCoordinate.of(-c.y, -c.x, -c.z)
    );
    private List<Set<ZCoordinate>> scanners;

    public static void main(String[] args) {
        new Day19().run();
    }

    @Override
    protected Result evaluate() {
        List<IntList> scannerRelatives = new ArrayList<>();
        for (Set<ZCoordinate> beacons : scanners) {
            List<ZCoordinate> beaconsList = new ArrayList<>(beacons);
            IntList relDist = new IntArrayList();
            for (int i = 0; i < beaconsList.size(); i++) {
                ZCoordinate beacon = beaconsList.get(i);
                // i + 1 prevents against duplicate pair matching
                for (int j = i + 1; j < beaconsList.size(); j++) {
                    ZCoordinate other = beaconsList.get(j);
                    if (beacon == other)
                        continue;
                    relDist.add(beacon.distance(other));
                }
            }
            scannerRelatives.add(relDist);
        }

        Int2ObjectMap<Set<Node>> offsets = new Int2ObjectOpenHashMap<>();
        for (int i = 0; i < scannerRelatives.size(); i++) {
            IntList relDist = scannerRelatives.get(i);
            for (int j = 0; j < scannerRelatives.size(); j++) {
                if (i == j)
                    continue;
                IntList other = scannerRelatives.get(j);
                // Magic number 66 == arithmetic series of 1 to 11, which is the sum of the distances that need to match for 12 beacons/points to overlap
                if (Processor.intersection(relDist, other).size() >= 66)
                    computeOffsets(offsets, i, j);
            }
        }

        Deque<Node> queue = new ArrayDeque<>(offsets.get(0));
        Int2ObjectMap<ZCoordinateMutator> relatives = new Int2ObjectOpenHashMap<>();

        relatives.put(0, ZCoordinateMutator.IDENTITY);

        while (!queue.isEmpty()) {
            Node node = queue.pop();
            // Data for going from scanner A coord -> scanner 0 coord
            ZCoordinateMutator firstToZero = relatives.get(node.scannerA);
            // Data for going from scanner B coord -> scanner A coord
            ZCoordinateMutator thisToFirst = node.mutator;
            // Data for going from scanner B coord -> scanner A coord -> scanner 0 coord == scanner B coord -> scanner 0 coord
            ZCoordinateMutator thisToZero = thisToFirst.and(firstToZero);
            // Populate the map
            relatives.put(node.scannerB, thisToZero);
            // Add nearby scanners of scanner B
            Set<Node> nearby = offsets.get(node.scannerB);
            for (Node next : nearby) {
                if (!relatives.containsKey(next.scannerB))
                    queue.addAll(nearby);
            }
        }

        int maxDist = 0;
        Set<ZCoordinate> all = new HashSet<>();

        for (int i = 0; i < scanners.size(); i++) {
            ZCoordinateMutator thisToZero = relatives.get(i);

            // Part 1
            Set<ZCoordinate> scannerSet = scanners.get(i);
            for (ZCoordinate coord : scannerSet) {
                all.add(thisToZero.apply(coord));
            }

            // Part 2
            for (int j = 0; j < scanners.size(); j++) {
                if (i == j)
                    continue;
                ZCoordinateMutator secondToZero = relatives.get(j);
                int dist = thisToZero.apply(ZCoordinate.ORIGIN).distance(secondToZero.apply(ZCoordinate.ORIGIN));
                if (dist > maxDist)
                    maxDist = dist;
            }
        }

        return Result.of(all.size(), maxDist);
    }

    private void computeOffsets(Int2ObjectMap<Set<Node>> offsets, int scannerA, int scannerB) {
        Set<ZCoordinate> firstSet = scanners.get(scannerA);
        Set<ZCoordinate> secondSet = scanners.get(scannerB);

        for (ZCoordinateMutator mutator : ROTATION_REFLECTIONS) {
            Object2IntMap<ZCoordinate> map = new Object2IntOpenHashMap<>();
            for (ZCoordinate coord : secondSet) {
                for (ZCoordinate other : firstSet) {
                    // Offset = (scanner B reordered coord) - (scanner A normal coord)
                    // Scanner A normal coord = (scanner B reordered coord) - offset
                    // Scanner B reordered coord = (scanner A normal coord) + offset
                    // Reordering -> what we need to do to scanner B's positions to get it to something relative to scanner A
                    ZCoordinate offset = mutator.apply(coord).subtract(other);
                    if (map.compute(offset, (k, v) -> v == null ? 1 : v + 1) == 12) {
                        offsets.computeIfAbsent(scannerA, k -> new HashSet<>()).add(new Node(scannerA, scannerB, c -> mutator.apply(c).subtract(offset)));
                    }
                }
            }
        }
    }

    @Override
    protected void parse() {
        scanners = new ArrayList<>();
        Set<ZCoordinate> current = null;

        for (String line : lines) {
            if (line.isBlank())
                continue;
            if (line.startsWith("---")) {
                if (current != null)
                    scanners.add(current);
                current = new HashSet<>();
            } else {
                current.add(ZCoordinate.parse(line));
            }
        }

        scanners.add(current);
    }

    @FunctionalInterface
    private interface ZCoordinateMutator {
        ZCoordinateMutator IDENTITY = c -> c;

        ZCoordinate apply(ZCoordinate original);

        default ZCoordinateMutator and(ZCoordinateMutator other) {
            return c -> other.apply(this.apply(c));
        }
    }

    private record Node(int scannerA, int scannerB, ZCoordinateMutator mutator) {}
}
