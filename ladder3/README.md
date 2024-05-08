# Ladder 3

## Aufgabe 1

Die Dateien in "/materials/2023-06-15" sind so organisiert:

* Alle Dateien in einem Verzeichnis haben den gleichen Text
* In "Modifiers" sind die Modifiers gelb markiert. Der Name der Datei gibt den Modifier bekannt (z.B. "Downtorner, Evaluation...)
* In "Subacts" werden die Subacts angegeben. (Z.B. "Alerter")

Wir brauchen:

* Eine Datenstruktur, die folgende Datenverarbeitungen ermöglicht: 
  * Erfassung des Original-Textes
  * Erfassung aller Modifiers (von ... bis)
  * Erfassung aller Subacts (von ... bis)
* Eine Exportmöglichkeit für diese Datenstruktur:
  * In TEI-XML, wobei die Modifiers und die Subacts markiert werden
  * In JSON
  * In CSV
