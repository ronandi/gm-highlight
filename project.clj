(defproject gm-highlight "0.1.0"
  :description "A highlight notification bot for GroupMe. Uses Pushover for notifications"
  :url "http://github.com/ronandi/gm-highlight"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [clj-http "0.7.8"]
                 [compojure "1.1.6"]
                 [org.clojure/data.json "0.2.4"]
                 [ring/ring-jetty-adapter "1.2.1"]
                 [org.clojure/java.jdbc "0.3.3"]
                 [postgresql/postgresql "8.4-702.jdbc4"]
                 [environ "0.4.0"]]
  :uberjar-name "gm-highlight.jar"
  :min-lein-version "2.0.0"
  :plugins [[lein-ring "0.8.7"]]
  :ring {:handler gm-highlight.core/app}
  :profiles {:dev {:dependencies [[org.clojure/tools.namespace "0.2.4"]]}})
