# Gemini

## Short Description

Compare two text annotations and provide an alignment score.

## Installation

You need to :  
* clone this project on your computer, or download and extract the corresponding ZIP file (using the green "Clone or download" button above)
* download __jdom-2.0.6.zip__ (http://www.jdom.org/dist/binary/) and unzip it in a folder __jdom-2.0.6__ at the root of the project
* download __jgrapht-core-1.0.1.jar__ (https://mvnrepository.com/artifact/org.jgrapht/jgrapht-core/1.0.1) and place it at the root of the project 

## Usage

Once you've reached the folder which contains the `Main.java` file with the terminal, the command to compile is :  
`javac -cp jdom-2.0.6/jdom-2.0.6.jar;jgrapht-core-1.0.1.jar;. Main.java`
  
Then to run the program :  
`java -cp jdom-2.0.6/jdom-2.0.6.jar;jgrapht-core-1.0.1.jar;. Main`
  
Followed by some parameters :  
* To indicate the two files to compare (which contain annotations defined by: an annotation type, the index of the first and the last characters covered by the annotations, as well as the part of the text covered by the annotation):
   * `-bratfile1` followed by first file's name (.ann)  
   *or*
   * `-xmlfile1` followed by first file's name (.xml) [but currently this option does not work if the same piece of text between tags can be found twice in the text: it should NOT be used!]
   *and*
   * `-bratfile2` followed by second file's name (.ann)  
   *or*
   * `-xmlfile2` followed by second file's name (.xml) [but currently this option does not work if the same piece of text between tags can be found twice in the text: it should NOT be used!]
* To indicate the chosen score type to evaluate the correspondence between one annotation from the first file and one annotation from the second file, please use one of the following options: `weakprecision`, `strictprecision`, `weightedprecision`, `weakrecall`, `strictrecall`, `weightedrecall`, `weakF-measure`, `strictF-measure`, `weightedF-measure`
   * __weak__ means that an annotation of the first file will be considered as corresponding to an annotation of the second file if they intersect on at least one character.
   * __strict__ means that an annotation of the first file will be considered as corresponding to an annotation of the second file if they cover exactly the same characters, that is they start and end exactly at the same characters.
   * __weighted__ (by default) means that the match will be scored by the ration of the number of characters common to both annotations divided by the total number of characters covered by at least one of the two annotations.
   * __precision__, the ratio of annotations of the second file which correspond to an annotation in the first file
   * __recall__, the ratio of annotations of the first file which correspond to an annotation in the second file
   * __F-measure__, a combination of precision and recall (https://en.wikipedia.org/wiki/Precision_and_recall)
* To indicate how this score should be weighted when the two annotations do not have the same label, please use the parameter matchingTypeScore, followed by one of the following options:
   * `strictTypeMatching`: to keep the score unchanged if both annotation labels are the same, and set it to 0 if they have different annotation labels.
   * `weightedTypeMatching` (not properly working yet, has to be debugged...): matches two annotations only if they have exactly the same type (currently option by default, no other available)
* To indicate alignment type:
   * `greedyMatching` (default value): an annotation in the first file will correspond to at most one annotation in the second file, and vice versa, the matching of annotations in the first and second files is done with a greedy algorithm trying to match the closest annotations first
   * `maxMatching` (not working yet, has to be debugged...): an annotation in the first file will correspond to at most one annotation in the second file, and vice versa, the matching of annotations in the first and second file is done optimally using a maximum matching algorithm in bipartite graphs
