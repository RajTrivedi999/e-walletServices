package com.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class TransactionService {

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    KafkaTemplate kafkaTemplate;

    @Autowired
    ObjectMapper objectMapper;
    public void createTransaction(TransactionRequest transactionRequest){
        Transaction transaction=Transaction.builder()
                .fromUser(transactionRequest.getFromUser())
                .toUser(transactionRequest.getToUser())
                .amount(transactionRequest.getAmount())
                .status(TransactionStatus.PENDING)
                .transactionTime(String.valueOf(new Date()))
                .build();

        transactionRepository.save(transaction);

        //kafka
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("fromUser",transactionRequest.getFromUser());
        jsonObject.put("toUser",transactionRequest.getToUser());
        jsonObject.put("amount",transactionRequest.getAmount());
        jsonObject.put("transactionId",transaction.getTransactionId());

        String message=jsonObject.toString();
        kafkaTemplate.send("create_transaction",message);
    }
    @KafkaListener(topics = {"update_transaction"},groupId = "avengers")
    public void updateTransaction(String message) throws JsonProcessingException{
        JSONObject transactionRequest= objectMapper.readValue(message,JSONObject.class);

        String transactionId=(String) transactionRequest.get("transactionId");
        String status=(String) transactionRequest.get("status");

        Transaction transaction=transactionRepository.findByTransactionId(transactionId);
        if(status=="SUCCESS") {
            transaction.setStatus(TransactionStatus.SUCCESS);
        }else{
            transaction.setStatus(TransactionStatus.FAILED);
        }

        transactionRepository.save(transaction);
    }
}
