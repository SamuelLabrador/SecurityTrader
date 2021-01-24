package models.rest

case class GameState(turn: Int,
                     phase: Int,
                     board: Board,
                     ledgers: Seq[Ledger])
