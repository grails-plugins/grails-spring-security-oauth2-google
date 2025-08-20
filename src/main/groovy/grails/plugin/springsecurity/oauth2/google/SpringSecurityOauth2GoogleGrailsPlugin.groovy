package grails.plugin.springsecurity.oauth2.google

import grails.plugin.springsecurity.ReflectionUtils
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.plugin.springsecurity.oauth2.SpringSecurityOauth2BaseService
import grails.plugin.springsecurity.oauth2.exception.OAuth2Exception
import grails.plugins.Plugin
import groovy.util.logging.Slf4j

@Slf4j
class SpringSecurityOauth2GoogleGrailsPlugin extends Plugin {

    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "7.0.0-SNAPSHOT > *"
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/views/error.gsp"
    ]
    List loadAfter = ['spring-security-oauth2']

    def title = "Spring Security Oauth2 Google Provider" // Headline display name of the plugin
    def author = "Johannes Brunswicker"
    def authorEmail = "johannes.brunswicker@gmail.com"
    def description = 'This plugin provides the capability to authenticate via google-oauth provider. Depends on grails-spring-security-oauth2.'
    def documentation = 'https://grails.org/plugin/grails-spring-security-oauth2-google'
    def license = "APACHE"
    def developers = [
            [name: 'Johannes Brunswicker', github: 'MatrixCrawler'],
            [name: 'Ryan Vanderwerf', github: 'rvanderwerf'],
            [name: 'Søren Berg Glasius', github: 'sbglasius']
    ]
    def issueManagement = [system: "GitHub", url: 'https://github.com/grails-plugins/grails-spring-security-oauth2-google/issues/browse/GPMYPLUGIN']
    def scm = [url: 'https://github.com/grails-plugins/grails-spring-security-oauth2-google']

    Closure doWithSpring() {
        { ->
            ReflectionUtils.application = grailsApplication
            if (grailsApplication.warDeployed) {
                SpringSecurityUtils.resetSecurityConfig()
            }
            SpringSecurityUtils.application = grailsApplication

            // Check if there is an SpringSecurity configuration
            def coreConf = SpringSecurityUtils.securityConfig
            boolean printStatusMessages = (coreConf.printStatusMessages instanceof Boolean) ? coreConf.printStatusMessages : true
            if (!coreConf || !coreConf.active) {
                if (printStatusMessages) {
                    println('ERROR: There is no SpringSecurity configuration or SpringSecurity is disabled')
                    println('       Stopping configuration of SpringSecurity Oauth2')
                }
                return
            }

            if (printStatusMessages) {
                println('Configuring Spring Security OAuth2 Google plugin...')
            }
            SpringSecurityUtils.loadSecondaryConfig('DefaultOAuth2GoogleConfig')
            if (printStatusMessages) {
                println('... finished configuring Spring Security OAuth2 Google\n')
            }
        }
    }

    @Override
    void doWithApplicationContext() {
        log.trace("doWithApplicationContext")
        SpringSecurityOauth2BaseService oAuth2BaseService = grailsApplication.mainContext.getBean(SpringSecurityOauth2BaseService)
        GoogleOAuth2Service googleOAuth2Service = grailsApplication.mainContext.getBean(GoogleOAuth2Service)
        try {
            oAuth2BaseService.registerProvider(googleOAuth2Service)
        } catch (OAuth2Exception exception) {
            log.error('OAuth2 Google not loaded', exception)
        }
    }
}
