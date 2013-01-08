(ns hello_parsley.core
  (:use [clojure.pprint])
  (:require [clojure.java.io    :as io]
            [clojure.string     :as s]
            [net.cgrand.parsley :as p])
  (:gen-class))

; just parses the date line of cave.txt
(def parser1 
  (p/parser {:main :daterecord :space :ws?}
            :ws-         #"\s+"
            :daterecord  [:date]
            :date        ["date" #"\w{3}-\d{1,2}-\d{4}"]
            ))

; ignores content of Path block
(def parser2 
  (p/parser {:main :daterecord :space :ws?}
            :ws-         #"\s+"
            :daterecord  [:date :cave]
            :date        ["date" #"\w{3}-\d{1,2}-\d{4}"]
            :cave        ["cave" :name  "{" :assn+ "}"]
            :assn        [:label "=" :value]
            :label       #{"avg_temp" "material" "altitude"}
            :name        #"[a-zA-Z_0-9-]+"
            :value       #"[\w\.]+"
            ))

; working parser for cave.txt
(def parser3
  (p/parser {:main :daterecord :space :ws?}
            :ws-         #"\s+"
            :daterecord  [:date :cave]
            :date        ["date" #"\w{3}-\d{1,2}-\d{4}"]
            :cave        ["cave" :name  "{" :assn* :path* "}"]
            :assn        [:label "=" :value]
            :label       #{"avg_temp" "material" "altitude" "parent"}
            :path        ["path" :name "{" :assn* :measurements "}"]
            :measurements  ["measurements" "{" :triple+ "}"]
            :triple      ["(" :num "," :num "," :num #"\)(\s*,)?"] 
            :name        #"[a-zA-Z0-9_-]+"
            :value       #"[a-zA-Z0-9\.]+"
            :num         #"\d+"
            ))

; ambiguity exception when run
(def parser3bad
  (p/parser {:main :daterecord :space :ws?}
            :ws-         #"\s+"
            :daterecord  [:date :cave]
            :date        ["date" #"\w{3}-\d{1,2}-\d{4}"]
            :cave        ["cave" :name  "{" :assn* :path* "}"]
            :assn        [:name "=" :value]  ; causes ambiguity
            :path        ["path" :name "{" :assn* :measurements "}"]
            :measurements  ["measurements" "{" :triple+ "}"]
            :triple      ["(" :num "," :num "," :num #"\)(\s*,)?"] 
            :name        #"[a-zA-Z0-9_-]+"
            :value       #"[a-zA-Z0-9\.]+"
            :num         #"\d+"
            ))

; won't compile -- produces a shift/reduce conflict
; (def parser4bad
;   (p/parser {:main :daterecord :space :ws?}
;             :ws-         #"\s+"
;             :daterecord  [:date :cave]
;             :date        ["date" #"\w{3}-\d{1,2}-\d{4}"]
;             :cave        ["cave" :name  "{" :assn* :path* "}"]
;             :assn        [:label "=" :value]
;             :label       #{"avg_temp" "material" "altitude" "parent"}
;             :path        ["path" :name "{" :assn* :measurements "}"]
;             ; causes shift/reduce conflict
;             :measurements  ["measurements" "{" :triple* :lasttriple "}"]
;             :triple      ["(" :num "," :num "," :num ")" ","] 
;             :lasttriple  ["(" :num "," :num "," :num ")"] 
;             :name        #"[a-zA-Z0-9_-]+"
;             :value       #"[a-zA-Z0-9\.]+"
;             :num         #"\d+"
;             ))

; parser for cave2.txt -- doesn't compile
; (def parser5
;   (p/parser {:main :daterecord :space :ws?}
;             :ws-         #"\s+"
;             :daterecord  [:date :cave+]   ; only change from parser3 is here
;             :date        ["date" #"\w{3}-\d{1,2}-\d{4}"]
;             :cave        ["cave" :name  "{" :assn* :path* "}"]
;             :assn        [:label "=" :value]
;             :label       #{"avg_temp" "material" "altitude" "parent"}
;             :path        ["path" :name "{" :assn* :measurements "}"]
;             :measurements  ["measurements" "{" :triple+ "}"]
;             :triple      ["(" :num "," :num "," :num #"\)(\s*,)?"] 
;             :name        #"[a-zA-Z0-9_-]+"
;             :value       #"[a-zA-Z0-9\.]+"
;             :num         #"\d+"
;             ))

(defn parse-n-print 
  "Applies the provided parser to the file (relative path).
   Usage: (parse-n-print parser3 'cave.txt')"
  [parser fname]
  (let [result (parser
                 (s/lower-case (slurp (io/as-relative-path fname))))]
    (pprint result)))

(defn -main [& args]
  (if (first args)
    (parse-n-print parser3 (first args))
    (parse-n-print parser3 "cave.txt")))

