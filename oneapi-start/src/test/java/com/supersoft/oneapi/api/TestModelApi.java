package com.supersoft.oneapi.api;

import com.alibaba.fastjson.JSON;
import com.supersoft.oneapi.util.OneapiHttpUtils;
import dev.ai4j.openai4j.OpenAiClient;
import dev.ai4j.openai4j.chat.ChatCompletionChoice;
import dev.ai4j.openai4j.chat.ChatCompletionRequest;
import dev.ai4j.openai4j.chat.ChatCompletionResponse;
import dev.ai4j.openai4j.embedding.EmbeddingRequest;
import dev.ai4j.openai4j.embedding.EmbeddingResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.TimeUnit.SECONDS;

@Slf4j
public class TestModelApi {
    private static final String BASE_URL = "http://localhost:7001/v1";
    private static final String OPENAI_API_KEY = "sk-oneapi-oneapi";

    OpenAiClient client = OpenAiClient
            .builder()
            .baseUrl(BASE_URL)
            .openAiApiKey(OPENAI_API_KEY)
            .withPersisting()
            .logRequests()
            .logResponses()
            .build();

    /**
     * 验证llm
     */
    public void llmAsyncTest() throws Exception {
        CompletableFuture<String> future = new CompletableFuture<>();
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model("gpt-4o-mini")
                .addUserMessage("你的模型名称是什么, 你的信息更新截止时间是什么")
                .build();
        StringBuilder responseBuilder = new StringBuilder();
        client.chatCompletion(request)
                .onPartialResponse(response -> {
                    List<ChatCompletionChoice> choices = response.choices();
                    if (choices.isEmpty()) {
                        return;
                    }
                    String content = choices.getFirst().delta().content();
                    if (content != null) {
                        log.info("content: {}", content);
                        responseBuilder.append(content);
                    }
                })
                .onComplete(() -> future.complete(responseBuilder.toString()))
                .onError(future::completeExceptionally)
                .execute();
        String response = future.get(30, SECONDS);
        log.info("response:{}", response);
    }

    public void llmSyncTest() throws Exception {
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model("gpt-4o")
                .addUserMessage("你的模型名称是什么, 你的信息更新截止时间是什么")
                .build();
        ChatCompletionResponse response = client.chatCompletion(request).execute();
        log.info("response:{}", response);
    }

    public void llmSyncProviderTest() throws Exception {
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model("openrouter:gpt-4o-mini")
                .addUserMessage("你的模型名称是什么, 你的信息更新截止时间是什么")
                .build();
        ChatCompletionResponse response = client.chatCompletion(request).execute();
        log.info("response:{}", response);
    }

    /**
     * 验证embedding
     */
    public void embeddingTest() throws Exception {
        EmbeddingRequest request = EmbeddingRequest.builder()
                .model("text-embedding-v3").input("你好, 你叫什么名字").build();
        long start = System.currentTimeMillis();
        EmbeddingResponse response = client.embedding(request).execute();
        log.info("cost:{}", System.currentTimeMillis() - start);
        log.info("response:{}", response);
    }

    /**
     * 验证ocr
     */
    public void ocrTest() {
        String data = JSON.toJSONString(Map.of(
                "url", "https://xx/xx.jpg",
                "model", "ocr-ali-v1"
        ));
        Map<String, String> head = getHead();
        String post = OneapiHttpUtils.post(BASE_URL + "/ocr",
                data, head, 60000);
        log.info(post);
    }

    private static Map<String, String> getHead() {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/json");
        head.put("Authorization", "Bearer " + OPENAI_API_KEY);
        return head;
    }
}
