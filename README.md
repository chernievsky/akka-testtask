# akka-testtask

####(Just a simple test task to get a little practice with akka)

###Task description:
"Technologies to use: Akka Actors, Akka Http

We have two actors:
1. Googler actor, which receives messages of the form AskGoogle(question: String) and returns Seq(url: String)
2. Reminder actor, which remembers all messages of the form RememberString(toRemember: String), and after receiving message "Remind" returns Seq(strings: String)
3. REST API to work with it

Actors are connected. Googler sends all queries to Reminder to get them remembered"

###API
/ask?q=**something** - scrape and return results from google first page for query **something**  
/remind - get previous queries