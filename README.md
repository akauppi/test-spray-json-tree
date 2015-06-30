# test-spray-json-tree

A test project to experiment with non-trivial case class hierarchies, JSON and back.

## Compile and run

`sbt test`

->

<pre>
[info]   "dt": "2015-06-30T15:51:30.125+03:00",
[info]   "map": {
[info]     "aaa": 900.0
[info]   },
[info]   "t": ["a", "b"],
[info]   "t2": [4, 5],
[info]   "c": {
[info]     "name": "some",
[info]     "red": 1,
[info]     "green": 2,
[info]     "blue": 3
[info]   },
[info]   "inner": {
[info]     "s": "xxx",
[info]     "c": {
[info]       "name": "some",
[info]       "red": 1,
[info]       "green": 2,
[info]       "blue": 3
[info]     }
[info]   }
[info] } 
</pre>

The *ease* of using Spray-json to convert JSON to type safe structures is actually brilliant. Too bad the sample codes normally don't go this deep, so I failed to realize the full potential of this. Until now.
