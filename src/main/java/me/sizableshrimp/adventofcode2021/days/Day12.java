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

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import lombok.Data;
import me.sizableshrimp.adventofcode2021.templates.Day;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day12 extends Day {
    private Multimap<String, String> cavePaths;

    public static void main(String[] args) {
        new Day12().run();
    }

    @Override
    protected Result evaluate() {
        Deque<Node> queue = new ArrayDeque<>();
        Set<Node> validPaths = new HashSet<>();

        queue.add(new Node(List.of("start"), null));

        while (!queue.isEmpty()) {
            Node node = queue.removeLast();
            String current = node.path.get(node.path.size() - 1);

            if ("end".equals(current)) {
                validPaths.add(node);
                continue;
            }

            Collection<String> currentPaths = cavePaths.get(current);
            Set<String> possible = new HashSet<>();
            Set<String> secondSmallPossibles = node.secondSmall == null ? new HashSet<>() : null;

            for (String cave : currentPaths) {
                if ("start".equals(cave))
                    continue;
                if (node.path.contains(cave) && isSmall(cave)) {
                    if (secondSmallPossibles != null)
                        secondSmallPossibles.add(cave);
                    continue;
                }
                possible.add(cave);
            }

            if (secondSmallPossibles != null) {
                for (String cave : secondSmallPossibles) {
                    List<String> newPath = new ArrayList<>(node.path);
                    newPath.add(cave);
                    queue.add(new Node(newPath, cave));
                }
            }
            for (String cave : possible) {
                List<String> newPath = new ArrayList<>(node.path);
                newPath.add(cave);
                queue.add(new Node(newPath, node.secondSmall));
            }
        }

        return Result.of(getPart1Count(validPaths), validPaths.size());
    }

    private long getPart1Count(Set<Node> validPaths) {
        int count = 0;
        for (Node path : validPaths) {
            if (path.secondSmall == null)
                count++;
        }
        return count;
    }

    private boolean isSmall(String in) {
        for (int i = 0; i < in.length(); i++) {
            if (!Character.isLowerCase(in.charAt(i)))
                return false;
        }

        return true;
    }

    @Override
    protected void parse() {
        cavePaths = MultimapBuilder.hashKeys().hashSetValues().build();
        for (String line : lines) {
            String[] split = line.split("-");
            cavePaths.put(split[0], split[1]);
            cavePaths.put(split[1], split[0]);
        }
    }

    @Data
    private class Node {
        final List<String> path;
        final String secondSmall;
    }
}
