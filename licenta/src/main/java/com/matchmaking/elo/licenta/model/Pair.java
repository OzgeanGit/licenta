package com.matchmaking.elo.licenta.model;

public final class Pair {
    private long user1Id;
    private long user2Id;



    public Pair(long user1Id, long user2Id) {
        this.user1Id = user1Id;
        this.user2Id = user2Id;
    }

    public Pair() {}

    public long getUser1Id() {
        return user1Id;
    }

    public long getUser2Id() {
        return user2Id;
    }
}
