package com.example.teamproject.card.service;


import com.example.teamproject.card.controller.form.CardRequestForm;
import com.example.teamproject.card.entity.Card;
import com.example.teamproject.card.repository.CardRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {
    final private CardRepository cardRepository;

    // 현재 오류나고 있습니다. 추후 수정 바랍니다.
    // @Value("${spring.web.cors.allowed-origins}")
    // private String[] allowedOrigins;

    @Override
    public List<Card> getActivateCard() {
        List<Card> cardList = cardRepository.findByActivateTrue();
        return cardList;
    }

    @Override
    public Card cardRegister(CardRequestForm form) {
        Card newCard = form.toCard();
        Optional<Card> maybeCard = cardRepository.findByName(newCard.getName());
        if (maybeCard.isEmpty()){
            return cardRepository.save(newCard);
        }
        return null;
    }

    @Override
    public List<Card> getAgeCard() {
        RestTemplate restTemplate = new RestTemplate();
        String fastApiUrl = "http://15.165.11.253:3002/age-recommend-card";
        String response = restTemplate.getForObject(fastApiUrl, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        List<Card> ageCardList = new ArrayList<>();
        try {
            List<String> ageCardNumberList = objectMapper.readValue(response, new TypeReference<List<String>>() {
            });
            log.info(ageCardNumberList.toString());
            for (String cardNumber : ageCardNumberList) {
                Optional<Card> maybeCardNumber = cardRepository.findByCardId(Long.valueOf(cardNumber));
                if (maybeCardNumber.isPresent()) {
                    ageCardList.add(maybeCardNumber.get());
                } else {
                    log.info("없는 카드 번호 입니다.");
                    return null;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return ageCardList;
    }

    @Override
    public Boolean stopCard(Long id) {
        Optional<Card> maybeCard = cardRepository.findById(id);
        if (maybeCard.isPresent()){
            Card targetCard = maybeCard.get();
            targetCard.setActivate(false);
            cardRepository.save(targetCard);

            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public Card retrieve(long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("잘못된 카드정보입니다."));
        card.increaseViewCount();
        return card;
    }

    @Override
    public List<Card> retrieveInterestList() {
        return cardRepository.findTop10ByActivateTrueOrderByViewCountDesc();
    }
}
