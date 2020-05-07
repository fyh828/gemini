# Gemini

## Short Description

Compare two text annotations and provide an alignment score.
<p align="center">
<img src="https://github.com/fyh828/gemini/blob/master/demo/vis1.png" width="806" height="350"></br>
</p><p align="center">
<img src="https://github.com/fyh828/gemini/blob/master/demo/vis2.png" width="806" height="350"></br>
</p><p align="center">
<img src="https://github.com/fyh828/gemini/blob/master/demo/RESULTAT_GLOB.png" width="428" height="585"></br>
</p><p align="center">
<img src="https://github.com/fyh828/gemini/blob/master/demo/csv1.png" width="638" height="613"></br>
</p>

## Installation

You need to :  
* clone this project on your computer, or download and extract the corresponding ZIP file (using the green "Clone or download" button above)
* install Maven (https://maven.apache.org/install.html)

## Usage

Once you've reached the folder which contains the `pom.xml` file with the terminal, the command to compile and run at the same time is :  
`mvn -q clean compile exec:java -Dexec.mainClass="gemini.Main" -Dexec.args="[choose_your_argument_here]"`

Alternativily, if you are only looking to compile once Gemini, you can do:

`mvn clean compile assembly:single`

A new file named `gemini-X.X.X-SNAPSHOT-jar-with-dependencies.jar` will be placed under the `target` directory.

You can rename this file as ``gemini.jar`` and move it to your preferred location.

To execute `gemini.jar` you must type :

`java -cp gemini.jar gemini.Main`
  
Followed by some parameters :  
* To indicate the two files to compare (which contain annotations defined by: an annotation type, the index of the first and the last characters covered by the annotations, as well as the part of the text covered by the annotation):
   * `-bratfile1` followed by first file's name (.ann)  
   *or*
   * `-xmlfile1` followed by first file's name (.xml) 
   *and*
   * `-bratfile2` followed by second file's name (.ann)  
   *or*
   * `-xmlfile2` followed by second file's name (.xml) 
* To indicate the chosen score type to evaluate the correspondence between one annotation from the first file and one annotation from the second file, please use one of the following options: `weakprecision`, `strictprecision`, `weightedprecision`, `weakrecall`, `strictrecall`, `weightedrecall`, `weakF-measure`, `strictF-measure`, `weightedF-measure`
   * __weak__ means that an annotation of the first file will be considered as corresponding to an annotation of the second file if they intersect on at least one character.
   * __strict__ means that an annotation of the first file will be considered as corresponding to an annotation of the second file if they cover exactly the same characters, that is they start and end exactly at the same characters.
   * __weighted__ (by default) means that the match will be scored by the ration of the number of characters common to both annotations divided by the total number of characters covered by at least one of the two annotations.
   * __precision__, the ratio of annotations of the second file which correspond to an annotation in the first file
   * __recall__, the ratio of annotations of the first file which correspond to an annotation in the second file
   * __F-measure__, a combination of precision and recall (https://en.wikipedia.org/wiki/Precision_and_recall)
* To indicate how this score should be weighted when the two annotations do not have the same label, please use the parameter matchingTypeScore, followed by one of the following options:
   * `strictTypeMatching` (default value): to keep the score unchanged if both annotation labels are the same, and set it to 0 if they have different annotation labels.
   * `weightedTypeMatching` : matches two annotations only if they have exactly the same type (currently option by default, no other available)
* To indicate alignment type:
   * `greedyMatching` : an annotation in the first file will correspond to at most one annotation in the second file, and vice versa, the matching of annotations in the first and second files is done with a greedy algorithm trying to match the closest annotations first
   * `maxMatching` (default value): an annotation in the first file will correspond to at most one annotation in the second file, and vice versa, the matching of annotations in the first and second file is done optimally using a maximum matching algorithm in bipartite graphs
* To generate a CSV file which contains the result:  
   * `-CSV` : the CSV file will be created at the current folder.
* To compare annotations by each type:  
   * `-type` : This option will calculate the precision, recall and F-measure for each type. [Can't use together with option -CSV, haven't finished yet]
* To compare one annotation between two files:  
   * `-visualize=[TYPE_YOU_WANT_TO_COMAPRE]` : Result will be highlighted and be stored in a HTML file at the current folder.
* If you use one XML file and one brat file, and if your brat file uses '\r\n' as the line separator in counting index, you need to compile with the parameter `-CRLF`. When building a XML file, the XML parse library normalize all '\r\n' in contents to '\n', so there is a offset in two annotation files. This option allows to calculate index with this offset. You can also use the latest function "repair" to do the same thing.
* `-TEI` : The command to compare two TEI(Text Encoding Initiative) files is:
   `Main -TEI [First_TEI_File] [Second_TEI_File] [Type_to_compare] [Attribute1_to_compare(optional)] [Attribute2_to_compare(optional)] ...`  
   * You can add `-visualize=` before an attribute to generate a HTML file with result for this attribute.
   * `-TEI` should be put at the first place of all parameters. In addition, you cannot use other parameter mentioned above when you use this parameter.
* If you want to generate a XML file from a Brat File and its corresponding text file, use:
   `Main -XML [Path to Origin text file] [Path to Brat file]`
* If two origin texts are not exactly the same, you can correct the one text according to the other text by using the command below:
   `Main -repair [-mode] [Path to the hypothesis XML file] [Path to the reference XML file]`
   For each sentence who doesn't match, we use the algorithm of Levenshtein distance to modify the hypothesis text according to the reference text. 
   You can should the mode parameters from following two options:
   * `-part` (by default) : The text is separated by sentences. This means if there is a misalignment at the sentences level or above, this may not work well.
   * `-all` : Use the algorithm to the whole text. This costs a huge amount of memories.
