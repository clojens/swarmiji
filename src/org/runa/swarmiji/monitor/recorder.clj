(ns org.runa.swarmiji.monitor.recorder
  (:use [org.runa.swarmiji.mpi.transport])
  (:use [org.runa.swarmiji.config.system-config])
  (:use [org.runa.swarmiji.utils.logger])
  (:use [org.runa.swarmiji.monitor.control_message :as control-message])
  (:import (java.sql Date Time)))

(defn timestamp-for-sql [time-in-millis]
  (str (.toString (Date. time-in-millis)) " " (.toString (Time. time-in-millis))))

(defn persist-message [control-message]
  (log-message "control-message:" control-message)
  (let [now (timestamp-for-sql (System/currentTimeMillis))
        with-timestamps (merge {:created_at now :updated_at now} control-message)]
  (control-message/insert with-timestamps)))

(defn start []
  (let [client (new-queue-client)
	q-name (queue-diagnostics-q-name)
	handler (queue-message-handler-for-function persist-message)]
    (log-message "Swarmiji: Starting Control-Message-Recorder...")
    (log-message "Listening on:" q-name)
    (.subscribe client q-name handler)))
    