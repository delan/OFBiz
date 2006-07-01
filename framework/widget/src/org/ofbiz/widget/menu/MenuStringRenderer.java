/*
 * $Id: MenuStringRenderer.java 7427 2006-04-27 00:02:37Z jonesde $
 *
 * Copyright 2003-2006 The Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.ofbiz.widget.menu;

import java.util.Map;


/**
 * Widget Library - Form String Renderer interface
 *
 * @author     <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 * @since      2.2
 */
public interface MenuStringRenderer {
    public void renderMenuItem(StringBuffer buffer, Map context, ModelMenuItem menuItem);
    public void renderMenuOpen(StringBuffer buffer, Map context, ModelMenu menu);
    public void renderMenuClose(StringBuffer buffer, Map context, ModelMenu menu);
    public void renderFormatSimpleWrapperOpen(StringBuffer buffer, Map context, ModelMenu menu);
    public void renderFormatSimpleWrapperClose(StringBuffer buffer, Map context, ModelMenu menu);
    public void renderFormatSimpleWrapperRows(StringBuffer buffer, Map context, Object menu);
    public void setUserLoginIdHasChanged(boolean b);
    public void renderLink(StringBuffer buffer, Map context, ModelMenuItem.Link link);
    public void renderImage(StringBuffer buffer, Map context, ModelMenuItem.Image image);
}
