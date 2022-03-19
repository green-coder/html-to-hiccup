# Change Log
All notable changes to this project will be documented in this file. This change log follows the conventions of [keepachangelog.com](http://keepachangelog.com/).

## [Unreleased]

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
