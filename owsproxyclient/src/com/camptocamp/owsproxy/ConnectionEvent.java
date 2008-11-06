package com.camptocamp.owsproxy;

public class ConnectionEvent {
	
	public enum ConnectionStatus { IDLE, RUNNING, UNAUTHORIZED, ERROR, CONNECTING, PROXY_AUTH_REQUIRED, KEYSTORE_PASSWORD, NO_KEYSTORE };

	ConnectionEvent.ConnectionStatus status;
	String message;
	private Throwable throwable;
	
	public ConnectionEvent(ConnectionEvent.ConnectionStatus status, String message, Throwable throwable) {
		this.status = status;
		this.message = message;
		this.throwable = throwable;
	}
	public ConnectionEvent(ConnectionEvent.ConnectionStatus status, String message) {
		this(status, message, null);
	}
	public ConnectionEvent(ConnectionEvent.ConnectionStatus status) {
		this(status, "");
	}
	@Override
	public String toString() {
		return status.toString() + " " + message + " " + (throwable != null ? throwable.toString() : "");
	}
}