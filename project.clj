(defproject net.clojars.oyvinht/id3 "1.0.0"
  :description "Implementation of Quinlans Iterative Dichotomiser 3."
  :url "https://github.com/oyvinht/id3"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 ;[com.clojure-goes-fast/clj-async-profiler "0.5.0"]
                 [criterium "0.4.6"]]
  :repl-options {:init-ns id3.core})
