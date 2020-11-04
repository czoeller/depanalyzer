# depanalyzer
This project analyzes a dependency graph of a maven pom file. The dependency graph is enriched with information from SpotBugs, Dependency Checker & JDepend. The information is visualized in an interactive dependency graph with found issues in a JavaFX Desktop Application.
![demo](https://user-images.githubusercontent.com/2690708/98106849-f984ed80-1e99-11eb-8caa-4ca7a9d9b44b.png)

[Demo (8MB)](https://user-images.githubusercontent.com/2690708/98107287-99427b80-1e9a-11eb-80d4-7df582214938.gif)

## Usage
The is a properties file called `app.properties` in the root of the project. Configure a project to analyze by setting the path of `targetPomFile` to a pom file.
The distributed properties file uses pom.xml and the `depenalyzer` project is analyzed therefore.
```
targetPomFile=pom.xml
```
After this 
- open the project in a proper IDE like IntelliJ IDEA and find the main-Method in the `ui` module `de/czoeller/depanalyzer/ui/Application#main(String[])`.
- or execute `mvn exec:java` (make sure mvn uses proper Java version (see Requirements))

By running the application the following steps are executed:
1. The referenced project with it's submodules is built and installed with all dependencies to your local maven repository.
2. The maven build-reactor for this build is analyzed with the build-reactor build order of the submodules and the dependency graph.
3. The dependency graph from maven is mapped to an internal graph. During this step a `.dot` file and a `.png` is created in the built project in the `target` directory (these files are not used for further processing but are helpful anyway).
4. The dependency graph is enriched with information from the maven model (scope, relations (`PARENT`, `INCLUDED` etc.) etc.).
5. The dependency graph is enriched with analyzer information by running individual analyzers like SpotBugs, Dependency Checker, JDepend.
6. The final dependency graph is serialized to `results.dar` in the project root.
7. The GUI is started and the dependency graph is visualized by deserializing the graph from the `results.dar`.

A repeated start of the application will use the `results.dar` unless it's not present or the `targetPomFile` property was changed.
Please note that the serialized `results.dar` is not intended to be shared neither has any compatibility warranties because it is bound to the classes interfaces at time of file creation.

## Requirements
- Java, developed and tested with 1.8.
- Graphviz must be installed (for dot)
- Symlinks must be allowed for the user (On Windows: Disable UAC or follow: Local Security Policies for User Rights Assignment, Security Setting for Create symbolic links.  Open Control Panel->Administrative Tools and open Local Security Policy. From there, open Local Policies->User Rights Assignment->Create Symbolic Links -> Restart Computer.)

Please note that later versions than Java 1.8 do not come with JavaFX modules. Make sure to install them before.
