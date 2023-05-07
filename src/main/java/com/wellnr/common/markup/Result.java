package com.wellnr.common.markup;

import com.wellnr.common.functions.Function1;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.Objects;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class Result<T> {

    private T value;

    private Exception failure;

    public static <T> Result<T> success(T value) {
        return new Result<>(value, null);
    }

    public static <T> Result<T> failure(Exception ex) {
        return new Result<>(null, ex);
    }

    public boolean isFailure() {
        return Objects.nonNull(failure);
    }

    public boolean isSuccess() {
        return Objects.nonNull(value);
    }

    public Exception getException() {
        if (this.isSuccess()) {
            throw new IllegalStateException("`getException` is called on successful result.");
        }

        return failure;
    }

    public T getValue() {
        if (this.isFailure()) {
            throw new IllegalStateException("`getValue` is called on failed result.");
        }

        return value;
    }

    public T getOrThrow() {
        if (this.isFailure()) {
            throw new RuntimeException(this.failure);
        }

        return value;
    }

    public <TYPE> Result<TYPE> mapSuccess(Function1<T, TYPE> map) {
        if (this.isSuccess()) {
            return Result.success(map.get(this.getValue()));
        } else {
            return Result.failure(this.getException());
        }
    }

}
