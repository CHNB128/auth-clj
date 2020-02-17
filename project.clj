(defproject auth "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/tools.reader "1.3.2"]
                 [compojure "1.6.1"]
                 [hiccup "1.0.5"]
                 [buddy/buddy-auth "2.2.0"]
                 [org.postgresql/postgresql "42.2.6.jre7"]
                 [honeysql "0.9.4"]
                 [com.fzakaria/slf4j-timbre "0.3.14"]
                 [environ "1.1.0"]
                 [migratus "1.2.6"]
                 [http-kit "2.3.0"]
                 [hikari-cp "2.8.0"]
                 [org.clojure/java.jdbc "0.7.9"]
                 [ring-server "0.5.0"]]
  :main auth.core
  :plugins [[lein-ring "0.12.5"]]
  :ring {:handler auth.handler/handler
         :init auth.handler/init
         :destroy auth.handler/destroy}
  :profiles
  {:uberjar
   {:aot :all}
   :production
   {:ring
    {:open-browser? false, :stacktraces? false, :auto-reload? false}}
   :dev
   {:dependencies [[ring/ring-mock "0.4.0"] [ring/ring-devel "1.7.1"]]}})
