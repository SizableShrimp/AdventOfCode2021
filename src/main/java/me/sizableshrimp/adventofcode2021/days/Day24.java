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

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import me.sizableshrimp.adventofcode2021.templates.Day;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Day24 extends Day {
    private List<List<Instruction>> perInput = new ArrayList<>();

    public static void main(String[] args) {
        new Day24().run();
    }

    @Override
    protected Result evaluate() {
        return Result.of(findModelNumber(false), findModelNumber(true));
    }

    private long findModelNumber(boolean smallest) {
        int startingDigit = smallest ? 1 : 9;
        int endingDigit = smallest ? 10 : 0;
        int direction = smallest ? 1 : -1;

        List<IntSet> outputs = calculateOutputs();
        long result = 0;
        int inputZ = 0;

        for (int i = 0; i < outputs.size(); i++) {
            IntSet target = i == outputs.size() - 1 ? IntSet.of(0) : outputs.get(i + 1);

            for (int digit = startingDigit; digit != endingDigit; digit += direction) {
                int outputZ = runDigit(i, digit, inputZ);
                if (target.contains(outputZ)) {
                    inputZ = outputZ;
                    result = result * 10 + digit;
                    break;
                }
            }
        }

        return result;
    }

    private List<IntSet> calculateOutputs() {
        IntSet targets = IntSet.of(0);
        List<IntSet> outputs = new ArrayList<>(14);

        for (int i = 13; i >= 0; i--) {
            IntSet validInputs = new IntOpenHashSet();

            for (int digit = 9; digit >= 1; digit--) {
                for (int z = 0; z < 10_000; z++) {
                    if (targets.contains(runDigit(i, digit, z))) {
                        validInputs.add(z);
                    }
                }
            }

            outputs.add(0, validInputs);
            targets = validInputs;
        }

        return outputs;
    }

    private int runDigit(int index, int digit, int z) {
        List<Instruction> subInstructions = perInput.get(index);
        return runModZ(digit, z, subInstructions.get(4).b, subInstructions.get(5).b, subInstructions.get(15).b);
    }

    private int runModZ(int digit, int z, int divZ, int addX, int addY) {
        return z % 26 + addX != digit ? (z / divZ * 26) + digit + addY : z / divZ;
    }

    @Override
    protected void parse() {
        perInput = new ArrayList<>();
        List<Instruction> currentInput = null;

        for (String line : lines) {
            int spaceIdx = line.indexOf(' ');
            Type type = Type.BY_ID.get(line.substring(0, spaceIdx));
            String[] args = line.substring(spaceIdx + 1).split(" ");

            if (type == Type.INPUT) {
                if (currentInput != null)
                    perInput.add(currentInput);
                currentInput = new ArrayList<>();
            }

            boolean aLetter = Character.isLetter(args[0].charAt(0));
            int a = aLetter ? args[0].charAt(0) - 'w' : Integer.parseInt(args[0]);
            boolean bLetter = false;
            int b = 0;

            if (args.length >= 2) {
                bLetter = Character.isLetter(args[1].charAt(0));
                b = bLetter ? args[1].charAt(0) - 'w' : Integer.parseInt(args[1]);
            }

            currentInput.add(new Instruction(type, a, b, aLetter, bLetter));
        }

        perInput.add(currentInput);
    }

    private record Instruction(Type type, int a, int b, boolean aLetter, boolean bLetter) {}

    private enum Type {
        INPUT("inp"),
        ADD("add"),
        MULTIPLY("mul"),
        DIVIDE("div"),
        MODULO("mod"),
        EQUALS("eql");

        public static final Map<String, Type> BY_ID = Arrays.stream(Type.values()).collect(Collectors.toMap(Type::getId, Function.identity()));
        private final String id;

        Type(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }
}
