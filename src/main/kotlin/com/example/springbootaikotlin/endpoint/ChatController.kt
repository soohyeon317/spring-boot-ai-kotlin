package com.example.springbootaikotlin.endpoint

import com.example.springbootaikotlin.service.ChatService
import jakarta.annotation.Nullable
import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty
import org.springframework.ai.chat.messages.Message
import org.springframework.ai.chat.messages.SystemMessage
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.model.ChatResponse
import org.springframework.ai.chat.prompt.DefaultChatOptions
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import java.util.*
import java.util.function.Predicate

@RestController
@RequestMapping("/chat")
class ChatController(
    private val chatService: ChatService,
) {

    data class PromptBody(
        @field:NotEmpty val conversationId: String? = null,
        @field:NotEmpty val userPrompt: String? = null,
        @field:Nullable val systemPrompt: String? = null,
        val chatOptions: DefaultChatOptions? = null
    )

    @PostMapping("/call", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun call(@RequestBody @Valid promptBody: PromptBody): ChatResponse? {
        return chatService.call(buildPrompt(promptBody), promptBody.conversationId!!)
    }

    @PostMapping("/stream", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun stream(@RequestBody @Valid promptBody: PromptBody): Flux<String> {
        return chatService.stream(buildPrompt(promptBody), promptBody.conversationId!!)
    }

    @PostMapping("/emotion", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun emotion(@RequestBody @Valid promptBody: PromptBody): ChatService.EmotionEvaluation {
        return chatService.callEmotionEvaluation(buildPrompt(promptBody), promptBody.conversationId!!)
    }

    companion object {

        private fun buildPrompt(promptBody: PromptBody): Prompt {
            val messages = mutableListOf<Message>()

            Optional.ofNullable(promptBody.systemPrompt).filter(Predicate.not(String::isEmpty))
                .map(SystemMessage.builder()::text).map(SystemMessage.Builder::build).ifPresent(messages::add)

            messages.add(UserMessage.builder().text(promptBody.userPrompt!!).build())

            val promptBuilder = Prompt.builder().messages(messages)

            Optional.ofNullable(promptBody.chatOptions).ifPresent(promptBuilder::chatOptions)

            return promptBuilder.build()
        }
    }
}