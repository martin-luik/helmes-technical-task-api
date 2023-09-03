package com.helmes.app.common.exception;

import lombok.Getter;

@Getter
public enum BusinessError {
  CATEGORY_NOT_FOUND("Category not exists");

  private final String message;

  BusinessError(String message) {
    this.message = message;
  }
}
