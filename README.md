# Google HashCode utils
[![Download](https://api.bintray.com/packages/joffrey-bion/maven/hashcode-utils/images/download.svg) ](https://bintray.com/joffrey-bion/maven/hashcode-utils/_latestVersion)
[![Build Status](https://travis-ci.org/joffrey-bion/hashcode-utils.svg?branch=master)](https://travis-ci.org/joffrey-bion/hashcode-utils)

This library provides useful tools to make your life easier when competing in the Google Hash Code:
- **HCParser**: an easily configured input parser which maps the input file to your classes representing the problem
- **HCSolver**: a class that takes care of all the boilerplate file I/O code, and just needs a function that actually solves the problem
- **HCRunner**: a tiny framework that takes care of solving each input file in a separate thread with proper exception logging

## HCParser

HCParser allows you to describe how to map the input file to your classes and it'll take care of the actual parsing for 
you. It also provides nice error handling with line numbers, which saves a lot of time.

#### Basic usage example

Input file:
```
3        // 3 points
1.2 4.2  // point 0: x=1.2 y=4.2
1.5 3.4  // point 1: x=1.5 y=3.4
6.8 2.2  // point 2: x=6.8 y=2.2
```

Here's how you would write the parser with `HCParser`:

```java
public class Problem {
    private List<Point> points;
    
    public List<Point> getPoints() {
        return points;
    }
    
    public void setPoints(List<Point> points) {
        this.points = points;
    }
}

public class Point {
    public float x;
    public float y;
}
    
public class Main {
    
    public static void main(String[] args){
        ObjectReader<Point> pointReader = TreeObjectReader.of(Point::new)
                                                          .fieldsAndVarsLine("x", "y");
        
        ObjectReader<Problem> rootReader = TreeObjectReader.of(Problem::new)
                                                           .fieldsAndVarsLine("@N") // stores the nb of points in var N
                                                           .listSection(Problem::setPoints, "N", pointReader);
        HCParser<Problem> parser = new HCParser<>(rootReader);
        Problem problem = parser.parse(args[0]);
        
        // do something with the problem
    }    
}
```

#### Let's break this down

Basically, creating an `HCParser` boils down to configuring a root `ObjectReader`.

Object readers are components that can read as many lines as necessary to build a specific type of object. They can be 
composed together to form more complex object readers.

Here, we first create an `ObjectReader<Point>` to be able to read `Point`s from the input. Then we use it to configure 
the root reader, because we need to read a list of points.
 
- `fieldsAndVarsLine` tells the reader to map each element of the next line to a field of the created object. 
It also allows to save any value to a context variable using the `@` syntax. The variable can be used later to know how 
many elements we should read.
 
- `addList` tells the reader to read the next bunch of lines as a list of elements:
  - `Problem::setPoints` provides a way to set the created list on the `Problem` object we're creating
  - `"N"` gives the number of `Point`s we should read (in the form of a context variable that was set earlier)
  - `pointReader` provides a reader to use for each element of the list

It might look over-complicated for a simple example like that, but when the input gets more complex, it can be pretty 
useful. There are plenty of other useful configurations to make the parsing easy and powerful. You may read more about 
them directly in HCParser's Javadoc.
 
## HCSolver

HCSolver takes care of the file I/O for you, so that you just have to write the code that actually solves the problem.

#### Basic usage example

Using the same example problem, here is how we use HCSolver:
```java
public class Problem {
    public int nPoints;
    public List<Point> points;
    
    public List<String> solve() {
        // solve the problem
        
        // write solution into lines (this is problem-specific)
        List<String> lines = new ArrayList<>();
        lines.add(outputLine0);
        lines.add(outputLine1);
        return lines;
    }
}

public class Point {
    public float x;
    public float y;
}
    
public class Main {
    
    public static void main(String[] args){
        ObjectReader<Point> pointReader = TreeObjectReader.of(Point::new)
                                                          .addFieldsLine("x", "y");
        
        ObjectReader<Problem> rootReader = TreeObjectReader.of(Problem::new)
                                                           .addFieldsLine("nPoints@N") // stores the nb of points in var N
                                                           .addList((p, l) -> p.points = l, "N", pointReader);
        HCParser<Problem> parser = new HCParser<>(rootReader);
        HCSolver<Problem> solver = new HCSolver<>(parser, Problem::solve);
        
        // reads the given input file and writes lines to an output file
        // the name of the output file is calculated from the input file
        solver.accept(args[0]);
    }    
}
```

Note that `HCSolver` implements `Consumer<String>` (it consumes input file names), which makes it nicely compatible with
`HCRunner`.

## HCRunner

HCRunner allows you to run your solver on all input files at the same time.

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
public class Problem {
    public int nPoints;
    public List<Point> points;
    
    public List<String> solve() {
        // solve the problem
        
        // write solution into lines (this is problem-specific)
        List<String> lines = new ArrayList<>();
        lines.add(outputLine0);
        lines.add(outputLine1);
        return lines;
    }
}
    
public class Point {
    public float x;
    public float y;
}
    
public class Main {
    
    public static void main(String[] args){
        ObjectReader<Point> pointReader = TreeObjectReader.of(Point::new)
                                                          .addFieldsAndVarsLine("x", "y");
        
        ObjectReader<Problem> rootReader = TreeObjectReader.of(Problem::new)
                                                           .addFieldsAndVarsLine("nPoints@N") // stores the nb of points in var N
                                                           .addList((p, l) -> p.points = l, "N", pointReader);
        HCParser<Problem> parser = new HCParser<>(rootReader);
        HCSolver<Problem> solver = new HCSolver<>(parser, Problem::solve);
        HCRunner<String> runner = new HCRunner<>(solver, UncaughtExceptionsPolicy.LOG_ON_SLF4J);
        runner.run(args);
    }    
}
```