# hello_parsley

Simple example of how to use the Clojure [Parsley library](https://github.com/cgrand/parsley) to parse a text file ("cave.txt").

The repo is a companion to [this blog post](http://walkwithoutrhythm.net/blog/2012/12/29/parsing-is-fun-again/)

## Pre-requisites

Just [leiningen](https://github.com/technomancy/leiningen).


## Usage


    $ lein run "cave.txt"

or 

    $ lein repl
    ...
    > (parse-n-print parser3 "cave.txt")

