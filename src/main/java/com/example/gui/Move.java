package com.example.gui;

import java.util.*;

public class Move {

    static HashSet<State> states = collectStates();
    static HashMap<State, Integer> forcedWins = forcedLines(states, 0);
    static HashMap<State, Integer> forcedLosses = reverseTurns(forcedWins);

    static HashMap<State, Integer> reverseTurns(HashMap<State, Integer> forcedWins){
        HashMap<State, Integer> forcedLosses = new HashMap<>();
        for(State win : forcedWins.keySet())
            forcedLosses.put(new State(win.max1, win.min1, win.max2, win.min2, 1), forcedWins.get(win));
        return forcedLosses;
    }

    static HashMap<State, Integer> forcedLines(HashSet<State> states, int player) {
        HashMap<State, Integer> forcedWins = new HashMap<>();
        for(int i = 0; i < 5; i++){
            for(int j = i; j < 5; j++)
                forcedWins.put(new State(0, 0, i, j, player ^ 1), 0);
        }
        boolean foundForce = true;
        while(foundForce){
            foundForce = false;
            for(State state : states){
                if(forcedWins.containsKey(state))
                    continue;
                HashSet<State> nextStates = state.getNextStates();
                int movesToWin = state.turn == player ? anyForced(nextStates, forcedWins) : allForced(nextStates, forcedWins);
                if(movesToWin != -1){
                    foundForce = true;
                    forcedWins.put(state, movesToWin + (state.turn == player ? 1 : 0));
                }
            }
        }
        return forcedWins;
    }

    static State bestMove(State state){
        int minMoves = -1, maxMoves = 0;
        State optimalMove = null, optimalTrash = null;
        ArrayList<State> draws = new ArrayList<>();
        for(State next : state.getNextStates()){
            Integer moves = forcedWins.get(next);
            if(moves != null) {
                if(minMoves == -1 || moves < minMoves) {
                    minMoves = moves;
                    optimalMove = next;
                }
            }
            else if(!forcedLosses.containsKey(next)) draws.add(next);
            else{
                moves = forcedLosses.get(next);
                if(moves > maxMoves){
                    maxMoves = moves;
                    optimalTrash = next;
                }
            }
        }
        if(optimalMove == null && draws.size() == 0) return optimalTrash;
        return optimalMove == null ? draws.get(new Random().nextInt(draws.size())) : optimalMove;
    }

    static int anyForced(HashSet<State> states, HashMap<State, Integer> forcedStates){
        int movesToWin = -1;
        for(State state : states){
            if(forcedStates.containsKey(state)){
                int moves = forcedStates.get(state);
                if(moves > movesToWin) movesToWin = moves;
            }
        }
        return movesToWin;
    }

    static int allForced(HashSet<State> states, HashMap<State, Integer> forcedStates){
        int movesToWin = -1;
        for(State state : states){
            if(!forcedStates.containsKey(state))
                return -1;
            int moves = forcedStates.get(state);
            if(moves > movesToWin) movesToWin = moves;
        }
        return movesToWin;
    }

    static HashSet<State> collectStates() {
        HashSet<State> searched = new HashSet<>();
        State s = new State(1, 1, 1, 1, 0);
        searched.add(s);
        Queue<State> queue = new LinkedList<>();
        queue.offer(s);
        while(!queue.isEmpty()){
            State current = queue.poll();
            for(State next : current.getNextStates()){
                if(!searched.contains(next)){
                    queue.offer(next);
                    searched.add(next);
                }
            }
        }
        return searched;
    }
}