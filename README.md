# jenkins pipeline unit test learning repo

This is the simplest of hello-world repos to get off the ground, and I can’t get past the first step.

Based off this tutorial: https://medium.com/disney-streaming/testing-jenkins-shared-libraries-4d4939406fa2

`./gradlew test` should compile and run the test, but I get an ssl error:

```shell
> ./gradlew test
Downloading https://services.gradle.org/distributions/gradle-6.8.3-bin.zip

Exception in thread "main" javax.net.ssl.SSLHandshakeException: sun.security.validator.ValidatorException: PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to fi
nd valid certification path to requested target
        at sun.security.ssl.Alerts.getSSLException(Unknown Source)
        at sun.security.ssl.SSLSocketImpl.fatal(Unknown Source)
```

(most of the error messages clipped)

I get this even when off the VPN. So I figure my java cert chain is out of date. What’s the best way to fix it?
