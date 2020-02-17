(ns auth.utils.db
  (:require
   [honeysql.core :as sql]
   [environ.core :refer [env]]
   [hikari-cp.core :refer [make-datasource close-datasource]]
   [taoensso.timbre :refer [debug]]
   [clojure.java.jdbc :as jdbc]))

(defonce datasource
  (atom nil))

(defn open! []
  (let [datasource-options
        {:auto-commit        true
         :read-only          false
         :connection-timeout 30000
         :validation-timeout 5000
         :idle-timeout       600000
         :max-lifetime       1800000
         :minimum-idle       10
         :maximum-pool-size  10
         :pool-name          "db-pool"
         :adapter            "postgresql"
         :username           (env :db-user)
         :password           (env :db-password)
         :database-name      (env :db-name)
         :server-name        (env :db-host)
         :port-number        (Integer/parseInt (env :db-port))
         :register-mbeans    false}]
    (reset! datasource (make-datasource datasource-options))))

(defn close!
  []
  (close-datasource @datasource))

(defn query
  ([conn sql]
   (query conn sql nil))
  ([conn sql opts]
   (let [raw-query (sql/format sql)]
     (debug raw-query)
     (if (:excute? opts)
       (jdbc/execute! conn raw-query opts)
       (jdbc/query conn raw-query opts)))))
