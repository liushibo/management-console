
package org.duraspace.customerwebapp.aop;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.log4j.Logger;

import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;

public class IngestMessageConverter
        implements MessageConverter {

    protected final Logger log = Logger.getLogger(getClass());

    protected static final String CONTENT_ID = "contentId";

    protected static final String MIMETYPE = "mimetype";

    protected static final String SPACE_ID = "spaceId";

    public Object fromMessage(Message msg) throws JMSException,
            MessageConversionException {
        if (!(msg instanceof MapMessage)) {
            String err = "Arg obj is not an instance of 'MapMessage': ";
            log.error(err + msg);
            throw new MessageConversionException(err);
        }

        IngestMessage ingestMsg = new IngestMessage();
        ingestMsg.setContentId(msg.getStringProperty(CONTENT_ID));
        ingestMsg.setContentMimeType(msg.getStringProperty(MIMETYPE));
        ingestMsg.setSpaceId(msg.getStringProperty(SPACE_ID));
        return ingestMsg;
    }

    public Message toMessage(Object obj, Session session) throws JMSException,
            MessageConversionException {
        if (!(obj instanceof IngestMessage)) {
            String err = "Arg obj is not an instance of 'IngestMessage': ";
            log.error(err + obj);
            throw new MessageConversionException(err);
        }
        IngestMessage ingestMsg = (IngestMessage) obj;

        MapMessage msg = session.createMapMessage();
        msg.setStringProperty(CONTENT_ID, ingestMsg.getContentId());
        msg.setStringProperty(MIMETYPE, ingestMsg.getContentMimeType());
        msg.setStringProperty(SPACE_ID, ingestMsg.getSpaceId());
        return msg;
    }

}
