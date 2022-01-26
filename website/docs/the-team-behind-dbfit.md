---
layout: page
title: The team behind DbFit
nav_bar_name: docs
show_comments: false
---
Ordinarily, the question of who maintains an open-source project is easily answered - there is generally one main project repository and one set of maintainers. For DbFit, the picture is somewhat convoluted, as there are several branches of the project - I've tried to explain the situation in a recent blog post, [DbFit: the past and present](http://blog.quickpeople.co.uk/2013/03/21/dbfit-the-past-and-present/).

The binary distribution of `DbFit 2.X`, which is available from [dbfit.github.io/dbfit](http://dbfit.github.io/dbfit), contains 3 parts:

- **FitNesse**: this is the test framework, HTTP server and wiki editing part.
  - maintained by [Arjan Molenaar](https://github.com/amolenaar), [Mike Stockdale](https://github.com/jediwhale) and [Dan Woodward](https://github.com/woodybrood)
  - binary comes from [fitnesse.org](http://fitnesse.org)
  - source code lives at [github.com/unclebob/fitnesse](https://github.com/unclebob/fitnesse)
- **DbFit/Java**: this is a Java implementation of the DbFit tables which FitNesse uses to talk to the database.
  - maintained by [Yavor Nikolov](https://github.com/javornikolov) and [Jake Benilov](https://github.com/benilovj)
  - binaries are built by Jake or Yavor whenever we cut a release
  - source code lives at [{{ site.repo }}]({{ site.repo }})
- **DbFit/Fitsharp**: this is the .NET implementation of the DbFit tables.
  - maintained by [Mike Stockdale](https://github.com/jediwhale)
  - source code lives at [github.com/jediwhale/fitsharp](https://github.com/jediwhale/fitsharp)
  - the DbFit/Fitsharp binaries come from [github.com/jediwhale/fitsharp](https://github.com/jediwhale/fitsharp/tree/master/binary)
