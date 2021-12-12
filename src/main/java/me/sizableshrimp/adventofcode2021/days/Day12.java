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

import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenCustomHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import me.sizableshrimp.adventofcode2021.templates.Day;

import java.util.ArrayList;
import java.util.List;

public class Day12 extends Day {
    private long smallCaves;
    private Long2ObjectMap<LongSet> cavePaths;
    private long startId;
    private long endId;

    public static void main(String[] args) {
        new Day12().run();
    }

    @Override
    protected Result evaluate() {
        List<Node> validPaths = new ArrayList<>();

        traverse(new Node(startId, startId, -1), validPaths);

        return Result.of(getPart1Count(validPaths), validPaths.size());
    }

    private void traverse(Node node, List<Node> validPaths) {
        long current = node.current;

        if (endId == current) {
            validPaths.add(node);
            return;
        }

        LongSet currentPaths = cavePaths.get(current);

        for (long cave : currentPaths) {
            if (cave == startId)
                continue;
            if (containsCave(node.visited, cave) && containsCave(smallCaves, cave)) {
                if (node.secondSmall == -1) {
                    traverse(new Node(getNewVisited(node, cave), cave, cave), validPaths);
                }
                continue;
            }

            traverse(new Node(getNewVisited(node, cave), cave, node.secondSmall), validPaths);
        }
    }

    private long getNewVisited(Node node, long cave) {
        return node.visited | cave;
    }

    private boolean containsCave(long visited, long cave) {
        return (visited & cave) == cave;
    }

    private long getPart1Count(List<Node> validPaths) {
        int count = 0;
        for (Node path : validPaths) {
            if (path.secondSmall == -1)
                count++;
        }
        return count;
    }

    @Override
    protected void parse() {
        List<String> caves = new ArrayList<>();
        smallCaves = 0L;
        cavePaths = new Long2ObjectOpenHashMap<>();

        for (String line : lines) {
            String[] split = line.split("-");
            long a = getCaveId(caves, split[0]);
            long b = getCaveId(caves, split[1]);
            cavePaths.computeIfAbsent(a, k -> new LongOpenHashSet()).add(b);
            cavePaths.computeIfAbsent(b, k -> new LongOpenHashSet()).add(a);
        }

        startId = 1L << caves.indexOf("start");
        endId = 1L << caves.indexOf("end");
    }

    private long getCaveId(List<String> caves, String cave) {
        int id = caves.indexOf(cave);
        long shifted;

        if (id == -1) {
            id = caves.size();
            shifted = 1L << id;
            caves.add(cave);
            if (allLowercase(cave))
                smallCaves |= shifted;
        } else {
            shifted = 1L << id;
        }

        return shifted;
    }

    private boolean allLowercase(String in) {
        for (int i = 0; i < in.length(); i++) {
            if (!Character.isLowerCase(in.charAt(i)))
                return false;
        }

        return true;
    }

    private record Node(long visited, long current, long secondSmall) {}
}
