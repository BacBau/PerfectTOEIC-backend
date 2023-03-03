package org.example.http;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class HttpSender {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(300, TimeUnit.SECONDS)
            .build();
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    JsonObject postForm(String uri, Map<String, String> header, Map<String, String> params, int timeout) {
        JsonObject result = null;
        try {
            logger.info("POST FORM uri={}, params={}", uri, new Gson().toJson(params));
            FormBody.Builder formBodyBuilder = new FormBody.Builder();
            if (params != null && !params.isEmpty()) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    formBodyBuilder.addEncoded(entry.getKey(), entry.getValue());
                }
            }
            Request.Builder requestBuilder = new Request.Builder().url(uri).post(formBodyBuilder.build())
                    .addHeader("Content-Type", "application/x-www-form-urlencoded");
            if (header != null && !header.isEmpty()) {
                for (Map.Entry<String, String> entry : header.entrySet()) {
                    requestBuilder.addHeader(entry.getKey(), entry.getValue());
                }
            }
            Request request = requestBuilder.build();
            String rs = decodeResponse(client, request);
            logger.info("POST FORM result: {}", rs);
            if (!Strings.isNullOrEmpty(rs)) {
                JsonParser parser = new JsonParser();
                result = parser.parse(rs).getAsJsonObject();
            }
            return result;
        } catch (Exception ex) {
            logger.error("", ex);
            return null;
        }
    }

    JsonObject postJson(String uri, Map<String, String> header, String params, int timeout) {
        JsonObject result = null;
        try {
            logger.info("POST JSON uri={}, params={}", uri, params);
            RequestBody body = RequestBody
                    .create(JSON, params);
            Request.Builder requestBuilder = new Request.Builder().url(uri).post(body)
                    .addHeader("Content-Type", "application/json; charset=utf-8");
            if (header != null && !header.isEmpty()) {
                for (Map.Entry<String, String> entry : header.entrySet()) {
                    requestBuilder.addHeader(entry.getKey(), entry.getValue());
                }
            }
            Request req = requestBuilder.build();
            String rs = decodeResponse(client, req);
            logger.info("POST JSON result: {}", rs);
            if (!Strings.isNullOrEmpty(rs)) {
                JsonParser parser = new JsonParser();
                result = parser.parse(rs).getAsJsonObject();
            }
            return result;
        } catch (Exception ex) {
            logger.error("POST Exception: ", ex);
            return null;
        }
    }

    String postJson2(String uri, Map<String, String> header, String params, int timeout) {
        String result = null;
        try {
            logger.info("POST JSON2 uri={}, params={}", uri, params);
            RequestBody body = RequestBody.create(JSON, params);
            Request.Builder requestBuilder = new Request.Builder().url(uri).post(body)
                    .addHeader("Content-Type", "application/json; charset=utf-8");
            if (header != null && !header.isEmpty()) {
                for (Map.Entry<String, String> entry : header.entrySet()) {
                    requestBuilder.addHeader(entry.getKey(), entry.getValue());
                }
            }
            Request req = requestBuilder.build();
            String rs = decodeResponse(client, req);
            logger.info("POST JSON result: {}", rs);
            result = rs;
            return result;
        } catch (Exception ex) {
            logger.error("POST Exception: ", ex);
            return null;
        }
    }

    public JsonArray postJson3(String uri, Map<String, String> header, String params) {
        JsonArray result = null;
        try {
            logger.info("POST JSON uri={}, params={}", uri, params);
            RequestBody body = RequestBody
                    .create(JSON, params);
            Request.Builder requestBuilder = new Request.Builder().url(uri).post(body)
                    .addHeader("Content-Type", "application/json; charset=utf-8");
            if (header != null && !header.isEmpty()) {
                for (Map.Entry<String, String> entry : header.entrySet()) {
                    requestBuilder.addHeader(entry.getKey(), entry.getValue());
                }
            }
            Request req = requestBuilder.build();
            String rs = decodeResponse(client, req);
            logger.info("POST JSON result: {}", rs);
            if (!Strings.isNullOrEmpty(rs)) {
                JsonParser parser = new JsonParser();
                result = parser.parse(rs).getAsJsonArray();
            }
            return result;
        } catch (Exception ex) {
            logger.error("POST Exception: ", ex);
            return null;
        }
    }


    protected JsonObject getJson(String uri, Map<String, String> header, Map<String, String> params, int timeout) {
        JsonObject result = null;
        try {
            String resp = get(uri, header, params, timeout);
            if (!Strings.isNullOrEmpty(resp)) {
                JsonParser parser = new JsonParser();
                result = parser.parse(resp).getAsJsonObject();
            }
            return result;
        } catch (Exception ex) {
            logger.error("", ex);
            ex.printStackTrace();
            return null;
        }
    }

    protected JsonArray getJson2(String uri, Map<String, String> header, Map<String, String> params, int timeout) {
        JsonArray result = null;
        try {
            String resp = get(uri, header, params, timeout);
            if (!Strings.isNullOrEmpty(resp)) {
                JsonParser parser = new JsonParser();
                result = parser.parse(resp).getAsJsonArray();
            }
            return result;
        } catch (Exception ex) {
            logger.error("", ex);
            ex.printStackTrace();
            return null;
        }
    }

    protected String get(String uri, Map<String, String> header, Map<String, String> params, int timeout) {
        try {
            HttpUrl.Builder urlBuilder = HttpUrl.parse(uri).newBuilder();
            if (params != null && !params.isEmpty()) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
                }
            }
            String url = urlBuilder.build().toString();
            Request.Builder requestBuilder = new Request.Builder().url(url).get();
            if (header != null && !header.isEmpty()) {
                for (Map.Entry<String, String> entry : header.entrySet()) {
                    requestBuilder.addHeader(entry.getKey(), entry.getValue());
                }
            }
            Request request = requestBuilder.build();
            return decodeResponse(client, request);
        } catch (Exception ex) {
            logger.error("", ex);
            return "";
        }
    }

    private String decodeResponse(OkHttpClient client, Request request) throws IOException {
        ResponseBody body = null;
        try {
            Response response = client.newCall(request).execute();
            if (response.code() != 200 && response.code() != 201) {
                return "";
            }
            body = response.body();
            if (body == null) {
                return "";
            }
            return body.string();
        } catch (final Throwable th) {
            logger.error("", th);
            return "";
        } finally {
            if (body != null) {
                try {
                    body.close();
                } catch (final Throwable th) {
                    logger.error("", th);
                }
            }
        }
    }

    public File getDownloadFile(String uri, Map<String, String> header, Map<String, String> params, String folderTemp) {
        ResponseBody body = null;
        try {
            HttpUrl.Builder urlBuilder = HttpUrl.parse(uri).newBuilder();
            if (params != null && !params.isEmpty()) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
                }
            }
            String url = urlBuilder.build().toString();
            Request.Builder requestBuilder = new Request.Builder().url(url).get();
            if (header != null && !header.isEmpty()) {
                for (Map.Entry<String, String> entry : header.entrySet()) {
                    requestBuilder.addHeader(entry.getKey(), entry.getValue());
                }
            }
            Request request = requestBuilder.build();
            Response response = client.newCall(request).execute();
            if (response.code() != 200) {
                return null;
            }

            Headers j = response.headers();
            logger.info("headers: {}", j);

            body = response.body();
            if (body == null) {
                return null;
            }

            if (body.contentLength() > 0) {

                String fileName = getFileName(response);
                String _filePathTemp = folderTemp + fileName;
                logger.info("start save file to: {}", _filePathTemp);

                File destFile = new File(_filePathTemp);
                long contentLength = body.contentLength();
                BufferedSource source = body.source();

                logger.info("contentLength: {}", contentLength);
                logger.info("contentType: {}", Objects.requireNonNull(body.contentType()));

                BufferedSink sink = Okio.buffer(Okio.sink(destFile));
                Buffer sinkBuffer = sink.buffer();

                long totalBytesRead = 0;
                int bufferSize = 8 * 1024;
                logger.info("checkPoint 1");
                for (long bytesRead; (bytesRead = source.read(sinkBuffer, bufferSize)) != -1; ) {
                    logger.info("checkPoint 2");
                    sink.emit();
                    totalBytesRead += bytesRead;
                    int progress = (int) ((totalBytesRead * 100) / contentLength);
                    logger.info("[{}/{}] <-> {}% ", totalBytesRead, contentLength, progress);
                }

                sink.flush();
                sink.close();
                source.close();

//                File downloadedFile = new File(_filePathTemp);
//                BufferedSink sink = Okio.buffer(Okio.sink(downloadedFile));
//                long cp = sink.writeAll(body.source());
//                sink.close();

//                long cp = FileUtils.copyFileUsingStream(body.byteStream(), new File(_filePathTemp));
//                long cp = Files.copy(body.byteStream(), Paths.get(_filePathTemp), StandardCopyOption.REPLACE_EXISTING);
                if (totalBytesRead > 0) {
                    return destFile;
                }

                // response.networkResponse().request().url(); -> get file name

            }

            return null;

        } catch (Exception ex) {
            logger.error("", ex);
            return null;
        } finally {
            if (body != null) {
                try {
                    body.close();
                } catch (final Throwable th) {
                    logger.error("", th);
                }
            }
        }
    }

    private String getFileName(Response response) {
        Response r = response.networkResponse();
        if (r != null) {
            Request req = r.request();
            HttpUrl url = req.url();
            List<String> segments = url.encodedPathSegments();
            if (!segments.isEmpty()) {
                return segments.get(segments.size() - 1);
            }
        }
        return "unknown";
    }

}