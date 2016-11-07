package G3Dsort;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;


/**
 * Represents the G3Dsort algorithm;
 * The G3Dsort algorithm determines the number of strongly connected components of a digraph and determines
 * the structure of the partial order on the quotient set of strongly connected components.
 * For this partial order, the algorithm computes the stratification.
 *
 * Input via standard in:
 *
 *      First line of the input contains a single integer n > 0 representing the number of arcs in your digraph
 *      followed by n lines, each line contains two integers x and y separated by a space representing
 *      a (directed) arc from x to y in your digraph.
 *      e.g.
 *
 *      12
 *      11 12
 *      12 13
 *      21 22
 *      22 23
 *      12 23
 *      12 22
 *      22 12
 *      31 23
 *      23 24
 *      23 34
 *      34 41
 *      41 23
 *
 *
 * Ouput via standard out:
 *
 *      The first line of the output states "DAG" or "nonDAG" depending on whether the input graph is cyclic or
 *      acyclic. DAG=Directed acyclic graph
 *
 *      The next line of the output contains a single number k, the number of strata that G3Dsort is computing.
 *
 *      This is followed by k blocks of lines;
 *      the first block represents Stratum 0, the next Stratum 1 and so on.
 *
 *      The block for Stratum i starts with an integer mi giving the number of strongly connected components
 *      on Stratum i.
 *      This is followed by mi lines; each of those lines is listing a strongly connected component in ascending order
 *      in the natural numbers, separated by a blank space.
 *      The Strongly connected components are listed in ascending order of their first element for each Stratum.
 *      This concludes the block for stratum i.
 *
 *      from the sample input; The sample output is:
 *
 *      nonDAG
 *      4
 *      3
 *      11
 *      21
 *      31
 *      1
 *      12 22
 *      2
 *      13
 *      23 34 41
 *      1
 *      24
 *
 *      NOTE: although considered 'bad practice' the Node and DirectedArc classes are contained in the same file as the
 *      public DigraphSort.java file for testing reasons not discussed.
 *
 */
public class DigraphSort {

    //Array representation of graph, where each element in the graph is a directed arc
    public static List<DirectedArc> _graph = new ArrayList<DirectedArc>();

    public static int numberOfArcs = 0;

    public static Set<Node> nodeSet = new HashSet<Node>();

    public static Map<Integer, ArrayList<ArrayList<Node>>> stratification = new HashMap<Integer, ArrayList<ArrayList<Node>>>();

    public static Node predecessorNode;

    public static boolean nonDagDetected = false;
    //---------------------------------------------------------------------------------------------------------------

    public static void main(String[] args) throws IOException {


        String[] stringArcRepresentation = new String[2];

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        numberOfArcs = Integer.parseInt(reader.readLine());

        //---------------------------------------------------------------------------------------
        //Read input, populate the graph and nodeSet with arcs and nodes respectively
        for (int i = 0; i < numberOfArcs; i++) {

            Node from = null;
            Node to = null;

            // stores booleans for if the 'from' or 'to' nodes are unique i.e haven't been seen
            Boolean fromUnique = true;
            Boolean toUnique = true;


            stringArcRepresentation = reader.readLine().split(" "); // 0th index is 'from' node, 1st index is 'to' node

            // check if 'from' node is unique
            for (Node n : nodeSet) {
                if (Integer.parseInt(stringArcRepresentation[0]) == n._name) {
                    fromUnique = false;
                    from = n;
                }
            }

            // check if 'to' node is unique
            for (Node n : nodeSet) {
                if (Integer.parseInt(stringArcRepresentation[1]) == n._name) {
                    toUnique = false;
                    to = n;
                }
            }

            //if the 'from' node is unique, i.e hasn't been seen before. Then make a new node and add it to the set
            if (fromUnique) {
                from = new Node(Integer.parseInt(stringArcRepresentation[0]));
                nodeSet.add(from);
            }

            // if the 'to' node is unique, i.e hasn't been seen before. Then make a new node and add it to the nodeSet
            if (toUnique) {
                to = new Node(Integer.parseInt(stringArcRepresentation[1]));
                nodeSet.add(to);
            }

            _graph.add(new DirectedArc(from, to));


        }

        // Input has been read in
        //-------------------------------------------------------------------------------------------------------------

        // for every node in graph attempt to find strataValue
        for (Node myNode : nodeSet) {

            if (!myNode._hasBeenCollapsed) {


                int strataValue;

                try {

                    strataValue = G2DsortMerge(myNode);



                } catch (InvalidInputException exceptionNode) {

                    // do nothing

                }
            }
        }

        // display output
        StringBuilder output = new StringBuilder();

        if(nonDagDetected){
            output.append("nonDAG\n");
        } else {
            output.append("DAG\n");
        }

        output.append("" + stratification.size() + "\n");

        for(Integer kek : stratification.keySet()){

            output.append(stratification.get(kek).size() + "\n");

            Collections.sort(stratification.get(kek), new compareListStuff());

            for(ArrayList<Node> myComponent : stratification.get(kek)) {

                for(Node node : myComponent) {
                    output.append(node._name + " ");
                }

                output.append("\n");

            }

        }

        System.out.print(output.toString());




    }// end of main brace


    public static int G2DsortMerge(Node x) throws InvalidInputException {

        // check if cycle is detected
        if (x._hasBeenVisitedFlag) {

            nonDagDetected = true;

            throw new InvalidInputException(x); // cycle has been detected

        }

        while (true) {

            try {

                if (x._z!=-1){
                    return x._z;
                } else {

                    x.setVisitedFlag(true); // the node has appeared on current walk

                    int max = -1;

                    for (int i = 0; i < _graph.size(); i++) { // loop through every arc in the graph

                        if (_graph.get(i) != null) {

                            DirectedArc arc = _graph.get(i);

                            if (arc._to.equals(x)) { // if the arc is entering x (yDx relation)

                                int tmp = G2DsortMerge(arc._from); // recursively call function on node it was from (possible fix, if z value of node != -1 then just return the z value of the arc.from node.

                                if (tmp > max) {

                                    max = tmp;

                                }

                            }
                        }

                    }

                    x.setVisitedFlag(false); // reset the flag

                    x.setZ(max + 1);

                    if (stratification.get(max+1)==null){


                        ArrayList<ArrayList<Node>> group = new ArrayList<ArrayList<Node>>();

                        ArrayList<Node> temp = new ArrayList<Node>();

                        temp.addAll(x._clientSet);

                        Collections.sort(temp);

                        group.add(temp);

                        stratification.put(max+1, group);

                    } else {

                        //ArrayList<ArrayList<G3Dsort.Node>> group = stratification.get(max+1);

                        ArrayList temp = new ArrayList<Node>();

                        temp.addAll(x._clientSet);

                        Collections.sort(temp);

                        stratification.get(max+1).add(temp);
                    }

                    return max + 1; // return the strata value
                }

            } catch (InvalidInputException exceptionNode) {

                Node xc = exceptionNode.getNode(); // get node xc from the exception thrown

                //catch block of Algorithm 4 "G2DsortMerge"
                if (!x.equals(xc)) { // if x=/=xc then

                    // merge x into xc (arcs and client sets)
                    mergeNodes(x, xc); // note myNode = 'x' in pseudocode

                    throw exceptionNode;

                } else {


                    // remove any reflexive arcs
                    removeReflectiveArcs();

                    // goto 'line 5' i.e where max = -1 by calling method with x

                }
            }
        }// while brace
    }//method brace


    /**
     * collapses a cycle in a digraph by merging two nodes together
     * @param x
     * @param xc
     */
    public static void mergeNodes(Node x, Node xc){

        List<DirectedArc> arcsIndexesToDelete = new ArrayList<DirectedArc>();
        Set<DirectedArc> arcsToAdd = new HashSet<DirectedArc>();

        int count = 0;

        for(DirectedArc arc : _graph){

            // if the arc is going to x
            if(arc._to.equals(x)){

                arcsIndexesToDelete.add(arc);

                //add a new arc that makes it going to xc
                arcsToAdd.add(new DirectedArc(arc._from, xc));

            }

            // if the arc is from x
            if (arc._from.equals(x)) {

                arcsIndexesToDelete.add(arc);

                // add a new arc that it coming from xc
                arcsToAdd.add(new DirectedArc(xc, arc._to));

            }
            count++; // stores an index

        }

        _graph.removeAll(arcsIndexesToDelete);


        for(DirectedArc arc : arcsToAdd){
            _graph.add(arc);
        }

        x.setHasBeenCollapsed(true);

        mergeClientSets(x, xc);

    }


    public static void mergeClientSets(Node x, Node xc){

        // for every node in x's client set, add it to xc's client set
        for(Node n : x._clientSet){
            xc.addToClientSet(n);
        }

    }

    /**
     * removes self arcs i.e xRx
     */
    public static void removeReflectiveArcs(){

        List<DirectedArc> reflexiveArcs = new ArrayList<DirectedArc>();

        for(DirectedArc arc : _graph){

            if(arc._to._name == arc._from._name){

                reflexiveArcs.add(arc);

            }

        }

        _graph.removeAll(reflexiveArcs);

    }



    public static void printVariables(){
        System.out.println(nodeSet.size());
        for(Node n : nodeSet){
            System.out.println(n);
        }
        System.out.println();
        System.out.println(_graph.size());
        for(DirectedArc a : _graph){
            System.out.println(a);
        }

        for(Integer kek : stratification.keySet()){

            System.out.println(stratification.get(kek));

        }
    }

} // end of G3Dsort.DigraphSort.java brace


/**
 * Node class for G3D sort
 */
class Node implements Comparable{


    int _name;
    Boolean _hasBeenVisitedFlag;
    Boolean _hasBeenCollapsed;
    int _z; // holds strata value
    List<Node> _clientSet = new ArrayList<Node>();

    @Override
    public int compareTo(Object o) {

        Node node2 = (Node)o;

        return this._name - node2._name;
    }

    public Node(int name){
        _name = name;
        _hasBeenVisitedFlag = false;
        _z = -1;
        _clientSet.add(this);
        _hasBeenCollapsed = false;
    }

    public void setVisitedFlag(Boolean newFlagValue){
        _hasBeenVisitedFlag = newFlagValue;
    }

    public void setZ(int newZ){
        _z = newZ;
    }

    @Override
    public String toString(){
        return "G3Dsort.Node " + this._name + " has Z value " + this._z + " and has been visited flag is set to " + _hasBeenVisitedFlag;
    }

    public void setHasBeenCollapsed(Boolean val){
        _hasBeenCollapsed = val;
    }

    public void addToClientSet(Node nodeToAdd){

        // if client set doesn't contain current node, add it
        if(!_clientSet.contains(nodeToAdd)){
            _clientSet.add(nodeToAdd);
        }
    }

}


/**
 * abstraction of a DirectedArc in a digraph
 */
class DirectedArc {

    Node _from;
    Node _to;

    public DirectedArc(Node from, Node to) {
        _from = from;
        _to = to;

    }

    @Override
    public String toString(){
        return _from._name + " ---> " + _to._name;
    }


}

/**
 * compares based on node integer value
 */
class NodeValueComparator implements Comparator<Node> {

    public int compare(Node node1, Node node2) {
        return node1._name - node2._name;
    }

}

/**
 * Thrown when DAG expected but cycle detected
 */
class InvalidInputException extends Exception {

    Node _x;

    public InvalidInputException(Node x){
        _x = x;
    }

    public Node getNode() {

        return _x;
    }

    public void setNode(Node newSource) {
        _x = newSource;
    }

}

/**
 * compares first element of two lists of nodes
 */
class compareListStuff implements Comparator<ArrayList<Node>> {

    @Override
    public int compare(ArrayList<Node> o1, ArrayList<Node> o2) {

        int val1 = o1.get(0)._name;
        int val2 = o2.get(0)._name;

        return val1 - val2;
    }
}






