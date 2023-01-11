package com.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class WalletService {

    @Autowired
    WalletRepository walletRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    KafkaTemplate kafkaTemplate;

    @KafkaListener(topics = {"create_wallet"},groupId = "avengers")
    public void createWallet(String message) throws JsonProcessingException {

        JSONObject jsonObject=objectMapper.readValue(message,JSONObject.class);

        String userName= (String) jsonObject.get("userName");
        Wallet wallet=Wallet.builder()
                .userName(userName)
                .balance(0)
                .build();

        walletRepository.save(wallet);
    }
    @KafkaListener(topics = {"create_transaction"},groupId = "avengers")
    public void updateWallet(String message) throws JsonProcessingException {
        JSONObject jsonObject=objectMapper.readValue(message,JSONObject.class);

        String fromUser=(String) jsonObject.get("fromUser");
        String toUser=(String) jsonObject.get("toUser");
        int amount=(int) jsonObject.get("amount");
        String transactionId=(String) jsonObject.get("transactionId");

        Wallet sender=walletRepository.findByUserName(fromUser);

        int balance=sender.getBalance();
        JSONObject transactionObj=new JSONObject();
        if(balance>=amount){
            //walletRepository.updateWallet(fromUser,-1*amount);
            //walletRepository.updateWallet(toUser,amount);

            Wallet fromWallet=walletRepository.findByUserName(fromUser);
            fromWallet.setBalance(balance+amount);
            walletRepository.save(fromWallet);

            Wallet toWallet=walletRepository.findByUserName(toUser);
            toWallet.setBalance(balance-amount);
            walletRepository.save(fromWallet);

            transactionObj.put("status","SUCCESS");
            transactionObj.put("transactionId",transactionId);
        }else {
            transactionObj.put("status","FAILED");
            transactionObj.put("transactionId",transactionId);
        }

        String ack=transactionObj.toString();
        kafkaTemplate.send("update_transaction",ack);
    }
}
