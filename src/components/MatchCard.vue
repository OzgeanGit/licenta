<template>
  <div class="match-card">
    <!-- Randul 1: Info jucători și "vs" -->
    <div class="match-info">
      <div class="player-info">
        <h2>{{ pair.player1.name }}</h2>
        <p>ELO: {{ pair.player1.eloRating }}</p>
        <p>Poziție: {{ pair.player1.rank }}</p>
      </div>
      <div class="versus">VS</div>
      <div class="player-info">
        <h2>{{ pair.player2.name }}</h2>
        <p>ELO: {{ pair.player2.eloRating }}</p>
        <p>Poziție: {{ pair.player2.rank }}</p>
      </div>
    </div>
    <!-- Randul 2: Input pentru scoruri -->
    <div class="scores-input">
      <input type="number" v-model="player1Score" :readonly="readonly" placeholder="Scor User1">
      <span class="vs-text">-</span>
      <input type="number" v-model="player2Score" :readonly="readonly" placeholder="Scor User2">
    </div>
    <!-- Randul 3: Buton de înregistrare meci sau mesaj succes -->
    <div v-if="!matchRegistered">
      <button @click="registerMatch">Înregistrează meci</button>
    </div>
    <div v-else class="match-registered">
      Meci înregistrat cu succes!
    </div>
  </div>
</template>

<script>
export default {
  name: "MatchCard",
  props: {
    pair: Object,
    readonly: Boolean,
    matchRegistered: Boolean
  },
  data() {
    return {
      player1Score: this.pair.player1Score,
      player2Score: this.pair.player2Score
    };
  },
  methods: {
    registerMatch() {
      this.$emit('register-match', {
        pair: this.pair,
        player1Score: this.player1Score,
        player2Score: this.player2Score
      });
    }
  },
  watch: {
    player1Score(newScore) {
      this.$emit('update-score', { userId: this.pair.player1.id, score: newScore });
    },
    player2Score(newScore) {
      this.$emit('update-score', { userId: this.pair.player2.id, score: newScore });
    }
  }
};
</script>

  