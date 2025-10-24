package project.iw3.iw3.model.business;

import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class BusinessException extends Exception{

	@Builder
	public BusinessException(String message, Throwable ex) {
		super(message, ex);
	}

	@Builder
	public BusinessException(String message) {
		super(message);
	}

	@Builder
	public BusinessException(Throwable ex) {
		super(ex);
	}

}
