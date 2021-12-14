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

import it.unimi.dsi.fastutil.chars.Char2LongMap;
import it.unimi.dsi.fastutil.chars.Char2LongOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2CharMap;
import it.unimi.dsi.fastutil.ints.Int2CharOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import me.sizableshrimp.adventofcode2021.templates.SeparatedDay;

public class Day14 extends SeparatedDay {
    private Int2ObjectMap<Char2LongMap> cache;
    private Int2CharMap insertions;
    private String template;

    public static void main(String[] args) {
        new Day14().run();
    }

    @Override
    protected Object part1() {
        return calculate(10);
    }

    @Override
    protected Object part2() {
        return calculate(40);
    }

    private long calculate(int steps) {
        Char2LongMap quantities = new Char2LongOpenHashMap();
        int length = template.length();
        for (int i = 0; i < length; i++) {
            char a = template.charAt(i);
            // We don't account for the starting template's character count, so do it here
            quantities.mergeLong(a, 1, Long::sum);
            if (i < length - 1)
                mergeAll(quantities, calculate(a, template.charAt(i + 1), steps));
        }

        long max = Long.MIN_VALUE;
        long min = Long.MAX_VALUE;
        for (long count : quantities.values()) {
            if (count > max)
                max = count;
            if (count < min)
                min = count;
        }

        return max - min;
    }

    private Char2LongMap calculate(char a, char b, int depth) {
        int hash = hashNode(a, b, depth);
        if (cache.containsKey(hash))
            return cache.get(hash);

        Char2LongOpenHashMap quantities = new Char2LongOpenHashMap();
        char c = insertions.get(hashChars(a, b));
        if (depth > 1) {
            mergeAll(quantities, calculate(a, c, depth - 1));
            mergeAll(quantities, calculate(c, b, depth - 1));
        }
        quantities.mergeLong(c, 1, Long::sum);
        cache.put(hash, quantities);

        return quantities;
    }

    private void mergeAll(Char2LongMap quantities, Char2LongMap other) {
        for (var entry : other.char2LongEntrySet()) {
            quantities.mergeLong(entry.getCharKey(), entry.getLongValue(), Math::addExact);
        }
    }

    private int hashNode(char a, char b, int depth) {
        // a and b - 0 through 25 (5 bits)
        // depth - 1 through 40 (6 bits)
        return a - 'A' << 16 | b - 'A' << 11 | depth;
    }

    private int hashChars(char a, char b) {
        // a and b - 0 through 25 (5 bits)
        return a - 'A' << 10 | b - 'A';
    }

    @Override
    protected void parse() {
        cache = new Int2ObjectOpenHashMap<>();
        insertions = new Int2CharOpenHashMap();
        template = lines.get(0);

        for (int i = 2; i < lines.size(); i++) {
            String line = lines.get(i);
            insertions.put(hashChars(line.charAt(0), line.charAt(1)), line.charAt(6));
        }
    }
}
