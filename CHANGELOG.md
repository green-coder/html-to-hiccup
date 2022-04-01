# Change Log
All notable changes to this project will be documented in this file. This change log follows the conventions of [keepachangelog.com](http://keepachangelog.com/).

## [Unreleased]

## v0.1.7

### Fixed

- `minify-hiccup` now doesn't alter the content of `[:code ...]` blocks in addition to the `[:pre ...]` blocks.

## v0.1.6

### Improved

- Improved the grammar w.r.t. the spaces, based on https://html.spec.whatwg.org/multipage/syntax.html.
- Improved the grammar w.r.t. the whitespaces.
- Added support for single-quoted attribute values.

### Broke

- Made the `html->hiccup` function not using `edn/read-string` anymore.
  It's a change of behavior as the text now stays raw, unescaped.

## v0.1.5

### Improved

- Removed any potential chances at having the grammar ambiguous.
  It also simplifies the parsing by Instaparse and probably makes it more efficient.
- Made the implementation of `minify-hiccup` very explicit and easier to
  clone and modify for people who will need it.
- Improved the tests.

## v0.1.4

### Fixed

- Made `taipei-404.html/minify-hiccup` more robust.
- Fixed the `requires` to work in Clojurescript.

## v0.1.3

### Added

- Added the function `taipei-404.html/minify-hiccup`.
- Added more tests.

## v0.1.2

### Fixed

- Fixed #1, a bug where the unquoted attribute values were erroneously parsed via `edn/read-string`.
- Fixed grammar: tag names can be ended using a tab.

## v0.1.1

### Added

- Added an option to keep the html comments <!-- xxx --> found in the html.

## v0.1.0

### Added

- Initial commit, with improvements compared to the twitted version.
