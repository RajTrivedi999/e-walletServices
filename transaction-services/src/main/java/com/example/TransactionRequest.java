package com.example;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class TransactionRequest {

    private  String fromUser;
    private String toUser;
    private int amount;
}
