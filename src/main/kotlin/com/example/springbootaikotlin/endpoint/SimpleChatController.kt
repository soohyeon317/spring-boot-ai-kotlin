package com.example.springbootaikotlin.endpoint

import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.model.ChatResponse
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
class SimpleChatController(
    private val chatClient: ChatClient

) {

    @GetMapping("answer")
    fun answer(userPrompt: String): String? {
        return chatClient.prompt().user(userPrompt).call().content()
    }

    @GetMapping("call", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun call(userPrompt: String): ChatResponse? {
        return chatClient.prompt().user(userPrompt).call().chatResponse()
    }

    @GetMapping("stream", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun stream(userPrompt: String): Flux<String> {
        return chatClient.prompt().user(userPrompt).stream().content()
    }
}
