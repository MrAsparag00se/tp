# Em-Social Developer Guide

## Table of Contents
- [Acknowledgements](#acknowledgements)
- [Setting up, getting started](#setting-up-getting-started)
- [Design](#design)
- [Implementation](#implementation)
- [Documentation, logging, testing, configurations, dev-ops](#documentation-logging-testing-configuration-dev-ops)
- [Appendix: Requirements](#appendix-requirements)
- [Appendix: Instructions for Manual Testing](#appendix-instructions-for-manual-testing)
- [Appendix: Planned Enhancements](#appendix-planned-enhancements)

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

[//]: # (_{ list here sources of all reused/adapted ideas, code, documentation, and third-party libraries -- include links to the original source as well }_)
This project is based on the AddressBook-Level3 project created by the [SE-EDU initiative](https://se-education.org).

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the default guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

### Architecture

<puml src="diagrams/ArchitectureDiagram.puml" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

Given below is a quick overview of main components and how they interact with each other.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/AY2425S2-CS2103T-F10-2/tp/blob/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/AY2425S2-CS2103T-F10-2/tp/blob/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `delete id/H000001`.

<puml src="diagrams/ArchitectureSequenceDiagram.puml" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* implements its functionality using a concrete `{Component Name}Manager` class (which follows the corresponding API `interface` mentioned in the previous point.

For example, the `Logic` component defines its API in the `Logic.java` interface and implements its functionality using the `LogicManager.java` class which follows the `Logic` interface. Other components interact with a given component through its interface rather than the concrete class (reason: to prevent outside component's being coupled to the implementation of a component), as illustrated in the (partial) class diagram below.

<puml src="diagrams/ComponentManagers.puml" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/AY2425S2-CS2103T-F10-2/tp/blob/master/src/main/java/seedu/address/ui/Ui.java)

<puml src="diagrams/UiClassDiagram.puml" alt="Structure of the UI Component"/>

The UI consists of a `MainWindow` that is made up of parts e.g.`CommandBox`, `ResultDisplay`, `SessionListPanel`, `StatusBarFooter` etc. All these, including the `MainWindow`, inherit from the abstract `UiPart` class which captures the commonalities between classes that represent parts of the visible GUI.

The `UI` component uses the JavaFx UI framework. The layout of these UI parts are defined in matching `.fxml` files that are in the `src/main/resources/view` folder. For example, the layout of the [`MainWindow`](https://github.com/AY2425S2-CS2103T-F10-2/tp/blob/master/src/main/java/seedu/address/ui/MainWindow.java) is specified in [`MainWindow.fxml`](https://github.com/AY2425S2-CS2103T-F10-2/tp/blob/master/src/main/resources/view/MainWindow.fxml)

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component, as it displays `Household` object residing in the `Model`.

### Logic component

**API** : [`Logic.java`](https://github.com/AY2425S2-CS2103T-F10-2/tp/blob/master/src/main/java/seedu/address/logic/Logic.java)

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete id/H000001")` API call as an example.

<puml src="diagrams/DeleteSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `delete id/H000001` Command" />

**Note:** The lifeline for `DeleteHouseholdCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline continues till the end of diagram.


How the `Logic` component works:

1. When `Logic` is called upon to execute a command, it is passed to an `HouseholdBookParser` object which in turn creates a parser that matches the command (e.g., `DeleteHouseholdCommandParser`) and uses it to parse the command.
2. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteHouseholdCommand`) which is executed by the `LogicManager`.
3. The command can communicate with the `Model` when it is executed (e.g. to delete a household).<br>
   Note that although this is shown as a single step in the diagram above (for simplicity), in the code it can take several interactions (between the command object and the `Model`) to achieve.
4. The result of the command execution is encapsulated as a `CommandResult` object which is returned back from `Logic`.

### Model component
**API** : [`Model.java`](https://github.com/AY2425S2-CS2103T-F10-2/tp/blob/master/src/main/java/seedu/address/model/Model.java)

<puml src="diagrams/ModelClassDiagram.puml" width="800" />


The `Model` component,

* stores the Em-Social data i.e., all `Household` objects and `Session` objects
* stores the currently 'selected' `Household` objects (e.g., results of a search query) as a separate _filtered_ list which is exposed to outsiders as an unmodifiable `ObservableList<Household>` that can be 'observed' e.g. the UI can be bound to this list so that the UI automatically updates when the data in the list change.
* stores a `UserPref` object that represents the user’s preferences. This is exposed to the outside as a `ReadOnlyUserPref` objects.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components).

### Storage component

**API** : [`Storage.java`](https://github.com/AY2425S2-CS2103T-F10-2/tp/blob/master/src/main/java/seedu/address/storage/Storage.java)

<puml src="diagrams/StorageClassDiagram.puml" width="550" />

The `Storage` component,
* can save both Em-Social data and user preference data in JSON format, and read them back into corresponding objects.
* inherits from both `HouseholdBookStorage` and `UserPrefStorage`, which means it can be treated as either one (if only the functionality of only one is needed).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### \[Proposed\] Undo/redo feature

#### Proposed Implementation

The proposed undo/redo mechanism is facilitated by `VersionedHouseholdBook`. It extends `HouseholdBook` with an undo/redo history, stored internally as an `HouseholdBookStateList` and `currentStatePointer`. Additionally, it implements the following operations:

* `VersionedHouseholdBook#commit()`- Saves the current household book state in its history.
* `VersionedHouseholdBook#undo()`- Restores the previous household book state from its history.
* `VersionedHouseholdBook#redo()`- Restores a previously household book state from its history.

These operations are exposed in the `Model` interface as `Model#commitHouseholdBook()`, `Model#undoHouseholdBook()` and `Model#redoHouseholdBook()` respectively.

Given below is an example usage scenario and how the undo/redo mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `VersionedHouseholdBook` will be initialized with the initial household book state, and the `currentStatePointer` pointing to that single household book state.

<puml src="diagrams/UndoRedoState0.puml" alt="UndoRedoState0" />

Step 2. The user executes `delete id/H000005` command to delete the 5th household in the household book. The `delete` command calls `Model#commitHouseholdBook()`, causing the modified state of the household book after the `delete id/H000005` command executes to be saved in the `HouseholdBookStateList`, and the `currentStatePointer` is shifted to the newly inserted household book state.

<puml src="diagrams/UndoRedoState1.puml" alt="UndoRedoState1" />

Step 3. The user executes `add n/David Family a/Blk 30 Geylang Street 29, #06-40 p/97751978` to add a new household. The `add` command also calls `Model#commitHouseholdBook()`, causing another modified household book state to be saved into the `HouseholdBookStateList`.

<puml src="diagrams/UndoRedoState2.puml" alt="UndoRedoState2" />


**Note:** If a command fails its execution, it will not call `Model#commitHouseholdBook()`, so the household book state will not be saved into the `HouseholdBookStateList`.


Step 4. The user now decides that adding the person was a mistake, and decides to undo that action by executing the `undo` command. The `undo` command will call `Model#undoHouseholdBook()`, which will shift the `currentStatePointer` once to the left, pointing it to the previous household book state, and restores the household book to that state.

<puml src="diagrams/UndoRedoState3.puml" alt="UndoRedoState3" />


**Note:** If the `currentStatePointer` is at index 0, pointing to the initial HouseholdBook state, then there are no previous HouseholdBook states to restore. The `undo` command uses `Model#canUndoHouseholdBook()` to check if this is the case. If so, it will return an error to the user rather
than attempting to perform the undo.


The following sequence diagram shows how an undo operation goes through the `Logic` component:

<puml src="diagrams/UndoSequenceDiagram-Logic.puml" alt="UndoSequenceDiagram-Logic" />

**Note:** The lifeline for `UndoCommand` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline reaches the end of diagram.


Similarly, how an undo operation goes through the `Model` component is shown below:

<puml src="diagrams/UndoSequenceDiagram-Model.puml" alt="UndoSequenceDiagram-Model" />

The `redo` command does the opposite - it calls `Model#redoHouseholdBook()`, which shifts the `currentStatePointer` once to the right, pointing to the previously undone state, and restores the household book to that state.

**Note:** If the `currentStatePointer` is at index `HouseholdBookStateList.size() - 1`, pointing to the latest household book state, then there are no undone HouseholdBook states to restore. The `redo` command uses `Model#canRedoHouseholdBook()` to check if this is the case. If so, it will return an error to the user rather than attempting to perform the redo.


Step 5. The user then decides to execute the command `list`. Commands that do not modify the household book, such as `list`, will usually not call `Model#commitHouseholdBook()`, `Model#undoHouseholdBook()` or `Model#redoHouseholdBook()`. Thus, the `HouseholdBookStateList` remains unchanged.

<puml src="diagrams/UndoRedoState4.puml" alt="UndoRedoState4" />

Step 6. The user executes `clear`, which calls `Model#commitHouseholdBook()`. Since the `currentStatePointer` is not pointing at the end of the `HouseholdBookStateList`, all household book states after the `currentStatePointer` will be purged. Reason: It no longer makes sense to redo the `add n/David Family a/Blk 30 Geylang Street 29, #06-40 p/97751978` command. This is the behavior that most modern desktop applications follow.

<puml src="diagrams/UndoRedoState5.puml" alt="UndoRedoState5" />

The following activity diagram summarizes what happens when a user executes a new command:

<puml src="diagrams/CommitActivityDiagram.puml" width="250" />

#### Design considerations:

**Aspect: How undo & redo executes:**

* **Alternative 1 (current choice):** Saves the entire household book.
    * Pros: Easy to implement.
    * Cons: May have performance issues in terms of memory usage.


* **Alternative 2:** Individual command knows how to undo/redo by
  itself.
    * Pros: Will use less memory (e.g. for `delete`, just save the household being deleted).
    * Cons: We must ensure that the implementation of each individual command are correct.


--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, configuration, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [Configuration guide](Configuration.md)
* [DevOps guide](DevOps.md)


--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product Scope

**Target User Profile**: Social service workers in Singapore

- **Efficient Management:**
  Has a need to manage a significant number of household records and engagement sessions efficiently.

- **Monitoring Household Conditions:**
  Has a need to take note of household conditions when there are areas of concern.

- **Desktop Preference:**
  Prefers desktop applications over web or mobile solutions for stability and performance.

- **CLI Enthusiast:**
  Prefers typing over mouse interactions, favoring a Command Line Interface (CLI) for speed.

- **Comfort with CLI:**
  Is reasonably comfortable using CLI applications, with minimal reliance on Graphical User Interfaces (GUI).

**Value Proposition**

**Em-Social** enables social service workers to manage household records and schedule sessions faster than traditional mouse/GUI-driven applications by leveraging an efficient CLI interface and structured data handling. This ensures that users can quickly search, update, and schedule without unnecessary navigation overhead, improving productivity and accuracy. By helping social workers to stay organized, they can focus on supporting households instead of managing logistics.

--------------------------------------------------------------------------------------------------------------------
### User stories

Priorities: High (must have) - `* * *`, Medium (good to have) - `* *`, Low (unlikely to have) - `*`

#### Create & Store Household Records [`* * *`]
**User Story:** As a social service worker, I can create a new household record (address, members, etc.) so that I can easily store and reference important household information.

Conditions of Satisfaction:
- I can fill in household address, contact details, and member names.
- The record is saved in the system and is visible in a household list.

#### Update & Edit Household Information [`* *`]
**User Story:** As a social service worker, I can update an existing household record so that I can keep the information current and accurate.

Conditions of Satisfaction:
- I can edit household details at any time.
- The updated information is saved and immediately visible in the system.

#### Add Session Notes [`* *`]
**User Story:** As a social service worker, I can add notes to a session so that I can document important details about household visits.

Conditions of Satisfaction:
- I can write session notes when editing a session.
- Notes are saved and linked to the session for future reference.

#### Search Household Records [`* *`]
**User Story:** As a social service worker, I can search for households by name or address so that I can quickly find the relevant record.

Conditions of Satisfaction:
- I can use a search bar or filter to find households by name and/or address.
- Matching results appear immediately in a results list.

#### View List of Households [`* * *`]
**User Story:** As a social service worker, I can view a list of all stored households so that I can quickly browse through records.

Conditions of Satisfaction:
- I can see all stored households in a structured list.
- Each household entry displays key information like name, address, and contact details.

#### Delete Household [`* * *`]
**User Story:** As a social service worker, I can delete a household record so that I can remove outdated or incorrect information.

Conditions of Satisfaction:
- I can delete a household from the system.
- The deleted household is removed from the household list.

#### Clear All Entries [`* *`]
**User Story:** As a social service worker, I can clear all stored entries so that I can reset the system when necessary.

Conditions of Satisfaction:
- I can remove all stored households and sessions.
- The system confirms before clearing all data.

#### Exit Program [`* * *`]
**User Story:** As a social service worker, I can exit the program so that I can close the application safely.

Conditions of Satisfaction:
- I can terminate the application using an exit command.
- The system safely closes all sessions and saves necessary data.

#### Viewing Help [`* * *`]
**User Story:** As a social service worker, I can view a help menu so that I can understand how to use the system.

Conditions of Satisfaction:
- The system provides a help command.
- The help menu explains how to use available features.

--------------------------------------------------------------------------------------------------------------------
### Use cases

(For all use cases below, the **System** is `Em-Social` and the **Actor** is the `Social Service Worker`, unless specified otherwise)

### Use Case: U1. Create Household Record

**Main Success Scenario:**
1. User adds a household and enters household name, address, and contact information.
2. System prompts for household details.
3. System validates the input.
4. System saves the household record.
5. System displays a confirmation message.

    Use case ends.

**Extensions:**

3a. Invalid input provided.
* 3a1. System displays an error message.
Use case resumes from step 1.

5a. System fails to save the household record.
* 5a1. System displays an error message indicating the failure.
* 5a2. System prompts the user to retry saving.
* 5a3. If the issue persists, the system logs the error for troubleshooting.
Use case resumes from step 3 or ends if saving is unsuccessful.

---

### Use Case: U2. Schedule Engagement Session

**Main Success Scenario:**
1. User schedules an engagement session with a specified household.
2. System performs (**Prevent Double-Booking (U4)**) to prevent scheduling conflicts.
3. If there is no conflict, system saves the session and updates the calendar.
4. System displays a confirmation message.

    Use case ends.

**Extensions:**

1a. Invalid date/time format.
* 1a1. System displays an error message.
Use case resumes from step 1.

---

### Use Case: U3. Edit Household Record

**Main Success Scenario:**
1. User specifies a household to edit and the detail to change.
2. System validates changes.
3. System saves changes and updates the record.
4. System displays a confirmation message.

    Use case ends.

**Extensions:**

2a. Invalid input provided.
* 2a1. System displays an error message.
Use case resumes from step 1.

---

### Use Case: U4. Prevent Double-Booking

**Main Success Scenario:**
1. User selects a date and time to schedule a session.
2. System checks for existing sessions at the specified time.
3. System displays a confirmation message.

    Use case ends.

**Extensions:**

2a. A session already exists at the selected time.
* 2a1. System displays a conflict warning and session is not scheduled.

    Use case ends.

---

### Use Case: U5. View Household Session History

**Main Success Scenario:**
1. User views session history for a specific household.
2. System retrieves and displays past sessions in chronological order.
3. User reviews session details.

    Use case ends.

**Extensions:**

2a. No past sessions recorded.
* 2a1. System displays a message indicating no history available.

    Use case ends.

---

### Use Case: U6. Search for Household

**Main Success Scenario:**
1. User searches a keyword in the search bar.
2. System retrieves matching household records.
3. System displays a list of results.
4. User selects a household for further action.

    Use case ends.

**Extensions:**

2a. No matching records found.
* 2a1. System displays an error message.

    Use case ends.

---

### Use Case: U7. Delete Household

**Main Success Scenario:**
1. User specifies a household to delete.
2. System checks if the household exists.
3. System prompts the user for confirmation.
4. User confirms the deletion.
5. System removes the household record and its sessions from the list.
6. System displays a confirmation message.

    Use case ends.

**Extensions:**

2a. Household ID does not exist.
* 2a1. System displays an error message.
Use case ends.

3a. User cancels deletion.
* 3a1. System aborts the deletion process.

    Use case ends.

---

### Use Case: U8. Edit Session (with Optional Notes)

**Main Success Scenario:**
1. User specifies the date, time or note to update for a household's session.
2. System checks if the provided session index exists.
3. System validates the new date, time, or note.
4. If valid, system updates the session details.
5. System displays a confirmation message

    Use case ends.

**Extensions:**

2a. Invalid session index.
* 2a1. System displays an error message.

    Use case ends.

3a. Invalid date/time format.
* 3a1. System displays an error message.
* 3a2. User corrects input and retries.
Use case resumes from step 1.

3b. A session already exists at the new date/time (**Prevent Double-Booking (U4)**).
Use case resumes from step 1 or ends if canceled.

4a. System fails to save the changes.
* 4a1. System displays an error message.
* 4a2. User retries or exits.
Use case resumes from step 1 or ends if unsuccessful.

---

### Use Case: U9. Clear All Entries

**Main Success Scenario:**
1. User clears all entries.
2. System prompts for confirmation.
3. User confirms the action.
4. System removes all stored data (households, sessions, and notes).
5. System displays a confirmation message.

    Use case ends.

**Extensions:**

2a. User cancels the operation.
* 2a1. System aborts the clearing process.

    Use case ends.

4a. System fails to clear data.
* 4a1. System displays an error message.
* 4a2. User retries or exits.
Use case resumes from step 2 or ends if the issue persists.

--------------------------------------------------------------------------------------------------------------------

### Non-Functional Requirements:

1. All commands (e.g., adding, searching, updating, deleting) must execute within 300 milliseconds under normal conditions.
2. The application must launch within 3 seconds on a standard machine (e.g., Intel i5, 8GB RAM).
3. If bulk operations (e.g., clearing all records) are performed, they must complete within 5 seconds for up to 5,000 entries.
4. The application must consume no more than 200 MB of RAM during normal operation.
5. The system must be fully operable via CLI, with intuitive and minimal keyboard commands to streamline workflows.
6. Error messages must be clear and descriptive, helping users correct mistakes efficiently.
7. The application must support high contrast mode for accessibility.
8. Household records and session data must be automatically saved to prevent loss due to unexpected shutdowns.
9. The system must be able to recover from a crash without losing the last committed state.
10. The application must run on Windows, macOS, and Linux without modification.
11. The system should require only a standard Java Runtime Environment (JRE 11 or later) to function.
12. The system must work without an internet connection and store data locally.
13. The application must be self-contained with minimal dependencies, avoiding unnecessary external libraries.
14. The system must run on machines with at least 2GB RAM, 1GHz CPU, and 200MB disk space.

---------------------------------------------------------------------------------------------------------------------

## Glossary

* **Household Record:** A digital record containing information about a household, including its address, members, and relevant notes.
* **Engagement Session:** A scheduled appointment between a social service worker and a household for follow-up, assistance, or other social services.
* **CLI (Command Line Interface):** A text-based interface that allows users to interact with the system using commands rather than a graphical interface.
* **GUI (Graphical User Interface):** A visual interface that allows users to interact with the system using graphical elements such as buttons, icons, and windows, as opposed to a text-based interface like a CLI
* **RAM (Random Access Memory):** A type of computer memory that temporarily stores data and instructions while a program is running, allowing for quick access and processing.
* **Tag:** A keyword or label assigned to a household record to categorize or identify it easily.
* **Double-Booking:** A scheduling conflict where two sessions are assigned to the same time slot, which the system should prevent.
* **Session History:** A chronological log of all past engagement sessions associated with a household.
* **Filter:** A feature that allows users to refine search results based on specific criteria like name, address, or tags.
* **High Contrast Mode:** A visual accessibility feature that increases the contrast between text and background to improve readability for users with visual impairments.
* **Crash:** A case of an unexpected shutdown.
* **Automatic save:** A feature that saves user data with the last command entered to prevent loss in case of system failure.
* **Workflows:** A defined sequence of steps or processes that users follow to complete a task efficiently within the system, such as adding a household record or scheduling an engagement session.
* **Bulk Operation:** A command or function that processes multiple records at once, such as clearing all entries.
* **Session Notification:** An alert or reminder about an upcoming engagement session, which can be displayed in-app or sent via email.
* **Session Data:** Information related to a user's current interaction with the system, including active records, scheduled sessions, and temporary changes before they are saved.
* **Structured List:** A formatted way of displaying household records, showing key details like name, address, and contact information.
* **Last Committed State:** The most recent version of stored data that was successfully saved, ensuring that no progress is lost in case of an unexpected system crash.
* **Dependencies:** External software libraries or components required for the application to function properly, such as Java Runtime Environment (JRE) or specific third-party tools.

--------------------------------------------------------------------------------------------------------------------

## Appendix: Instructions for Manual Testing

### Download Em Social:
Download the latest `em-social.jar` from the [Em Social Releases Page](https://github.com/AY2425S2-CS2103T-F10-2/tp/releases).

### Setup:
Save the downloaded `em-social.jar` file in your preferred home folder.

### Launching and Shutting Down the Application

#### Initial Launch

1. **Open a Terminal:**

    - **Windows:** Press `Win + R`, type `cmd`, and press `Enter`.
    - **macOS/Linux:** Open your Terminal application.

2. **Navigate to the Application Folder:**

   Change to the folder containing `em-social.jar`:
   ```bash
   cd [YOUR_FOLDER_LOCATION]
Replace [YOUR_FOLDER_LOCATION] with the actual path where the jar file is located.

3. **Run the Application**
   Launch Em Social by executing:
    ```bash
    java -jar em-social.jar
**Expected:** A graphical user interface (GUI) should appear with sample data loaded.

**Shutdown**

- Type `exit` in the command box to close the application.

- OR click the `File` button and select `Exit` from the dropdown menu.

**Saving window preferences**

1. Resize the window to an optimum size.
2. Resize ratio of household panel and session panel.

#### Deciding what to do - Help

1. Provides a quick summary of the available commands and a URL to the User Guide for more detailed instructions

   a. Prerequisites: None.

   b. Test case: `help`<br>
   Expected: A pop up appears with a quick help guide.

   c. Test case: `HELP`<br>
   Expected: Unreognized command as command words are case sensitive

#### Adding your first household - Add

1. Adds a household's details

   a. Prerequisites: None.

   b. Test case: `add n/Tan Family a/Tan Road p/92813822`<br>
   Expected: A household with the above details is added. A success message is displayed to the user.

   c. Test case: `add n/Benson & Yeung a/Blank  Road p/8192312`<br>
   Expected: Error for an invalid name will be shown to the user (invalid symbol in the name field).

#### Edit a household - Edit

1. Edits a household's details

   a. Prerequisites: The household for which you want to edit details must exist.

   b. Test case: `edit id/H000001 n/Tan Family t/FA`<br>
   Expected: A household specified by the id will have its name changed to `Tan Family`, with a tag of `FA`. A success message is displayed to the user.

   c. Test case: `edit id/H000001 p/2192312`<br>
   Expected: Error for an invalid phone number will be shown to the user (Application is for the Singapore context so only numbers with 6,8,9 are permitted).

#### Remove a household - Delete

1. Deleting a household and all its sessions

   a. Prerequisites: A household specified by the id must exist.

   b. Test case: `delete id/H000001`<br>
   Expected: The household with Household ID H000001 is deleted. Success message appears.

   c. Test case: `delete-s id/H000000`<br>
   Expected: No household is deleted. An error message for the invalid ID is shown.

#### Finding a household - Find

1. Find a household based on keywords

   a. Prerequisites: None.

   b. Test case: `find Tan`<br>
   Expected: If `Tan` appears in a household's name, tag or address, the matching result is shown to the user, otherwise no result is shown.

#### Adding your first visit - Add a session

1. Add a session to a specific household

   a. Prerequisites: The household for which you want to add a session must exist.

   b. Test case: `add-s id/H000001-1 d/2026-06-06 tm/12:00`<br>
   Expected: A session at the scheduled date and time appears in the sessions box.

   c. Test case: `add-s id/H000001-1 d/1999-06-06 tm/12:00`<br>
   Expected: A session at the scheduled date and time is not added due to an invalid date (date is in the past).

#### Rescheduling your visit - Edit a session

1. Edits a session to a specific household

   a. Prerequisites: The household for which you want to edit a session must exist.

   b. Test case: `edit-s id/H000001-1 d/2026-06-06 tm/12:00`<br>
   Expected: The session specified in the id will be rescheduled to the specified date and time. Success message appears.


#### Removing a session from your schedule - Delete a session

1. Deleting a session while all household sessions are being shown

   a. Prerequisites: List all sessions for a household using the `view-s` command. It will show all the sessions of the specified household.

   b. Test case: `delete-s id/H000001-1`<br>
   Expected: The first session from the household with Household ID H000001 is deleted. Details of the deleted contact shown in the output box.

   c. Test case: `delete-s id/H000001-0`<br>
   Expected: No session is deleted. An error message for the invalid ID is shown.

### Simulating Missing/Corrupted Data Files in Em-Social

To simulate missing or corrupted data files and observe how Em-Social handles these situations, follow the steps below.
#### 1. Simulating Missing Files

To simulate the case where a data file is missing, simply delete the file before starting the application.

##### Example:
- **Missing `addressbook.log.0`, `config.json`, or `preferences.json` file:**
    - Delete the file from the directory.
    - **Expected Behavior:**
        - The application should handle the missing file gracefully, new logs will be created.

#### 2. Simulating Corrupted Files

To simulate file corruption, open any of the data files (e.g., `householdbook.json`, `addressbook.log.0`, `config.json`, or `preferences.json`) and modify their contents (e.g., change some characters, truncate the file, etc.).

##### Example:
- **Corrupted `addressbook.log.0` file:**
    - Open the file `addressbook.log.0` and introduce some random characters or truncate the file.
    - **Expected Behavior:**
        - The application will not be able to detect a corrupted log file but new logs will be appended without error.

- **Corrupted `config.json`, `preferences.json` file:**
    - Open the file and change some of its values to invalid data.
    - **Expected Behavior:**
        - The application will reset to the preferences when the user next starts up Em-Social.

- **Corrupted `householdbook.json` file:**
    - Open the file and change some of its values to invalid data.
    - **Expected Behavior:**
        - The application will reset to a fresh state when the user next starts up Em-Social.

--------------------------------------------------------------------------------------------------------------------

## Appendix: Planned Enhancements

Team size: 4

1. Make the error messages for commands more specific as to wherein the problem lies. Some of the current error messages throw the required parameters and example command formats but is not specific enough.
2. Currently, the selected household above the sessions window does not automatically update as soon as the household's name is updated. We intend to make the selected household automatically update to reflect these real-time changes to reduce possible user-confusion.
3. Currently, the selected household above the sessions window is unable to display the full household name if it is too long. While the household panel displays the full name, we intend to fix this flaw in future for additional clarity for the user.
4. Corrupted `addressbook.log.0` file will detect the corrupted logs and **inform** the user about the corrupted logs.
5. Currently, there is no option to remove **all** tags from a household. We intend to modify the tagging parameter such that "t/ " will clear all existing tags from a household.

