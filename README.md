# Gemini

## Short Description

Compare two text annotations and provide an alignment score.

## Installation

You need to :  
* clone this project on your computer, or download and extract the corresponding ZIP file (using the green "Clone or download" button above)
* download __jdom-2.0.6.zip__ (http://www.jdom.org/dist/binary/) and unzip it in a folder __jdom-2.0.6__ at the root of the project
* download __algs4.jar__ (https://github.com/kevin-wayne/algs4/blob/master/README.md) and place it at the root of the project 

## Usage

Once you've reached the folder which contains the `Main.java` file with the terminal, the command to compile is :  
`javac -cp jdom-2.0.6/jdom-2.0.6.jar;algs4.jar;. Main.java`  
  
Then to run the program :  
`java -cp jdom-2.0.6/jdom-2.0.6.jar;algs4.jar;. Main`  
  
Followed by some parameters :  
* To indicate the two files to compare (which contain annotations defined by: an annotation type, the index of the first and the last characters covered by the annotations, as well as the part of the text covered by the annotation):
   * `-bratfile1` followed by first file's name (.ann)  
   *or*
   * `-xmlfile1` followed by first file's name (.xml)  
   *and*
   * `-bratfile2` followed by second file's name (.ann)  
   *or*
   * `-xmlfile2` followed by second file's name (.xml)
* To indicate score type, please use one of the following parameters: `weakprecision`, `strictprecision`, `weightedprecision, weakrecall, strictrecall, weightedrecall`, `weakF-measure`, `strictF-measure`, `weightedF-measure`. Those parameters combine:
   * the context of evaluation (note that we always require two annotations two have the same type to be considered as corresponding annotations):
      * __weak__ means that an annotation of the first file will be considered as corresponding to an annotation of the second file if they intersect on at least one character.
      * __strict__ means that an annotation of the first file will be considered as corresponding to an annotation of the second file if they cover exactly the same characters, that is they start and end exactly at the same characters.
      * __weighted__ means that the match will be scored by the ration of the number of characters common to both annotations divided by the total number of characters covered by at least one of the two annotations.
   * the type of score being evaluated:
      * __precision__, the ratio of annotations of the second file which correspond to an annotation in the first file
      * __recall__, the ratio of annotations of the first file which correspond to an annotation in the second file
      * __F-measure__, a combination of precision and recall (https://en.wikipedia.org/wiki/Precision_and_recall)
* To indicate alignment type :
   * `multiple` (default value): annotations in the first file may correspond to several annotations in the second file, and vice versa
   * `greedy`: an annotation in the first file will correspond to at most one annotation in the second file, and vice versa, the matching of annotations in the first and second files is done with a greedy algorithm trying to match the closest annotations first (appropriate for the __weighted__ context)
   * `maxmatching`: an annotation in the first file will correspond to at most one annotation in the second file, and vice versa, the matching of annotations in the first and second file is done optimally using a maximum matching algorithm in bipartite graphs (this is an unweighted maximum matching, so appropriate in the __strict__ or __weak__ contexts, but not appropriate for the __weighted__ context)
