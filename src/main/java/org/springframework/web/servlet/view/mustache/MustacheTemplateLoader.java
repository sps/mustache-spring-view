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
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;

import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.samskivert.mustache.Mustache.TemplateLoader;

/**
 * @author Sean Scanlon <sean.scanlon@gmail.com>
 * @author Adam Gent <adam.gent@evocatus.com>
 */
public class MustacheTemplateLoader implements TemplateLoader, ResourceLoaderAware {

    private ResourceLoader resourceLoader;
    private Map<String, String> partialAliases;
    
    private String suffix = "";
    private String prefix = "";

    public MustacheTemplateLoader withPartialAliases(Map<String, String> aliases) {
    	MustacheTemplateLoader loader = cloneTemplateLoader();
    	return loader;
    	
    }
    
    protected MustacheTemplateLoader cloneTemplateLoader() {
    	MustacheTemplateLoader loader = new MustacheTemplateLoader();
    	loader.setPrefix(prefix);
    	loader.setSuffix(suffix);
    	loader.setResourceLoader(resourceLoader);
    	loader.setPartialAliases(partialAliases);
    	return loader;
    }

    public Reader getTemplate(String filename) throws Exception {
    	String fn = partialAliases != null && partialAliases.containsKey(filename) ? 
    			partialAliases.get(filename) : filename;
        Resource resource = resourceLoader.getResource(getPrefix() + fn + getSuffix());
        if (resource.exists()) {
            return new InputStreamReader(resource.getInputStream());
        }
        throw new FileNotFoundException(filename);
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

	protected String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
	
	protected String getPrefix() {
		return prefix;
	}
	
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public Map<String, String> getPartialAliases() {
		return partialAliases;
	}	
	public void setPartialAliases(Map<String, String> aliases) {
		this.partialAliases = aliases;
	}
}
