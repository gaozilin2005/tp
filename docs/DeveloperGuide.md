---
layout: page
title: Developer Guide
---
* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

* {list here sources of all reused/adapted ideas, code, documentation, and third-party libraries -- include links to the original source as well}

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

<div markdown="span" class="alert alert-primary">

:bulb: **Tip:** The `.puml` files used to create diagrams are in this document `docs/diagrams` folder. Refer to the [_PlantUML Tutorial_ at se-edu/guides](https://se-education.org/guides/tutorials/plantUml.html) to learn how to create and edit diagrams.
</div>

### Architecture

<img src="images/ArchitectureDiagram.png" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

Given below is a quick overview of main components and how they interact with each other.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `delete 1`.

<img src="images/ArchitectureSequenceDiagram.png" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* implements its functionality using a concrete `{Component Name}Manager` class (which follows the corresponding API `interface` mentioned in the previous point.

For example, the `Logic` component defines its API in the `Logic.java` interface and implements its functionality using the `LogicManager.java` class which follows the `Logic` interface. Other components interact with a given component through its interface rather than the concrete class (reason: to prevent outside component's being coupled to the implementation of a component), as illustrated in the (partial) class diagram below.

<img src="images/ComponentManagers.png" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/Ui.java)

![Structure of the UI Component](images/UiClassDiagram.png)

The UI consists of a `MainWindow` that is made up of parts e.g.`CommandBox`, `ResultDisplay`, `PersonListPanel`, `StatusBarFooter` etc. All these, including the `MainWindow`, inherit from the abstract `UiPart` class which captures the commonalities between classes that represent parts of the visible GUI.

The `UI` component uses the JavaFx UI framework. The layout of these UI parts are defined in matching `.fxml` files that are in the `src/main/resources/view` folder. For example, the layout of the [`MainWindow`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/MainWindow.java) is specified in [`MainWindow.fxml`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/resources/view/MainWindow.fxml)

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component, as it displays `Person` object residing in the `Model`.

### Logic component

**API** : [`Logic.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<img src="images/LogicClassDiagram.png" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete 1")` API call as an example.

![Interactions Inside the Logic Component for the `delete 1` Command](images/DeleteSequenceDiagram.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</div>

How the `Logic` component works:

1. When `Logic` is called upon to execute a command, it is passed to an `AddressBookParser` object which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a person).<br>
   Note that although this is shown as a single step in the diagram above (for simplicity), in the code it can take several interactions (between the command object and the `Model`) to achieve.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned back from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<img src="images/ParserClasses.png" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name e.g., `AddCommandParser`) which uses the other classes shown above to parse the user command and create a `XYZCommand` object (e.g., `AddCommand`) which the `AddressBookParser` returns back as a `Command` object.
* All `XYZCommandParser` classes (e.g., `AddCommandParser`, `DeleteCommandParser`, ...) inherit from the `Parser` interface so that they can be treated similarly where possible e.g, during testing.

### Model component
**API** : [`Model.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/model/Model.java)

<img src="images/ModelClassDiagram.png" width="450" />


The `Model` component,

* stores the address book data i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
* stores the currently 'selected' `Person` objects (e.g., results of a search query) as a separate _filtered_ list which is exposed to outsiders as an unmodifiable `ObservableList<Person>` that can be 'observed' e.g. the UI can be bound to this list so that the UI automatically updates when the data in the list change.
* stores a `UserPref` object that represents the user’s preferences. This is exposed to the outside as a `ReadOnlyUserPref` objects.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)

<div markdown="span" class="alert alert-info">:information_source: **Note:** An alternative (arguably, a more OOP) model is given below. It has a `Tag` list in the `AddressBook`, which `Person` references. This allows `AddressBook` to only require one `Tag` object per unique tag, instead of each `Person` needing their own `Tag` objects.<br>

<img src="images/BetterModelClassDiagram.png" width="450" />

</div>

### Extended Person Model

The `Person` class has been extended to support three new optional fields:
- **`Organisation`**: Represents the institution (e.g., company, school) the person is affiliated with.
- **`Event`**: Records meeting contexts or future interactions (e.g., "Met at NUS Career Fair").
- **`Note`**: Stores cultural or personal notes (e.g., "Prefers Western cuisine").

All three fields are:
- Immutable and validated
- Optional (default to empty string)
- Fully serializable to JSON
- Positioned in the constructor as: `(Name, Phone, Email, Address, Country, Organisation, Event, Note, ...)`

### Storage component

**API** : [`Storage.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/storage/Storage.java)

<img src="images/StorageClassDiagram.png" width="550" />

The `Storage` component,
* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* inherits from both `AddressBookStorage` and `UserPrefStorage`, which means it can be treated as either one (if only the functionality of only one is needed).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### Extended Person Model

The `Person` class has been extended to support three new optional fields:
- **`Organisation`**: Represents the institution (e.g., company, school) the person is affiliated with.
- **`Event`**: Records meeting contexts or future interactions (e.g., "Met at NUS Career Fair").
- **`Note`**: Stores cultural or personal notes (e.g., "Prefers WhatsApp").

All three fields are:
- Immutable and validated
- Optional (default to empty string)
- Fully serializable to JSON
- Positioned in the constructor as: `(Name, Phone, Email, Address, Country, Organisation, Event, Note, ...)`

### \[Proposed\] Undo/redo feature

#### Proposed Implementation

The proposed undo/redo mechanism is facilitated by `VersionedAddressBook`. It extends `AddressBook` with an undo/redo history, stored internally as an `addressBookStateList` and `currentStatePointer`. Additionally, it implements the following operations:

* `VersionedAddressBook#commit()` — Saves the current address book state in its history.
* `VersionedAddressBook#undo()` — Restores the previous address book state from its history.
* `VersionedAddressBook#redo()` — Restores a previously undone address book state from its history.

These operations are exposed in the `Model` interface as `Model#commitAddressBook()`, `Model#undoAddressBook()` and `Model#redoAddressBook()` respectively.

Given below is an example usage scenario and how the undo/redo mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `VersionedAddressBook` will be initialized with the initial address book state, and the `currentStatePointer` pointing to that single address book state.

![UndoRedoState0](images/UndoRedoState0.png)

Step 2. The user executes `delete 5` command to delete the 5th person in the address book. The `delete` command calls `Model#commitAddressBook()`, causing the modified state of the address book after the `delete 5` command executes to be saved in the `addressBookStateList`, and the `currentStatePointer` is shifted to the newly inserted address book state.

![UndoRedoState1](images/UndoRedoState1.png)

Step 3. The user executes `add n/David …​` to add a new person. The `add` command also calls `Model#commitAddressBook()`, causing another modified address book state to be saved into the `addressBookStateList`.

![UndoRedoState2](images/UndoRedoState2.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** If a command fails its execution, it will not call `Model#commitAddressBook()`, so the address book state will not be saved into the `addressBookStateList`.

</div>

Step 4. The user now decides that adding the person was a mistake, and decides to undo that action by executing the `undo` command. The `undo` command will call `Model#undoAddressBook()`, which will shift the `currentStatePointer` once to the left, pointing it to the previous address book state, and restores the address book to that state.

![UndoRedoState3](images/UndoRedoState3.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** If the `currentStatePointer` is at index 0, pointing to the initial AddressBook state, then there are no previous AddressBook states to restore. The `undo` command uses `Model#canUndoAddressBook()` to check if this is the case. If so, it will return an error to the user rather
than attempting to perform the undo.

</div>

The following sequence diagram shows how an undo operation goes through the `Logic` component:

![UndoSequenceDiagram](images/UndoSequenceDiagram-Logic.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The lifeline for `UndoCommand` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline reaches the end of diagram.

</div>

Similarly, how an undo operation goes through the `Model` component is shown below:

![UndoSequenceDiagram](images/UndoSequenceDiagram-Model.png)

The `redo` command does the opposite — it calls `Model#redoAddressBook()`, which shifts the `currentStatePointer` once to the right, pointing to the previously undone state, and restores the address book to that state.

<div markdown="span" class="alert alert-info">:information_source: **Note:** If the `currentStatePointer` is at index `addressBookStateList.size() - 1`, pointing to the latest address book state, then there are no undone AddressBook states to restore. The `redo` command uses `Model#canRedoAddressBook()` to check if this is the case. If so, it will return an error to the user rather than attempting to perform the redo.

</div>

Step 5. The user then decides to execute the command `list`. Commands that do not modify the address book, such as `list`, will usually not call `Model#commitAddressBook()`, `Model#undoAddressBook()` or `Model#redoAddressBook()`. Thus, the `addressBookStateList` remains unchanged.

![UndoRedoState4](images/UndoRedoState4.png)

Step 6. The user executes `clear`, which calls `Model#commitAddressBook()`. Since the `currentStatePointer` is not pointing at the end of the `addressBookStateList`, all address book states after the `currentStatePointer` will be purged. Reason: It no longer makes sense to redo the `add n/David …​` command. This is the behavior that most modern desktop applications follow.

![UndoRedoState5](images/UndoRedoState5.png)

The following activity diagram summarizes what happens when a user executes a new command:

<img src="images/CommitActivityDiagram.png" width="250" />

#### Design considerations:

**Aspect: How undo & redo executes:**

* **Alternative 1 (current choice):** Saves the entire address book.
  * Pros: Easy to implement.
  * Cons: May have performance issues in terms of memory usage.

* **Alternative 2:** Individual command knows how to undo/redo by
  itself.
  * Pros: Will use less memory (e.g. for `delete`, just save the person being deleted).
  * Cons: We must ensure that the implementation of each individual command are correct.

_{more aspects and alternatives to be added}_

## **Data Archiving & Sorting**

### Archiving Contacts

The app now supports **archiving and unarchiving** contacts to help users keep their address book relevant and organized.

#### Commands

| Action        | Command Format & Example |
|---------------|-------------------------|
| Archive       | `archive INDEX` <br> e.g., `archive 1` → archives the first contact in the current view. |
| Unarchive     | `unarchive INDEX` <br> e.g., `unarchive 2` → restores the second contact from the archived list. |
| List Archives | `archivelist` <br> e.g., `archivelist` → displays all archived contacts. |

#### Implementation

- Each `Person` object contains an **archival status** flag (`true` for archived, `false` for unarchived`).
- **Archiving a person** (`ArchiveCommand`) replaces the original `Person` in the model with a copy that has `archivalStatus = true`.
- **Unarchiving** (`UnarchiveCommand`) similarly creates a copy with `archivalStatus = false`.
- `ArchiveListCommand` updates the filtered list to show only archived contacts.
- Commands validate the index against the current filtered list and return appropriate errors if:
    - Index is invalid
    - Contact is already archived/unarchived

#### Interaction with Model and UI

- Commands update the **filtered person list** in the `Model` via predicates:
    - `PREDICATE_SHOW_ALL_UNARCHIVED` → shows only active contacts
    - `PREDICATE_SHOW_ALL_ARCHIVED` → shows only archived contacts
- The UI observes the filtered list and automatically refreshes when changes occur.
- Archiving does **not remove contacts from storage**, preserving the full address book.

#### Example Scenario

1. User executes `archive 1` on the first contact:
    - `ArchiveCommand` validates the index.
    - Creates a new `Person` object with `archivalStatus = true`.
    - Updates the `Model` and refreshes the view to show only unarchived contacts.
2. User executes `archivelist`:
    - `ArchiveListCommand` updates the filtered list to show archived contacts only.
3. User executes `unarchive 1`:
    - `UnarchiveCommand` validates the index in the archived list.
    - Replaces the archived person with an unarchived version.
    - Updates the view to show archived contacts or returns to unarchived view depending on predicate.

#### Manual Test Cases

| Test | Command | Expected Outcome |
|------|---------|-----------------|
| Archive a contact | `archive 1` | Contact is moved from unarchived list; success message displayed. |
| Archive an already archived contact | `archive 1` | Error message: contact already archived. |
| List archives | `archivelist` | Only archived contacts displayed. |
| Unarchive a contact | `unarchive 1` | Contact restored to unarchived list; success message displayed. |
| Unarchive an unarchived contact | `unarchive 1` | Error message: contact already unarchived. |

---

### Sorting Contacts

The app also supports sorting contacts by various criteria:

| Action            | Command Format & Example |
|------------------|-------------------------|
| Sort by Name      | `sortname` <br> e.g., `sortname` → sorts all visible contacts alphabetically by name. |
| Sort by Country   | `sortcountry` <br> e.g., `sortcountry` → sorts contacts alphabetically by country. |
| Sort by Met Date  | `sortdate` <br> e.g., `sortdate` → sorts contacts chronologically by first met date. |

#### Implementation

- Sorting operates on the **filtered list** in the `Model` so it respects current filters (e.g., after `find`, `archivelist`).
- The `ObservableList` is updated after sorting, triggering automatic UI refresh.
- Sorting is **stable**: contacts with equal keys maintain their relative order.

#### Manual Test Cases

| Test | Command | Expected Outcome |
|------|---------|-----------------|
| Sort by name | `sortname` | Contacts listed alphabetically by name. |
| Sort by country | `sortcountry` | Contacts listed alphabetically by country. |
| Sort by date met | `sortdate` | Contacts listed from earliest to latest first met date. |
| Sort after find | `find Singapore` then `sortname` | Only filtered contacts are sorted; others hidden. |


--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, configuration, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [Configuration guide](Configuration.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**:

* Exchange students on short-term programs abroad are adventurous and eager to maximise their limited time. 
* They simultaneously juggle academics, cultural exploration, and networking.
* They face challenges remembering people, maintaining connections, remembering cultural details and managing scattered communication across different apps. 
* Their goals are forging lasting friendships and global networks.


**Value proposition**: manage contacts faster than a typical mouse/GUI driven app

Our app helps exchange students build and sustain meaningful global connections while avoiding cultural missteps. 
Centralising contacts and notes makes it easier to remember people and cultural nuances. 
Unlike scattered chat apps, it’s tailored for short but intense exchange journeys, ensuring friendships and networks last beyond the program.


### User stories

*{More to be added}*

|    | Profile<br>As a…                                     | Feature<br>I can…                                                                       | Benefit<br>so that I can…                                                                                | Priority |
| -- | ---------------------------------------------------- | --------------------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------- | -------- |
| 1  | user                                                 | add a contact                                                                           | track the people I've met                                                                                | High     |
| 2  | user                                                 | delete a contact                                                                        | keep my network and knowledge tidy and relevant.                                                         | High     |
| 3  | user                                                 | view all my contacts                                                                    | easily recall who I met.                                                                                 | High     |
| 4  | user                                                 | add cultural notes                                                                      | recall important details about them.                                                                     | High     |
| 5  | sociable user who meets people from different places | tag contacts with “where we met” labels (e.g. “business course”, “xx networking event”) | remember the context in which I met them.                                                                | High     |
| 6  | user                                                 | edit a contact                                                                          | fix any errors I made when adding a contact.                                                             | High     |
| 7  | forgetful user                                       | search my contacts by name                                                              | find the right person even if I forget their exact name.                                                 | High     |
| 8  | first-time exchange student user                     | get help with the commands                                                              | know how to use the app.                                                                                 | Medium   |
| 9  | busy exchange student                                | centralise and track all my trip-related contacts and cultural notes in the app         | save time switching between scattered apps.                                                              | Medium   |
| 10 | sociable user who meets many people                  | tag contacts with role labels<br>(e.g. “classmate,” “local buddy,” “travel buddy”),     | organise people by roles.                                                                                | Medium   |
| 11 | forgetful user                                       | filter contacts by the country I met them in                                            | find people when I don’t remember anything except where we met.                                          | Medium   |
| 12 | user                                                 | filter contacts by tag                                                                  | find people I am looking for based on the role required.                                                 | Medium   |
| 13 | user who cannot remember everyone’s time zones       | have every contact’s local time displayed                                               | avoid contacting them at odd hours.                                                                      | Medium   |
| 14 | user who cannot remember everyone’s preferences      | have every contact’s preferred communication channel listed                             | contact them on the platform that they are most comfortable with.                                        | Medium   |
| 15 | first-time exchange student user                     | see sample contacts and notes when I open the app                                       | immediately explore how to organise people and cultural details without having to add my own data first. | Low      |
| 16 | user                                                 | view a timeline of interactions and trips                                               | reflect on my exchange journey after it ends.                                                            | Low      |
| 17 | user                                                 | import contacts from my phone or social apps                                            | quickly populate my network without manually typing everything.                                          | Low      |
| 18 | user                                                 | set reminders to follow up with friends                                                 | keep connections alive beyond a single meeting.                                                          | Low      |
| 19 | organised user                                       | archive contacts                                                                        | keep my network relevant.                                                                                | Low      |
| 20 | auditory learner                                     | record the pronunciation of contacts’ names                                             | remember the pronunciation of names that are foreign to me.                                              | Low      |
| 21 | visual user                                          | create map visuals to visualise networks                                                | so that I can see clusters of connections easily.                                                        | Low      |
### Use cases

(For all use cases below, the **System** is the `Worldly` and the **Actor** is the `user`, unless specified otherwise)

**Use case: UC01 Add a Contact**

Actor: User

**MSS**

1.  User chooses to add someone to their contacts
2. User types the command to add the contact
3. Wordly validates each field: name, phone, email, and country
4. If all fields are valid, the system adds the new contact to the list
5. The new contact appears in the contact list view

    Use case ends.

**Extensions**
3a. If a field is invalid → Wordly shows an error message
etc. “relevant error message/fields are name, number, email & country”

b.  If contact already exists → Wordly shows another error message 
etc. “A contact with this email/phone already exists.”

<br>

**Use Case: UC02 View Contact**

Actor: User

**MSS**

1. User chooses to view contact
2. User types view
3. System retrieves all contact and cultural notes
4. System displays the list of contacts in alphabetical order including names, countries, and notes.

    Use case ends.

**Extensions**
3a.  If there are no contacts → Wordly shows an error message
etc. “You have no contacts yet. Use ‘add’ to create one”

<br>

**Use Case: UC03 Delete Contact**

Actor: User

**MSS**

1. User chooses to delete someone from their contacts
2. User types the command to delete the contact
3. System validates the name
4. If the name is valid, the system deletes the contact from the list
5. The contact no longer appears in the list

**Extension**
3a. If the name is invalid → system shows an error message
etc. "Invalid name. Only letters, spaces, hyphens, and apostrophes are allowed, max 50 characters."

<br>

### Non-Functional Requirements

1.  Should work on any _mainstream OS_ as long as it has Java `17` or above installed.
2.  Should be able to hold up to 1000 persons without a noticeable sluggishness in performance for typical usage.
3.  A user with above average typing speed for regular English text (i.e. not code, not system admin commands) should be able to accomplish most of the tasks faster using commands than using the mouse.
4. The system should respond within two seconds.
5. The contact name or other information should not contain any words deemed offensive.
6. The user interface should be intuitive enough for users who are not IT-savvy.
7. The system is offered as a free online service.

*{More to be added}*

### Glossary

* **Mainstream OS**: Windows, Linux, Unix, MacOS
* **Private contact detail**: A contact detail that is not meant to be shared with others
* **Cultural note**: A short user-generated entry recording cultural tips, etiquette reminders, or unique local insights (e.g., greetings, taboos)
* **First Meet Circumstances**: A note describing how/where the user first met the contact (e.g., “Met at orientation event,” “Group project partner”), to strengthen recall. 
* **Alias**: A shortcut keyword (e.g., typing t for “todo,” e for “event”) to speed up data entry.
* **Organisation**: The institution (e.g., company, university) a contact is affiliated with.
* **Event**: A recorded interaction or planned meeting with a contact.

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<div markdown="span" class="alert alert-info">:information_source: **Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.

</div>

### Launch and shutdown

1. Initial launch

   1. Download the jar file and copy into an empty folder

   1. Double-click the jar file Expected: Shows the GUI with a set of sample contacts. The window size may not be optimum.

1. Saving window preferences

   1. Resize the window to an optimum size. Move the window to a different location. Close the window.

   1. Re-launch the app by double-clicking the jar file.<br>
       Expected: The most recent window size and location is retained.

1. _{ more test cases …​ }_

### Deleting a person

1. Deleting a person while all persons are being shown

   1. Prerequisites: List all persons using the `list` command. Multiple persons in the list.

   1. Test case: `delete 1`<br>
      Expected: First contact is deleted from the list. Details of the deleted contact shown in the status message. Timestamp in the status bar is updated.

   1. Test case: `delete 0`<br>
      Expected: No person is deleted. Error details shown in the status message. Status bar remains the same.

   1. Other incorrect delete commands to try: `delete`, `delete x`, `...` (where x is larger than the list size)<br>
      Expected: Similar to previous.

1. _{ more test cases …​ }_

### Saving data

1. Dealing with missing/corrupted data files

   1. _{explain how to simulate a missing/corrupted file, and the expected behavior}_

1. _{ more test cases …​ }_
