package com.camptocamp.owsproxy;

import com.camptocamp.owsproxy.ConnectionEvent.ConnectionStatus;

interface ErrorReporter {

	void reportError(ConnectionStatus status, String error);

    void connected();
}