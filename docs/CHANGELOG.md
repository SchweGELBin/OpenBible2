# Changelog

All notable changes to this project will be documented in this file.

## [3.0.1] - 2026-02-05

### ⚙️ Miscellaneous Tasks

- *(translations)* Translate using Weblate (Estonian) (#123)
- *(translations)* Add translation using Weblate (Russian) (#124)
- *(translations)* Translate using Weblate (Russian) (#125)
- *(translations)* Update OpenBible/Application (#126)
- *(translations)* Translate using Weblate (Russian) (#127)
- *(translations)* Translate using Weblate (Russian) (#129)
- *(translations)* Translate using Weblate (Russian) (#128)

## [3.0.0] - 2026-02-02

### 🚀 Features

- Added Dutch translations (#103)
- Added agents.md file (#100)
- [**breaking**] Remove apocrypha
- Import translations via files

### 🐛 Bug Fixes

- Remove kotlin-android plugin

### 🚜 Refactor

- Delete unnecessary files
- *(metadata)* Cleanup nl

### 📚 Documentation

- *(readme)* Add dutch translation
- *(license)* Update copyright year
- *(readme)* Remove dutch version
- *(readme)* Add contribution and translation section
- *(readme)* Add translation note
- *(readme)* Fix syntax

### ⚙️ Miscellaneous Tasks

- Update .gitignores
- Update .gitignore
- *(codeql)* Scan without building
- *(translations)* Translate using Weblate (German) (#114)
- *(translations)* Update OpenBible/Application (#113)
- *(translations)* Update OpenBible/Application (#115)
- *(templates)* Update issue templates
- *(translations)* Update OpenBible/Application (#116)
- *(translations)* Translate using Weblate (Estonian) (#117)
- *(translations)* Translate using Weblate (German) (#118)
- *(gitignore)* Add .idea/deviceManager.xml
- *(translations)* Update OpenBible/Application (#120)
- Bump to 3.0.0

### ◀️ Revert

- Ci(codeql): scan without building

## [2.1.2] - 2025-11-24

### 🐛 Bug Fixes

- *(ui)* Improve overflow in the read screen

### 🚜 Refactor

- Remove setting EdgeToEdge

### 🎨 Styling

- Cleanup app/build.gradle.kts

### ⚙️ Miscellaneous Tasks

- Bump to 2.1.2

## [2.1.1] - 2025-11-20

### 🐛 Bug Fixes

- Deep link can't open first book
- Default to chapter 1 in deeplinks
- Disable deep links for translations with fewer books

### ⚡ Performance

- Simplify checkSelection

### ⚙️ Miscellaneous Tasks

- Bump to 2.1.1

## [2.1.0] - 2025-11-19

### 🚀 Features

- Deeplink support

### 🐛 Bug Fixes

- Setting out of bounce values using deep links
- *(ui)* Set search bar container color to transparent

### 🎨 Styling

- Rename route value in App

### ⚙️ Miscellaneous Tasks

- Update tagetApi to match targetSdk
- Use droplast instead of substring in getChapter
- Remove unused imports
- Bump to 2.1.0

## [2.0.0] - 2025-09-18

### 🚀 Features

- Add contact button to settings
- Move translation management from Settings to Selection
- [**breaking**] Improve translation management

### 🐛 Bug Fixes

- Don't allow deleting the last translation
- Fallback to working translation in case of a deserialization error
- Selection not showing available translations of installed languages
- Create fallbacks for fallbacks for translations

### 📚 Documentation

- *(metadata)* Update short and full descriptions
- *(readme)* Update short and full descriptions
- *(readme)* Fix typo
- *(metadata, readme)* Fix another typo
- *(readme)* Format table

### 🎨 Styling

- Cleanup Icon syntax
- Cleanup elevation syntax

### ⚙️ Miscellaneous Tasks

- Remove unnecessary val
- Update getbible link (getbible.net -> getbible.life)
- Bump to 2.0.0

## [1.9.2] - 2025-08-20

### ⚙️ Miscellaneous Tasks

- Bump to 1.9.2

### ◀️ Revert

- Chore(deps): bump com.android.application from 8.11.1 to 8.12.0 (#63)

## [1.9.1] - 2025-08-18

### 🐛 Bug Fixes

- *(ui)* Improve alignment in selection screen
- *(ui)* Match menu color to background

### 📚 Documentation

- *(metadata)* Add one missing but added feature to changelog 31 (1.9.0)

### ⚙️ Miscellaneous Tasks

- Bump to 1.9.1

## [1.9.0] - 2025-07-18

### 🚀 Features

- Seperate translation and chapter selection in ReadScreen title
- Set font size range

### 🐛 Bug Fixes

- *(ui)* Remove padding around TranslationButton in ReadScreen
- Update kotlin jvmTarget

### 🚜 Refactor

- *(ui)* Use switches instead of checkboxes in settings

### 📚 Documentation

- *(readme)* Update `Get It On Github` badge link
- *(readme)* Fix license path of license badge

### ⚙️ Miscellaneous Tasks

- Update deprecated kotlin compilerOptions
- Bump to 1.9.0

## [1.8.1] - 2025-06-12

### 📚 Documentation

- Move documentation to ./docs
- Move license back to root

### ⚙️ Miscellaneous Tasks

- Update .idea
- *(build)* Sign apk
- *(build)* Cleanup keystore after signing
- *(build)* Correctly decrypt secret
- *(build)* Correctly decrypt secret
- *(build)* Install apksigner
- *(build)* Install apksigner
- Add release workflow
- *(build)* Fix syntax error
- *(dependabot)* Add github-actions
- *(build)* Inherit secret
- *(release)* Fix release body
- Bump to 1.8.1

## [1.8.0] - 2025-05-09

### 🚀 Features

- Add infinite scroll checkmark
- Pinch to zoom
- Add SearchScreen
- Double tap to zoom

### 🐛 Bug Fixes

- Output for checksum was null
- Translation selection not updating
- Old dirs not deleting
- Back gesture blocked by search bar

### 🚜 Refactor

- Split shared prefs logic to seperat file

### ⚡ Performance

- Automatically calculate shasum
- Improve startup logic
- Reorder startup checks

### ⚙️ Miscellaneous Tasks

- Bump sdk from 35 to 36
- Remove "DropdownSelection" from deploymentTargetSelector.xml
- Add todos and initialize them
- Update Menu UI
- Add issue links to todos
- Change dependabot intervall from weekly to monthly
- Close menu on click
- Finish search todo
- Bump to 1.8.0

## [1.7.0] - 2025-03-24

### 🚀 Features

- *(selection)* Show translation information
- Backup data
- Add horizontal split screen
- Restore user backup
- Add previous/next chapter buttons to ReadScreen

### 🐛 Bug Fixes

- Return to download translation screen when no translation found

### 📚 Documentation

- *(license)* Bump copyright date

### ⚡ Performance

- Improve translation information interface

### 🎨 Styling

- Reformat and cleanup
- Reformat

### ⚙️ Miscellaneous Tasks

- Use ktx functions
- Bump to 1.7.0

## [1.6.1] - 2025-03-11

### 📚 Documentation

- *(strings)* Add PT-BR (#37)
- *(strings)* Make some keys untranslatable

### ⚙️ Miscellaneous Tasks

- Bump to 1.6.1

## [1.6.0] - 2025-02-25

### 🚀 Features

- *(settings)* Add `Google Play` link
- *(theme)* Add custom theme based on app icon color

### 🎨 Styling

- Cleanup dependencies
- Cleanup dependencies again

### ⚙️ Miscellaneous Tasks

- Auto-merge dependabot PRs
- *(auto-merge)* Declare repo
- *(auto-merge)* Remove condition
- *(auto-merge)* Fix last commit
- *(auto-merge)* Squash instead if merge
- *(codeql)* Disable for dependabot
- Bump to 1.6.0

### ◀️ Revert

- Ci(codeql): disable for dependabot

## [1.5.2] - 2025-01-22

### 🐛 Bug Fixes

- Selecting smaller translation at first launch

### ⚙️ Miscellaneous Tasks

- Bump to 1.5.2

## [1.5.1] - 2025-01-22

### 🐛 Bug Fixes

- Back button closes the app at Read screen
- Book out of bounce at language selection
- Return right number of chapters

### 📚 Documentation

- Add issue templates
- Add SECURITY.md

### 🎨 Styling

- Reformat and cleanup
- Reformat and cleanup

### ⚙️ Miscellaneous Tasks

- Add dependabot.yml
- Add codeql.yml
- Dont schedule codeql
- Bump to 1.5.1

## [1.5.0] - 2024-12-19

### 🚀 Features

- Toggle download notification visibility
- Display language of translations

### 📚 Documentation

- Update changelog
- Add GitHub badge

### 🎨 Styling

- Reformat

### ⚙️ Miscellaneous Tasks

- Bump to 1.5.0

## [1.4.2] - 2024-12-15

### ⚙️ Miscellaneous Tasks

- Add build workflow
- Bump to 1.4.2
- *(build)* Update apk path
- Bump to 1.4.2
- Bump to 1.4.2
- Bump to 1.4.2

## [1.4.1] - 2024-12-14

### ⚙️ Miscellaneous Tasks

- Bump to 1.4.1

## [1.4.0] - 2024-12-13

### 🚀 Features

- Readd update translations button

### 🐛 Bug Fixes

- Handle missing index
- Update index before manually updating a translation
- Reset clicked status
- Adapt status bar color
- *(ui)* Align start screen items in the center

### 🚜 Refactor

- Merge duplicated code

### 📚 Documentation

- *(readme)* Add F-Droid badges

### ⚡ Performance

- Remove unnecessary check
- Remove global constants
- Declare vals of time

### 🎨 Styling

- Reformat
- Reformat
- Reformat
- Reformat

### ⚙️ Miscellaneous Tasks

- Bump to 1.4.0

## [1.3.0] - 2024-12-11

### 🚀 Features

- Reference getBible in settings
- Update translations at startup

### 📚 Documentation

- Reference getBible

### 🎨 Styling

- Reformat
- Reformat

### ⚙️ Miscellaneous Tasks

- Restructure metadata
- Update dependencies
- Bump to 1.3.0

## [1.2.2] - 2024-12-08

### 🐛 Bug Fixes

- Hide unsupported options

### 📚 Documentation

- *(metadata)* Typo in changelog

### 🎨 Styling

- Reformat

### ⚙️ Miscellaneous Tasks

- Bump to 1.2.2
- Bump to 1.2.2

## [1.2.1] - 2024-12-07

### 🐛 Bug Fixes

- Handle long strings

### ⚙️ Miscellaneous Tasks

- Update dependencies
- Bump to 1.2.1

## [1.2.0] - 2024-12-03

### 🚀 Features

- Add split screen

### 🐛 Bug Fixes

- Improved padding

### 🎨 Styling

- Reformat
- Reformat

### ⚙️ Miscellaneous Tasks

- Remove redundant if statement
- Bump to 1.2.0

## [1.1.0] - 2024-12-02

### 🚀 Features

- Update translations
- Update index before the translations
- Clean up translations on updateTranslations
- Delete translation

### 🐛 Bug Fixes

- Replace deprecated method
- Protect user from deleting all translations

### 📚 Documentation

- *(readme)* Remove deleted screenshots

### 🎨 Styling

- Reformat

### ⚙️ Miscellaneous Tasks

- Remove unused function
- Remove redundant if statement
- Bump to 1.1.0

## [1.0.0] - 2024-12-01

### 🚀 Features

- [**breaking**] Add Start screen
- Improve selection screen ui
- Improve
- Add new strings to res/values
- Add new strings to res/values

### 🐛 Bug Fixes

- Make selection scrollable again
- First launch
- Used wrong string
- Not selecting while saving last selection

### 🎨 Styling

- Reformat

### ⚙️ Miscellaneous Tasks

- Bump to 1.0.0

## [0.7.0] - 2024-11-23

### 🚀 Features

- Show/hide verse number

### 🚜 Refactor

- Combine shared prefs
- Remove global variables

### ⚡ Performance

- Remember selection, add default selection vars

### 🎨 Styling

- Reformat code
- Reformat

### ⚙️ Miscellaneous Tasks

- Bump to 0.7.0

## [0.6.1] - 2024-11-20

### ⚙️ Miscellaneous Tasks

- Bump to 0.6.1

## [0.6.0] - 2024-11-19

### 🚀 Features

- Recompose on theme change

### 🎨 Styling

- Reformat code

### ⚙️ Miscellaneous Tasks

- Bump to 0.6.0

## [0.5.0] - 2024-11-18

### 🚀 Features

- Select text style (alignment)

### 🎨 Styling

- Reformat code
- Adjust button widths in settings
- Remove custom NavBar size

### ⚙️ Miscellaneous Tasks

- *(metadata)* Update screenshots
- Add androidx.navigation dependency
- Bump to 0.5.0

## [0.4.0] - 2024-11-14

### 🚀 Features

- Change color scheme
- Selectable text

### 📚 Documentation

- Remove OpenAPK badge

### 🎨 Styling

- Format code

### ⚙️ Miscellaneous Tasks

- Update dependencies
- Bump to 0.4.0

## [0.3.0] - 2024-11-10

### 🚀 Features

- Use adaptive icon

### 📚 Documentation

- *(readme)* Add download badge
- *(readme)* Add links to badges
- Capitalize `Bible`

### ⚙️ Miscellaneous Tasks

- Bump to 0.3.0

## [0.2.1] - 2024-11-07

### ⚡ Performance

- Enable minify and shrink

### ⚙️ Miscellaneous Tasks

- Bump to 0.2.1

## [0.2.0] - 2024-11-07

### 🚀 Features

- Update read icon
- Colorize title

### 🚜 Refactor

- Remove duplication

### 📚 Documentation

- *(readme)* Add OpenAPK badge
- *(readme)* Update german translation
- *(readme)* Update german translation
- *(readme)* Sync description with fastlane
- *(readme)* Update german translation

### 🎨 Styling

- Reformat code

### ⚙️ Miscellaneous Tasks

- Update dependencies
- *(metadata)* Update screenshots
- Bump to 0.1.5
- Bump to 0.2.0

## [0.1.4] - 2024-11-05

### 📚 Documentation

- *(readme)* Add izzyondroid badges

### ⚙️ Miscellaneous Tasks

- Drop jdk 22 -> 21
- Initial fastlane data from IzzyOnDroid
- Restructure repository
- Bump to 0.1.4

## [0.1.3] - 2024-11-04

### 📚 Documentation

- *(readme)* Add description
- *(readme)* Add german translation
- *(readme)* Link german translation
- *(readme)* Update german translation
- Add badges to readme
- Add icon, feature graphic and screenshots
- *(readme)* Add screenshots
- *(changelog)* Update

### ⚙️ Miscellaneous Tasks

- Remove app/release from repo
- Bump to 0.1.3

### Build

- Remove dependenciesInfo from apk/bundle

## [0.1.2] - 2024-10-23

### ⚙️ Miscellaneous Tasks

- Release 0.1.2
- Release 0.1.2

## [0.1.1] - 2024-10-23

### 📚 Documentation

- Update readme

### ⚙️ Miscellaneous Tasks

- Release 0.1.1

### Build

- Add aab

## [0.1.0] - 2024-10-22

### 🚀 Features

- Download checksum
- Improve Read Screen UI
- Add translation(de) and todos
- Improve file structure
- Choose, which translation to download
- Add select dialog
- Choose translation, book and chapter
- *(ui)* Select books and chapters from a table
- *(ui)* Improve settings screen, add about section
- Save last selection

### 🐛 Bug Fixes

- Serialization error
- Crash at first time launch
- Crash because of not checking book number
- Default translation
- Crash when checksum not found
- Crashes with unloaded files
- Overflow of old selected Items to new ones
- Crash when using a downloading file
- First time launch without internet

### 🚜 Refactor

- Change file structure, delete example tests

### 📚 Documentation

- Add changelog
- Update changelog
- Update changelog
- Update changelog
- Update changelog
- Update changelog
- Update changelog
- Update changelog
- Update changelog
- Update changelog
- Fix wrong name of compose
- Update changelog
- Update changelog
- Update changelog

### ⚡ Performance

- Split mainactivity, optimize code

### 🎨 Styling

- Format
- Cleanup
- Format and cleanup
- Add custom app icon
- Update app icon
- Cleanup

### ⚙️ Miscellaneous Tasks

- Init
- Add license
- Remove todos

### Build

- Update apk
- Update apk
- Update apk
- Update apk
- Update apk
- Update apk
- Update apk
- Update apk
- Update apk

<!-- generated by git-cliff -->
