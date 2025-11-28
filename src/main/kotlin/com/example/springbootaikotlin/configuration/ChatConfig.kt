package com.example.springbootaikotlin.configuration

import ch.qos.logback.classic.LoggerContext
import com.example.springbootaikotlin.service.ChatService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor
import org.springframework.ai.chat.client.advisor.api.Advisor
import org.springframework.ai.chat.memory.ChatMemory
import org.springframework.ai.chat.memory.MessageWindowChatMemory
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*

@Configuration
class ChatConfig {

    @Bean
    fun chatClient(chatClient: ChatClient.Builder, advisors: List<Advisor>): ChatClient {
        return chatClient.defaultAdvisors(advisors).build()
    }

    @Bean
    fun simpleLoggerAdvisor(): SimpleLoggerAdvisor {
        return SimpleLoggerAdvisor.builder().build()
    }

    @Bean
    fun chatMemory(): ChatMemory {
        return MessageWindowChatMemory
            .builder()
            .maxMessages(10)
            .build() // InMemoryChatMemoryRepository 사용
    }

    @Bean
    fun messageChatMemoryAdvisor(chatMemory: ChatMemory): MessageChatMemoryAdvisor {
        return MessageChatMemoryAdvisor.builder(chatMemory).build()
    }

    @Bean
    @ConditionalOnProperty(prefix = "spring.application", name = ["cli"], havingValue = "true")
    fun cliMessageChatMemoryAdvisor(
        @Value("\${spring.application.name}") applicationName: String,
        chatService: ChatService
    ): CommandLineRunner {
        return CommandLineRunner {
            val context = LoggerFactory.getILoggerFactory() as LoggerContext
            val rootLogger = context.getLogger(Logger.ROOT_LOGGER_NAME)
            rootLogger.detachAppender("CONSOLE")

            println("\n$applicationName CLI Chat Bot")

            Scanner(System.`in`).use { scanner ->
                while (true) {
                    print("\nUser: ")
                    val userMessage = scanner.nextLine()
                    chatService.stream(Prompt.builder().content(userMessage).build(), "cli")
                        .doFirst {
                            print("\nAssistant: ")
                        }
                        .doOnNext {
                            print(it)
                        }
                        .doOnComplete {
                            println(it)
                        }
                        .blockLast()
                }
            }
        }
    }
}
