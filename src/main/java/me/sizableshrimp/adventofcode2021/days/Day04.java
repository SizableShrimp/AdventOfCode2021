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
import it.unimi.dsi.fastutil.ints.IntList;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import me.sizableshrimp.adventofcode2021.helper.LineConvert;
import me.sizableshrimp.adventofcode2021.templates.Day;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Day04 extends Day {
    private List<Integer> drawList;
    private Multimap<Integer, Slot> slotCallMap;
    private List<Board> boards;

    public static void main(String[] args) {
        new Day04().run();
    }

    @Override
    protected Result evaluate() {
        int part1 = -1;

        for (int num : drawList) {
            for (Slot slot : slotCallMap.get(num)) {
                Board board = slot.getBoard();
                if (board.isDone())
                    continue;

                slot.setEnabled(true);
                if (board.hasBingo()) {
                    if (part1 == -1)
                        part1 = board.getUnmarkedSum() * num;
                    board.setDone(true);
                    if (boards.size() == 1) {
                        return Result.of(part1, board.getUnmarkedSum() * num);
                    }
                    boards.remove(board);
                }
            }
        }

        throw new IllegalStateException();
    }

    @Override
    protected void parse() {
        drawList = Arrays.stream(lines.get(0).split(",")).map(Integer::parseInt).toList();

        slotCallMap = MultimapBuilder.hashKeys().arrayListValues().build();
        boards = new ArrayList<>();

        Board cur = null;
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.isBlank()) {
                cur = new Board();
                boards.add(cur);
                continue;
            }

            IntList nums = LineConvert.ints(line);
            Board finalCur = cur;
            List<Slot> slots = nums.intStream().mapToObj(n -> new Slot(n, finalCur)).toList();
            slots.forEach(s -> slotCallMap.put(s.num, s));
            cur.board.add(slots);
        }
    }

    @Data
    private class Slot {
        final int num;
        @ToString.Exclude
        @EqualsAndHashCode.Exclude
        final Board board;
        boolean enabled;
    }

    @Data
    private class Board {
        final List<List<Slot>> board = new ArrayList<>();
        boolean done;

        int getUnmarkedSum() {
            return board.stream().mapToInt(l -> l.stream().filter(s -> !s.isEnabled()).mapToInt(Slot::getNum).sum()).sum();
        }

        boolean hasBingo() {
            for (List<Slot> row : board) {
                boolean valid = true;
                for (Slot slot : row) {
                    if (!slot.isEnabled()) {
                        valid = false;
                        break;
                    }
                }
                if (valid)
                    return true;
            }

            for (int x = 0; x < 5; x++) {
                boolean valid = true;
                for (List<Slot> row : board) {
                    if (!row.get(x).isEnabled()) {
                        valid = false;
                        break;
                    }
                }
                if (valid)
                    return true;
            }

            // This isn't real bingo since diagonals aren't counted, but that's what the problem said to do
            return false;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (List<Slot> row : board) {
                for (Slot s : row) {
                    sb.append(s.isEnabled() ? '#' : '.');
                }
                sb.append('\n');
            }
            return sb.toString();
        }
    }
}
