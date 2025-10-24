package project.iw3.iw3.model.business;

import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class FoundException extends Exception{

	@Builder
	public FoundException(String message, Throwable ex) {
		super(message, ex);
	}

	@Builder
	public FoundException(String message) {
		super(message);
	}

	@Builder
	public FoundException(Throwable ex) {
		super(ex);
	}

}
