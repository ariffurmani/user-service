package org.furmani.userservice.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenDto {
    private String value;

    public static TokenDto from(String tokenValue) {
        if (tokenValue == null) {
            return null;
        }

        TokenDto tokenDto = new TokenDto();
        tokenDto.setValue(tokenValue);
        return tokenDto;
    }
}
