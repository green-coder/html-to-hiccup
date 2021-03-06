(ns taipei-404.html
  (:require [clojure.string :as str]
            #?(:clj [instaparse.core :as insta :refer [defparser]]
               :cljs [instaparse.core :as insta :refer-macros [defparser]])))

(defparser html-parser "
  nodes = node*
  <node> = comment-element | void-element-tag | open-close-tags | self-closing-tag | text
  comment-element = <'<!--'> (!'-->' #'.|\n')* <'-->'>
  void-element-tag = <'<'> void-element-tag-name attributes? <maybe-spaces> <'>'>
  void-element-tag-name = 'area' | 'base' | 'basefont' | 'bgsound' | 'br' | 'col' |
                          'command' | 'embed' | 'frame' | 'hr' | 'img' | 'input' | 'isindex' |
                          'keygen' | 'link' | 'menuitem' | 'meta' | 'nextid' | 'param' |
                          'source' | 'track' | 'wbr' | (!'!--' #'![^ />]+')
  open-close-tags = opening-tag nodes closing-tag
  opening-tag = <'<'> tag-name attributes? <maybe-spaces> <'>'>
  closing-tag = <'</'> tag-name <maybe-spaces> <'>'>
  self-closing-tag = <'<'> tag-name attributes? <maybe-spaces> <'/>'>
  tag-name = #'[^ \"\\'\t\f\r\n/>]+'
  attributes = (<spaces> attribute)+
  attribute = attribute-name (<maybe-spaces> <'='> <maybe-spaces> attribute-value)?
  <attribute-value> = unquoted-attribute-value | single-quoted-attribute-value | double-quoted-attribute-value
  <attribute-name> = #'[^ \"\\'\t\f\r\n=>]+'
  <unquoted-attribute-value> = #'[^ \"\\'\t\f\r\n>]+'
  <single-quoted-attribute-value> = <'\\''> #'[^\\']*' <'\\''>
  <double-quoted-attribute-value> = <'\"'> #'[^\"]*' <'\"'>
  <text> = #'[^<]+'
  maybe-spaces = #'[ \t\f\r\n]*'
  spaces = #'[ \t\f\r\n]+'
")

(defn html->hiccup
  "A simple cross-platform (CLJC) html->hiccup converter."
  ([html-str] (html->hiccup html-str nil))
  ([html-str {:as options :keys [comment-keyword]}]
   (->> (html-parser html-str)
        (insta/transform {:nodes                    (fn [& nodes] (remove nil? nodes))
                          :comment-element          (fn [& comment]
                                                      (when (some? comment-keyword)
                                                        [comment-keyword (apply str comment)]))
                          :void-element-tag-name    keyword
                          :void-element-tag         (fn ([tag-name] [tag-name])
                                                        ([tag-name attributes] [tag-name attributes]))
                          :open-close-tags          (fn [opening-tag nodes _closing-tag]
                                                      (into opening-tag nodes))
                          :opening-tag              (fn ([tag-name] [tag-name])
                                                        ([tag-name attributes] [tag-name attributes]))
                          :self-closing-tag         (fn ([tag-name] [tag-name])
                                                        ([tag-name attributes] [tag-name attributes]))
                          :tag-name                 keyword
                          :attributes               (fn [& attributes]
                                                      (into {} attributes))
                          :attribute                (fn ([attribute-name]
                                                         [(keyword attribute-name) true])
                                                        ([attribute-name attribute-value]
                                                         [(keyword attribute-name) attribute-value]))}))))

(defn minify-hiccup
  "Get rid of blank strings where it doesn't influence the html semantic."
  [hiccup]
  (cond
    (string? hiccup) (not-empty (str/trim hiccup))
    (map? hiccup) hiccup
    (vector? hiccup) (let [[tag-kw & children] hiccup]
                       (if (contains? #{:pre :code} tag-kw)
                         hiccup
                         (let [[first-child & rest-children] children
                               [props children] (if (or (nil? first-child)
                                                        (map? first-child))
                                                  [first-child rest-children]
                                                  [nil children])]
                           (-> [tag-kw]
                               (cond-> (seq props) (conj (minify-hiccup props)))
                               (into (comp (map minify-hiccup)
                                           (remove nil?))
                                     children)))))
    :else hiccup))
