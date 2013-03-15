---
layout: page
title: What's wrong with xUnit
nav_bar_name: docs
show_comments: false
created_at: 2008
author_name: Gojko Adzic
author_url: http://gojko.net
---

DbFit is the result of a three year long effort to apply agile development practices in a database-centric environment. Lack of proper tools for database-level testing was one of the major obstacles in that effort, and DbFit finally solved that issue. Here is a very short summary of that journey and reasons why DbFit was originally created. If you are interested in finding out more about the wider problem and applying agile practices to databases, see my article [Fighting the monster](http://gojko.net/2007/11/20/fighting-the-monster/) and Scott Ambler's [AgileData](http://www.agiledata.org).

Agile practices and databases do not often go hand in hand. For starters, most of the innovation today is in the object-oriented and web space, so database tools are a bit behind. Compared to say Idea or Eclipse, the best IDE available for Oracle PL/SQL development is still in the ice ages. This has influenced the database testing tools and libraries &mdash; most of the tools currently available are copies of JUnit translated into the database environment. Examples are [utPLSQL](http://utplsql.sourceforge.net/) and [TSQLUnit](http://sourceforge.net/apps/trac/tsqlunit/). Some other tools, like [DbUnit](http://www.dbunit.org/) just focus on setting the stage for Java or .NET integration tests, not really for executing tests directly against database code.

The problem with xUnit-like database testing tools is that they require too much boilerplate code. I could never get database developers to really use them when no one was looking over their shoulders. Writing tests was simply seen as too much overhead. All the buzz about object-relational mismatch over the last few years was mostly about relational models getting in the way of object development. This is effectively the other side of the problem, with object tools getting in the way of relational testing.

[FIT testing framework](http://fit.c2.com/), on the other hand, does not suffer from that mismatch. FIT is an acceptance testing framework developed by Ward Cunningam, which is customer oriented and has nothing to do with database unit testing whatsoever. But FIT tests are described as tables, which is much more like the relational model than Java code. FIT also has a nice Web-wiki front-end called FitNesse, which allows database developers to write tests on their own without help from Java or .NET developers. DbFit utilises the power of these two tools to make database tests easy.

My goal with DbFit was not just to enable efficient database testing &mdash; it was to motivate database developers to use an automated testing framework. That is why DbFit has quite a few shortcuts to make database testing easier through DbFit than even doing manual validations in PL/SQL or TSQL.
