[mustache.js](http://mustache.github.com/mustache.5.html) view for [spring3](http://static.springsource.org/spring/docs/3.0.x/spring-framework-reference/html/mvc.html)
---------------------------

maven dependency
-----------------

    <dependency>
        <groupId>com.github.sps.mustache</groupId>
        <artifactId>mustache-spring-view</artifactId>
        <version>1.0</version>
    </dependency>


spring config
-------------

    <bean id="viewResolver" class="org.springframework.web.servlet.view.mustache.MustacheViewResolver">
        <property name="cache" value="${TEMPLATE_CACHE_ENABLED}" />
        <!-- The default view path is below -->
        <property name="prefix" value="/WEB-INF/views/" />
        <!-- The default suffix path is below -->
        <property name="suffix" value=".mustache" />
    </bean>


layout and partials support
---------------------------

You can now use partials in your templates. The partials are either resolved through aliases 
that you supply as URI parameters in your view name or by explicitely putting view name as a partial.
There are two reserved partial aliases called "inner" and "layout". Inner is the actual template 
view name (below its 'recruite/submit'). "layout" is the wrapping template. Partial aliases are also
available in the model as a hashmap with the key 'partialAliases'.

Example Java:

	@RequestMapping(value="/m", method = RequestMethod.POST)
	public String mobilePost(Map<String, Object> model) {
		return "recruite/submit?layout=recruite/layout&header=recruite/header";
	}

/WEB-INF/views/recruite/submit.mustache:

	Hello World!
	{{> recruite/explicit }}

/WEB-INF/views/recruite/layout.mustache:

	{{> header}}
	<body>
	{{> inner}}
	</body>



