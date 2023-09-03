package com.helmes.app.common.exception;

import lombok.Getter;

@Getter
public class BusinessLogicException extends RuntimeException {
  private final BusinessError businessError;

  public BusinessLogicException(BusinessError businessError) {
    super(businessError.getMessage());
    this.businessError = businessError;
  }
}
