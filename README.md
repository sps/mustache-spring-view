[mustache.java](https://github.com/spullara/mustache.java) view for [spring3](http://static.springsource.org/spring/docs/3.0.x/spring-framework-reference/html/mvc.html)
---------------------------
What is mustache: [mustache.js](http://mustache.github.com/mustache.5.html)

This is a version of [mustache-spring-view](https://github.com/sps/mustache-spring-view) that
 works with [mustache.java](https://github.com/spullara/mustache.java).

Big thanks to sps (Sean Scanlon) for mustache-spring-view, which supports
jmustache.  This code base is derived from mustache-spring-view.

Getting Started
-----------------
See: http://blog.springsource.com/2011/01/04/green-beans-getting-started-with-spring-mvc/

And the following sections

Maven dependency
-----------------


    <dependencies>
        ...
        <dependency>
            <groupId>com.github.ericdwhite</groupId>
	        <artifactId>mustache.java-spring-webmvc</artifactId>
            <version>0.9.3-SNAPSHOT</version>
        </dependency>
        ...
    </dependencies>
    
    <repositories>
    	...
  		<repository>
    		<id>Sonatype Snapshots</id>
    		<url>https://oss.sonatype.org/content/repositories/snapshots/</url>
  		</repository>
  		...
	</repositories>

Spring configuration
-------------

    <bean id="viewResolver" class="org.springframework.web.servlet.view.mustache.MustacheViewResolver">
        <property name="cache" value="${TEMPLATE_CACHE_ENABLED}" />
        <property name="prefix" value="/WEB-INF/views/" />
        <property name="suffix" value=".html" />
        <property name="templateLoader">
            <bean class="org.springframework.web.servlet.view.mustache.MustacheTemplateLoader"" />
        </property>
    </bean>
    
Example
-------------
WEB-INF/views/parent.html

    <html>
        <head>
            <title>Home</title>
        </head>
        <body>
            <h1>Hello world!</h1>
            <h2>{{ token }}</h2>
            
            <!-- Partial Support -->
            {{> footer }}
        </body>
    </html>

WEB-INF/views/footer.html
    
    <div id="#footer">
        <p>Copyright (C) 2012, Example Inc.</p>
    </div>

A Controller
-------------

    @Controller
    public class HelloWorldController {
    
    	@RequestMapping(value="/hello")
    	public String hello(Model m) {
    		m.addAttribute("token", new java.util.Date());
    		return "parent";
    	}
    }


Mustache and Localization
-------------
If you want to use mustache in a localized application, you can use the MustacheMessageInterceptor.
Configure your application for localization.
See also for more background: http://viralpatel.net/blogs/2010/07/spring-3-mvc-internationalization-i18n-localization-tutorial-example.html

Create resource bundle e.g.
* messages_en.properties
    > labels.global.mustache=moustache
* messages_nl.properties
    > labels.global.mustache=snor
* ...

Spring configuration for localization:

    <bean id="messageSource" class="org.springframework.context.support.ReloadableResourceBundleMessageSource">
        <property name="basename" value="classpath:messages"/>
    </bean>

    <bean id="localeResolver" class="org.springframework.web.servlet.i18n.SessionLocaleResolver">
        <property name="defaultLocale" value="en"/>
    </bean>

    <bean id="localeChangeInterceptor" class="org.springframework.web.servlet.i18n.LocaleChangeInterceptor">
        <property name="paramName" value="lang"/>
    </bean>

    <bean class="org.springframework.web.servlet.mvc.annotation.DefaultAnnotationHandlerMapping">
        <property name="interceptors">
          <list>
            <ref bean="localeChangeInterceptor"/>
            <ref bean="messageInterceptor"/>
          </list>
        </property>
    </bean>

    <bean id="messageInterceptor" class="org.springframework.web.servlet.i18n.MustacheMessageInterceptor">
        <constructor-arg ref="messageSource" />
        <constructor-arg ref="localeResolver" />
        <!--<property name="messageKey" value="i18n"/> default is 'i18n'-->
    </bean>

In your mustache template, do:

    {{#i18n}}labels.global.mustache{{/i18n}}

Will be replaced by (if your locale has language 'en'):

    moustache




