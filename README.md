# jenkins pipeline unit test learning repo

This is the simplest of hello-world repos to get off the ground, and I can’t get past the first step.

## References

- Based off this tutorial: https://medium.com/disney-streaming/testing-jenkins-shared-libraries-4d4939406fa2
- JPU repo: https://github.com/jenkinsci/JenkinsPipelineUnit
- Shot in the dark question to them: https://github.com/jenkinsci/JenkinsPipelineUnit/issues/51

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

# The Answer

I get the error when off VPN because the Zscaler grabs _all_ traffic from our laptops, regardless of VPN status.

These steps are required to add the new Zscaler root CA cert to the Java keystore:

1. Grab the Root ZScaler cert from a browser:


2. Execute these commands:

   ```powershell
   $caargs = @('-importcert',
     '-trustcacerts',
     '-alias', 'zscaler-rootca-may2021',
     '-file', 'C:\Users\mcascone1\Desktop\zscaler-rootca-may2021.cer',
     '-storepass', 'changeit', 
     '-keystore', '.\cacerts', 
     '-noprompt')

   Push-Location 'C:\Program Files (x86)\Java\jre1.8.0_241\lib\security'   
   
   ..\..\bin\keytool.exe @caargs
   ```
  
   > Note the `@` in the last line is not a typo, it is [the `splat` operator](https://docs.microsoft.com/en-us/powershell/module/microsoft.powershell.core/about/about_splatting?view=powershell-7.1) to write the contents of the array as a single line.
   