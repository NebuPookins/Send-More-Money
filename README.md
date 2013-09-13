Send-More-Money
===============

Generates puzzles of the form "SEND + MORE = MONEY".

What it does
============

There's a popular puzzle on the internet called "SEND + MORE = MONEY". The idea
is that each letter represents some decimal digit, and to solve the puzzle, you
have to figure out which digit each letter represents. For example, you might
suspect that S represents 1, E represents 2, N represents 3, D represents 4, M
represents 5, O represents 6, R represents 7 and Y represents 8, but if you
perform the substitution, you'll get 1234 + 5672 = 56328, which is false, and
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

How to run it
=============

The easiest way to compile the program is to use SBT. You can download SBT from
http://www.scala-sbt.org/release/docs/Getting-Started/Setup.html

While SBT is downloading, you should edit the application.conf file. The
location is `src/main/resources/application.conf`. The two most important things
for you to edit are the "twitter-authentication" section, and the
"number-of-sum-checkers" field. Both fields are explained in the
application.conf file itself.

Once you've edited the application.conf file, and have installed SBT, run sbt
and at the prompt, type `run`. You should see output similar to the following:

    [INFO] [09/13/2013 09:58:46.902] [SendMoreMoneySystem-akka.actor.default-dispatcher-2] [akka://SendMoreMoneySystem/user/master] Received files: List(/home/nebu/dev/send-more-money/test.txt, /home/nebu/dev/send-more-money/wikipedia-100-words.txt) 
    [INFO] [09/13/2013 09:58:46.902] [SendMoreMoneySystem-akka.actor.default-dispatcher-2] [akka://SendMoreMoneySystem/user/master] Processing /home/nebu/dev/send-more-money/test.txt...
    [INFO] [09/13/2013 09:58:46.902] [SendMoreMoneySystem-akka.actor.default-dispatcher-2] [akka://SendMoreMoneySystem/user/master] Processing /home/nebu/dev/send-more-money/wikipedia-100-words.txt...
    [INFO] [09/13/2013 09:58:46.927] [SendMoreMoneySystem-akka.actor.default-dispatcher-2] [akka://SendMoreMoneySystem/user/master] Master received start message. Starting. Length of words is 53.
    [INFO] [09/13/2013 09:58:47.393] [SendMoreMoneySystem-akka.actor.default-dispatcher-2] [akka://SendMoreMoneySystem/user/master] Master has finished sending out the wordlist.

Then, depending on how large your wordlist is, you may have a pause here
anywhere from a few seconds, to tens of minutes.

Eventually, you should see puzzles being churned out, e.g.


                FROM
        +      THERE
        ============
               OTHER
    E.g. @TweetsfromSandy @Paige2psu my mom bought me a Vera Wang dress from Kohl's a couple weeks ago. Only thing I have from there other than Nike.
    Solutions: 7536 + 24959 = 32495
    
                WITH
        +       WHAT
        ============
               DADDY
    E.g. If you have a problem with what daddy says, go to the house. He's the boss. I'm tired of working with immature "adults"
    Solutions: 6278 + 6837 = 13115

... and so on. Eventually, you should see:

    [INFO] [09/13/2013 10:01:10.282] [SendMoreMoneySystem-akka.actor.default-dispatcher-1] [akka://SendMoreMoneySystem/user/master] All work done. Shutting down.
    [success] Total time: 144 s, completed 13-Sep-2013 10:01:10 AM

... after which you will be returned to the SBT prompt. Type `exit` to quit SBT.

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
