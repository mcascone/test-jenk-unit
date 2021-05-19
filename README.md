# jenkins pipeline unit test learning repo

This is the simplest of hello-world repos to get off the ground, and I ~~can’t~~ couldn't get past the first step.

## References

- [Based off this tutorial][1]
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

# The Answer*
`*` to the SSL issue, at least

I get the error when off VPN because the Zscaler grabs _all_ traffic from our laptops, regardless of VPN status. IT pushes certs to our Windows cert store in the background, but we're on our own for non-standard runtimes.

These steps are required to add the new Zscaler root CA cert to the Java keystore:

1. Grab the Root ZScaler cert from a browser.

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
   
# Getting The Build To Work

After getting past the SSL issue, the code I'd copied wholesale from [the tutorial][1] still wouldn't compile, as it's several years old. I had some kind of mental breakthrough and remembered [this other tutorial][2] that talked about how to set up the directories correctly:

```gradle
sourceSets {
  main {
    groovy {
      // all code files will be in either of the folders
      srcDirs = ['src', 'vars'] 
    }
  }

  test {
    groovy {
      srcDir 'test'
    }
  }
}
```


I fiddled with that, made some progress, but was still getting into dependency hell with the libraries. After a few iterations I landed on this, which combined the info from both walkthroughs:

```gradle
repositories {
  jcenter()
    maven {
     url 'http://repository.apache.org/snapshots/'
     url 'https://repo.jenkins-ci.org/releases/'
  }
}
```

See [build.gradle](build.gradle) for the final file contents.

At last, a working build:

```gradle
> ./gradlew test

 Task :test
ToAlphanumericTest: testCall: SUCCESS
Tests: 1, Failures: 0, Errors: 0, Skipped: 0

BUILD SUCCESSFUL in 5s
4 actionable tasks: 2 executed, 2 up-to-date
```

**I'll be honest - I don't really understand the java specifics as deeply as I'd like to. But, it's compiling, it's testing, it's writing output - this is a huge step forward.**


# Going Deeper

Let's set up some failing tests:














[1]: https://medium.com/disney-streaming/testing-jenkins-shared-libraries-4d4939406fa2
[2]: https://dev.to/kuperadrian/how-to-setup-a-unit-testable-jenkins-shared-pipeline-library-2e62
