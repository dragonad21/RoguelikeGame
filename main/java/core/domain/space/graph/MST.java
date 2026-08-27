package core.domain.space.graph;

import core.domain.space.Room;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MST {
    public static List<Edge> findMST(List<Room> rooms) {
        List<Edge> allPossibleEdges = generateAllEdges(rooms);
        allPossibleEdges.sort(Comparator.comparing(e -> e.distance()));

        DisjointSet disjointSet = new DisjointSet(rooms.size());
        List<Edge> mstEdges = new ArrayList<>();

        for (Edge edge : allPossibleEdges) {
            if (disjointSet.find(edge.index1()) != disjointSet.find(edge.index2())) {
                disjointSet.union(edge.index1(), edge.index2());
                mstEdges.add(edge);

                if (mstEdges.size() == rooms.size() - 1)
                    break;
            }
        }

        return mstEdges;
    }

    private static List<Edge> generateAllEdges(List<Room> rooms) {
        List<Edge> edges = new ArrayList<>();

        for (int index1 = 0; index1 < rooms.size(); index1++) {
            for (int index2 = index1 + 1; index2 < rooms.size(); index2++) {
                Room room1 = rooms.get(index1);
                Room room2 = rooms.get(index2);

                double distance = room1.distanceFromCentreTo(room2.getCentre());

                edges.add(new Edge(index1, index2, distance));
            }
        }

        return edges;
    }
}
