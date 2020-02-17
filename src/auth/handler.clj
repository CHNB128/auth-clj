(ns auth.handler
  (:require [compojure.core :refer [defroutes routes]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [clojure.java.jdbc :as jdbc]
            [auth.utils.db :refer [datasource]]
            [auth.routes :refer [core-routes]]))

;; mw

(defn wrap-jdbc-connection [handler datasource]
  (fn [req]
    (jdbc/with-db-transaction [conn {:datasource @datasource}]
      (handler (assoc req :conn conn)))))

;;

(defn init []
  (println "auth is starting"))

(defn destroy []
  (println "auth is shutting down"))

(defroutes app-routes
  (route/resources "/")
  (route/not-found "Not Found"))

(def handler
  (-> (routes core-routes app-routes)
      (wrap-jdbc-connection datasource)
      (handler/api)))
