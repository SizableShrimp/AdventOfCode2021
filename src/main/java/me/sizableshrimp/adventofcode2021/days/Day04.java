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

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.sizableshrimp.adventofcode2021.helper.LineConvert;
import me.sizableshrimp.adventofcode2021.templates.SeparatedDay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// https://adventofcode.com/2021/day/4 - Giant Squid
public class Day04 extends SeparatedDay {
    private IntList drawList;
    private Int2IntMap drawMap;
    private List<Board> boards;

    public static void main(String[] args) {
        new Day04().run();
    }

    @Override
    protected Object part1() {
        return getWinningScore(false);
    }

    @Override
    protected Object part2() {
        return getWinningScore(true);
    }

    private int getWinningScore(boolean max) {
        Board selected = null;
        int target = 0;

        for (Board board : boards) {
            int winIndex = board.getWinIndex();
            if (selected == null || (max && winIndex > target) || (!max && winIndex < target)) {
                target = winIndex;
                selected = board;
            }
        }

        return selected.getScore(target);
    }

    @Override
    protected void parse() {
        drawList = IntArrayList.toList(Arrays.stream(lines.get(0).split(",")).mapToInt(Integer::parseInt));
        drawMap = new Int2IntOpenHashMap();
        for (int i = 0; i < drawList.size(); i++) {
            drawMap.put(drawList.getInt(i), i);
        }

        boards = new ArrayList<>();

        Board cur = null;
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.isBlank()) {
                cur = new Board();
                boards.add(cur);
                continue;
            }

            cur.board.add(IntArrayList.toList(LineConvert.ints(line).intStream().map(drawMap::get)));
        }
    }

    @RequiredArgsConstructor
    @Getter
    @Setter
    private class Board {
        final List<IntList> board = new ArrayList<>();
        boolean done;
        int winIndex = -1;

        int getScore(int winIndex) {
            return drawList.getInt(winIndex) * getUnmarkedSum(winIndex);
        }

        int getUnmarkedSum(int winIndex) {
            return board.stream().mapToInt(row -> row.intStream().filter(n -> n > winIndex).map(drawList::getInt).sum()).sum();
        }

        int getWinIndex() {
            if (winIndex == -1) {
                int min = Integer.MAX_VALUE;

                for (IntList row : board) {
                    int rowMax = 0;
                    for (int num : row) {
                        rowMax = Math.max(rowMax, num);
                    }
                    min = Math.min(min, rowMax);
                }

                for (int x = 0; x < 5; x++) {
                    int columnMax = 0;
                    for (IntList row : board) {
                        columnMax = Math.max(columnMax, row.getInt(x));
                    }
                    min = Math.min(min, columnMax);
                }

                winIndex = min;
                // winIndex = IntStream.concat(board.stream().mapToInt(row -> row.intStream().max().orElseThrow()),
                //         IntStream.range(0, 5).map(x -> board.stream().mapToInt(row -> row.getInt(x)).max().orElseThrow())).min().orElseThrow();
            }

            return winIndex;
        }

        public String toString(int winIndex) {
            StringBuilder sb = new StringBuilder();
            for (IntList row : board) {
                for (int num : row) {
                    sb.append(num <= winIndex ? '#' : '.');
                }
                sb.append('\n');
            }
            return sb.toString();
        }
    }
}
