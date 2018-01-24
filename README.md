# Google HashCode utils
[![Download](https://api.bintray.com/packages/joffrey-bion/maven/hashcode-utils/images/download.svg)](https://bintray.com/joffrey-bion/maven/hashcode-utils/_latestVersion)
[![Build Status](https://travis-ci.org/joffrey-bion/hashcode-utils.svg?branch=master)](https://travis-ci.org/joffrey-bion/hashcode-utils)

This library provides useful tools to make your life easier when competing in the Google Hash Code:
- **HCParser**: an easily configured input parser which maps the input file to your classes representing the problem
- **HCSolver**: a class that takes care of all the boilerplate file I/O code, and just needs a function that actually solves the problem
- **HCRunner**: a tiny framework that takes care of solving each input file in a separate thread with proper exception logging

## HCParser

HCParser allows you to describe how to map the input file to your classes and it'll take care of the actual parsing for 
you. It also provides nice error handling with line numbers, which saves a lot of time.

#### Basic usage example

Imagine a simple input file giving you some points, and a number of clusters to find:

```
3 2      // 3 points, 2 clusters to find
1.2 4.2  // point 0: x=1.2 y=4.2
1.5 3.4  // point 1: x=1.5 y=3.4
6.8 2.2  // point 2: x=6.8 y=2.2
```

Now let's assume you design the following classes to represent this data:

```java
public class Point {
    public double x;
    public double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }
}

public class Problem {
    public final int nClusters;
    private List<Point> points;
	
    public Problem(int nClusters) {
        this.nClusters = nClusters;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }
}
```

Here's how you would write the parser with `HCParser`:

```java
public class Main {
    
    public static void main(String[] args) {
        String filename = args[0];
        ObjectReader<Problem> rootReader = problemReader();
        HCParser<Problem> parser = new HCParser<>(rootReader);
        Problem problem = parser.parse(filename);

        // do something with the problem
    }

    private static ObjectReader<Problem> problemReader() {
        // full custom reader using Context
        ObjectReader<Point> pointReader = (Context ctx) -> {
            double x = ctx.readDouble();
            double y = ctx.readDouble();
            return new Point(x, y);
        };

        // reader using the fluent API
        return HCReader.withVars("P", "C") // reads the 2 first tokens into variables P and C
                       .createFromVar(Problem::new, "C") // creates a new Problem using the value of C as parameter
                       .thenList(Problem::setPoints, "P", pointReader); // reads P elements using the pointReader
    }   
}
```

#### Let's break this down

Basically, creating an `HCParser` boils down to configuring a root `ObjectReader`.

Object readers are components that can read as much input as necessary to build a specific type of object. They can be 
composed together to form more complex object readers.

Here, we first create an `ObjectReader<Point>` to be able to read `Point`s from the input. Then we use it to configure 
the root reader, because we need to read a list of points.
 
- `withVars` allows to read some tokens from the input and store them in variables before creating the object
- `createFromVar` creates an `ObjectReader` that instantiate a new object using variable values as constructor parameters
- `thenList` augments the existing reader so that it then reads a list of points and sets it on the created `Problem` object
  - `Problem::setPoints` provides a way to set the created list on the `Problem` object we're creating
  - `"P"` gives the number of `Point`s we should read (in the form of a context variable that was set earlier)
  - `pointReader` provides a reader to use for each element of the list

It might look over-complicated for a simple example like that, but when the input gets more complex, it can be pretty 
useful. There are plenty of other useful configurations to make the parsing easy and powerful. You may read more about 
them directly in HCReader's ObjectReader's Javadoc.
 
## HCSolver

HCSolver takes care of the file I/O for you, so that you just have to write the code that actually solves the problem.

#### Basic usage example

Using the same example problem, here is how we use HCSolver:
```java
public class BasicExample {

    public static void main(String[] args) {
        String filename = args[0];
        ObjectReader<Problem> rootReader = problemReader(); // omitted for brevity, see previous section for this
        HCParser<Problem> parser = new HCParser<>(rootReader);
        HCSolver<Problem> solver = new HCSolver<>(parser, BasicExample::solve);

        // reads the given input file and writes lines to an output file
        // the name of the output file is calculated from the input file
        solver.accept(args[0]);
    }

    private static List<String> solve(Problem problem) {
        // solve the problem

        // write solution into lines (this is problem-specific)
        List<String> lines = new ArrayList<>();
        lines.add("output line 0");
        lines.add("output line 1");
        return lines;
    }
}
```

Note that `HCSolver` implements `Consumer<String>` (it consumes input file names), which makes it nicely compatible with
`HCRunner`.

## HCRunner

HCRunner allows you to run your solver on all input files at the same time.
It is really just a way of executing in parallel multiple `Consumer<String>`, each receiving one file name.
Potential exceptions may even be logged instead of being swallowed by the execution framework.

#### Basic usage example

```java
public class Main {
    
    public static void main(String[] args) {
        Consumer<String> solver = s -> System.out.println("I solved input " + s + "!");
        HCRunner<String> runner = new HCRunner<>(solver, UncaughtExceptionsPolicy.LOG_ON_SLF4J);
        runner.run(args);
    }    
}
```

## All the pieces together

As you can see, the combination of all 3 components allows you to focus on problem-specific code only:

```java
public class Point {
    private double x;
    private double y;

    Point(double x, double y) {
        this.x = x;
        this.y = y;
    }
}

public class Problem {
    private final int nClusters;
    private List<Point> points;

    Problem(int nClusters) {
        this.nClusters = nClusters;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }

    public List<String> solve() {
	
        // solve the problem here

        // write solution into lines (this is problem-specific)
        List<String> lines = new ArrayList<>();
        lines.add("output line 0");
        lines.add("output line 1");
        return lines;
    }
}
    
public class Main {

    public static void main(String[] args) {
        ObjectReader<Problem> rootReader = problemReader();
        HCParser<Problem> parser = new HCParser<>(rootReader);
        HCSolver<Problem> solver = new HCSolver<>(parser, Problem::solve);
        HCRunner<String> runner = new HCRunner<>(solver, UncaughtExceptionsPolicy.LOG_ON_SLF4J);
        runner.run(args);
    }

    private static ObjectReader<Problem> problemReader() {
        // full custom reader using Context
        ObjectReader<Point> pointReader = (Context ctx) -> {
            double x = ctx.readDouble();
            double y = ctx.readDouble();
            return new Point(x, y);
        };

        // reader using the fluent API
        return HCReader.withVars("P", "C") // reads the 2 first tokens into variables P and C
                       .createFromVar(Problem::new, "C") // creates a new Problem using the value of C as parameter
                       .thenList(Problem::setPoints, "P", pointReader); // reads P elements using the pointReader
    }
}
```
