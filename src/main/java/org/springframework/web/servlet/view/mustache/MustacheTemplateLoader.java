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

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.IOException;

import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.github.mustachejava.DefaultMustacheFactory;

/**
 * Uses the spring resource loader to find template files.
 * 
 * The prefix is set from the view resolver to handle partials
 * as the path to the parent will be fully qualified, but partials
 * with the parent will not be.
 * 
 * @author Sean Scanlon <sean.scanlon@gmail.com>
 * @author Eric D. White <eric@ericwhite.ca>
 */
public class MustacheTemplateLoader extends DefaultMustacheFactory implements ResourceLoaderAware {

	private ResourceLoader resourceLoader;
	private String prefix = "";

    public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	@Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
	public Reader getReader(String resourceName) {
    	resourceName = getFullyQualifiedResourceName(resourceName);
        Resource resource = resourceLoader.getResource(resourceName);
        if (resource.exists()) {
        	try {
        		return new InputStreamReader(resource.getInputStream());	
        	}
        	catch(IOException e) {
        		throw new RuntimeException("Failed to load template"+resourceName, e);        		
        	}
        }
        throw new RuntimeException(resourceName);
	}

    /**
     * This is to handle partials within templates
     * that have been prefixed in the View Resolver.
     * 
     * As the parent will have a prefix, we want partials
     * declared to also use the same prefix without explicitly
     * specifying that prefix in the parent template.
     * 
     * e.g. WEB-INF/views/parent.html
     * Want this 
     *   <h1> Parent </h1>
     *   {{> aPartial }}
     *   
     * Instead of:
     *   <h1> Parent </h1>
     *   {{> WEB-INF/views/aPartial.html }}
     *   
     * @param resourceName
     * @return the resource prefixed if applicable
     */
	private String getFullyQualifiedResourceName(String resourceName) {
		if( !resourceName.startsWith(this.prefix) ) {
    		resourceName = this.prefix + resourceName;
    	}
		return resourceName;
	}
}
