(ns gm-highlight.pushover-api
  (:require [clj-http.client :as http]))

(def ^:dynamic *token*)
(def ^:private api-root "https://api.pushover.net/1/messages.json")

(defn send-message
  "Sends a message via Pushover's REST API"
  [user message & {:keys [token] :or {token *token*} :as opts}]
  (http/post api-root {:throw-exceptions false
                       :form-params (merge (dissoc opts :token)
                                           {:token token :user user
                                            :message message})}))
