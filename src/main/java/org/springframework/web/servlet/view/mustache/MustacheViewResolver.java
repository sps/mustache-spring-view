/*
 * Copyright 2011-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.web.servlet.view.mustache;

import java.io.FileNotFoundException;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.AbstractTemplateViewResolver;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

/**
 * @author Sean Scanlon <sean.scanlon@gmail.com>
 */
public class MustacheViewResolver extends AbstractTemplateViewResolver implements ViewResolver {
	private static Log LOG = LogFactory.getLog(MustacheViewResolver.class);
    private MustacheTemplateFactory templateFactory;

    public MustacheViewResolver() {
        setViewClass(MustacheView.class);
    }

    @Override
    protected Class<?> requiredViewClass() {
        return MustacheView.class;
    }

    @Override
    protected AbstractUrlBasedView buildView(String viewName) throws Exception {

        MustacheView view = (MustacheView) super.buildView(viewName);

        try {
        	final MustacheTemplate template = templateFactory.getTemplate(view.getUrl());
        	view.setTemplate(template);
        }
//        catch( FileNotFoundException ex ){
//        	LOG.debug("Template [" + viewName + "] not found.");
//        }
        catch( MustacheTemplateException ex ){
        	if( ex.getCause() != null && ex.getCause() instanceof FileNotFoundException ){
        		LOG.debug("Template [" + viewName + "] not found.");
        		// view = null;
        	}
        	else throw ex; // simply rethrow!
        }
        return view;
    }


	
    @Required
    public void setTemplateFactory(MustacheTemplateFactory templateFactory) {
        this.templateFactory = templateFactory;
    }
}
