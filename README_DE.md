# OpenBible
OpenBible stellt die `Bibel` als einfache Android App zur Verfügung.

# [English README](./README.md)

## Beschreibung
OpenBible lässt Sie die Bibel in mehreren Übersetzungen herunterladen.
Sie können eine heruntergeladene Übersetzung und ein zugehöriges Buch und Kapitel auswählen.
Dann können Sie das ausgewählte Kapitel lesen.
Wenn Sie die App schließen, speichert es den letzten Stand und Sie können weitermachen, wo Sie aufhörten.
Es folgt Ihrem System-Design (Hell-/Dunkelmodus, Sprache und Material Design), um die Erfahrung so leicht und unkompliziert wie möglich zu gestalten.

## Wie funktioniert es?
Die App ist in [Jetpack Compose](https://developer.android.com/compose) geschrieben und nutzt [Material You](https://m3.material.io) in der Gestaltung.
Sie lädt json Dateien, welche von der [GETBIBLE API](https://getbible.net/docs) und dem [SWORD Projekt](https://www.crosswire.org/sword) von Crosswire zur Verfügung gestellt werden, herunter und arbeitet mit diesen.
