package com.puppynoteserver.global.exception;

import lombok.Getter;

@Getter
public class PuppyNoteException extends RuntimeException {

	private final String message;

	public PuppyNoteException(String message, Exception e) {
		super(message, e);
		this.message = message;
	}

	public PuppyNoteException(String message) {
		this.message = message;
	}
}
