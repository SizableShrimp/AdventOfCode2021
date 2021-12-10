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

import it.unimi.dsi.fastutil.chars.Char2CharMap;
import it.unimi.dsi.fastutil.chars.Char2CharMaps;
import it.unimi.dsi.fastutil.chars.Char2CharOpenHashMap;
import it.unimi.dsi.fastutil.chars.CharArrayList;
import it.unimi.dsi.fastutil.chars.CharList;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import me.sizableshrimp.adventofcode2021.templates.Day;

import java.util.Map;

public class Day10 extends Day {
    private static final Char2CharMap CLOSERS = Char2CharMaps.unmodifiable(new Char2CharOpenHashMap(Map.of('(', ')', '[', ']', '{', '}', '<', '>')));

    public static void main(String[] args) {
        new Day10().run();
    }

    @Override
    protected Result evaluate() {
        int count = 0;
        LongList scores = new LongArrayList();

        for (String line : lines) {
            char[] chars = line.toCharArray();
            CharList list = new CharArrayList();

            boolean invalid = false;
            for (char c : chars) {
                if (CLOSERS.containsKey(c)) {
                    list.add(c);
                } else {
                    char last = list.removeChar(list.size() - 1);
                    if (CLOSERS.get(last) != c) {
                        // Part 1
                        count += switch (c) {
                            case ')' -> 3;
                            case ']' -> 57;
                            case '}' -> 1197;
                            case '>' -> 25137;
                            default -> throw new IllegalStateException("Unexpected value: " + c);
                        };
                        invalid = true;
                        break;
                    }
                }
            }

            // Part 2
            if (!invalid && !list.isEmpty()) {
                long score = 0;
                while (!list.isEmpty()) {
                    char last = list.removeChar(list.size() - 1);
                    score = score * 5 + switch (last) {
                        case '(' -> 1;
                        case '[' -> 2;
                        case '{' -> 3;
                        case '<' -> 4;
                        default -> throw new IllegalStateException("Unexpected value: " + last);
                    };
                }
                scores.add(score);
            }
        }

        scores.sort(null);
        return Result.of(count, scores.getLong(scores.size() / 2));
    }
}
