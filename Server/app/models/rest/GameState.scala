package models.rest

case class GameState(turn: Int,
                     phase: Int,
                     board: Board,
                     players: Seq[Player],
                     ledgers: Seq[Ledger])
