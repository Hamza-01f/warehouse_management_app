package com.brief.demo.dto.response;


import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApiTokenResponse<T> {

    private String message;
    private boolean success;
    private T data;
    private LocalDateTime time;

    public static <T> ApiTokenResponse<T> success(T data , String message){
        return ApiTokenResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .time(LocalDateTime.now())
                .build();
    }
}
