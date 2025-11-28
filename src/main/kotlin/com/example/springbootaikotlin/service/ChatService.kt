package com.example.springbootaikotlin.service

import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.memory.ChatMemory
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class ChatService(
    private val chatClient: ChatClient
) {

    fun stream(prompt: Prompt, conversationId: String): Flux<String> {
        return buildChatClientRequestSpec(prompt, conversationId).stream().content()
    }

    fun call(prompt: Prompt, conversationId: String) =
        buildChatClientRequestSpec(prompt, conversationId).call().chatResponse()

    fun callEmotionEvaluation(
        prompt: Prompt,
        conversationId: String,
    ): EmotionEvaluation {
        return buildChatClientRequestSpec(prompt, conversationId).call().entity(EmotionEvaluation::class.java)!!
    }

    enum class Emotion {
        VERY_NEGATIVE, NEGATIVE, NEUTRAL, POSITIVE, VERY_POSITIVE
    }

    data class EmotionEvaluation(
        val emotion: Emotion,
        val reason: List<String>,
    )

    private fun buildChatClientRequestSpec(prompt: Prompt, conversationId: String): ChatClient.ChatClientRequestSpec {
        return chatClient.prompt(prompt).advisors {
            it.param(ChatMemory.CONVERSATION_ID, conversationId)
        }
    }
}
