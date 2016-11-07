  The G3Dsort algorithm determines the number of strongly connected components of a digraph and determines
  the structure of the partial order on the quotient set of strongly connected components.
  For this partial order, the algorithm computes the stratification.
 
  Input via standard in:
 
       First line of the input contains a single integer n > 0 representing the number of arcs in your digraph
       followed by n lines, each line contains two integers x and y separated by a space representing
       a (directed) arc from x to y in your digraph.
       e.g.
 
       12
       11 12
       12 13
       21 22
       22 23
       12 23
       12 22
       22 12
       31 23
       23 24
       23 34
       34 41
       41 23
 
 
  Ouput via standard out:
 
       The first line of the output states "DAG" or "nonDAG" depending on whether the input graph is cyclic or
       acyclic. DAG=Directed acyclic graph
 
       The next line of the output contains a single number k, the number of strata that G3Dsort is computing.
 
       This is followed by k blocks of lines;
       the first block represents Stratum 0, the next Stratum 1 and so on.
 
       The block for Stratum i starts with an integer mi giving the number of strongly connected components
       on Stratum i.
       This is followed by mi lines; each of those lines is listing a strongly connected component in ascending order
       in the natural numbers, separated by a blank space.
       The Strongly connected components are listed in ascending order of their first element for each Stratum.
       This concludes the block for stratum i.
 
       from the sample input; The sample output is:
 
       nonDAG
       4
       3
       11
       21
       31
       1
       12 22
       2
       13
       23 34 41
       1
       24
