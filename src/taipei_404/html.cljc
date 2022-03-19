(ns taipei-404.html
  (:require [clojure.string :as str]
            [clojure.edn :as edn]
            #?(:clj [instaparse.core :as insta :refer [defparser]]
               :cljs [instaparse.core :as insta :refer-macros [defparser]])))

(defparser html-parser "
  nodes = node*
  <node> = comment-element | void-element-tag | open-close-tags | self-closing-tag | text
  comment-element = <'<!--'> (!'-->' #'.')* <'-->'>
  void-element-tag = <'<'> <spaces>? void-element-tag-name attributes? <spaces>? <'>'>
  void-element-tag-name = 'area' | 'base' | 'basefont' | 'bgsound' | 'br' | 'col' |
                          'command' | 'embed' | 'frame' | 'hr' | 'img' | 'input' | 'isindex' |
                          'keygen' | 'link' | 'menuitem' | 'meta' | 'nextid' | 'param' |
                          'source' | 'track' | 'wbr' | (!'!--' #'![^ />]+')
  open-close-tags = opening-tag nodes closing-tag
  opening-tag = <'<'> <spaces>? tag-name attributes? <spaces>? <'>'>
  closing-tag = <'</'> tag-name <'>'>
  self-closing-tag = <'<'> <spaces>? tag-name attributes? <spaces>? <'/>'>
  tag-name = #'[^ \t/>]+'
  attributes = (<spaces> attribute)+
  attribute = attribute-name (<'='> (unquoted-attribute-value | quoted-attribute-value))?
  <attribute-name> = #'[^ \t=>]+'
  <unquoted-attribute-value> = #'[^ \t>]+'
  quoted-attribute-value = #'\"[^\"]*\"'
  <text> = #'[^<]+'
  spaces = #'[ \t]+'
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
                                                         [(keyword attribute-name) attribute-value]))
                          :quoted-attribute-value   (fn [quoted-value] (edn/read-string quoted-value))}))))

(defn minify-hiccup
  "Get rid of blank strings where it doesn't influence the html semantic."
  [hiccup]
  (cond
    (string? hiccup) (not-empty (str/trim hiccup))
    (vector? hiccup) (let [[tag-kw & children] hiccup]
                       (if (= tag-kw :pre)
                         hiccup
                         (into [tag-kw]
                               (comp (map minify-hiccup)
                                     (remove nil?))
                               children)))
    :else hiccup))
