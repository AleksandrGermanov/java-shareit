package ru.practicum.shareit.TestClient;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TestClient {
    private final static int PORT = 8080;
    @Autowired
    private final ObjectMapper objectMapper;
    private final HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofMinutes(1))
            .build();

    public HttpResponse<String> sendRequest(int port, TestHttpMethod method, Object body,
                                            Map<String, String> headers, String... endpointParts) {
        checkMethodAndBodyRelations(method, body);
        URI uri = URI.create("http://localhost:" + port + String.join("", endpointParts));

        try {
            String json = null;
            if (body != null) {
                json = objectMapper.writeValueAsString(body);
            }
            HttpRequest.Builder requestBuilder = method.getBuilder(json).uri(uri);
            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    requestBuilder.header(entry.getKey(), entry.getValue());
                }
            }
            return client.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public HttpResponse<String> sendRequest(TestHttpMethod method, Object body,
                                            Map<String, String> headers, String... endpointParts) {
        return sendRequest(PORT, method, body, headers, endpointParts);
    }

    public HttpResponse<String> sendRequest(TestHttpMethod method,
                                            Map<String, String> headers, String... endpointParts) {
        return sendRequest(method, null, headers, endpointParts);
    }

    public HttpResponse<String> sendRequest(TestHttpMethod method, Object body,
                                            String... endpointParts) {
        return sendRequest(method, body, null, endpointParts);
    }

    public HttpResponse<String> sendRequest(TestHttpMethod method,
                                            String... endpointParts) {
        return sendRequest(method, null, endpointParts);
    }

    public <T> T deserializeBody(HttpResponse<String> response, Class<T> clas) {
        try {
            return objectMapper.readValue(response.body(), clas);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T deserializeBody(HttpResponse<String> response, TypeReference<T> ref) {
        try {
            return objectMapper.readValue(response.body(), ref);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void checkMethodAndBodyRelations(TestHttpMethod method, Object body) {
        if ((method.equals(TestHttpMethod.POST) || method.equals(TestHttpMethod.PATCH)) && body == null) {
            throw new RuntimeException("У метода пост/патч должно быть тело!");
        }
        if ((method.equals(TestHttpMethod.GET) || method.equals(TestHttpMethod.DELETE)) && body != null) {
            throw new RuntimeException("У метода гет/делит не должно быть тела!");
        }
    }


    public enum TestHttpMethod {
        GET {
            @Override
            HttpRequest.Builder getBuilder(String json) {
                return HttpRequest.newBuilder().GET();
            }
        },
        POST {
            @Override
            HttpRequest.Builder getBuilder(String json) {
                return HttpRequest.newBuilder().POST(stringPublisherOf(json))
                        .header("Content-type", "application/json");
            }
        },
        PATCH {
            @Override
            HttpRequest.Builder getBuilder(String json) {
                return HttpRequest.newBuilder().method("PATCH", stringPublisherOf(json))
                        .header("Content-type", "application/json");
            }
        },
        DELETE {
            @Override
            HttpRequest.Builder getBuilder(String json) {
                return HttpRequest.newBuilder().DELETE();
            }
        };

        private static HttpRequest.BodyPublisher stringPublisherOf(String json) {
            return HttpRequest.BodyPublishers.ofString(json);
        }

        abstract HttpRequest.Builder getBuilder(String json);
    }
}
