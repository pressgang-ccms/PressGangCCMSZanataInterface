package org.jboss.pressgang.ccms.zanata;

import org.zanata.common.ContentState;
import org.zanata.rest.dto.resource.TextFlowTarget;

public class ZanataTranslation
{
    private String translation = null;
    private ContentState contentState = null;
    
    public ZanataTranslation(final String translation)
    {
        this.translation = translation;
    }
    
    public ZanataTranslation(final String translation, final ContentState contentState)
    {
        this.translation = translation;
        this.contentState = contentState;
    }
    
    public ZanataTranslation(final TextFlowTarget textFlowTarget)
    {
        this.translation = textFlowTarget.getContent();
        this.contentState = textFlowTarget.getState();
    }

    public boolean isFuzzy()
    {
        return contentState == null ? false : contentState == ContentState.NeedReview;
    }

    public String getTranslation()
    {
        return translation;
    }

    public void setTranslation(String translation)
    {
        this.translation = translation;
    }

    public ContentState getContentState() {
        return contentState;
    }

    public void setContentState(ContentState contentState) {
        this.contentState = contentState;
    }
}
