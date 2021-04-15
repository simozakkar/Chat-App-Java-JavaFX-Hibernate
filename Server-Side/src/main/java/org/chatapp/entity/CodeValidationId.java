package org.chatapp.entity;

import java.io.Serializable;
import java.util.Objects;

public class CodeValidationId implements Serializable {
    private String numberPhone;

    private String code;

    public CodeValidationId() {
    }

    public CodeValidationId(String numberPhone, String code) {
        this.numberPhone = numberPhone;
        this.code = code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CodeValidationId that = (CodeValidationId) o;
        return code == that.code && Objects.equals(numberPhone, that.numberPhone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numberPhone, code);
    }
}
