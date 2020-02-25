# Contributing

## Coding Guidelines

### Java

  - [Oracle's Code Conventions for the Java Programming Language][1]
  - [Google's Java Style Guide][2]
  - Optionals
    - Avoid null references going in or coming out of protected, package or public methods. Use
      Optionals instead.
    - Don't use optional in fields, method parameters or collections.
    - Useful links:
      - [Uses for Optional (StackOverflow question)][3]
      - [Should Java 8 getters return optional type? (StackOverflow question)][4]
      - [Intention Revealing Code With Optional][5]
      - [The Design of Optional][6]
      - [26 Reasons Why Using Optional Correctly Is Not Optional][7]
      - [Must(read) on Optional(type)][8]
      - [Java 8 Optional best practices and wrong usage][9]

[1]: https://www.oracle.com/technetwork/java/codeconvtoc-136057.html
[2]: https://google.github.io/styleguide/javaguide.html
[3]: https://stackoverflow.com/questions/23454952/uses-for-optional
[4]: https://stackoverflow.com/questions/26327957/should-java-8-getters-return-optional-type
[5]: https://blog.codefx.org/techniques/intention-revealing-code-java-8-optional/
[6]: https://blog.codefx.org/java/dev/design-optional/
[7]: https://dzone.com/articles/using-optional-correctly-is-not-optional
[8]: https://medium.com/12-developer-labors/must-read-on-optional-type-b171e1b397bb
[9]: http://dolszewski.com/java/java-8-optional-use-cases/