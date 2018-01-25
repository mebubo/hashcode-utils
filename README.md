# Google HashCode utils
[![Bintray](https://api.bintray.com/packages/joffrey-bion/maven/hashcode-utils/images/download.svg)](https://bintray.com/joffrey-bion/maven/hashcode-utils/_latestVersion)
[![Maven central version](https://img.shields.io/maven-central/v/org.hildan.hashcode/hashcode-utils.svg)](http://mvnrepository.com/artifact/org.hildan.hashcode/hashcode-utils)
[![Build Status](https://travis-ci.org/joffrey-bion/hashcode-utils.svg?branch=master)](https://travis-ci.org/joffrey-bion/hashcode-utils)

This library provides useful tools to make your life easier when competing in the Google Hash Code:
- **HCParser**: maps the input file to your classes representing the problem
- **HCSolver**: just needs a function that actually solves the problem, and takes care of the file I/O code
- **HCRunner**: a tiny framework that takes care of solving each input file in a separate thread with proper exception logging

The goal here is to take care of the boilerplate code to avoid debugging your input parser while you should be focusing
on solving the problem at hand.

## Example problems

You can find examples of usage of this library on previous HashCode editions problems in the
[examples folder](src/test/java/org/hildan/hashcode/utils/examples).

For the purpose of this readme, we'll just give a quick glance at what this library provides, through a very simple
example problem.

### Simple example problem

Imagine you need to find clusters in a point cloud. The input file gives you the number of points and the number of
clusters to find, and then the list of point positions:

```
3 2      // 3 points, 2 clusters to find
1.2 4.2  // point 0: x=1.2 y=4.2
1.5 3.4  // point 1: x=1.5 y=3.4
6.8 2.2  // point 2: x=6.8 y=2.2
```

Now, let's assume you represent the problem this way:

```java
public class Point {
    public final double x;
    public final double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }
}

public class Problem {
    public final int nClusters;
    private List<Point> points;

    Problem(int nClusters) {
        this.nClusters = nClusters;
    }

    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }

    public List<String> solve() {

        // solves the problem here

        // write solution into lines
        List<String> lines = new ArrayList<>();
        lines.add("output line 0");
        lines.add("output line 1");
        return lines;
    }
}
```

All of this is really what you want to be focusing on during the HashCode. We'll see how HashCode Utils can help you
with the rest.

## HCParser

HCParser allows you to describe how to map the input file to your classes and it'll take care of the actual parsing for 
you. It also provides nice error handling with line numbers, which saves a lot of time.

#### Usage on the example problem

For our little example problem, here's how you would write the parser with `HCParser`:

```java
public class Main {
    
    public static void main(String[] args) {
        ObjectReader<Problem> rootReader = createProblemReader();
        HCParser<Problem> parser = new HCParser<>(rootReader);

        String filename = args[0];
        Problem problem = parser.parse(filename);

        // do something with the problem
    }

    private static ObjectReader<Problem> createProblemReader() {
        // full custom reader using the Context class
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

Basically, creating an `HCParser` boils down to configuring a root `ObjectReader`. Note that `createProblemReader()`
does not parse the input, it just creates a reader that is able to parse the input.

`ObjectReader`s are components that can read as much input as necessary to build a specific type of object. They can be
composed together to form more complex object readers.

Here, we first create an `ObjectReader<Point>` to be able to read `Point`s from the input. Then we use it to configure 
the root reader, because we need to read a list of points.

The `pointReader` is defined manually, using `Context` (the parsing context) and getting input from it. With this
method, you have full control as to how you read the input, you'll just benefit from some nice error handling features.
On the other hand, the root reader uses the more convenient fluent API:

- `withVars` allows to read some tokens from the input and store them in variables before creating the object
- `createFromVar` creates an `ObjectReader` that instantiate a new object using variable values as constructor parameters
- `thenList` augments the existing reader so that it then reads a list of points and sets it on the created `Problem` object
  - `Problem::setPoints` provides a way to set the created list on the `Problem` object we're creating
  - `"P"` gives the number of `Point`s we should read (in the form of a context variable that was set earlier)
  - `pointReader` provides a reader to use for each element of the list

There are plenty of other useful methods that provides very quick ways of expressing common use cases. If more
customization is needed, there is always an option to have more control with a bit more code. HashCode Utils will never
prevent you from doing something very specific and unusual, it just won't help you as much as it could have.

You may read more about the API directly in [HCReader](src/main/java/org/hildan/hashcode/parser/readers/HCReader.java)'s
and [ObjectReader](src/main/java/org/hildan/hashcode/parser/readers/ObjectReader.java)'s Javadocs.

## HCSolver

HCSolver takes care of the file I/O for you, so that you just have to write the code that actually solves the problem.

#### Basic usage example

Using the same example problem, here is how we use HCSolver:
```java
public class BasicExample {

    public static void main(String[] args) {
        String filename = args[0];
        ObjectReader<Problem> rootReader = createProblemReader(); // omitted for brevity, see previous section
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

**Note**: People sometimes read standard input and write to standard output, but that prevents you from logging anything
 to the console in order to check that everything is fine while your algorithm is running. With file I/O in the code,
 you can log whatever you want and still write only the solution lines to the output file.

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
        runner.run(args); // args contains the names of the input files to run the solver on
    }    
}
```

Then you would run:

```
$ java Main input1.in input2.in input3.in
I solved input input1.in!
I solved input input3.in!
I solved input input2.in!
```

## All the pieces together

As you can see, the combination of all 3 components allows you to focus on problem-specific code only:

```java
public class Point {
    public final double x;
    public final double y;

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

    public List<String> solve() {

        // solves the problem here

        // write solution into lines
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
