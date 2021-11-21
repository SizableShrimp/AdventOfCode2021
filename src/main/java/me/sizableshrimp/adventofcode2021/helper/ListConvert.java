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

package me.sizableshrimp.adventofcode2021.helper;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ListConvert {
    /**
     * Parse all lines to integers.
     * <b>NOTE: Not the same as {@link LineConvert#ints}</b>
     *
     * @return A list of parsed integers.
     */
    public static List<Integer> ints(List<String> list) {
        return convert(list, Integer::valueOf);
    }

    private static <T> List<T> convert(List<String> list, Function<String, T> func) {
        return list.stream()
                .map(func)
                .collect(Collectors.toList());
    }

    public static List<Long> longs(List<String> list) {
        return convert(list, Long::valueOf);
    }

    public static List<Double> doubles(List<String> list) {
        return convert(list, Double::valueOf);
    }

    public static List<Boolean> booleans(List<String> list) {
        return convert(list, Boolean::valueOf);
    }

    public static List<Character> chars(List<String> list) {
        return list.stream()
                .flatMap(s -> LineConvert.chars(s).stream())
                .collect(Collectors.toList());
    }
}
