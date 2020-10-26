package mas.services.api.utils;

import mas.utils.generators.CommonDataGenerator;
import mas.utils.runTime.Event;
import mas.utils.runTime.EventAppender;
import org.glassfish.jersey.client.ClientResponse;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Priority;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Priority(Integer.MIN_VALUE)
public class RESTLogger implements ClientRequestFilter, ClientResponseFilter, WriterInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(RESTLogger.class);
    private static final String ENTITY_STREAM_PROPERTY = "RESTLogger.entityStream";
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final int maxEntitySize = 1024 * 8;

    @Override
    public void filter(ClientRequestContext requestContext) {
        //Add request ID to every request
        addRequestUID();

        // LOG REQUEST URL
        logger.info("Request - method: {} to: {}", requestContext.getMethod(), requestContext.getUri());
        Event event = new Event(Event.Type.WS_REQUEST).setValue(requestContext.getMethod())
                .setLocator(requestContext.getUri().toString());

        if (requestContext.hasEntity()) {
            final OutputStream stream = new LoggingStream(requestContext.getEntityStream());
            requestContext.setEntityStream(stream);
            requestContext.setProperty(ENTITY_STREAM_PROPERTY, stream);
        }
        EventAppender.logEvent(event);
    }

    @Override
    public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
        // LOG RESPONSE
        logger.info("Response - status: {} from: {}",
                responseContext.getStatus(),
                ((ClientResponse) responseContext).getResolvedRequestUri().toString());
        Event event = new Event(Event.Type.WS_RESPONSE).setLocator(((ClientResponse) responseContext).getResolvedRequestUri().toString());

        final StringBuilder sb = new StringBuilder();
        if (responseContext.hasEntity()) {
            if (MediaType.APPLICATION_JSON_TYPE.isCompatible(responseContext.getMediaType())) {
                responseContext.setEntityStream(logInboundEntity(sb, responseContext.getEntityStream(), DEFAULT_CHARSET));
                logger.info("Response - body: {}", sb.toString());
                event.setValue(sb.toString());
            } else {
                logger.info("Response - type: {} size: {}, disposition: {}", responseContext.getMediaType(), responseContext.getLength(), responseContext.getHeaderString("Content-Disposition"));
                event.setValue(responseContext.getHeaders().toString());
            }
        }
        EventAppender.logEvent(event);
    }


    @Override
    public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException {
        // LOG REQUEST BODY
        final LoggingStream stream = (LoggingStream) context.getProperty(ENTITY_STREAM_PROPERTY);
        context.proceed();
        if (stream != null) {
            if (MediaType.APPLICATION_JSON_TYPE.isCompatible(context.getMediaType())) {
                String requestBody = stream.getStringBuilder(DEFAULT_CHARSET).toString();
                logger.info("Request - body: {}", requestBody);
                Event event = new Event(Event.Type.WS_REQUEST).setValue(requestBody);
                EventAppender.logEvent(event);
            } else {
                logDetails(context);
            }
        }
    }

    private void logDetails(WriterInterceptorContext context) {
        if (MediaType.MULTIPART_FORM_DATA_TYPE.isCompatible(context.getMediaType())) {
            FormDataBodyPart file = ((FormDataMultiPart) context.getEntity()).getField("file");
            if (file != null) {
                logger.info("Request - headers: {}", file.getHeaders());
            }
        } else {
            logger.info("Request - type: {} headers: {}", context.getMediaType(), context.getHeaders());
        }
    }

    private InputStream logInboundEntity(final StringBuilder b, InputStream stream, final Charset charset) throws IOException {
        if (!stream.markSupported()) {
            stream = new BufferedInputStream(stream);
        }
        stream.mark(maxEntitySize + 1);
        final byte[] entity = new byte[maxEntitySize + 1];
        final int entitySize = stream.read(entity);
        b.append(new String(entity, 0, Math.min(entitySize, maxEntitySize), charset));
        if (entitySize > maxEntitySize) {
            b.append("...more...");
        }
        stream.reset();
        return stream;
    }

    //Add UID to request in report and in log
    private void addRequestUID() {
        String r_uid = "RUID" + CommonDataGenerator.createNumericString(10).toUpperCase();
        EventAppender.logEvent(new Event(Event.Type.INFO).setValue("Request - UID: " + r_uid));
        logger.info("Request - UID: " + r_uid);
    }

    private class LoggingStream extends FilterOutputStream {
        private final StringBuilder sb = new StringBuilder();
        private final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        LoggingStream(OutputStream out) {
            super(out);
        }

        StringBuilder getStringBuilder(Charset charset) {
            // write entity to the builder
            final byte[] entity = baos.toByteArray();

            sb.append(new String(entity, 0, entity.length, charset));
            if (entity.length > maxEntitySize) {
                sb.append("...more...");
            }
            return sb;
        }

        @Override
        public void write(final int i) throws IOException {
            if (baos.size() <= maxEntitySize) {
                baos.write(i);
            }
            out.write(i);
        }
    }
}