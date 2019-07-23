package com.bank.core.validation;

public interface IValidator<T> {
    public boolean isValid(T t) throws Exception;
}
