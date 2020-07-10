package com.example.dynamite

import com.softwire.dynamite.bot.Bot
import com.softwire.dynamite.game.Gamestate
import com.softwire.dynamite.game.Move
import java.util.Collections

class MyBot : Bot {
    private fun getRandomMove(): Move {
        val moves = listOf(Move.R, Move.P, Move.S, Move.D, Move.W)
        return moves.shuffled().first()
    }

    private fun getRandomNonWMove(): Move {
        val moves = listOf(Move.R, Move.P, Move.S, Move.D)
        return moves.shuffled().first()
    }

    private fun getRandomNonDMove(): Move {
        val moves = listOf(Move.R, Move.P, Move.S, Move.W)
        return moves.shuffled().first()
    }

    private fun getRandomRPSMove(): Move {
        val moves = listOf(Move.R, Move.P, Move.S)
        return moves.shuffled().first()
    }
    private fun getSingleResult(p1: Move, p2: Move): String {
        return if (p1 == p2) {
            "Draw"
        } else if (p1 == Move.D && p2 != Move.W) {
            "Won"
        } else if (p1 == Move.W && p2 == Move.D) {
            "Won"
        } else if (p1 != Move.W && p2 == Move.D) {
            "Lost"
        } else if (p1 == Move.R && p2 != Move.P) {
            "Won"
        } else if (p1 == Move.P && p2 != Move.S) {
            "Won"
        } else if (p1 == Move.S && p2 != Move.R) {
            "Won"
        } else {
            "Lost"
        }
    }

    private fun getPrevResults(gamestate: Gamestate): List<String> {
        val rounds = gamestate.rounds
        var prevResults = listOf<String>()
        for (round in rounds) {
            val p1 = round.p1
            val p2 = round.p2
            prevResults = prevResults + getSingleResult(p1, p2)
        }
        return prevResults
    }

    private fun getP1Moves(gamestate: Gamestate): List<Move> {
        val rounds = gamestate.rounds
        var p1Moves = listOf<Move>()
        for (round in rounds) {
            p1Moves = p1Moves + round.p1
        }
        return p1Moves
    }

    private fun getP2Moves(gamestate: Gamestate): List<Move> {
        val rounds = gamestate.rounds
        var p2Moves = listOf<Move>()
        for (round in rounds) {
            p2Moves = p2Moves + round.p2
        }
        return p2Moves
    }

    private fun repetitiveBot(prevP2Moves: List<Move>): Boolean {
        return if (prevP2Moves.size > 20) {
            val lastThree = prevP2Moves.takeLast(3)
            lastThree[0] == lastThree[1] && lastThree[0] == lastThree[2]
        } else false
    }

    private fun randomRPSBot(prevP2Moves: List<Move>): Boolean {
        return Move.D !in prevP2Moves && prevP2Moves.size > 50
    }


    private fun dynOnDraw(
        prevP2Moves: List<Move>,
        prevResults: List<String>,
        dynLeft2: Int,
        prevRes: String
    ): Boolean {
        var dynDraws = 0
        var draws = 0
        for (i in 1 until prevP2Moves.size) {
            if (prevResults[i - 1] == "Draw") draws += 1
        }
        if (draws < 10) {
            return false
        }
        if (dynLeft2 <= 0) {
            return false
        }
        for (i in 1 until prevP2Moves.size) {
            if (prevP2Moves[i] == Move.D && prevResults[i - 1] == "Draw") dynDraws += 1
        }
        return draws - dynDraws == 0
    }

    private fun beatTheirPreviousMoveBot(prevP1Moves: List<Move>, prevP2Moves: List<Move>): Boolean {
        if (prevP2Moves.size < 20) {
            return false
        }
        var count = 0
        for (i in 1 until prevP1Moves.size) {
            if (getSingleResult(prevP2Moves[i], prevP1Moves[i-1]) == "Won") count +=1
        }
        return count > prevP1Moves.size - 10
    }

    private fun getBeatPreviousMoveBot(prevP1Moves: List<Move>, dynLeft1: Int): Move {
        val last = prevP1Moves.last()
        if (dynLeft1 > 0 && last != Move.D) return Move.D
        return if (last == Move.R) {
            Move.S
        } else if (last == Move.P) {
            Move.R
        } else if (last == Move.S) {
            Move.P
        } else if (last == Move.D) {
            getRandomRPSMove()
        } else {
            Move.P
        }
    }

    private fun dynDependentMove(dynLeft1: Int, dynLeft2: Int, prevResults: List<String>): Move {
        return if (dynLeft1 > 0 && dynLeft2 > 0) {
            if (Collections.frequency(prevResults, "Won") > prevResults.size * 0.7) {
                getRandomNonDMove()
            } else {
                getRandomMove()
            }
        } else if (dynLeft1 > 0 && dynLeft2 <= 0) {
            if (Collections.frequency(prevResults, "Won") > prevResults.size * 0.7) {
                getRandomRPSMove()
            } else {
                getRandomNonWMove()
            }
        } else if (dynLeft1 <= 0 && dynLeft2 > 0) {
            getRandomNonDMove()
        } else {
            getRandomRPSMove()
        }
    }


    override fun makeMove(gamestate: Gamestate): Move {
        // Are you debugging?
        // Put a breakpoint in this method to see when we make a move

        //initialise variables, first move scenario
        val rounds = gamestate.rounds
        val r = Move.R
        val p = Move.P
        val s = Move.S
        val d = Move.D
        val w = Move.W
        if (rounds.size == 0) {
            return d
        } else {
            val prevResults = getPrevResults(gamestate)
            val prevP1Moves = getP1Moves(gamestate)
            val prevP2Moves = getP2Moves(gamestate)
            val prevRes = prevResults.last()
            //get dyn left
            var dynLeft1 = 100
            var dynLeft2 = 100
            for (round in rounds) {
                val p1 = round.p1
                val p2 = round.p2
                if (p1 == d) {
                    dynLeft1 -= 1
                }
                if (p2 == d) {
                    dynLeft2 -= 1
                }
            }

            if (repetitiveBot(prevP2Moves)) {
                 if (prevP2Moves.last() == r) {
                     return p
                 } else if (prevP2Moves.last() == p) {
                     return s
                 } else if (prevP2Moves.last() == s) {
                     return r
                 } else if (prevP2Moves.last() == d) {
                     return w
                 } else {
                     return getRandomRPSMove()
                 }
            } else if (dynOnDraw(prevP2Moves, prevResults, dynLeft2, prevRes)) {
                if (prevRes == "Draw") {
                    return w
                } else if (dynLeft1 > 0) {
                    return d
                } else {
                    return getRandomRPSMove()
                }
            } else if (randomRPSBot(prevP2Moves)) {
                if (dynLeft1 > 0) {
                    return d
                } else {
                    return getRandomRPSMove()
                }
            } else if (beatTheirPreviousMoveBot(prevP1Moves, prevP2Moves)) {
                 return getBeatPreviousMoveBot(prevP1Moves, dynLeft1)
            } else {
                 return dynDependentMove(dynLeft1, dynLeft2, prevResults)
            }
        }
    }

    init {
        // Are you debugging?
        // Put a breakpoint on the line below to see when we start a new match
        println("Started new match")
    }
}
