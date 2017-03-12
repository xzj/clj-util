(defproject clj-util "1.0.0-SNAPSHOT"
  :description "FIXME: write description"
  :dependencies [[org.clojure/clojure "1.8.0"]]
  :profiles {:dev {:dependencies [[midje "1.8.3"]]
                   :plugins [
                             [lein-cloverage "1.0.9"]
                             ]}}
  )
