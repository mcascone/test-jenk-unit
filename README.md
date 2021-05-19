# jenkins pipeline unit test learning repo

This is the simplest of hello-world repos to get off the ground, and I ~~can’t~~ couldn't get past the first step.

## References

- Based off [this tutorial][1]
- [Jenkins Pipeline Unit on GitHub][4] and a [shot in the dark question to them][5]


## Starting Point

After many false starts, I finally was able to put together this starter repo based on [the tutorial above][1] and give it a try.

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

I get the error when off VPN because the corporate Zscaler grabs _all_ traffic from our laptops, regardless of VPN status. IT pushes certs to our Windows cert store in the background, but we're on our own for non-standard runtimes.

These steps are required to add the new Zscaler root CA cert to the Java keystore:

1. Grab the Root ZScaler cert from a browser.

2. Execute these commands:

   ```powershell
   $caargs = @('-importcert',
     '-trustcacerts',
     '-alias', 'pick-a-good-alias',
     '-file', 'path/to/the/exported.cer',
     '-storepass', 'changeit', 
     '-keystore', '.\cacerts', 
     '-noprompt')

   Push-Location 'C:\Program Files (x86)\Java\jre1.8.0_241\lib\security'   
   
   ..\..\bin\keytool.exe @caargs
   ```
  
   > Note the `@` in the last line is not a typo, it is [the `splat` operator][8] to write the contents of the array as a single line.
   
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


I fiddled with that, made some progress, but was still getting into [dependency hell][6] with the libraries. After a few iterations I landed on this, which combined the info from both walkthroughs:

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

## Tweaking The Test Command

- First off, I set an alias to speed up the ol' fingers:

  ```powershell
  > set-alias -name gw -value './gradlew'

  > get-alias gw

  CommandType     Name
  -----------     ----
  Alias           gw -> gradlew.bat
  ```

- `gradlew --help` shows a bunch of options.
  
  `--console verbose` provides some interesting details, including several tasks that have no code, and we might as well skip:

  ```shell
  > gw test --console verbose
  > Task :compileJava NO-SOURCE
  > Task :compileGroovy UP-TO-DATE
  > Task :processResources NO-SOURCE
  > Task :classes UP-TO-DATE
  > Task :cleanTest
  > Task :compileTestJava NO-SOURCE
  > Task :compileTestGroovy UP-TO-DATE
  > Task :processTestResources NO-SOURCE
  > Task :testClasses UP-TO-DATE

  > Task :test
  ToAlphanumericTest: testCall: SUCCESS
  Tests: 1, Failures: 0, Errors: 0, Skipped: 0

  BUILD SUCCESSFUL in 5s
  4 actionable tasks: 2 executed, 2 up-to-date
  ```

  So we can skip those tasks:

  ```powershell
  > gw test --console verbose --exclude-task compileJava -x compileTestJava -x processResources -x processTestResources
  > Task :compileGroovy UP-TO-DATE
  > Task :classes
  > Task :cleanTest
  > Task :compileTestGroovy UP-TO-DATE
  > Task :testClasses

  > Task :test
  ToAlphanumericTest: testCall: SUCCESS
  Tests: 1, Failures: 0, Errors: 0, Skipped: 0

  BUILD SUCCESSFUL in 5s
  4 actionable tasks: 2 executed, 2 up-to-date  
  ```

  It doesn't seem to make a difference when there's only one test, but perhaps with a real test suite it would. In any case, I prefer to skip tests that will likely never have any code.

- [I found][3] that you can set that in the `build.gradle`:

  ```gradle
  gradle.startParameter.excludedTaskNames += ['compileJava', 'compileTestJava', 'processResources', 'processTestResources']
  ```

  Let's prove it:

  ```powershell
  > gw test --console verbose
  > Task :compileGroovy UP-TO-DATE
  > Task :classes
  > Task :cleanTest
  > Task :compileTestGroovy UP-TO-DATE
  > Task :testClasses

  > Task :test
  ToAlphanumericTest: testCall: SUCCESS
  Tests: 1, Failures: 0, Errors: 0, Skipped: 0

  BUILD SUCCESSFUL in 4s
  4 actionable tasks: 2 executed, 2 up-to-date
  ```

- We can also set the default task in the `build.gradle`:

  ```gradle
  defaultTasks 'test'
  ```

- Putting it all together, we can now just run `gw` to build and run the tests.

  ```powershell
  > gw

  > Task :test
  ToAlphanumericTest: testCall: SUCCESS
  Tests: 1, Failures: 0, Errors: 0, Skipped: 0

  BUILD SUCCESSFUL in 4s
  4 actionable tasks: 2 executed, 2 up-to-date
  ```

See [build.gradle](build.gradle) for the complete file.


# Appendix

1. The build/test results are output to a file: `path/to/repo/build/reports/tests/test/index.html`
2. [These old-school `assert` options might come in handy.][7]




[1]: https://medium.com/disney-streaming/testing-jenkins-shared-libraries-4d4939406fa2
[2]: https://dev.to/kuperadrian/how-to-setup-a-unit-testable-jenkins-shared-pipeline-library-2e62
[3]: https://stackoverflow.com/questions/46916673/gradle-exclude-multiple-tasks-programmatically
[4]: https://github.com/jenkinsci/JenkinsPipelineUnit
[5]: https://github.com/jenkinsci/JenkinsPipelineUnit/issues/51
[6]: https://en.wikipedia.org/wiki/Dependency_hell
[7]: https://docs.groovy-lang.org/2.4.7/html/gapi/groovy/util/GroovyTestCase.html
[8]: https://docs.microsoft.com/en-us/powershell/module/microsoft.powershell.core/about/about_splatting?view=powershell-7.1
