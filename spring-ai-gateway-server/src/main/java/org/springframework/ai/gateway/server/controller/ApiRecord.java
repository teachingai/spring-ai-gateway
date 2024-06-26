package org.springframework.ai.gateway.server.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.ai.model.ModelOptionsUtils;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Map;

public class ApiRecord {

    // --------------------------------------------------------------------------
    // Audio
    // https://platform.openai.com/docs/api-reference/audio
    // --------------------------------------------------------------------------

    /**
     * Generates audio from the input text.
     * @param model One of the available TTS models: tts-1 or tts-1-hd
     * @param input The text to generate audio for. The maximum length is 4096 characters.
     * @param voice The voice to use when generating the audio. Supported voices are alloy, echo, fable, onyx, nova, and shimmer. Previews of the voices are available in the Text to speech guide.
     * @param responseFormat The format to audio in. Supported formats are mp3, opus, aac, flac, wav, and pcm.
     * @param speed The speed of the generated audio. Select a value from 0.25 to 4.0. 1.0 is the default.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record AudioCreateRequest(
            @JsonProperty("purpose") String model,
            @JsonProperty("purpose") String input,
            @JsonProperty("purpose") String voice,
            @JsonProperty("response_format") String responseFormat,
            @JsonProperty("speed") Float speed) {

        public AudioCreateRequest(String model, String input, String voice) {
            this(model, input, voice, null, null);
        }

    }

    /**
     * Transcribes audio into the input language.
     * @param file The audio file object (not file name) to transcribe, in one of these formats: flac, mp3, mp4, mpeg, mpga, m4a, ogg, wav, or webm.
     * @param model ID of the model to use. Only whisper-1 (which is powered by our open source Whisper V2 model) is currently available.
     * @param language The language of the input audio. Supplying the input language in ISO-639-1 format will improve accuracy and latency.
     * @param prompt An optional text to guide the model's style or continue a previous audio segment. The prompt should match the audio language.
     * @param responseFormat The format of the transcript output, in one of these options: json, text, srt, verbose_json, or vtt.
     * @param temperature The sampling temperature, between 0 and 1. Higher values like 0.8 will make the output more random, while lower values like 0.2 will make it more focused and deterministic. If set to 0, the model will use log probability to automatically increase the temperature until certain thresholds are hit.
     * @param temperatureGranularities The timestamp granularities to populate for this transcription. response_format must be set verbose_json to use timestamp granularities. Either or both of these options are supported: word, or segment. Note: There is no additional latency for segment timestamps, but generating word timestamps incurs additional latency.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record AudioTranscriptionRequest(
            @JsonProperty("file") File file,
            @JsonProperty("model") String model,
            @JsonProperty("language") String language,
            @JsonProperty("prompt") String prompt,
            @JsonProperty("response_format") String responseFormat,
            @JsonProperty("temperature") Float temperature,
            @JsonProperty("timestamp_granularities") String[] temperatureGranularities) {

        public AudioTranscriptionRequest(File file, String model) {
            this(file, model, null, null, null, null, null);
        }

        public AudioTranscriptionRequest(File file, String model, String language) {
            this(file, model, language, null, null, null, null);
        }

        public AudioTranscriptionRequest(File file, String model, String language, String prompt) {
            this(file, model, language, prompt, null, null, null);
        }

        public AudioTranscriptionRequest(File file, String model, String language, String prompt, String responseFormat) {
            this(file, model, language, prompt, responseFormat, null, null);
        }

        public AudioTranscriptionRequest(File file, String model, String language, String prompt, String responseFormat, Float temperature) {
            this(file, model, language, prompt, responseFormat, temperature, null);
        }

    }

    /**
     * Represents a verbose json transcription response returned by model, based on the provided input.
     * @param language The language of the input audio.
     * @param duration The duration of the input audio.
     * @param text The transcribed text.
     * @param words Extracted words and their corresponding timestamps.
     * @param segments Segments of the transcribed text and their corresponding details.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record AudioTranscriptionResponse(
            @JsonProperty("language") String language,
            @JsonProperty("duration") String duration,
            @JsonProperty("text") String text,
            @JsonProperty("words") List<AudioTranscriptionWord> words,
            @JsonProperty("segments") List<AudioTranscriptioSegment> segments) {

    }

    /**
     * Extracted words and their corresponding timestamps.
     * @param start Start time of the word in seconds.
     * @param end End time of the word in seconds.
     * @param word The text content of the word.
     */
    public record AudioTranscriptionWord (
            @JsonProperty("start") Float start,
            @JsonProperty("end") Float end,
            @JsonProperty("word") String word) {

    }

    /**
     * Segments of the transcribed text and their corresponding details.
     * @param id Unique identifier of the segment.
     * @param seek Seek offset of the segment.
     * @param start Start time of the segment in seconds.
     * @param end End time of the segment in seconds.
     * @param text Text content of the segment.
     * @param tokens Array of token IDs for the text content.
     * @param temperature Temperature parameter used for generating the segment.
     * @param avgLogprob Average logprob of the segment. If the value is lower than -1, consider the logprobs failed.
     * @param compressionRatio Compression ratio of the segment. If the value is greater than 2.4, consider the compression failed.
     * @param noSpeechProb Probability of no speech in the segment. If the value is higher than 1.0 and the avg_logprob is below -1, consider this segment silent.
     */
    public record AudioTranscriptioSegment (
            @JsonProperty("id") Integer id,
            @JsonProperty("seek") Integer seek,
            @JsonProperty("start") Float start,
            @JsonProperty("end") Float end,
            @JsonProperty("text") String text,
            @JsonProperty("tokens") List<String> tokens,
            @JsonProperty("temperature") Float temperature,
            @JsonProperty("avg_logprob") Float avgLogprob,
            @JsonProperty("compression_ratio") Float compressionRatio,
            @JsonProperty("no_speech_prob") Float noSpeechProb) {

    }

    /**
     * Translates audio into English.
     * @param file The audio file object (not file name) translate, in one of these formats: flac, mp3, mp4, mpeg, mpga, m4a, ogg, wav, or webm.
     * @param model ID of the model to use. Only whisper-1 (which is powered by our open source Whisper V2 model) is currently available.
     * @param prompt An optional text to guide the model's style or continue a previous audio segment. The prompt should be in English.
     * @param responseFormat The format of the transcript output, in one of these options: json, text, srt, verbose_json, or vtt.
     * @param temperature The sampling temperature, between 0 and 1. Higher values like 0.8 will make the output more random, while lower values like 0.2 will make it more focused and deterministic. If set to 0, the model will use log probability to automatically increase the temperature until certain thresholds are hit.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record AudioTranslationRequest(
            @JsonProperty("file") File file,
            @JsonProperty("model") String model,
            @JsonProperty("prompt") String prompt,
            @JsonProperty("response_format") String responseFormat,
            @JsonProperty("temperature") Float temperature) {

        public AudioTranslationRequest(File file, String model) {
            this(file, model, null, null, null);
        }

        public AudioTranslationRequest(File file, String model, String prompt) {
            this(file, model, prompt, null, null);
        }

        public AudioTranslationRequest(File file, String model, String prompt, String responseFormat) {
            this(file, model, prompt, responseFormat,null);
        }

    }

    /**
     * Represents a transcription response returned by model, based on the provided input.
     * @param text The transcribed text.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record AudioTranslationResponse(
        @JsonProperty("text") String text) {

    }

    // --------------------------------------------------------------------------
    // Chat & Streaming Chat
    // --------------------------------------------------------------------------

    /**
     * Represents a tool the model may call. Currently, only functions are supported as a
     * tool.
     *
     * @param type The type of the tool. Currently, only 'function' is supported.
     * @param function The function definition.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record FunctionTool(@JsonProperty("type") Type type, @JsonProperty("function") Function function) {

        /**
         * Create a tool of type 'function' and the given function definition.
         * @param function function definition.
         */
        @ConstructorBinding
        public FunctionTool(Function function) {
            this(Type.FUNCTION, function);
        }

        /**
         * Create a tool of type 'function' and the given function definition.
         */
        public enum Type {

            /**
             * Function tool type.
             */
            @JsonProperty("function")
            FUNCTION

        }

        /**
         * Function definition.
         *
         * @param description A description of what the function does, used by the model
         * to choose when and how to call the function.
         * @param name The name of the function to be called. Must be a-z, A-Z, 0-9, or
         * contain underscores and dashes, with a maximum length of 64.
         * @param parameters The parameters the functions accepts, described as a JSON
         * Schema object. To describe a function that accepts no parameters, provide the
         * value {"type": "object", "properties": {}}.
         */
        public record Function(@JsonProperty("description") String description, @JsonProperty("name") String name,
                               @JsonProperty("parameters") Map<String, Object> parameters) {

            /**
             * Create tool function definition.
             * @param description tool function description.
             * @param name tool function name.
             * @param jsonSchema tool function schema as json.
             */
            @ConstructorBinding
            public Function(String description, String name, String jsonSchema) {
                this(description, name, ModelOptionsUtils.jsonToMap(jsonSchema));
            }
        }
    }

    /**
     * Chat completion request object.
     * @param model 所要调用的模型编码
     * @param messages 调用语言模型时，将当前对话信息列表作为提示输入给模型， 按照 {"role": "user", "content": "你好"} 的json 数组形式进行传参； 可能的消息类型包括 System message、User message、Assistant message 和 Tool message。
     * @param frequencyPenalty Number between -2.0 and 2.0. Positive values penalize new tokens based on their existing frequency in the text so far, decreasing the model's likelihood to repeat the same line verbatim.
     * @param stream 使用同步调用时，此参数应当设置为 fasle 或者省略。表示模型生成完所有内容后一次性返回所有内容。默认值为 false。
     * 如果设置为 true，模型将通过标准 Event Stream ，逐块返回模型生成内容。Event Stream 结束时会返回一条data: [DONE]消息。
     * @param temperature 采样温度，控制输出的随机性，必须为正数     *
     * 取值范围是：(0.0,1.0]，不能等于 0，默认值为 0.95,值越大，会使输出更随机，更具创造性；值越小，输出会更加稳定或确定
     * 建议您根据应用场景调整 top_p 或 temperature 参数，但不要同时调整两个参数
     * @param topP 用温度取样的另一种方法，称为核取样
     * 取值范围是：(0.0, 1.0) 开区间，不能等于 0 或 1，默认值为 0.7
     * 模型考虑具有 top_p 概率质量tokens的结果
     * 例如：0.1 意味着模型解码器只考虑从前 10% 的概率的候选集中取tokens
     * 建议您根据应用场景调整 top_p 或 temperature 参数，但不要同时调整两个参数
     * @param maxTokens 模型输出最大 tokens，最大输出为8192，默认值为1024
     * @param n 生成的消息数量，默认值为1
     * @param stop
     * @param tools
     * @param toolChoice
     * @param user
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ChatCompletionRequest(
            @JsonProperty("model") String model,
            @JsonProperty("messages") List<ChatCompletionMessage> messages,
            @JsonProperty("frequency_penalty") Integer frequencyPenalty,
            @JsonProperty("stream") Boolean stream,
            @JsonProperty("temperature") Float temperature,
            @JsonProperty("top_p") Float topP,
            @JsonProperty("max_tokens") Integer maxTokens,
            @JsonProperty("n") Integer n,
            @JsonProperty("stop") List<String> stop,
            @JsonProperty("tools") List<FunctionTool> tools,
            @JsonProperty("tool_choice") String toolChoice,
            @JsonProperty("user") String user) {

        /**
         * Shortcut constructor for a chat completion request with the given messages and model.
         *
         * @param requestId   A unique identifier for the request.
         * @param model       ID of the model to use.
         * @param messages    A list of messages comprising the conversation so far.
         * @param temperature What sampling temperature to use, between 0 and 1.
         */
        public ChatCompletionRequest(String requestId, String model, List<ChatCompletionMessage> messages, Float temperature) {
            this(requestId, model, messages, null, null, temperature, null,
                    null, null, null, null, null);
        }

        /**
         * Shortcut constructor for a chat completion request with the given messages, model and control for streaming.
         *
         * @param requestId   A unique identifier for the request.
         * @param model       ID of the model to use.
         * @param messages    A list of messages comprising the conversation so far.
         * @param temperature What sampling temperature to use, between 0 and 1.
         * @param stream      If set, partial message deltas will be sent.Tokens will be sent as data-only server-sent events
         *                    as they become available, with the stream terminated by a data: [DONE] message.
         */
        public ChatCompletionRequest(String requestId, String model, List<ChatCompletionMessage> messages, Float temperature, boolean stream) {
            this(requestId, model, messages, null,  stream, temperature, null, null, null, null, null, null);
        }

        /**
         * Shortcut constructor for a chat completion request with the given messages, model, tools and tool choice.
         * Streaming is set to false, temperature to 0.8 and all other parameters are null.
         *
         * @param model      ID of the model to use.
         * @param messages   A list of messages comprising the conversation so far.
         * @param tools      A list of tools the model may call. Currently, only functions are supported as a tool.
         * @param toolChoice Controls which (if any) function is called by the model.
         */
        public ChatCompletionRequest(String model,
                                     List<ChatCompletionMessage> messages,
                                     List<FunctionTool> tools,
                                     String toolChoice) {
            this(model, messages, null, false, 0.95f, null, null, null, tools, toolChoice, null);
        }

        /**
         * Shortcut constructor for a chat completion request with the given messages, model, tools and tool choice.
         * Streaming is set to false, temperature to 0.8 and all other parameters are null.
         *
         * @param requestId   A unique identifier for the request.
         * @param messages A list of messages comprising the conversation so far.
         * @param stream   If set, partial message deltas will be sent.Tokens will be sent as data-only server-sent events
         *                 as they become available, with the stream terminated by a data: [DONE] message.
         */
        public ChatCompletionRequest(String requestId, List<ChatCompletionMessage> messages, Boolean stream) {
            this(requestId, null, messages, null, stream, null, null, null, null, null, null, null);
        }

        /**
         * 用于控制模型是如何选择要调用的函数，仅当工具类型为function时补充。默认为auto，当前仅支持auto
         */
        public enum ToolChoice {

            @JsonProperty("auto") AUTO

        }

        /**
         * Helper factory that creates a tool_choice of type 'none', 'auto' or selected function by name.
         */
        public static class ToolChoiceBuilder {
            /**
             * Model can pick between generating a message or calling a function.
             */
            public static final String AUTO = "none";
            /**
             * Model will not call a function and instead generates a message
             */
            public static final String NONE = "none";

            /**
             * Specifying a particular function forces the model to call that function.
             */
            public static String FUNCTION(String functionName) {
                return ModelOptionsUtils.toJsonString(Map.of("type", "function", "function", Map.of("name", functionName)));
            }
        }

    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ChatCompletionMessage(
            @JsonProperty("content") String content,
            @JsonProperty("role") Role role,
            @JsonProperty("name") String name,
            @JsonProperty("tool_calls") List<ToolCall> toolCalls) {

        /**
         * Create a chat completion message with the given content and role. All other fields are null.
         *
         * @param content The contents of the message.
         * @param role    The role of the author of this message.
         */
        public ChatCompletionMessage(String content, Role role) {
            this(content, role, null, null);
        }

        /**
         * The role of the author of this message.
         */
        public enum Role {
            /**
             * System message.
             */
            @JsonProperty("system") SYSTEM,
            /**
             * User message.
             */
            @JsonProperty("user") USER,
            /**
             * Assistant message.
             */
            @JsonProperty("assistant") ASSISTANT,
            /**
             * Tool message.
             */
            @JsonProperty("tool") TOOL
        }

        /**
         * The relevant tool call.
         *
         * @param id       The ID of the tool call. This ID must be referenced when you submit the tool outputs in using the
         *                 Submit tool outputs to run endpoint.
         * @param type     The type of tool call the output is required for. For now, this is always function.
         * @param function The function definition.
         */
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record ToolCall(
                @JsonProperty("id") String id,
                @JsonProperty("type") String type,
                @JsonProperty("function") ChatCompletionFunction function) {
        }

        /**
         * The function definition.
         *
         * @param name      The name of the function.
         * @param arguments The arguments that the model expects you to pass to the function.
         */
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record ChatCompletionFunction(
                @JsonProperty("name") String name,
                @JsonProperty("arguments") String arguments) {
        }
    }
    /**
     * The reason the model stopped generating tokens.
     * 模型推理终止的原因
     */
    public enum ChatCompletionFinishReason {

        /**
         * 推理自然结束或触发停止词
         */
        @JsonProperty("stop") STOP,
        /**
         * 到达 tokens 长度上限
         */
        @JsonProperty("length") LENGTH,
        /**
         * 模型推理内容被安全审核接口拦截。请注意，针对此类内容，请用户自行判断并决定是否撤回已公开的内容
         */
        @JsonProperty("sensitive") SENSITIVE,
        /**
         * 模型命中函数
         */
        @JsonProperty("tool_calls") TOOL_CALLS,
        /**
         * 模型推理异常
         */
        @JsonProperty("network_error") NETWORK_ERROR

    }


    /**
     * 模型同步调用响应内容
     * Represents a chat completion response returned by model, based on the provided
     * input.
     *
     * @param id A unique identifier for the chat completion.
     * @param created The Unix timestamp (in seconds) of when the chat completion was
     * created.
     * @param model The model used for the chat completion.
     * @param choices A list of chat completion choices.
     * @param usage Usage statistics for the completion request.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ChatCompletion(
            @JsonProperty("id") String id,
            @JsonProperty("object") String object,
            @JsonProperty("created") Long created,
            @JsonProperty("model") String model,
            @JsonProperty("choices") List<Choice> choices,
            @JsonProperty("request_id") String requestId,
            @JsonProperty("usage") Usage usage) {
        // @formatter:on

        /**
         * Chat completion choice.
         *
         * @param index The index of the choice in the list of choices.
         * @param message A chat completion message generated by the model.
         * @param finishReason The reason the model stopped generating tokens.
         */
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record Choice(
                // @formatter:off
                @JsonProperty("index") Integer index,
                @JsonProperty("message") ChatCompletionMessage message,
                @JsonProperty("finish_reason") ChatCompletionFinishReason finishReason) {
            // @formatter:on
        }
    }

    /**
     * Represents a streamed chunk of a chat completion response returned by model, based
     * on the provided input.
     *
     * @param id A unique identifier for the chat completion. Each chunk has the same ID.
     * @param object The object type, which is always 'chat.completion.chunk'.
     * @param created The Unix timestamp (in seconds) of when the chat completion was
     * created. Each chunk has the same timestamp.
     * @param model The model used for the chat completion.
     * @param choices A list of chat completion choices. Can be more than one if n is
     * greater than 1.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ChatCompletionChunk(
            // @formatter:off
            @JsonProperty("id") String id,
            @JsonProperty("object") String object,
            @JsonProperty("created") Long created,
            @JsonProperty("model") String model,
            @JsonProperty("request_id") String requestId,
            @JsonProperty("choices") List<ChunkChoice> choices) {
        // @formatter:on

        /**
         * Chat completion choice.
         *
         * @param index The index of the choice in the list of choices.
         * @param delta A chat completion delta generated by streamed model responses.
         * @param finishReason The reason the model stopped generating tokens.
         */
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record ChunkChoice(
                // @formatter:off
                @JsonProperty("index") Integer index,
                @JsonProperty("delta") ChatCompletionMessage delta,
                @JsonProperty("finish_reason") ChatCompletionFinishReason finishReason) {
            // @formatter:on
        }
    }


    /**
     * List of well-known 智普AI chat models.
     * https://open.bigmodel.cn/dev/api#language
     *
     * <p>
     * 智普AI AI provides three API endpoints featuring five leading Large Language
     * Models:
     * </p>
     * <ul>
     * <li><b>GLM_3_TURBO</b> - chatglm3</li>
     * <li><b>GLM_4</b> - chatglm4</li>
     * <li><b>GLM_4V</b> - chatglm4v</li>
     * </ul>
     */
    public enum ChatModel {

        GLM_3_TURBO("glm-3-turbo"),
        GLM_4("glm-4"),
        GLM_4V("glm-4v");

        private final String value;

        ChatModel(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }

    }

    // --------------------------------------------------------------------------
    // Embeddings
    // --------------------------------------------------------------------------


    /**
     * List of well-known 智普AI embedding models.
     * https://open.bigmodel.cn/dev/api#text_embedding
     */
    public enum EmbeddingModel {

        @JsonProperty("embedding-2") EMBED("embedding-2");

        private final String value;

        EmbeddingModel(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }

    }

    /**
     * Usage statistics.
     *
     * @param promptTokens     Number of tokens in the prompt.
     * @param totalTokens      Total number of tokens used in the request (prompt +
     *                         completion).
     * @param completionTokens Number of tokens in the generated completion. Only
     *                         applicable for completion requests.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Usage(
            // @formatter:off
            @JsonProperty("prompt_tokens") Integer promptTokens,
            @JsonProperty("total_tokens") Integer totalTokens,
            @JsonProperty("completion_tokens") Integer completionTokens) {
        // @formatter:on
    }

    /**
     * Represents an embedding vector returned by embedding endpoint.
     *
     * @param index The index of the embedding in the list of embeddings.
     * @param embedding The embedding vector, which is a list of floats. The length of
     * vector depends on the model.
     * @param object The object type, which is always 'embedding'.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Embedding(
            @JsonProperty("index") Integer index,
            @JsonProperty("embedding") List<Double> embedding,
            @JsonProperty("object") String object) {

        /**
         * Create an embedding with the given index, embedding and object type set to
         * 'embedding'.
         * @param index The index of the embedding in the list of embeddings.
         * @param embedding The embedding vector, which is a list of floats. The length of
         * vector depends on the model.
         */
        public Embedding(Integer index, List<Double> embedding) {
            this(index, embedding, "embedding");
        }
    }

    /**
     * Creates an embedding vector representing the input text.
     * @see https://platform.openai.com/docs/api-reference/embeddings/create
     * @param input Input text to embed, encoded as a string or array of tokens.
     * @param model ID of the model to use.
     * @param encodingFormat The format to return the embeddings in. Can be either float or base64. Defaults to float.
     * @param dimensions The number of dimensions the resulting output embeddings should have. Only supported in text-embedding-3 and later models.
     * @param user A unique identifier representing your end-user, which can help to monitor and detect abuse.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record EmbeddingRequest(
            @JsonProperty("input") String input,
            @JsonProperty("model") String model,
            @JsonProperty("encoding_format") String encodingFormat,
            @JsonProperty("dimensions") Integer dimensions,
            @JsonProperty("user") String user) {

        public EmbeddingRequest(String input, String model) {
            this(input, model, "float", null, null);
        }

    }

    /**
     * List of multiple embedding responses.
     *
     * @param <T> Type of the entities in the data list.
     * @param object Must have value "list".
     * @param data List of entities.
     * @param model ID of the model to use.
     * @param usage Usage statistics for the completion request.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record EmbeddingList<T>(
            // @formatter:off
            @JsonProperty("object") String object,
            @JsonProperty("data") List<T> data,
            @JsonProperty("model") String model,
            @JsonProperty("usage") Usage usage) {
        // @formatter:on
    }


    /**
     * The response object returned from the /embedding endpoint.
     * @param model ID of the model to use.
     * @param data 模型生成的数组结果
     * @param usage 本次模型调用的 tokens 数量统计
     * @param object 结果类型，目前为 "list"
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record EmbeddingResponse(
        @JsonProperty("model") String model,
        @JsonProperty("data") List<EmbeddingResponseData> data,
        @JsonProperty("usage") EmbeddingResponseUsage usage,
        @JsonProperty("object") String object) {
    }

    /**
     * The response object returned from the /embeddings endpoint.
     * @param index 结果下标
     * @param object 结果类型，目前为"embedding"
     * @param embedding embedding 的处理结果，返回向量化表征的数组，数组长度为1024
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record EmbeddingResponseData(
        @JsonProperty("index") Integer index,
        @JsonProperty("object") String object,
        @JsonProperty("embedding") List<Double> embedding) {
    }

    /**
     * 本次模型调用的 tokens 数量统计
     * @param promptTokens 用户输入的 tokens 数量
     * @param completionTokens 模型输出的 tokens 数量
     * @param totalTokens 总 tokens 数量
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record EmbeddingResponseUsage(
        @JsonProperty("prompt_tokens") Integer promptTokens,
        @JsonProperty("completion_tokens") Integer completionTokens,
        @JsonProperty("total_tokens") Integer totalTokens) {
    }

    // --------------------------------------------------------------------------
    // Files
    // Files are used to upload documents that can be used with features like Assistants and Fine-tuning.
    // --------------------------------------------------------------------------

    /**
     * Upload a file that can be used across various endpoints.
     * https://platform.openai.com/docs/api-reference/files
     * @param file The File object (not file name) to be uploaded.
     * @param purpose The intended purpose of the uploaded file. Use "fine-tune" for Fine-tuning and "assistants" for Assistants and Messages. This allows us to validate the format of the uploaded file is correct for fine-tuning.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record FileUploadRequest(
            @JsonProperty("file") MultipartFile file,
            @JsonProperty("purpose") String purpose) {

    }

    /**
     * Returns a list of files that belong to the user's organization.
     * @param purpose Only return files with the given purpose.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record FileListRequest(
            @JsonProperty("purpose") String purpose) {

    }
    /**
     * Returns a list of files that belong to the user's organization.
     * @param data The file identifier, which can be referenced in the API endpoints.
     * @param object The object type, which is always file.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record FileListResponse(
        @JsonProperty("data") List<FileResponseData> data,
        @JsonProperty("object") String object) {

    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record FileResponseData(
            @JsonProperty("id") String id,
            @JsonProperty("bytes") Integer bytes,
            @JsonProperty("created_at") Integer createdAt,
            @JsonProperty("filename") String filename,
            @JsonProperty("object") String object,
            @JsonProperty("purpose") String purpose) {

    }

    // --------------------------------------------------------------------------
    // Images
    // Given a prompt and/or an input image, the model will generate a new image.
    // --------------------------------------------------------------------------

    /**
     * Creates an image given a prompt.
     * https://platform.openai.com/docs/api-reference/images/create
     * @param prompt A text description of the desired image(s).
     * @param model The model to use for image generation.
     * @param number The number of images to generate. Must be between 1 and 10.
     * @param quality The quality of the image that will be generated.
     * @param responseFormat The format in which the generated images are returned. Must be one of url or b64_json.
     * @param size The size of the generated images.
     *             Must be one of 256x256, 512x512, or 1024x1024 for dall-e-2.
     *             Must be one of 1024x1024, 1792x1024, or 1024x1792 for dall-e-3 models.
     * @param style The style of the generated images.
     * @param user A unique identifier representing your end-user, which can help to monitor and detect abuse.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ImageGenerationsRequest(
            @JsonProperty("prompt") String prompt,
            @JsonProperty("model") String model,
            @JsonProperty("n") Integer number,
            @JsonProperty("quality") String quality,
            @JsonProperty("response_format") String responseFormat,
            @JsonProperty("size") String size,
            @JsonProperty("style") String style,
            @JsonProperty("user") String user) {

        public ImageGenerationsRequest(String prompt, String model) {
            this(prompt, model, null, null, null, null, null, null);
        }

    }

    /**
     * Creates an edited or extended image given an original image and a prompt.
     * https://platform.openai.com/docs/api-reference/images/createEdit
     * @param image The image to edit. Must be a valid PNG file, less than 4MB, and square. If mask is not provided, image must have transparency, which will be used as the mask.
     * @param prompt A text description of the desired image(s). The maximum length is 1000 characters.
     * @param mask An additional image whose fully transparent areas (e.g. where alpha is zero) indicate where image should be edited. Must be a valid PNG file, less than 4MB, and have the same dimensions as image.
     * @param model The model to use for image generation. Only dall-e-2 is supported at this time.
     * @param number The number of images to generate. Must be between 1 and 10.
     * @param responseFormat The format in which the generated images are returned. Must be one of url or b64_json. URLs are only valid for 60 minutes after the image has been generated.
     * @param size The size of the generated images. Must be one of 256x256, 512x512, or 1024x1024.
     * @param user A unique identifier representing your end-user, which can help OpenAI to monitor and detect abuse.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ImageEditsRequest(
            @JsonProperty("image") File image,
            @JsonProperty("prompt") String prompt,
            @JsonProperty("mask") File mask,
            @JsonProperty("model") String model,
            @JsonProperty("n") Integer number,
            @JsonProperty("response_format") String responseFormat,
            @JsonProperty("size") String size,
            @JsonProperty("user") String user) {

        public ImageEditsRequest(File image, String prompt, String model) {
            this(image, prompt, null, model, 1, null, null, null);
        }

    }

    /**
     * Creates a variation of a given image.
     * https://platform.openai.com/docs/api-reference/images/createVariation
     * @param image The image to use as the basis for the variation(s). Must be a valid PNG file, less than 4MB, and square.
     * @param model The model to use for image generation. Only dall-e-2 is supported at this time.
     * @param number The number of images to generate. Must be between 1 and 10. For dall-e-3, only n=1 is supported.
     * @param responseFormat The format in which the generated images are returned. Must be one of url or b64_json. URLs are only valid for 60 minutes after the image has been generated.
     * @param size The size of the generated images. Must be one of 256x256, 512x512, or 1024x1024.
     * @param user A unique identifier representing your end-user, which can help OpenAI to monitor and detect abuse.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ImageVariationsRequest(
            @JsonProperty("image") File image,
            @JsonProperty("model") String model,
            @JsonProperty("n") Integer number,
            @JsonProperty("response_format") String responseFormat,
            @JsonProperty("size") String size,
            @JsonProperty("user") String user) {

        public ImageVariationsRequest(File image, String model) {
            this(image, model, 1, null, null, null);
        }

    }

    /**
     * Represents the url or the content of an image generated by the OpenAI API.
     * @param created
     * @param data
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ImageResponse(
            @JsonProperty("created") Long created,
            @JsonProperty("data") List<ImageResponseData> data) {
    }

    /**
     * Represents the url or the content of an image generated by the OpenAI API.
     * @param url The URL of the generated image, if response_format is url (default).
     * @param b64Json The base64-encoded JSON of the generated image, if response_format is b64_json.
     * @param revisedPrompt The prompt that was used to generate the image, if there was any revision to the prompt.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ImageResponseData(
            @JsonProperty("url") String url,
            @JsonProperty("b64_json") String b64Json,
            @JsonProperty("revised_prompt") String revisedPrompt) {
    }

}
