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

import it.unimi.dsi.fastutil.ints.IntList;
import me.sizableshrimp.adventofcode2021.helper.LineConvert;
import me.sizableshrimp.adventofcode2021.templates.Coordinate;
import me.sizableshrimp.adventofcode2021.templates.SeparatedDay;

import java.util.HashSet;
import java.util.Set;

// https://adventofcode.com/2021/day/17 - Trick Shot
public class Day17 extends SeparatedDay {
    private Coordinate minTarget;
    private Coordinate maxTarget;

    public static void main(String[] args) {
        new Day17().run();
    }

    @Override
    protected Object part1() {
        // We can use the parabolic nature of the y velocity to easily retrieve it from the target input.
        // This assumes that the target y-range is always negative, which appears to always be true for the standard puzzle inputs.
        // Using this assumption, the y-velocity that produces the maximum height is always the opposite of the minimum y-value minus one.
        // The second zero of the parabola will always have a y-velocity opposite in sign of the original y-velocity minus one, or -yVel - 1.
        // Since y=0, the next step will produce a y-value equal to -yVel - 1.
        // If we minimize this value while still being contained within the y-range, we get the maximized original y-velocity.
        // This means -yVel - 1 = minY, which derives yVel = -minY - 1 when solving for the original y-velocity.
        int targetYVel = -minTarget.y - 1;
        // Arithmetic progression from 1 to n gets the maximum height in a single calculation from the target y-velocity
        return targetYVel * (targetYVel + 1) / 2;
    }

    @Override
    protected Object part2() {
        // This solution assumes the x-range is always positive, and the y-range is always negative

        // See part 1 explanation
        int maxYVel = -minTarget.y - 1;
        Set<Coordinate> velocities = new HashSet<>();

        // Y-velocity must be between minimum y-value and maximum y-velocity, inclusive.
        // Lower than the minimum y-value will be outside the target y-range within the first step.
        // Higher than the maximum y-velocity will be outside the y-range on the first step.
        for (int yVel = minTarget.y; yVel <= maxYVel; yVel++) {
            // X-velocity must be between 1 and the maximum x-value, inclusive.
            // Lower than 0 is undefined for x-ranges that are purely positive.
            // 0 only works if the starting position is already inside the target x-range, which is assumed to not be possible with standard puzzle inputs.
            // Higher than the maximum x-value will be outside the x-range on the first step.
            for (int xVel = 1; xVel <= maxTarget.x; xVel++) {
                if (validVelocity(xVel, yVel)) {
                    velocities.add(Coordinate.of(xVel, yVel));
                }
            }
        }

        return velocities.size();
    }

    // Calculates if a velocity pair is valid and returns false if the drone misses the target range after any step.
    private boolean validVelocity(int xVel, int yVel) {
        Coordinate cur = Coordinate.ORIGIN;

        do {
            cur = cur.resolve(xVel, yVel);
            if (cur.x >= minTarget.x && cur.x <= maxTarget.x && cur.y >= minTarget.y && cur.y <= maxTarget.y)
                return true;
            if (xVel > 0) {
                xVel--;
            } else if (xVel < 0) {
                xVel++;
            }
            yVel--;
        } while (cur.x <= maxTarget.x && cur.y >= minTarget.y); // Assumes x-range is purely positive and y-range is purely negative.

        return false;
    }

    @Override
    protected void parse() {
        IntList ints = LineConvert.ints(lines.get(0));
        minTarget = Coordinate.of(ints.getInt(0), ints.getInt(2));
        maxTarget = Coordinate.of(ints.getInt(1), ints.getInt(3));
    }
}
