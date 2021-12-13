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
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import me.sizableshrimp.adventofcode2021.templates.Day;

import java.util.ArrayList;
import java.util.List;

public class Day12 extends Day {
    private int smallCaves;
    private Int2ObjectMap<IntSet> cavePaths;
    private int startId;
    private int endId;

    public static void main(String[] args) {
        new Day12().run();
    }

    @Override
    protected Result evaluate() {
        return Result.of(traverse(startId, startId, -1, false), traverse(startId, startId, -1, true));
    }

    private int traverse(int visited, int current, int secondSmall, boolean allowSecondSmallCave) {
        if (endId == current)
            return 1;

        IntSet currentPaths = cavePaths.get(current);
        int count = 0;

        for (int cave : currentPaths) {
            if (cave == startId)
                continue;
            if (containsCave(visited, cave) && containsCave(smallCaves, cave)) {
                if (allowSecondSmallCave && secondSmall == -1) {
                    count += traverse(visited | cave, cave, cave, true);
                }
                continue;
            }

            count += traverse(visited | cave, cave, secondSmall, allowSecondSmallCave);
        }

        return count;
    }

    private boolean containsCave(int visited, int cave) {
        return (visited & cave) == cave;
    }

    @Override
    protected void parse() {
        List<String> caves = new ArrayList<>();
        smallCaves = 0;
        cavePaths = new Int2ObjectOpenHashMap<>();

        for (String line : lines) {
            String[] split = line.split("-");
            int a = getCaveId(caves, split[0]);
            int b = getCaveId(caves, split[1]);
            cavePaths.computeIfAbsent(a, k -> new IntOpenHashSet()).add(b);
            cavePaths.computeIfAbsent(b, k -> new IntOpenHashSet()).add(a);
        }

        startId = 1 << caves.indexOf("start");
        endId = 1 << caves.indexOf("end");
    }

    private int getCaveId(List<String> caves, String cave) {
        int id = caves.indexOf(cave);
        int shifted;

        if (id == -1) {
            id = caves.size();
            shifted = 1 << id;
            caves.add(cave);
            if (allLowercase(cave))
                smallCaves |= shifted;
        } else {
            shifted = 1 << id;
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
}
