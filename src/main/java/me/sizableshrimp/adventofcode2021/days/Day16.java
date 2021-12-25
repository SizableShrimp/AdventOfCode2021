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

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import me.sizableshrimp.adventofcode2021.templates.Day;

import java.math.BigInteger;

// https://adventofcode.com/2021/day/16 - Packet Decoder
public class Day16 extends Day {
    private String template;
    private int versions;

    public static void main(String[] args) {
        new Day16().run();
    }

    @Override
    protected Result evaluate() {
        versions = 0;
        LongList values = new LongArrayList();
        total(template, 0, values);
        return Result.of(versions, values.getLong(0));
    }

    private int total(String input, int idx, LongList values) {
        return total(input, Integer.parseInt(input.substring(idx, idx + 3), 2), Integer.parseInt(input.substring(idx + 3, idx + 6), 2), idx + 6, values);
    }

    private int total(String input, int version, int type, int idx, LongList values) {
        versions += version;

        if (type == 4) {
            // Literal
            long total = 0;
            int prefix;

            do {
                prefix = input.charAt(idx) - '0';
                int group = Integer.parseInt(input.substring(idx + 1, idx + 5), 2);
                total = total << 4L | group;
                idx += 5;
            } while (prefix == 1);

            values.add(total);
            return idx;
        }

        int lengthType = input.charAt(idx) - '0';
        idx++;
        LongList subValues = new LongArrayList();

        if (lengthType == 1) {
            int numSubs = Integer.parseInt(input.substring(idx, idx + 11), 2);
            idx += 11;
            for (int i = 0; i < numSubs; i++) {
                idx = total(input, idx, subValues);
            }
        } else {
            int numBits = Integer.parseInt(input.substring(idx, idx + 15), 2);
            idx += 15;
            int target = idx + numBits;
            while (idx < target) {
                idx = total(input, idx, subValues);
            }
        }

        switch (type) {
            case 0 -> values.add(subValues.longStream().reduce(0, Math::addExact));
            case 1 -> values.add(subValues.longStream().reduce(1, Math::multiplyExact));
            case 2 -> values.add(subValues.longStream().min().orElseThrow());
            case 3 -> values.add(subValues.longStream().max().orElseThrow());
            case 5 -> values.add(subValues.getLong(0) > subValues.getLong(1) ? 1L : 0L);
            case 6 -> values.add(subValues.getLong(0) < subValues.getLong(1) ? 1L : 0L);
            case 7 -> values.add(subValues.getLong(0) == subValues.getLong(1) ? 1L : 0L);
            default -> throw new IllegalStateException("Unexpected id: " + type);
        }

        return idx;
    }

    @Override
    protected void parse() {
        template = getBinaryInput(lines.get(0));
    }

    private String getBinaryInput(String hex) {
        BigInteger bigInt = new BigInteger(hex, 16);
        StringBuilder builder = new StringBuilder(bigInt.toString(2));
        int diff = hex.length() * 4 - builder.length();
        for (int i = 0; i < diff; i++) {
            builder.insert(0, '0');
        }
        return builder.toString();
    }
}
