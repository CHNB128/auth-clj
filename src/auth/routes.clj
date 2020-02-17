(ns auth.routes
  (:require [compojure.core :refer :all]
            ;; auth
            [buddy.auth.backends.token :refer [jwe-backend]]
            [buddy.core.nonce :as nonce]
            [buddy.sign.jwt :as jwt]
            [clj-time.core :as time]
            ;; db
            [auth.utils.db :refer [query]]
            [honeysql.helpers :as h]
            [honeysql.core :as sql]))

;; auth

(defn get-expiration-date []
  (time/plus (time/now) (time/hours 6)))

(defonce secret
  (nonce/random-bytes 32))

(defonce encruption-options
  {:alg :a256kw :enc :a128gcm})

(defonce backend
  (jwe-backend
   {:secret secret
    :options encruption-options}))

;; db

(defn create-token [user-data]
  (let [claims {:user-data user-data :exp (get-expiration-date)}
        token (jwt/encrypt claims secret encruption-options)]
    token))

(defn find-user [conn condition]
  (query conn
         (-> (h/select :*)
             (h/from :users)
             condition)))

;; fn

(defn login [conn {:keys [login password]}]
  (let [condition (h/where [:= :loign login] [:= :password password])
        user (find-user conn condition)]
    (when user (create-token user))))

(defn register [conn {:keys [data]}]
  (query conn
         (-> (h/insert-into :users)
             (h/values [data]))
         {:excute? true}))

(defn delete [conn {:keys [id]}]
  (query conn
         (-> (h/delete-from :users)
             (h/where [:= :id id]))
         {:excute? true}))

(defn verify [conn {:keys [token] :as p}]
  (when token
    (let [raw (jwt/decrypt token secret encruption-options)]
      raw)))

(defn update-user [conn {:keys [id data]}]
  (query conn
         (-> (h/update :users)
             (h/sset (assoc data :updated_at (sql/call :now)))
             (h/where [:= :id id]))
         {:excute? true}))

;; route

(defroutes core-routes
  (context "/" {:keys [conn]}
    (PUT    "/"       {:keys [params]} (register conn params))
    (DELETE "/:id"    {:keys [params]} (delete conn params))
    (PATCH  "/:id"    {:keys [params]} (update-user conn params))
    (POST   "/login"  {:keys [params]} (login conn params))
    (POST   "/verify" {:keys [params]} (verify conn params))))
