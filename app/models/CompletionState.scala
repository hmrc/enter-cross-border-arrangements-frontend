package models

sealed trait CompletionState

case object CannotStart extends CompletionState
case object NotStarted extends CompletionState
case object InProgress extends CompletionState
case object Complete extends CompletionState
