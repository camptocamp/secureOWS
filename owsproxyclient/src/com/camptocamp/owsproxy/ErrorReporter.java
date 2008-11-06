package com.camptocamp.owsproxy;

import java.io.File;

import owsproxyclient.ExamineCertPanel.AddCert;

import com.camptocamp.owsproxy.ConnectionEvent.ConnectionStatus;

public interface ErrorReporter {

	void reportError(ConnectionStatus status, String error);

    void connected();

    AddCert certificateValidationFailure(boolean readonlyKeystore, String errorMessage, String certificateInformation);

    void keystoreMissing(File keystore);

}