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
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import me.sizableshrimp.adventofcode2021.helper.LineConvert;
import me.sizableshrimp.adventofcode2021.templates.Day;
import me.sizableshrimp.adventofcode2021.templates.SeparatedDay;
import me.sizableshrimp.adventofcode2021.templates.ZCoordinate;

import java.util.ArrayList;
import java.util.List;

public class Day22 extends SeparatedDay {
    private static final Cuboid PART_1_REGION = new Cuboid(ZCoordinate.of(-50, -50, -50), ZCoordinate.of(50, 50, 50), false);
    private List<Cuboid> part1Cuboids;
    private List<Cuboid> cuboids;

    public static void main(String[] args) {
        new Day22().run();
    }

    @Override
    protected Object part1() {
        return calculateCubesOn(part1Cuboids);
    }

    @Override
    protected Object part2() {
        return calculateCubesOn(cuboids);
    }

    private long calculateCubesOn(List<Cuboid> cuboids) {
        Object2LongOpenHashMap<Cuboid> totals = new Object2LongOpenHashMap<>();

        Object2LongOpenHashMap<Cuboid> sub = new Object2LongOpenHashMap<>();
        for (Cuboid cuboid : cuboids) {
            sub.clear();

            for (Object2LongMap.Entry<Cuboid> entry : totals.object2LongEntrySet()) {
                Cuboid other = entry.getKey();
                Cuboid overlap = other.getOverlapCuboid(cuboid, false);
                if (overlap != null) {
                    sub.addTo(overlap, -entry.getLongValue());
                }
            }

            if (cuboid.on)
                sub.addTo(cuboid, 1);

            for (Object2LongMap.Entry<Cuboid> entry : sub.object2LongEntrySet()) {
                totals.addTo(entry.getKey(), entry.getLongValue());
            }
        }

        long total = 0;

        for (Object2LongMap.Entry<Cuboid> entry : totals.object2LongEntrySet()) {
            total += entry.getKey().getSize() * entry.getLongValue();
        }

        return total;
    }

    @Override
    protected void parse() {
        cuboids = new ArrayList<>(lines.size());
        part1Cuboids = new ArrayList<>(lines.size());

        for (String line : lines) {
            IntList ints = LineConvert.ints(line);
            boolean turnOn = line.startsWith("on");

            Cuboid cuboid = new Cuboid(ZCoordinate.of(ints.getInt(0), ints.getInt(2), ints.getInt(4)), ZCoordinate.of(ints.getInt(1), ints.getInt(3), ints.getInt(5)), turnOn);
            cuboids.add(cuboid);

            Cuboid clamped = PART_1_REGION.getOverlapCuboid(cuboid, cuboid.on);
            if (clamped != null)
                part1Cuboids.add(clamped);
        }
    }

    private record Cuboid(ZCoordinate start, ZCoordinate end, boolean on) {
        private long getSize() {
            return (end.x - start.x + 1L) * (end.y - start.y + 1L) * (end.z - start.z + 1L);
        }

        private Cuboid getOverlapCuboid(Cuboid other, boolean on) {
            int startX = Math.max(this.start.x, other.start.x);
            int startY = Math.max(this.start.y, other.start.y);
            int startZ = Math.max(this.start.z, other.start.z);
            int endX = Math.min(this.end.x, other.end.x);
            int endY = Math.min(this.end.y, other.end.y);
            int endZ = Math.min(this.end.z, other.end.z);

            if (startX <= endX && startY <= endY && startZ <= endZ) {
                return new Cuboid(ZCoordinate.of(startX, startY, startZ), ZCoordinate.of(endX, endY, endZ), on);
            } else {
                return null;
            }
        }
    }
}
