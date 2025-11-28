package com.example.springbootaikotlin.configuration

import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.api.Advisor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ChatClientConfig {

    @Bean
    fun chatClient(chatClient: ChatClient.Builder, advisors: List<Advisor>): ChatClient {
        return chatClient.defaultAdvisors(advisors).build()
    }
}
