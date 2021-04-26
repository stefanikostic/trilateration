import java.util.*;
import java.util.stream.Collectors;

public class SensorNetwork {

    private final Random random;
    private final int L, N;

    public SensorNetwork () {
        this.random = new Random();

        // fixed total number of nodes and fixed length of the sensor network
        this.L = 100;
        this.N = 100;
    }

    static Point findRandomCoordinatesOfNotAnchorNode (int L, List<Point> nodes, Random random) {
        double x = random.nextInt(L + 1);
        double y = random.nextInt(L + 1);
        Point node = new Point(x, y);
        if (nodes.contains(node)) {
            node = findRandomCoordinatesOfNotAnchorNode(L, nodes, random);
        }
        return node;
    }

    public List<Point> fillAnchorNodes (List<Point> nodes, int anchorNodesCount) {
        List<Point> anchorNodes = new ArrayList<>();

        // fill the anchor nodes
        for (int i = 0; i < anchorNodesCount; i++) {
            Point anchorNode = findRandomCoordinatesOfNotAnchorNode(L, nodes, random);
            anchorNode.anchorFlag = true;
            nodes.add(anchorNode);
            anchorNodes.add(anchorNode);
        }

        return anchorNodes;
    }

    public List<Point> fillNodesThatShouldBeFound (List<Point> nodes, int seekedNodesCount,
                                                   List<Point> anchorNodes, int R, int r) {
        List<Point> seekedNodes = new ArrayList<>();

        // fill the nodes that should be found
        for (int i = 0; i < seekedNodesCount; i++) {
            Point seekedNode = findRandomCoordinatesOfNotAnchorNode(L, nodes, random);
            seekedNode.anchorFlag = false;
            nodes.add(seekedNode);
            findAnchorNeighbours(seekedNode, anchorNodes, R, r);
            seekedNodes.add(seekedNode);
        }

        return seekedNodes;
    }

    public void generateNetwork (CreateExcelFile excelFileFractionLocatedNodes,
                                 CreateExcelFile excelFileErrorLocalization,
                                 boolean iterative, int heuristic) {
        int r, f = 20, radioRangePercentage = 20;

        while (radioRangePercentage <= 50) {
            int[] seekedNodesCountArray = new int[15];
            int[] anchorNodesCountArray = new int[15];
            int[] totalNodesCountArray = new int[15];

            List<Double> totalErrorsLocalization = new ArrayList<>();

            // noise - range is -10% to 10% of the distance
            r = random.nextInt(11 + 10) - 10;

            for (int k = 0; k < 15; k++) {
                // gets the radio range of the network with given length L.
                int R = (radioRangePercentage * L) / 100;

                totalNodesCountArray[k] = N;

                List<Point> nodes = new ArrayList<>();

                int anchorNodesCount = (N * f) / 100;
                anchorNodesCountArray[k] = anchorNodesCount;
                List<Point> anchorNodes = fillAnchorNodes(nodes, anchorNodesCount);

                int seekedNodesCount = N - anchorNodesCount;
                List<Point> seekedNodes = fillNodesThatShouldBeFound(nodes, seekedNodesCount, anchorNodes, R, r);

                if (iterative) {
                    iterativeAlgorithmTrilateration(seekedNodes, anchorNodes, R, seekedNodesCountArray,
                            totalErrorsLocalization, k, r, heuristic);
                } else {
                    nonIterativeAlgorithmTrilateration(seekedNodes, anchorNodes, R,
                            seekedNodesCountArray, totalErrorsLocalization, k, r);
                }
            }

            // fraction of located nodes
            double fractionFound = findFractionOfLocatedNodes(totalNodesCountArray, anchorNodesCountArray,
                    seekedNodesCountArray);
            System.out.println("Average of found nodes from different topologies: " + fractionFound);

            excelFileFractionLocatedNodes.addRowForErrorLocalization(radioRangePercentage, f, fractionFound);

            // average localization error
            double errorsTotal = totalErrorsLocalization.stream()
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(0);

            excelFileErrorLocalization.addRowForErrorLocalization(radioRangePercentage, f, errorsTotal, r);

            // increase by step 5
            f += 5;
            if (f > 40) {
                // increase by step 5
                radioRangePercentage += 5;
                f = 20;
            }
        }
    }

    public void findAnchorNeighbours (Point seekedNode, List<Point> anchorNodes, int R, int r) {
        List<Point> anchorNeighbours = new ArrayList<>();
        for (Point anchorNode : anchorNodes) {
            double distance = Distance(
                    anchorNode.x, anchorNode.y,
                    seekedNode.x, seekedNode.y);

            // calculates the noise based on the radio range error percentage
            double noise = r * distance / 100;
            distance += noise;
            if (distance <= R) {
                anchorNode.r = distance;
                anchorNeighbours.add(anchorNode);
            }
        }
        seekedNode.anchors = anchorNeighbours;
    }

    public double findFractionOfLocatedNodes (int[] totalNodesCountArray, int[] anchorNodesCountArray,
                                              int[] seekedNodesCountArray) {
        int totalNodesCount = Arrays.stream(totalNodesCountArray).sum();
        int totalAnchorNodesCount = Arrays.stream(anchorNodesCountArray).sum();
        int totalFoundNodes = Arrays.stream(seekedNodesCountArray).sum();

        // fraction of located nodes
        double fractionFound = (double) totalFoundNodes / (totalNodesCount - totalAnchorNodesCount) * 100;
        return fractionFound;
    }

    public void nonIterativeAlgorithmTrilateration (List<Point> seekedNodes, List<Point> anchorNodes, int R,
                                                    int[] seekedNodesCountArray, List<Double> totalErrorsLocalization,
                                                    int k, int r) {
        List<Double> errors = new ArrayList<>();
        List<Point> foundNodes = new ArrayList<>();

        for (Point seekedNode : seekedNodes) {
            if (!seekedNode.anchorFlag) {
                // fills the distances of the current node and every anchor node
                // gets the total number of neighbours
                List<Point> closestAnchorNeighbours = findThreeClosestNeighbours(seekedNode.anchors);

                findTrilateratedPoint(seekedNode, closestAnchorNeighbours, anchorNodes, foundNodes, errors,
                        false, 0);
            }
        }
        calculateErrorAndPrintOutput(errors, k, foundNodes, R, totalErrorsLocalization,
                seekedNodes, seekedNodesCountArray);
    }

    public void calculateErrorAndPrintOutput (List<Double> errors, int k, List<Point> foundNodes, int R,
                                              List<Double> totalErrorsLocalization, List<Point> seekedNodes,
                                              int[] seekedNodesCountArray) {
        double avgErrors = errors.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0);

        System.out.println("Average error: " + String.format("%.2f", avgErrors));

        double errorPercent = avgErrors * 100 / R;

        totalErrorsLocalization.add(errorPercent);
        seekedNodesCountArray[k] = foundNodes.size();
        printOutputForEachIteration(foundNodes, seekedNodes, errors, R);
    }

    public void iterativeAlgorithmTrilateration (List<Point> seekedNodes, List<Point> anchorNodes, int R,
                                                 int[] seekedNodesCountArray, List<Double> totalErrorsLocalization,
                                                 int k, int r, int heuristic) {
        List<Double> errors = new ArrayList<>();
        List<Point> foundNodes = new ArrayList<>();

        if (heuristic == 2) {
            seekedNodes.sort(Comparator.comparing(Point::getAnchorsSize).reversed());
        }

        for (Point seekedNode : seekedNodes) {

            if (!seekedNode.anchorFlag) {
                // fills the distances of the current node and every anchor node
                // gets the total number of neighbours
                findAnchorNeighbours(seekedNode, anchorNodes, R, r);
                if (heuristic == 1) {
                    List<Point> closestAnchorNeighbours = findThreeClosestNeighbours(seekedNode.anchors);
                    findTrilateratedPoint(seekedNode, closestAnchorNeighbours, anchorNodes, foundNodes, errors,
                            true, 1);
                } else if (heuristic == 2) {
                    List<Point> mostRelevantAnchorNeighbours = findThreeMostRelevantNeighbours(seekedNode.anchors);
                    findTrilateratedPoint(seekedNode, mostRelevantAnchorNeighbours, anchorNodes, foundNodes,
                            errors, true, 2);
                }
            }
        }

        calculateErrorAndPrintOutput(errors, k, foundNodes, R, totalErrorsLocalization,
                seekedNodes, seekedNodesCountArray);
        System.out.println("Size of anchor nodes: " + anchorNodes.size());
    }

    public void findTrilateratedPoint (Point seekedNode, List<Point> finalAnchorNeighbours, List<Point> anchorNodes,
                                       List<Point> foundNodes, List<Double> errors, boolean iterative,
                                       int heuristic) {
        if (finalAnchorNeighbours.size() == 3) {
            Point p1 = finalAnchorNeighbours.get(0);

            Point p2 = finalAnchorNeighbours.get(1);

            Point p3 = finalAnchorNeighbours.get(2);

            if (p1 != null && p2 != null && p3 != null) {
                // if all the distances are smaller than the radio range
                // and the anchor nodes were found (are not null)
                // make trilateration to find the seeked point
                Point p = Trilaterate(p1, p2, p3);

                if (p.x <= L && p.x >= 0 && p.y <= L && p.y >= 0) {
                    // the found point has coordinates within the borders of the sensor network
                    if (!Double.isNaN(p.x) && !Double.isNaN(p.y)) {
                        foundNodes.add(p);
                        // calculate the distance between the real node and the estimated one.
                        errors.add(Distance(p.x, p.y, seekedNode.x, seekedNode.y));

                        if (iterative) {
                            // iterative trilateration
                            anchorNodes.add(p);
                            seekedNode.anchorFlag = true;
                            if (heuristic == 2) {
                                seekedNode.stepen = p1.stepen + p2.stepen + p3.stepen + 1;
                            }
                        }
                    }
                }
            }
        }
    }

    public List<Point> findThreeClosestNeighbours (List<Point> anchorNeighbours) {
        anchorNeighbours.sort(Comparator.comparing(Point::getR));
        List<Point> threeClosestNeighbours = anchorNeighbours.stream()
                .limit(3)
                .collect(Collectors.toList());
        return threeClosestNeighbours;
    }

    public List<Point> findThreeMostRelevantNeighbours (List<Point> anchorNeighbours) {
        anchorNeighbours.sort(
                Comparator.comparing(Point::getStepen)
                        .thenComparing(Point::getR));

        List<Point> threeMostRelevantNeighbours = anchorNeighbours.stream()
                .limit(3)
                .collect(Collectors.toList());

        return threeMostRelevantNeighbours;
    }

    public void printOutputForEachIteration (List<Point> foundNodes, List<Point> seekedNodes,
                                             List<Double> errors, int R) {
        System.out.println("Original: " + "\t" + "Found: ");

        for (int i = 0; i < foundNodes.size(); i++) {
            System.out.println("X: " + String.format("%.1f", seekedNodes.get(i).x) + "\t" +
                    "Y: " + String.format("%.1f", seekedNodes.get(i).y) + "\t" +
                    "X: " + String.format("%.1f", foundNodes.get(i).x) + "\t" +
                    "Y: " + String.format("%.1f", foundNodes.get(i).y));
        }

        System.out.println("Size of seeked nodes: " + seekedNodes.size());
        System.out.println("Size of nodes that were found: " + foundNodes.size());

        double avgErrors = errors.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0);

        System.out.println("Average error: " + String.format("%.2f", avgErrors));

        double errorPercent = avgErrors * 100 / R;

        System.out.println("Error from the radio range is " + String.format("%.2f", errorPercent));
    }

    public static double Distance (double x1, double y1, double x2, double y2) {
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    public static double sqr (double a) {
        return a * a;
    }

    public static double norm (Point a) {
        return Math.sqrt(sqr(a.x) + sqr(a.y));
    }

    public static double dot (Point a, Point b) {
        return a.x * b.x + a.y * b.y;
    }

    public static Point vector_subtract (Point a, Point b) {
        double x = a.x - b.x;
        double y = a.y - b.y;
        Point p = new Point(x, y);
        return p;
    }

    public static Point vector_add (Point a, Point b) {
        double x = a.x + b.x;
        double y = a.y + b.y;
        Point p = new Point(x, y);
        return p;
    }

    public static Point vector_divide (Point a, double b) {
        double x = a.x / b;
        double y = a.y / b;
        Point p = new Point(x, y);
        return p;
    }

    public static Point vector_multiply (Point a, double b) {
        double x = a.x * b;
        double y = a.y * b;
        Point p = new Point(x, y);
        return p;
    }

    public static Point Trilaterate (Point p1, Point p2, Point p3) {
        Point ex = vector_divide(vector_subtract(p2, p1), norm(vector_subtract(p2, p1)));
        double i = dot(ex, vector_subtract(p3, p1));
        Point a = vector_subtract(vector_subtract(p3, p1), vector_multiply(ex, i));
        Point ey = vector_divide(a, norm(a));
        double d = norm(vector_subtract(p2, p1));
        double j = dot(ey, vector_subtract(p3, p1));

        double x = (sqr(p1.r) - sqr(p2.r) + sqr(d)) / (2 * d);
        double y = (sqr(p1.r) - sqr(p3.r) + sqr(i) + sqr(j)) / (2 * j) - (i / j) * x;

        Point result = vector_add(p1, vector_add(vector_multiply(ex, x), vector_multiply(ey, y)));

        return result;
    }
}
