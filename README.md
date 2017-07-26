# Gemini

## Short Description

Compare text annotations and provides an alignment score.

## Installation

You need to :  
* download __jdom-2.0.6.zip__ (http://www.jdom.org/dist/binary/) and unzip it in a folder __jdom-2.0.6__ at the root of the project
* download __algs4.jar__ (https://github.com/kevin-wayne/algs4/blob/master/README.md) and place it at the root of the project  

## Usage

The command to compile is :  
`javac -cp jdom-2.0.6/jdom-2.0.6.jar;algs4.jar;. Main.java`  
  
Then to run the program :  
`java -cp jdom-2.0.6/jdom-2.0.6.jar;algs4.jar;. Main`  
  
Followed by some parameters :  
* To indicate files to compare :
   * -bratfile1  : followed by first file's name (.ann)  
   *or*
   * -xmlfile1 : followed by first file's name (.xml)  
   *and*
   * -bratfile2  : followed by second file's name (.ann)  
   *or*
   * -xmlfile2 : followed by second file's name (.xml)
* To indicate score type :
   * weakprecision
   * strictprecision
   * weightedprecision
   * weakrecall
   * strictrecall
   * weightedrecall
   * weakF-measure
   * strictF-measure
   * weightedF-measure
* To indicate alignment type :
   * multiple (default value)
   * greedy
   * maxmatching
