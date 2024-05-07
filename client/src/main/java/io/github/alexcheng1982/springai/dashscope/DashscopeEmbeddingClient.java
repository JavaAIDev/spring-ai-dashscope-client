package io.github.alexcheng1982.springai.dashscope;

import com.alibaba.dashscope.embeddings.TextEmbedding;
import com.alibaba.dashscope.embeddings.TextEmbeddingParam;
import com.alibaba.dashscope.embeddings.TextEmbeddingParam.TextType;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import io.github.alexcheng1982.springai.dashscope.api.DashscopeApiException;
import java.util.List;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;

/**
 * Spring AI {@linkplain EmbeddingClient} for Aliyun Dashscope
 */
public class DashscopeEmbeddingClient implements EmbeddingClient {

  private DashscopeEmbeddingOptions defaultOptions;

  public DashscopeEmbeddingClient() {
  }

  public DashscopeEmbeddingClient(DashscopeEmbeddingOptions defaultOptions) {
    this.defaultOptions = defaultOptions;
  }

  @Override
  public EmbeddingResponse call(EmbeddingRequest request) {
    var builder = TextEmbeddingParam.builder()
        .texts(request.getInstructions());
    var options = request.getOptions() instanceof DashscopeEmbeddingOptions
        ? (DashscopeEmbeddingOptions) request.getOptions() : defaultOptions;
    if (options != null) {
      if (options.getModel() != null) {
        builder.model(options.getModel());
      }
      if (options.getTextType() != null) {
        builder.textType(options.getTextType());
      }
    } else {
      builder.model(DashscopeEmbeddingOptions.DEFAULT_MODEL)
          .textType(TextType.DOCUMENT);
    }
    TextEmbedding embedding = new TextEmbedding();
    try {
      var result = embedding.call(builder.build());
      return new EmbeddingResponse(
          result.getOutput().getEmbeddings().stream().map(item ->
              new Embedding(item.getEmbedding(), item.getTextIndex())
          ).toList());
    } catch (ApiException | NoApiKeyException e) {
      throw new DashscopeApiException(e);
    }
  }

  @Override
  public List<Double> embed(Document document) {
    return embed(document.getContent());
  }
}
