## Doing
* provide constraints and validation
  * the validation returns failures, but there is no way for users to find out the constraints
  * unit tests
  * constraints have to use 1 field - multi constraint structure; so rework is needed

## Todo
* implement saving

## Done
* implement data cleaning - likely to exclude, because data cleaning modifies the value, so it's good only for the UI. Server side often just reject invalid values
* implement reading objects
