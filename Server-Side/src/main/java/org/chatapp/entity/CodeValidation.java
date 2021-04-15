package org.chatapp.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@Table(name = "codeValidation")
@IdClass(CodeValidationId.class)
public class CodeValidation {
    @Id
    private String numberPhone;

    @Id
    private String code;

    public CodeValidation(String numberPhone, String code) {
        this.numberPhone = numberPhone;
        this.code = code;
    }

    public CodeValidation() {}

    public String getNumberPhone() {
        return numberPhone;
    }

    public void setNumberPhone(String numberPhone) {
        this.numberPhone = numberPhone;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "CodeValidition{" +
                "numberPhone='" + numberPhone + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
