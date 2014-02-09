(ns gm-highlight.core
  (:require [compojure.core :refer [defroutes GET POST]]
            [compojure.handler :as handler]
            [ring.adapter.jetty :as ring]
            [clojure.data.json :as json]
            [clojure.java.jdbc :as sql]
            [clojure.string :as string :refer [lower-case split]]
            [clojure.set :as set :refer [intersection]]
            [gm-highlight.pushover-api :as pushover]
            [gm-highlight.groupme-api :as groupme]
            [environ.core :refer [env]]))

(def db (or (System/getenv "DATABASE_URL")
                  "postgresql://localhost:5432/groupme_highlight"))

(def p-api-key (env :pushover-token))
(def g-api-key (env :groupme-token))

(defn notify-user
  "Send notification to user"
  [{:keys [name key]} mentioner context group_id]
  (let [group-name (groupme/get-group-name group_id :token g-api-key)]
    (pushover/send-message key
                           (str (format "%s: %s" mentioner context)
                                "\nGroup: " group-name)
                           :token p-api-key)))

(defn get-words
  "Get words without punctuation from a string"
  [str]
  (-> str
      (lower-case)
      (string/replace #"[^a-z\s]" "")
      (split #"\s+")))

(defn check-message-for-names
  "Checks to see if a message contains the names of anyone registered for highlights"
  [{:keys [name text user_id group_id]}]
  (let [words (get-words text)
        members (filter :key (sql/query db ["SELECT * FROM users"]))
        mentions (-> (map (comp lower-case :name) members)
                     (set)
                     (intersection (set words)))
        mentions (filter (comp (partial contains? mentions) lower-case :name) members)]
    (doseq [mention mentions]
      (notify-user mention name text group_id)))
  {:status 200})

(defn dispatch
  "Dispatch method. Checks if a command has been issued, if a message
should be processed, or if it should be ignored"
  [{:keys [body]}]
  (let [_ (json/read-str (slurp body) :key-fn keyword)
        {:keys [text system] :as msg }  _
        words (split text #"\s+")]
    (when (= "!highlight-register" (first words)) nil;TODO
          )
    (when (not system) (check-message-for-names msg))))

(defroutes routes
  (POST "/highlight" req (dispatch req))
  (GET "/hello" [] (fn [req] )))

(def app (handler/api routes))

(defn -main []
  (ring/run-jetty #'app {:port (or (Integer. (System/getenv "PORT")) 5432) :join? false}))
