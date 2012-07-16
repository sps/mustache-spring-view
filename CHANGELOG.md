= 0.9.3-SNAPSHOT
 * Changed the default encoding to UTF-8 instead of the JVM default
   this may break some clients.  If you need another encoding then
   set the JVM property.  This impacts both the template rendering
   and HTTP output stream.
   
   To force a encoding set the System property 'mustache.template.encoding'
     -Dmustache.template.encoding=ISO-8859-1

= 0.9.2-SNAPSHOT

 * Support for internationalized messages (see: MustacheMessageInterceptor)
 * Switch to jMock for testing
 * Switch to mustache.java 0.7.2 RELEASE

= 0.9.1-SNAPSHOT

 * Removed the RuntimeException in favour of MustacheException

= 0.9.0-SNAPSHOT

 * First working release
