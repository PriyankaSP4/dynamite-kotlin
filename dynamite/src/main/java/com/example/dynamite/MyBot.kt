package com.example.dynamite

import com.softwire.dynamite.bot.Bot
import com.softwire.dynamite.game.Gamestate
import com.softwire.dynamite.game.Move
import java.util.Collections

class MyBot : Bot {
    private fun getRandomMove(): Move {
        val nums = listOf(Move.R, Move.P, Move.S, Move.D, Move.W)
        return nums.shuffled().first()
    }

    private fun getRandomNonWMove(): Move {
        val nums = listOf(Move.R, Move.P, Move.S, Move.D)
        return nums.shuffled().first()
    }

    private fun getRandomNonDMove(): Move {
        val nums = listOf(Move.R, Move.P, Move.S, Move.W)
        return nums.shuffled().first()
    }

    private fun getRandomRPSMove(): Move {
        val nums = listOf(Move.R, Move.P, Move.S)
        return nums.shuffled().first()
    }

    private fun getPrevResults(gamestate: Gamestate): List<String> {
        val rounds = gamestate.rounds
        var prevResults = listOf<String>()
        for (round in rounds) {
            val p1 = round.p1
            val p2 = round.p2
            if (p1 == Move.D && p2 != Move.W) {
                prevResults = prevResults + "Won"
            } else if (p1 == Move.W && p2 == Move.D) {
                prevResults = prevResults + "Won"
            } else if (p1 == Move.R && p2 != Move.P) {
                prevResults = prevResults + "Won"
            } else if (p1 == Move.R && p2 != Move.D) {
                prevResults = prevResults + "Won"
            } else if (p1 == Move.P && p2 != Move.R) {
                prevResults = prevResults + "Won"
            } else if (p1 == Move.P && p2 != Move.D) {
                prevResults = prevResults + "Won"
            } else if (p1 == Move.S && p2 != Move.R) {
                prevResults = prevResults + "Won"
            } else if (p1 == Move.S && p2 != Move.D) {
                prevResults = prevResults + "Won"
            } else if (p1 == p2) {
                prevResults = prevResults + "Draw"
            } else {
                prevResults = prevResults + "Lost"
            }
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

    private fun rockBot(prevP2Moves: List<Move>): Boolean {
        return if (prevP2Moves.size > 50) {
            Collections.frequency(prevP2Moves, Move.R) == prevP2Moves.size
        } else false
    }

    private fun paperBot(prevP2Moves: List<Move>): Boolean {
        return if (prevP2Moves.size > 50) {
            Collections.frequency(prevP2Moves, Move.P) == prevP2Moves.size
        } else false
    }

    private fun scissorsBot(prevP2Moves: List<Move>): Boolean {
        return if (prevP2Moves.size > 50) {
            Collections.frequency(prevP2Moves, Move.S) == prevP2Moves.size
        } else false
    }

    private fun randomRPSBot(prevP2Moves: List<Move>): Boolean {
        return Move.D !in prevP2Moves && prevP2Moves.size > 50
    }

    private fun dynamiteFirst(prevP2Moves: List<Move>): Boolean {
        return if (prevP2Moves.size > 10) {
            Collections.frequency(prevP2Moves, Move.D) == prevP2Moves.size
        } else false
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

    private fun beatTheirPreviousMoveBot(
        prevP1Moves: List<Move>,
        prevP2Moves: List<Move>,
        prevResults: List<String>,
        dynLeft2: Int
    ): Boolean {
        if (prevP2Moves.size < 10) {
            return false
        }
        if (dynLeft2 <= 0) {
            return false
        }
        var count = 0
        for (i in 1 until prevResults.size) {
            if (prevP2Moves[i] == Move.D && prevP1Moves[i - 1] != Move.W) {
                count += 1
            } else if (prevP2Moves[i] == Move.R && prevP1Moves[i - 1] == Move.S) {
                count += 1
            } else if (prevP2Moves[i] == Move.P && prevP1Moves[i - 1] == Move.R) {
                count += 1
            } else if (prevP2Moves[i] == Move.S && prevP1Moves[i - 1] == Move.P) {
                count += 1
            } else if (prevP2Moves[i] == Move.W && prevP1Moves[i - 1] == Move.D) {
                count += 1
            }
        }
        return count == prevResults.size - 1
    }

    private fun getBeatPreviousMoveBot(prevP1Moves: List<Move>, dynLeft1: Int): Move {
        val last = prevP1Moves.last()
        if (last != Move.D && dynLeft1 > 0) {
            return Move.D
        }
        return if (last == Move.R) {
            Move.S
        } else if (last == Move.P) {
            Move.R
        } else if (last == Move.S) {
            Move.P
        } else if (last == Move.D) {
            Move.R
        } else {
            Move.P
        }
    }

    private fun dynDependentMove(dynLeft1: Int, dynLeft2: Int): Move {
        return if (dynLeft1 > 0 && dynLeft2 > 0) {
            getRandomMove()
        } else if (dynLeft1 > 0 && dynLeft2 <= 0) {
            getRandomNonWMove()
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
        var play: Move
        if (rounds.size == 0) {
            play = d
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

             if (rockBot(prevP2Moves)) {
                play = p
            } else if (paperBot(prevP2Moves)) {
                 play = s
            } else if (scissorsBot(prevP2Moves)) {
                 play = r
            } else if (dynamiteFirst(prevP2Moves)) {
                 play = w
            } else if (dynOnDraw(prevP2Moves, prevResults, dynLeft2, prevRes)) {
                if (prevRes == "Draw") {
                    play = w
                } else if (dynLeft1 > 0) {
                    play = d
                } else {
                    play = getRandomRPSMove()
                }
            } else if (randomRPSBot(prevP2Moves)) {
                if (dynLeft1 > 0) {
                    play = d
                } else {
                    play = getRandomRPSMove()
                }
            } else if (beatTheirPreviousMoveBot(prevP1Moves, prevP2Moves, prevResults, dynLeft2)) {
                 play = getBeatPreviousMoveBot(prevP1Moves, dynLeft1)
            } else {
                 play = dynDependentMove(dynLeft1, dynLeft2)
            }
        }
        return play
    }

    init {
        // Are you debugging?
        // Put a breakpoint on the line below to see when we start a new match
        println("Started new match")
    }
}
