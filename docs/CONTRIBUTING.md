# Contributing

## Coding Guidelines

### File Encoding

  - All source files are encoded in UTF-8.
  - If the file contains code points with more than one byte, it must have a UTF-8 byte order mark (BOM).

### Copyright Notices and License Headers

  - Every file containing source code must include copyright and license information.
  - The file header format must be based on the [CLR JIT Coding Conventions][1].
  - For copyright notices, use the rules described in the [GNU Maintainer Information][2] document.
  - Additional resources
    - [How do I apply &lt;SOME OPEN SOURCE LICENSE&gt; to software I'm releasing?][3]
    - [.NET Foundation discussion][4] about file headers and copyright statements
    - [The MIT License, Line by Line][5]

### Java

  - The following rules must be applied for anything that is not specifically stated in this document:
    - [Oracle's Code Conventions for the Java Programming Language][6]
    - [Google's Java Style Guide][7]
  - Formatting
    - Four spaces should be used as the unit of indentation
    - Column limit: 100 characters
      - Lines between 100-110 are perfectly acceptable in many cases where it aids readability and
        where wrapping has the opposite effect of reducing readability
      - When an expression will not fit on a single line, break it after symbols
        - The exceptions are the dot separator (`.`) and the two colons of a method reference (`::`),
          for which the break comes before the symbol
  - Naming
    - When using acronyms and initialisms, define a capitalization that matches the underlying name
      and value readability. For example, `httpRequest`, `HttpRequest` and `HTTPRequest` are all
      valid names.
    - Additional resources
      - [Airbnb JavaScript Style Guide: Acronyms and initialisms][8]
  - Optionals
    - Avoid null references going in or coming out of protected, package or public methods. Use
      Optionals instead.
    - Don't use optional in fields, method parameters or collections.
    - Additional resources
      - [Uses for Optional (StackOverflow question)][9]
      - [Should Java 8 getters return optional type? (StackOverflow question)][10]
      - [Intention Revealing Code With Optional][11]
      - [The Design of Optional][12]
      - [26 Reasons Why Using Optional Correctly Is Not Optional][13]
      - [Must(read) on Optional(type)][14]
      - [Java 8 Optional best practices and wrong usage][15]

[1]: https://github.com/dotnet/runtime/blob/master/docs/coding-guidelines/clr-jit-coding-conventions.md#7.2
[2]: https://www.gnu.org/prep/maintain/html_node/Copyright-Notices.html
[3]: https://opensource.org/faq#apply-license
[4]: https://forums.dotnetfoundation.org/t/file-headers-and-copyright-statements/1276
[5]: https://writing.kemitchell.com/2016/09/21/MIT-License-Line-by-Line.html

[6]: https://www.oracle.com/technetwork/java/codeconvtoc-136057.html
[7]: https://google.github.io/styleguide/javaguide.html
[8]: https://github.com/airbnb/javascript#naming--Acronyms-and-Initialisms
[9]: https://stackoverflow.com/questions/23454952/uses-for-optional
[10]: https://stackoverflow.com/questions/26327957/should-java-8-getters-return-optional-type
[11]: https://blog.codefx.org/techniques/intention-revealing-code-java-8-optional/
[12]: https://blog.codefx.org/java/dev/design-optional/
[13]: https://dzone.com/articles/using-optional-correctly-is-not-optional
[14]: https://medium.com/12-developer-labors/must-read-on-optional-type-b171e1b397bb
[15]: http://dolszewski.com/java/java-8-optional-use-cases/