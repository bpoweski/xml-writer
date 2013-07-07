# xml-writer

[xml-writer](http://github.com/bpoweski/xml-writer) generates XML using StAX in a manner compatible with [clojure.data.xml](http://github.com/clojure/clojure.data.xml) but with lower overhead.

## Usage


## Benchmarks

```bash
$ lein with-profile dev perforate
```

```
Performing task 'perforate' with profile(s): 'dev'
Benchmarking profiles:  [:dev :bench]
======================
Reflection warning, clojure/data/xml.clj:294:17 - call to createXMLStreamReader can't be resolved.
Reflection warning, clojure/data/xml.clj:362:5 - call to transform can't be resolved.
Goal:  Emit XML
-----
Case:  :clojure-data-xml-nested
Evaluation count : 180 in 60 samples of 3 calls.
             Execution time mean : 353.511661 ms
    Execution time std-deviation : 27.027851 ms
   Execution time lower quantile : 341.283025 ms ( 2.5%)
   Execution time upper quantile : 393.613717 ms (97.5%)

Found 2 outliers in 60 samples (3.3333 %)
	low-severe	 2 (3.3333 %)
 Variance from outliers : 56.8174 % Variance is severely inflated by outliers

Case:  :xml-writer-10k
Evaluation count : 5160 in 60 samples of 86 calls.
             Execution time mean : 11.571566 ms
    Execution time std-deviation : 81.898332 µs
   Execution time lower quantile : 11.427674 ms ( 2.5%)
   Execution time upper quantile : 11.743721 ms (97.5%)

Case:  :xml-writer-nested
Evaluation count : 480 in 60 samples of 8 calls.
             Execution time mean : 132.103054 ms
    Execution time std-deviation : 555.582421 µs
   Execution time lower quantile : 131.241375 ms ( 2.5%)
   Execution time upper quantile : 133.431791 ms (97.5%)

Found 2 outliers in 60 samples (3.3333 %)
	low-severe	 2 (3.3333 %)
 Variance from outliers : 1.6389 % Variance is slightly inflated by outliers

Case:  :clojure-data-xml-10k
Evaluation count : 1320 in 60 samples of 22 calls.
             Execution time mean : 46.277786 ms
    Execution time std-deviation : 459.673914 µs
   Execution time lower quantile : 45.708318 ms ( 2.5%)
   Execution time upper quantile : 47.322723 ms (97.5%)

Found 2 outliers in 60 samples (3.3333 %)
	low-severe	 2 (3.3333 %)
 Variance from outliers : 1.6389 % Variance is slightly inflated by outliers
```

## Examples

A single root element:

```clojure
user> (require '[xml-writer.core :as xml])
user> (xml/emit-sexp-str [:root])
"<?xml version=\"1.0\" encoding=\"UTF-8\"?><root></root>"
```

An element with optional attributes:

```clojure
user> (xml/emit-sexp-str [:root [:child "value"]])
"<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><child>value</child></root>"
```

With attributes:

```clojure
user> (xml/emit-sexp-str [:root [:child {:attribute "value"} "value"]])
"<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><child attribute=\"value\">value</child></root>"
```

With prefixed elements and attributes:

```clojure
user> (xml/emit-sexp-str [:root {:xmlns:xsi "http://www.w3.org/2001/XMLSchema-instance"} [:child {:xsi:nil true} nil]])
"<?xml version=\"1.0\" encoding=\"UTF-8\"?><root xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><child xsi:nil=\"true\"></child></root>"
```

## TODO

- [ ] use empty elements when values are nil [:elem {} nil] -> <elem />
- [ ] indenting transformer
- [ ]


## License

Copyright © 2013 Ben Poweski

Distributed under the Eclipse Public License, the same as Clojure.
