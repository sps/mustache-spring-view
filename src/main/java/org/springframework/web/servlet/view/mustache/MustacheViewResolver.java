/*
 * Copyright 2011 the original author or authors.
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
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.AbstractTemplateViewResolver;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

import com.sampullara.mustache.Mustache;
import com.sampullara.mustache.MustacheCompiler;

/**
 * @author Sean Scanlon <sean.scanlon@gmail.com>
 * 
 */
public class MustacheViewResolver extends AbstractTemplateViewResolver implements ViewResolver,
        ResourceLoaderAware {

    /*
     * TODO: this will work for now, but will probably want to make some options externally
     * configurable.
     */
    private static final MustacheCompiler MUSTACHE_COMPILER = new MustacheCompiler();

    private ResourceLoader resourceLoader;

    public MustacheViewResolver() {
        setViewClass(MustacheView.class);
    }

    @Override
    protected Class<?> requiredViewClass() {
        return MustacheView.class;
    }

    @Override
    protected AbstractUrlBasedView buildView(String viewName) throws Exception {

        final MustacheView view = (MustacheView) super.buildView(viewName);

        Resource resource = resourceLoader.getResource(view.getUrl());
        if (resource.exists()) {
            /*
             * TODO: might be better to supply the path instead of the entire template.
             */
            StringWriter writer = new StringWriter();
            IOUtils.copy(resource.getInputStream(), writer);
            Mustache template = MUSTACHE_COMPILER.parse(writer.toString());
            template.setRoot(resource.getFile().getParentFile());
            view.setTemplate(template);
        } else {
            throw new FileNotFoundException(viewName);
        }

        return view;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

}
