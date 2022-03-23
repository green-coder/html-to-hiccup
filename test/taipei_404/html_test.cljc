(ns taipei-404.html-test
  (:require [clojure.test :refer [deftest testing is are]]
            [taipei-404.html :refer [html->hiccup minify-hiccup]]))

(deftest html->hiccup-test
  (testing "the basic usage"
    (are [html hiccup]
      (= (html->hiccup html) hiccup)

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

      ;; Tab used instead of spaces
      "<a\thref=\"#anchor\"\thidden\t/>"
      '([:a {:href "#anchor"
             :hidden true}])

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

      ;; That thing that goes before the html element
      "<!doctype html>"
      '([:!doctype {:html true}])))

  (testing "the :discard-comments option"
    (is (= (html->hiccup "<!--Some useful comment--><p><!--Here also--></p>"
                         {:comment-keyword :!--})
           '([:!-- "Some useful comment"] [:p [:!-- "Here also"]])))
    (is (= (html->hiccup "<!--Some useful comment--><p><!--Here also--></p>"
                         {:comment-keyword nil})
          '([:p])))))

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
