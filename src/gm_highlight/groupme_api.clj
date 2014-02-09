(ns gm-highlight.groupme-api
  (:require [clj-http.client :as http]
            [clojure.data.json :as json]))

(def ^:dynamic *token*)
(def ^:private api-root "https://api.groupme.com/v3")

(defn get-group-name [id & {:keys [token] :or {token *token*} :as opts}]
  "Gets the group name associated with a group id from GroupMe"
  (let [uri (str api-root "/groups/" id "?token=" token )]
    (-> (http/get uri {:throw-exceptions false})
        (:body)
        (json/read-str :key-fn keyword)
        (get-in [:response :name]))))
