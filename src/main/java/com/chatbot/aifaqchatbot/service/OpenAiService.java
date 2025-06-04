package com.chatbot.aifaqchatbot.service;

import com.chatbot.aifaqchatbot.model.FaqResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OpenAiService {
    private ChatClient chatClient;

    public OpenAiService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public FaqResponse generateResponse(String userQuestion, List<String> faqContext) {
        try {

            // Create system prompt with FAQ context
            String systemPrompt =   "Vous êtes un assistant utile qui répond aux questions basées sur la FAQ fournie. " +
                    "Les données de la FAQ sont fournies au format JSON. " +
                    "Si aucune réponse trouvé indiquez poliment que vous ne disposez pas de cette information et afficher: Je n’ai pas trouvé de réponse à votre question. Veuillez contacter votre gestionnaire à l’adresse samia@one.be ou au 012345678." +
                    "IMPORTANT: Si vous trouvez des questions dans la FAQ qui sont proches ou similaires à la question de l'utilisateur, " +

                    "vous DEVEZ lister ces questions similaires à la fin de votre réponse au format suivant:" +

                    "---\n" +
                    "Questions similaires trouvées:\n" +
                    "1. **[Question similaire 1]**\n\n" +
                    "   Réponse: [Réponse correspondante 1]\n\n" +
                    "2. **[Question similaire 2]**\n\n" +
                    "   Réponse: [Réponse correspondante 2]\n" +
                    "---\n\n" +
                    "Assurez-vous que chaque question est en GRAS (entre ** **) et qu'il y ait un retour à la ligne après chaque question et chaque réponse. " +

                    "Si aucune question similaire n'est trouvée, indiquez poliment que vous ne disposez pas de cette information." +
                    "Voici les données de la FAQ : "   + String.join("\n", faqContext) + "\n```\n" +
                    "Question de l'utilisateur: " + userQuestion
                    ;

            // Get chat response
            FaqResponse response = chatClient.prompt(systemPrompt)
                    .user(userQuestion)
                    .call()
                    .entity(FaqResponse.class)
                    ;

            return response;

        } catch (Exception e) {
            e.printStackTrace();
            return new FaqResponse("I apologize, but I'm having trouble processing your question right now.", null, null);
        }
    }

    public String generateResponse2(String userQuestion, List<String> faqContext) {
        try {
            // Create system prompt with FAQ context
            String systemPrompt = "Vous travaillez au service client. Utilise la liste FAQ pour répondre à la question du client: \n"
                    + String.join("\n", faqContext)
                    + "\n Si tu ne trouves pas de réponse, réponds que tu ne sais pas. " +
                    "Ensuite, affiche la liste des faq(question, réponse) les plus proche " +
                    "qui sont les plus proches de la question de l'utilisateur " +
                    "en te basant seulement sur le contexte fourni." +
                    "la liste doit être bien formatté avec un retour à la ligne entre question et réponse et la question en gras ";

            // Get chat response
            String response = chatClient.prompt(systemPrompt)
                    .user(userQuestion)
                    .call()
                    .content();

            return response;

        } catch (Exception e) {
            e.printStackTrace();
            return "I apologize, but I'm having trouble processing your question right now.";
        }
    }

}


