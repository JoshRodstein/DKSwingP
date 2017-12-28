# DKSwingP
**Solution to Diamond Kinetics problem set using Java Predicates**<br />
 1.Implement data structure for sample swing data in 'latestSwing.csv'<br /><br />
 2.Implement 4 methods for searching continuous values
 3.Modify previous solution in DKSwing using Java predicates to improve code re-use. 

# Description:
**SwingTable.java-** <br />
  ArrayList<SwingSample> backed ADT. 
  *did away with parrellel indexMap.
   
**SwingSample.java**  <br />
  Object representing 1 sample (row) of 7 data points (in order)<br />
      - Timestamp, ax, ay, az, wx, wy, wz <br />
      - Parameters are of inner enum class, matching 7 data point labels<br />
      - *Added 'int index' data member to preserve ref to sequential order through filtering
      
**IndexPair.java** <br /> 
  Object represents starting and ending index of continuous runs of values. 
  Allows for return of multiple indices. 
  
**SamplePredicates.java** <br />
  Predicate class containing 2 static Predicates and a static filtering method.
  Returns an ArrayList of IndexPair objects representing all contiguous runs of
  valid samples. 
   
     **filterData()**
        uses *Java.util.stream* and passed predicate param to populate a list with all samples meeting 
        the search criteria. Filtered data is then iterated over to locate runs of adjacency matching
        the desired length. Runs are stored as IndexPairs and returned as an ArrayList. 
   
     
# Notes 
  -Currently SwingTable indices start at 1 to mirror logical indexing of csv spreadsheet.<br /><br />
  -csv file contains proprietary data, not included in public repo.  
  

