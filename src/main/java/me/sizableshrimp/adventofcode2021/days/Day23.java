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
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIntPair;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Value;
import me.sizableshrimp.adventofcode2021.helper.GridHelper;
import me.sizableshrimp.adventofcode2021.templates.Coordinate;
import me.sizableshrimp.adventofcode2021.templates.Direction;
import me.sizableshrimp.adventofcode2021.templates.SeparatedDay;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

// https://adventofcode.com/2021/day/23 - Amphipod
public class Day23 extends SeparatedDay {
    private static final List<String> PART_TWO_EXTENSION = List.of("  #D#C#B#A#", "  #D#B#A#C#");
    private static final IntList ROOM_COLUMNS = IntList.of(3, 5, 7, 9);
    private static final List<Coordinate> HALLWAY_SPOTS = IntStream.of(1, 2, 4, 6, 8, 10, 11).mapToObj(x -> Coordinate.of(x, 1)).toList();
    private static final List<List<Coordinate>> ROOM_LISTS = IntStream.of(3, 5, 7, 9)
            .mapToObj(x -> List.of(Coordinate.of(x, 2), Coordinate.of(x, 3), Coordinate.of(x, 4), Coordinate.of(x, 5)))
            .toList();

    public static void main(String[] args) {
        new Day23().run();
    }

    @Override
    protected Object part1() {
        return solve(this.lines);
    }

    @Override
    protected Object part2() {
        ArrayList<String> lines = new ArrayList<>(this.lines);
        lines.addAll(3, PART_TWO_EXTENSION);
        return solve(lines);
    }

    private int solve(List<String> lines) {
        boolean[][] grid = GridHelper.convertBool(lines, c -> c == '#');

        Set<Amphipod> startingAmphipods = new HashSet<>();
        for (int y = 0; y < lines.size(); y++) {
            String row = lines.get(y);
            for (int x = 0; x < row.length(); x++) {
                char c = row.charAt(x);
                if (Character.isLetter(c)) {
                    Amphipod amphipod = new Amphipod(c, Coordinate.of(x, y), Set.of());
                    startingAmphipods.add(amphipod);
                }
            }
        }

        Object2IntMap<Node> weights = new Object2IntOpenHashMap<>();
        Queue<Node> queue = new PriorityQueue<>(Comparator.comparingInt(Node::getTotal));
        // Deque<Node> queue = new ArrayDeque<>();
        queue.add(new Node(null, startingAmphipods, 0));
        Set<Node> completed = new HashSet<>();
        int min = Integer.MAX_VALUE;
        while (!queue.isEmpty()) {
            Node node = queue.poll();
            if (min != Integer.MAX_VALUE && min < node.total)
                continue;
            if (node.isComplete()) {
                if (node.total < min) {
                    completed.add(node);
                    min = node.total;
                }
                continue;
            }
            for (Amphipod amphipod : node.amphipods) {
                if (amphipod.isStuck(node.amphipods)
                        || (amphipod.isInOwnRoom() && !amphipod.isBlocking(node.amphipods))
                        || (amphipod.isInHallway() && !amphipod.canMoveIntoRoom(node.amphipods)))
                    continue;
                List<Coordinate> toCheck = new ArrayList<>(HALLWAY_SPOTS.size());
                List<Coordinate> destPoses = amphipod.getDestinationPositions();
                if (amphipod.canMoveIntoRoom(node.amphipods)) {
                    int i = 0;
                    while (i < destPoses.size() - 1 && !occupied(grid, node.amphipods, destPoses.get(i + 1))) {
                        i++;
                    }
                    if (i < destPoses.size()) {
                        toCheck.add(destPoses.get(i));
                    }
                }
                // Can't move around in the hallway once you're in it!
                if (amphipod.pos.y != 1)
                    toCheck.addAll(HALLWAY_SPOTS);
                for (Coordinate dest : toCheck) {
                    if (dest.equals(amphipod.pos))
                        continue;

                    if (!occupied(grid, node.amphipods, dest)) {
                        int distance = getDistance(grid, node.amphipods, amphipod, dest);
                        if (distance != -1) {
                            int newEnergy = distance * amphipod.getEnergy();
                            if (addNextNode(queue, destPoses, weights, node, amphipod, newEnergy, dest) && destPoses.contains(dest)) {
                                break;
                            }
                        }
                    }
                }
            }
        }

        return min;
    }

    private int getDistance(boolean[][] grid, Set<Amphipod> amphipods, Amphipod amphipod, Coordinate dest) {
        if (amphipod.pos.equals(dest))
            return 0;
        int[][] weights = new int[grid.length][grid[0].length];
        Queue<ObjectIntPair<Coordinate>> queue = new ArrayDeque<>();
        queue.add(ObjectIntPair.of(amphipod.pos, 0));
        while (!queue.isEmpty()) {
            ObjectIntPair<Coordinate> node = queue.poll();
            int newWeight = node.rightInt() + 1;
            for (Direction dir : Direction.cardinalDirections()) {
                Coordinate neighbor = node.left().resolve(dir);
                if (!neighbor.equals(amphipod.pos) && !occupied(grid, amphipods, neighbor)) {
                    int weight = weights[neighbor.y][neighbor.x];
                    if (weight == 0 || newWeight < weight) {
                        weights[neighbor.y][neighbor.x] = newWeight;
                        queue.add(ObjectIntPair.of(neighbor, newWeight));
                    }
                }
            }
        }
        int destWeight = weights[dest.y][dest.x];
        return destWeight == 0 ? -1 : destWeight;
    }

    private void print(boolean[][] grid, Set<Amphipod> amphipods) {
        Map<Coordinate, Amphipod> coords = amphipods.stream().collect(Collectors.toMap(Amphipod::getPos, Function.identity()));
        for (int y = 0; y < grid.length; y++) {
            boolean[] row = grid[y];
            for (int x = 0; x < row.length; x++) {
                if (row[x]) {
                    System.out.print('#');
                } else {
                    Coordinate coord = Coordinate.of(x, y);
                    Amphipod amp = coords.get(coord);
                    System.out.print(amp == null ? '.' : amp.type);
                }
            }
            System.out.println();
        }
    }

    private boolean addNextNode(Queue<Node> queue, List<Coordinate> destPoses, Object2IntMap<Node> weights, Node node, Amphipod amphipod, int addedEnergy, Coordinate newTarget) {
        if (amphipod.getVisited().contains(newTarget))
            return false;
        Set<Coordinate> newVisited = new HashSet<>(amphipod.getVisited());
        HashSet<Amphipod> newAmphipods = new HashSet<>(node.amphipods);

        if (!destPoses.contains(newTarget)) {
            newVisited.add(newTarget);
        }
        newAmphipods.remove(amphipod);
        newAmphipods.add(new Amphipod(amphipod.type, newTarget, newVisited));

        Node next = new Node(node, newAmphipods, node.total + addedEnergy);
        if (!weights.containsKey(next) || next.total < weights.getInt(next)) {
            weights.put(next, next.total);
            queue.add(next);
            return true;
        }

        return false;
    }

    private boolean occupied(boolean[][] grid, Set<Amphipod> amphipods, Coordinate target) {
        if (grid[target.y][target.x])
            return true;

        for (Amphipod amphipod : amphipods) {
            if (amphipod.pos.equals(target))
                return true;
        }

        return false;
    }

    private static Set<Amphipod> getAmphipodsInRoom(Set<Amphipod> amphipods, int room) {
        return amphipods.stream().filter(a -> a.isInRoom(room)).collect(Collectors.toSet());
    }

    @Value
    @AllArgsConstructor
    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    private static class Amphipod {
        private static final IntList ENERGY_AMOUNTS = IntList.of(1, 10, 100, 1000);
        @EqualsAndHashCode.Include
        char type;
        @EqualsAndHashCode.Include
        Coordinate pos;
        Set<Coordinate> visited;
        int destinationRoom;
        int currentRoom;
        int energy;

        public Amphipod(char type, Coordinate pos, Set<Coordinate> visited) {
            this(type, pos, visited, type - 'A', ROOM_COLUMNS.indexOf(pos.x), ENERGY_AMOUNTS.getInt(type - 'A'));
        }

        private boolean isInHallway() {
            return this.pos.y == 1;
        }

        private boolean isStuck(Set<Amphipod> amphipods) {
            if (this.pos.y <= 2 || this.isInOwnRoom())
                return false;

            Set<Amphipod> inCurrentRoom = getAmphipodsInRoom(amphipods, getCurrentRoom());
            for (Amphipod other : inCurrentRoom) {
                if (other == this)
                    continue;

                if (other.pos.y < this.pos.y)
                    return true;
            }

            return false;
        }

        private boolean isInOwnRoom() {
            return isInRoom(this.getDestinationRoom());
        }

        private boolean isInRoom(int room) {
            return isAlignedWithRoomVertically(room) && this.pos.y > 1;
        }

        private List<Coordinate> getDestinationPositions() {
            return ROOM_LISTS.get(this.destinationRoom);
        }

        private boolean isAlignedWithRoomVertically(int room) {
            return this.pos.x == ROOM_COLUMNS.getInt(room);
        }

        private boolean isBlocking(Set<Amphipod> amphipods) {
            if (this.pos.y == 5 || !isInOwnRoom())
                return false;
            Set<Amphipod> inMyRoom = getAmphipodsInRoom(amphipods, this.getDestinationRoom());
            return inMyRoom.stream().anyMatch(a -> a.type != this.type);
        }

        private boolean canMoveIntoRoom(Set<Amphipod> amphipods) {
            Set<Amphipod> inMyRoom = getAmphipodsInRoom(amphipods, getDestinationRoom());
            return inMyRoom.isEmpty() || inMyRoom.stream().allMatch(a -> a.type == this.type);
        }
    }

    @Value
    private static class Node {
        @EqualsAndHashCode.Exclude
        Node prev;
        Set<Amphipod> amphipods;
        @EqualsAndHashCode.Exclude
        int total;

        private boolean isComplete() {
            return amphipods.stream().allMatch(Amphipod::isInOwnRoom);
        }
    }
}
