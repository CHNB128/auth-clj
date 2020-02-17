(ns auth.core
  (:require
   [org.httpkit.server :as http]
   [taoensso.timbre :refer [info] :as timbre]
   [environ.core :refer [env]]
   [nrepl.server :as nrepl]
   [auth.utils.db :as db]
   [auth.handler :refer [handler]])
  (:gen-class))

(defonce server (atom nil))

(defn stop-server []
  (when-not (nil? @server)
    ;; graceful shutdown: wait 100ms for existing requests to be finished
    ;; :timeout is optional, when no timeout, stop immediately
    (@server :timeout 100)
    (reset! server nil)))

(defn start-server [opts]
  (reset! server (http/run-server #'handler opts)))

(defn -main [& args]
  (timbre/set-level! (keyword (env :log-level)))
  (db/open!)
  (start-server {:port 3000}))
