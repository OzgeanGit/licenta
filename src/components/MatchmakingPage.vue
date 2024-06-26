<template>
  <div class="background-image"></div>
  <div class="navigation-bar">
    <router-link to="/" class="back-button">Înapoi la Dashboard</router-link>
  </div>

  <div class="matchmaking-container">
    <h1>Matchmaking</h1>

    <league-selector :leagues="leagues" v-model="selectedLeagueId" @change="fetchDivisions" />
    <division-selector :divisions="divisions" v-model="selectedDivisionId" />

    <button @click="matchPlayers">Match Players</button>

    <div class="matches-grid">
      <match-card
        v-for="(pair, index) in pairs"
        :key="index"
        :pair="pair"
        :readonly="matchRegistered[pair.player1.id + '_' + pair.player2.id]"
        @register-match="registerMatch"
      />
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import LeagueSelector from './LeagueSelector.vue';
import DivisionSelector from './DivisionSelector.vue';
import MatchCard from './MatchCard.vue';

export default {
  name: "MatchmakingPage",
  components: { LeagueSelector, DivisionSelector, MatchCard },
  data() {
    return {
      leagues: [],
      divisions: [],
      pairs: [],
      selectedLeagueId: null,
      selectedDivisionId: null,
      matchRegistered: {},
    };
  },
  methods: {
    fetchLeagues() {
      axios.get('http://localhost:8088/api/league/get/all')
        .then(response => {
          this.leagues = response.data;
          console.log('Leagues fetched:', this.leagues);
        })
        .catch(error => console.error('Error fetching leagues:', error));
    },
    fetchDivisions() {
      axios.get(`http://localhost:8088/api/league/get/divisions/${this.selectedLeagueId}`)
        .then(response => this.divisions = response.data)
        .catch(error => console.error('Error fetching divisions:', error));
    },
    async matchPlayers() {
      if (!this.selectedDivisionId) {
        console.error('No division selected');
        return;
      }

      console.log('Starting matchmaking for divisionId:', this.selectedDivisionId);

      try {
        const standingsResponse = await axios.get(`http://localhost:8088/api/division/get/standings/${this.selectedDivisionId}`);
        let standings = standingsResponse.data;

        const pairsResponse = await axios.post(`http://localhost:8088/api/division/pair/${this.selectedDivisionId}`);
        let pairs = pairsResponse.data;

        for (let pair of pairs) {
          pair.player1 = await this.fetchPlayerDetails(pair.user1Id);
          pair.player2 = await this.fetchPlayerDetails(pair.user2Id);

          pair.player1.rank = standings.findIndex(user => user.id === pair.player1.id) + 1;
          pair.player2.rank = standings.findIndex(user => user.id === pair.player2.id) + 1;
        }

        this.pairs = pairs;
        console.log('Matchmaking completed:', this.pairs);
      } catch (error) {
        console.error('Error processing matchmaking:', error);
      }
    },
    async fetchPlayerDetails(userId) {
      try {
        let response = await axios.get(`http://localhost:8088/api/users/get/${userId}`);
        return response.data;
      } catch (error) {
        console.error(`Error fetching user details for user ${userId}:`, error);
        return null;
      }
    },
    async registerMatch({ pair, player1Score, player2Score }) {
      if (player1Score == null || player2Score == null) {
        alert('Te rog să introduci scorurile pentru ambii jucători.');
        return;
      }

      try {
        const matchData = {
          player1Id: pair.player1.id,
          player2Id: pair.player2.id,
        };

        await axios.post(`http://localhost:8088/api/match/create?player1Score=${player1Score}&player2Score=${player2Score}`, matchData);

        alert('Meci înregistrat cu succes!');
        const matchKey = pair.player1.id + '_' + pair.player2.id;
        this.matchRegistered[matchKey] = true;

        this.$set(pair, 'readonly', true);
      } catch (error) {
        console.error('Eroare la înregistrarea meciului:', error);
        alert('A apărut o eroare la înregistrarea meciului. Verifică logurile pentru detalii.');
      }
    }
  },
  created() {
    this.fetchLeagues();
  },
};
</script>


<style>
.background-image {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background-image: url('../assets/background_main.jpg');
  background-size: cover;
  background-attachment: fixed;
  z-index: -1;
}

.matchmaking-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  padding: 2rem;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.85);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2);
  margin-top: 10vh;
  width: 80%;
  max-width: 800px;
  margin-left: auto;
  margin-right: auto;
  z-index: 10;
}

.match-card {
  background: rgba(255, 255, 255, 0.85);
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  padding: 1rem;
  margin-bottom: 2rem;
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;

}

.match-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.player-info {
  color: #2c3e50;
  text-align: center;
  font-weight: bold;
}

.player-info h2 {
  font-size: 1.25rem;
}

.player-info p {
  font-size: 1rem;
}

.versus {
  margin: 0 10px;
  font-size: 2rem;
  color: #666;
  font-weight: bold;
}

.vs-text {
  font-size: 2rem;
  color: #666;
  margin: 1rem;
  font-weight: bold;
}

.scores-input {
  display: flex;
  justify-content: center;
  margin-top: 10px;
  margin-bottom: 10px;
}


.scores-input input {
  margin: 0 5px;
  width: 35%;
  padding: 0.5rem;
  text-align: center;
}

.register-match-button {
  background-color: #4CAF50;
  color: white;
  padding: 10px 20px;
  border-radius: 5px;
  font-size: 1rem;
  transition: background-color 0.2s;
  border: none;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  width: auto;
  margin-top: 1rem;
}

select {
  padding: 10px;
  margin-bottom: 1rem;
  border-radius: 4px;
  border: 1px solid #ccc;
  box-shadow: inset 0 1px 3px rgba(0, 0, 0, 0.1);
  background-color: white;
  font-size: 1rem;
  color: #2c3e50;
  -webkit-appearance: none;
  -moz-appearance: none;
  appearance: none;
  cursor: pointer;
}

.matchmaking-container h1 {
  color: #2c3e50;
  font-size: 2rem;
  margin-bottom: 1rem;
  text-align: center;
}

.matches-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  grid-gap: 1rem;
  padding: 1rem;
  justify-content: center;
  align-content: start;
}

@media (max-width: 768px) {
  .match-card {
    max-width: 100%;
  }

  .matches-grid {
    grid-template-columns: 1fr;
  }

  .vs {
    margin: 1rem 0;
  }

  .matchmaking-container {
    padding: 1rem;
  }

  .scores-input {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 1rem;
  }

  .scores-input input {
    width: 3rem;
    padding: 0.25rem;
    font-size: 1rem;
    margin: 0;
    text-align: center;
  }

  .register-match-button {
    padding: 0.5rem 1rem;
    font-size: 0.8rem;
    margin-top: 1rem;
  }


}

.navigation-bar {
  position: absolute;
  top: 20px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 10;
}

.back-button {
  display: block;
  padding: 10px 20px;
  background: rgba(255, 255, 255, 0.8);
  color: #2c3e50;
  text-align: center;
  border-radius: 5px;
  text-decoration: none;
  box-shadow: 0 2px 5px w(0, 0, 0, 0.2);
  transition: background-color 0.3s ease;
}

.back-button:hover {
  background-color: rgba(255, 255, 255, 0.9);
}

.match-registered {
  text-align: center;
  margin-top: 10px;
  font-size: 1rem;
  font-weight: bold;
  color: #4CAF50;
  width: 100%;
}
</style>