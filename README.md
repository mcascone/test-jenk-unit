Based off this tutorial: https://medium.com/disney-streaming/testing-jenkins-shared-libraries-4d4939406fa2

I have the simplest of hello-world repos to get off the ground, and I can’t get past the first step.

https://github.consilio.com/mcascone/test-jenk-unit

`./gradlew test` should compile and run the test, but I get an ssl error:

```java
> ./gradlew test
Downloading https://services.gradle.org/distributions/gradle-6.8.3-bin.zip

Exception in thread "main" javax.net.ssl.SSLHandshakeException: sun.security.validator.ValidatorException: PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to fi
nd valid certification path to requested target
        at sun.security.ssl.Alerts.getSSLException(Unknown Source)
        at sun.security.ssl.SSLSocketImpl.fatal(Unknown Source)

// (most of the error messages clipped; this is more than you need)
```
I get this even when off the VPN. So I figure my java cert chain is out of date. What’s the best way to fix it?
