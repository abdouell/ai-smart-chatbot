package com.chatbot.aifaqchatbot.service;

import com.chatbot.aifaqchatbot.model.FaqResponse;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.BrowserCallable;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
@BrowserCallable
@AnonymousAllowed
public class QuestionClassifierService {
    private ChatClient chatClient;


    public QuestionClassifierService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public FaqResponse findAnswer(String userQuestion) {
        String prompteAccueil = """
                Je vais t'envoyer un message utilisateur.
                    \s
                     Ton travail est d'analyser ce message et de répondre selon les règles suivantes :
                    \s
                     Si le message est de nature conversationnelle (salutations, remerciements, questions\s
                     informelles), réponds poliment et brièvement comme un professionnel.
                    \s
                     Si le message exprime un besoin général d'aide sans préciser un sujet spécifique,\s
                     réponds professionnellement et invite l'utilisateur à préciser sa demande.
                    \s
                     Si le message concerne un sujet spécifique lié aux produits ou services, réponds uniquement\s
                     avec le mot FAQ.
                """;

        FaqResponse response = chatClient.prompt(prompteAccueil)
                .user(userQuestion)
                .call()
                .entity(FaqResponse.class);

        return response;
    }

}
