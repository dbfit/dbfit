---
layout: page
title: The test framework
nav_bar_name: docs
show_comments: false
---
When you're testing your database code with DbFit, you're actually using the [FitNesse test framework](http://fitnesse.org) to the edit and run your tests. The following pages give a good overview of FitNesse:

- [A two-minute example](http://fitnesse.org/FitNesse.UserGuide.TwoMinuteExample)
- [Editing and creating pages in FitNesse](http://fitnesse.org/FitNesse.UserGuide.EditingFitNessePages)
- [Getting test tables onto the page](http://fitnesse.org/FitNesse.UserGuide.CreatingTestTables)
- [The FitNesse wiki markup language reference](http://fitnesse.org/FitNesse.UserGuide.MarkupLanguageReference)
- [The FitNesse cheat sheet](http://fitnesse.org/FitNesse.UserGuide.QuickReferenceGuide)

## Test execution

### Test results and cell outcomes

When a test is run, FitNesse prints out a result summary. Here's an example:

    Assertions: 5 right, 1 wrong, 0 ignored, 0 exceptions (0.004 seconds)

The test runner calculates this summary by summing the number of table cells that passed, failed and threw exceptions. Any cell in the [different DbFit tables](/dbfit/docs/reference.html) could potentially have any one of these outcomes. The general convention is:

- cells comparing expected results against database resultsets will either be matching (right = green colour) or non-matching (wrong = red colour)
- cells that cause database exceptions will show the exception (and be coloured yellow)
- the other cells generally don't affect the results (they remain a white colour)

### When a failure occurs

If a cell fails:

- the test runner doesn't stop - it will continue executing the remaining tables in the test, and the remaining tests in the suite
- there will be at least one failure in the result summary
- the test will be marked as failed in the suite summary

Framework misconfiguration (eg missing fixture files, missing driver libraries) or keyword typos manifest themselves as exceptions.
