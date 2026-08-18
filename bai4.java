package com.example.springai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.function.Function;

@RestController
public class FunctionCallingController {

    private final ChatClient chatClient;

    public FunctionCallingController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder
                .defaultFunctions("weatherFunction")
                .build();
    }

    @GetMapping("/ai/agent")
    public String askAgent(@RequestParam(value = "message", defaultValue = "Thời tiết ở Hà Nội hôm nay thế nào?") String message) {
        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }
}

@Configuration
class TempConfig {
    public record WeatherRequest(String location) {}
    public record WeatherResponse(String weatherDetails) {}

    @Bean
    public Function<WeatherRequest, WeatherResponse> weatherFunction() {
        return request -> {
            String location = request.location();
            if (location.equalsIgnoreCase("Hà Nội") || location.contains("Hanoi")) {
                return new WeatherResponse("28°C, Trời có mây rải rác, độ ẩm 70%");
            } else if (location.equalsIgnoreCase("Sài Gòn") || location.contains("TPHCM")) {
                return new WeatherResponse("33°C, Nắng nóng gay gắt");
            } 
            return new WeatherResponse("25°C, Thời tiết ôn hòa mát mẻ");
        };
    }
}