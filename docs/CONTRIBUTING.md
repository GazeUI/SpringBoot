# Contributing

## Coding Guidelines

### File Encoding

  - All source files are encoded in UTF-8.
  - If the file contains code points with more than one byte, it must have a UTF-8 byte order mark (BOM).

### Copyright Notices and License Headers

  - Every file containing source code must include copyright and license information.
  - The file header format must be based on the [CLR JIT Coding Conventions][1].
  - For copyright notices, use the rules described in the [GNU Maintainer Information][2] document.
  - Other useful links:
    - [How do I apply &lt;SOME OPEN SOURCE LICENSE&gt; to software I'm releasing?][3]
    - [.NET Foundation discussion][4] about file headers and copyright statements
    - [The MIT License, Line by Line][5]

### Java

  - [Oracle's Code Conventions for the Java Programming Language][6]
  - [Google's Java Style Guide][7]
  - Optionals
    - Avoid null references going in or coming out of protected, package or public methods. Use
      Optionals instead.
    - Don't use optional in fields, method parameters or collections.
    - Useful links:
      - [Uses for Optional (StackOverflow question)][8]
      - [Should Java 8 getters return optional type? (StackOverflow question)][9]
      - [Intention Revealing Code With Optional][10]
      - [The Design of Optional][11]
      - [26 Reasons Why Using Optional Correctly Is Not Optional][12]
      - [Must(read) on Optional(type)][13]
      - [Java 8 Optional best practices and wrong usage][14]

[1]: https://github.com/dotnet/runtime/blob/master/docs/coding-guidelines/clr-jit-coding-conventions.md#7.2
[2]: https://www.gnu.org/prep/maintain/html_node/Copyright-Notices.html
[3]: https://opensource.org/faq#apply-license
[4]: https://forums.dotnetfoundation.org/t/file-headers-and-copyright-statements/1276
[5]: https://writing.kemitchell.com/2016/09/21/MIT-License-Line-by-Line.html

[6]: https://www.oracle.com/technetwork/java/codeconvtoc-136057.html
[7]: https://google.github.io/styleguide/javaguide.html
[8]: https://stackoverflow.com/questions/23454952/uses-for-optional
[9]: https://stackoverflow.com/questions/26327957/should-java-8-getters-return-optional-type
[10]: https://blog.codefx.org/techniques/intention-revealing-code-java-8-optional/
[11]: https://blog.codefx.org/java/dev/design-optional/
[12]: https://dzone.com/articles/using-optional-correctly-is-not-optional
[13]: https://medium.com/12-developer-labors/must-read-on-optional-type-b171e1b397bb
[14]: http://dolszewski.com/java/java-8-optional-use-cases/