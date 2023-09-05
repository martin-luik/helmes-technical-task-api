package com.helmes.app.domain.industry.exception;

import lombok.Getter;

@Getter
public enum BusinessError {
  CATEGORY_NOT_FOUND("Category not exists");

  private final String message;

  BusinessError(String message) {
    this.message = message;
  }
}
