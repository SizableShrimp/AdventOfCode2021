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

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import me.sizableshrimp.adventofcode2021.templates.SeparatedDay;

// https://adventofcode.com/2021/day/18 - Snailfish
public class Day18 extends SeparatedDay {
    public static void main(String[] args) {
        new Day18().run();
    }

    @Override
    protected Object part1() {
        Token start = parseToken(lines.get(0));

        for (int i = 1; i < lines.size(); i++) {
            Token oldStart = start;
            start = new Open();
            start.add(oldStart);
            start.end().add(parseToken(lines.get(i))).end().add(new Close());
            reduce(start);
        }

        return start.getMagnitude();
    }

    @Override
    protected Object part2() {
        int max = 0;

        for (int i = 0; i < lines.size(); i++) {
            String first = lines.get(i);
            for (int j = 0; j < lines.size(); j++) {
                if (i == j)
                    continue;
                String second = lines.get(j);
                max = Math.max(max, getPairMagnitude(first, second));
                max = Math.max(max, getPairMagnitude(second, first));
            }
        }

        return max;
    }

    private int getPairMagnitude(String first, String second) {
        Token start = parseToken('[' + first + ',' + second + ']');
        reduce(start);
        return start.getMagnitude();
    }

    private void reduce(Token start) {
        boolean changed;
        do {
            changed = reduceOnce(start);
        } while (changed);
    }

    private boolean reduceOnce(Token start) {
        return reduceExplode(start) || reduceSplit(start);
    }

    private boolean reduceExplode(Token start) {
        Token cur = start;
        int depth = 0;

        while (cur != null) {
            if (cur instanceof Open) {
                depth++;
            } else if (cur instanceof Close) {
                depth--;
            }

            if (depth >= 5) {
                Token explodeStart = cur;
                while (!(explodeStart instanceof Value) || !(explodeStart.next() instanceof Value)) {
                    explodeStart = explodeStart.next();
                }
                int leftValue = ((Value) explodeStart).value();
                int rightValue = ((Value) explodeStart.next()).value();
                Value empty = new Value(0);
                Token beforeOpen = explodeStart.prev().prev();
                Token afterClose = explodeStart.next().next().next();
                beforeOpen.add(empty).add(afterClose);
                Value left = getLeftValue(explodeStart);
                Value right = getRightValue(explodeStart.next());
                if (left != null)
                    left.value += leftValue;
                if (right != null)
                    right.value += rightValue;
                return true;
            }
            cur = cur.next();
        }

        return false;
    }

    private boolean reduceSplit(Token start) {
        Token cur = start;
        while (cur != null) {
            if (cur instanceof Value valueToken) {
                int value = valueToken.value();
                if (value >= 10) {
                    int down = value / 2;
                    int up = value % 2 == 0 ? down : (value / 2 + 1);
                    Token oldNext = valueToken.next();
                    valueToken.prev().add(new Open()).add(new Value(down)).add(new Value(up)).add(new Close()).add(oldNext);
                    return true;
                }
            }
            cur = cur.next();
        }

        return false;
    }

    private Value getLeftValue(Token token) {
        Token cur = token.prev();
        while (cur != null) {
            if (cur instanceof Value value)
                return value;
            cur = cur.prev();
        }

        return null;
    }

    private Value getRightValue(Token token) {
        Token cur = token.next();
        while (cur != null) {
            if (cur instanceof Value value)
                return value;
            cur = cur.next();
        }

        return null;
    }

    private Token parseToken(String s) {
        Token start = null;
        Token cur = null;

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            Token prev = cur;
            if (c == '[') {
                cur = new Open();
            } else if (c == ']') {
                cur = new Close();
            } else if (Character.isDigit(c)) {
                int num = 0;
                do {
                    num = num * 10 + c - '0';
                    i++;
                    c = s.charAt(i);
                } while (Character.isDigit(c));
                i--;
                cur = new Value(num);
            } else {
                continue;
            }
            if (start == null)
                start = cur;
            if (prev != null)
                prev.add(cur);
        }

        return start;
    }

    @RequiredArgsConstructor
    @Getter
    @Accessors(fluent = true, chain = false)
    @EqualsAndHashCode
    private abstract static sealed class Token permits Open, Close, Value {
        Token prev;
        Token next;

        int getMagnitude() {
            Token left = this.next();
            Token right;
            if (left instanceof Value) {
                right = left.next();
            } else {
                right = left;
                int depth = 0;
                do {
                    if (right instanceof Open) {
                        depth++;
                    } else if (right instanceof Close) {
                        depth--;
                    }
                    right = right.next();
                } while (depth != 0);
            }
            return 3 * (left instanceof Value value ? value.value() : left.getMagnitude()) + 2 * (right instanceof Value value ? value.value() : right.getMagnitude());
        }

        Token end() {
            Token cur = this;
            while (cur.next != null) {
                cur = cur.next;
            }
            return cur;
        }

        <T extends Token> T add(T next) {
            this.next = next;
            next.prev = this;
            return next;
        }
    }

    private static final class Open extends Token {
        @Override
        public String toString() {
            return "[" + next;
        }
    }

    private static final class Close extends Token {
        @Override
        public String toString() {
            if (next == null)
                return "]";
            return "]" + (next instanceof Close ? next.toString() : "," + next);
        }
    }

    @AllArgsConstructor
    @Getter
    @Accessors(fluent = true, chain = false)
    @EqualsAndHashCode(callSuper = false)
    private static final class Value extends Token {
        int value;

        @Override
        public String toString() {
            return value + (next instanceof Close ? next.toString() : "," + next);
        }
    }
}
