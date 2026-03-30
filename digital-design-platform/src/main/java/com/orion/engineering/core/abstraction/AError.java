package com.orion.engineering.core.abstraction;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@NoArgsConstructor
@Getter
@Setter
public class AError<ERROR> implements Serializable {
	private String errorCode;
	private String errorMessage;
	private ERROR error;


	public AError(String errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}


	public AError(String errorCode, String errorMessage, ERROR error) {
		this(errorCode, errorMessage);
		this.error = error;
	}
}
