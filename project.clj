(defproject hackaton "0.1.0-SNAPSHOT"
  :description "Løsningen på DevOps Akadami hold2 hackaton "
  :url "http://hackaton.dk/app"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/tools.logging "0.3.1"]
                 ;; Prioritets kø
                 [shams/priority-queue "0.1.2"]
                 ;; Shadow-cljs
                 [reagent "1.1.0"]
                 [thheller/shadow-cljs "2.15.2"]
                 ]
  :repl-options {:init-ns hackaton.core}
  :source-paths ["src"])
