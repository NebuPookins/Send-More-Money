Send-More-Money
===============

Generates puzzles of the form "SEND + MORE = MONEY".

What it does
============

There's a popular puzzle on the internet called "SEND + MORE = MONEY". The idea
is that each letter represents some decimal digit, and to solve the puzzle, you
have to figure out which digit each letter represents. For example, you might
suspect that S represents 1, E represents 2, N represens 3, D represents 4, M
represents 5, O represents 6, R represents 7 and Y represents 8, but if you
perform the substitution, you'll get 1234 + 5672 = 56327, which is false, and
so this is not the correct solution to the puzzle. (If you want to see the
correct solution, you can google for "SEND MORE MONEY".)

This Scala program attempts to generate more puzzles of the same form. It does
so in three phases:

1. It reads in one or more dictionary files to get a list of (usually, but not
necessarily) English words.

2. It finds every possible three-word combination from the word list, and
checks whether there exists any character-to-digit mapping which would yield a
valid equation. For example "A + DEMOCRACY = GOOD" has no possible assignment
that would produce a valid equation. Only combinations which yield a possible
solution move on to the next phase.

3. It tries to find evidence that the three-word combination is "meaningful" to
humans, as opposed to just gibberish. Currently, the program does this by
searching Twitter for an example of a tweet containing the three-word sequence,
on the assumption that if somebody tweeted it, it's probably meaningful. This is
an implementation detail, however, and future versions of this program might
use other strategies for determining that the combination is meaningful.

The program is intended to be highly configurable. There is an application.conf
file which the user can edit to adjust parameters such as the minimum length of
each word (e.g. only produce puzzles where all the words are at least 4 letters
long), and whether to produce puzzles which allow multiple solutions or only
puzzles with unique solutions, etc.

Why it was written
==================

My intent with writing this program was to learn Akka. In order to explore a
good chunk of the Akka API, I figured I needed a problem which (1) consumed a
lot of CPU time, (2) was parallelizable, (3) involved "slow" access to an
outside system. The phase in which the program searches for valid equations is
both slow (it probably could be faster, but I intentionally did not spend too
much time optimizing this part, because the whole point was to have some sort
of slow process) and parallelizable. Querying Twitter was a "slow, outside"
system, particularly because Twitter limits you to around 180 queries every 15
minutes, and so the actor responsible for twitter queries will occasionally
literally just sleep for 15 minutes when it hits the API limits.

Because this program was intended as a learning exercise, I freely invite people
to read the source code. I've tried to provide a lot of documentation explaining
the reasoning for all major code decisions. Also, if anyone has suggestions for
improvements to the code, please send that in also, as that will help me learn.
