package models.rest

/** Ledger is used to represent a players state
 *
 * @param player The player this ledger belongs to
 * @param bonds The amount of bonds the player owns
 * @param insurances The amount of insurances the player owns
 * @param banks The amount to banks the player owns
 * @param capital The amount of capital the player owns
 */
case class Ledger(player: Player,
                  bonds: Int,
                  insurances: Int,
                  banks: Int,
                  capital: Int)
