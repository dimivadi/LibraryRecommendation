# LibraryRecommendation

## Library Bootstrap for Java and Android libraries, using keywords describing your next project.

This recommendation system utilizes knowledge derived from dependencies of many java and android projects. 
This knowledge is captured on a graph data structure. Given the user input, that comprises keywords describing a project, 
system outputs a set of useful libraries so that the user can bootstrap the project.<br/>
Follow in-app instructions to set suitable parameters. You can choose PageRank variations, change the damping factor, etc<br/>
Also, system can be used, via evaluate command, to evaluate its efficiency on user prefered datasets. A new dataset needs an appropriate class, that implements EvaluationDataSource intefcace, in order to bring data to an appropriate format. Evaluation mode outputs a set of evaluation metrics such as AUC, f1score etc. 

## Installation instructions
Currently system is used as a stand-alone java application. 

In order to install the app, 
1. Download and compile source code 
   (Note: it is advised to set explicitly the entry point at src/main/java/init/MainClass.java)
2. Run the generated .jar file, providing up to 4GB of heap size, for better performance, using parameters -Xms4096 -Xmx4096
