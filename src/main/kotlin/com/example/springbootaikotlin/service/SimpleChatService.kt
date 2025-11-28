package com.example.springbootaikotlin.service

import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.memory.ChatMemory
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class SimpleChatService(
    private val chatClient: ChatClient
) {

    fun stream(prompt: Prompt, conversationId: String): Flux<String> {
        return buildChatClientRequestSpec(prompt, conversationId).stream().content()
    }

    private fun buildChatClientRequestSpec(prompt: Prompt, conversationId: String): ChatClient.ChatClientRequestSpec {
        return chatClient.prompt(prompt).advisors {
            it.param(ChatMemory.CONVERSATION_ID, conversationId)
        }
    }
}
