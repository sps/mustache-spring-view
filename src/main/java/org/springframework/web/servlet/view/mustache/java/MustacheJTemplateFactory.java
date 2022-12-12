/*
 * Copyright 2011-2014 the original author or authors.
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
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.springframework.web.servlet.view.mustache.java;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.MustacheException;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.servlet.view.mustache.MustacheTemplate;
import org.springframework.web.servlet.view.mustache.MustacheTemplateException;
import org.springframework.web.servlet.view.mustache.MustacheTemplateFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Objects;

import static java.lang.System.getProperty;

/**
 * Uses the spring resource loader to find template files.
 * <p/>
 * The prefix is set from the view resolver to handle partials as the path to
 * the parent will be fully qualified, but partials within the parent will not
 * be.
 *
 * @author Sean Scanlon <sean.scanlon@gmail.com>
 * @author Eric D. White <eric@ericwhite.ca>
 */
public class MustacheJTemplateFactory extends DefaultMustacheFactory implements
        ResourceLoaderAware, MustacheTemplateFactory {

    private ResourceLoader resourceLoader;
    private String prefix = "";
    private String encoding = Objects.requireNonNullElse(getProperty("mustache.template.encoding"), "UTF-8");


    public void setSuffix(String suffix) {
        //
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public MustacheTemplate getTemplate(String templateURL) throws MustacheTemplateException {
        return new MustacheJTemplate(this.compile(templateURL));
    }

    @Override
    public Reader getReader(String resourceName) {
        resourceName = getFullyQualifiedResourceName(resourceName);
        Resource resource = resourceLoader.getResource(resourceName);
        if (resource.exists()) {
            try {
                return new InputStreamReader(resource.getInputStream(), encoding);
            } catch (IOException e) {
                throw new MustacheException("Failed to load template: " + resourceName, e);
            }
        }
        throw new MustacheException("No template exists named: " + resourceName);
    }

    /**
     * This is to handle partials within templates that have been prefixed in
     * the View Resolver.
     * <p/>
     * As the parent may have a prefix, we want partials declared to also use
     * the same prefix without explicitly specifying that prefix in the parent
     * template.
     * <p/>
     * <pre>
     * e.g. WEB-INF/views/parent.html
     * Want this
     *   <h1> Parent </h1>
     *   {{> aPartial }}
     *
     * Instead of:
     *   <h1> Parent </h1>
     *   {{> WEB-INF/views/aPartial.html }}
     * </pre>
     *
     * @param resourceName
     * @return the resource prefixed if applicable
     */
    private String getFullyQualifiedResourceName(String resourceName) {
        if (resourceName.startsWith(this.prefix)) {
            return resourceName;
        }
        return this.prefix + resourceName;
    }


}