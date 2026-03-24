package com.java.importer.interceptor;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.java.importer.util.CopyUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.postgresql.PGConnection;
import org.postgresql.copy.PGCopyOutputStream;
import org.springframework.stereotype.Component;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;

@Log4j2
@Component
@Intercepts({@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})})
public class CopyInterceptor implements Interceptor {

    private static final ObjectMapper INPUT_MAPPER = JsonMapper.builder().enable(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS).build();

    private static final ObjectMapper OUTPUT_MAPPER = new ObjectMapper();

    private static void writeCsvField(PGCopyOutputStream out, String value) throws IOException {
        String escaped = value.replace("\"", "\"\"");
        String csvField = "\"" + escaped + "\"";
        out.write(csvField.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement ms = (MappedStatement) invocation.getArgs()[0];

        if (!CopyUtil.COPY_METHOD.equals(ms.getId())) {
            return invocation.proceed();
        }

        Object param = invocation.getArgs()[1];

        if (!(param instanceof InputStream inputStream)) {
            throw new IllegalStateException("Expected InputStream parameter for COPY");
        }

        Executor executor = (Executor) invocation.getTarget();
        Connection connection = executor.getTransaction().getConnection();
        PGConnection pgConnection = connection.unwrap(PGConnection.class);

        executeCopy(pgConnection, inputStream);
        return 1;
    }

    private void executeCopy(PGConnection pgConnection, InputStream inputStream) throws Exception {
        InputStream nonClosing = new FilterInputStream(inputStream) {
            @Override
            public void close() {
                // do not close underlying ZipInputStream
            }
        };

        try (JsonParser parser = INPUT_MAPPER.getFactory().createParser(nonClosing); PGCopyOutputStream out = new PGCopyOutputStream(pgConnection, CopyUtil.COPY_JSON_ENTRY)) {
            if (parser.nextToken() != JsonToken.START_ARRAY) {
                throw new IllegalStateException("Expected JSON array at root");
            }

            writeValues(parser, out);
        }

        log.info("COPY completed");
    }

    private void writeValues(JsonParser parser, PGCopyOutputStream out) throws IOException {
        while (parser.nextToken() != JsonToken.END_ARRAY) {
            JsonNode node = INPUT_MAPPER.readTree(parser);
            if (node == null) {
                break;
            }

            String json = OUTPUT_MAPPER.writeValueAsString(node);
            writeCsvField(out, json);
            out.write('\n');
        }
    }
}