# Alert Term Extraction

## How to run
Simply run main class to receive the output in the console.

### Output 
Terms -- \
[ List of query terms from rest api ] \
Alerts -- \
[ List of query terms from rest api ] \

results ---\
results of the the matcher in following format \

```json
[{
	"alertId" : "hz5n855393tn",
  	"queryId" : "204",
  	"alertText" : [ "MÜNCHEN (dpa-AFX) - Die IG Metall erwartet einen heißen Herbst mit vielen Auseinandersetzungen um Jobs in Deutschland. Es wird um viele Arbeitsplätze gehen, 	sagte Vorstandsmitglied und Hauptkassierer Jürgen Kerner am Mittwochabend in München. Alleine in den Branchen, für die die Gewerkschaft zuständig sei, stünden ungefähr 300 	000 Jobs im Feuer. Der größte Bereich dabei seien die Automobilindustrie und deren Zulieferer, doch auch anderen Branchen seien betroffen." ],
  	"queryText" : "jobs"
  	}]

```
