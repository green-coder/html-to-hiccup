# html-to-hiccup

[![CircleCI](https://circleci.com/gh/green-coder/html-to-hiccup.svg?style=svg)](https://circleci.com/gh/green-coder/html-to-hiccup)

[![Clojars Project](https://img.shields.io/clojars/v/taipei.404/html-to-hiccup.svg)](https://clojars.org/taipei.404/html-to-hiccup)
[![Cljdoc badge](https://cljdoc.org/badge/taipei.404/html-to-hiccup)](https://cljdoc.org/d/taipei.404/html-to-hiccup/CURRENT)
[![Clojars download_badge](https://img.shields.io/clojars/dt/taipei.404/html-to-hiccup?color=opal)](https://clojars.org/taipei.404/html-to-hiccup)

A tiny, simple and truly cross-platform (CLJC) `html->hiccup` function
which does not rely on the browser.

## Installation

[![Clojars Project](http://clojars.org/taipei.404/html-to-hiccup/latest-version.svg)](http://clojars.org/taipei.404/html-to-hiccup)

## Feature

- Truly cross-platform (CLJC), does not rely on the browser.
- Tiny source code, easy to digest.
- Highly hackable. Fork me as soon as needed!

## Usage

```clojure
(require '[taipei-404.html :refer [html->hiccup]])

(html->hiccup "<p>hello, world</p>")
; => ([:p "hello, world"])
```

## License

Copyright © 2022 Vincent Cantin

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
