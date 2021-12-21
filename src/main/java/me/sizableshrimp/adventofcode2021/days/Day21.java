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
import it.unimi.dsi.fastutil.ints.Int2IntMaps;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongLongPair;
import me.sizableshrimp.adventofcode2021.templates.SeparatedDay;

import java.util.HashMap;
import java.util.Map;

public class Day21 extends SeparatedDay {
    private static final LongLongPair PLAYER_ONE_WINS = LongLongPair.of(1, 0);
    private static final LongLongPair PLAYER_TWO_WINS = LongLongPair.of(0, 1);
    private static final Int2IntMap QUANTUM_DICE_CURVE;
    private Map<State, LongLongPair> cache;
    private State startingState;

    static {
        Int2IntMap diceCurve = new Int2IntOpenHashMap();

        // Curve of total roll -> frequency to speed up memoization
        diceCurve.put(3, 1);
        diceCurve.put(4, 3);
        diceCurve.put(5, 6);
        diceCurve.put(6, 7);
        diceCurve.put(7, 6);
        diceCurve.put(8, 3);
        diceCurve.put(9, 1);

        QUANTUM_DICE_CURVE = Int2IntMaps.unmodifiable(diceCurve);
    }

    public static void main(String[] args) {
        new Day21().run();
    }

    @Override
    protected Object part1() {
        State state = startingState;
        int diceRolled = 0;

        while (true) {
            state = runTurn(state, diceRolled++ % 100 + diceRolled++ % 100 + diceRolled++ % 100 + 3);

            if (state.p1Score >= 1000) {
                return state.p2Score * diceRolled;
            } else if (state.p2Score >= 1000) {
                return state.p1Score * diceRolled;
            }
        }
    }

    @Override
    protected Object part2() {
        LongLongPair winPair = findWinUniverses(startingState);
        return Math.max(winPair.leftLong(), winPair.rightLong());
    }

    private LongLongPair findWinUniverses(State node) {
        if (cache.containsKey(node))
            return cache.get(node);

        LongLongPair result;

        if (node.p1Score >= 21) {
            result = PLAYER_ONE_WINS;
        } else if (node.p2Score >= 21) {
            result = PLAYER_TWO_WINS;
        } else {
            long left = 0;
            long right = 0;

            for (Int2IntMap.Entry entry : QUANTUM_DICE_CURVE.int2IntEntrySet()) {
                int roll = entry.getIntKey();
                int frequency = entry.getIntValue();
                LongLongPair pair = findWinUniverses(runTurn(node, roll));
                left += frequency * pair.leftLong();
                right += frequency * pair.rightLong();
            }

            result = LongLongPair.of(left, right);
        }

        cache.put(node, result);
        return result;
    }

    private State runTurn(State prev, int roll) {
        if (prev.turnOfP1) {
            int p1Pos = ((prev.p1Pos + roll) - 1) % 10 + 1;
            return new State(p1Pos, prev.p2Pos, prev.p1Score + p1Pos, prev.p2Score, false);
        } else {
            int p2Pos = ((prev.p2Pos + roll) - 1) % 10 + 1;
            return new State(prev.p1Pos, p2Pos, prev.p1Score, prev.p2Score + p2Pos, true);
        }
    }

    @Override
    protected void parse() {
        cache = new HashMap<>();

        int p1StartingPos = Integer.parseInt(lines.get(0).substring(lines.get(0).indexOf(':') + 2));
        int p2StartingPos = Integer.parseInt(lines.get(1).substring(lines.get(1).indexOf(':') + 2));
        startingState = new State(p1StartingPos, p2StartingPos, 0, 0, true);
    }

    private record State(int p1Pos, int p2Pos, int p1Score, int p2Score, boolean turnOfP1) {}
}
