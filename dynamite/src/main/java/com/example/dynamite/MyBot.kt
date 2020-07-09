package com.example.dynamite

import com.softwire.dynamite.bot.Bot
import com.softwire.dynamite.game.Gamestate
import com.softwire.dynamite.game.Move
import com.softwire.dynamite.game.Round
import java.util.Collections

class MyBot : Bot {
    private fun getRandomMove(): Move {
        val nums = listOf<Move>(Move.R, Move.P, Move.S, Move.D, Move.W)
        return nums.shuffled().first()
    }

    private fun getRandomNonWMove(): Move {
        val nums = listOf<Move>(Move.R, Move.P, Move.S, Move.D)
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

    private fun getP2Moves(gamestate: Gamestate): List<Move> {
        val rounds = gamestate.rounds
        var p2Moves = listOf<Move>()
        for (round in rounds) {
            p2Moves = p2Moves + round.p2
        }
        return p2Moves
    }

    private fun rockBot(prevP2Moves: List<Move>, rounds: List<Round>): Boolean {
        return if (rounds.size > 50) {
            Collections.frequency(prevP2Moves, Move.R) == rounds.size
        } else false
    }

    private fun paperBot(prevP2Moves: List<Move>, rounds: List<Round>): Boolean {
        return if (rounds.size > 50) {
            Collections.frequency(prevP2Moves, Move.P) == rounds.size
        } else false
    }

    private fun scissorsBot(prevP2Moves: List<Move>, rounds: List<Round>): Boolean {
        return if (rounds.size > 50) {
            Collections.frequency(prevP2Moves, Move.S) == rounds.size
        } else false
    }

    private fun testDynamiteFirst(prevP2Moves: List<Move>, rounds: List<Round>): Boolean {
        return if (rounds.size > 10) {
            Collections.frequency(prevP2Moves, Move.D)== rounds.size
        } else false
    }

    private fun testDynOnDraw(prevP2Moves: List<Move>, prevResults: List<String>, dynLeft2: Int, prevRes: String): Boolean {
        var dynDraws = 0
        var draws = 0
        for (move in prevP2Moves) {
            if (move == Move.D && prevResults[prevP2Moves.indexOf(move) - 1] == "Draw") dynDraws += 1
        }
        for (item in prevResults) {
            if (item == "Draw") draws += 1
        }
        return draws - dynDraws == 0 && dynLeft2 > 0 && prevRes == "Draw"
    }

    override fun makeMove(gamestate: Gamestate): Move {
        // Are you debugging?
        // Put a breakpoint in this method to see when we make a move

        //initialise variables, first move scenario
        val rounds = gamestate.rounds

        val r = Move.R;
        val p = Move.P;
        val s = Move.S;
        val d = Move.D;
        val w = Move.W

        if (rounds.size == 0) {
            return getRandomMove()
        } else {
            val prevResults = getPrevResults(gamestate)
            val prevP2Moves = getP2Moves(gamestate)
            val prevRound: Round = rounds[rounds.size - 1]
            val prevRes = prevResults[prevResults.size - 1]
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
            return if (testDynamiteFirst(prevP2Moves, rounds)) {
                w
            } else if (testDynOnDraw(prevP2Moves, prevResults, dynLeft2, prevRes)) {
                w
            } else if (rockBot(prevP2Moves, rounds)) {
                p
            } else if (paperBot(prevP2Moves, rounds)) {
                s
            } else if (scissorsBot(prevP2Moves, rounds)) {
                r
            } else {
                getRandomNonWMove()
            }
        }
    }

    init {
        // Are you debugging?
        // Put a breakpoint on the line below to see when we start a new match
        println("Started new match")
    }
}
