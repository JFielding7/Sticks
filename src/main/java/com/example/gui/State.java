package com.example.gui;

import java.util.HashSet;

public final class State {
    final int max1;
    final int min1;
    final int max2;
    final int min2;
    final int turn;
    public State(int left1, int right1, int left2, int right2, int turn){
        this.max1 = Math.max(left1, right1);
        this.min1 = Math.min(left1, right1);
        this.max2 = Math.max(left2, right2);
        this.min2 = Math.min(left2, right2);
        this.turn = turn;
    }

    public HashSet<State> getNextStates() {
        if(max1 == 0 && min1 == 0) return new HashSet<>();
        HashSet<State> nextStates = new HashSet<>();
        int nextTurn = this.turn ^ 1;
        if(max1 != 0 && max2 != 0) nextStates.add(new State((this.max2 + this.max1) % 5, this.min2, this.max1, this.min1, nextTurn));
        if(min1 != 0 && max2 != 0) nextStates.add(new State((this.max2 + this.min1) % 5, this.min2, this.max1, this.min1, nextTurn));
        if(max1 != 0 && min2 != 0) nextStates.add(new State(this.max2, (this.min2 + this.max1) % 5, this.max1, this.min1, nextTurn));
        if(min1 != 0 && min2 != 0) nextStates.add(new State(this.max2, (this.min2 + this.min1) % 5, this.max1, this.min1, nextTurn));
        int sum = this.max1 +this.min1;
        for(int i = sum > 4 ? sum - 4 : 0; i <= sum >> 1; i++){
            State next = new State(sum - i, i, this.max2, this.min2, turn);
            if(!next.equals(this)) nextStates.add(new State(this.max2, this.min2, sum - i, i, nextTurn));
        }
        return nextStates;
    }

    @Override
    public int hashCode() { return max1 + 5* min1 + 25* max2 + 125* min2 + 625*turn; }

    @Override
    public boolean equals(Object obj) {
        if(obj.getClass() != this.getClass()) return false;
        State state2 = (State) obj;
        return this.turn == state2.turn && this.max1 == state2.max1 && this.min1 == state2.min1
                && this.max2 == state2.max2 && this.min2 == state2.min2;
    }

    @Override
    public String toString() { return this.max1 + " " + this.min1 + "\t" + this.max2 + " " + this.min2 + "   " + turn; }
}
