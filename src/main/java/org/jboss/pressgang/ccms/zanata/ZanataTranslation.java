/*
  Copyright 2011-2014 Red Hat, Inc

  This file is part of PressGang CCMS.

  PressGang CCMS is free software: you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  PressGang CCMS is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with PressGang CCMS.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.jboss.pressgang.ccms.zanata;

import org.zanata.common.ContentState;
import org.zanata.rest.dto.resource.TextFlowTarget;

public class ZanataTranslation {
    private String translation = null;
    private ContentState contentState = null;

    public ZanataTranslation(final String translation) {
        this.translation = translation;
    }

    public ZanataTranslation(final String translation, final ContentState contentState) {
        this.translation = translation;
        this.contentState = contentState;
    }

    @SuppressWarnings("deprecation")
    public ZanataTranslation(final TextFlowTarget textFlowTarget) {
        this.translation = textFlowTarget.getContent();
        this.contentState = textFlowTarget.getState();
    }

    public boolean isFuzzy() {
        return contentState == null ? false : contentState == ContentState.NeedReview;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public ContentState getContentState() {
        return contentState;
    }

    public void setContentState(ContentState contentState) {
        this.contentState = contentState;
    }
}
