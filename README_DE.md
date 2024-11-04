# OpenBible
![GitHub License](https://img.shields.io/github/license/SchweGELBin/OpenBible2)
![GitHub Release](https://img.shields.io/github/v/release/SchweGELBin/OpenBible2)

<a href="https://play.google.com/store/apps/details?id=com.schwegelbin.openbible"><img src="https://play.google.com/intl/en_us/badges/images/generic/de_badge_web_generic.png" height="60"></a>

[English](./README.md)

OpenBible stellt die `Bibel` als einfache Android App zur Verfügung.

## Beschreibung
OpenBible lässt Sie die Bibel in mehreren Übersetzungen herunterladen.
Sie können eine heruntergeladene Übersetzung und ein zugehöriges Buch und Kapitel auswählen.
Dann können Sie das ausgewählte Kapitel lesen.
Wenn Sie die App schließen, speichert es den letzten Stand und Sie können weitermachen, wo Sie aufhörten.
Es folgt Ihrem System-Design (Hell-/Dunkelmodus, Sprache und Material Design), um die Erfahrung so leicht und unkompliziert wie möglich zu gestalten.

## Wie funktioniert es?
Die App ist in [Jetpack Compose](https://developer.android.com/compose) geschrieben und nutzt [Material You](https://m3.material.io) in der Gestaltung.
Sie lädt json Dateien, welche von der [GETBIBLE API](https://getbible.net/docs) und dem [SWORD Projekt](https://www.crosswire.org/sword) von Crosswire zur Verfügung gestellt werden, herunter und arbeitet mit diesen.

## Blidschirmfotos
| ![](./res/screenshots/screenshot_screen_home.png) | ![](./res/screenshots/screenshot_screen_read.png) | ![](./res/screenshots/screenshot_screen_settings.png) |
|---------------------------------------------------|---------------------------------------------------|-------------------------------------------------------|
| ![](./res/screenshots/screenshot_choose_translation.png) | ![](./res/screenshots/screenshot_choose_book.png) | ![](./res/screenshots/screenshot_choose_chapter.png) |