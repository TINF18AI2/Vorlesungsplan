## Before you start
create a file ```key.properties``` in the root of the project directory with content

```
storePassword=pw
keyPassword=kpw
keyAlias=alias
storeFile=sf
```

## Branches
| Branch        | Funktion                                                                                                                             |
| ------------- | ------------------------------------------------------------------------------------------------------------------------------------ |
| `stable`      | Current published version of app                                                                                                     |
| `master`      | Current development version of app                                                                                                   |
| `feature/xyz` | Branch for the creation of the feature xyz (create pull request to master when finished)                                             |
| `dev/xyz`     | Branch for development tasks that do not change the features of the app. Examples here would be documentation updates or refactoring |

## Directories
- The XML layout files are in the `res` dir like in a default Android project
- The main App Code is in `/app/src/main/java/com/tinf18ai2/vorlesungsplan`, so all dirs covered here are subdirs of that dir
  
| Name               | Function                                                             |
| ------------------ | -------------------------------------------------------------------- |
| `backend_services` | Contains all backend services like Network calls and Website parsing |
| `models`           | Contains data models like i.e. Vorlesung                             |
| `ui`               | Contains all UI Code for the app like the Activities, Adapters,...   |