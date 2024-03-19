package com.sismics.books.core.model.builder;


import org.codehaus.jackson.node.ObjectNode;

import java.util.Date;

public interface MediaBuilder {

    public void createMedia();

    public void setCoreContent(ObjectNode data);

    public void setMediaProfile(ObjectNode data);

    public void setMetaData(ObjectNode data);

}
