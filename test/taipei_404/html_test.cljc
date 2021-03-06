(ns taipei-404.html-test
  (:require [clojure.test :refer [deftest testing is are]]
            [taipei-404.html :refer [html->hiccup minify-hiccup]]))

(deftest html->hiccup-test
  (testing "the basic usage"
    (are [html hiccup]
      (= hiccup (html->hiccup html))

      ;; Obligatory test
      "<p>hello, world</p>"
      '([:p "hello, world"])

      ;; Nested elements
      "<p><ol><li>a</li><li>b</li><li>c</li></ol></p>"
      '([:p
         [:ol
          [:li "a"]
          [:li "b"]
          [:li "c"]]])

      ;; Sequence of html tags separated by texts
      "a<p/>b<img/>c"
      '("a" [:p] "b" [:img] "c")

      ;; Void elements
      "<br><hr>"
      '([:br] [:hr])

      ;; Void element with attributes
      "<img hidden src=\"flower.jpg\" alt=\"A flower\" width=\"500\" height=\"600\" style=\"border:5px solid black\">"
      '([:img {:hidden true
               :src    "flower.jpg"
               :alt    "A flower"
               :width  "500"
               :height "600"
               :style  "border:5px solid black"}])

      ;; Element with unquoted attribute
      "<script src=/foo/bar/baz.js></script>"
      '([:script {:src "/foo/bar/baz.js"}])

      ;; Single quoted attributes
      "<script src='/foo/bar/baz.js'></script>"
      '([:script {:src "/foo/bar/baz.js"}])

      ;; Double quoted attributes
      "<script src=\"/foo/bar/baz.js\"></script>"
      '([:script {:src "/foo/bar/baz.js"}])

      ;; That thing that goes before the html element
      "<!doctype html>"
      '([:!doctype {:html true}])

      ;; With space-like characters everywhere inside the tags
      "a<p \n/>b<img src = /flower.jpg \r\n\f alt = \"A flower\" \n />c"
      '("a" [:p] "b" [:img {:src "/flower.jpg", :alt "A flower"}] "c")))

  (testing "the :discard-comments option"
    (is (= '([:!-- "Some useful comment"] [:p [:!-- "Here also"]])
           (html->hiccup "<!--Some useful comment--><p><!--Here also--></p>"
                         {:comment-keyword :!--})))
    (is (= '([:p])
           (html->hiccup "<!--Some useful comment--><p><!--Here also--></p>"
                          {:comment-keyword nil}))))

  (testing "multiple line comments"
    (is (= '([:!-- "\nsome useful\ncomment\n"] "\n" [:h1 "foo"])
           (html->hiccup "<!--\nsome useful\ncomment\n-->\n<h1>foo</h1>"
                         {:comment-keyword :!--})))
    (is (= '("\n" [:h1 "foo"])
           (html->hiccup "<!--\nsome useful\ncomment\n-->\n<h1>foo</h1>")))))

(deftest minify-hiccup-test
  (testing "that blank strings are discarded"
    (is (= [:ul [:li [:p "foo"] [:p "bar"]]]
           (minify-hiccup
             (first
               (html->hiccup
                 "<ul>\n<li>\n<p>foo</p>\n<p>bar</p>\n</li>\n</ul>\n"))))))

  (testing "that anything within [:pre ,,,] remains unchanged"
    (is (= [:p [:pre " " [:code "\n(def a 1)"] " "]]
           (minify-hiccup
             (first
               (html->hiccup
                 "<p> <pre> <code>\n(def a 1)</code> </pre> </p>"))))))

  (testing "that the attributes are not discarded"
    (is (= [:ol {:start "123456789"} [:li "ok"]]
           (minify-hiccup
             (first
               (html->hiccup
                 "<ol start=\"123456789\">\n<li>ok</li>\n</ol>\n"))))))

  (testing "that empty property hashmaps are discarded"
    (is (= [:p "hi"]
           (minify-hiccup [:p nil "hi"])))
    (is (= [:p "hi"]
           (minify-hiccup [:p {} "hi"])))))
